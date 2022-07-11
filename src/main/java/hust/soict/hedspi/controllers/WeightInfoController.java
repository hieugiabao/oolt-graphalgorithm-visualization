package hust.soict.hedspi.controllers;

import hust.soict.hedspi.model.graph.Edge;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;

public class WeightInfoController {
  @FXML
  private AnchorPane root;
  @FXML
  private Button confirmButton;
  @FXML
  private TextField weightInput;
  private double weight = Edge.DEFAULT_EDGE_WEIGHT;

  @FXML
  private void initialize() {
    weightInput.setText(weight + "");
    BooleanBinding binding = weightInput.textProperty().isEmpty();
    confirmButton.disableProperty().bind(binding);

    confirmButton.setOnAction(event -> {
      confirm();
    });

    root.setOnKeyPressed((ke) -> {
      if (ke.getCode() == KeyCode.ENTER) {
        confirm();
      }
    });
  }

  public double getWeight() {
    return weight;
  }

  private void confirm() {
    try {
      weight = Double.parseDouble(weightInput.getText());
      confirmButton.getScene().getWindow().hide();
    } catch (NumberFormatException e) {
      weight = Edge.DEFAULT_EDGE_WEIGHT;
      weightInput.setText("");
      Alert alert = new Alert(AlertType.ERROR);
      alert.setTitle("Invalid weight!");
      alert.setHeaderText("Please enter a valid weight!");
      alert.setContentText("Weight value must be a number!");
      alert.showAndWait();
    }
  }
}
