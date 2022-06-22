package hust.soict.hedspi.model.algo.spanning;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import hust.soict.hedspi.model.algo.step.State;
import hust.soict.hedspi.model.algo.step.Step;
import hust.soict.hedspi.model.graph.BaseGraph;
import hust.soict.hedspi.model.graph.Edge;
import hust.soict.hedspi.model.graph.Vertex;
import hust.soict.hedspi.utils.UnionFind;

public class Kruskal extends SpanningTreeAlgorithm {

  public Kruskal(BaseGraph<?> graph) {
    super(graph);
    pseudoCode.put(1, "Sort E edges by increasing weight");
    pseudoCode.put(2, "T = {}");
    pseudoCode.put(3, "for each edge e in E do");
    pseudoCode.put(4, "  if adding e = edgelist[i] does not form a cycle");
    pseudoCode.put(5, "    add e to T");
    pseudoCode.put(6, "  else ignore e");
    pseudoCode.put(7, "MST = T");
  }

  public Kruskal() {
    this(null);
  }

  /**
   * <pre>
   * Pseudo-code:
   * 1. Sort E edges by increasing weight
   * 2. <p>T = {}</p>
   * 3. for (i = 0; i < edgeList.length; i++)
   * 4. &nbsp;&nbsp;if adding e = edgelist[i] does not form a cycle
   * 5. &nbsp;&nbsp;&nbsp;&nbsp;add e to T
   * 6. &nbsp;&nbsp;else ignore e
   * 7. MST = T
   * </pre>
   */
  @Override
  protected SpanningTree getSpanningTree() {
    double spanningTreeCost = 0.0d;
    List<Edge> treeEdgeList = new ArrayList<>();
    List<Edge> edgeHighlighted = new ArrayList<>();
    List<Vertex> vertexHighlighted = new ArrayList<>();
    List<Edge> edgeTraversed = new ArrayList<>();
    List<Vertex> vertexTraversed = new ArrayList<>();
    List<Edge> edgeQueued = new ArrayList<>(graph.edgeSet());
    State state;
    String status;
    boolean added;

    List<Vertex> vertexList = new ArrayList<>(graph.vertexSet());
    List<Edge> edgeList = new ArrayList<>(graph.edgeSet());

    UnionFind<Vertex> forest = new UnionFind<Vertex>(graph.vertexSet());
    List<Edge> allEdges = new ArrayList<>(graph.edgeSet());
    allEdges.sort(Comparator.comparingDouble((edge) -> edge.getWeight()));

    state = new State(vertexList, edgeList, vertexHighlighted, edgeHighlighted, vertexTraversed, edgeTraversed,
        edgeQueued);
    status = "Edges are sorted in increasing order of weight: " + fromListToString(allEdges);
    steps.add(new Step(state, status, 1, 2));

    while (!allEdges.isEmpty()) {
      status = "The remaining "
          + (allEdges.size() == 1 ? "edge is " : "edges are ")
          + fromListToString(allEdges);
      state = new State(vertexList, edgeList, vertexHighlighted,
          edgeHighlighted, vertexTraversed, edgeTraversed, edgeQueued);
      steps.add(new Step(state, status, 3));
      Edge edge = allEdges.remove(0);
      Vertex source = edge.getSource();
      Vertex target = edge.getTarget();

      edgeHighlighted.add(edge);
      vertexHighlighted.add(source);
      vertexHighlighted.add(target);

      state = new State(vertexList, edgeList, vertexHighlighted,
          edgeHighlighted, vertexTraversed, edgeTraversed, edgeQueued);
      status = "Checking if adding edge " + fromEdgeToString(edge) + " does not form a cycle";
      steps.add(new Step(state, status, 4));

      added = false;
      // if source and target is not in a same set
      if (!forest.find(source).equals(forest.find(target))) {
        added = true;
        forest.union(source, target);
        treeEdgeList.add(edge);
        spanningTreeCost += edge.getWeight();
        edgeTraversed.add(edge);
        vertexTraversed.add(source);
        vertexTraversed.add(target);
      }

      edgeHighlighted.remove(edge);
      vertexHighlighted.remove(source);
      vertexHighlighted.remove(target);
      edgeQueued.remove(edge);

      state = new State(vertexList, edgeList, vertexHighlighted,
          edgeHighlighted, vertexTraversed, edgeTraversed, edgeQueued);
      status = added
          ? ("Adding edge will not form a cycle, so we add it to T. The current weight of T is " + spanningTreeCost)
          : ("That edge will form a cycle, so we ignore it. The current weight of T remains at " + spanningTreeCost);
      steps.add(new Step(state, status, added ? 5 : 6));
    }

    SpanningTree tree = new SpanningTree(spanningTreeCost, treeEdgeList);

    state = new State(vertexList, edgeList, vertexHighlighted, edgeHighlighted,
        vertexTraversed, edgeTraversed, edgeQueued);
    status = "The Minimum Spanning Tree with MST weight = "
        + spanningTreeCost + ", edges: "
        + tree.getEdges();
    steps.add(new Step(state, status, 7));

    return tree;
  }

}
