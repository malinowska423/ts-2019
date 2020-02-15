package com.network.reliability;

import org.jgrapht.GraphTests;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class NetworkModel {

    private UndirectedGraph<Integer, WeightedEdge> graph;
    private int [][] intensity; // number of packages send per second from i to j
    private int [][] bitRate;   // max number of packets per edge
    private int capacity;

    NetworkModel(UndirectedGraph<Integer, WeightedEdge> graph, int[][] intensity, int capacity) {
        this.graph = graph;
        this.intensity = intensity;
        this.bitRate = new int[10][10];
        this.capacity = capacity;
        setBitRateMatrix();
    }

    static void approximateReliability(int interval, int time) {
        // creating network graph
        UndirectedGraph<Integer, WeightedEdge> graph
                = new SimpleWeightedGraph<>(WeightedEdge.class);
        // adding vertexes
        graph.addVertex(20);
        for (int i = 19; i >= 1 ; i--) {
            graph.addVertex(i);
            graph.addEdge(i,i+1, new WeightedEdge());
        }
        System.out.println("Approximate network reliability");
        // first tryout
        System.out.println("\t(default): "
                + (reliabilityEstimator(graph, interval, time))*100 + "%");
        // second tryout
        graph.addEdge(1,20,new WeightedEdge());
        System.out.println("\t(with e(1,20)): "
                + (reliabilityEstimator(graph, interval, time))*100 + "%");
        // third tryout
        graph.addEdge(1,10, new WeightedEdge(0.8));
        graph.addEdge(5,15, new WeightedEdge(0.7));
        System.out.println("\t(with e(1,10) and e(5,15)): "
                + (reliabilityEstimator(graph, interval, time))*100 + "%");
        // 4 random vertexes
        Random r = new Random();
        int v1,v2;
        for (int i = 0; i < 4; i++) {
            v1 = r.nextInt(20);
            v2 = r.nextInt(20);
            if (v1 != v2 && !graph.containsEdge((graph.getEdge(v1, v2)))) {
                graph.addEdge(v1+1, v2+1, new WeightedEdge(0.4));
            } else {
                i--;
            }
        }
        System.out.println("\t(with 4 random vertexes): "
                + (reliabilityEstimator(graph, interval, time))*100 + "%");

    }

    private static double reliabilityEstimator(UndirectedGraph testGraph, int interval, int testTime) {
        int time = 0;
        double success = 0, attempts = 0;
        Random rand = new Random();
        ArrayList<WeightedEdge> edges = new ArrayList<>();
        ArrayList<WeightedEdge> removedEdges = new ArrayList<>();
        testGraph.edgeSet().forEach(e -> edges.add(((WeightedEdge) e)));
        while (time < testTime) {
            attempts++;
            for (WeightedEdge edge : edges) {
                if (rand.nextDouble() > edge.getWeight()) {
                    removedEdges.add(edge);
                    testGraph.removeEdge(edge);
                }
            }
            if (GraphTests.isConnected(testGraph)) {
                success++;
            }
            removedEdges.forEach(edge -> testGraph.addEdge(edge.getSource(), edge.getTarget(), edge));
            time += interval;
        }
        return success/attempts;
    }

    void approximateReliability(int interval, int testTime, int noOfDifferentValues, int packageSize) {
        System.out.println("Conditional network reliability (" + packageSize + "b): ");
        Random r = new Random();
        for (int i = 1; i <= noOfDifferentValues; i++) {
            double probability = r.nextDouble();
            double TMax = r.nextDouble();
            System.out.println("\t" + i + ". (p=" + probability + " TMax=" + TMax + "):\t" +
                    reliabilityEstimator(interval,testTime,probability,TMax,packageSize)*100 + "%");
        }

    }

    double reliabilityEstimator(int interval, int testTime, double probability, double TMax, int packageSize) {
        int time = 0;
        double success = 0, attempts = 0;
        ArrayList<WeightedEdge> edges = new ArrayList<>();
        graph.edgeSet().forEach(e -> {
            e.setWeight(probability);
            edges.add(e);
        });
        ArrayList<WeightedEdge> removedEdges = new ArrayList<>();
        while (time < testTime) {
            attempts++;
            for (WeightedEdge edge :
                    edges) {
                Random rand = new Random();
                double p = rand.nextDouble();
                if (p > edge.getWeight()) {
                    removedEdges.add(edge);
                    graph.removeEdge(edge);
                }
            }
            if (GraphTests.isConnected(graph)) {
                setBitRateMatrix();
                if (isBitRateMatrixFine(packageSize)) {
                    if (getPacketDelay(packageSize) < TMax) {
                        success++;
                    }
                }
            }
            for (WeightedEdge edge : removedEdges) {
                graph.addEdge(edge.getSource(), edge.getTarget(), edge);
            }
            time += interval;
        }
        return success/attempts;
    }

    double getPacketDelay(int packageSize) {
        double sumN = 0;
        for (int[] i : intensity) {
            for (int j : i) {
                sumN += j;
            }
        }
        ArrayList<WeightedEdge> edges = new ArrayList<>(graph.edgeSet());
        double sumE = 0, bitRate;
        for (WeightedEdge edge:
                edges) {
            bitRate = getEdgeBitRate(edge);
            sumE += (bitRate/((getEdgeCapacity(edge)/packageSize) - bitRate));
        }
        return sumE/sumN;
    }

    private double getEdgeCapacity(WeightedEdge edge) {
        if (edge.getSource().equals(edge.getTarget())) {
            return 0;
        } else {
            return capacity;
        }
    }

    private double getEdgeBitRate(WeightedEdge edge) {
        return bitRate[edge.getSource()][edge.getTarget()];
    }

    private void setBitRateMatrix() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                bitRate[i][j] = 0;
            }
        }
        DijkstraShortestPath<Integer, WeightedEdge> path = new DijkstraShortestPath<>(graph);
        for (int i = 0; i < 10; i++) {
            for (int j = i+1; j < 10; j++) {
                List<WeightedEdge> edges = path.getPath(i,j).getEdgeList();
                for (WeightedEdge e :
                        edges) {
                    bitRate[e.getSource()][e.getTarget()] += intensity[i][j] + intensity[j][i];
                    bitRate[e.getTarget()][e.getSource()] = bitRate[e.getSource()][e.getTarget()];
                }
            }
        }
    }

    private boolean isBitRateMatrixFine(int packageSize) {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (graph.containsEdge(i,j)) {
                    if (bitRate[i][j] * packageSize > getEdgeCapacity(graph.getEdge(i, j))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
