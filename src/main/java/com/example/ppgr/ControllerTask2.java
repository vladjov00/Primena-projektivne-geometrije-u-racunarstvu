package com.example.ppgr;

import javafx.event.ActionEvent;
import javafx.scene.Node;
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

import java.util.Arrays;
import java.util.List;

public class ControllerTask2  extends ButtonAction{

    public Button btChooseImage1, btChooseImage2;
    public Button btClear1, btClear2, btCompute;
    public ImageView ivCoordinateSystem1, ivCoordinateSystem2, ivImage1, ivImage2;
    public TextField tfImage1, tfImage2;
    public TextArea taResult, taInfo;
    public AnchorPane window, apCoords1, apCoords2;

    private int[] counters;
    private boolean image1Chosen;
    private boolean image2Chosen;


    private static class AnchorPaneData {
        private final int counterId;
        private final Color pointsColor;
        private final String labelExtra;
        private final Circle[] pointsArray;

        private AnchorPaneData(int counterId, Color pointsColor, Circle[] pointsArray) {
            this.counterId = counterId;
            this.pointsColor = pointsColor;
            this.labelExtra = "";
            this.pointsArray = pointsArray;
        }

        private AnchorPaneData(int counterId, Color pointsColor,  Circle[] pointsArray, String labelExtra) {
            this.counterId = counterId;
            this.pointsColor = pointsColor;
            this.labelExtra = labelExtra;
            this.pointsArray = pointsArray;
        }

        public int getCounterId() { return counterId; }

        public Color getPointsColor() { return pointsColor; }

        public String getLabelExtra() { return labelExtra; }

        public Circle[] getPointsArray() { return pointsArray; }
    }

    public void initialize() {
        Arrow arrow = new Arrow(400, 200, 500, 200, 15);
        window.getChildren().add(arrow);
        counters = new int[4];
        image1Chosen = false;
        image2Chosen = false;
        apCoords1.setUserData(new AnchorPaneData(0, Color.rgb(0, 255, 34), new Circle[4]));
        apCoords2.setUserData(new AnchorPaneData(1, Color.rgb(40, 157, 245), new Circle[4], "'"));
        btClear1.setUserData(apCoords1);
        btClear2.setUserData(apCoords2);
        btCompute.setUserData(new AnchorPane[]{apCoords1, apCoords2});
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

        taInfo.setText(taInfo.getText() + Character.toString('A' + counter)
                + ((AnchorPaneData)ap.getUserData()).getLabelExtra() + "("
                + points[counter].getCenterX() + ", " + points[counter].getCenterY() + ")\n");

        counters[counterId]++;
    }

    public void clearCoords(ActionEvent e) {
        AnchorPane ap = (AnchorPane) ((Node) e.getSource()).getUserData();
        counters[((AnchorPaneData) ap.getUserData()).getCounterId()] = 0;
        Object[] list = ap.getChildren().toArray();
        for(Object obj : list) {
            if(obj.getClass().equals(Circle.class) || obj.getClass().equals(Line.class) || obj.getClass().equals(Text.class)) {
                ap.getChildren().remove(obj);
            }
        }
    }

    public void computeTransformationMatrix(ActionEvent e) {
        if(counters[0] != 4 || counters[1] != 4)
            return;

        Circle[] points1 = ((AnchorPaneData) ((List<AnchorPane>) ((Node)e.getSource()).getUserData()).get(0).getUserData()).getPointsArray();
        Circle[] points2 = ((AnchorPaneData) ((List<AnchorPane>) ((Node)e.getSource()).getUserData()).get(1).getUserData()).getPointsArray();

        Vector[] vectorsBefore = new Vector[4];
        Vector[] vectorsAfter = new Vector[4];

        for(int i = 0; i < 4; i++) {
            vectorsBefore[i] = new Vector(points1[i].getCenterX(), points1[i].getCenterY());
            vectorsAfter[i] = new Vector(points2[i].getCenterX(), points2[i].getCenterY());
        }

        // TODO
    }

}
