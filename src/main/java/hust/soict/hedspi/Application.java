package hust.soict.hedspi;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
		 * DirectedGraph graph = (DirectedGraph) BaseGraph.CP4414();
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
			stage = new Stage(StageStyle.DECORATED);
			Parent root = loadFXML("main");
			scene = new Scene(root);
			stage.setScene(scene);
			stage.setTitle("Graph visualization");
			stage.setMaximized(true);
			stage.show();

			stage.getIcons().add(new Image(getClass().getResource("/icon/logo.jpg").toExternalForm()));
		} catch (IOException e) {
			// TODO: handle exception
			logger.error("Error: ", e);
		}

	}

	private static Parent loadFXML(String fxml) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("/fxml/" + fxml + ".fxml"));
		return fxmlLoader.load();
	}

	public static void setRoot(String fxml) throws IOException {
		scene.setRoot(loadFXML(fxml));
	}
}
