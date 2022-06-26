package hust.soict.hedspi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hust.soict.hedspi.model.algo.spanning.Prim;
import hust.soict.hedspi.model.graph.BaseGraph;
import hust.soict.hedspi.model.graph.DirectedGraph;
import hust.soict.hedspi.model.graph.UndirectedGraph;
import hust.soict.hedspi.model.graph.Vertex;
import hust.soict.hedspi.utils.TypeUtil;
import hust.soict.hedspi.view.CircularPlacementStrategy;
import hust.soict.hedspi.view.GraphPanel;
import hust.soict.hedspi.view.PlacementStrategy;
import hust.soict.hedspi.view.container.GraphContainer;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Hello world!
 *
 */
public class Application extends javafx.application.Application {
	private static final Logger logger = LogManager.getLogger(Application.class);

	public static void main(String[] args) {

		/*
		 * UndirectedGraph graph = (UndirectedGraph) BaseGraph.CP410();
		 * Prim a1 = new Prim(graph, new Vertex(0));
		 * 
		 * a1.printStep();
		 */

		/*
		 * Context context = new Context();
		 * context.setAlgorithm(a1);
		 * context.doExploration();
		 */
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		// TODO Auto-generated method stub

		UndirectedGraph graph = TypeUtil.uncheckedCast(BaseGraph.Rail());
		// PlacementStrategy stategy = new CircularPlacementStrategy();
		GraphPanel graphView = new GraphPanel(graph);

		Scene scene = new Scene(new GraphContainer(graphView), 800, 600);
		Stage stage = new Stage(StageStyle.DECORATED);
		stage.setScene(scene);
		stage.setTitle("Graph View");
		stage.setScene(scene);
		stage.show();
		System.out.println("");
		graphView.init();
		logger.info("Graph view initialized");

		// graphView.setAutomaticLayout(false);
	}
}
