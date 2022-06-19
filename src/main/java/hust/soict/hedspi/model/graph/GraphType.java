package hust.soict.hedspi.model.graph;

public class GraphType {
  private final boolean directed;
  private final boolean weighted;

  public GraphType(boolean directed, boolean weighted) {
    this.directed = directed;
    this.weighted = weighted;
  }

  public boolean isDirected() {
    return directed;
  }

  public boolean isWeighted() {
    return weighted;
  }
}
