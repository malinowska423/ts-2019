package com.network.reliability;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleWeightedGraph;

import static com.network.reliability.NetworkModel.*;

public class Main {

    public static void main(String[] args) {
        //testing default model
        approximateReliability(1,10000);
        approximateReliability(10,100);

        //creating custom network model
        UndirectedGraph<Integer, WeightedEdge> graph =
                new SimpleWeightedGraph<>(WeightedEdge.class);
        for (int i = 0; i < 10; i++) {
            graph.addVertex(i);
        }
        for (int i = 1; i <= 3; i++) {
            graph.addEdge(0,i, new WeightedEdge(1));
            graph.addEdge(i, i+3, new WeightedEdge(1));
            graph.addEdge(i,i+6, new WeightedEdge(1));
            graph.addEdge(i+3,i+6, new WeightedEdge(1));
        }
        graph.addEdge(7,8, new WeightedEdge(1));
        graph.addEdge(8,9, new WeightedEdge(1));
        //filling-up N matrix
        int [][] N = new int[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (i == j) {
                    N[i][j] = 0;
                } else {
                    N[i][j] = 1 + (i+j-1)%5;
                }
            }
        }
        //saving up model
        NetworkModel myModel = new NetworkModel(graph,N,100000);
        //testing custom model packet delay
        System.out.println("\n\nApproximate packet delay: "
                + myModel.getPacketDelay(1024)*1000 + "ms");
        myModel.approximateReliability(1,10000,5,1024);
        myModel.approximateReliability(10,1000,3,512);
    }
}
