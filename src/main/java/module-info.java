module oolt.project {
  requires javafx.base;
  requires javafx.controls;
  requires transitive javafx.graphics;
  requires org.apache.logging.log4j;

  opens hust.soict.hedspi to javafx.fxml;
  // opens com.example.controller to javafx.fxml;

  exports hust.soict.hedspi.model;
  exports hust.soict.hedspi;
}