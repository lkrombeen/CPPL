package window;

import datastructure.Node;
import datastructure.NodeGraph;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Button;
import javafx.fxml.FXMLLoader;
import javafx.scene.shape.Rectangle;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.Pane;
import javafx.event.EventHandler;

import java.io.IOException;
import java.io.File;
import javafx.util.Pair;
import parsing.Parser;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by 101010 on 8-5-2017.
 */
public class Controller {

    private NodeGraph graph;

    /**
     * Main frame making up the window.
     */
    @FXML private Pane mainPane;

    /**
     * Button when clicked allows the user to browse to gfa file in directory.
     */
    @FXML private Button browse;

    /**
     * The menu bar.
     */
    @FXML private MenuBar menu;

    /**
     * TextArea used to print information about the running process.
     */
    @FXML private TextArea console;

    EventHandler<MouseEvent> rectClick = event -> {

        if( event.getSource() instanceof Node) {
            Node rect = (Node) (event.getSource());
            System.out.println(NodeGraph.getCurrentInstance().getSegment(NodeGraph.getCurrentInstance().getId(rect)));
        }
    };

    /**
     * Handling file opening when the open button in the menu bar is clicked.
     * @param event The event holding information about what the user did in the program.
     * @throws IOException An Exception is thrown when the file can not be found.
     */
    @FXML private void openFile(final ActionEvent event) throws IOException {
        Stage stage = (Stage) menu.getScene().getWindow();
        Parent root;
        final Button openButton = new Button("Open");
        File file = FileSelector.showOpenDialog(stage);
        mainPane.getChildren().add(new Rectangle(mainPane.getWidth(), 100));
    }

    /**
     * Handling file opening when browse button is clicked. Also creates the main program window and opens that window.
     * @param event The event holding information about what the user did in the program.
     * @throws IOException An Exception is thrown when the file can not be found.
     */
    @FXML private void handleBrowseButton(final ActionEvent event) throws IOException {
        Stage stage = (Stage) browse.getScene().getWindow();
        Parent root;
        if (event.getSource() == browse) {
            final Button openButton = new Button("Open");
            File file = FileSelector.showOpenDialog(stage);
            if (file != null) {
                stage.setTitle("Graph visualization");
                NodeGraph.setCurrentInstance(Parser.getInstance().parse(file));
                //drawGraph();
                root = null;
                try {
                     root = FXMLLoader.load(getClass().getResource("/window.fxml"));
                } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(new Scene(root, 1600, 900));
            stage.show();
            }
        }
    }

    /**
     * Handling printing the console when a node is clicked.
     * @param event Information about the mouse action.
     */
    @FXML protected void handleNode(MouseEvent event) {
        console.appendText("[" + new SimpleDateFormat("HH:mm").format(System.currentTimeMillis()) + "] - " + "ATCTGGTTCATG\n");
    }

    /**
     * Handling clearing the console when the clear button is clicked in the menu bar.
     * @param event Information about the event.
     */
    @FXML protected void clearConsole(ActionEvent event) {
        console.clear();
    }

    /**
     * Handling exiting the application.
     * @param event Information about the event.
     */
    @FXML protected void exitApp(ActionEvent event) {
        Platform.exit();
    }

    /**
     * Draw method for drawing the graph.
     * @param center The node that will be the center of the drawn graph.
     * @param radius The maximum depth we want to draw.
     */
    @FXML
    public void drawGraph() {
        Set<Node> visited = new HashSet<>();
        int depth = 0;

        drawGraphUtil(visited, NodeGraph.getCurrentInstance().getNode(1), 10, depth, new Pair<>((mainPane.getWidth() / 2) - 115, (mainPane.getHeight() / 2)), true);
    }

    /**
     * The recursive method used to draw all nodes within the radius from the center node.
     * @param visited Set of visited nodes we do not need to visit again.
     * @param current The current node we want to draw.
     * @param radius The maximum depth we want to go.
     * @param depth The current depth we are on.
     * @param location The current location we are drawing on.
     * @param direction True if we went from parent to child and false visa versa.
     */
    private void drawGraphUtil(Set<Node> visited, Node current, int radius, int depth, Pair<Double, Double> location, boolean direction) {
        if (depth <= radius && !visited.contains(current)) {
            if (direction) {
                location = new Pair<>(location.getKey() + 100, location.getValue());
            } else {
                location = new Pair<>(location.getKey() - 100, location.getValue());
            }
            current.setOnMousePressed(rectClick);
            current.setX(location.getKey());
            current.setY(location.getValue());
            current.setWidth(50);
            current.setHeight(10);

            mainPane.getChildren().add(current);

            visited.add(current);

            for (Integer i : current.getOutgoingEdges()) {
                drawGraphUtil(visited, NodeGraph.getCurrentInstance().getNode(i), radius, depth + 1, location, true);
            }
            for (Integer i : current.getIncomingEdges()) {
                drawGraphUtil(visited, NodeGraph.getCurrentInstance().getNode(i), radius, depth + 1, location, false);
            }
        }
    }
}
