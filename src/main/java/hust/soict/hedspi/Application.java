package hust.soict.hedspi;

import hust.soict.hedspi.model.Context;
import hust.soict.hedspi.model.algo.Algorithm;
import hust.soict.hedspi.model.algo.spanning.Kruskal;
import hust.soict.hedspi.model.graph.BaseGraph;
import hust.soict.hedspi.model.graph.UndirectedGraph;

/**
 * Hello world!
 *
 */
public class Application {
	public static void main(String[] args) {
		UndirectedGraph graph = (UndirectedGraph) BaseGraph.CP410();
		Algorithm a1 = new Kruskal(graph);

		Context context = new Context();
		context.setAlgorithm(a1);
		context.doExploration();
	}
}
