package hust.soict.hedspi.controllers;

import static javafx.beans.binding.Bindings.createBooleanBinding;

import java.util.Arrays;
import java.util.List;

import hust.soict.hedspi.model.graph.BaseGraph;
import hust.soict.hedspi.model.graph.Edge;
import hust.soict.hedspi.view.CircularPlacementStrategy;
import hust.soict.hedspi.view.GraphPanel;
import hust.soict.hedspi.view.PlacementStrategy;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class ExampleGraphController {
  @FXML
  private VBox exampleListContainer;
  @FXML
  private BorderPane previewContainer, root;
  @FXML
  private Button doneBtn, cancelBtn;
  @FXML
  private AnchorPane controlPane;
  @FXML
  private Line line;

  private BaseGraph<? extends Edge> graph = null;
  private GraphPanel graphPanel = null;
  private String graphSelected = null;
  private BooleanProperty haveGraphProperty = new SimpleBooleanProperty(false);
  private List<String> exampleList = Arrays.asList("CP410", "CP414", "K5", "Rail", "Tesselation", "CP4414");
  private PauseTransition pauseTransition = null;

  public void close() {
    previewContainer.getScene().getWindow().hide();
  }

  private void getGraph(String name) {
    if (graphSelected == name) {
      return;
    }
    if (graph != null) {
      graphPanel.automaticLayoutProperty().set(false);
    }
    switch (name) {
      case "CP410":
        graph = BaseGraph.CP410();
        break;
      case "CP414":
        graph = BaseGraph.CP414();
        break;
      case "K5":
        graph = BaseGraph.K5();
        break;
      case "Rail":
        graph = BaseGraph.Rail();
        break;
      case "Tesselation":
        graph = BaseGraph.Tesselation();
        break;
      case "CP4414":
        graph = BaseGraph.CP4414();
        break;
      default:
        break;
    }
    if (graph != null)
      graphSelected = name;

    haveGraphProperty.set(!haveGraphProperty.get());
  }

  public BaseGraph<? extends Edge> getGraph() {
    return graph;
  }

  public void adjustUI(WindowEvent event) {
    line.endXProperty().bind(root.widthProperty());

    BooleanBinding disableBinding = createBooleanBinding(() -> {
      return graph == null;
    }, haveGraphProperty);
    doneBtn.disableProperty().bind(disableBinding);

    for (int i = 0; i < exampleList.size(); i++) {
      Label label = new Label(exampleList.get(i));
      label.getStyleClass().add("label");
      if (i % 2 == 0) {
        label.getStyleClass().add("odd");
      } else {
        label.getStyleClass().add("even");
      }
      label.maxWidthProperty().bind(exampleListContainer.widthProperty());

      label.setOnMouseClicked(e -> {
        exampleListContainer.getChildren().forEach(node -> {
          node.getStyleClass().remove("selected");
        });
        label.getStyleClass().add("selected");
        getGraph(label.getText());
      });

      exampleListContainer.getChildren().add(label);
    }

    haveGraphProperty.addListener((ov, oldValue, newValue) -> {
      if (graph != null) {
        previewContainer.getChildren().clear();
        PlacementStrategy placementStrategy = new CircularPlacementStrategy();
        graphPanel = new GraphPanel(graph, placementStrategy);
        previewContainer.setCenter(graphPanel);
        if (pauseTransition == null) {
          pauseTransition = new PauseTransition(Duration.millis(50));
          pauseTransition.setOnFinished(e -> {
            graphPanel.init();
            pauseTransition = null;
          });
          pauseTransition.play();
        }
      }
    });

    doneBtn.setOnAction(e -> {
      close();
    });

    cancelBtn.setOnAction(e -> {
      graph = null;
      close();
    });

    Platform.runLater(() -> {
      root.getScene().getWindow().setOnCloseRequest(e -> {
        graph = null;
        close();
      });
    });
  }
}
