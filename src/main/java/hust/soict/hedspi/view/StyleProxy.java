package hust.soict.hedspi.view;

import javafx.scene.shape.Shape;

public class StyleProxy implements StylableNode {
  private final Shape client;

  public StyleProxy(Shape client) {
    this.client = client;
  }

  @Override
  public void setStyle(String css) {
    client.setStyle(css);
  }

  @Override
  public void setStyleClass(String cssClass) {
    client.getStyleClass().clear();
    client.setStyle(null);
    client.getStyleClass().add(cssClass);
  }

  @Override
  public void addStyleClass(String cssClass) {
    client.getStyleClass().add(cssClass);
  }

  @Override
  public boolean removeStyleClass(String cssClass) {
    return client.getStyleClass().remove(cssClass);
  }
}
