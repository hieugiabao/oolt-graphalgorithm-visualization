package hust.soict.hedspi.view;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hust.soict.hedspi.annotation.LabelSource;
import hust.soict.hedspi.model.algo.step.State;
import hust.soict.hedspi.model.algo.step.State.EdgeState;
import hust.soict.hedspi.model.algo.step.State.VertexState;
import hust.soict.hedspi.model.graph.BaseGraph;
import hust.soict.hedspi.model.graph.DirectedEdge;
import hust.soict.hedspi.model.graph.Edge;
import hust.soict.hedspi.model.graph.Vertex;
import hust.soict.hedspi.utils.TypeUtil;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import static hust.soict.hedspi.utils.Utilities.*;

public class GraphPanel extends Pane {
  private final Logger logger = LogManager.getLogger(GraphPanel.class);

  private final BaseGraph<?> graph;
  private final Map<Vertex, VertexView> vertexNodes;
  private final Map<Edge, BaseEdgeView> edgeNodes;
  private final boolean edgesWithArrows;
  private final boolean edgesWithLabels;
  private final PlacementStrategy placementStrategy;

  private VertexView selectedVertex = null;
  private BaseEdgeView selectedEdge = null;

  private Consumer<VertexView> vertexClickConsumer = null;
  private Consumer<BaseEdgeView> edgeClickConsumer = null;

  private final BooleanProperty automaticLayout;
  private AnimationTimer timer;
  private boolean initialized = false;
  private State state;

  public GraphPanel(BaseGraph<? extends Edge> graph, PlacementStrategy placementStrategy, State state) {
    if (graph == null)
      throw new IllegalArgumentException("Graph cannot be null");
    this.graph = graph;
    this.edgesWithArrows = graph.getType().isDirected();
    this.edgesWithLabels = graph.getType().isWeighted();

    if (placementStrategy == null)
      this.placementStrategy = new RandomPlacementStrategy();
    else
      this.placementStrategy = placementStrategy;

    vertexNodes = new HashMap<>();
    edgeNodes = new HashMap<>();

    loadStylesheet();

    this.state = state == null ? createDefaultState() : state;

    initNodes();
    enableRightClickListener();

    timer = new AnimationTimer() {
      @Override
      public void handle(long now) {
        runLayoutIteration();
      }
    };

    this.automaticLayout = new SimpleBooleanProperty(true);
    this.automaticLayout.addListener((ov, oldvalue, newvalue) -> {
      if (newvalue) {
        timer.start();
      } else {
        timer.stop();
      }
    });
  }

  public GraphPanel(BaseGraph<? extends Edge> graph, PlacementStrategy placementStrategy) {
    this(graph, placementStrategy, null);
  }

  public GraphPanel(BaseGraph<?> graph) {
    this(graph, null, null);
  }

  public BooleanProperty automaticLayoutProperty() {
    return this.automaticLayout;
  }

  public void setAutomaticLayout(boolean value) {
    automaticLayout.set(value);
  }

  private void initNodes() {
    for (Vertex vertex : graph.vertexSet()) {
      VertexView vertexAnchor = new VertexView(vertex, 0, 0, 15, true);
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

        BaseEdgeView edgeView = createEdge(edge, oppositeVertexView, vertexView);
        addEdge(edgeView, edge);
        edgesToPlace.remove(edge);
      }
    }

    // add all vertex view
    for (Vertex vertex : vertexNodes.keySet()) {
      VertexView vertexView = vertexNodes.get(vertex);
      addVertex(vertexView);
    }

