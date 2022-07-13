package hust.soict.hedspi.model.algo.spanning;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import hust.soict.hedspi.model.algo.step.State;
import hust.soict.hedspi.model.algo.step.Step;
import hust.soict.hedspi.model.graph.BaseGraph;
import hust.soict.hedspi.model.graph.Edge;
import hust.soict.hedspi.model.graph.Vertex;

public class Prim extends SpanningTreeAlgorithm {
  private Vertex start;

  public Prim(BaseGraph<? extends Edge> graph, Vertex start) {
    super(graph);
    this.start = start;
    pseudoCode.put(1, "T = {start}");
    pseudoCode.put(2, "enqueue edges connected to s in PQ (by inc weight)");
    pseudoCode.put(3, "while PQ is not empty");
    pseudoCode.put(4, "  if (vertex v linked with e = PQ.remove ∉ T)");
    pseudoCode.put(5, "    T = T ∪ {v, e}, enqueue edges connected to v");
    pseudoCode.put(6, "  else ignore e");
    pseudoCode.put(7, "MST = T");
  }

  public Prim(BaseGraph<? extends Edge> graph) {
    this(graph, null);
  }

  public Prim() {
    this(null, null);
  }

  private class VertexInfo {
    Vertex vertex;
    double distance;
    Edge parentEdge;

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + getEnclosingInstance().hashCode();
      result = prime * result + ((vertex == null) ? 0 : vertex.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      VertexInfo other = (VertexInfo) obj;
      if (!getEnclosingInstance().equals(other.getEnclosingInstance()))
        return false;
      if (vertex == null) {
        if (other.vertex != null)
          return false;
      } else if (!vertex.equals(other.vertex))
        return false;
      return true;
    }

    private Prim getEnclosingInstance() {
      return Prim.this;
    }
  }

