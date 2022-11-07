package com.example.ppgr;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

public class ControllerTask2  extends ButtonAction{

    public Button btChooseImage1, btChooseImage2;
    public Button btClear1, btClear2, btCompute;
    public ImageView ivCoordinateSystem1, ivCoordinateSystem2, ivImage1, ivImage2;
    public TextField tfImage1, tfImage2;
    public TextField tfAx,tfAy,tfBx,tfBy,tfCx,tfCy,tfDx,tfDy,tfApx,tfApy,tfBpx,tfBpy,tfCpx,tfCpy,tfDpx,tfDpy;
    public TextArea taResult;
    public AnchorPane window, apCoords1, apCoords2;

    private int[] counters;
    private boolean image1Chosen;
    private boolean image2Chosen;

    private static class AnchorPaneData {
        private final int counterId;
        private final Color pointsColor;
        private final String labelExtra;
        private final Circle[] pointsArray;
        private final TextField[] tfArray;

        private AnchorPaneData(int counterId, Color pointsColor, Circle[] pointsArray, TextField[] tfArray) {
            this.counterId = counterId;
            this.pointsColor = pointsColor;
            this.labelExtra = "";
            this.pointsArray = pointsArray;
            this.tfArray = tfArray;
        }

        private AnchorPaneData(int counterId, Color pointsColor, Circle[] pointsArray, TextField[] tfArray, String labelExtra) {
            this.counterId = counterId;
            this.pointsColor = pointsColor;
            this.labelExtra = labelExtra;
            this.pointsArray = pointsArray;
            this.tfArray = tfArray;
        }

        public int getCounterId() { return counterId; }

        public Color getPointsColor() { return pointsColor; }

        public String getLabelExtra() { return labelExtra; }

        public Circle[] getPointsArray() { return pointsArray; }

        public TextField[] getTfArray() { return tfArray; }
    }

    public void initialize() {
        Arrow arrow1 = new Arrow(400, 200, 500, 200, 15);
        Arrow arrow2 = new Arrow(1150, 26, 1200, 26, 5);
        Arrow arrow3 = new Arrow(1150, 76, 1200, 76, 5);
        Arrow arrow4 = new Arrow(1150, 126, 1200, 126, 5);
        Arrow arrow5 = new Arrow(1150, 176, 1200, 176, 5);
        window.getChildren().addAll(arrow1, arrow2, arrow3, arrow4, arrow5);
        counters = new int[4];
        TextField[] tfArray1 = {tfAx, tfAy, tfBx, tfBy, tfCx, tfCy, tfDx, tfDy};
        TextField[] tfArray2 = {tfApx, tfApy, tfBpx, tfBpy, tfCpx, tfCpy, tfDpx, tfDpy};
        apCoords1.setUserData(new AnchorPaneData(0, Color.rgb(0, 255, 34), new Circle[4], tfArray1));
        apCoords2.setUserData(new AnchorPaneData(1, Color.rgb(40, 157, 245), new Circle[4], tfArray2, "'"));
        btClear1.setUserData(apCoords1);
        btClear2.setUserData(apCoords2);
        btCompute.setUserData(new AnchorPane[]{apCoords1, apCoords2});
        image1Chosen = false;
        image2Chosen = false;
    }

    public void chooseImage1ButtonPressed(){
        chooseImage(ivImage1, tfImage1);
        image1Chosen = true;
    }
    public void chooseImage2ButtonPressed(){
        chooseImage(ivImage2, tfImage2);
        image2Chosen = true;
    }

    private Circle addPoint(double x, double y, Color color, AnchorPane ap, int counter){
        Circle circle = new Circle();
        circle.setCenterX(x);
        circle.setCenterY(y);
        circle.setRadius(5);
        circle.setFill(color);
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(2);
        ap.getChildren().add(circle);
        circle.toFront();

        Text pointId = new Text();
        if(((AnchorPaneData)ap.getUserData()).getLabelExtra() != null)
            pointId.setText(Character.toString('A' + counter) + ((AnchorPaneData)ap.getUserData()).getLabelExtra());
        else
            pointId.setText(Character.toString('A' + counter));
        pointId.setX(circle.getCenterX() - 5);
        pointId.setY(circle.getCenterY() - 15);
        ap.getChildren().add(pointId);
        pointId.toFront();

        return circle;
    }

    private void connectPoints(Circle point1, Circle point2, AnchorPane ap) {
        if(point2 == null)
            return;
        Line line = new Line();
        line.setStartX(point1.getCenterX());
        line.setStartY(point1.getCenterY());
        line.setEndX(point2.getCenterX());
        line.setEndY(point2.getCenterY());
        line.setStrokeWidth(2);
        ap.getChildren().add(line);
        line.toFront();
    }

