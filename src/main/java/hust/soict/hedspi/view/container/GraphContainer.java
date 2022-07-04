package hust.soict.hedspi.view.container;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

public class GraphContainer extends BorderPane {
  private final DoubleProperty scaleFactorProperty = new ReadOnlyDoubleWrapper(1);
  private final Node content;

  private static final double MIN_SCALE = 1;
  private static final double MAX_SCALE = 5;
  private static final double SCROLL_DELTA = 0.25;

  public GraphContainer(Node content) {
    if (content == null)
      throw new IllegalArgumentException("Cannot content be null");
    this.content = content;
    Node center = content;
    setCenter(center);

    content.toFront();
    enablePanAndZoom();
  }

  private void enablePanAndZoom() {
    setOnScroll(e -> {
      double direction = e.getDeltaY() >= 0 ? 1 : -1;

      double currentScale = scaleFactorProperty.getValue();
      double computedScale = currentScale + direction * SCROLL_DELTA;
      computedScale = boundValue(computedScale);

      if (currentScale != computedScale) {
        content.setScaleX(computedScale);
        content.setScaleY(computedScale);

        if (computedScale == 1) {
          content.setTranslateX(-getTranslateX());
          content.setTranslateY(-getTranslateY());
        } else {
          scaleFactorProperty.setValue(computedScale);
          Bounds bounds = content.localToScene(content.getBoundsInLocal());
          double f = (computedScale / currentScale) - 1;
          double dx = e.getX() - (bounds.getWidth() / 2 + bounds.getMinX());
          double dy = e.getY() - (bounds.getHeight() / 2 + bounds.getMinY());
          setContentPivot(f * dx, f * dy);
        }
      }
      e.consume();
    });

    final DragContent sceneDragContext = new DragContent();

    setOnMousePressed(e -> {
      if (e.isSecondaryButtonDown()) {
        getScene().setCursor(Cursor.MOVE);

        sceneDragContext.mouseAnchorX = e.getX();
        sceneDragContext.mouseAnchorY = e.getY();

        sceneDragContext.translateAnchorX = content.getTranslateX();
        sceneDragContext.translateAnchorY = content.getTranslateY();
      }
    });

    setOnMouseReleased(e -> {
      getScene().setCursor(Cursor.DEFAULT);
    });

    setOnMouseDragged(e -> {
      if (e.isSecondaryButtonDown()) {

        content.setTranslateX(sceneDragContext.translateAnchorX + e.getX() - sceneDragContext.mouseAnchorX);
        content.setTranslateY(sceneDragContext.translateAnchorY + e.getY() - sceneDragContext.mouseAnchorY);
      }
    });
  }

  public void setContentPivot(double x, double y) {
    content.setTranslateX(content.getTranslateX() - x);
    content.setTranslateY(content.getTranslateY() - y);
  }

  public static double boundValue(double value) {
    if (Double.compare(value, MIN_SCALE) < 0) {
      return MIN_SCALE;
    }

    if (Double.compare(value, MAX_SCALE) > 0) {
      return MAX_SCALE;
    }

    return value;
  }

  public DoubleProperty scaleFactorProperty() {
    return scaleFactorProperty;
  }

  class DragContent {
    double mouseAnchorX;
    double mouseAnchorY;

    double translateAnchorX;
    double translateAnchorY;
  }
}
