package hust.soict.hedspi.controllers;

import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hust.soict.hedspi.model.algo.spanning.SpanningTreeAlgorithm;
import hust.soict.hedspi.model.algo.step.State;
import hust.soict.hedspi.model.algo.step.Step;
import hust.soict.hedspi.model.graph.BaseGraph;
import hust.soict.hedspi.model.graph.Edge;
import hust.soict.hedspi.model.graph.Vertex;
import hust.soict.hedspi.view.CircularPlacementStrategy;
import hust.soict.hedspi.view.GraphPanel;
import hust.soict.hedspi.view.PlacementStrategy;
import hust.soict.hedspi.view.container.GraphContainer;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class MainController implements Initializable {
  private static final Logger logger = LogManager.getLogger(MainController.class);

  @FXML
  private VBox root;
  @FXML
  private SplitPane main;
  @FXML
  private BorderPane container;
  @FXML
  private ControlController controlController;
  @FXML
  private HierarchyController hierarchyController;
  @FXML
  private AlgorithmShowController pseudoCodeController;

  BaseGraph<? extends Edge> graph = null;
  SpanningTreeAlgorithm mstAlgorithm = null;
  BooleanProperty graphDisableBtnProperty = new SimpleBooleanProperty(true);
  BooleanProperty algoDisableProperty = new SimpleBooleanProperty(true);
  GraphPanel graphPane = null;

  @Override
  public void initialize(URL uri, ResourceBundle resourceBundle) {
    // TODO Auto-generated method stub
    if (graph == null) {
      container.setStyle("-fx-background-color: #cccccc;");
    }
    controlController.injectMainController(this);
    hierarchyController.injectMainController(this);
    pseudoCodeController.injectMainController(this);

    Platform.runLater(() -> {
      root.prefHeightProperty().bind(root.getScene().getWindow().heightProperty());
      main.prefHeightProperty().bind(root.prefHeightProperty().multiply(0.81));
      hierarchyController.getRoot().prefHeightProperty().bind(main.prefHeightProperty());
    });

    graphDisableBtnProperty.addListener((ov, oldValue, newValue) -> {
      container.getChildren().clear();
      if (graph == null) {
        container.setStyle("-fx-background-color: #cccccc;");
      } else {
        container.setStyle("");
        drawGraph();
      }
    });
  }

  public void selectVertex(Vertex v) {
    logger.info("Selected vertex: " + v);
    graphPane.selectVertex(v);
  }

  public void selectEdge(Edge e) {
    logger.info("Selected vertex: " + e);
    graphPane.selectEdge(e);
  }

  private void drawGraph() {
    PlacementStrategy placementStrategy = new CircularPlacementStrategy();
    graphPane = new GraphPanel(graph, placementStrategy);
    GraphContainer graphContainer = new GraphContainer(graphPane);
    container.setCenter(graphContainer);
    Platform.runLater(() -> {
      graphPane.init();
    });
  }

  public void updateDisplay(Step step) {
    updateGraphDisplay(step.getState());
    updateDetailCodeDisplay(step.getStatus());
    highlightPseudoCode(step.getLineNo());
  }

  private void updateGraphDisplay(State state) {
    graphPane.setState(state);
  }

  private void updateDetailCodeDisplay(String detail) {
    pseudoCodeController.setDetailCode(detail);
  }

  private void highlightPseudoCode(int[] lineNo) {
    pseudoCodeController.highlightPseudoCode(lineNo);
  }

  public void clearDisplay() {
    graphPane.setState(new State(new LinkedList<Vertex>(graph.vertexSet()), new LinkedList<Edge>(graph.edgeSet())));
  }

  public boolean isPlaying() {
    return controlController.isPlaying();
  }
}
