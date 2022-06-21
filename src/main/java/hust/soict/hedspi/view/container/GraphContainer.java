package hust.soict.hedspi.view.container;

import hust.soict.hedspi.view.GraphPanel;
import javafx.scene.layout.BorderPane;

public class GraphContainer extends BorderPane {
  public GraphContainer(GraphPanel graphView) {
    setCenter(graphView);
  }
}
