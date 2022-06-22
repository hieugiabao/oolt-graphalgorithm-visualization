package hust.soict.hedspi.model.graph;

import java.util.HashSet;
import java.util.Set;

public class DirectedGraph extends BaseGraph<DirectedEdge> {

  public DirectedGraph(boolean isWeighted) {
    super(new GraphType(true, isWeighted));
  }

  public DirectedGraph() {
    this(true);
  }

  @Override
  public boolean containsEdge(Vertex source, Vertex target) {
    return containsEdge(new DirectedEdge(source, target));
  }

  @Override
  public DirectedEdge addEdge(Vertex source, Vertex target) {
    DirectedEdge e = new DirectedEdge(source, target);
    return addEdge(e) ? e : null;
  }

  @Override
  public DirectedEdge removeEdge(Vertex source, Vertex target) {
    DirectedEdge e = new DirectedEdge(source, target);
    return removeEdge(e) ? e : null;
  }

  @Override
  public Set<DirectedEdge> incomingEdgesOf(Vertex v) {
    Set<DirectedEdge> result = new HashSet<>();
    for (DirectedEdge e : edgeSet()) {
      if (e.getTarget().equals(v)) {
        result.add(e);
      }
    }
    return result;
  }

  @Override
  public Set<DirectedEdge> outgoingEdgesOf(Vertex v) {
    Set<DirectedEdge> result = new HashSet<>();
    for (DirectedEdge e : edgeSet()) {
      if (e.getSource().equals(v)) {
        result.add(e);
      }
    }
    return result;
  }
}
