package hust.soict.hedspi.model.algo.spanning;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import hust.soict.hedspi.model.graph.BaseGraph;
import hust.soict.hedspi.model.graph.Edge;
import hust.soict.hedspi.model.graph.Vertex;
import hust.soict.hedspi.utils.UnionFind;

public class Kruskal extends SpanningTreeAlgorithm {
  public Kruskal(BaseGraph<Edge> graph) {
    super(graph);
  }

  public Kruskal() {
    this(null);
  }

  /**
   * <pre>
   * Pseudo code:
   * 1. <p>Sort E edges by increasing weight</p><br/>&nbsp;&nbsp;&nbsp;&nbsp;T = {}
   * 2. for (i = 0; i < edgeList.length; i++)
   * 3. &nbsp;&nbsp;if adding e = edgelist[i] does not form a cycle
   * 4. &nbsp;&nbsp;&nbsp;&nbsp;add e to T
   * 5. &nbsp;&nbsp;else ignore e
   * 6. MST = T
   * </pre>
   */
  @Override
  protected SpanningTree getSpanningTree() {
    double spanningTreeCost = 0.0d;
    Set<Edge> edgeList = new HashSet<>();

    UnionFind<Vertex> forest = new UnionFind<Vertex>(graph.vertexSet());
    List<Edge> allEdges = new ArrayList<>(graph.edgeSet());

    allEdges.sort(Comparator.comparingDouble((edge) -> edge.getWeight()));

    while (!allEdges.isEmpty()) {
      Edge edge = allEdges.remove(0);
      Vertex source = edge.getSource();
      Vertex target = edge.getTarget();

      if (forest.find(source).equals(forest.find(target))) {
        continue;
      }

      forest.union(source, target);
      edgeList.add(edge);
      spanningTreeCost += edge.getWeight();
    }

    SpanningTree tree = new SpanningTree(spanningTreeCost, edgeList);
    return tree;
  }

}
