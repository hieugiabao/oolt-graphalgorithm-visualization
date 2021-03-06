package hust.soict.hedspi.model.graph;

import hust.soict.hedspi.annotation.LabelSource;

public class Vertex {
  private int id;

  public Vertex(int id) {
    this.id = id;
  }

  @LabelSource
  public int getId() {
    return id;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + id;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Vertex other = (Vertex) obj;
    if (id != other.id)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return id + "";
  }
}
