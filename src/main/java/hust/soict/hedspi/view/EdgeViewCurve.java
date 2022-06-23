package hust.soict.hedspi.view;

import hust.soict.hedspi.model.graph.Edge;
import javafx.scene.shape.CubicCurve;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import static hust.soict.hedspi.utils.Utilities.*;

public class EdgeViewCurve extends CubicCurve implements BaseEdgeView {

  private final Edge edge;
  private final StyleProxy styleProxy;
  private final VertexView source;
  private final VertexView target;

  private Label attachedLabel = null;
  private Arrow attachedArrow = null;

  public EdgeViewCurve(Edge e, VertexView source, VertexView target) {
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

    enableListeners();
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
      double midpointY1 = source.getCenterY() - target.getRadius() * 2;

      double midpointX2 = source.getCenterX() + target.getRadius() * 5;
      double midpointY2 = source.getCenterY() - target.getRadius() * 2;

      setControlX1(midpointX1);
      setControlY1(midpointY1);
      setControlX2(midpointX2);
      setControlY2(midpointY2);
    } else {
      // atode
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
  public StylableNode getAttachedLabel() {
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

}
