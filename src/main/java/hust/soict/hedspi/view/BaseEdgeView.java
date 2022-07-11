package hust.soict.hedspi.view;

import hust.soict.hedspi.model.algo.step.State;
import hust.soict.hedspi.model.graph.Edge;

public interface BaseEdgeView extends LabeledNode, StylableNode {
  public Edge getEdge();

  public Arrow getAttatchedArrow();

  public void attachArrow(Arrow arrow);

  public Label getAttachedLabel();

  public void setState(State.EdgeState state);

  public void selected(boolean b);
}
