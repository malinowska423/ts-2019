package com.network.reliability;

import org.jgrapht.graph.DefaultEdge;

public class WeightedEdge extends DefaultEdge {
    private double weight;

    public WeightedEdge() {
        this.weight = 0.95;
    }

    public WeightedEdge(double weight) {
        this.weight = weight;
    }

    double getWeight() {
        return weight;
    }

    void setWeight(double weight) {
        this.weight = weight;
    }

    protected Integer getSource() {
        return ((Integer) super.getSource());
    }

    protected Integer getTarget() {
        return ((Integer) super.getTarget());
    }
}
