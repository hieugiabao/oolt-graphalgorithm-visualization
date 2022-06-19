package hust.soict.hedspi;

import hust.soict.hedspi.model.graph.DirectedGraph;
import hust.soict.hedspi.model.graph.Vertex;

/**
 * Hello world!
 *
 */
public class Application {
    public static void main(String[] args) {
        DirectedGraph digraph = new DirectedGraph();
        digraph.addVertex(new Vertex(0));
        digraph.addVertex(new Vertex(1));
        digraph.addVertex(new Vertex(2));

        digraph.addEdge(new Vertex(0), new Vertex(1));
        if (digraph.addEdge(new Vertex(1), new Vertex(0)) == null) {
            System.out.println("ok");
        }
    }
}
