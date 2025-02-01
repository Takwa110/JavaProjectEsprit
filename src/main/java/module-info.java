module com.example.easyquiz {
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.easyquiz to javafx.fxml;
    exports com.example.easyquiz;
}
