package hust.soict.hedspi.utils;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.NoSuchElementException;

public class FibonacciHeap<K, V> implements Serializable, Cloneable {
  private static final long serialVersionUID = 2312314213123142L;
  private static final int AUX_CONSOLIDATE_ARRAY_SIZE = 100;

  private Node<K, V> minRoot;
  private int roots;
  private long size;

  private Node<K, V>[] aux;
  protected FibonacciHeap<K, V> other;

  @SuppressWarnings("unchecked")
  public FibonacciHeap() {
    this.minRoot = null;
    this.roots = 0;
    this.size = 0;
    this.aux = (Node<K, V>[]) Array.newInstance(Node.class, AUX_CONSOLIDATE_ARRAY_SIZE);
    this.other = this;
  }

  public boolean isEmpty() {
    return size == 0;
  }

  public long size() {
    return size;
  }

  public void clear() {
    minRoot = null;
    roots = 0;
    size = 0;
  }

  public Node<K, V> insert(K key, V value) {
    if (other != this) {
      throw new IllegalStateException("A heap cannot be used after a meld");
    }
    if (key == null) {
      throw new NullPointerException("Null keys not permitted");
    }
    Node<K, V> n = new Node<K, V>(this, key, value);
    addToRootList(n);
    size++;
    return n;
  }

  public Node<K, V> insert(K key) {
    return insert(key, null);
  }

  public Node<K, V> findMin() {
    if (size == 0) {
      throw new NoSuchElementException();
    }
    return minRoot;
  }

  public Node<K, V> deleteMin() {
    if (size == 0) {
      throw new NoSuchElementException();
    }
    Node<K, V> z = minRoot;

    // move z children into root list
    Node<K, V> x = z.child;
    while (x != null) {
      Node<K, V> nextX = (x.next == x) ? null : x.next;

      // clear parent
      x.parent = null;

      // remove from child list
      x.prev.next = x.next;
      x.next.prev = x.prev;

      // add to root list
      x.next = minRoot.next;
      x.prev = minRoot;
      minRoot.next = x;
      x.next.prev = x;
      roots++;

      // advance
      x = nextX;
    }
    z.degree = 0;
    z.child = null;

    // remove z from root list
    z.prev.next = z.next;
    z.next.prev = z.prev;
    roots--;

    // decrease size
    size--;

    // update minimum root
    if (z == z.next) {
      minRoot = null;
    } else {
      minRoot = z.next;
      consolidate();
    }

    // clear other fields
    z.next = null;
    z.prev = null;

    return z;
  }

  @SuppressWarnings("unchecked")
  private void consolidate() {
    int maxDegree = -1;

    // for each node in root list
    int numRoots = roots;
    Node<K, V> x = minRoot;
    while (numRoots > 0) {
      Node<K, V> nextX = x.next;
      int d = x.degree;

      while (true) {
        Node<K, V> y = aux[d];
        if (y == null) {
          break;
        }

        // make sure x's key is smaller
        int c = ((Comparable<? super K>) y.key).compareTo(x.key);
        if (c < 0) {
          Node<K, V> tmp = x;
          x = y;
          y = tmp;
        }

        // make y a child of x
        link(y, x);

        aux[d] = null;
        d++;
      }

      // store result
      aux[d] = x;

      // keep track of max degree
      if (d > maxDegree) {
        maxDegree = d;
      }

      // advance
      x = nextX;
      numRoots--;
    }

    // recreate root list and find minimum root
    minRoot = null;
    roots = 0;
    for (int i = 0; i <= maxDegree; i++) {
      if (aux[i] != null) {
        addToRootList(aux[i]);
        aux[i] = null;
      }
    }
  }

  private void link(Node<K, V> y, Node<K, V> x) {
    // remove from root list
    y.prev.next = y.next;
    y.next.prev = y.prev;

    // one less root
    roots--;

    // clear if marked
    y.mark = false;

    // hang as x's child
    x.degree++;
    y.parent = x;

    Node<K, V> child = x.child;
    if (child == null) {
      x.child = y;
      y.next = y;
      y.prev = y;
    } else {
      y.prev = child;
      y.next = child.next;
      child.next = y;
      y.next.prev = y;
    }
  }

