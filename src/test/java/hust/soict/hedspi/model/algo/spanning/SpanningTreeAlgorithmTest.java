package hust.soict.hedspi.model.algo.spanning;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hust.soict.hedspi.model.graph.BaseGraph;
import hust.soict.hedspi.model.graph.Edge;
import hust.soict.hedspi.model.graph.Vertex;

class SpanningTreeAlgorithmTest {
  SpanningTreeAlgorithm algo;
  SpanningTree tree;

  @BeforeEach
  void setup() {
    Set<Edge> edges = new HashSet<>();
    edges.add(new Edge(new Vertex(0), new Vertex(1)));
    edges.add(new Edge(new Vertex(0), new Vertex(3)));
    edges.add(new Edge(new Vertex(0), new Vertex(4)));
    edges.add(new Edge(new Vertex(1), new Vertex(2)));
    double weight = 18.0d;
    tree = new SpanningTree(weight, edges);
  }

  @Test
  void testKruskal() {
    algo = new Kruskal(BaseGraph.CP410());
    assertEquals(tree, algo.getSpanningTree());
    /*
     * assertAll(
     * () -> assertEquals(tree.getWeight(), algo.getSpanningTree().getWeight()),
     * () -> assertEquals(tree.getEdges(), algo.getSpanningTree().getEdges()));
     */
  }

  @Test
  void testPrim() {
    algo = new Prim(BaseGraph.CP410());
    assertEquals(tree, algo.getSpanningTree());
  }
}
