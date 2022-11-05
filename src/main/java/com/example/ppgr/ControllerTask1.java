package com.example.ppgr;

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


public class ControllerTask1 extends ButtonAction {
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

    public void chooseImageButtonPressed() {
        chooseImage(imageView, tfImage);
        imageChosen = true;
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

        Vector[] tacke = new Vector[7];

        for (int i = 0; i < 7; i++) {
            String str = textFields[i].getText();
            Scanner sc = new Scanner(str);
            sc.next();
            double x = sc.nextDouble();
            sc.next(); sc.next();
            double y = sc.nextDouble();

            tacke[i] = new Vector(x, y);

            sc.close();
        }

        // ovde se vrsi izracunavanje polozaja tacke 4
        Vector prava51 = tacke[0].cross(tacke[3]);
        Vector prava62 = tacke[1].cross(tacke[4]);
        Vector prava73 = tacke[2].cross(tacke[5]);
        Vector tackaXb1 = prava51.cross(prava62);
        Vector tackaXb2 = prava62.cross(prava73);
        Vector tackaXb3 = prava51.cross(prava73);

        // tacka Xb se racuna kao aritmeticka sredina dobijenih preseka
        Vector tackaXb = tackaXb1.add(tackaXb2).add(tackaXb3).divideBy(3.0);

        Vector prava21 = tacke[0].cross(tacke[1]);
        Vector prava78 = tacke[6].cross(tacke[5]);
        Vector prava65 = tacke[3].cross(tacke[4]);
        Vector tackaYb1 = prava21.cross(prava78);
        Vector tackaYb2 = prava65.cross(prava78);
        Vector tackaYb3 = prava21.cross(prava65);

        // tacka Yb se racuna kao aritmeticka sredina dobijenih preseka
        Vector tackaYb = tackaYb1.add(tackaYb2).add(tackaYb3).divideBy(3.0);

        // tacka 4 se racuna kao presek pravih Xb8 i Yb3
        Vector pravaXb8 = tackaXb.cross(tacke[6]);
        Vector pravaYb3 = tackaYb.cross(tacke[2]);
        Vector tacka4 = pravaXb8.cross(pravaYb3).affinize();

        addPoint(tacka4.getX(), tacka4.getY(), Color.RED);

        tfResult.setText("x: " + tacka4.getX() + " ; y: " + tacka4.getY());
    }
}


