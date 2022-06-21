package hust.soict.hedspi.utils;

import static javafx.beans.binding.Bindings.createDoubleBinding;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ObservableDoubleValue;
import javafx.geometry.Point2D;

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
}
