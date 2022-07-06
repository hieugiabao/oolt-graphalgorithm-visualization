package hust.soict.hedspi.controllers;

import java.util.HashMap;
import java.util.Map;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class AlgorithmShowController {
  @FXML
  private AnchorPane root;
  @FXML
  private Label detailShow;
  @FXML
  private VBox pseudoCode;

  private MainController mainController;
  private Map<Integer, String> pseudoCodeMap;
  private Map<Integer, Label> pseudoLabelMap;

  @FXML
  private void initialize() {
    detailShow.setText("");
    detailShow.setPrefWidth(250);
    detailShow.setStyle("-fx-font-size: 14;-fx-word-wrap: break-word;");
    detailShow.setPadding(new Insets(5, 0, 0, 4));
    detailShow.setWrapText(true);
    detailShow.maxWidthProperty().bind(pseudoCode.widthProperty());

    Platform.runLater(() -> {
      mainController.algoDisableProperty.addListener((ov, oldValue, newValue) -> {
        pseudoCode.getChildren().clear();
        if (mainController.mstAlgorithm != null) {
          root.setPrefWidth(300);
          initPseudoCode();
        }
      });
    });
  }

  public void injectMainController(MainController mainController) {
    this.mainController = mainController;
  }

  private void initPseudoCode() {
    pseudoLabelMap = new HashMap<>();
    pseudoCodeMap = mainController.mstAlgorithm.getPseudoCode();
    for (int i = 1; i <= pseudoCodeMap.size(); i++) {
      Label label = new Label(i + ". " + pseudoCodeMap.get(i));
      label.setStyle("-fx-font-size: 14;-fx-word-wrap: break-word;");
      label.setPadding(new Insets(5, 0, 0, 4));
      label.setWrapText(true);
      label.maxWidthProperty().bind(pseudoCode.widthProperty());

      pseudoLabelMap.put(i, label);
      pseudoCode.getChildren().add(label);
    }
  }

  public void setDetailCode(String detail) {
    detailShow.setText(detail);
  }

  public void highlightPseudoCode(int[] lineNo) {
    resetPseudoCodeCss();
    for (int i = 0; i < lineNo.length; i++) {
      pseudoLabelMap.get(lineNo[i]).setStyle(
          "-fx-font-size: 14;-fx-word-wrap: break-word;-fx-font-weight: bold; -fx-background-color: #333; -fx-text-fill: #eee;");
    }
  }

  private void resetPseudoCodeCss() {
    for (Label label : pseudoLabelMap.values()) {
      label.setStyle("-fx-font-size: 14;-fx-word-wrap: break-word;");
    }
  }
}
