package hust.soict.hedspi.view;

import hust.soict.hedspi.model.graph.Edge;

public interface BaseEdgeView extends LabeledNode, StylableNode {
  public Edge getEdge();

  public Arrow getAttatchedArrow();

  public void attachArrow(Arrow arrow);
}
