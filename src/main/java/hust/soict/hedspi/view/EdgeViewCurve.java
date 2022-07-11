package hust.soict.hedspi.view;

import hust.soict.hedspi.model.algo.step.State;
import hust.soict.hedspi.model.algo.step.State.EdgeState;
import hust.soict.hedspi.model.graph.Edge;
import javafx.geometry.Point2D;
import javafx.scene.shape.CubicCurve;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import static hust.soict.hedspi.utils.Utilities.rotate;

import static hust.soict.hedspi.utils.Utilities.*;

public class EdgeViewCurve extends CubicCurve implements BaseEdgeView {

  private final Edge edge;
  private final StyleProxy styleProxy;
  private final VertexView source;
  private final VertexView target;

  private Label attachedLabel = null;
  private Arrow attachedArrow = null;
  private int angleFactor = 0;
  private State.EdgeState state;

  public EdgeViewCurve(Edge e, VertexView source, VertexView target) {
    this(e, source, target, false, null);
  }

  public EdgeViewCurve(Edge e, VertexView source, VertexView target, boolean haveReverseEdge) {
    this(e, source, target, haveReverseEdge, null);
  }

  public EdgeViewCurve(Edge e, VertexView source, VertexView target, boolean haveReverseEdge, State.EdgeState state) {
    if (source == null || target == null) {
      throw new IllegalArgumentException("Cannot connect null vertex");
    }

    this.edge = e;
    this.source = source;
    this.target = target;
    styleProxy = new StyleProxy(this);
    styleProxy.addStyleClass("edge");

    startXProperty().bind(target.centerXProperty());
    startYProperty().bind(target.centerYProperty());
    endXProperty().bind(source.centerXProperty());
    endYProperty().bind(source.centerYProperty());

    angleFactor = haveReverseEdge ? 1 : 0;

    update();
    enableListeners();

    this.state = state == null ? new State.EdgeState(this.edge, State.EDGE_STATE.DEFAULT) : state;
    updateState();
  }

  private void enableListeners() {
    startXProperty().addListener((ov, t1, t2) -> {
      update();
    });
    startYProperty().addListener((ov, t1, t2) -> {
      update();
    });
    endXProperty().addListener((ov, t1, t2) -> {
      update();
    });
    endYProperty().addListener((ov, t1, t2) -> {
      update();
    });
  }

  private void update() {
    // TODO: find control1 and control2 points
    if (source.equals(target)) {
      double midpointX1 = source.getCenterX() - target.getRadius() * 5;
      double midpointY1 = source.getCenterY() - target.getRadius() * 3;

      double midpointX2 = source.getCenterX() + target.getRadius() * 5;
      double midpointY2 = source.getCenterY() - target.getRadius() * 3;

      setControlX1(midpointX1);
      setControlY1(midpointY1);
      setControlX2(midpointX2);
      setControlY2(midpointY2);
    } else {
      // atode
      double midpointX = (source.getCenterX() + target.getCenterX()) / 2;
      double midpointY = (source.getCenterY() + target.getCenterY()) / 2;

      Point2D midPoint = new Point2D(midpointX, midpointY);

      Point2D startPoint = new Point2D(source.getCenterX(), source.getCenterY());
      Point2D endPoint = new Point2D(target.getCenterX(), target.getCenterY());

      final double MAX_ANGLE = 20;
      double distance = startPoint.distance(endPoint);
      double angle = MAX_ANGLE - (distance / 15000 * MAX_ANGLE);

      midPoint = rotate(midPoint,
          angleFactor == 0 ? startPoint : endPoint,
          (-angle) + angleFactor * (angle - (-angle)));

      setControlX1(midPoint.getX());
      setControlY1(midPoint.getY());
      setControlX2(midPoint.getX());
      setControlY2(midPoint.getY());
    }
  }

  @Override
  public void attachLabel(Label label) {
    // TODO Auto-generated method stub
    this.attachedLabel = label;
    label.xProperty()
        .bind(controlX1Property().add(controlX2Property()).divide(2)
            .subtract(label.getLayoutBounds().getWidth() / 2));
    label.yProperty()
        .bind(controlY1Property().add(controlY2Property()).divide(2)
            .add(label.getLayoutBounds().getHeight() / 2));
  }

