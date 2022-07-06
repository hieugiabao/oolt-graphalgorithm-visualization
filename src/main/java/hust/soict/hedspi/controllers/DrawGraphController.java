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

import hust.soict.hedspi.model.graph.BaseGraph;
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

  private boolean isDirected, isWeighted;
  private int id = 0;
  private BaseGraph<? extends Edge> graph;
  private Map<Vertex, VertexView> vertexViewMap = new LinkedHashMap<>();
  private Map<Edge, BaseEdgeView> edgeViewMap = new LinkedHashMap<>();
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

  public void init(boolean isDirected, boolean isWeighted) {
    // TODO Auto-generated method stub
    if (this.isDirected == isDirected && this.isWeighted == isWeighted) {
      if (graph == null)
        graph = isDirected ? new DirectedGraph(isWeighted) : new UndirectedGraph(isWeighted);
    } else {
      reset();
      this.isDirected = isDirected;
      this.isWeighted = isWeighted;
      graph = isDirected ? new DirectedGraph(isWeighted) : new UndirectedGraph(isWeighted);
    }

    line.endXProperty().bind(root.widthProperty());

    empty = new SimpleBooleanProperty(graph, "empty", true);
    BooleanBinding haveGraph = createBooleanBinding(() -> {
      if (graph.isEmpty()) {
        return true;
      } else {
        return false;
      }
    }, empty);
    doneButton.disableProperty().bind(haveGraph);

    canvas.setOnMousePressed(e -> {
      createVertex(e);
    });

    previousButton.onMouseClickedProperty().set(e -> {
      logger.debug("Previous button clicked");
      mainController.toInfoGraph();
    });

    cancelButton.onMouseClickedProperty().set(e -> {
      logger.debug("Cancel button clicked");
      this.graph = null;
      cancelButton.getScene().getWindow().hide();
    });

    doneButton.onMouseClickedProperty().set(e -> {
      logger.info("graph finished: {}, {}", graph.vertexSet(), graph.edgeSet());
      mainController.setGraph(graph);
      root.getScene().getWindow().hide();
    });
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

      VertexView vertexView = new VertexView(vertex, x, y, RADIUS, false);
      vertexViewMap.put(vertex, vertexView);
      Label label = new Label(vertex.getId() + "");
      vertexView.attachLabel(label);
      graph.addVertex(vertex);
      empty.set(false);

      canvas.getChildren().addAll(borderPane, vertexView, label);
      borderPaneToBack();

      logger.info("vertex added to graph: {}", vertex);
      vertexView.setOnMousePressed(ev -> {
        Circle v = (Circle) ev.getTarget();
        logger.info("vertex selected: {}", v);
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
        ev.consume();
      });
      vertexView.setOnMouseDragged(ev -> {
        drawLine(ev);
      });
      vertexView.setOnMouseReleased(ev -> {
        mouseReleased(ev);
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
    VertexView source = (VertexView) ev.getTarget();
    canvas.getChildren().removeAll(l, arrow);
    for (VertexView target : vertexViewMap.values()) {
      if (target.equals(source) && !isDirected) {
        continue;
      }
      Point2D point = new Point2D(ev.getX(), ev.getY());
      Point2D center = new Point2D(target.getCenterX(), target.getCenterY());
      if (checkInCircle(point, center, RADIUS)) {
        Edge edge = graph.addEdge(source.getVertex(), target.getVertex());
        if (edge == null) {
          return;
        }
        BaseEdgeView edgeView = createEdgeView(edge, source, target);
        edgeViewMap.put(edge, edgeView);
        canvas.getChildren().add((Node) edgeView);
        if (isWeighted) {
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
            newStage.show();

            weight = weightInfoController.getWeight();

            Label label = new Label(weight + "");
            edgeView.attachLabel(label);
            canvas.getChildren().add(label);
            weightInfoController.weightProperty.addListener((ov, oldValue, newValue) -> {
              label.setText(newValue + "");
              graph.setWeight(edge, (double) newValue);
            });
          } catch (IOException e) {
            // TODO: handle exception
            logger.error("Cant load weight info fxml: {}", e);
          }
        }

        logger.info("edge added to graph: {}", edge);
        ObservableList<Node> workingCollection = FXCollections.observableArrayList(
            canvas.getChildren());
        // day edge xuong cuoi
        Collections.sort(workingCollection, (n1, n2) -> {
          if (n1 instanceof BaseEdgeView && !(n2 instanceof BaseEdgeView))
            return -1;
          else if (!(n1 instanceof BaseEdgeView) && n2 instanceof BaseEdgeView) {
            return 1;
          } else
            return 0;
        });
        canvas.getChildren().setAll(workingCollection);
        break;
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
      ev.consume();
    });
    if (edgeView instanceof EdgeViewLine) {
      canvas.getChildren().add(borderPane);
      borderPaneToBack();
    }
    return edgeView;
  }

  private void reset() {
    id = 0;
    graph = null;
    vertexViewMap = new LinkedHashMap<>();
    edgeViewMap = new LinkedHashMap<>();
    canvas.getChildren().removeIf((node) -> true);
  }

  private void borderPaneToBack() {
    ObservableList<Node> workingCollection = FXCollections.observableArrayList(canvas.getChildren());
    // sort by BorderPane ascending
    Collections.sort(workingCollection, (n1, n2) -> {
      if (n1 instanceof BorderPane && !(n2 instanceof BorderPane))
        return -1;
      else if (n2 instanceof BorderPane && !(n1 instanceof BorderPane))
        return 1;
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
}
