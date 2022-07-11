package hust.soict.hedspi.view;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import hust.soict.hedspi.model.algo.step.State;
import hust.soict.hedspi.model.graph.Vertex;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.shape.Circle;

public class VertexView extends Circle implements StylableNode, LabeledNode {
  private final Vertex vertex;
  private final Set<VertexView> adjVerteices;

  private final StyleProxy styleProxy;
  private Label attachedLabel = null;
  private boolean isDragging = false;
  private State.VertexState state;

  private final PointVector forceVector = new PointVector(0, 0);
  private final PointVector updatedPosition = new PointVector(0, 0);

  public VertexView(Vertex v, double x, double y, double radius, boolean allowMove, State.VertexState state) {
    super(x, y, radius);
    this.vertex = v;
    this.adjVerteices = new HashSet<>();
    this.styleProxy = new StyleProxy(this);
    styleProxy.addStyleClass("vertex");

    if (allowMove) {
      enableDrag();
    }

    this.state = state != null ? state : new State.VertexState(this.vertex, State.VERTEX_STATE.DEFAULT);
    updateState();
  }

  public VertexView(Vertex v, double x, double y, double radius, boolean allowMove) {
    this(v, x, y, radius, allowMove, null);
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
    if (isDragging) {
      return;
    }
    setCenterX(x);
    setCenterY(y);
  }

  public void setPosition(Point2D p) {
    this.setPosition(p.getX(), p.getY());
  }

  public Vertex getVertex() {
    return vertex;
  }

  @Override
  public void attachLabel(Label label) {
    this.attachedLabel = label;
    label.xProperty().bind(this.centerXProperty().subtract(label.getLayoutBounds().getWidth() / 2.0));
    label.yProperty().bind(this.centerYProperty().add(label.getLayoutBounds().getHeight() / 4.0));
  }

  @Override
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
    if (value < min + radius) {
      return min + radius;
    } else if (value > max - radius) {
      return max - radius;
    } else {
      return value;
    }
  }

  private void enableDrag() {
    PointVector dragDelta = new PointVector(0, 0);

    setOnMousePressed((mouseEvent) -> {
      if (mouseEvent.isPrimaryButtonDown()) {
        dragDelta.x = getCenterX() - mouseEvent.getX();
        dragDelta.y = getCenterY() - mouseEvent.getY();
        getScene().setCursor(Cursor.MOVE);
        isDragging = true;

        mouseEvent.consume();
      }
    });

    setOnMouseReleased((mouseEvent) -> {
      getScene().setCursor(Cursor.HAND);
      isDragging = false;

      mouseEvent.consume();
    });

    setOnMouseDragged((mouseEvent) -> {
      if (mouseEvent.isPrimaryButtonDown()) {
        double newX = mouseEvent.getX() + dragDelta.x;
        double x = boundCenterCoordinate(newX, 0, getParent().getLayoutBounds().getWidth());
        double newY = mouseEvent.getY() + dragDelta.y;
        double y = boundCenterCoordinate(newY, 0, getParent().getLayoutBounds().getHeight());
        setCenterX(x);
        setCenterY(y);
        mouseEvent.consume();
      }
    });

    setOnMouseEntered((mouseEvent) -> {
      if (!mouseEvent.isPrimaryButtonDown()) {
        getScene().setCursor(Cursor.HAND);
      }
    });

    setOnMouseExited((mouseEvent) -> {
      if (!mouseEvent.isPrimaryButtonDown()) {
        getScene().setCursor(Cursor.DEFAULT);
      }
    });
  }

  private class PointVector {
    double x, y;

    public PointVector(double x, double y) {
      this.x = x;
      this.y = y;
    }
  }

  @Override
  public void setStyleClass(String cssClass) {
    // TODO Auto-generated method stub
    styleProxy.setStyleClass(cssClass);
  }

  @Override
  public void addStyleClass(String cssClass) {
    // TODO Auto-generated method stub
    styleProxy.addStyleClass(cssClass);
  }

  @Override
  public boolean removeStyleClass(String cssClass) {
    // TODO Auto-generated method stub
    return styleProxy.removeStyleClass(cssClass);
  }

  public void setState(State.VertexState state) {
    if (!this.state.equals(state)) {
      this.state = state;
      updateState();
    }
  }

  private void updateState() {
    if (state == null)
      return;
    switch (state.getState()) {
      case DEFAULT:
        removeStyleClass("vertex-highlighted");
        removeStyleClass("vertex-unqueued");
        removeStyleClass("vertex-traversed");
        if (attachedLabel != null) {
          attachedLabel.removeStyleClass("vertex-label-highlighted");
          attachedLabel.removeStyleClass("vertex-label-unqueued");
          attachedLabel.removeStyleClass("vertex-label-traversed");
        }
        break;
      case HIGHLIGHTED:
        removeStyleClass("vertex-unqueued");
        removeStyleClass("vertex-traversed");
        addStyleClass("vertex-highlighted");
        if (attachedLabel != null) {
          attachedLabel.removeStyleClass("vertex-label-unqueued");
          attachedLabel.removeStyleClass("vertex-label-traversed");
          attachedLabel.addStyleClass("vertex-label-highlighted");
        }
        break;
      case UNQUEUED:
        removeStyleClass("vertex-highlighted");
        removeStyleClass("vertex-traversed");
        addStyleClass("vertex-unqueued");
        if (attachedLabel != null) {
          attachedLabel.removeStyleClass("vertex-label-highlighted");
          attachedLabel.removeStyleClass("vertex-label-traversed");
          attachedLabel.addStyleClass("vertex-label-unqueued");
        }
        break;
      case TRAVERSED:
        removeStyleClass("vertex-unqueued");
        removeStyleClass("vertex-highlighted");
        addStyleClass("vertex-traversed");
        if (attachedLabel != null) {
          attachedLabel.removeStyleClass("vertex-label-highlighted");
          attachedLabel.removeStyleClass("vertex-label-unqueued");
          attachedLabel.addStyleClass("vertex-label-traversed");
        }
        break;
      default:
        break;
    }
  }

  public void selected(boolean b) {
    if (b) {
      addStyleClass("vertex-selected");
    } else {
      removeStyleClass("vertex-selected");
    }
  }
}
