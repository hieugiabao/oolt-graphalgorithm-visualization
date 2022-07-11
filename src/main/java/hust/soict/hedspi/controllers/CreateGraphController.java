package hust.soict.hedspi.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hust.soict.hedspi.model.graph.BaseGraph;
import hust.soict.hedspi.model.graph.Edge;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;

public class CreateGraphController implements Initializable {
  private static final Logger logger = LogManager.getLogger(CreateGraphController.class);

  @FXML
  private StackPane root;
  @FXML
  private GraphInfoController infoController;
  @FXML
  private DrawGraphController drawController;

  BaseGraph<? extends Edge> graph;
  boolean isDirected = false, isWeighted = false;

  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {
    infoController.injectMainController(this);
    drawController.injectMainController(this);
    infoController.show();
    drawController.hide();
    infoController.getRoot().prefHeightProperty().bind(root.prefHeightProperty());
    infoController.getRoot().prefWidthProperty().bind(root.prefWidthProperty());
    drawController.getRoot().prefHeightProperty().bind(root.prefHeightProperty());
    drawController.getRoot().prefWidthProperty().bind(root.prefWidthProperty());

    Platform.runLater(() -> {
      root.prefHeightProperty().bind(root.getScene().heightProperty().divide(1));
      root.prefWidthProperty().bind(root.getScene().widthProperty().divide(1));
    });
  }

  public void toInfoGraph() {
    drawController.hide();
    infoController.show();
  }

  public void toDrawGraph() {
    infoController.hide();
    drawController.show();
    drawController.init();
  }

  public StackPane getRoot() {
    return root;
  }

  public BaseGraph<? extends Edge> getGraph() {
    return graph;
  }
}