  @SuppressWarnings("unchecked")
  public void decreaseKey(Node<K, V> n, K newKey) {
    int c = ((Comparable<? super K>) newKey).compareTo(n.key);
    if (c > 0) {
      throw new IllegalArgumentException("Keys can only be decreased!");
    }
    n.key = newKey;
    if (c == 0) {
      return;
    }

    if (n.next == null) {
      throw new IllegalArgumentException("Invalid handle!");
    }

    // if not root and heap order violation
    Node<K, V> y = n.parent;
    if (y != null && ((Comparable<? super K>) n.key).compareTo(y.key) < 0) {
      cut(n, y);
      cascadingCut(y);
    }

    // update minimum root
    if (((Comparable<? super K>) n.key).compareTo(minRoot.key) < 0) {
      minRoot = n;
    }
  }

  private void forceDecreaseKeyToMinimum(Node<K, V> n) {
    // if not root
    Node<K, V> y = n.parent;
    if (y != null) {
      cut(n, y);
      cascadingCut(y);
    }
    minRoot = n;
  }

  private void cut(Node<K, V> x, Node<K, V> y) {
    // remove x from child list of y
    x.prev.next = x.next;
    x.next.prev = x.prev;
    y.degree--;
    if (y.degree == 0) {
      y.child = null;
    } else if (y.child == x) {
      y.child = x.next;
    }

    // add x to the root list
    x.parent = null;
    addToRootList(x);

    // clear if marked
    x.mark = false;
  }

  @SuppressWarnings("unchecked")
  private void addToRootList(Node<K, V> n) {
    if (minRoot == null) {
      n.next = n;
      n.prev = n;
      minRoot = n;
      roots = 1;
    } else {
      n.next = minRoot.next;
      n.prev = minRoot;
      minRoot.next.prev = n;
      minRoot.next = n;

      int c = ((Comparable<? super K>) n.key).compareTo(minRoot.key);
      if (c < 0) {
        minRoot = n;
      }
      roots++;
    }
  }

  private void cascadingCut(Node<K, V> y) {
    Node<K, V> z;
    while ((z = y.parent) != null) {
      if (!y.mark) {
        y.mark = true;
        break;
      }
      cut(y, z);
      y = z;
    }
  }

  @Override
  public Object clone() {
    try {
      FibonacciHeap<K, V> newHeap = TypeUtil.<FibonacciHeap<K, V>>uncheckedCast(super.clone());

      newHeap.size = this.size;
      newHeap.roots = this.roots;
      newHeap.minRoot = this.minRoot;
      newHeap.aux = (Node<K, V>[]) this.aux.clone();
      newHeap.other = newHeap;
      return newHeap;
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException();
    }
  }

  public static class Node<K, V> implements Serializable, Cloneable {
    private static final long serialVersionUID = 2312314213123142L;

    public Node<K, V> parent;
    public Node<K, V> child;
    public Node<K, V> next;
    public Node<K, V> prev;
    public K key;
    public V value;
    public int degree;
    public boolean mark;

    FibonacciHeap<K, V> heap;

    public Node(FibonacciHeap<K, V> heap, K key, V value) {
      this.heap = heap;
      this.key = key;
      this.value = value;
      this.parent = null;
      this.child = null;
      this.next = null;
      this.prev = null;
      this.degree = 0;
      this.mark = false;
    }

    public K getKey() {
      return key;
    }

    public V getValue() {
      return value;
    }

    public void setValue(V value) {
      this.value = value;
    }

    public void decreaseKey(K newKey) {
      FibonacciHeap<K, V> h = getOwner();
      h.decreaseKey(this, newKey);
    }

    public void delete() {
      if (this.next == null) {
        throw new IllegalArgumentException("Invalid handle!");
      }
      FibonacciHeap<K, V> h = getOwner();
      h.forceDecreaseKeyToMinimum(this);
      h.deleteMin();
    }

    FibonacciHeap<K, V> getOwner() {
      if (heap.other != heap) {
        FibonacciHeap<K, V> root = heap;
        while (root != root.other) {
          root = root.other;
        }

        FibonacciHeap<K, V> current = heap;
        while (current.other != root) {
          FibonacciHeap<K, V> next = current.other;
          current.other = root;
          current = next;
        }
        heap = root;
      }
      return heap;
    }
  }
}
