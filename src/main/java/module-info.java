module org.esprit.projets {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    // Opening packages to JavaFX
    opens org.esprit.projets to javafx.fxml;
    opens org.esprit.projets.entity to javafx.base;

    // Exporting the main package
    exports org.esprit.projets;
    exports org.esprit.projets.entity;
}
