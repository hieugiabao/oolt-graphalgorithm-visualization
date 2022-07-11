package hust.soict.hedspi.utils;

import static javafx.beans.binding.Bindings.createDoubleBinding;

import java.util.List;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ObservableDoubleValue;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;

public class Utilities {

  public static DoubleBinding atan2(final ObservableDoubleValue x, final ObservableDoubleValue y) {
    return createDoubleBinding(() -> Math.atan2(x.get(), y.get()), y, x);
  }

  public static DoubleBinding toDegrees(final ObservableDoubleValue angrad) {
    return createDoubleBinding(() -> Math.toDegrees(angrad.get()), angrad);
  }

  public static Point2D repellingForce(Point2D from, Point2D to, double scale) {
    double distance = from.distance(to);

    Point2D vector = to.subtract(from).normalize();
    double factor = 0;
    if (distance < 1) {
      factor = -scale;
    } else {
      factor = -scale / (distance * distance);
    }

    return vector.multiply(factor);
  }

  public static Point2D attractiveForce(Point2D from, Point2D to, double force, double scale) {
    double distance = from.distance(to);

    Point2D vector = to.subtract(from).normalize();
    double factor = 0;

    if (distance < 1) {
      distance = 1;
    }

    factor = force * Math.log(distance / scale) * 0.1;
    return vector.multiply(factor);
  }

  public static Point2D rotate(final Point2D point, final Point2D pivot, double angle_degrees) {
    double angle = Math.toRadians(angle_degrees);

    double sin = Math.sin(angle);
    double cos = Math.cos(angle);

    // translate to origin
    Point2D result = point.subtract(pivot);

    // rotate point
    Point2D rotatedOrigin = new Point2D(
        result.getX() * cos - result.getY() * sin,
        result.getX() * sin + result.getY() * cos);

    // translate point back
    result = rotatedOrigin.add(pivot);

    return result;
  }

  public static boolean checkInCircle(Point2D point, Point2D center, double radius) {
    double distance = point.distance(center);
    return distance <= radius;
  }

  public static Node pick(Node node, double x, double y) {
    Point2D p = new Point2D(x, y);
    if (!node.contains(p))
      return null;

    if (node instanceof Parent) {
      Node bsetMatchingChild = null;
      List<Node> children = ((Parent) node).getChildrenUnmodifiable();
      for (int i = children.size() - 1; i >= 0; i--) {
        Node child = children.get(i);
        if (child.isVisible() && !child.isMouseTransparent() && child.contains(p)) {
          bsetMatchingChild = child;
          break;
        }
      }

      if (bsetMatchingChild != null)
        return pick(bsetMatchingChild, x, y);
    }

    return node;
  }
}
