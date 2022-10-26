package com.example.nevidljivo_teme;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import java.io.File;
import java.util.Scanner;


public class Controller {
    @FXML

    private Boolean imageChosen;
    private int tfCounter = 0;
    public ImageView imageView;
    public TextField textField1;
    public TextField textField2;
    public TextField textField3;
    public TextField textField4;
    public TextField textField5;
    public TextField textField6;
    public TextField textField7;
    public TextField tfImage;
    public TextField tfResult;
    public Label lblClick7;
    public AnchorPane box;
    private final TextField[] textFields = new TextField[7];

    public void initialize() {
        textFields[0] = textField1;
        textFields[1] = textField2;
        textFields[2] = textField3;
        textFields[3] = textField4;
        textFields[4] = textField5;
        textFields[5] = textField6;
        textFields[6] = textField7;
        imageChosen = false;
    }

    private class Vektor {
        private double x, y, z;

        public Vektor(double x, double y) {
            this.x = x;
            this.y = y;
            this.z = 1;
        }

        public Vektor(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ", " + z + ")";
        }
        
        public Vektor cross(Vektor v2) {
            return new Vektor(this.y * v2.z - this.z * v2.y,
                    -this.x * v2.z + this.z * v2.x,
                    this.x * v2.y - this.y * v2.x);
        }

        public Vektor afinize() {
            return new Vektor(Math.round(x/z), Math.round(y/z), z/z);
        }
    }

    public void chooseImage(ActionEvent e) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Open Image");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));

        File selectedFile = fc.showOpenDialog(null);

        Image img = new Image(selectedFile.toURI().toString());
        imageView.setImage(img);
        imageChosen = true;

        tfImage.setText(selectedFile.toURI().toString());
    }

    private void addPoint(double x, double y, Color color){
        Circle circle = new Circle();
        circle.setCenterX(x + imageView.getLayoutX());
        circle.setCenterY(y + imageView.getLayoutY());
        circle.setRadius(5);
        circle.setFill(color);
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(2);
        box.getChildren().add(circle);
        circle.toFront();
    }

    public void clickOnImage(MouseEvent e) {
        if (tfCounter == 7 || !imageChosen)
            return;

        textFields[tfCounter].setText("x: " + e.getX() + " ; y: " + e.getY());
        tfCounter++;

        addPoint(e.getX(), e.getY(), Color.rgb(0, 255, 34));
    }

    public void clearTextFields(ActionEvent e){
        tfCounter = 0;
        for (TextField tf : textFields) {
            tf.setText("");
        }
        Object[] list = box.getChildren().toArray();
        for (Object obj : list) {
            if(obj.getClass().equals(Circle.class))
                box.getChildren().remove(obj);
        }
        tfResult.setText("");
    }

    public void computeInvisiblePoint(ActionEvent e){
        if(tfCounter < 7) {
            lblClick7.setVisible(true);
            return;
        }
        lblClick7.setVisible(false);

        Vektor[] tacke = new Vektor[7];

        for (int i = 0; i < 7; i++) {
            String str = textFields[i].getText();
            Scanner sc = new Scanner(str);
            sc.next();
            double x = sc.nextDouble();
            sc.next(); sc.next();
            double y = sc.nextDouble();

            tacke[i] = new Vektor(x, y);

            sc.close();
        }

        // ovde se vrsi izracunavanje polozaja tacke 4
        Vektor prava51 = tacke[0].cross(tacke[3]);
        Vektor prava62 = tacke[1].cross(tacke[4]);
        Vektor tackaXb = prava51.cross(prava62);
        Vektor prava21 = tacke[0].cross(tacke[1]);
        Vektor prava78 = tacke[6].cross(tacke[5]);
        Vektor tackaYb = prava21.cross(prava78);
        Vektor pravaXb8 = tackaXb.cross(tacke[6]);
        Vektor pravaYb3 = tackaYb.cross(tacke[2]);
        Vektor tacka4 = pravaXb8.cross(pravaYb3).afinize();

        addPoint(tacka4.x, tacka4.y, Color.RED);

        tfResult.setText("x: " + tacka4.x + " ; y: " + tacka4.y);
    }
}


