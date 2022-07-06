package hust.soict.hedspi.controllers;

import static javafx.beans.binding.Bindings.createBooleanBinding;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hust.soict.hedspi.model.algo.spanning.Kruskal;
import hust.soict.hedspi.model.algo.spanning.Prim;
import hust.soict.hedspi.model.algo.step.Step;
import hust.soict.hedspi.model.graph.BaseGraph;
import hust.soict.hedspi.model.graph.Edge;
import hust.soict.hedspi.model.graph.Vertex;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ControlController implements Initializable {
  private static final Logger logger = LogManager.getLogger(ControlController.class);

  @FXML
  private HBox root;
  @FXML
  private Button createGraphBtn, exampleBtn, editBtn, printBtn, goBeginBtn,
      backwardBtn, playBtn, forwardBtn, goEndBtn;
  @FXML
  private Label speedLabel;
  @FXML
  private MenuButton menuAlgo;
  @FXML
  private Slider speedControl, progressSlider;
  @FXML
  private Rectangle progressRec;
  @FXML
  private Menu primBtn;
  @FXML
  private MenuItem kruskalBtn;

  private List<Step> steps = null;
  private MainController mainController;

  private int currentIteration = -1, maxIteration = 0;
  private boolean isPlaying = false, isPaused = false;

  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {
    Platform.runLater(() -> {
      setupSpeedControl();
      setupProgressShow();
      binding();

      createGraphBtn.setOnMousePressed(e -> {
        if (e.isPrimaryButtonDown())
          createGraph();
      });

      kruskalBtn.setOnAction(e -> {
        mainController.mstAlgorithm = new Kruskal(mainController.graph);
        steps = mainController.mstAlgorithm.getStepsList();
        maxIteration = steps.size();
        progressSlider.setMax(maxIteration);
        mainController.algoDisableProperty.set(false);
        menuAlgo.setText("Kruskal");
        e.consume();
      });
    });
  }

  private void binding() {
    BooleanBinding graphBinding = createBooleanBinding(() -> {
      if (mainController.graph == null) {
        return true;
      } else
        return false;
    }, mainController.graphDisableBtnProperty);
    editBtn.disableProperty().bind(graphBinding);
    printBtn.disableProperty().bind(graphBinding);
    menuAlgo.disableProperty().bind(graphBinding);

    BooleanBinding algorithmBinding = createBooleanBinding(() -> {
      if (mainController.mstAlgorithm == null)
        return true;
      else
        return false;
    }, mainController.algoDisableProperty);
    goBeginBtn.disableProperty().bind(algorithmBinding);
    forwardBtn.disableProperty().bind(algorithmBinding);
    backwardBtn.disableProperty().bind(algorithmBinding);
    playBtn.disableProperty().bind(algorithmBinding);
    goEndBtn.disableProperty().bind(algorithmBinding);
    progressSlider.setMax(maxIteration);
  }

  private double boundValue(double value, double min, double max) {
    if (value < min) {
      return min;
    } else if (value > max) {
      return max;
    } else {
      return value;
    }
  }

  private void setSpeed() {
    double newValue = speedControl.getValue();

    if (newValue >= 0.5 && newValue < 0.625) {
      newValue = 0.5;
    } else if (newValue >= 0.625 && newValue < 0.875) {
      newValue = 0.75;
    } else if (newValue >= 0.875 && newValue < 1.125) {
      newValue = 1;
    } else if (newValue >= 1.125 && newValue < 1.375) {
      newValue = 1.25;
    } else
      newValue = 1.5;

    newValue = boundValue(newValue, speedControl.getMin(), speedControl.getMax());
    speedControl.setValue(newValue);
  }

  private void setupSpeedControl() {
    StringProperty speedLabelProperty = new SimpleStringProperty("Run speed at ");
    speedLabel.textProperty().bind(speedLabelProperty.concat(speedControl.valueProperty().asString()).concat("x"));

    speedControl.setOnMousePressed(e -> {
      setSpeed();
    });

    speedControl.setOnMouseDragged(e -> {
      setSpeed();
    });
  }

  private void setupProgressShow() {
    progressSlider.setPrefWidth(300);
    progressSlider.setBlockIncrement(1);
    progressRec.heightProperty().bind(progressSlider.heightProperty().subtract(2));
    progressRec.widthProperty().bind(progressSlider.widthProperty());

    progressSlider.valueProperty().addListener((ov, oldValue, newValue) -> {
      int value = Math.round(newValue.floatValue());
      int percent = maxIteration == 0 ? 0 : (int) ((value * 100) / maxIteration);
      String style = String.format("-fx-fill: linear-gradient(to right, #2D819D %d%%, #ccc %d%%);",
          percent, percent);
      progressRec.setStyle(style);
      progressSlider.setValue(value);
    });
  }

  private void createGraph() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/create-graph.fxml"));
      Parent parent = loader.load();
      CreateGraphController createGraphController = loader.getController();

      Scene newScene = new Scene(parent);
      Stage newStage = new Stage(StageStyle.DECORATED);
      newStage.initModality(Modality.APPLICATION_MODAL);
      newStage.initOwner(root.getScene().getWindow());
      newStage.setTitle("Create graph");
      newStage.setScene(newScene);
      newStage.showAndWait();

      if (createGraphController.getGraph() != null) {
        setGraph(createGraphController.getGraph());
        boolean preValue = mainController.graphDisableBtnProperty.get();
        mainController.graphDisableBtnProperty.set(!preValue);
        logger.info("graph received: {}, {}", mainController.graph.vertexSet(), mainController.graph.edgeSet());
      } else {
        logger.info("graph is null");
      }
    } catch (IOException e) {
      logger.error("Cannot open fxml file", e);
    }
  }

  private void setGraph(BaseGraph<? extends Edge> graph) {
    if (graph == null)
      throw new IllegalArgumentException("graph cannot be null");
    mainController.graph = graph;
    for (Vertex v : mainController.graph.vertexSet()) {
      MenuItem item = new MenuItem(v.getId() + "");
      primBtn.getItems().add(item);

      item.setOnAction(e -> {
        mainController.mstAlgorithm = new Prim(graph, v);
        steps = mainController.mstAlgorithm.getStepsList();
        maxIteration = steps.size();
        progressSlider.setMax(maxIteration);
        mainController.algoDisableProperty.set(false);
        menuAlgo.setText("PRIM - Start " + v.getId());
        e.consume();
      });
    }
  }

  public void injectMainController(MainController mainController) {
    this.mainController = mainController;
  }
}
