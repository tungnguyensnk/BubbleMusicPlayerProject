module com.example.musicplaydemo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.desktop;
    requires core;
    requires minim;
    requires unirest.java;
    requires java.sql;

    opens com.example.musicplaydemo to javafx.fxml;
    exports com.example.musicplaydemo;
}