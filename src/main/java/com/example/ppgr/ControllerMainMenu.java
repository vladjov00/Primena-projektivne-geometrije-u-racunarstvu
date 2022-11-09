package com.example.ppgr;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class ControllerMainMenu {

    public TextArea taSceneDesc;
    public TextField tfSceneName;
    public ImageView ivScenePreview;
    public Button btImagePrev;
    public Button btImageNext;
    public ToggleGroup tgChooseScene;
    public RadioButton rbSelect1;
    public RadioButton rbSelect2;
    public Label lbImageNotFound;

    public void initialize() {
        rbSelect1.setUserData(new Task("scene1.fxml", "1. Nevidljivo teme",
                "task1.txt",
                Arrays.asList("task1preview.jpg", "task1preview2.jpg")));

        rbSelect2.setText("TASK 2: TRANSFORMATION MATRIX");
        rbSelect2.setUserData(new Task("scene2.fxml", "2. Računanje projektivnog preslikavanja, otklanjanje distorzije, panorama",
                "task2.txt",
                Arrays.asList("task2preview1.png", "task2preview2.png", "task2preview3.png")));
    }

    private void setImage(String fName) {
        File file = new File("src/main/resources/preview_images/" + fName);
        if(!file.exists()) {
            lbImageNotFound.setText("IMAGE " + fName + " NOT FOUND");
            lbImageNotFound.setVisible(true);
            lbImageNotFound.toFront();
            ivScenePreview.setImage(null);
            return;
        }
        ivScenePreview.setImage(new Image((file.toURI().toString())));
    }

    public void getInformation(ActionEvent e) {
        btImagePrev.setDisable(true);
        btImagePrev.setVisible(false);
        btImageNext.setDisable(true);
        btImageNext.setVisible(false);
        lbImageNotFound.setVisible(false);

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
            desc = Util.readFileAsString("src/main/resources/task_descriptions/" + selectedTask.descFile);
            descSR = desc.substring(desc.indexOf('~') + 2);
            descEN = desc.substring(desc.lastIndexOf('~') + 2);
        } catch (IOException ex) {
            desc = "ERROR::TASK_DESC_FILE (" + selectedTask.descFile + ") NOT FOUND";
        }

        taSceneDesc.setText(desc);
        tfSceneName.setText(selectedTask.name);

        setImage(selectedTask.previewImages.get(0));

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
        lbImageNotFound.setVisible(false);
        Task selectedTask = getTask();
        if(selectedTask == null || !selectedTask.hasNextImage())
            return;

        btImagePrev.setDisable(false);
        btImagePrev.setVisible(true);
        String nextImg = selectedTask.nextPreviewImage();

        if(!selectedTask.hasNextImage()){
            btImageNext.setDisable(true);
            btImageNext.setVisible(false);
            selectedTask.previousPreviewImage();
        }

        setImage(nextImg);
    }

    public void previousPreviewImage(ActionEvent e) {
        lbImageNotFound.setVisible(false);
        Task selectedTask = getTask();
        if(selectedTask == null || !selectedTask.hasPreviousImage())
            return;

        btImageNext.setDisable(false);
        btImageNext.setVisible(true);
        String prevImg = selectedTask.previousPreviewImage();

        if(!selectedTask.hasPreviousImage()){
            btImagePrev.setDisable(true);
            btImagePrev.setVisible(false);
            selectedTask.nextPreviewImage();
        }

        setImage(prevImg);
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