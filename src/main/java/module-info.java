module oolt.project {
  requires javafx.base;
  requires javafx.controls;
  requires transitive javafx.graphics;
  requires javafx.fxml;
  requires transitive javafx.swing;
  requires org.apache.logging.log4j;

  opens hust.soict.hedspi to javafx.fxml;
  opens hust.soict.hedspi.controllers to javafx.fxml;

  exports hust.soict.hedspi.model;
  exports hust.soict.hedspi.model.graph;
  exports hust.soict.hedspi;
}