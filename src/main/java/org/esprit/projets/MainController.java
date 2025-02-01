package org.esprit.projets;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class MainController {

    @FXML
    private AnchorPane centerPane;

    /**
     * Loads the given FXML file into the centerPane and anchors it to all sides.
     */
    public void loadUI(String fxmlFile) {
        try {
            Node node = FXMLLoader.load(getClass().getResource(fxmlFile));
            centerPane.getChildren().setAll(node);
            // Anchor the loaded node to all sides
            AnchorPane.setTopAnchor(node, 0.0);
            AnchorPane.setBottomAnchor(node, 0.0);
            AnchorPane.setLeftAnchor(node, 0.0);
            AnchorPane.setRightAnchor(node, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        // Load a default view (AdminView)
        loadUI("AdminView.fxml");
    }

    @FXML
    public void goToAdminView(ActionEvent event) {
        loadUI("AdminView.fxml");
    }

    @FXML
    public void goToUserView(ActionEvent event) {
        loadUI("UserView.fxml");
    }

    // Optionally, a method to completely switch scenes if needed:
    public void switchScene(ActionEvent event, String fxmlFile) throws IOException {
        Node root = FXMLLoader.load(getClass().getResource(fxmlFile));
        ((AnchorPane)((Node) event.getSource()).getScene().getRoot()).getChildrenUnmodifiable().setAll(root);
    }
}
