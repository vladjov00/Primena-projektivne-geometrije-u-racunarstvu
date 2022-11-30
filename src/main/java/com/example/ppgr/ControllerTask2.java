package com.example.ppgr;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

public class ControllerTask2 extends ButtonAction {

    public Button btChooseImageTransform, btChooseImage2;
    public Button btClear1, btClear2, btCompute, btTransformImageNaive;
    public Button btBackToMainMenu;
    public ImageView ivCoordinateSystem1, ivCoordinateSystem2, ivImage1, ivImage2;
    public TextField tfImage1, tfImage2;
    public TextField tfAx,tfAy,tfAz,tfBx,tfBy,tfBz,tfCx,tfCy,tfCz,tfDx,tfDy,tfDz,tfApx,tfApy,tfApz,tfBpx,tfBpy,tfBpz,tfCpx,tfCpy,tfCpz,tfDpx,tfDpy,tfDpz;
    public TextArea taResult;
    public AnchorPane window, apCoords1, apCoords2;


    private int[] counters;
    private Arrow arrowTransform;
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

    private static class ChooseImageButtonData {
        private ImageView iv;
        private TextField tf;
        private ChooseImageButtonID id;

        public ChooseImageButtonData(ImageView iv, TextField tf, ChooseImageButtonID id) {
            this.id = id;
            this.iv = iv;
            this.tf = tf;
        }

        public ChooseImageButtonID getId() {
            return id;
        }

        public ImageView getIv() {
            return iv;
        }

        public TextField getTf() {
            return tf;
        }
    }

    private enum ChooseImageButtonID {
        TRANSFORM
    }

    public void initialize() {
        Arrow arrow1 = new Arrow(400, 200, 500, 200, 15);
        Arrow arrow2 = new Arrow(1250, 40, 1300, 40, 5);
        Arrow arrow3 = new Arrow(1250, 90, 1300, 90, 5);
        Arrow arrow4 = new Arrow(1250, 140, 1300, 140, 5);
        Arrow arrow5 = new Arrow(1250, 190, 1300, 190, 5);
        arrowTransform = new Arrow(640, 388, 475, 388, 7);
        arrowTransform.setVisible(false);
        window.getChildren().addAll(arrow1, arrow2, arrow3, arrow4, arrow5, arrowTransform);
        counters = new int[4];
        TextField[] tfArray1 = {tfAx, tfAy, tfAz, tfBx, tfBy, tfBz, tfCx, tfCy, tfCz, tfDx, tfDy, tfDz};
        TextField[] tfArray2 = {tfApx, tfApy, tfApz, tfBpx, tfBpy, tfBpz, tfCpx, tfCpy, tfCpz, tfDpx, tfDpy, tfDpz};
        apCoords1.setUserData(new AnchorPaneData(0, Color.rgb(0, 255, 34), new Circle[4], tfArray1));
        apCoords2.setUserData(new AnchorPaneData(1, Color.rgb(40, 157, 245), new Circle[4], tfArray2, "'"));
        btClear1.setUserData(apCoords1);
        btClear2.setUserData(apCoords2);
        btCompute.setUserData(new AnchorPane[]{apCoords1, apCoords2});
        btChooseImageTransform.setUserData(new ChooseImageButtonData(ivCoordinateSystem1, tfImage1, ChooseImageButtonID.TRANSFORM));
        image1Chosen = false;
        image2Chosen = false;
    }

    public void backToMainMenuButtonPressed(ActionEvent e) throws IOException {
        backToMainMenu((Stage) ((Node) e.getSource()).getScene().getWindow());
    }

