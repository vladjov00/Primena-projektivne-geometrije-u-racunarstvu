package com.example.ppgr;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class ControllerMainMenu {

    @FXML
    private Stage stage;
    private Scene scene;
    private Parent root;

    public TextArea taSceneDesc;
    public TextField tfSceneName;

    public ToggleGroup tgChooseScene;
    public RadioButton rbSelect1;
    public RadioButton rbSelect2;

    private Task task1;
    private Task task2;

    public void initialize() {
        task1 = new Task("scene.fxml", "1. Nevidljivo teme", "Opis", "placeholder");
        rbSelect1.setUserData(task1);
        task2 = new Task("test.fxml", "Test", "Test", "Test");
        rbSelect2.setUserData(task2);
    }

    private class Task {
        private String fxmlName;
        private String name;
        private String desc;
        private String previewImage;

        public Task(String fxmlName, String name, String desc, String previewImage) {
            this.name = name;
            this.desc = desc;
            this.previewImage = previewImage;
            this.fxmlName = fxmlName;
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
    }

    public void getInformation(ActionEvent e) {
        if(tgChooseScene.getSelectedToggle() == null) {
            System.out.println("Please select a task");
            return;
        }
        Task selectedTask = (Task) tgChooseScene.getSelectedToggle().getUserData();
        if(selectedTask == null) {
            System.out.println("Please select a valid task");
            return;
        }

        taSceneDesc.setText(selectedTask.desc);
        tfSceneName.setText(selectedTask.name);
    }

    public void switchScene(ActionEvent e) throws IOException {
        if(tgChooseScene.getSelectedToggle() == null) {
            System.out.println("Please select a task");
            return;
        }
        Task selectedTask = (Task) tgChooseScene.getSelectedToggle().getUserData();
        if(selectedTask == null) {
            System.out.println("Please select a valid task");
            return;
        }
        selectedTask.load(e);
    }
}
