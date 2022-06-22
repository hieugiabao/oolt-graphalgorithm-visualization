package hust.soict.hedspi.model.algo;

import java.util.ArrayList;
import java.util.List;

import hust.soict.hedspi.model.algo.step.Step;
import hust.soict.hedspi.model.graph.BaseGraph;

public abstract class Algorithm {
  protected BaseGraph<?> graph;

  protected List<Step> steps = new ArrayList<Step>();

  public Algorithm(BaseGraph<?> graph) {
    this.graph = graph;
  }

  public Algorithm() {
    this(null);
  }

  public void setGraph(BaseGraph<?> graph) {
    this.graph = graph;
  }

  public abstract void explore();

  public abstract void printStep();
}
