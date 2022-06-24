package hust.soict.hedspi.view;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import hust.soict.hedspi.annotation.LabelSource;
import hust.soict.hedspi.model.graph.BaseGraph;
import hust.soict.hedspi.model.graph.DirectedEdge;
import hust.soict.hedspi.model.graph.Edge;
import hust.soict.hedspi.model.graph.Vertex;
import hust.soict.hedspi.utils.TypeUtil;
import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import static hust.soict.hedspi.utils.Utilities.*;

public class GraphPanel extends Pane {
  private final BaseGraph<?> graph;
  private final Map<Vertex, VertexView> vertexNodes;
  private final Map<Edge, BaseEdgeView> edgeNodes;
  private final boolean edgesWithArrows;

  private AnimationTimer timer;
  private boolean initialized = false;

  public GraphPanel(BaseGraph<?> graph) {
    if (graph == null)
      throw new IllegalArgumentException("Graph cannot be null");
    this.graph = graph;
    this.edgesWithArrows = graph.getType().isDirected();

    vertexNodes = new HashMap<>();
    edgeNodes = new HashMap<>();

    loadStylesheet();

    initNodes();

    timer = new AnimationTimer() {
      @Override
      public void handle(long now) {
        runLayoutIteration();
      }
    };
  }

  private void initNodes() {
    for (Vertex vertex : graph.vertexSet()) {
      VertexView vertexAnchor = new VertexView(vertex, 0, 0, 15);
      vertexNodes.put(vertex, vertexAnchor);
    }

    List<Edge> edgesToPlace = new LinkedList<>(graph.edgeSet());

    for (Vertex vertex : vertexNodes.keySet()) {
      Set<Edge> incomingEdges = TypeUtil.uncheckedCast(graph.incomingEdgesOf(vertex));
      for (Edge edge : incomingEdges) {
        if (!edgesToPlace.contains(edge))
          continue;

        Vertex oppositeVertex = edge.getOppositeVertex(vertex);
        VertexView oppositeVertexView = vertexNodes.get(oppositeVertex);
        VertexView vertexView = vertexNodes.get(vertex);

        vertexView.addAdjacentVertex(oppositeVertexView);
        oppositeVertexView.addAdjacentVertex(vertexView);

        BaseEdgeView edgeView = createEdge(edge, vertexView, oppositeVertexView);
        addEdge(edgeView, edge);

        edgesToPlace.remove(edge);
      }
    }

    // add all vertex view
    for (Vertex vertex : vertexNodes.keySet()) {
      VertexView vertexView = vertexNodes.get(vertex);
      addVertex(vertexView);
    }
  }

  private synchronized void runLayoutIteration() {
    for (int i = 0; i < 30; i++) {
      resetForces();
      computeForces();
      updateForces();
    }
    applyForces();
  }

  private void applyForces() {
    vertexNodes.values().forEach((v) -> {
      v.moveFromForces();
    });
  }

  private void updateForces() {
    vertexNodes.values().forEach((v) -> {
      v.updateDelta();
    });
  }

  private void computeForces() {
    for (VertexView v : vertexNodes.values()) {
      for (VertexView other : vertexNodes.values()) {
        if (v == other)
          continue;

        Point2D repellingForce = repellingForce(v.getUpdatedPosition(), other.getUpdatedPosition(), 100000);

        double deltaForceX = 0, deltaForceY = 0;

        if (v.isAdjacentVertex(other)) {
          Point2D attractiveForce = attractiveForce(v.getUpdatedPosition(), other.getUpdatedPosition(), 30, 10);
          deltaForceX = attractiveForce.getX() + repellingForce.getX();
          deltaForceY = attractiveForce.getY() + repellingForce.getY();
        } else {
          deltaForceX = repellingForce.getX();
          deltaForceY = repellingForce.getY();
        }
        v.addForceVector(deltaForceX, deltaForceY);
      }
    }
  }

