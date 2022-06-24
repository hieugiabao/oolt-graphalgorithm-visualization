package hust.soict.hedspi.view;

import java.util.Collection;

import hust.soict.hedspi.model.graph.BaseGraph;
import hust.soict.hedspi.model.graph.Edge;

public interface PlacementStrategy {
  public void place(double width, double height, BaseGraph<? extends Edge> graph,
      Collection<? extends VertexView> vertices);
}
