package hust.soict.hedspi.model.algo;

import hust.soict.hedspi.model.graph.BaseGraph;
import hust.soict.hedspi.model.graph.Edge;

public abstract class Algorithm {
  protected BaseGraph<Edge> graph;

  public Algorithm(BaseGraph<Edge> graph) {
    this.graph = graph;
  }

  public Algorithm() {
    this(null);
  }

  public void setGraph(BaseGraph<Edge> graph) {
    this.graph = graph;
  }

  public abstract void explore();
}
