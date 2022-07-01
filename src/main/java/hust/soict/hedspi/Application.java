package hust.soict.hedspi;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 * Hello world!
 *
 */
public class Application extends javafx.application.Application {
	private static final Logger logger = LogManager.getLogger(Application.class);

	private static Stage stage;
	private static Scene scene;

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

		/*
		 * UndirectedGraph graph = TypeUtil.uncheckedCast(BaseGraph.Rail());
		 * // PlacementStrategy stategy = new CircularPlacementStrategy();
		 * GraphPanel graphView = new GraphPanel(graph);
		 * GraphContainer container = new GraphContainer(graphView);
		 * Scene scene = new Scene(container, 800, 600);
		 * primaryStage.setScene(scene);
		 * primaryStage.setTitle("Graph");
		 * primaryStage.show();
		 * graphView.init();
		 * logger.info("Graph view initialized");
		 */

		try {
			scene = new Scene(loadFXML("create-graph"));
			stage = new Stage(StageStyle.DECORATED);
			stage.setScene(scene);
			stage.setTitle("Graph View");
			stage.setScene(scene);
			stage.show();

			stage.setOnCloseRequest((WindowEvent t) -> {
				Platform.exit();
				System.exit(0);
			});
		} catch (IOException e) {
			// TODO: handle exception
			logger.error("Failed to load fxml file", e);
		}

		/*
		 * 
		 * graphView.init();
		 * logger.info("Graph view initialized");
		 */

		// graphView.setAutomaticLayout(false);
	}

	private static Parent loadFXML(String fxml) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("/fxml/" + fxml + ".fxml"));
		return fxmlLoader.load();
	}

	public static void setRoot(String fxml) throws IOException {
		scene.setRoot(loadFXML(fxml));
	}
}
