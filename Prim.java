
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Override
protected SpanningTree getSpanningTree() {
    // Todo: implement Prim algorithm
    int capacity = (int) (data.vertexSet().size() / 0.75f + 1.0f);
    Set<Edge> treeEdgeSet = new HashSet<>(capacity);
    Set<Vertex> treeVertexSet = new HashSet<>();
    double spanningWeight = 0d;

    FibonacciHeap<Double, VertexInfo> fibonacciHeap = new FibonacciHeap<>();

    if (start == null) {
      start = data.vertexSet().stream().findAny().get();
    } else {
      if (!data.containsVertex(start)) {
        throw new IllegalArgumentException("Start vertex is not in graph");
      }
    }

    treeVertexSet.add(start);

    for (Edge e : data.edgesOf(start)) {
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

        for (Edge e : data.edgesOf(v)) {
          Vertex q = e.getOppositeVertex(v);
          VertexInfo qInfo = new VertexInfo();
          qInfo.vertex = q;
          qInfo.distance = e.getWeight();
          qInfo.parentEdge = e;
          fibonacciHeap.insert(qInfo.distance, qInfo);
        }
      }
    }

    return null;
}