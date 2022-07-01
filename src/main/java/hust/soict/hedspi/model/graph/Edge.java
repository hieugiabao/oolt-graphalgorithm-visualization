package hust.soict.hedspi.model.graph;

import hust.soict.hedspi.annotation.LabelSource;

public class Edge implements Comparable<Edge> {
  public static final double DEFAULT_EDGE_WEIGHT = 1.0d;
  protected Vertex source;
  protected Vertex target;
  protected double weight = DEFAULT_EDGE_WEIGHT;

  public Edge(Vertex source, Vertex target) {
    this.source = source;
    this.target = target;
  }

  public Edge(Vertex source, Vertex target, double weight) {
    this.source = source;
    this.target = target;
    this.weight = weight;
  }

  public Vertex getSource() {
    return source;
  }

  public Vertex getTarget() {
    return target;
  }

  @LabelSource
  public double getWeight() {
    return weight;
  }

  void setWeight(double weight) {
    this.weight = weight;
  }

  public Vertex getOppositeVertex(Vertex vertex) {
    if (vertex.equals(source)) {
      return target;
    } else if (vertex.equals(target)) {
      return source;
    } else {
      throw new IllegalArgumentException("No such vertex: " + vertex);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((source == null) ? 0 : source.hashCode()) + ((target == null) ? 0 : target.hashCode());
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
    Edge other = (Edge) obj;
    if (source == null || target == null || other.source == null || other.target == null)
      return false;
    if ((source.equals(other.source) && target.equals(other.target))
        || (source.equals(other.target) && target.equals(other.source))) {
      return true;
    }
    return false;
  }

  @Override
  public int compareTo(Edge o) {
    return Double.compare(weight, o.weight);
  }

  @Override
  public String toString() {
    return "(" + source + " -- " + target + ", " + getWeight() + ")";
  }
}
