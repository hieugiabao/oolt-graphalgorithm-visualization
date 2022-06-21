package hust.soict.hedspi.view;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import hust.soict.hedspi.model.graph.Vertex;
import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;

public class VertexView extends Circle {
  private final Vertex vertex;
  private final Set<VertexView> adjVerteices;

  private Label attachedLabel = null;

  private final PointVector forceVector = new PointVector(0, 0);
  private final PointVector updatedPosition = new PointVector(0, 0);

  public VertexView(Vertex v, double x, double y, double radius) {
    super(x, y, radius);
    this.vertex = v;
    this.adjVerteices = new HashSet<>();
    this.getStyleClass().add("vertex");
  }

  public boolean addAdjacentVertex(VertexView v) {
    return adjVerteices.add(v);
  }

  public boolean removeAdjacentVertex(VertexView v) {
    return adjVerteices.remove(v);
  }

  public boolean removeAdjacentVertex(Collection<VertexView> vs) {
    return adjVerteices.removeAll(vs);
  }

  public boolean isAdjacentVertex(VertexView v) {
    return adjVerteices.contains(v);
  }

  public Point2D getPosition() {
    return new Point2D(getCenterX(), getCenterY());
  }

  public void setPosition(double x, double y) {
    setCenterX(x);
    setCenterY(y);
  }

  public void setPosition(Point2D p) {
    this.setPosition(p.getX(), p.getY());
  }

  public Vertex getVertex() {
    return vertex;
  }

  public void attachLabel(Label label) {
    this.attachedLabel = label;
    label.xProperty().bind(this.centerXProperty().subtract(label.getLayoutBounds().getWidth() / 2.0));
    label.yProperty().bind(this.centerYProperty().add(label.getLayoutBounds().getHeight() / 4.0));
  }

  public Label getAttachedLabel() {
    return attachedLabel;
  }

  public void resetForces() {
    forceVector.x = forceVector.y = 0;
    updatedPosition.x = getCenterX();
    updatedPosition.y = getCenterY();
  }

  public void addForceVector(double x, double y) {
    forceVector.x += x;
    forceVector.y += y;
  }

  public Point2D getForceVector() {
    return new Point2D(forceVector.x, forceVector.y);
  }

  public Point2D getUpdatedPosition() {
    return new Point2D(updatedPosition.x, updatedPosition.y);
  }

  public void updateDelta() {
    updatedPosition.x = updatedPosition.x + forceVector.x;
    updatedPosition.y = updatedPosition.y + forceVector.y;
  }

  public void moveFromForces() {
    double height = getParent().getLayoutBounds().getHeight();
    double width = getParent().getLayoutBounds().getWidth();

    updatedPosition.x = boundCenterCoordinate(updatedPosition.x, 0, width);
    updatedPosition.y = boundCenterCoordinate(updatedPosition.y, 0, height);

    setPosition(updatedPosition.x, updatedPosition.y);
  }

  private double boundCenterCoordinate(double value, double min, double max) {
    double radius = getRadius();
    if (value < min - radius) {
      return min - radius;
    } else if (value > max - radius) {
      return max - radius;
    } else {
      return value;
    }
  }

  private class PointVector {
    double x, y;

    public PointVector(double x, double y) {
      this.x = x;
      this.y = y;
    }
  }
}
