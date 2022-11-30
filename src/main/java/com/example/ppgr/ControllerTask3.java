package com.example.ppgr;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class ControllerTask3 extends ButtonAction {

    public TextField tfPhi, tfTheta, tfPsi;
    public TextArea taRotationMatrix;
    public Button btFindRotationMatrix;
    public Label lbInvalidInput;

    public void backToMainMenuButtonPressed(ActionEvent e) throws IOException {
        backToMainMenu((Stage) ((Node) e.getSource()).getScene().getWindow());
    }

    public void findRotationMatrixButtonPressed() {
        lbInvalidInput.setVisible(false);
        try {
            double phi = Double.parseDouble(tfPhi.getText());
            double theta = Double.parseDouble(tfTheta.getText());
            double psi = Double.parseDouble(tfPsi.getText());

            Matrix.Matrix3x3 M = euler2A(phi, theta, psi);
            taRotationMatrix.setText(M.toString());
        }
        catch (NumberFormatException e) {
            lbInvalidInput.setVisible(true);
        }
    }

    public Matrix.Matrix3x3 euler2A(double phi, double theta, double psi) {
//        phi = -Math.atan(0.25);
//        theta = -Math.asin(8/9.0);
//        psi = Math.atan(4);

        double cosPhi = Math.cos(phi);
        double sinPhi = Math.sin(phi);
        double cosTheta = Math.cos(theta);
        double sinTheta = Math.sin(theta);
        double cosPsi = Math.cos(psi);
        double sinPsi = Math.sin(psi);

        Matrix.Matrix3x3 Rx = new Matrix.Matrix3x3(1, 0, 0, 0, cosPhi, -sinPhi, 0, sinPhi, cosPhi);
        Matrix.Matrix3x3 Ry = new Matrix.Matrix3x3(cosTheta, 0, sinTheta, 0, 1, 0, -sinTheta, 0, cosTheta);
        Matrix.Matrix3x3 Rz = new Matrix.Matrix3x3(cosPsi, -sinPsi, 0, sinPsi, cosPsi, 0, 0, 0, 1);

        return Matrix.multiply(Rz, Matrix.multiply(Ry, Rx));
    }
}