    public void clickOnCoordinateSystem(MouseEvent e) {
        AnchorPane ap = (AnchorPane) e.getSource();
        int counterId = ((AnchorPaneData)ap.getUserData()).getCounterId();
        int counter = counters[counterId];
        if(counter == 4)
            return;

        Color color = ((AnchorPaneData)ap.getUserData()).getPointsColor();
        Circle[] points = ((AnchorPaneData)ap.getUserData()).getPointsArray();

        points[counter] = addPoint(e.getX(), e.getY(), color, ap, counter);
        if(counter > 0 && points[counter] != null) {
            connectPoints(points[counter], points[counter-1], ap);
        }
        if(counter == 3) {
            connectPoints(points[counter], points[0], ap);
        }

        TextField[] tfArray = ((AnchorPaneData)ap.getUserData()).getTfArray();
        tfArray[counter*2].setText(String.valueOf(points[counter].getCenterX()));
        tfArray[counter*2+1].setText(String.valueOf(points[counter].getCenterY()));

        counters[counterId]++;
    }

    public void clearCoords(ActionEvent e) {
        AnchorPane ap = (AnchorPane) ((Node) e.getSource()).getUserData();
        for(TextField tf : ((AnchorPaneData)ap.getUserData()).getTfArray()){
            tf.clear();
        }
        counters[((AnchorPaneData) ap.getUserData()).getCounterId()] = 0;
        Object[] list = ap.getChildren().toArray();
        for(Object obj : list) {
            if(obj.getClass().equals(Circle.class) || obj.getClass().equals(Line.class) || obj.getClass().equals(Text.class)) {
                ap.getChildren().remove(obj);
            }
        }
    }

    private double[] solve3x3(Vector[] vectors) {
        double eps = 1e-5;

        double Dx = Matrix.determinant3x3(new Matrix.Matrix3x3(vectors[3], vectors[1], vectors[2]));
        double Dy = Matrix.determinant3x3(new Matrix.Matrix3x3(vectors[0], vectors[3], vectors[2]));
        double Dz = Matrix.determinant3x3(new Matrix.Matrix3x3(vectors[0], vectors[1], vectors[3]));
        double D = Matrix.determinant3x3(new Matrix.Matrix3x3(vectors[0], vectors[1], vectors[2]));

        if(Math.abs(Dx) < eps || Math.abs(Dy) < eps || Math.abs(Dz) < eps || Math.abs(D) < eps) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Kolinearni vektori");
            alert.setContentText("Tacke moraju biti u opstem polozaju!");
            alert.show();
            return null;
        }

        return new double[]{Dx/D, Dy/D, Dz/D};
    }

    public void computeTransformationMatrixNaive(ActionEvent e) {
        if(counters[0] != 4 || counters[1] != 4)
            return;

        TextField[] points1 = ((AnchorPaneData) ((AnchorPane[]) ((Node)e.getSource()).getUserData())[0].getUserData()).getTfArray();
        TextField[] points2 = ((AnchorPaneData) ((AnchorPane[]) ((Node)e.getSource()).getUserData())[1].getUserData()).getTfArray();

        Vector[] vectorsBefore = new Vector[4];
        Vector[] vectorsAfter = new Vector[4];

        for(int i = 0; i < 4; i++) {
            vectorsBefore[i] = new Vector(Double.parseDouble(points1[i*2].getText()), Double.parseDouble(points1[i*2+1].getText()));
            vectorsAfter[i] = new Vector(Double.parseDouble(points2[i*2].getText()), Double.parseDouble(points2[i*2+1].getText()));
        }

        double[] vP1 = solve3x3(vectorsBefore);
        if(vP1 == null) {
            System.out.println("ERROR: Collinear vectors");
            return;
        }

        Matrix.Matrix3x3 P1 = new Matrix.Matrix3x3(vectorsBefore[0].multiplyBy(vP1[0]), vectorsBefore[1].multiplyBy(vP1[1]), vectorsBefore[2].multiplyBy(vP1[2]));

        double[] vP2 = solve3x3(vectorsAfter);
        if(vP2 == null) {
            System.out.println("ERROR: Collinear vectors");
            return;
        }

        Matrix.Matrix3x3 P2 = new Matrix.Matrix3x3(vectorsAfter[0].multiplyBy(vP2[0]), vectorsAfter[1].multiplyBy(vP2[1]), vectorsAfter[2].multiplyBy(vP2[2]));

        Matrix.Matrix3x3 P;
        try {
            P = Matrix.multiply(P2, Matrix.inverse(P1));
        } catch (Exception ex) {
            System.err.println("EXCEPTION");
            return;
        }

        taResult.setText(P.toString());

//        Vector A = new Vector(1,2,3);
//        Vector B = new Vector(3,2,1);
//        Vector C = new Vector(0,1,1);
//        Vector D = new Vector(7,11,10);
//        Vector A = new Vector(5,10,15);
//        Vector B = new Vector(15,10,5);
//        Vector C = new Vector(0,5,5);
//        Vector D = new Vector(35,55,50);
//        double[] r = solve3x3(new Vector[]{A, B, C, D});
//
//        System.out.println("lambda1: " + r[0] + " lambda2: " + r[1] + " lambda3: " + r[2]);
//        Matrix.Matrix3x3 m = new Matrix.Matrix3x3(A.multiplyBy(r[0]), B.multiplyBy(r[1]), C.multiplyBy(r[2]));
//        System.out.println(m.reduce());

    }

}