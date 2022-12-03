package com.example.ppgr;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
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
    public RadioButton rbRadians, rbDegrees;

    public void initialize() {
        Arrow arrow1 = new Arrow(260, 305, 310, 345, 15);
        Arrow arrow2 = new Arrow(550, 305, 500, 345, 15);
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

            if(rbDegrees.isSelected()) {                                 // pretvaranje uglova u radijane
                phi *= Math.PI / 180;
                theta *= Math.PI / 180;
                psi *= Math.PI / 180;
            }

            Matrix.Matrix3x3 A = euler2A(phi, theta, psi);              // 1. Euler2A(φ,θ,ψ) -> A
            taRotationMatrix.setText(A.toString());

            Pair<Vector, Double> pair = A2AxisAngle(A);                 // 2. A2AxisAngle(A) -> p, Φ
            Vector p = pair.getKey();
            double angle = pair.getValue();

            tfP.setText(p.toString());
            tfAngle.setText(String.format("%.6f", (angle * 180) / (Math.PI)) + "° = PI*" + String.format("%.6f", angle/ Math.PI));

            System.out.println("Rezultat dobijen formulom rodrigeza za p" + p + " i ugao " + String.format("%.6f", (angle * 180) / (Math.PI)) + "°:");
            System.out.println(rodriguez(p, angle));                    // 3. Rodriguez(p, Φ) -> A
            System.out.println("---------------------------------------------------------------");

            System.out.println("Uglovi dobijeni primenjivanjem funkcije A2Euler na matricu A:");
            Vector angles = A2Euler(A);                                 // 4. A2Euler(A) -> φ, θ, ψ
            System.out.println("φ: " + angles.getX() + ", u stepenima: " + String.format("%.3f", (angles.getX() * 180) / (Math.PI)) + "°");
            System.out.println("θ: " + angles.getY() + ", u stepenima: " + String.format("%.3f", (angles.getY() * 180) / (Math.PI)) + "°");
            System.out.println("ψ: " + angles.getZ() + ", u stepenima: " + String.format("%.3f", (angles.getZ() * 180) / (Math.PI)) + "°");
            System.out.println("---------------------------------------------------------------");

            Quaternion q = AxisAngle2Q(p, angle);                       // 5. AxisAngle2Q(p, Φ) -> q
            tfQuaternion.setText(q.toString());

            Pair<Vector, Double> pairQ = Q2AxisAngle(q);                // 6. Q2AxisAngle(q) -> p, Φ
            Vector pQ = pairQ.getKey();
            double angleQ = pairQ.getValue();
            System.out.println("q = " + q);
            System.out.println("Osa rotacije dobijena iz kvaterniona q:");
            System.out.println("p = " + pQ);
            System.out.println("Ugao rotacije dobijen iz kvaterniona q:");
            System.out.println(String.format("%.5f", angleQ) + ", u stepenima: " + String.format("%.3f", (angleQ * 180) / (Math.PI)) + "°");
            System.out.println("---------------------------------------------------------------");
        }
        catch (NumberFormatException e) {
            lbInvalidInput.setVisible(true);
        }
    }

    private Matrix.Matrix3x3 euler2A(double phi, double theta, double psi) {
        System.out.println("phi: " + phi + ", theta: " + theta + ", psi: " + psi);
        System.out.println("---------------------------------------------------------------");

        double cosPhi = Math.cos(phi);
        double sinPhi = Math.sin(phi);
        double cosTheta = Math.cos(theta);
        double sinTheta = Math.sin(theta);
        double cosPsi = Math.cos(psi);
        double sinPsi = Math.sin(psi);

        Matrix.Matrix3x3 Rx = new Matrix.Matrix3x3(1, 0, 0, 0, cosPhi, -sinPhi, 0, sinPhi, cosPhi);
        Matrix.Matrix3x3 Ry = new Matrix.Matrix3x3(cosTheta, 0, sinTheta, 0, 1, 0, -sinTheta, 0, cosTheta);
        Matrix.Matrix3x3 Rz = new Matrix.Matrix3x3(cosPsi, -sinPsi, 0, sinPsi, cosPsi, 0, 0, 0, 1);

        return Matrix.multiply(Rz, Matrix.multiply(Ry, Rx));        // Rz(ψ)∘Ry(θ)∘Rx(φ)
    }

    private Pair<Vector, Double> A2AxisAngle(Matrix.Matrix3x3 A) {
        Matrix.Matrix3x3 B = A.subtract(Matrix.identity3());  // A - E

        Vector n1 = Matrix.transpose(B).getV1();    // prva vrsta matrice A - E
        Vector n2 = Matrix.transpose(B).getV2();    // druga vrsta matrice A - E
        Vector n3 = Matrix.transpose(B).getV3();    // treca vrsta matrice A - E

        Vector p = n1.cross(n2);                    // vektor p = n1 x n2
        if(p.isZeroVector())                        // ako su n1 i n2 linearno zavisni
            p = n1.cross(n3);                       // tada je p = n1 x n3

        p = p.normalize();                          // normiramo vektor p

        Vector x = n1.normalize();                  // jedinicni vektor normalan na p
        Vector xp = A.multiplyBy(x);                // vektor dobijen primenom izometrije A na x

        double angleCos = x.scalar(xp);             // kosinus ugla rotacije dobijamo skalarnim proizvodom x i xp
        double angle = Math.acos(angleCos);         // ugao je arccos prethodne vrednosti

        double mixedProduct = x.cross(xp).scalar(p); // mesoviti proizvod [x, xp, p]

        if(mixedProduct < 0)                        // ako je mesoviti proizvod < 0
            p = p.multiplyBy(-1);             // p = -p

        return new Pair<>(p, angle);                // p, Φ
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
        double a11 = A.getV1().getX();
        double a12 = A.getV2().getX();
        double a21 = A.getV1().getY();
        double a22 = A.getV2().getY();
        double a31 = A.getV1().getZ();
        double a32 = A.getV2().getZ();
        double a33 = A.getV3().getZ();
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
        return new Vector(phi, theta, psi);     // φ, θ, ψ
    }

    private Quaternion AxisAngle2Q(Vector p, double phi) {
        double w = Math.cos(phi/2);
        p = p.normalize();

        Vector xyz = p.multiplyBy(Math.sin(phi/2)); // (x, y, z) = sin(φ/2) * (px, py, pz);
        double x = xyz.getX();
        double y = xyz.getY();
        double z = xyz.getZ();

        return new Quaternion(x, y, z, w);          // q = x*i + y*j + k*z + w
    }

    private Pair<Vector, Double> Q2AxisAngle(Quaternion q) {
        if(q.getW() < 0) {
            q = q.multiplyBy(-1);
        }
        double w = q.getW();

        double angle = 2 * Math.acos(w);        // Φ = 2arccos(w)
        Vector p;

        double EPS = 0.001;
        if(Math.abs(w - 1) < EPS) {             // ako je w = 1
            p = new Vector(1,0,0);     // uzimamo bilo koji jedinicni vektor, npr (1,0,0)
        } else {
            p = q.getP().normalize();
        }

        return new Pair<>(p, angle);            // p, Φ
    }

    public void Deg2Rad() {     // pretvara uglove date u stepenima u radijane
        try {
            double phi = Double.parseDouble(tfPhi.getText());
            double theta = Double.parseDouble(tfTheta.getText());
            double psi = Double.parseDouble(tfPsi.getText());

            phi *= Math.PI / 180;
            theta *= Math.PI / 180;
            psi *= Math.PI / 180;

            tfPhi.setText(String.valueOf(phi));
            tfTheta.setText(String.valueOf(theta));
            tfPsi.setText(String.valueOf(psi));
        }
        catch (NumberFormatException ignored) {}
    }

    public void Rad2Deg() {     // pretvara uglove date u radijanima u stepene
        try {
            double phi = Double.parseDouble(tfPhi.getText());
            double theta = Double.parseDouble(tfTheta.getText());
            double psi = Double.parseDouble(tfPsi.getText());

            phi *= 180 / Math.PI;
            theta *= 180 / Math.PI;
            psi *= 180 / Math.PI;

            tfPhi.setText(String.valueOf(phi));
            tfTheta.setText(String.valueOf(theta));
            tfPsi.setText(String.valueOf(psi));
        }
        catch (NumberFormatException ignored) {}
    }

    public void loadTest1() {   // test primer 1 - onaj koji je koriscen u fazi razvoja
        double phi = -Math.atan(0.25);
        double theta = -Math.asin(8/9.0);
        double psi = Math.atan(4);

        tfPhi.setText(String.valueOf(phi));
        tfTheta.setText(String.valueOf(theta));
        tfPsi.setText(String.valueOf(psi));

        if(rbDegrees.isSelected()) {
            Rad2Deg();
        }
    }

    public void loadTest2() {   // test primer 2 - prema formuli za broj indeksa n = 96
        int n = 96;
        double phi = (1/3.0)*Math.PI * (n % 5 + 1);
        double theta = (Math.PI / 17) * (n % 8 + 1);
        double psi = (Math.PI / 4) * (n % 7 + 1);

        tfPhi.setText(String.valueOf(phi));
        tfTheta.setText(String.valueOf(theta));
        tfPsi.setText(String.valueOf(psi));

        if(rbDegrees.isSelected()) {
            Rad2Deg();
        }
    }
}