package hust.soict.hedspi.controllers;

import static hust.soict.hedspi.utils.Utilities.atan2;
import static hust.soict.hedspi.utils.Utilities.checkInCircle;
import static hust.soict.hedspi.utils.Utilities.toDegrees;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static javafx.beans.binding.Bindings.createBooleanBinding;

import hust.soict.hedspi.model.graph.DirectedEdge;
import hust.soict.hedspi.model.graph.DirectedGraph;
import hust.soict.hedspi.model.graph.Edge;
import hust.soict.hedspi.model.graph.UndirectedGraph;
import hust.soict.hedspi.model.graph.Vertex;
import hust.soict.hedspi.view.Arrow;
import hust.soict.hedspi.view.BaseEdgeView;
import hust.soict.hedspi.view.EdgeViewCurve;
import hust.soict.hedspi.view.EdgeViewLine;
import hust.soict.hedspi.view.Label;
import hust.soict.hedspi.view.VertexView;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DrawGraphController {
  private final double RADIUS = 15;
  private final Logger logger = LogManager.getLogger(DrawGraphController.class);

  private boolean isDirected = false, isWeighted = false;
  private int id = 0;
  private Map<Vertex, VertexView> vertexViewMap = new LinkedHashMap<>();
  private Map<Edge, BaseEdgeView> edgeViewMap = new LinkedHashMap<>();
  private Map<Vertex, BorderPane> paneVertex = new LinkedHashMap<>();
  private Map<Edge, BorderPane> paneEdge = new LinkedHashMap<>();
  private BooleanProperty empty;
  private Line l;
  private Arrow arrow;
  private CreateGraphController mainController;

  @FXML
  private Line line;
  @FXML
  private AnchorPane root;
  @FXML
  private Button previousButton, cancelButton, doneButton;
  @FXML
  private Pane canvas;

  private Pane nanoPane = new Pane();
  private ContextMenu contextMenu = new ContextMenu();
  private MenuItem deleteItem = new MenuItem("Delete");
  private MenuItem changeItem = new MenuItem("Change weight");

  public void init() {
    // TODO Auto-generated method stub
    if (mainController.isDirected == this.isDirected && mainController.isWeighted == this.isWeighted) {
      if (mainController.graph == null)
        mainController.graph = isDirected ? new DirectedGraph(isWeighted) : new UndirectedGraph(isWeighted);
    } else {
      reset();
      this.isDirected = mainController.isDirected;
      this.isWeighted = mainController.isWeighted;
      mainController.graph = isDirected ? new DirectedGraph(isWeighted) : new UndirectedGraph(isWeighted);
    }

    line.endXProperty().bind(root.widthProperty());

    empty = new SimpleBooleanProperty(mainController.graph, "empty", true);
    BooleanBinding haveGraph = createBooleanBinding(() -> {
      if (mainController.graph.isEmpty()) {
        return true;
      } else {
        return false;
      }
    }, empty);
    doneButton.disableProperty().bind(haveGraph);

    canvas.setOnMousePressed(e -> {
      createVertex(e);
    });

    previousButton.onMouseClickedProperty().set(e ->

    {
      logger.debug("Previous button clicked");
      mainController.toInfoGraph();
    });

    cancelButton.onMouseClickedProperty().set(e -> {
      logger.debug("Cancel button clicked");
      mainController.graph = null;
      cancelButton.getScene().getWindow().hide();
    });

    doneButton.onMouseClickedProperty().set(e -> {
      logger.info("graph finished: {}, {}", mainController.graph.vertexSet(), mainController.graph.edgeSet());
      root.getScene().getWindow().hide();
    });

    setupContextMenu();
  }

  private void createVertex(MouseEvent e) {
    if (e.isPrimaryButtonDown()) {
      Vertex vertex = new Vertex(id);
      double x, y;
      if (e.getX() < RADIUS)
        x = RADIUS;
      else if (e.getX() > canvas.getWidth() - RADIUS)
        x = canvas.getWidth() - RADIUS;
      else
        x = e.getX();
      if (e.getY() < RADIUS)
        y = RADIUS;
      else if (e.getY() > canvas.getHeight() - RADIUS)
        y = canvas.getHeight() - RADIUS;
      else
        y = e.getY();
      BorderPane borderPane = new BorderPane();
      borderPane.setLayoutX(x - RADIUS * 2.5);
      borderPane.setLayoutY(y - RADIUS * 2.5);
      borderPane.setPrefSize(RADIUS * 5, RADIUS * 5);
      paneVertex.put(vertex, borderPane);

      VertexView vertexView = new VertexView(vertex, x, y, RADIUS, false);
      vertexViewMap.put(vertex, vertexView);
      Label label = new Label(vertex.getId() + "");
      vertexView.attachLabel(label);
      mainController.graph.addVertex(vertex);
      empty.set(false);

      canvas.getChildren().addAll(borderPane, vertexView, label);
      moveComponent(BorderPane.class, false);

      logger.info("vertex added to graph: {}", vertex);
      vertexView.setOnMousePressed(ev -> {
        contextMenu.hide();
        Circle v = (Circle) ev.getTarget();
        logger.info("vertex selected: {}", v);
        if (ev.isPrimaryButtonDown()) {
          l = new Line();
          l.setStrokeWidth(1.5);
          l.setLayoutX(0);
          l.setLayoutY(0);
          l.setStartX(v.getCenterX());
          l.setStartY(v.getCenterY());
          l.setEndX(l.getStartX());
          l.setEndY(l.getStartY());
          canvas.getChildren().add(l);
          if (isDirected) {
            arrow = new Arrow(5);
            arrow.translateXProperty().bind(l.endXProperty());
            arrow.translateYProperty().bind(l.endYProperty());
            Rotate rotation = new Rotate();
            rotation.pivotXProperty().bind(l.translateXProperty());
            rotation.pivotYProperty().bind(l.translateYProperty());
            rotation.angleProperty().bind(
                toDegrees(atan2(l.endYProperty().subtract(l.startYProperty()),
                    l.endXProperty().subtract(l.startXProperty()))));
            arrow.getTransforms().add(rotation);
            canvas.getChildren().add(arrow);
          }
        }
        ev.consume();
      });
      vertexView.setOnMouseDragged(ev -> {
        drawLine(ev);
      });
      vertexView.setOnMouseReleased(this::mouseReleased);
      vertexView.setOnContextMenuRequested(ev -> {
        contextMenu.show(vertexView, ev.getScreenX(), ev.getScreenY());
      });
      label.setOnMousePressed(ev -> {
        ev.consume();
      });
      borderPane.setOnMousePressed(ev -> {
        ev.consume();
      });
      id++;
    }
  }

  private void drawLine(MouseEvent ev) {
    l.setEndX(ev.getX());
    l.setEndY(ev.getY());
  }

  private void mouseReleased(MouseEvent ev) {
    if (ev.getButton() == MouseButton.PRIMARY) {
      VertexView source = (VertexView) ev.getTarget();
      canvas.getChildren().removeAll(l, arrow);
      for (VertexView target : vertexViewMap.values()) {
        if (target.equals(source) && !isDirected) {
          continue;
        }
        Point2D point = new Point2D(ev.getX(), ev.getY());
        Point2D center = new Point2D(target.getCenterX(), target.getCenterY());
        if (checkInCircle(point, center, RADIUS)) {
          Edge edge = mainController.graph.addEdge(source.getVertex(), target.getVertex());
          if (edge == null) {
            return;
          }
          BaseEdgeView edgeView = createEdgeView(edge, source, target);
          edgeViewMap.put(edge, edgeView);
          canvas.getChildren().add((Node) edgeView);
          if (isWeighted) {
            double weight = getWeight();
            mainController.graph.setWeight(edge, weight);

            Label label = new Label(weight + "");
            edgeView.attachLabel(label);
            canvas.getChildren().add(label);
          }

          logger.info("edge added to graph: {}", edge);
          moveComponent(BaseEdgeView.class, false);
          moveComponent(BorderPane.class, false);
          break;
        }
      }
    }
  }

  private BaseEdgeView createEdgeView(Edge edge, VertexView source, VertexView target) {
    BaseEdgeView edgeView;

    if (isDirected) {
      if (source.getVertex().equals(target.getVertex())) {
        edgeView = new EdgeViewCurve(edge, source, target);
      } else {
        Edge reverseEdge = new DirectedEdge(target.getVertex(), source.getVertex());
        if (edgeViewMap.containsKey(reverseEdge)) {
          BaseEdgeView preEdgeView = edgeViewMap.get(reverseEdge);
          canvas.getChildren().removeAll((Node) preEdgeView, preEdgeView.getAttachedLabel(),
              preEdgeView.getAttatchedArrow());
          BaseEdgeView reverseEdgeView = new EdgeViewCurve(reverseEdge, target, source);
          canvas.getChildren().add((Node) reverseEdgeView);
          Arrow arrow = new Arrow(5);
          reverseEdgeView.attachArrow(arrow);
          canvas.getChildren().add(arrow);
          if (isWeighted) {
            Label label = new Label(edge.getWeight() + "");
            reverseEdgeView.attachLabel(label);
            canvas.getChildren().add(label);
          }
          edgeView = new EdgeViewCurve(edge, source, target, true);
        } else {
          edgeView = new EdgeViewLine(edge, source, target);
        }
      }
      Arrow arrow = new Arrow(5);
      edgeView.attachArrow(arrow);
      canvas.getChildren().add(arrow);
    } else {
      edgeView = new EdgeViewLine(edge, source, target);
    }

    if (edgeView instanceof EdgeViewLine) {
      BorderPane borderPane = new BorderPane();
      borderPane.setLayoutX(source.getCenterX());
      borderPane.setLayoutY(source.getCenterY() - RADIUS);
      Point2D start = new Point2D(source.getCenterX(), source.getCenterY());
      Point2D end = new Point2D(target.getCenterX(), target.getCenterY());
      borderPane.setPrefSize(end.distance(start), RADIUS * 2);
      Rotate rotate = new Rotate();
      rotate.setAngle(Math.toDegrees(Math.atan2(end.getY() - start.getY(), end.getX() - start.getX())));
      borderPane.getTransforms().add(rotate);
      borderPane.setOnMousePressed(ev -> {
        logger.info("consume");
        contextMenu.hide();
        ev.consume();
      });
      canvas.getChildren().add(borderPane);
      paneEdge.put(edge, borderPane);
      moveComponent(BorderPane.class, false);
    }
    ((Node) edgeView).setOnContextMenuRequested(ev -> {
      contextMenu.show((Node) edgeView, ev.getScreenX(), ev.getScreenY());
    });
    return edgeView;
  }

  private void reset() {
    id = 0;
    mainController.graph = null;
    vertexViewMap = new LinkedHashMap<>();
    edgeViewMap = new LinkedHashMap<>();
    paneVertex = new LinkedHashMap<>();
    paneEdge = new LinkedHashMap<>();
    canvas.getChildren().clear();
  }

  private void moveComponent(Class<?> clazz, boolean up) {
    ObservableList<Node> workingCollection = FXCollections.observableArrayList(canvas.getChildren());
    // sort by BorderPane ascending
    Collections.sort(workingCollection, (n1, n2) -> {
      if (clazz.isInstance(n1) && !clazz.isInstance(n2))
        return up ? 1 : -1;
      else if (!clazz.isInstance(n1) && clazz.isInstance(n2))
        return up ? -1 : 1;
      return 0;
    });
    canvas.getChildren().setAll(workingCollection);
  }

  public void show() {
    root.setVisible(true);

    Platform.runLater(() -> {
      StackPane parent = (StackPane) root.getParent();
      parent.getScene().getWindow().setWidth(800);
      parent.getScene().getWindow().setHeight(600);
      parent.getScene().getWindow().centerOnScreen();
    });
  }

  public void hide() {
    root.setVisible(false);
  }

  public AnchorPane getRoot() {
    return root;
  }

  public void injectMainController(CreateGraphController controller) {
    this.mainController = controller;
  }

  private void setupContextMenu() {
    ImageView deleteView = new ImageView(getClass().getResource("/icon/delete.png").toExternalForm());
    deleteView.setFitHeight(10);
    deleteView.setFitWidth(10);
    deleteItem.setGraphic(deleteView);
    ImageView changeWeightView = new ImageView(getClass().getResource("/icon/change.png").toExternalForm());
    changeWeightView.setFitHeight(10);
    changeWeightView.setFitWidth(10);
    changeItem.setGraphic(changeWeightView);
    contextMenu.getItems().add(deleteItem);
    contextMenu.setAutoHide(false);
    nanoPane.prefHeight(40);
    nanoPane.prefWidth(40);
    nanoPane.prefHeightProperty().bind(canvas.heightProperty());
    nanoPane.prefWidthProperty().bind(canvas.widthProperty());
    nanoPane.setOnMousePressed(e -> {
      contextMenu.hide();
      e.consume();
      canvas.getChildren().remove(nanoPane);
    });

    contextMenu.setOnHiding(e -> {
      Node owner = contextMenu.getOwnerNode();
      if (owner instanceof VertexView) {
        VertexView vertexView = (VertexView) owner;
        vertexView.selected(false);
        logger.info("Un hi :{}", owner);
      } else if (owner instanceof BaseEdgeView) {
        BaseEdgeView edgeView = (BaseEdgeView) owner;
        edgeView.selected(false);
      }
    });
    contextMenu.setOnShowing(e -> {
      Node owner = contextMenu.getOwnerNode();
      if (owner instanceof VertexView) {
        VertexView vertexView = (VertexView) owner;
        vertexView.selected(true);
        contextMenu.getItems().remove(changeItem);
      } else if (owner instanceof BaseEdgeView) {
        if (!contextMenu.getItems().contains(changeItem) && isWeighted) {
          contextMenu.getItems().add(changeItem);
        }
        BaseEdgeView edgeView = (BaseEdgeView) owner;
        edgeView.selected(true);
      }
      canvas.getChildren().add(nanoPane);
    });

    deleteItem.setOnAction(e -> {
      Node owner = contextMenu.getOwnerNode();
      if (owner instanceof VertexView) {
        VertexView vertexView = (VertexView) owner;
        // mainController.deleteVertex(vertexView.getVertex());
        deleteVertex(vertexView.getVertex());
      } else if (owner instanceof BaseEdgeView) {
        BaseEdgeView edgeView = (BaseEdgeView) owner;
        // mainController.deleteEdge(edgeView.getEdge());
        deleteEdge(edgeView.getEdge());
      }
      contextMenu.hide();
      e.consume();
    });

    changeItem.setOnAction(e -> {
      contextMenu.hide();
      Node owner = contextMenu.getOwnerNode();
      if (isWeighted && owner instanceof BaseEdgeView) {
        BaseEdgeView edgeView = (BaseEdgeView) owner;
        double weight = getWeight();
        mainController.graph.setWeight(edgeView.getEdge(), weight);
        edgeView.getAttachedLabel().setText(String.valueOf(weight));
      }
      e.consume();
    });
  }

  private void deleteEdge(Edge edge) {
    if (mainController.graph.removeEdge(edge)) {
      BaseEdgeView edgeView = edgeViewMap.get(edge);
      edgeViewMap.remove(edge);
      Label label = edgeView.getAttachedLabel();
      if (label != null) {
        canvas.getChildren().remove(label);
      }
      Arrow arrow = edgeView.getAttatchedArrow();
      if (arrow != null) {
        canvas.getChildren().remove(arrow);
      }
      BorderPane borderPane = paneEdge.get(edge);
      if (borderPane != null) {
        canvas.getChildren().remove(borderPane);
      }
      canvas.getChildren().remove((Node) edgeView);
      paneEdge.remove(edge);
    }
  }

  private void deleteVertex(Vertex vertex) {
    mainController.graph.edgesOf(vertex).forEach(e -> {
      deleteEdge(e);
    });
    if (mainController.graph.removeVertex(vertex)) {
      VertexView vertexView = vertexViewMap.get(vertex);
      Label label = vertexView.getAttachedLabel();
      if (label != null) {
        canvas.getChildren().remove(label);
      }
      vertexViewMap.remove(vertex);
      BorderPane borderPane = paneVertex.get(vertex);
      if (borderPane != null) {
        canvas.getChildren().remove(borderPane);
      }
      canvas.getChildren().remove((Node) vertexView);
      paneVertex.remove(vertex);
    }
  }

  private double getWeight() {
    try {
      double weight;
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/weight-info.fxml"));
      Parent parent = loader.load();
      WeightInfoController weightInfoController = loader.getController();

      Scene newScene = new Scene(parent);
      Stage newStage = new Stage(StageStyle.DECORATED);
      newStage.initModality(Modality.APPLICATION_MODAL);
      newStage.initOwner(root.getScene().getWindow());
      newStage.setTitle("Get weight");
      newStage.setScene(newScene);
      newStage.setResizable(false);
      newStage.showAndWait();

      weight = weightInfoController.getWeight();

      return weight;
    } catch (IOException e) {
      // TODO: handle exception
      logger.error("Cant load weight info fxml: {}", e);
      return 0;
    }
  }
}
