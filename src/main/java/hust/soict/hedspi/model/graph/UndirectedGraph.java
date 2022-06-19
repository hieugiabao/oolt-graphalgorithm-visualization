package hust.soict.hedspi.model.graph;

import java.util.Set;

public class UndirectedGraph extends BaseGraph<Edge> {

  public UndirectedGraph(boolean weighted) {
    super(new GraphType(false, weighted));
  }

  public UndirectedGraph() {
    this(true);
  }

  @Override
  public boolean containsEdge(Vertex source, Vertex target) {
    return containsEdge(new Edge(source, target));
  }

  @Override
  public Edge addEdge(Vertex source, Vertex target) {
    Edge e = new Edge(source, target);
    return addEdge(e) ? e : null;
  }

  @Override
  public Edge removeEdge(Vertex source, Vertex target) {
    Edge e = new Edge(source, target);
    return removeEdge(e) ? e : null;
  }

  @Override
  public Set<Edge> incomingEdgesOf(Vertex v) {
    return edgesOf(v);
  }

  @Override
  public Set<Edge> outgoingEdgesOf(Vertex v) {
    return edgesOf(v);
  }

}
