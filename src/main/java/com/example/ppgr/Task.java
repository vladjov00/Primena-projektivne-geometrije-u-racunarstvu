package com.example.ppgr;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.ListIterator;

public class Task {
    public final String fxmlName;
    public final String name;
    public final String descFile;
    public final List<String> previewImages;
    private final ListIterator<String> iterator;

    public Task(String fxmlName, String name, String descFile, List<String> previewImages) {
        this.name = name;
        this.descFile = descFile;
        this.previewImages = previewImages;
        this.fxmlName = fxmlName;
        this.iterator = previewImages.listIterator();
        if(iterator.hasNext())
            iterator.next();
    }

    /**
     * Switch to another scene defined by this.fxmlName
     * @param e
     * @throws IOException
     */
    public void load(ActionEvent e) throws IOException {
        var fxmlSource = getClass().getResource(fxmlName);
        if(fxmlSource == null)
        {
            System.err.println("INVALID FXML SOURCE FILE!");
            return;
        }
        Parent root = FXMLLoader.load(fxmlSource);
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        Util.setStageOnCenter(stage);
        stage.show();
    }

    public String nextPreviewImage() {
        return iterator.next();
    }

    public String previousPreviewImage() {
        return iterator.previous();
    }

    public boolean hasNextImage() {
        return iterator.hasNext();
    }

    public boolean hasPreviousImage() {
        return iterator.hasPrevious();
    }
}