package hust.soict.hedspi;

import hust.soict.hedspi.model.graph.BaseGraph;
import hust.soict.hedspi.model.graph.UndirectedGraph;
import hust.soict.hedspi.utils.TypeUtil;
import hust.soict.hedspi.view.GraphPanel;
import hust.soict.hedspi.view.container.GraphContainer;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Hello world!
 *
 */
public class Application extends javafx.application.Application {
	public static void main(String[] args) {
		/*
		 * UndirectedGraph graph = (UndirectedGraph) BaseGraph.CP410();
		 * Kruskal a1 = new Kruskal(graph);
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

		UndirectedGraph graph = TypeUtil.uncheckedCast(BaseGraph.CP410());
		GraphPanel graphView = new GraphPanel(graph);

		Scene scene = new Scene(new GraphContainer(graphView), 800, 600);
		Stage stage = new Stage(StageStyle.DECORATED);
		stage.setScene(scene);
		stage.setTitle("Graph View");
		stage.setScene(scene);
		stage.show();

		graphView.init();
	}
}
