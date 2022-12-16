package com.example.ppgr;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class ControllerTask4 extends ButtonAction {

    public TextField tfT11, tfT12, tfT13, tfT14, tfT21, tfT22, tfT23, tfT24, tfT31, tfT32, tfT33, tfT34;
    public Label lbInvalidInput;
    public TextArea taK, taA, taC;

    private final TextField[] textFieldsT = new TextField[12];

    public static class CameraParams {
        private final Matrix.Matrix3x3 K;
        private final Matrix.Matrix3x3 A;
        private final Vector C;

        public CameraParams(Matrix.Matrix3x3 k, Matrix.Matrix3x3 a, Vector c) {
            K = k;
            A = a;
            C = c;
        }

        public Matrix.Matrix3x3 getK() {
            return K;
        }

        public Matrix.Matrix3x3 getA() {
            return A;
        }

        public Vector getC() {
            return C;
        }
    }

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

            CameraParams params = calculateCameraParams(T);

            taK.setText(params.getK().toString());
            taA.setText(params.getA().toString());
            taC.setText(params.getC().toString());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            lbInvalidInput.setVisible(true);
        }
    }

    public CameraParams calculateCameraParams(JamaMatrix T) throws Exception {
        if(T.getColumnDimension() != 4 && T.getRowDimension() != 3) {
            throw new Exception("Matrix must be 3x4!");
        }

        var jamaT0 = T.getJamaMatrix(0, 2, 0, 2);

        Matrix.Matrix3x3 T0 = new Matrix.Matrix3x3(jamaT0);
        Vector v4 = new Vector(T.get(0,3), T.get(1,3), T.get(2, 3));

        Matrix.Matrix3x3 T0inv = Matrix.inverse(T0);
        Vector C = T0inv.multiplyBy(v4);

        var jamaT0inv = new JamaMatrix(T0inv.getElementsAsArray(), 3);

        QRDecomposition QR = new QRDecomposition(jamaT0inv);
        var jamaQ = QR.getQ();
        var jamaR = QR.getR();

        Matrix.Matrix3x3 Q = new Matrix.Matrix3x3(jamaQ);
        Matrix.Matrix3x3 R = new Matrix.Matrix3x3(jamaR);

        Matrix.Matrix3x3 K = Matrix.inverse(R);
        Matrix.Matrix3x3 A = Matrix.inverse(Q);

        K = K.multiplyBy(1 / K.getV3().getZ());

        return new CameraParams(K, A, C);
    }

    public void loadTest1() {
        testN(11);
    }

    public void loadTest2() {
        testN(96);
    }

    private void testN(int n) {
        int[] testArray = {5, -1 - 2 * n, 3, 18 - 3 * n, 0, -1, 5, 21, 0, -1, 0, 1};

        for (int i = 0; i < 12; i++) {
            textFieldsT[i].setText(String.valueOf(testArray[i]));
        }
    }
}
