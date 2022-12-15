package com.example.ppgr;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class ControllerTask4 extends ButtonAction {

    public TextField tfT11, tfT12, tfT13, tfT14, tfT21, tfT22, tfT23, tfT24, tfT31, tfT32, tfT33, tfT34;
    public Label lbInvalidInput;

    private final TextField[] textFieldsT = new TextField[12];

    public void initialize() {
        textFieldsT[0] = tfT11;
        textFieldsT[1] = tfT12;
        textFieldsT[2] = tfT13;
        textFieldsT[3] = tfT14;
        textFieldsT[4] = tfT21;
        textFieldsT[5] = tfT22;
        textFieldsT[6] = tfT23;
        textFieldsT[7] = tfT24;
        textFieldsT[8] = tfT31;
        textFieldsT[9] = tfT32;
        textFieldsT[10] = tfT33;
        textFieldsT[11] = tfT34;
    }

    public void backToMainMenuButtonPressed(ActionEvent e) throws IOException {
        backToMainMenu((Stage) ((Node) e.getSource()).getScene().getWindow());
    }

    public void clearTextFieldsT() {
        for(TextField tf : textFieldsT) {
            tf.clear();
        }
    }

    public void calculateCameraParamsButtonPressed() {
        lbInvalidInput.setVisible(false);
        double[] elements = new double[12];

        try {
            for (int i = 0; i < 12; i++) {
                elements[i] =  Double.parseDouble(textFieldsT[i].getText());
            }
            JamaMatrix T = new JamaMatrix(elements, 3);

            calculateCameraParams(T);
        } catch (NumberFormatException e) {
            lbInvalidInput.setVisible(true);
        }
    }

    public void calculateCameraParams(JamaMatrix T) {

    }

    public void loadTest1() {
        int n = 96;
        int[] testArray = {5, -1 - 2 * n, 3, 18 - 3 * n, 0, -1, 5, 21, 0, -1, 0, 1};

        for (int i = 0; i < 12; i++) {
            textFieldsT[i].setText(String.valueOf(testArray[i]));
        }
    }
}
