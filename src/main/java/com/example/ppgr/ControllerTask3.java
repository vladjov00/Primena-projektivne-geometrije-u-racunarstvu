package com.example.ppgr;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;

public class ControllerTask3 extends ButtonAction {

    public TextField tfPhi, tfTheta, tfPsi, tfP, tfAngle;
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

            Matrix.Matrix3x3 A = euler2A(phi, theta, psi);
            taRotationMatrix.setText(A.toString());

            Pair<Vector, Double> a = A2AxisAngle(A);
            Vector p = a.getKey();
            double angle = a.getValue();

            tfP.setText(p.toString());
            tfAngle.setText(String.format("%.6f", (angle * 360) / (2*Math.PI)) + "° = PI*" + String.format("%.6f", angle/ Math.PI));
        }
        catch (NumberFormatException e) {
            lbInvalidInput.setVisible(true);
        }
    }

    private Matrix.Matrix3x3 euler2A(double phi, double theta, double psi) {
//        phi = -Math.atan(0.25);
//        theta = -Math.asin(8/9.0);
//        psi = Math.atan(4);
//        System.out.println("phi: " + phi + ", theta: " + theta + ", psi: " + psi);

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

    private Pair<Vector, Double> A2AxisAngle(Matrix.Matrix3x3 A) {
        Matrix.Matrix3x3 B = A.subtract(Matrix.identity3());  // A - E

        Vector n1 = Matrix.transpose(B).getV1();    // prva vrsta matrice A - E
        Vector n2 = Matrix.transpose(B).getV2();    // druga vrsta matrice A - E
        Vector n3 = Matrix.transpose(B).getV3();    // treca vrsta matrice A - E

        Vector p = n1.cross(n2);    // vektor p = n1 x n2
        if(p.isZeroVector())        // ako su n1 i n2 linearno zavisni
            p = n1.cross(n3);       // tada je p = n1 x n3

        p = p.norm();               // normiramo vektor p

        Vector x = n1.norm();           // jedinicni vektor normalan na p
        Vector xp = A.multiplyBy(x);    // vektor dobijen primenom izometrije A na x

        double angleCos = x.scalar(xp);     // kosinus ugla rotacije dobijamo skalarnim proizvodom x i xp
        double angle = Math.acos(angleCos); // ugao je arccos prethodne vrednosti

        double mixedProduct = x.cross(xp).scalar(p); // mesoviti proizvod [x, xp, p]

        if(mixedProduct < 0)                // ako je mesoviti proizvod < 0
            p = p.multiplyBy(-1);     // p = -p

        return new Pair<>(p, angle);
    }
}
