module com.example.nevidljivo_teme {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.ppgr to javafx.fxml;
    exports com.example.ppgr;
}