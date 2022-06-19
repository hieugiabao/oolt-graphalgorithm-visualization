package hust.soict.hedspi.model.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UndirectedGraphTest {
  UndirectedGraph graph;

  @BeforeEach
  void setup() {
    graph = new UndirectedGraph(false);
    Vertex v0 = new Vertex(0);
    Vertex v1 = new Vertex(1);
    Vertex v2 = new Vertex(2);

    graph.addVertex(v0);
    graph.addVertex(v1);
    graph.addVertex(v2);

    graph.addEdge(v0, v1);
    graph.addEdge(v1, v2);
  }

  @Test
  @DisplayName("Add the edge which contains in the graph")
  void testAddTheSameEdge() {
    assertNull(graph.addEdge(new Vertex(1), new Vertex(0)));
  }

  @Test
  @DisplayName("Add the edge which not contains in the graph")
  void testAddTheDifferentEdge() {
    Edge e = new Edge(new Vertex(0), new Vertex(2));
    assertEquals(e, graph.addEdge(new Vertex(2), new Vertex(0)));
  }
}
