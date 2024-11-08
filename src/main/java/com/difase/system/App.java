package com.difase.system;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

  @Override
  public void start(Stage stage) throws IOException {
    Parent root = FXMLLoader.load(getClass().getResource("/com/difase/system/Layout.fxml"));
    Scene scene = new Scene(root);
    scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
    stage.setTitle("Difase Machinery");
    stage.setScene(scene);
    stage.setResizable(false);
    stage.show();
  }
}