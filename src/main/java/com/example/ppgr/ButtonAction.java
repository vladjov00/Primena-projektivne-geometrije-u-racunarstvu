package com.example.ppgr;

import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;

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

}
