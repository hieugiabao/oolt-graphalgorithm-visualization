package hust.soict.hedspi.utils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class UnionFind<T> {

  private final Map<T, T> parentMap;
  private final Map<T, Integer> rankMap;
  private int count;

  public UnionFind(Set<T> elements) {
    parentMap = new LinkedHashMap<>();
    rankMap = new HashMap<>();
    for (T element : elements) {
      parentMap.put(element, element);
      rankMap.put(element, 0);
    }
    count = elements.size();
  }

  public void addElement(T element) {
    if (parentMap.containsKey(element)) {
      throw new IllegalArgumentException("Element is already contained in UnionFind: " + element);
    }
    parentMap.put(element, element);
    rankMap.put(element, 0);
    count++;
  }

  protected Map<T, T> getParentMap() {
    return parentMap;
  }

  protected Map<T, Integer> getRankMap() {
    return rankMap;
  }

  public T find(final T element) {
    if (!parentMap.containsKey(element)) {
      throw new IllegalArgumentException("Element is not contained in UnionFind: " + element);
    }

    T current = element;
    while (true) {
      T parent = parentMap.get(current);
      if (parent.equals(current)) {
        break;
      }
      current = parent;
    }
    final T root = current;

    current = element;
    while (!current.equals(root)) {
      T parent = parentMap.get(current);
      parentMap.put(current, root);
      current = parent;
    }

    return root;
  }

  public void union(T element1, T element2) {
    if (!parentMap.containsKey(element1) || !parentMap.containsKey(element2)) {
      throw new IllegalArgumentException("elements must be contained in set");
    }

    T parent1 = find(element1);
    T parent2 = find(element2);

    if (parent1.equals(parent2)) {
      return;
    }

    int rank1 = rankMap.get(parent1);
    int rank2 = rankMap.get(parent2);
    if (rank1 > rank2) {
      parentMap.put(parent2, parent1);
    } else if (rank1 < rank2) {
      parentMap.put(parent1, parent2);
    } else {
      parentMap.put(parent2, parent1);
      rankMap.put(parent1, rank1 + 1);
    }
    count--;
  }

  public boolean isSameSet(T element1, T element2) {
    return find(element1).equals(find(element2));
  }

  public void reset() {
    for (T element : parentMap.keySet()) {
      parentMap.put(element, element);
      rankMap.put(element, 0);
    }
    count = parentMap.size();
  }

}