package hust.soict.hedspi.model.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EdgeTest {
  Edge e1, e2;
  DirectedEdge de1, de2;

  @BeforeEach
  void setup() {
    e1 = new Edge(new Vertex(0), new Vertex(1));
    e2 = new Edge(new Vertex(1), new Vertex(0));
    de1 = new DirectedEdge(new Vertex(0), new Vertex(1));
    de2 = new DirectedEdge(new Vertex(1), new Vertex(0));
  }

  @Test
  void testUndirectedEdgeEquals() {
    assertEquals(e1.hashCode(), e2.hashCode());
  }

  @Test
  void testDirectedEdgeEquals() {
    assertNotEquals(de1.hashCode(), de2.hashCode());
  }
}
