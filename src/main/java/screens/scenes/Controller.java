package screens.scenes;

import datastructure.GenomeCountCondition;
import datastructure.NodeGraph;
import datastructure.RegexCondition;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import screens.GraphInfo;
import screens.Window;
import services.ServiceLocator;

/**
 * Controller class to allow input that will be used for interacting with the graph.
 */
public class Controller extends GridPane {
    /**
     * Labels.
     */
    @SuppressWarnings("FieldCanBeLocal")
    private Label currentCenter, centerInput, radius, genomeNum, genomeRegex;

    /**
     * Input fields.
     */
    private static TextField centerInputField, radiusInputField, currentCenterField, numGenomesField, regexGenomesField;

    /**
     * Submit button to initiate center queries.
     */
    private Button submitButton;

    private Button submitButtonNoGen;

    private Button submitRegex;

    private ChoiceBox choices;

    /**
     * Scene where the graph is drawn.
     */
    private GraphScene graphScene;

    /**
     * Event handler that checks content in the text fields in this object.
     */
    private EventHandler<ActionEvent> buttonAction = event -> {
        if (NodeGraph.getCurrentInstance() != null) {
            checkTextFields();
        }
    };

    private EventHandler<ActionEvent> regexAction = event -> {
        String regex = regexGenomesField.getText();
        RegexCondition regCond = new RegexCondition(regex, GraphInfo.getInstance().determineColor());
        GraphInfo.getInstance().addCondition(regCond);
        graphScene.drawConditions();
    };

    private EventHandler<ActionEvent> numGenomeAction = event -> {
        if (numGenomesField.getText().length() == 0 || numGenomesField.getText().contains("\\D")) {
            Window.errorPopup("Please enter a number as number of genomes.");
        } else {
            int number = Integer.parseInt(numGenomesField.getText());
            if (number < 0 || number >= GraphInfo.getInstance().getGenomesNum()) {
                Window.errorPopup("Input number is out of bounds, \nplease provide a different input between 0 and : " + GraphInfo.getInstance().getGenomesNum() + ".");
            } else {
                int index = choices.getSelectionModel().getSelectedIndex();
                GenomeCountCondition gcc;
                Color color = GraphInfo.getInstance().determineColor();
                switch (index) {
                    case 0:
                        gcc = new GenomeCountCondition(number, true, false, color);
                        GraphInfo.getInstance().addCondition(gcc);
                        break;
                    case 1:
                        gcc = new GenomeCountCondition(number, false, false, color);
                        GraphInfo.getInstance().addCondition(gcc);
                        break;
                    case 2:
                        gcc = new GenomeCountCondition(number, true, true, color);
                        GraphInfo.getInstance().addCondition(gcc);
                        break;
                    case 3:
                        gcc = new GenomeCountCondition(number, false, true, color);
                        GraphInfo.getInstance().addCondition(gcc);
                        break;
                    default:
                        Window.errorPopup("Please select a constraint.");
                        break;
                }
                graphScene.drawConditions();
            }
        }
    };




    /**
     * Constructor.
     * @param serviceLocator ServiceLocator for locating services registered in that object.
     */
    /*package*/ Controller(ServiceLocator serviceLocator) {
        this.graphScene = serviceLocator.getGraphScene();
        currentCenter = serviceLocator.getFxElementsFactory().createLabel("Current center:");
        centerInput = serviceLocator.getFxElementsFactory().createLabel("Search center:");
        radius = serviceLocator.getFxElementsFactory().createLabel("Radius:");
        genomeNum = serviceLocator.getFxElementsFactory().createLabel("No. of\ngenomes:");
        genomeRegex = serviceLocator.getFxElementsFactory().createLabel("Genome\nregex:");
        currentCenterField = new TextField();
        centerInputField = new TextField();
        radiusInputField = new TextField();
        numGenomesField = new TextField();
        regexGenomesField = new TextField();

        submitButton = new Button("Submit");
        submitButtonNoGen = new Button("Submit");
        submitRegex = new Button("Submit");
        choices = new ChoiceBox(FXCollections.observableArrayList(">", "<", ">=", "<="));
        controllerSettings();
        this.add(currentCenter, 1, 1);
        this.add(currentCenterField, 2, 1);
        this.add(centerInput, 1, 2);
        this.add(centerInputField, 2, 2);
        this.add(radius, 1, 3);
        this.add(radiusInputField, 2, 3);
        this.add(submitButton, 1, 4);
        this.add(genomeNum, 1, 6);
        this.add(choices, 2, 6);
        this.add(numGenomesField, 2, 7);
        this.add(submitButtonNoGen, 1, 7);
        this.add(genomeRegex, 1, 9);
        this.add(regexGenomesField, 2, 9);
        this.add(submitRegex, 1, 10);
        serviceLocator.setController(this);
    }

    /**
     * Settings for the fields and buttons are set.
     */
    private void controllerSettings() {
        centerInputField.setMaxWidth(75d);
        radiusInputField.setMaxWidth(75d);
        currentCenterField.setMaxWidth(75d);
        numGenomesField.setMaxWidth(75d);
        regexGenomesField.setMaxWidth(75d);
        currentCenterField.setEditable(false);
        submitButton.setOnAction(buttonAction);
        submitButtonNoGen.setOnAction(numGenomeAction);
        submitRegex.setOnAction(regexAction);
        this.getStyleClass().addAll("grid", "border_bottom");
    }

    /**
     * Checks the content of center node field and radius field.
     */
    private void checkTextFields() {
        if (centerInputField.getText().length() == 0 || centerInputField.getText().contains("\\D")
                || radiusInputField.getText().length() == 0 || radiusInputField.getText().contains("\\D")) {
            Window.errorPopup("Please enter an id or radius.");
        } else if (Integer.parseInt(centerInputField.getText()) < 0
                || Integer.parseInt(centerInputField.getText()) >= NodeGraph.getCurrentInstance().getSize()) {
            Window.errorPopup("Input center id is out of bounds, \nplease provide a different input id.");
        } else if (Integer.parseInt(radiusInputField.getText()) < 5
                || Integer.parseInt(radiusInputField.getText()) > 500) {
            Window.errorPopup("Input radius is out of bounds, \nplease provide a different radius.");
        } else {
            graphScene.drawGraph(Integer.parseInt(centerInputField.getText()), Integer.parseInt(radiusInputField.getText()));
            graphScene.switchToInfo();
        }
    }

    /**
     * Returns the radius in its text field.
     * @return Radius.
     */
    public int getRadius() {
        if (radiusInputField.getText().length() == 0 || !radiusInputField.getText().contains("\\D")
                || Integer.parseInt(radiusInputField.getText()) < 5
                || Integer.parseInt(radiusInputField.getText()) > 500) {
            throw new IllegalArgumentException("Radius is invalid.");
        }
        return Integer.parseInt(radiusInputField.getText());
    }

    /**
     * Enters the index of the current center node in its corresponding text field.
     * @param center Index of the center node.
     */
    public void setCurrentCenter(int center) {
        currentCenterField.setText(Integer.toString(center));
    }

}

