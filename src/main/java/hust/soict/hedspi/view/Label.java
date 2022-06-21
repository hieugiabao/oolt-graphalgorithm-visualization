package hust.soict.hedspi.view;

import javafx.scene.text.Text;

public class Label extends Text {
  public Label(double x, double y, String text) {
    super(x, y, text);
  }

  public Label(String text) {
    this(0, 0, text);
  }

  public Label() {
    this(0, 0, "");
  }
}
