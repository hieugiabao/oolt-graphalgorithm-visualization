package hust.soict.hedspi.model.algo;

import java.util.ArrayList;
import java.util.List;

import hust.soict.hedspi.model.algo.step.Step;
import hust.soict.hedspi.model.graph.BaseGraph;
import hust.soict.hedspi.model.graph.Edge;

public abstract class Algorithm {
  protected BaseGraph<Edge> graph;

  protected List<Step> steps = new ArrayList<Step>();

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

  public abstract void printStep();
}