  private void resetForces() {
    vertexNodes.values().forEach((v) -> {
      v.resetForces();
    });
  }

  private void loadStylesheet() {
    try {
      String css = getClass().getResource("/css/app.css").toExternalForm();
      getStylesheets().add(css);
      this.getStyleClass().add("graph");
    } catch (Exception e) {
      // TODO: handle exception
      System.out.println(e.getMessage());
    }
  }

  public void addEdge(BaseEdgeView ev, Edge e) {

    this.getChildren().add(0, (Node) ev);
    edgeNodes.put(e, ev);

    String labelText = generateEdgeLabel(e);

    Tooltip t = new Tooltip(labelText);
    Tooltip.install((Node) ev, t);

    Label label = new Label(labelText);
    label.getStyleClass().add("edge-label");
    this.getChildren().add(label);
    ev.attachLabel(label);

    if (this.edgesWithArrows) {
      Arrow arrow = new Arrow(5);
      ev.attachArrow(arrow);
      this.getChildren().add(arrow);
    }
  }

  private BaseEdgeView createEdge(Edge edge, VertexView vertexInView, VertexView vertexOutView) {
    BaseEdgeView edgeView;
    // TODO: create edge view base on source vertex and target vertex
    if ((this.graph.getType().isDirected() && graph.containsEdge(edge.getTarget(), edge.getSource()))
        || edge.getSource().equals(edge.getTarget())) {
      // create curve edge
      if (edgeNodes.get(new DirectedEdge(edge.getTarget(), edge.getSource())) != null) {
        edgeView = new EdgeViewCurve(edge, vertexInView, vertexOutView, true);
        // System.out.println("co");
      } else
        edgeView = new EdgeViewCurve(edge, vertexInView, vertexOutView);
    } else {
      // create line edge
      edgeView = new EdgeViewLine(edge, vertexInView, vertexOutView);
    }
    return edgeView;
  }

  public void addVertex(VertexView view) {
    this.getChildren().add(view);

    String labelText = generateVertexLabel(view.getVertex());
    Tooltip t = new Tooltip(labelText);
    Tooltip.install(view, t);

    Label label = new Label(labelText);
    label.getStyleClass().add("vertex-label");
    view.attachLabel(label);
    this.getChildren().add(label);
  }

  private String generateVertexLabel(Vertex v) {
    try {
      Class<?> clazz = v.getClass();
      for (Method method : clazz.getDeclaredMethods()) {
        if (method.isAnnotationPresent(LabelSource.class)) {
          method.setAccessible(true);
          Object value = method.invoke(v);
          return value.toString();
        }
      }
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    }

    return v != null ? v.toString() : "NULL";
  }

  private String generateEdgeLabel(Edge e) {
    try {
      Class<?> clazz = e.getClass();
      for (Method method : clazz.getDeclaredMethods()) {
        if (method.isAnnotationPresent(LabelSource.class)) {
          method.setAccessible(true);
          Object value = method.invoke(e);
          return value.toString();
        }
      }
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    }

    return e != null ? e.toString() : "NULL";
  }

  private void place() {
    double height = this.widthProperty().doubleValue();
    double width = this.heightProperty().doubleValue();

    Random rand = new Random();
    for (VertexView v : vertexNodes.values()) {
      double x = rand.nextDouble() * width;
      double y = rand.nextDouble() * height;
      v.setPosition(x, y);
    }
  }

  public void init() {
    if (this.getScene() == null) {
      throw new IllegalStateException("You must call this method after the instance was added to a scene.");
    } else if (this.getWidth() == 0 || this.getHeight() == 0) {
      throw new IllegalStateException(
          "You must call this method after the instance was added to a scene and the scene was rendered.");
    }
    if (initialized == true) {
      throw new IllegalStateException("Already initialized.");
    }

    place();
    timer.start();
    this.initialized = true;
  }
}
