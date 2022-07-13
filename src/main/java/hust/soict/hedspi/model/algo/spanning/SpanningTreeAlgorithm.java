package hust.soict.hedspi.model.algo.spanning;

import java.util.List;

import hust.soict.hedspi.model.algo.Algorithm;
import hust.soict.hedspi.model.algo.step.Step;
import hust.soict.hedspi.model.graph.BaseGraph;
import hust.soict.hedspi.model.graph.Edge;

public abstract class SpanningTreeAlgorithm extends Algorithm {
  protected SpanningTreeAlgorithm(BaseGraph<?> graph) {
    super(graph);
  }

  protected SpanningTreeAlgorithm() {
    this(null);
  }

  protected SpanningTree spanningTree;

  protected abstract SpanningTree getSpanningTree();

  @Override
  public void explore() {
    if (spanningTree == null) {
      spanningTree = getSpanningTree();
    }
    System.out.println("Spanning tree cost: " + spanningTree.getWeight());
    System.out.println("Edges in spanning tree: \n" + spanningTree.getEdges());
  }

  @Override
  public void printStep() {
    if (steps.isEmpty()) {
      spanningTree = getSpanningTree();
    }
    for (Step step : steps) {
      for (int i : step.getLineNo()) {
        System.out.println(i + "." + pseudoCode.get(i));
      }
      System.out.println(step.getStatus());
      System.out.println();
    }
  }

  protected String fromListToString(List<Edge> edges) {
    String[] toString = new String[edges.size()];
    for (int i = 0; i < edges.size(); i++) {
      toString[i] = fromEdgeToString(edges.get(i));
    }
    return String.join(", ", toString);
  }

  protected String fromEdgeToString(Edge edge) {
    return "(" + edge.getWeight() + ",(" + edge.getSource().getId() + "," + edge.getTarget().getId() + "))";
  }

  @Override
  public List<Step> getStepsList() {
    if (steps.isEmpty()) {
      spanningTree = getSpanningTree();
    }
    return steps;
  }
}
