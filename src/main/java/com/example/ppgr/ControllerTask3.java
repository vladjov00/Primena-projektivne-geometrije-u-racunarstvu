package com.example.ppgr;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;

public class ControllerTask3 extends ButtonAction {

    public AnchorPane window;
    public TextField tfPhi, tfTheta, tfPsi, tfP, tfAngle, tfQuaternion;
    public TextArea taRotationMatrix;
    public Button btFindRotationMatrix;
    public Label lbInvalidInput;

    public void initialize() {
        Arrow arrow1 = new Arrow(260, 140, 310, 180, 15);
        Arrow arrow2 = new Arrow(550, 140, 500, 180, 15);
        window.getChildren().addAll(arrow1, arrow2);
    }

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
            tfAngle.setText(String.format("%.6f", (angle * 180) / (Math.PI)) + "° = PI*" + String.format("%.6f", angle/ Math.PI));

            System.out.println("Rezultat dobijen formulom rodrigeza za p" + p + " i ugao " + String.format("%.6f", (angle * 180) / (Math.PI)) + "°:");
            System.out.println(rodriguez(p, angle));

            System.out.println("Uglovi dobijeni primenjivanjem funkcije A2Euler na matricu A:");
            Vector angles = A2Euler(A);
            System.out.println("φ: " + angles.getX() + ", u stepenima: " + String.format("%.3f", (angles.getX() * 180) / (Math.PI)) + "°");
            System.out.println("θ: " + angles.getY() + ", u stepenima: " + String.format("%.3f", (angles.getY() * 180) / (Math.PI)) + "°");
            System.out.println("ψ: " + angles.getZ() + ", u stepenima: " + String.format("%.3f", (angles.getZ() * 180) / (Math.PI)) + "°");

            Quaternion q = AxisAngle2Q(p, angle);
            tfQuaternion.setText(q.toString());
        }
        catch (NumberFormatException e) {
            lbInvalidInput.setVisible(true);
        }
    }

    private Matrix.Matrix3x3 euler2A(double phi, double theta, double psi) {
        phi = -Math.atan(0.25);
        theta = -Math.asin(8/9.0);
        psi = Math.atan(4);
        System.out.println("phi: " + phi + ", theta: " + theta + ", psi: " + psi);

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

    private Matrix.Matrix3x3 rodriguez(Vector p, double phi) {
        double p1 = p.getX();
        double p2 = p.getY();
        double p3 = p.getZ();
        Matrix.Matrix3x3 px = new Matrix.Matrix3x3(0, -p3, p2, p3, 0, -p1, -p2, p1, 0);
        Matrix.Matrix3x3 ppt = Matrix.multiplyVectors(p, p);

        double cosPhi = Math.cos(phi);
        double sinPhi = Math.sin(phi);


        Matrix.Matrix3x3 R = Matrix.identity3();        //  R = E
        R = R.multiplyBy(cosPhi);                       //  R = cosPhi * E
        R = R.add(ppt.multiplyBy(1 - cosPhi));    //  R = (1 - cosPhi)*ppt + cosPhi * E
        R = R.add(px.multiplyBy(sinPhi));               //  R = (1 - cosPhi)*ppt + cosPhi * E + sinPhi * px

        return R;
    }

    private Vector A2Euler(Matrix.Matrix3x3 A) {
        double phi, theta, psi;
        double a31 = A.getV1().getZ();
        double a21 = A.getV1().getY();
        double a11 = A.getV1().getX();
        double a32 = A.getV2().getZ();
        double a33 = A.getV3().getZ();
        double a12 = A.getV2().getX();
        double a22 = A.getV2().getY();
        if(a31 < 1){
            if(a31 > -1) {
                psi = Math.atan2(a21, a11);
                theta = Math.asin(-a31);
                phi = Math.atan2(a32, a33);
            } else {
                psi = Math.atan2(-a12, a22);
                theta = Math.PI / 2;
                phi = 0;
            }
        } else {
            psi = Math.atan2(-a12, a22);
            theta = -Math.PI / 2;
            phi = 0;
        }
        return new Vector(phi, theta, psi);
    }

    private Quaternion AxisAngle2Q(Vector p, double phi) {
        double w = Math.cos(phi/2);
        p = p.norm();

        Vector xyz = p.multiplyBy(Math.sin(phi/2)); // (x, y, z) = sin(φ/2) * (px, py, pz);
        double x = xyz.getX();
        double y = xyz.getY();
        double z = xyz.getZ();

        return new Quaternion(x, y, z, w);
    }
}