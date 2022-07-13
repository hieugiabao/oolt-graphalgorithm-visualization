package hust.soict.hedspi.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static javafx.beans.binding.Bindings.createBooleanBinding;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;

public class GraphInfoController implements Initializable {

  private static final Logger logger = LogManager.getLogger(GraphInfoController.class);

  private String name = null;
  private BooleanBinding nameFieldValid, diretionValid, weightValid;
  private CreateGraphController mainController;

  @FXML
  private AnchorPane root;
  @FXML
  private Line upLine, downLine;
  @FXML
  private RadioButton directedButton, undirectedButton, unweightedButton, weightedButton;
  @FXML
  private Button nextButton, cancelButton;
  @FXML
  private TextField nameField;

  @Override
  public void initialize(URL uri, ResourceBundle resourceBundle) {
    logger.info("load url: {}", uri);
    upLine.endXProperty().bind(root.widthProperty());
    downLine.endXProperty().bind(root.widthProperty());

    directedButton.setSelected(false);
    undirectedButton.setSelected(false);
    unweightedButton.setSelected(false);
    weightedButton.setSelected(false);

    nameFieldValid = createBooleanBinding(() -> {
      if (nameField.getText() == null || nameField.getText().length() == 0) {
        return false;
      } else {
        return true;
      }
    }, nameField.textProperty());
    diretionValid = createBooleanBinding(() -> {
      if (directedButton.isSelected() || undirectedButton.isSelected()) {
        return true;
      } else {
        return false;
      }
    }, directedButton.selectedProperty(), undirectedButton.selectedProperty());
    weightValid = createBooleanBinding(() -> {
      if (weightedButton.isSelected() || unweightedButton.isSelected()) {
        return true;
      } else {
        return false;
      }
    }, weightedButton.selectedProperty(), unweightedButton.selectedProperty());
    nextButton.disableProperty().bind(nameFieldValid.not().or(diretionValid.not()).or(weightValid.not()));

    directedButton.setOnAction(e -> {
      mainController.isDirected = true;
      // logger.debug("Press directed button: " + directed);
    });
    undirectedButton.setOnAction(e -> {
      mainController.isDirected = false;
      // logger.debug("Press undirected button: " + undirected);
    });
    unweightedButton.setOnAction(e -> {
      mainController.isWeighted = false;
      // logger.debug("Press unweighted button: " + unweighted);
    });
    weightedButton.setOnAction(e -> {
      mainController.isWeighted = true;
      // logger.debug("Press weighted button: " + weighted);
    });
    nameField.textProperty().addListener((observable, oldValue, newValue) -> {
      name = newValue;
      // logger.debug("Change name: " + name);
    });

    cancelButton.onMouseClickedProperty().set(e -> {
      logger.debug("Cancel button clicked");
      mainController.graph = null;
      cancelButton.getScene().getWindow().hide();
    });

    nextButton.onMouseClickedProperty().set(e -> {
      mainController.toDrawGraph();
    });
  }

  public void show() {
    root.setVisible(true);
    Platform.runLater(() -> {
      StackPane parent = (StackPane) root.getParent();
      parent.getScene().getWindow().setWidth(450);
      parent.getScene().getWindow().setHeight(473);
      parent.getScene().getWindow().centerOnScreen();
    });
  }

  public void hide() {
    root.setVisible(false);
  }

  public void injectMainController(CreateGraphController controller) {
    mainController = controller;
  }

  public AnchorPane getRoot() {
    return root;
  }
}
