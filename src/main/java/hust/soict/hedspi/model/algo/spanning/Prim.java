package hust.soict.hedspi.model.algo.spanning;

import hust.soict.hedspi.model.graph.BaseGraph;
import hust.soict.hedspi.model.graph.Edge;
import hust.soict.hedspi.model.graph.Vertex;

// import hust.soict.hedspi.model.graph.Edge;
// import hust.soict.hedspi.model.graph.Vertex;
import hust.soict.hedspi.utils.FibonacciHeap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Prim extends SpanningTreeAlgorithm {
  private Vertex start;

  public Prim(BaseGraph<Edge> graph, Vertex start) {
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

  public Prim(BaseGraph<Edge> graph) {
    this(graph, null);
  }

  public Prim() {
    this(null, null);
  }

  private class VertexInfo {
    public Vertex vertex;
    public boolean spanned = false;
    public double distance;
    public Edge parentEdge;

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
  public SpanningTree getSpanningTree2() {
    int capacity = (int) (graph.vertexSet().size() / 0.75f + 1.0f);
    Set<Edge> treeEdgeSet = new HashSet<>(capacity);
    double spanningWeight = 0d;

    List<VertexInfo> vertices = new ArrayList<>();
    Map<VertexInfo, FibonacciHeap.Node<Double, VertexInfo>> nodeMapping = new HashMap<>();

    FibonacciHeap<Double, VertexInfo> fibonacciHeap = new FibonacciHeap<>();
    // init priority queue with all vertex, set distance = infinity and set parent =
    // null
    for (Vertex vertex : graph.vertexSet()) {
      VertexInfo vertexInfo = new VertexInfo();
      vertexInfo.vertex = vertex;
      vertexInfo.distance = Double.MAX_VALUE;
      vertexInfo.parentEdge = null;
      vertices.add(vertexInfo);
      FibonacciHeap.Node<Double, VertexInfo> node = fibonacciHeap.insert(vertexInfo.distance, vertexInfo);
      nodeMapping.put(vertexInfo, node);
    }

    // set start vertex distance = 0
    if (start != null) {
      if (!graph.containsVertex(start)) {
        throw new IllegalArgumentException("Start vertex is not in graph");
      }
      VertexInfo startVertexInfo = vertices.stream().filter(info -> info.vertex.equals(start)).findFirst().get();
      startVertexInfo.distance = 0d;
      nodeMapping.get(startVertexInfo).decreaseKey(0d);
    }

    while (!fibonacciHeap.isEmpty()) {
      // extract min vertex from priority queue
      FibonacciHeap.Node<Double, VertexInfo> fibNode = fibonacciHeap.deleteMin();
      VertexInfo vertexInfo = fibNode.getValue();

      Vertex p = vertexInfo.vertex;
      vertexInfo.spanned = true;

      if (vertexInfo.parentEdge != null) {
        treeEdgeSet.add(vertexInfo.parentEdge);
        // System.out.println("Them canh: " + vertexInfo.parentEdge);
        spanningWeight += vertexInfo.parentEdge.getWeight();
      }

      for (Edge e : graph.edgesOf(p)) {
        Vertex q = e.getOppositeVertex(p);
        VertexInfo qInfo = vertices.stream().filter(info -> info.vertex.equals(q)).findFirst().get();
        if (!qInfo.spanned) {
          double cost = e.getWeight();

          if (cost < qInfo.distance) {
            qInfo.distance = cost;
            qInfo.parentEdge = e;
            nodeMapping.get(qInfo).decreaseKey(cost);
          }
        }
      }
    }

    return new SpanningTree(spanningWeight, treeEdgeSet);
  }

  @Override
  protected SpanningTree getSpanningTree() {
    // To do: Implement Prim Algorithm
    int capacity = (int) (graph.vertexSet().size() / 0.75f + 1.0f);
    Set<Edge> treeEdgeSet = new HashSet<>(capacity);
    Set<Vertex> treeVertexSet = new HashSet<>();
    double spanningWeight = 0d;

    FibonacciHeap<Double, VertexInfo> fibonacciHeap = new FibonacciHeap<>();

    if (start == null) {
      start = graph.vertexSet().stream().findAny().get();
    } else {
      if (!graph.containsVertex(start)) {
        throw new IllegalArgumentException("Start vertex is not in graph");
      }
    }

    treeVertexSet.add(start);

    for (Edge e : graph.edgesOf(start)) {
      Vertex q = e.getOppositeVertex(start);
      VertexInfo qInfo = new VertexInfo();
      qInfo.vertex = q;
      qInfo.distance = e.getWeight();
      qInfo.parentEdge = e;
      fibonacciHeap.insert(qInfo.distance, qInfo);
    }

    while (!fibonacciHeap.isEmpty()) {
      FibonacciHeap.Node<Double, VertexInfo> fibNode = fibonacciHeap.deleteMin();
      VertexInfo vertexInfo = fibNode.getValue();
      Vertex v = vertexInfo.vertex;

      if (!treeVertexSet.contains(v)) {
        treeVertexSet.add(v);
        spanningWeight += vertexInfo.distance;
        treeEdgeSet.add(vertexInfo.parentEdge);

        for (Edge e : graph.edgesOf(v)) {
          Vertex q = e.getOppositeVertex(v);
          VertexInfo qInfo = new VertexInfo();
          qInfo.vertex = q;
          qInfo.distance = e.getWeight();
          qInfo.parentEdge = e;
          fibonacciHeap.insert(qInfo.distance, qInfo);
        }
      }
    }

    return new SpanningTree(spanningWeight, treeEdgeSet);
  }

}
