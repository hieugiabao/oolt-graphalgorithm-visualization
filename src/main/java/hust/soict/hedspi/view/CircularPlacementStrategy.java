package hust.soict.hedspi.view;

import java.util.Collection;

import hust.soict.hedspi.model.graph.BaseGraph;
import hust.soict.hedspi.model.graph.Edge;
import javafx.geometry.Point2D;
import static hust.soict.hedspi.utils.Utilities.rotate;

public class CircularPlacementStrategy implements PlacementStrategy {

  @Override
  public void place(double width, double height, BaseGraph<? extends Edge> graph,
      Collection<? extends VertexView> vertices) {
    Point2D center = new Point2D(width / 2, height / 2);
    int N = vertices.size();
    double angleIncrement = -360f / N;

    // place first vertice north position, others in clockwise manner
    boolean first = true;
    Point2D p = null;
    for (VertexView vertex : vertices) {

      if (first) {
        // verifiy smaller width and height.
        if (width > height)
          p = new Point2D(center.getX(),
              center.getY() - height / 2 + vertex.getRadius() * 2);
        else
          p = new Point2D(center.getX(),
              center.getY() - width / 2 + vertex.getRadius() * 2);

        first = false;
      } else {
        p = rotate(p, center, angleIncrement);
      }

      vertex.setPosition(p.getX(), p.getY());

    }

  }

}
