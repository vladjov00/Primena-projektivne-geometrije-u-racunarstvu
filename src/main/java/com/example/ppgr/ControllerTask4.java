package com.example.ppgr;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class ControllerTask4 extends ButtonAction {

    public TextField tfT11, tfT12, tfT13, tfT14, tfT21, tfT22, tfT23, tfT24, tfT31, tfT32, tfT33, tfT34;
    public TextField tfM1, tfM1p, tfM2, tfM2p, tfM3, tfM3p, tfM4, tfM4p, tfM5, tfM5p, tfM6, tfM6p;
    public Label lbInvalidInput, lbInvalidInputCoords;
    public TextArea taK, taA, taC, taTResult;

    private final TextField[] textFieldsT = new TextField[12];
    private final TextField[] textFieldsSpace = new TextField[6];
    private final TextField[] textFieldsProj = new TextField[6];

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
        
        textFieldsSpace[0] = tfM1;
        textFieldsSpace[1] = tfM2;
        textFieldsSpace[2] = tfM3;
        textFieldsSpace[3] = tfM4;
        textFieldsSpace[4] = tfM5;
        textFieldsSpace[5] = tfM6;

        textFieldsProj[0] = tfM1p;
        textFieldsProj[1] = tfM2p;
        textFieldsProj[2] = tfM3p;
        textFieldsProj[3] = tfM4p;
        textFieldsProj[4] = tfM5p;
        textFieldsProj[5] = tfM6p;
        
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

    public void calculateCameraMatrixButtonPressed() {
        lbInvalidInputCoords.setVisible(false);
        try {
            Vector4[] originals = getSpaceCoordinatesFromTextFields();
            Vector[] projections = getProjectionCoordinatesFromTextFields();

            JamaMatrix cameraT = CameraDLP(originals, projections);
            cameraT = cameraT.times(1 / cameraT.get(0,0));

            StringBuilder cameraTstrb = new StringBuilder();
            for(int i = 0; i < 3; i++) {
                for(int j = 0; j < 4; j++) {
                    cameraTstrb.append(String.format("%16.7f", cameraT.get(0, i * 4 + j)));
                    cameraTstrb.append(" ");
                }
                cameraTstrb.append("\n");
            }
            taTResult.setText(cameraTstrb.toString());

        } catch (NumberFormatException e) {
            lbInvalidInputCoords.setVisible(true);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public JamaMatrix CameraDLP(Vector4[] originals, Vector[] projections) throws Exception {
        if(originals.length != projections.length) {
            throw new Exception("Lengths of arrays do not match!");
        }
        if(originals.length < 6) {
            throw new Exception("At least 6 points needed!");
        }

        JamaMatrix SystemMatrix = new JamaMatrix(2 * originals.length, 12);
        for(int i = 0; i < originals.length; i++) {
            SystemMatrix.set(2*i, 0, 0);
            SystemMatrix.set(2*i, 1, 0);
            SystemMatrix.set(2*i, 2, 0);
            SystemMatrix.set(2*i, 3, 0);

            SystemMatrix.set(2*i, 4, -projections[i].getZ() * originals[i].getX());
            SystemMatrix.set(2*i, 5, -projections[i].getZ() * originals[i].getY());
            SystemMatrix.set(2*i, 6, -projections[i].getZ() * originals[i].getZ());
            SystemMatrix.set(2*i, 7, -projections[i].getZ() * originals[i].getW());

            SystemMatrix.set(2*i, 8, projections[i].getY() * originals[i].getX());
            SystemMatrix.set(2*i, 9, projections[i].getY() * originals[i].getY());
            SystemMatrix.set(2*i, 10, projections[i].getY() * originals[i].getZ());
            SystemMatrix.set(2*i, 11, projections[i].getY() * originals[i].getW());

            SystemMatrix.set(2*i + 1, 0, projections[i].getZ() * originals[i].getX());
            SystemMatrix.set(2*i + 1, 1, projections[i].getZ() * originals[i].getY());
            SystemMatrix.set(2*i + 1, 2, projections[i].getZ() * originals[i].getZ());
            SystemMatrix.set(2*i + 1, 3, projections[i].getZ() * originals[i].getW());

            SystemMatrix.set(2*i + 1, 4, 0);
            SystemMatrix.set(2*i + 1, 5, 0);
            SystemMatrix.set(2*i + 1, 6, 0);
            SystemMatrix.set(2*i + 1, 7, 0);

            SystemMatrix.set(2*i + 1, 8, -projections[i].getX() * originals[i].getX());
            SystemMatrix.set(2*i + 1, 9, -projections[i].getX() * originals[i].getY());
            SystemMatrix.set(2*i + 1, 10, -projections[i].getX() * originals[i].getZ());
            SystemMatrix.set(2*i + 1, 11, -projections[i].getX() * originals[i].getW());
        }

        SVDecomposition svd = new SVDecomposition(SystemMatrix);
        var Vt = svd.getV();
        var V = Vt.transpose();

        return V.getJamaMatrix(V.getRowDimension()-1, V.getRowDimension()-1, 0, 11);
    }

    private Vector4[] getSpaceCoordinatesFromTextFields() throws NumberFormatException{
        Vector4[] coords = new Vector4[6];
        for(int i = 0; i < 6; i++) {
            String coordStr = textFieldsSpace[i].getText();
            String[] coordStrSplt = coordStr.split(",");
            coords[i] = new Vector4(Double.parseDouble(coordStrSplt[0].trim()), Double.parseDouble(coordStrSplt[1].trim()),
                    Double.parseDouble(coordStrSplt[2].trim()), Double.parseDouble(coordStrSplt[3].trim()));
        }
        return coords;
    }

    private Vector[] getProjectionCoordinatesFromTextFields() throws NumberFormatException {
        Vector[] coords = new Vector[6];
        for(int i = 0; i < 6; i++) {
            String coordStr = textFieldsProj[i].getText();
            String[] coordStrSplt = coordStr.split(",");
            coords[i] = new Vector(Double.parseDouble(coordStrSplt[0].trim()), Double.parseDouble(coordStrSplt[1].trim()),
                    Double.parseDouble(coordStrSplt[2].trim()));
        }
        return coords;
    }

    public void loadCameraMatrixToT() {
        if (taTResult.getText().isEmpty())
            return;

        Scanner sc = new Scanner(taTResult.getText());
        int i = 0;
        while(sc.hasNext()) {
            textFieldsT[i].setText(sc.next());
            i++;
        }
        sc.close();
    }

    public void loadTest1() {
        testN(11);
    }

    public void loadTest2() {
        testN(96);
    }

    public void loadTest3() {
        int[] testArray = {1, 5, 7, -2, 3, 0, 2, 3, 4, -1, 0, 1};

        for (int i = 0; i < 12; i++) {
            textFieldsT[i].setText(String.valueOf(testArray[i]));
        }
    }

    private void testN(int n) {
        int[] testArray = {5, -1 - 2 * n, 3, 18 - 3 * n, 0, -1, 5, 21, 0, -1, 0, 1};

        for (int i = 0; i < 12; i++) {
            textFieldsT[i].setText(String.valueOf(testArray[i]));
        }
    }

    public void loadCoordsTest1() {
        String Ms = "460, 280, 250, 1; 50, 380, 350, 1; 470, 500, 100, 1; 380, 630, 550, 1; 330, 290, 0, 1; 580, 0, 130, 1";
        String MPs = "288, 251, 1; 79, 510, 1; 470, 440, 1; 520, 590, 1; 365, 388, 1; 365, 20, 1";

        String[] splitMs = Ms.split(";");
        String[] splitMPs = MPs.split(";");

        for(int i = 0; i < textFieldsSpace.length; i++) {
            textFieldsSpace[i].setText(splitMs[i].trim());
            textFieldsProj[i].setText(splitMPs[i].trim());
        }
    }

    public void loadCoordsTest2() {
        String Ms = "460, 280, 250, 1; 50, 380, 350, 1; 470, 500, 100, 1; 380, 630, 300, 1; 180, 290, 0, 1; 580, 0, 130, 1";
        String MPs = "288, 251, 1; 79, 510, 1; 470, 440, 1; 520, 590, 1; 365, 388, 1; 365, 20, 1";

        String[] splitMs = Ms.split(";");
        String[] splitMPs = MPs.split(";");

        for(int i = 0; i < textFieldsSpace.length; i++) {
            textFieldsSpace[i].setText(splitMs[i].trim());
            textFieldsProj[i].setText(splitMPs[i].trim());
        }
    }

}