    public void chooseImageButtonPressed(ActionEvent e) {
        ChooseImageButtonData data = (ChooseImageButtonData) ((Node) e.getSource()).getUserData();
        File selectedFile = chooseImage(data.getIv(), data.getTf());
        if(selectedFile == null)
            return;

        if(data.getId() == ChooseImageButtonID.TRANSFORM) {
            arrowTransform.setVisible(true);
            btTransformImageNaive.setDisable(false);
            btTransformImageNaive.setVisible(true);
        }

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
        tfArray[counter*3].setText(String.valueOf(points[counter].getCenterX()));
        tfArray[counter*3+1].setText(String.valueOf(points[counter].getCenterY()));
        tfArray[counter*3+2].setText("1");

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
        double EPS = 1e-5;

        double Dx = Matrix.determinant3x3(new Matrix.Matrix3x3(vectors[3], vectors[1], vectors[2]));
        double Dy = Matrix.determinant3x3(new Matrix.Matrix3x3(vectors[0], vectors[3], vectors[2]));
        double Dz = Matrix.determinant3x3(new Matrix.Matrix3x3(vectors[0], vectors[1], vectors[3]));
        double D = Matrix.determinant3x3(new Matrix.Matrix3x3(vectors[0], vectors[1], vectors[2]));

        if(Math.abs(Dx) < EPS || Math.abs(Dy) < EPS || Math.abs(Dz) < EPS || Math.abs(D) < EPS) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Kolinearni vektori");
            alert.setHeaderText("UPOZORENJE!");
            alert.setContentText("Tacke moraju biti u opstem polozaju!");
            alert.show();
            return null;
        }

