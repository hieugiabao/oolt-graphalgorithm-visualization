package hust.soict.hedspi.view;

import static hust.soict.hedspi.utils.Utilities.*;

import hust.soict.hedspi.model.algo.step.State;
import hust.soict.hedspi.model.algo.step.State.EdgeState;
import hust.soict.hedspi.model.graph.Edge;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class EdgeViewLine extends Line implements BaseEdgeView {
  private final Edge edge;

  private final StyleProxy styleProxy;
  private final VertexView source;
  private final VertexView target;

  private Label attachedLabel = null;
  private Arrow attachedArrow = null;
  private State.EdgeState state;

  public EdgeViewLine(Edge e, VertexView source, VertexView target, State.EdgeState state) {
    if (source == null || target == null) {
      throw new IllegalArgumentException("Cannot connect null vertex");
    }

    this.edge = e;
    this.source = source;
    this.target = target;
    styleProxy = new StyleProxy(this);
    styleProxy.addStyleClass("edge");

    startXProperty().bind(source.centerXProperty());
    startYProperty().bind(source.centerYProperty());
    endXProperty().bind(target.centerXProperty());
    endYProperty().bind(target.centerYProperty());

    this.state = state == null ? new State.EdgeState(this.edge, State.EDGE_STATE.DEFAULT) : state;
    updateState();
  }

  public EdgeViewLine(Edge e, VertexView source, VertexView target) {
    this(e, source, target, null);
  }

  @Override
  public void attachLabel(Label label) {
    this.attachedLabel = label;
    label.xProperty().bind(
        this.startXProperty().add(this.endXProperty()).divide(2)
            .subtract(label.getLayoutBounds().getWidth() / 2));
    label.yProperty().bind(
        this.startYProperty().add(this.endYProperty()).divide(2)
            .add(label.getLayoutBounds().getHeight() / 1.5));
  }

  @Override
  public void attachArrow(Arrow arrow) {
    this.attachedArrow = arrow;
    arrow.translateXProperty().bind(endXProperty());
    arrow.translateYProperty().bind(endYProperty());

    // rotate arrow base on this line's angle
    Rotate rotation = new Rotate();
    rotation.pivotXProperty().bind(translateXProperty());
    rotation.pivotYProperty().bind(translateYProperty());
    rotation.angleProperty().bind(
        toDegrees(atan2(endYProperty().subtract(startYProperty()),
            endXProperty().subtract(startXProperty()))));

    arrow.getTransforms().add(rotation);
    Translate translation = new Translate(-target.getRadius(), 0);
    arrow.getTransforms().add(translation);
  }

  @Override
  public Label getAttachedLabel() {
    return attachedLabel;
  }

  @Override
  public Edge getEdge() {
    return edge;
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
  public Arrow getAttatchedArrow() {
    // TODO Auto-generated method stub
    return this.attachedArrow;
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