  /**
   * <pre>
   * Pseudo-code:
   * 1. T = {start}
   * 2. enqueue edges connected to s in PQ (by inc weight)
   * 3. while PQ is not empty
   * 4. &nbsp;&nbsp;if (vertex v linked with e = PQ.remove ∉ T)
   * 5. &nbsp;&nbsp;&nbsp;&nbsp;T = T ∪ {v, e}, enqueue edges connected to v
   * 6. &nbsp;&nbsp;else ignore e
   * 7. MST = T
   * </pre>
   */
  @Override
  public SpanningTree getSpanningTree() {
    // Todo: Implement Prim Algorithm
    int capacity = (int) (graph.vertexSet().size() / 0.75f + 1.0f);
    Set<Edge> treeEdgeSet = new HashSet<>(capacity);
    Set<Vertex> treeVertexSet = new HashSet<>();
    double spanningWeight = 0d;

    List<Vertex> verticesHighlighted = new LinkedList<>();
    List<Edge> edgesHighlighted = new LinkedList<>();
    List<Vertex> verticesTraversed = new LinkedList<>();
    List<Edge> edgesTraversed = new LinkedList<>();
    List<Edge> edgesQueued = new LinkedList<>();
    State state;
    String status;

    List<Vertex> vertexList = new LinkedList<>(graph.vertexSet());
    List<Edge> edgeList = new LinkedList<>(graph.edgeSet());

    PriorityQueue<VertexInfo> pq = new PriorityQueue<>((v1, v2) -> Double.compare(v1.distance, v2.distance));

    if (start == null) {
      start = graph.vertexSet().stream().findAny().get();
    } else {
      if (!graph.containsVertex(start)) {
        throw new IllegalArgumentException("Start vertex is not in graph");
      }
    }

    treeVertexSet.add(start);

    verticesTraversed.add(start);
    state = new State(vertexList, edgeList, verticesHighlighted,
        edgesHighlighted, verticesTraversed, edgesTraversed, edgesQueued);
    status = "T = {" + start.getId() + "}";
    steps.add(new Step(state, status, 1));

    status = "";
    for (Edge e : graph.edgesOf(start)) {
      Vertex q = e.getOppositeVertex(start);
      VertexInfo qInfo = new VertexInfo();
      qInfo.vertex = q;
      qInfo.distance = e.getWeight();
      qInfo.parentEdge = e;
      pq.add(qInfo);
      status += "(" + e.getWeight() + ", " + q.getId() + "), ";
      // add edge in queue
      edgesQueued.add(e);
    }

    state = new State(vertexList, edgeList, verticesHighlighted,
        edgesHighlighted, verticesTraversed, edgesTraversed, edgesQueued);
    // ignore ', '
    status = status.substring(0, status.length() - 2) + " is added to the PQ. \nThe PQ is now "
        + priorityQueueToString(pq);
    steps.add(new Step(state, status, 2));

    while (!pq.isEmpty()) {
      VertexInfo vertexInfo = pq.poll();
      Edge dequeuedEdge = vertexInfo.parentEdge;
      Vertex v = vertexInfo.vertex;

      verticesHighlighted.add(v);
      edgesHighlighted.add(dequeuedEdge);
      state = new State(vertexList, edgeList, verticesHighlighted,
          edgesHighlighted, verticesTraversed, edgesTraversed, edgesQueued);
      status = "(" + v.getId() + ", " + dequeuedEdge.getWeight() + ") is removed from PQ. Check if vertex " + v.getId()
          + " is in T.\nThe PQ is now " + priorityQueueToString(pq);
      steps.add(new Step(state, status, 4));

      if (!treeVertexSet.contains(v)) {
        state = new State(vertexList, edgeList, verticesHighlighted,
            edgesHighlighted, verticesTraversed, edgesTraversed, edgesQueued);
        status = v.getId() + " is not in T";
        steps.add(new Step(state, status, 4));

        treeVertexSet.add(v);
        spanningWeight += vertexInfo.distance;
        treeEdgeSet.add(vertexInfo.parentEdge);

        verticesHighlighted.remove(v);
        edgesHighlighted.remove(dequeuedEdge);
        verticesTraversed.add(v);
        edgesTraversed.add(dequeuedEdge);

        status = "";
        for (Edge e : graph.edgesOf(v)) {
          Vertex q = e.getOppositeVertex(v);
          if (treeVertexSet.contains(q)) {
            continue;
          }
          VertexInfo qInfo = new VertexInfo();
          qInfo.vertex = q;
          qInfo.distance = e.getWeight();
          qInfo.parentEdge = e;

          if (!edgesQueued.contains(e)) {
            edgesQueued.add(e);
            status += "(" + e.getWeight() + ", " + q.getId() + "), ";
            pq.add(qInfo);
          }
        }
        state = new State(vertexList, edgeList, verticesHighlighted,
            edgesHighlighted, verticesTraversed, edgesTraversed, edgesQueued);
        status = v.getId() + " and this edge is added to T(weight of T = " + spanningWeight + " ), "
            + (status.length() >= 2 ? status.substring(0, status.length() - 2) : "")
            + " is also added to the PQ. \nThe PQ is now "
            + priorityQueueToString(pq);
        steps.add(new Step(state, status, 5));
      } else {
        edgesQueued.remove(dequeuedEdge);
        edgesHighlighted.remove(dequeuedEdge);
        state = new State(vertexList, edgeList, verticesHighlighted, edgesHighlighted, verticesTraversed,
            edgesTraversed, edgesQueued);
        status = v.getId() + " is in T, so ignore this edge.";
        steps.add(new Step(state, status, 6));
      }
    }

    SpanningTree tree = new SpanningTree(spanningWeight, treeEdgeSet);

    state = new State(vertexList, edgeList, verticesHighlighted, edgesHighlighted, verticesTraversed, edgesTraversed,
        edgesQueued);
    status = "The Minimum Spanning Tree with MST weight = "
        + spanningWeight + ", edges: "
        + tree.getEdges();
    steps.add(new Step(state, status, 7));

    return tree;
  }

  private String priorityQueueToString(PriorityQueue<VertexInfo> pq) {
    StringBuilder sb = new StringBuilder();
    for (VertexInfo v : pq) {
      sb.append("(" + v.distance + ", " + v.vertex.getId() + "), ");
    }
    String result = sb.toString();
    return result.length() > 0
        ? result.substring(0, result.length() - 2)
        : result;
  }
}
