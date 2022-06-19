package hust.soict.hedspi.model;

import hust.soict.hedspi.model.algo.Algorithm;

public class Context {
  private Algorithm alg;

  public void setAlgorithm(Algorithm alg) {
    this.alg = alg;
  }

  public void doExploration() {
    alg.explore();
  }
}
