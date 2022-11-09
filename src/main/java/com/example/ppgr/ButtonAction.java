package com.example.ppgr;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public abstract class ButtonAction {

    public File chooseImage(ImageView imageView, TextField tfImage) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Open Image");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));

        File selectedFile = fc.showOpenDialog(null);

        if(selectedFile == null)
            return null;

        Image img = new Image(selectedFile.toURI().toString());
        imageView.setImage(img);

        tfImage.setText(selectedFile.toURI().toString());

        return selectedFile;
    }

    public void backToMainMenu(Stage stage) throws IOException {
        var fxmlSource = getClass().getResource("main_menu.fxml");
        if(fxmlSource == null) throw new IOException("INVALID FXML SOURCE FILE!");

        Parent root = FXMLLoader.load(fxmlSource);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("PPGR Project");
        Util.setStageOnCenter(stage);
        stage.show();
    }

}
