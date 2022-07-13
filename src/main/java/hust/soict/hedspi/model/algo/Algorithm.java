package hust.soict.hedspi.model.algo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hust.soict.hedspi.model.algo.step.Step;
import hust.soict.hedspi.model.graph.BaseGraph;

public abstract class Algorithm {
  protected BaseGraph<?> graph;

  protected List<Step> steps = new ArrayList<Step>();

  protected final Map<Integer, String> pseudoCode = new HashMap<Integer, String>();

  protected Algorithm(BaseGraph<?> graph) {
    this.graph = graph;
  }

  protected Algorithm() {
    this(null);
  }

  public void setGraph(BaseGraph<?> graph) {
    this.graph = graph;
  }

  public abstract void explore();

  public abstract void printStep();

  public abstract List<Step> getStepsList();

  public Map<Integer, String> getPseudoCode() {
    return pseudoCode;
  }
}
