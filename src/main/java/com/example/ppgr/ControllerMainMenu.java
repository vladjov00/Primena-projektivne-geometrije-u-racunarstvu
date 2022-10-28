package com.example.ppgr;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

public class ControllerMainMenu {

    @FXML
    private Stage stage;
    private Scene scene;
    private Parent root;

    public TextArea taSceneDesc;
    public TextField tfSceneName;
    public ImageView ivScenePreview;
    public Button btImagePrev;
    public Button btImageNext;
    public ToggleGroup tgChooseScene;
    public RadioButton rbSelect1;
    public RadioButton rbSelect2;

    private Task task1;
    private Task task2;

    private class Task {
        private String fxmlName;
        private String name;
        private String desc;
        private List<String> previewImages;
        private final ListIterator<String> iterator;

        public Task(String fxmlName, String name, String desc, List<String> previewImages) {
            this.name = name;
            this.desc = desc;
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
            root = FXMLLoader.load(fxmlSource);
            stage = (Stage)((Node)e.getSource()).getScene().getWindow();
            scene = new Scene(root);
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
        task1 = new Task("scene.fxml", "1. Nevidljivo teme", "Opis",
                Arrays.asList("task1preview.jpg", "task1preview2.jpg"));
        rbSelect1.setUserData(task1);
        task2 = new Task("test.fxml", "Test", "Test",
                Arrays.asList("test1.jpg", "test2.jpg"));
        rbSelect2.setUserData(task2);
    }

    public void getInformation(ActionEvent e) {
        Task selectedTask = getTask();

        taSceneDesc.setText(selectedTask.desc);
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