        return new double[]{Dx, Dy, Dz};
    }

    private Vector[] readVectorsFromTextFields(ActionEvent e) {
        TextField[] points1 = ((AnchorPaneData) ((AnchorPane[]) ((Node)e.getSource()).getUserData())[0].getUserData()).getTfArray();
        TextField[] points2 = ((AnchorPaneData) ((AnchorPane[]) ((Node)e.getSource()).getUserData())[1].getUserData()).getTfArray();

        for(TextField tf : points1) {
            if(Objects.equals(tf.getText(), "")) return null;
        }
        for(TextField tf : points2) {
            if(Objects.equals(tf.getText(), "")) return null;
        }

        Vector[] vectors = new Vector[8];

        for(int i = 0; i < 4; i++) {
            vectors[i] = new Vector(Double.parseDouble(points1[i*3].getText()),
                    Double.parseDouble(points1[i*3+1].getText()),
                    Double.parseDouble(points1[i*3+2].getText())
            );
            vectors[i+4] = new Vector(Double.parseDouble(points2[i*3].getText()),
                    Double.parseDouble(points2[i*3+1].getText()),
                    Double.parseDouble(points2[i*3+2].getText())
            );
        }

        return vectors;
    }

    public void computeTransformationMatrixNaive(ActionEvent e) {
        Vector[] vectors = readVectorsFromTextFields(e);
        if(vectors == null) return;

        Vector[] vectorsBefore = new Vector[4];
        System.arraycopy(vectors, 0, vectorsBefore, 0, 4);

        Vector[] vectorsAfter = new Vector[4];
        System.arraycopy(vectors, 4, vectorsAfter, 0, 4);

        double[] vP1 = solve3x3(vectorsBefore);
        if(vP1 == null) { return; }

        Matrix.Matrix3x3 P1 = new Matrix.Matrix3x3(vectorsBefore[0].multiplyBy(vP1[0]), vectorsBefore[1].multiplyBy(vP1[1]), vectorsBefore[2].multiplyBy(vP1[2])).reduce();

        double[] vP2 = solve3x3(vectorsAfter);
        if(vP2 == null) { return; }

        Matrix.Matrix3x3 P2 = new Matrix.Matrix3x3(vectorsAfter[0].multiplyBy(vP2[0]), vectorsAfter[1].multiplyBy(vP2[1]), vectorsAfter[2].multiplyBy(vP2[2])).reduce();

        Matrix.Matrix3x3 P;
        try {
            P = Matrix.multiply(P2, Matrix.inverse(P1).reduce());
            taResult.setText(P.reduce().toString());
        } catch (Exception ex) {
            taResult.setText("Matrica P1 nema inverz!");
        }
    }

    public void transformImage(ActionEvent e) {
        Matrix.Matrix3x3 A = Matrix.loadMatrix3x3FromText(taResult.getText());
        if(A == null)
            return;
        Image selectedImage = ivCoordinateSystem1.getImage();
        PixelReader pixelReaderOriginal = selectedImage.getPixelReader();

        int width = (int) selectedImage.getWidth();
        int height = (int) selectedImage.getHeight();

        WritableImage transformedImage = new WritableImage(width, height);
        PixelWriter pixelWriter = transformedImage.getPixelWriter();
        PixelReader pixelReaderTransform = transformedImage.getPixelReader();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = pixelReaderOriginal.getColor(x, y);
                Vector v = new Vector(x,y);
                Vector vp = A.multiplyBy(v).affinize(); // mnozenje piksela matricom transormacije da bi se dobile koordinate novog piksela
                pixelWriter.setColor(Math.max(0, Math.min(width-1, (int) vp.getX())),
                        Math.max(0, Math.min(height-1, (int) vp.getY())), color);
            }
        }
        for (int j = 1; j < height-1; j++) {    // interpolacija, uklanjanje praznih piksela
            for (int i = 1; i < width-1; i++) {
                if(checkIfTransparent(i, j, pixelReaderTransform) == 0) {
                    int count = checkIfTransparent(i-1, j-1, pixelReaderTransform) + checkIfTransparent(i-1, j, pixelReaderTransform) +
                            checkIfTransparent(i-1, j+1, pixelReaderTransform) + checkIfTransparent(i, j-1, pixelReaderTransform) +
                            checkIfTransparent(i, j+1, pixelReaderTransform) + checkIfTransparent(i+1, j-1, pixelReaderTransform) +
                            checkIfTransparent(i+1, j, pixelReaderTransform) + checkIfTransparent(i+1, j+1, pixelReaderTransform);
                    if(count != 0) {
                        double r = pixelReaderTransform.getColor(i-1, j-1).getRed() + pixelReaderTransform.getColor(i-1, j).getRed() +
                                pixelReaderTransform.getColor(i-1, j+1).getRed() +
                                pixelReaderTransform.getColor(i, j-1).getRed() + pixelReaderTransform.getColor(i, j+1).getRed() +
                                pixelReaderTransform.getColor(i+1, j-1).getRed() +
                                pixelReaderTransform.getColor(i+1, j).getRed() + pixelReaderTransform.getColor(i+1, j+1).getRed();
                        r/=count;
                        double g = pixelReaderTransform.getColor(i-1, j-1).getGreen() + pixelReaderTransform.getColor(i-1, j).getGreen() +
                                pixelReaderTransform.getColor(i-1, j+1).getGreen() +
                                pixelReaderTransform.getColor(i, j-1).getGreen() + pixelReaderTransform.getColor(i, j+1).getGreen() +
                                pixelReaderTransform.getColor(i+1, j-1).getGreen() +
                                pixelReaderTransform.getColor(i+1, j).getGreen() + pixelReaderTransform.getColor(i+1, j+1).getGreen();
                        g/=count;
                        double b = pixelReaderTransform.getColor(i-1, j-1).getBlue() + pixelReaderTransform.getColor(i-1, j).getBlue() +
                                pixelReaderTransform.getColor(i-1, j+1).getBlue() +
                                pixelReaderTransform.getColor(i, j-1).getBlue() + pixelReaderTransform.getColor(i, j+1).getBlue() +
                                pixelReaderTransform.getColor(i+1, j-1).getBlue() +
                                pixelReaderTransform.getColor(i+1, j).getBlue() + pixelReaderTransform.getColor(i+1, j+1).getBlue();
                        b/=count;

                        pixelWriter.setColor(i, j, new Color(r,g,b,1.0));
                    }
                }
            }
        }
        ivCoordinateSystem2.setImage(transformedImage);
    }


    private int checkIfTransparent(int x, int y, PixelReader pixelReader) {
        if(pixelReader.getColor(x, y).equals(Color.TRANSPARENT))
            return 0;
        return 1;
    }
}