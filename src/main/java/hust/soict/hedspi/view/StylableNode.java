package hust.soict.hedspi.view;

public interface StylableNode {
  public void setStyle(String css);

  public void setStyleClass(String cssClass);

  public void addStyleClass(String cssClass);

  public boolean removeStyleClass(String cssClass);
}
