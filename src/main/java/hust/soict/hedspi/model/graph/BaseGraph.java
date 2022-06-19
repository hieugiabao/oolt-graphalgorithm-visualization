package hust.soict.hedspi.model.graph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class BaseGraph<E extends Edge> {
  protected Set<Vertex> vertices = new HashSet<>();
  protected Set<E> edges = new HashSet<>();
  private final GraphType type;

  private Set<Vertex> unmodifiableVertexSet = null;
  private Set<E> unmodifiableEdgeSet = null;

  public BaseGraph(GraphType type) {
    this.type = type;
  }

  public GraphType getType() {
    return type;
  }

  public Set<Vertex> vertexSet() {
    if (unmodifiableVertexSet == null) {
      unmodifiableVertexSet = new HashSet<>(vertices);
    }
    return unmodifiableVertexSet;
  }

  public Set<E> edgeSet() {
    if (unmodifiableEdgeSet == null) {
      unmodifiableEdgeSet = new HashSet<>(edges);
    }
    return unmodifiableEdgeSet;
  }

  public boolean containsVertex(Vertex v) {
    return vertices.contains(v);
  }

  public boolean containsEdge(E e) {
    return edges.contains(e);
  }

  public abstract boolean containsEdge(Vertex source, Vertex target);

  public boolean addVertex(Vertex v) {
    return vertices.add(v);
  }

  public boolean addEdge(E e) {
    return edges.add(e);
  }

  public abstract E addEdge(Vertex source, Vertex target);

  public E addEdge(Vertex source, Vertex target, double weight) {
    E e = addEdge(source, target);
    if (e != null) {
      e.setWeight(weight);
    }
    return e;
  }

  public boolean removeVertex(Vertex v) {
    return vertices.remove(v);
  }

  public boolean removeEdge(E e) {
    return edges.remove(e);
  }

  public abstract E removeEdge(Vertex source, Vertex target);

  public boolean removeAllEdges(Collection<E> edges) {
    return this.edges.removeAll(edges);
  }

  public boolean removeAllVertices(Collection<Vertex> vertices) {
    return this.vertices.removeAll(vertices);
  }

  public Set<E> edgesOf(Vertex vertex) {
    if (vertex == null)
      throw new IllegalArgumentException("vertex is null");
    if (!containsVertex(vertex))
      throw new IllegalArgumentException("vertex is not in the graph");
    Set<E> result = new HashSet<>();
    for (E edge : edges) {
      if (edge.getSource().equals(vertex) || edge.getTarget().equals(vertex)) {
        result.add(edge);
      }
    }
    return result;
  }

  public double getWeight(E e) {
    return e.getWeight();
  }

  public void setWeight(E e, double weight) {
    if (type.isWeighted() == false) {
      throw new UnsupportedOperationException("Graph is not weighted");
    } else {
      e.setWeight(weight);
    }
  }

  public abstract Set<E> incomingEdgesOf(Vertex v);

  public abstract Set<E> outgoingEdgesOf(Vertex v);

  public static BaseGraph CP410() {
    UndirectedGraph graph = new UndirectedGraph();
    Vertex v0 = new Vertex(0);
    Vertex v1 = new Vertex(1);
    Vertex v2 = new Vertex(2);
    Vertex v3 = new Vertex(3);
    Vertex v4 = new Vertex(4);

    graph.addEdge(v0, v1, 24);
    graph.addEdge(v0, v2, 13);
    graph.addEdge(v0, v3, 13);
    graph.addEdge(v0, v4, 22);
    graph.addEdge(v1, v2, 22);
    graph.addEdge(v1, v3, 13);
    graph.addEdge(v1, v4, 13);
    graph.addEdge(v2, v3, 19);
    graph.addEdge(v2, v4, 14);
    graph.addEdge(v3, v4, 19);

    return graph;
  }
}
