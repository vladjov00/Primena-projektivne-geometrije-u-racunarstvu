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
            desc = "ERROR::TASK_DESC_FILE (" + selectedTask.descFile + ") NOT FOUND";
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

        ivScenePreview.setImage(new Image(
                (new File("src/main/resources/preview_images/" + nextImg))
                        .toURI().toString())
        );
    }

    public void previousPreviewImage(ActionEvent e) {
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