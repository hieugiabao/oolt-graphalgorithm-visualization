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
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
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
import javafx.util.Duration;

public class ControlController implements Initializable {
  private static final Logger logger = LogManager.getLogger(ControlController.class);
  private final int NO_ITERATION = -1;
  private final int ANIMATION_PLAY = 1;
  private final int ANIMATION_PAUSE = 0;
  private final int ANIMATION_STOP = -1;
  private final int DEFAULT_DURATION = 1000;

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
  private SequentialTransition sequentialTransition;

  private int currentIteration = NO_ITERATION, maxIteration = 0, animationStatus = ANIMATION_STOP;
  private boolean isPlaying = false, isPaused = false;
  private int animationDuration = DEFAULT_DURATION;
  private PauseTransition pauseControl = null;

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
        if (isPlaying)
          stop();
        mainController.mstAlgorithm = new Kruskal(mainController.graph);
        boolean preValue = mainController.algoDisableProperty.get();
        mainController.algoDisableProperty.set(!preValue);
        menuAlgo.setText("Kruskal");
        e.consume();
      });

      playBtn.setOnMousePressed(e -> {
        if (e.isPrimaryButtonDown()) {
          if (isPlaying) {
            play();
          } else {
            startAnimation();
          }
        }
      });

      goBeginBtn.setOnMousePressed(e -> {
        if (e.isPrimaryButtonDown()) {
          goToBegin();
        }
      });

      goEndBtn.setOnMousePressed(e -> {
        if (e.isPrimaryButtonDown()) {
          goToEnd();
        }
      });

      backwardBtn.setOnMousePressed(e -> {
        if (e.isPrimaryButtonDown()) {
          stepBackward();
        }
      });

      forwardBtn.setOnMousePressed(e -> {
        if (e.isPrimaryButtonDown()) {
          stepForward();
        }
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

  private void setupSpeedControl() {
    StringProperty speedLabelProperty = new SimpleStringProperty("Run speed at ");
    speedLabel.textProperty().bind(speedLabelProperty.concat(speedControl.valueProperty().asString()).concat("x"));

    speedControl.valueProperty().addListener((ov, oldValue, newValue) -> {
      double doubleValue = newValue.doubleValue();
      if (doubleValue >= 0.5 && doubleValue < 0.625) {
        doubleValue = 0.5;
      } else if (doubleValue >= 0.625 && doubleValue < 0.875) {
        doubleValue = 0.75;
      } else if (doubleValue >= 0.875 && doubleValue < 1.125) {
        doubleValue = 1;
      } else if (doubleValue >= 1.125 && doubleValue < 1.375) {
        doubleValue = 1.25;
      } else
        doubleValue = 1.5;

      doubleValue = boundValue(doubleValue, speedControl.getMin(), speedControl.getMax());
      speedControl.setValue(doubleValue);
      animationDuration = (int) (DEFAULT_DURATION / speedControl.getValue());
      if (isPlaying && !isPaused) {
        pause();
        if (pauseControl == null) {
          pauseControl = new PauseTransition(Duration.millis(animationDuration));
          pauseControl.setOnFinished(ev -> {
            play();
            pauseControl = null;
          });
          pauseControl.play();
        }
      }
    });
  }

  private void setupProgressShow() {
    progressSlider.setPrefWidth(300);
    progressSlider.setBlockIncrement(1);
    progressRec.heightProperty().bind(progressSlider.heightProperty().subtract(2));
    progressRec.widthProperty().bind(progressSlider.widthProperty());

    progressSlider.valueProperty().addListener((ov, oldValue, newValue) -> {
      int value = Math.round(newValue.floatValue());
      int percent = maxIteration == 0 ? 0 : Math.round((value * 100) / (maxIteration - 1));
      String style = String.format("-fx-fill: linear-gradient(to right, #2D819D %d%%, #ccc %d%%);",
          percent, percent);
      progressRec.setStyle(style);
      progressSlider.setValue(value);
    });

    progressSlider.setOnMousePressed(e -> {
      if (isPlaying && !isPaused && e.isPrimaryButtonDown()) {
        pause();
        jumToIteration((int) (progressSlider.getValue()));
        if (pauseControl == null) {
          pauseControl = new PauseTransition(Duration.millis(animationDuration));
          pauseControl.setOnFinished(ev -> {
            play();
            pauseControl = null;
          });
          pauseControl.play();
        }
      }
    });

    progressSlider.setOnMouseDragged(e -> {
      if (isPlaying && e.isPrimaryButtonDown()) {
        pause();
        jumToIteration((int) (progressSlider.getValue()));
      }
    });

    progressSlider.setOnMouseDragExited(e -> {
      if (isPlaying && !isPaused && e.isPrimaryButtonDown()) {
        pause();
        jumToIteration((int) (progressSlider.getValue()));
        if (pauseControl == null) {
          pauseControl = new PauseTransition(Duration.millis(animationDuration));
          pauseControl.setOnFinished(ev -> {
            play();
            pauseControl = null;
          });
          pauseControl.play();
        }
      }
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
    boolean preValue = mainController.graphDisableBtnProperty.get();
    mainController.graphDisableBtnProperty.set(!preValue);

    mainController.mstAlgorithm = null;
    preValue = mainController.algoDisableProperty.get();
    mainController.algoDisableProperty.set(!preValue);
    reset();
    for (Vertex v : mainController.graph.vertexSet()) {
      MenuItem item = new MenuItem(v.getId() + "");
      primBtn.getItems().add(item);

      item.setOnAction(e -> {
        if (isPlaying)
          stop();
        mainController.mstAlgorithm = new Prim(graph, v);
        mainController.algoDisableProperty.set(!mainController.algoDisableProperty.get());
        menuAlgo.setText("PRIM - Start " + v.getId());
        e.consume();
      });
    }
  }

  private void reset() {
    maxIteration = 0;
    isPlaying = false;
    isPaused = false;
    currentIteration = NO_ITERATION;
    progressSlider.setMax(0);
    progressSlider.setValue(0);
    menuAlgo.setText("Choose Algorithm");
    primBtn.getItems().clear();
  }

  private boolean isAtEnd() {
    return (currentIteration == maxIteration);
  }

  private void stop() {
    currentIteration = 0;
    maxIteration = 0;
    progressSlider.setMax(0);
    progressSlider.setValue(0);

    animationStatus = ANIMATION_STOP;
    steps = null;
    mainController.clearDisplay();
    isPlaying = false;
    isPaused = false;
  }

  public void startAnimation() {
    if (steps == null) {
      steps = mainController.mstAlgorithm.getStepsList();
      maxIteration = steps.size();
      progressSlider.setMax(maxIteration - 1);
    }
    if (currentIteration == NO_ITERATION)
      currentIteration = 0;
    isPlaying = true;
    isPaused = false;
    play();
  }

  private synchronized void play() {
    if (isPlaying) {
      isPaused = false;
      if (currentIteration < 0)
        currentIteration = 0;
      sequentialTransition = new SequentialTransition();
      if (animationStatus == ANIMATION_STOP) {
        animationStatus = ANIMATION_PLAY;
        updateDisplay();
      } else {
        animationStatus = ANIMATION_PLAY;
        animate();
      }
      int i = currentIteration;
      for (; i < maxIteration; i++) {
        PauseTransition pause = new PauseTransition(Duration.millis(animationDuration));
        pause.setOnFinished(e -> {
          animate();
        });
        if (animationStatus == ANIMATION_STOP || animationStatus == ANIMATION_PAUSE)
          return;
        sequentialTransition.getChildren().add(pause);
      }

      sequentialTransition.playFromStart();
    }
  }

  private synchronized void animate() {
    if (currentIteration >= maxIteration && animationStatus != ANIMATION_STOP)
      animationStatus = ANIMATION_PAUSE;
    if (animationStatus == ANIMATION_STOP || animationStatus == ANIMATION_PAUSE)
      return;

    next();
    /*
     * PauseTransition pause = new
     * PauseTransition(Duration.millis(animationDuration));
     * pause.setOnFinished(e -> {
     * animate();
     * });
     * sequentialTransition.getChildren().add(pause);
     */
  }

  private void next() {
    if (currentIteration < 0)
      currentIteration = 0;
    currentIteration++;
    if (currentIteration >= maxIteration) {
      currentIteration = maxIteration - 1;
      return;
    }
    progressSlider.setValue(currentIteration);
    updateDisplay();
  }

  private void previous() {
    if (currentIteration >= maxIteration)
      currentIteration = maxIteration - 1;
    currentIteration--;
    if (currentIteration < 0)
      return;
    updateDisplay();
  }

  private void pause() {
    isPaused = true;
    animationStatus = ANIMATION_PAUSE;
    sequentialTransition.stop();
  }

  private void stepBackward() {
    if (isPlaying) {
      if (!isPaused) {
        pause();
        previous();
        if (pauseControl == null) {
          pauseControl = new PauseTransition(Duration.millis(animationDuration));
          pauseControl.setOnFinished(e -> {
            play();
            pauseControl = null;
          });
          pauseControl.play();
        }
      } else {
        previous();
      }
    }
  }

  private void stepForward() {
    if (isPlaying) {
      if (!isPaused) {
        pause();
        next();
        if (pauseControl == null) {
          pauseControl = new PauseTransition(Duration.millis(animationDuration));
          pauseControl.setOnFinished(e -> {
            play();
            pauseControl = null;
          });
          pauseControl.play();
        }
      } else {
        next();
      }
    }
  }

  private void jumToIteration(int iteration) {
    pause();
    currentIteration = iteration;
    if (currentIteration >= maxIteration) {
      currentIteration = maxIteration - 1;
    }
    if (currentIteration < 0)
      currentIteration = 0;
    updateDisplay();
  }

  public void injectMainController(MainController mainController) {
    this.mainController = mainController;
  }

  private void updateDisplay() {
    progressSlider.setValue(currentIteration);
    if (currentIteration == maxIteration - 1) {

    }
    mainController.updateDisplay(steps.get(currentIteration));
  }

  private void goToBegin() {
    jumToIteration(0);
    progressSlider.setValue(currentIteration);
  }

  private void goToEnd() {
    jumToIteration(maxIteration - 1);
  }

}