  @Override
  public Label getAttachedLabel() {
    // TODO Auto-generated method stub
    return attachedLabel;
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

  @Override
  public Edge getEdge() {
    // TODO Auto-generated method stub
    return edge;
  }

  @Override
  public Arrow getAttatchedArrow() {
    // TODO Auto-generated method stub
    return attachedArrow;
  }

  @Override
  public void attachArrow(Arrow arrow) {
    // TODO Auto-generated method stub
    this.attachedArrow = arrow;
    arrow.translateXProperty().bind(endXProperty());
    arrow.translateYProperty().bind(endYProperty());

    // rotate arrow around itself based on this line's angle
    Rotate rotation = new Rotate();
    rotation.pivotXProperty().bind(translateXProperty());
    rotation.pivotYProperty().bind(translateYProperty());
    rotation.angleProperty().bind(
        toDegrees(atan2(endYProperty().subtract(controlY2Property()),
            endXProperty().subtract(controlX2Property()))));

    arrow.getTransforms().add(rotation);

    // add translation transform to put the arrow touching the circle's bounds
    Translate t = new Translate(-target.getRadius(), 0);
    arrow.getTransforms().add(t);
  }

  @Override
  public void setState(EdgeState state) {
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
        removeStyleClass("edge-unqueued");
        removeStyleClass("edge-highlighted");
        removeStyleClass("edge-traversed");
        if (attachedLabel != null) {
          attachedLabel.removeStyleClass("edge-label-highlighted");
          attachedLabel.removeStyleClass("edge-label-unqueued");
          attachedLabel.removeStyleClass("edge-label-traversed");
        }
        if (attachedArrow != null) {
          attachedArrow.removeStyleClass("arrow-highlighted");
          attachedArrow.removeStyleClass("arrow-unqueued");
          attachedArrow.removeStyleClass("arrow-traversed");
        }
        break;
      case UNQUEUED:
        removeStyleClass("edge-highlighted");
        removeStyleClass("edge-traversed");
        addStyleClass("edge-unqueued");
        if (attachedLabel != null) {
          attachedLabel.removeStyleClass("edge-label-highlighted");
          attachedLabel.removeStyleClass("edge-label-traversed");
          attachedLabel.addStyleClass("edge-label-unqueued");
        }
        if (attachedArrow != null) {
          attachedArrow.removeStyleClass("arrow-highlighted");
          attachedArrow.removeStyleClass("arrow-unqueued");
          attachedArrow.addStyleClass("arrow-unqueued");
        }
        break;
      case HIGHLIGHTED:
        removeStyleClass("edge-unqueued");
        removeStyleClass("edge-traversed");
        addStyleClass("edge-highlighted");
        if (attachedLabel != null) {
          attachedLabel.removeStyleClass("edge-label-unqueued");
          attachedLabel.removeStyleClass("edge-label-traversed");
          attachedLabel.addStyleClass("edge-label-highlighted");
        }
        if (attachedArrow != null) {
          attachedArrow.removeStyleClass("arrow-traversed");
          attachedArrow.removeStyleClass("arrow-unqueued");
          attachedArrow.addStyleClass("arrow-highlighted");
        }
        break;
      case TRAVERSED:
        removeStyleClass("edge-unqueued");
        removeStyleClass("edge-highlighted");
        addStyleClass("edge-traversed");
        if (attachedLabel != null) {
          attachedLabel.removeStyleClass("edge-label-highlighted");
          attachedLabel.removeStyleClass("edge-label-unqueued");
          attachedLabel.addStyleClass("edge-label-traversed");
        }
        if (attachedArrow != null) {
          attachedArrow.removeStyleClass("arrow-unqueued");
          attachedArrow.removeStyleClass("arrow-highlighted");
          attachedArrow.addStyleClass("arrow-traversed");
        }
        break;
      default:
        break;
    }
  }

  @Override
  public void selected(boolean b) {
    // TODO Auto-generated method stub
    if (b) {
      addStyleClass("edge-selected");
      if (attachedLabel != null) {
        attachedLabel.addStyleClass("edge-label-selected");
      }
      if (attachedArrow != null) {
        attachedArrow.addStyleClass("arrow-selected");
      }
    } else {
      removeStyleClass("edge-selected");
      if (attachedLabel != null) {
        attachedLabel.removeStyleClass("edge-label-selected");
      }
      if (attachedArrow != null) {
        attachedArrow.removeStyleClass("arrow-selected");
      }
    }
  }
}
