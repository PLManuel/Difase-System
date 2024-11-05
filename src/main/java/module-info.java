module com.difase.system {
    requires javafx.controls;
    requires java.base;
    requires javafx.fxml;

    requires transitive kernel;
    requires transitive io;
    requires transitive layout;
    requires transitive javafx.graphics;
    requires java.desktop;

    opens com.difase.system to javafx.fxml;

    exports com.difase.system;
}
