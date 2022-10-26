module com.example.nevidljivo_teme {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.nevidljivo_teme to javafx.fxml;
    exports com.example.nevidljivo_teme;
}