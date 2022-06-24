package hust.soict.hedspi.view;

import java.util.Collection;
import java.util.Random;

import hust.soict.hedspi.model.graph.BaseGraph;
import hust.soict.hedspi.model.graph.Edge;

public class RandomPlacementStrategy implements PlacementStrategy {

  @Override
  public void place(double width, double height, BaseGraph<? extends Edge> graph,
      Collection<? extends VertexView> vertices) {
    Random rand = new Random();
    for (VertexView v : vertices) {
      double x = rand.nextDouble() * width;
      double y = rand.nextDouble() * height;
      v.setPosition(x, y);
    }
  }

}
