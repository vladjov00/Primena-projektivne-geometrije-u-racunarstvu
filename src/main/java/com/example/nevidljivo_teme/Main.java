package com.example.nevidljivo_teme;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        var fxmlSource = getClass().getResource("scene.fxml");
        if(fxmlSource == null)
        {
            System.err.println("INVALID FXML SOURCE FILE!");
            System.exit(1);
        }

        Parent root = FXMLLoader.load(fxmlSource);
        Scene scena = new Scene(root);
        stage.setScene(scena);
        stage.setTitle("Nevidljivo teme");
        stage.show();
    }
}
