package hust.soict.hedspi.model.algo.spanning;

import hust.soict.hedspi.model.graph.BaseGraph;
import hust.soict.hedspi.model.graph.Edge;
import hust.soict.hedspi.model.graph.Vertex;

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
  protected SpanningTree getSpanningTree() {
    // Todo: implement Prim algorithm
    return null;
  }

}
