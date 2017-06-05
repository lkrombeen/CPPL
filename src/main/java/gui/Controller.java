package gui;

import datastructure.NodeGraph;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * Created by Michael on 6/4/2017.
 */
public class Controller extends GridPane {
    private Label currentCenter, centerInput, radius;

    private TextField centerInputField;
    private static TextField radiusInputField, currentCenterField;

    private Button submitButton;

    private GraphScene graphScene;

    private EventHandler<ActionEvent> buttonAction = event -> {
        if (NodeGraph.getCurrentInstance() != null) {
            checkTextFields();
        }
    };

    /*package*/ Controller(FXElementsFactory fxElementsFactory, GraphScene graphScene) {
        this.graphScene = graphScene;
        currentCenter = fxElementsFactory.createLabel("Current center:");
        centerInput = fxElementsFactory.createLabel("Search center:");
        radius = fxElementsFactory.createLabel("Radius:");
        currentCenterField = new TextField();
        centerInputField = new TextField();
        radiusInputField = new TextField();
        submitButton = new Button("Submit");
        controllerSettings();
        this.add(currentCenter, 1, 1);
        this.add(currentCenterField, 2, 1);
        this.add(centerInput, 1, 2);
        this.add(centerInputField, 2, 2);
        this.add(radius, 1, 3);
        this.add(radiusInputField, 2, 3);
    }

    private void controllerSettings() {
        centerInputField.setMaxWidth(50d);
        radiusInputField.setMaxWidth(50d);
        currentCenterField.setMaxWidth(50d);
        currentCenterField.setEditable(false);
        submitButton.setOnAction(buttonAction);
    }

    private void checkTextFields() {
        if (centerInputField.getText().length() == 0 || centerInputField.getText().contains("\\D")
                || radiusInputField.getText().length() == 0 || radiusInputField.getText().contains("\\D")) {
            Window.errorPopup("Please enter an id or radius.");
        } else if (Integer.parseInt(centerInputField.getText()) < 0 ||
                Integer.parseInt(centerInputField.getText()) >= NodeGraph.getCurrentInstance().getSize()) {
            Window.errorPopup("Input center id is out of bounds, \nplease provide a different input id.");
        } else if (Integer.parseInt(radiusInputField.getText()) < 5 ||
                Integer.parseInt(radiusInputField.getText()) > 500) {
            Window.errorPopup("Input radius is out of bounds, \nplease provide a different radius.");
        } else {
            graphScene.drawGraph(Integer.parseInt(centerInputField.getText()), Integer.parseInt(radiusInputField.getText()));
            graphScene.switchToInfo();
        }
    }

    public static int getRadius() {
        if (radiusInputField.getText().length() == 0 || !radiusInputField.getText().contains("\\D")
                || Integer.parseInt(radiusInputField.getText()) < 5
                || Integer.parseInt(radiusInputField.getText()) > 500) {
            throw new IllegalArgumentException("Radius is invalid.");
        }
        return Integer.parseInt(radiusInputField.getText());
    }

    public static void setCurrentCenter(int center) {
        currentCenterField.setText(Integer.toString(center));
    }

}
