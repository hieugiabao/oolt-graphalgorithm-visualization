package hust.soict.hedspi.model.algo.spanning;

import java.util.List;

import hust.soict.hedspi.model.algo.Algorithm;
import hust.soict.hedspi.model.graph.BaseGraph;
import hust.soict.hedspi.model.graph.Edge;

public abstract class SpanningTreeAlgorithm extends Algorithm {
  protected SpanningTreeAlgorithm(BaseGraph<Edge> graph) {
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
}
