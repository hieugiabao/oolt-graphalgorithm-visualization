package hust.soict.hedspi.view;

import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

public class Arrow extends Path {

  public Arrow(double size) {
    super.getElements().add(new MoveTo(0, 0));
    super.getElements().add(new LineTo(-size, size));
    super.getElements().add(new MoveTo(0, 0));
    super.getElements().add(new LineTo(-size, -size));

    getStyleClass().add("arrow");
  }

}
