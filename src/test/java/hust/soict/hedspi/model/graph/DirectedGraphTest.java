package hust.soict.hedspi.model.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DirectedGraphTest {
  DirectedGraph digraph;

  @BeforeEach
  void setUp() {
    Vertex v0 = new Vertex(0);
    Vertex v1 = new Vertex(1);
    Vertex v2 = new Vertex(2);
    digraph = new DirectedGraph();
    digraph.addVertex(v0);
    digraph.addVertex(v1);
    digraph.addVertex(v2);

    digraph.addEdge(v0, v1);
    digraph.addEdge(v1, v2);
    digraph.addEdge(v0, v2);
  }

  @Test
  @DisplayName("Add the edge which contains in the graph")
  void testAddTheSameEdge() {
    assertNull(digraph.addEdge(new Vertex(0), new Vertex(1)));
  }

  @Test
  @DisplayName("Add the edge which not contains in the graph")
  void testAddTheDifferentEdge() {
    DirectedEdge e = new DirectedEdge(new Vertex(1), new Vertex(0));
    assertEquals(e, digraph.addEdge(new Vertex(1), new Vertex(0)));
  }

  @Test
  @DisplayName("Add the vertex which not contains in the graph")
  void testAddTheDifferentVertex() {
    Vertex v = new Vertex(3);
    assertTrue(digraph.addVertex(v));
  }

  @Test
  @DisplayName("Add the vertex which contains in the graph")
  void testAddTheSameVertex() {
    assertFalse(digraph.addVertex(new Vertex(0)));
  }

  @Test
  @DisplayName("Set the weight of the unweigted graph")
  void testSetWeightOfUnweigtedGraph() {
    digraph = new DirectedGraph(false);
    Vertex v0 = new Vertex(0);
    Vertex v1 = new Vertex(1);

    digraph.addVertex(v0);
    digraph.addVertex(v1);

    DirectedEdge e = digraph.addEdge(v0, v1);
    UnsupportedOperationException thrown = assertThrows(UnsupportedOperationException.class, () -> {
      digraph.setWeight(e, 9);
    });

    assertTrue(thrown.getMessage().contains("Graph is not weighted"));
  }
}
