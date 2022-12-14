package com.example.ppgr;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Class containing utility functions
 */
public class Util {

    /**
     * Reads a file and returns its content as string
     * @param fileName Name of the file
     * @return Content of the file as string
     * @throws IOException
     */
    public static String readFileAsString(String fileName) throws IOException {
        String data;
        data = new String(Files.readAllBytes(Paths.get(fileName)));
        return data;
    }

    /**
     * Sets the stage to the center of the screen
     * @param stage
     */
    public static void setStageOnCenter(Stage stage) {
        Rectangle2D screen = Screen.getPrimary().getBounds();
        double SCR_WIDTH = screen.getWidth();
        double SCR_HEIGHT = screen.getHeight();
        stage.setX((SCR_WIDTH - stage.getScene().getWidth()) / 2);
        stage.setY((SCR_HEIGHT - stage.getScene().getHeight()) / 2);
    }


    /** sqrt(a^2 + b^2) without under/overflow. **/
    public static double hypot(double a, double b) {
        double r;
        if (Math.abs(a) > Math.abs(b)) {
            r = b/a;
            r = Math.abs(a)*Math.sqrt(1+r*r);
        } else if (b != 0) {
            r = a/b;
            r = Math.abs(b)*Math.sqrt(1+r*r);
        } else {
            r = 0.0;
        }
        return r;
    }
}
