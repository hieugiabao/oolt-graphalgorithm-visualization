package hust.soict.hedspi.model.algo.spanning;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import hust.soict.hedspi.model.graph.Edge;

public class SpanningTree {
  private final double weight;
  private final Set<Edge> edges;

  public SpanningTree(double weight, Collection<Edge> edges) {
    this.weight = weight;
    this.edges = new HashSet<>(edges);
  }

  public double getWeight() {
    return weight;
  }

  public Set<Edge> getEdges() {
    return edges;
  }

  @Override
  public String toString() {
    return "Weight: " + weight + "\nEdges: " + edges;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((edges == null) ? 0 : edges.hashCode());
    long temp;
    temp = Double.doubleToLongBits(weight);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    SpanningTree other = (SpanningTree) obj;
    if (edges == null) {
      if (other.edges != null)
        return false;
    } else if (!edges.equals(other.edges))
      return false;
    if (Double.doubleToLongBits(weight) != Double.doubleToLongBits(other.weight))
      return false;
    return true;
  }
}
