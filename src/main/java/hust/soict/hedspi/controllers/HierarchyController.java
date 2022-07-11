package hust.soict.hedspi.controllers;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hust.soict.hedspi.model.graph.Edge;
import hust.soict.hedspi.model.graph.Vertex;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class HierarchyController {
  static final Logger logger = LogManager.getLogger(HierarchyController.class);

  @FXML
  private VBox root;
  @FXML
  private Rectangle rect;

  private MainController mainController;
  private Map<TreeItem<String>, Vertex> vertexTree;
  private Map<TreeItem<String>, Edge> edgeTree;

  @FXML
  private void initialize() {
    rect.widthProperty().bind(root.widthProperty());

    Platform.runLater(() -> {
      mainController.graphDisableBtnProperty.addListener((ov, oldValue, newValue) -> {
        if (mainController.graph != null) {
          if (root.getChildren().size() >= 2) {
            vertexTree.clear();
            edgeTree.clear();
            root.getChildren().remove(1);
          }
          initTree();
        }
      });
    });
  }

  @SuppressWarnings("unchecked")
  private void initTree() {
    if (mainController.graph == null) {
      throw new IllegalStateException("Graph cannot be null when initTree() is called");
    }
    vertexTree = new HashMap<>();
    edgeTree = new HashMap<>();

    TreeView<String> treeView = new TreeView<>();
    TreeItem<String> rootItem = new TreeItem<>();
    rootItem.setExpanded(true);

    TreeItem<String> vertexItem = new TreeItem<>("Vertices");
    for (Vertex v : mainController.graph.vertexSet()) {
      ImageView vertexImg = new ImageView(getClass().getResource("/icon/vertex.png").toExternalForm());
      vertexImg.setFitWidth(20);
      vertexImg.setFitHeight(20);
      TreeItem<String> item = new TreeItem<>("Vertex " + v.getId());
      item.setGraphic(vertexImg);
      vertexItem.getChildren().add(item);
      vertexTree.put(item, v);
    }

    TreeItem<String> edgeItem = new TreeItem<>("Edges");
    for (Edge e : mainController.graph.edgeSet()) {
      TreeItem<String> item = new TreeItem<>("Edge " + e.getSource() + " -- " + e.getTarget());
      ImageView edgeImg = new ImageView(getClass().getResource("/icon/edge.png").toExternalForm());
      edgeImg.setFitWidth(20);
      edgeImg.setFitHeight(20);
      item.setGraphic(edgeImg);
      edgeItem.getChildren().add(item);
      edgeTree.put(item, e);

      TreeItem<String> vertexSourceItem = new TreeItem<>("Source: Vertex " + e.getSource().getId());
      TreeItem<String> vertexTargetItem = new TreeItem<>("Target: Vertex " + e.getTarget().getId());
      ImageView sourceImg = new ImageView(getClass().getResource("/icon/vertex.png").toExternalForm());
      sourceImg.setFitWidth(20);
      sourceImg.setFitHeight(20);
      ImageView targetImg = new ImageView(getClass().getResource("/icon/vertex.png").toExternalForm());
      targetImg.setFitWidth(20);
      targetImg.setFitHeight(20);
      vertexSourceItem.setGraphic(sourceImg);
      vertexTargetItem.setGraphic(targetImg);

      item.getChildren().addAll(vertexSourceItem, vertexTargetItem);
      vertexTree.put(vertexSourceItem, e.getSource());
      vertexTree.put(vertexTargetItem, e.getTarget());
      if (mainController.graph.getType().isWeighted()) {
        TreeItem<String> weightItem = new TreeItem<>("Weight: " + e.getWeight());
        item.getChildren().add(weightItem);
      }
    }

    rootItem.getChildren().addAll(vertexItem, edgeItem);
    treeView.setRoot(rootItem);
    treeView.setShowRoot(false);
    treeView.prefHeightProperty().bind(root.heightProperty().subtract(28));
    treeView.getSelectionModel().selectedItemProperty().addListener((ov, o, n) -> {
      if (!mainController.isPlaying()) {
        if (vertexTree.get(n) != null) {
          mainController.selectVertex(vertexTree.get(n));
        } else if (edgeTree.get(n) != null) {
          mainController.selectEdge(edgeTree.get(n));
        } else {
          mainController.selectEdge(null);
          mainController.selectVertex(null);
        }
      } else {
        mainController.selectEdge(null);
        mainController.selectVertex(null);
      }
    });
    root.getChildren().add(treeView);
  }

  public VBox getRoot() {
    return root;
  }

  public void injectMainController(MainController mainController) {
    this.mainController = mainController;
  }
}
