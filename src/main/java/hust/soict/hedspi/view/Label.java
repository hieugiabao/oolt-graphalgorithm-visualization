package hust.soict.hedspi.view;

import javafx.scene.text.Text;

public class Label extends Text implements StylableNode {
  private final StyleProxy styleProxy;

  public Label(double x, double y, String text) {
    super(x, y, text);
    this.styleProxy = new StyleProxy(this);
  }

  public Label(String text) {
    this(0, 0, text);
  }

  public Label() {
    this(0, 0, "");
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
}
