package hust.soict.hedspi.model.graph;

import java.util.Collection;
import java.util.Collections;
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
      unmodifiableVertexSet = Collections.unmodifiableSet(vertices);
    }
    return unmodifiableVertexSet;
  }

  public Set<E> edgeSet() {
    if (unmodifiableEdgeSet == null) {
      unmodifiableEdgeSet = Collections.unmodifiableSet(edges);
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
      this.setWeight(e, weight);
    }
    return e;
  }

  public boolean removeVertex(Vertex v) {
    edgesOf(v).forEach(e -> removeEdge(e));
    return vertices.remove(v);
  }

  public boolean removeEdge(Edge e) {
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

  public double getWeight(Edge e) {
    return e.getWeight();
  }

  public void setWeight(Edge e, double weight) {
    if (type.isWeighted() == false) {
      throw new UnsupportedOperationException("Graph is not weighted");
    } else {
      edges.forEach(edge -> {
        if (edge.equals(e))
          edge.setWeight(weight);
      });
    }
  }

  public boolean isEmpty() {
    return vertices.isEmpty();
  }

  public abstract Set<E> incomingEdgesOf(Vertex v);

  public abstract Set<E> outgoingEdgesOf(Vertex v);

  public static BaseGraph<? extends Edge> CP410() {
    UndirectedGraph graph = new UndirectedGraph();
    Vertex v0 = new Vertex(0);
    Vertex v1 = new Vertex(1);
    Vertex v2 = new Vertex(2);
    Vertex v3 = new Vertex(3);
    Vertex v4 = new Vertex(4);

    graph.addVertex(v0);
    graph.addVertex(v1);
    graph.addVertex(v2);
    graph.addVertex(v3);
    graph.addVertex(v4);

    graph.addEdge(v0, v1, 4);
    graph.addEdge(v0, v2, 4);
    graph.addEdge(v0, v3, 6);
    graph.addEdge(v0, v4, 6);
    graph.addEdge(v1, v2, 2);
    graph.addEdge(v2, v3, 8);
    graph.addEdge(v3, v4, 9);

    return graph;
  }

  public static BaseGraph<? extends Edge> CP414() {
    UndirectedGraph graph = new UndirectedGraph();
    Vertex v0 = new Vertex(0);
    Vertex v1 = new Vertex(1);
    Vertex v2 = new Vertex(2);
    Vertex v3 = new Vertex(3);
    Vertex v4 = new Vertex(4);

    graph.addVertex(v0);
    graph.addVertex(v1);
    graph.addVertex(v2);
    graph.addVertex(v3);
    graph.addVertex(v4);

    graph.addEdge(v0, v1, 9);
    graph.addEdge(v0, v2, 75);
    graph.addEdge(v1, v2, 95);
    graph.addEdge(v1, v3, 19);
    graph.addEdge(v1, v4, 42);
    graph.addEdge(v2, v3, 51);
    graph.addEdge(v3, v4, 31);

    return graph;
  }

  public static BaseGraph<? extends Edge> K5() {
    UndirectedGraph graph = new UndirectedGraph();
    Vertex v0 = new Vertex(0);
    Vertex v1 = new Vertex(1);
    Vertex v2 = new Vertex(2);
    Vertex v3 = new Vertex(3);
    Vertex v4 = new Vertex(4);

    graph.addVertex(v0);
    graph.addVertex(v1);
    graph.addVertex(v2);
    graph.addVertex(v3);
    graph.addVertex(v4);

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

  public static BaseGraph<? extends Edge> Rail() {
    UndirectedGraph graph = new UndirectedGraph();
    Vertex v0 = new Vertex(0);
    Vertex v1 = new Vertex(1);
    Vertex v2 = new Vertex(2);
    Vertex v3 = new Vertex(3);
    Vertex v4 = new Vertex(4);
    Vertex v5 = new Vertex(5);
    Vertex v6 = new Vertex(6);
    Vertex v7 = new Vertex(7);
    Vertex v8 = new Vertex(8);
    Vertex v9 = new Vertex(9);

    graph.addVertex(v0);
    graph.addVertex(v1);
    graph.addVertex(v2);
    graph.addVertex(v3);
    graph.addVertex(v4);
    graph.addVertex(v5);
    graph.addVertex(v6);
    graph.addVertex(v7);
    graph.addVertex(v8);
    graph.addVertex(v9);

    graph.addEdge(v0, v1, 10);
    graph.addEdge(v1, v2, 10);
    graph.addEdge(v1, v6, 8);
    graph.addEdge(v1, v7, 13);
    graph.addEdge(v2, v3, 10);
    graph.addEdge(v2, v7, 8);
    graph.addEdge(v2, v8, 13);
    graph.addEdge(v3, v4, 10);
    graph.addEdge(v3, v8, 8);
    graph.addEdge(v5, v6, 10);
    graph.addEdge(v6, v7, 10);
    graph.addEdge(v7, v8, 10);
    graph.addEdge(v8, v9, 10);

    return graph;
  }

  public static BaseGraph<? extends Edge> Tesselation() {
    UndirectedGraph graph = new UndirectedGraph();
    Vertex v0 = new Vertex(0);
    Vertex v1 = new Vertex(1);
    Vertex v2 = new Vertex(2);
    Vertex v3 = new Vertex(3);
    Vertex v4 = new Vertex(4);
    Vertex v5 = new Vertex(5);
    Vertex v6 = new Vertex(6);
    Vertex v7 = new Vertex(7);
    Vertex v8 = new Vertex(8);

    graph.addVertex(v0);
    graph.addVertex(v1);
    graph.addVertex(v2);
    graph.addVertex(v3);
    graph.addVertex(v4);
    graph.addVertex(v5);
    graph.addVertex(v6);
    graph.addVertex(v7);
    graph.addVertex(v8);

    graph.addEdge(v0, v1, 8);
    graph.addEdge(v0, v2, 12);
    graph.addEdge(v1, v2, 13);
    graph.addEdge(v1, v3, 25);
    graph.addEdge(v1, v4, 9);
    graph.addEdge(v2, v3, 14);
    graph.addEdge(v2, v6, 21);
    graph.addEdge(v3, v4, 20);
    graph.addEdge(v3, v5, 8);
    graph.addEdge(v3, v6, 12);
    graph.addEdge(v3, v7, 12);
    graph.addEdge(v3, v8, 16);
    graph.addEdge(v4, v5, 19);
    graph.addEdge(v5, v7, 11);
    graph.addEdge(v6, v8, 11);
    graph.addEdge(v7, v8, 9);

    return graph;
  }

  public static BaseGraph<? extends Edge> CP4414() {
    DirectedGraph graph = new DirectedGraph();
    Vertex v0 = new Vertex(0);
    Vertex v1 = new Vertex(1);
    Vertex v2 = new Vertex(2);
    Vertex v3 = new Vertex(3);
    Vertex v4 = new Vertex(4);

    graph.addVertex(v0);
    graph.addVertex(v1);
    graph.addVertex(v2);
    graph.addVertex(v3);
    graph.addVertex(v4);

    graph.addEdge(v0, v1, 9);
    graph.addEdge(v0, v2, 75);
    graph.addEdge(v1, v2, 95);
    graph.addEdge(v1, v4, 42);
    graph.addEdge(v2, v3, 51);
    graph.addEdge(v3, v1, 19);
    graph.addEdge(v4, v3, 31);

    return graph;
  }
}