    updateState();
  }

  private synchronized void runLayoutIteration() {
    for (int i = 0; i < 10; i++) {
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
      boolean edgesOf = graph.edgesOf(v.getVertex()).size() > 0;
      for (VertexView other : vertexNodes.values()) {
        if (v == other)
          continue;
        boolean haveEdge = graph.edgesOf(other.getVertex()).size() > 0;
        if (!haveEdge) {
          continue;
        }
        Point2D repellingForce = repellingForce(v.getUpdatedPosition(), other.getUpdatedPosition(),
            edgesOf ? 80000 : 25000);

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
      if (!edgesOf) {
        Vertex find = vertexNodes.keySet().stream().filter(vertex -> {
          return !vertex.equals(v.getVertex()) && graph.edgesOf(vertex).size() > 0;
        }).findFirst().orElse(null);
        if (find != null) {
          Point2D attractiveForce = attractiveForce(v.getUpdatedPosition(), vertexNodes.get(find).getUpdatedPosition(),
              30, 8);
          v.addForceVector(attractiveForce.getX(), attractiveForce.getY());
        }
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
      String css = getClass().getResource("/css/graph.css").toExternalForm();
      getStylesheets().add(css);
      this.getStyleClass().add("graph");
    } catch (Exception e) {
      // TODO: handle exception
      logger.error("Error loading stylesheet", e);
    }
  }

  private void addEdge(BaseEdgeView ev, Edge e) {

    this.getChildren().add(0, (Node) ev);
    edgeNodes.put(e, ev);

    String labelText = generateEdgeLabel(e);

    Tooltip t = new Tooltip(labelText);
    Tooltip.install((Node) ev, t);

    if (this.edgesWithLabels) {
      Label label = new Label(labelText);
      label.getStyleClass().add("edge-label");
      this.getChildren().add(label);
      ev.attachLabel(label);
    }

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
      } else
        edgeView = new EdgeViewCurve(edge, vertexInView, vertexOutView);
    } else {
      // create line edge
      edgeView = new EdgeViewLine(edge, vertexInView, vertexOutView);
    }
    return edgeView;
  }

  private void addVertex(VertexView view) {
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
    } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
      logger.error("Error generating vertex label", ex);
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
    } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
      logger.error("Error generating edge label", ex);
    }

    return e != null ? e.toString() : "NULL";
  }

  public synchronized void init() {
    if (this.getScene() == null) {
      throw new IllegalStateException("You must call this method after the instance was added to a scene.");
    } else if (this.getWidth() == 0 || this.getHeight() == 0) {
      throw new IllegalStateException(
          "You must call this method after the instance was added to a scene and the scene was rendered.");
    }
    if (initialized == true) {
      throw new IllegalStateException("Already initialized.");
    }

    placementStrategy.place(widthProperty().doubleValue(), heightProperty().doubleValue(), graph, vertexNodes.values());
    timer.start();
    this.initialized = true;
  }

  private State createDefaultState() {
    List<Vertex> vertexList = new LinkedList<>(graph.vertexSet());
    List<Edge> edgeList = new LinkedList<>(graph.edgeSet());
    return new State(vertexList, edgeList);
  }

  private synchronized void updateNodes() {
    removeNodes();
    insertNodes();
  }

  private void insertNodes() {
    Collection<Vertex> unplottedVertices = unplottedVertices();
    List<VertexView> newVertices = null;

    Bounds bounds = getPlotBounds();
    double midX = bounds.getMinX() + bounds.getWidth() / 2;
    double midY = bounds.getMinY() + bounds.getHeight() / 2;

    if (!unplottedVertices.isEmpty()) {
      newVertices = new LinkedList<>();
      for (Vertex vertex : unplottedVertices) {
        double x, y;
        Set<Edge> adjSet = TypeUtil.uncheckedCast(graph.edgesOf(vertex));
        if (adjSet.isEmpty()) {
          x = midX;
          y = midY;
        } else {
          Edge firstEdge = adjSet.iterator().next();
          Vertex oppositeVertex = firstEdge.getOppositeVertex(vertex);
          VertexView existing = vertexNodes.get(oppositeVertex);
          if (existing == null) {
            x = midX;
            y = midY;
          } else {
            Point2D p = rotate(existing.getPosition().add(50.0, 50.0), existing.getPosition(), Math.random() * 360);
            x = p.getX();
            y = p.getY();
          }
        }

        VertexView newVertex = new VertexView(vertex, x, y, 15, true);
        newVertices.add(newVertex);
        vertexNodes.put(vertex, newVertex);
      }
    }

    Collection<Edge> unplottedEdges = unplottedEdges();
    if (!unplottedEdges.isEmpty()) {
      for (Edge edge : unplottedEdges) {
        VertexView source = vertexNodes.get(edge.getSource());
        VertexView target = vertexNodes.get(edge.getTarget());

        if (source == null || target == null)
          continue;

        source.addAdjacentVertex(target);
        target.addAdjacentVertex(source);

        BaseEdgeView graphEdge = createEdge(edge, source, target);
        addEdge(graphEdge, edge);
      }
    }

    if (newVertices != null) {
      newVertices.forEach(v -> addVertex(v));
    }
  }

  private void removeNodes() {
    Collection<Edge> removedEdges = removedEdges();
    removedEdges.forEach(edge -> {
      BaseEdgeView ev = edgeNodes.remove(edge);
      removeEdge(ev);

      if (getTotalEdgeBetween(edge.getSource(), edge.getTarget()) == 0) {
        VertexView v0 = vertexNodes.get(edge.getSource());
        VertexView v1 = vertexNodes.get(edge.getTarget());

        v1.removeAdjacentVertex(v0);
        v0.removeAdjacentVertex(v1);
      }
    });

    Collection<Vertex> removedVertices = removedVertices();
    removedVertices.forEach(vertex -> {
      VertexView vv = vertexNodes.remove(vertex);
      removeVertex(vv);
    });

  }

  private Collection<Edge> removedEdges() {
    List<Edge> removed = new LinkedList<>();

    Collection<BaseEdgeView> plotted = edgeNodes.values();
    plotted.forEach(e -> {
      if (!graph.edgeSet().contains(e.getEdge())) {
        removed.add(e.getEdge());
      }
    });
    return removed;
  }

  private Collection<Vertex> removedVertices() {
    List<Vertex> removed = new LinkedList<>();

    Collection<VertexView> plotted = vertexNodes.values();
    plotted.forEach(v -> {
      if (!graph.vertexSet().contains(v.getVertex()))
        removed.add(v.getVertex());
    });

    return removed;
  }

  private Collection<Vertex> unplottedVertices() {
    List<Vertex> unplotted = new LinkedList<>();
    graph.vertexSet().forEach(v -> {
      if (!vertexNodes.containsKey(v)) {
        unplotted.add(v);
      }
    });
    return unplotted;
  }

  private Collection<Edge> unplottedEdges() {
    List<Edge> unplotted = new LinkedList<>();
    graph.edgeSet().forEach(e -> {
      if (!edgeNodes.containsKey(e)) {
        unplotted.add(e);
      }
    });
    return unplotted;
  }

  private void removeEdge(BaseEdgeView e) {
    getChildren().remove((Node) e);

    Arrow attachedArrow = e.getAttatchedArrow();
    if (attachedArrow != null) {
      getChildren().remove(attachedArrow);
    }

    Text attachLabel = e.getAttachedLabel();
    if (attachLabel != null) {
      getChildren().remove(attachLabel);
    }
  }

  private void removeVertex(VertexView v) {
    getChildren().remove(v);

    Text attachedLabel = v.getAttachedLabel();
    if (attachedLabel != null) {
      getChildren().remove(attachedLabel);
    }
  }

  private int getTotalEdgeBetween(Vertex v, Vertex u) {
    int count = 0;
    for (Edge e : graph.edgeSet()) {
      if ((e.getTarget().equals(v) && e.getSource().equals(u))
          || (e.getTarget().equals(u) && e.getSource().equals(v))) {
        count++;
      }
    }
    return count;
  }

  private Bounds getPlotBounds() {
    double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE,
        maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;

    if (vertexNodes.size() == 0)
      return new BoundingBox(0, 0, getWidth(), getHeight());
    for (VertexView v : vertexNodes.values()) {
      minX = Math.min(minX, v.getCenterX());
      minY = Math.min(minY, v.getCenterY());
      maxX = Math.max(maxX, v.getCenterX());
      maxY = Math.max(maxY, v.getCenterY());
    }

    return new BoundingBox(minX, minY, maxX - minX, maxY - minY);
  }

  private void updateState() {
    Map<Vertex, VertexState> vertexStateMap = state.getVertexStateMap();
    Map<Edge, EdgeState> edgeStateMap = state.getEdgeStateMap();

    for (Vertex v : vertexStateMap.keySet()) {
      VertexView vv = vertexNodes.get(v);
      if (vv == null)
        continue;

      VertexState state = vertexStateMap.get(v);
      vv.setState(state);
    }

    for (Edge e : edgeStateMap.keySet()) {
      BaseEdgeView ev = edgeNodes.get(e);
      if (ev == null)
        continue;

      EdgeState state = edgeStateMap.get(e);
      ev.setState(state);
    }
  }

  public void update() {
    if (this.getScene() == null) {
      throw new IllegalStateException("You must call this method after the instance was added to a scene.");
    }

    if (!this.initialized) {
      throw new IllegalStateException("You must call init() before calling update().");
    }

    Platform.runLater(() -> {
      updateNodes();
    });
  }

  public void setState(State newState) {
    this.state = newState;
    updateState();
  }

  public void setVertexRightClickAction(Consumer<VertexView> action) {
    this.vertexClickConsumer = action;
  }

  public void setEdgeRightClickAction(Consumer<BaseEdgeView> action) {
    this.edgeClickConsumer = action;
  }

  private void enableRightClickListener() {
    setOnMouseClicked((ev) -> {
      if (ev.isSecondaryButtonDown()) {
        if (vertexClickConsumer == null || edgeClickConsumer == null)
          return;

        Node node = pick(this, ev.getX(), ev.getY());
        if (node == null)
          return;

        if (node instanceof VertexView) {
          VertexView v = (VertexView) node;
          vertexClickConsumer.accept(v);
        } else if (node instanceof BaseEdgeView) {
          BaseEdgeView e = (BaseEdgeView) node;
          edgeClickConsumer.accept(e);
        }
      } else if (ev.isPrimaryButtonDown()) {

      }
    });
  }

  public void selectVertex(Vertex v) {
    VertexView vertexView = vertexNodes.get(v);
    if (selectedVertex != null)
      selectedVertex.selected(false);

    if (vertexView == null || vertexView.equals(selectedVertex)) {
      selectedVertex = null;
      return;
    }
    selectedVertex = vertexView;
    selectedVertex.selected(true);
  }

  public void selectEdge(Edge e) {
    BaseEdgeView edgeView = edgeNodes.get(e);
    if (selectedEdge != null)
      selectedEdge.selected(false);
    if (edgeView == null || edgeView.equals(selectedEdge)) {
      selectedEdge = null;
      return;
    }

    selectedEdge = edgeView;
    selectedEdge.selected(true);
  }

  public BaseEdgeView getSelectedEdge() {
    return selectedEdge;
  }

  public VertexView getSelectedVertex() {
    return selectedVertex;
  }
}
