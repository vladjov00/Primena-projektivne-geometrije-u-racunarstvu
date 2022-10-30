package com.example.ppgr;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

public class ControllerMainMenu {

    public TextArea taSceneDesc;
    public TextField tfSceneName;
    public ImageView ivScenePreview;
    public Button btImagePrev;
    public Button btImageNext;
    public ToggleGroup tgChooseScene;
    public RadioButton rbSelect1;
    public RadioButton rbSelect2;

    private class Task {
        private final String fxmlName;
        private final String name;
        private final String descFile;
        private final List<String> previewImages;
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
            stage.show();
        }

        public String nextPreviewImage() {
            btImagePrev.setDisable(false);
            btImagePrev.setVisible(true);
            if(iterator.hasNext()) {
                String next = iterator.next();
                if(!iterator.hasNext()){
                    btImageNext.setDisable(true);
                    btImageNext.setVisible(false);
                    iterator.previous();
                }
                return next;
            }
            return null;
        }

        public String previousPreviewImage() {
            btImageNext.setDisable(false);
            btImageNext.setVisible(true);
            if(iterator.hasPrevious()) {
                String prev = iterator.previous();
                if(!iterator.hasPrevious()){
                    btImagePrev.setDisable(true);
                    btImagePrev.setVisible(false);
                    iterator.next();
                }
                return prev;
            }
            return null;
        }

        public boolean hasNextImage() {
            return iterator.hasNext();
        }

        public boolean hasPreviousImage() {
            return iterator.hasPrevious();
        }
    }

    public void initialize() {
        rbSelect1.setUserData(new Task("scene1.fxml", "1. Nevidljivo teme", "task1.txt",
                Arrays.asList("task1preview.jpg", "task1preview2.jpg")));
    }

    public static String readFileAsString(String fileName) throws IOException {
        String data;
        data = new String(Files.readAllBytes(Paths.get(fileName)));
        return data;
    }

    public void getInformation(ActionEvent e) {
        Task selectedTask = getTask();
        if (selectedTask == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setContentText("INVALID TASK: task does not exist");
            alert.show();
            return;
        }

        String desc, descSR, descEN;
        try {
            desc = readFileAsString("src/main/resources/task_descriptions/" + selectedTask.descFile);
            descSR = desc.substring(desc.indexOf('~') + 2);
            descEN = desc.substring(desc.lastIndexOf('~') + 2);
        } catch (IOException ex) {
            System.err.println("ERROR::FATAL::FILE (" + selectedTask.descFile + ") NOT FOUND");
            throw new RuntimeException(ex);
        }

        taSceneDesc.setText(desc);
        tfSceneName.setText(selectedTask.name);

        ivScenePreview.setImage(new Image(
                (new File("src/main/resources/preview_images/" + selectedTask.previewImages.get(0)))
                        .toURI().toString())
        );

        btImagePrev.setDisable(true);
        btImagePrev.setVisible(false);
        if (selectedTask.hasNextImage()) {
            btImageNext.setDisable(false);
            btImageNext.setVisible(true);
        }
    }

    public void switchScene(ActionEvent e) throws IOException {
        if(tgChooseScene.getSelectedToggle() == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Please select a task");
            alert.setContentText("A task from the list must be selected");
            alert.show();
            return;
        }
        Task selectedTask = (Task) tgChooseScene.getSelectedToggle().getUserData();
        if(selectedTask == null) {
            System.out.println("Please select a valid task");
            return;
        }
        selectedTask.load(e);
    }
    
    public void nextPreviewImage(ActionEvent e) {
        Task selectedTask = getTask();
        if(selectedTask == null)
            return;
        String nextImg = selectedTask.hasNextImage() ? selectedTask.nextPreviewImage() : null;
        if(nextImg == null)
            return;

        ivScenePreview.setImage(new Image(
                (new File("src/main/resources/preview_images/" + nextImg))
                        .toURI().toString())
        );
    }

    public void previousPreviewImage(ActionEvent e) {
        Task selectedTask = getTask();
        if(selectedTask == null)
            return;
        String prevImg = selectedTask.hasPreviousImage() ? selectedTask.previousPreviewImage() : null;
        if(prevImg == null)
            return;
        ivScenePreview.setImage(new Image(
                (new File("src/main/resources/preview_images/" + prevImg))
                        .toURI().toString())
        );
    }

    private Task getTask() {
        if(tgChooseScene.getSelectedToggle() == null) {
            System.out.println("GET::TASK::No tasks selected!");
            return null;
        }
        Task selectedTask = (Task) tgChooseScene.getSelectedToggle().getUserData();
        if(selectedTask == null) {
            System.out.println("GET::TASK::Please select a valid task");
            return null;
        }
        return selectedTask;
    }
}