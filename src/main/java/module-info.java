module com.example.ppgr_project {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.ppgr to javafx.fxml;
    exports com.example.ppgr;
}