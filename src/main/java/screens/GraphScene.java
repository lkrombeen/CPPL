package screens;

import datastructure.DrawNode;
import datastructure.DummyNode;
import datastructure.Node;
import datastructure.NodeGraph;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Implementation of the window that handles graph visualization.
 */
 public final class GraphScene extends Pane {

    /**
     * State for handling center queries.
     */
    private final INodeHandler center;

    /**
     * State for handling information display queries.
     */
    private final INodeHandler info;

    /**
     * The current state for node events.
     */
    private INodeHandler state;

    /**
     * The factory the GraphScene uses to create JavaFX elements.
     */
    private FXElementsFactory fxElementsFactory;

    /**
     * Event handler for when a node or edge is clicked.
     */
     private EventHandler<MouseEvent> click = event -> {

        if (event.getSource() instanceof DrawNode) {
            DrawNode rect = (DrawNode) (event.getSource());
            state.handle(rect);
        } else if (event.getSource() instanceof Line) {
            Line l = (Line) (event.getSource());
            String edgeNodes = l.getId();
            Window.getInfoScreen().getTextArea().appendText("Edge from node " + edgeNodes.substring(0, edgeNodes.indexOf("-")) + " to " + edgeNodes.substring(edgeNodes.indexOf("-") + 1, edgeNodes.length()) + "\n");
        }
     };

    /**
     * GraphScene pane constructor.
     * @param fact the Factory used to create JavaFX elements.
     */
     /*package*/ GraphScene(FXElementsFactory fact) {
         center = new NodeCenter(this);
         info = new NodeInfo();
         state = info;
         this.fxElementsFactory = fact;
     }

    /**
     * Draws graph on the screen.
     * @param id Id of the node/segment.
     * @param radius Radius.
     */
    public void drawGraph(final int id, final int radius) {
        this.getChildren().clear();
        drawGraphUtil(id, radius);
    }

    /**
     * The recursive method used to draw all nodes within the radius from the center node.
     * @param center The node to take as center.
     * @param radius The maximum depth we want to go.
     */
    private void drawGraphUtil(int center, int radius) {
        NodeGraph nodeGraph = NodeGraph.getCurrentInstance();
        nodeGraph.generateDrawNodes(center, radius);
        ArrayList<Node> nodes = nodeGraph.getNodes();
        LinkedList<DrawNode> drawNodes = nodeGraph.getDrawNodes();
        LinkedList<DummyNode> dummyNodes = nodeGraph.getDummyNodes();
        for (DrawNode dNode : drawNodes) {
            dNode.setX(dNode.getX() - dNode.getWidth() / 2);
            dNode.setOnMousePressed(click);
            this.getChildren().add(dNode);
            DrawNode nOut;
            for (int i : nodes.get(dNode.getIndex()).getOutgoingEdges()) {
                nOut = nodeGraph.getDrawNode(i);
                if (nOut != null && nOut.getBoundsInLocal().getMinX() - dNode.getBoundsInLocal().getMaxX() <= 100) {
                    drawLine(dNode.getIndex() + "-" + i, 2, dNode.getBoundsInLocal().getMaxX(), dNode.getBoundsInLocal().getMinY() + 5, nOut.getBoundsInLocal().getMinX(), nOut.getBoundsInLocal().getMinY() + 5);
                }
            }
        }

        DummyNode current;
        DummyNode current2;
        DrawNode dN;
        Set<DummyNode> visited = new HashSet<>();
        for (int i = dummyNodes.size() - 1; i >= 0; i--) {
            current = dummyNodes.get(i);
            if (!visited.contains(current)) {
                visited.add(current);
                if (!visited.contains(current.prevInEdge())) {
                    dN = nodeGraph.getDrawNode(current.getFrom());
                    if (dN != null) {
                        drawLine(current.getFrom() + "-" + current.getTo(), 2, dN.getBoundsInLocal().getMaxX(), dN.getBoundsInLocal().getMinY() + 5, current.getX(), current.getY() + 5);
                    }
                }
                for (int j = i; j >= 0; j--) {
                    current2 = dummyNodes.get(j);
                    if (current.nextInEdge(current2)) {
                        drawLine(current.getFrom() + "-" + current.getTo(), 2, current.getX(), current.getY() + 5, current2.getX(), current2.getY() + 5);
                    }
                }
                if (current.getId() == -1) {
                    dN = nodeGraph.getDrawNode(current.getTo());
                    if (dN != null) {
                        drawLine(current.getFrom() + "-" + current.getTo(), 2, current.getX(), current.getY() + 5, dN.getBoundsInLocal().getMinX(), dN.getBoundsInLocal().getMinY() + 5);
                    }
                }
            }
        }
    }
    /**
     * Draws new root nodes.
     * @param newNodes the new root nodes.
     * @param newDummies the dummy nodes needed to draw the nodes.
     */
    private void drawUpdateRoot(LinkedList<DrawNode> newNodes, LinkedList<DummyNode> newDummies) {
        NodeGraph nodeGraph = NodeGraph.getCurrentInstance();
        ArrayList<Node> nodes = nodeGraph.getNodes();
        for (DrawNode dNode : newNodes) {
            dNode.setX(dNode.getX() - dNode.getWidth() / 2);
            dNode.setOnMousePressed(click);
            this.getChildren().add(dNode);
            DrawNode nOut;
            for (int i : nodes.get(dNode.getIndex()).getOutgoingEdges()) {
                nOut = nodeGraph.getDrawNode(i);
                if (nOut != null && nOut.getBoundsInLocal().getMinX() - dNode.getBoundsInLocal().getMaxX() <= 100) {
                    drawLine(dNode.getIndex() + "-" + i, 2, dNode.getBoundsInLocal().getMaxX(), dNode.getBoundsInLocal().getMinY() + 5, nOut.getBoundsInLocal().getMinX(), nOut.getBoundsInLocal().getMinY() + 5);
                }
            }
        }
    }

    /**
     * Draws new leaf nodes.
     * @param newNodes the new leaf nodes.
     * @param newDummies the dummy nodes needed to draw the nodes.
     */
    private void drawUpdateLeaf(LinkedList<DrawNode> newNodes, LinkedList<DummyNode> newDummies) {
        NodeGraph nodeGraph = NodeGraph.getCurrentInstance();
        ArrayList<Node> nodes = nodeGraph.getNodes();
        for (DrawNode dNode : newNodes) {
            dNode.setX(dNode.getX() - dNode.getWidth() / 2);
            dNode.setOnMousePressed(click);
            this.getChildren().add(dNode);
            DrawNode nOut;
            for (int i : nodes.get(dNode.getIndex()).getIncomingEdges()) {
                nOut = nodeGraph.getDrawNode(i);
                if (nOut != null && nOut.getBoundsInLocal().getMinX() - dNode.getBoundsInLocal().getMaxX() <= 100) {
                    drawLine(i + "-" + dNode.getIndex(), 2, dNode.getBoundsInLocal().getMinX(), dNode.getBoundsInLocal().getMinY() + 5, nOut.getBoundsInLocal().getMaxX(), nOut.getBoundsInLocal().getMinY() + 5);
                }
            }
        }
    }

    /**
     * Zooms out on the scene.
     * @param transX x-coordinate of cursor
     * @param transY y-coordinate of cursor
     */
    public void zoomOut(double transX, double transY) {
        Pair<LinkedList<DrawNode>, LinkedList<DummyNode>> pLeafOut = NodeGraph.getCurrentInstance().addAtLeaf();
        Pair<LinkedList<DrawNode>, LinkedList<DummyNode>> pRootOut = NodeGraph.getCurrentInstance().addAtRoot();
        drawUpdateLeaf(pLeafOut.getKey(), pLeafOut.getValue());
        drawUpdateRoot(pRootOut.getKey(), pRootOut.getValue());
        setScaleX(getWidth() / (NodeGraph.getCurrentInstance().getDrawNodes().getFirst().getBoundsInLocal().getMaxX() - NodeGraph.getCurrentInstance().getDrawNodes().getLast().getX()));
        LinkedList<DrawNode> drawNodes = NodeGraph.getCurrentInstance().getDrawNodes();
        setTranslateX((-drawNodes.getLast().getX() + getWidth() / 2) * getScaleX() - getWidth() / 2);
    }

    /**
     * Zooms in on the scene.
     * @param transX x-coordinate of cursor
     * @param transY y-coordinate of cursor
     */
    public void zoomIn(double transX, double transY) {
        double maxX = NodeGraph.getCurrentInstance().removeAtLeaf();
        removeNodesLeaf(maxX);
        double minX = NodeGraph.getCurrentInstance().removeAtRoot();
        removeNodesRoot(minX);
        LinkedList<DrawNode> drawNodes = NodeGraph.getCurrentInstance().getDrawNodes();
        setScaleX(getWidth() / (drawNodes.getFirst().getBoundsInLocal().getMaxX() + 200 - drawNodes.getLast().getX()));
        setTranslateX((-drawNodes.getLast().getX() + getWidth() / 2) * getScaleX() - getWidth() / 2);
    }

    /**
     * Deletes root nodes to remove from the scene.
     * @param minX x-coordinate of nodes to remove.
     */
    private void removeNodesRoot(double minX) {
        ArrayList<javafx.scene.Node> remove = new ArrayList<>();
        for (javafx.scene.Node drawElement: this.getChildren()) {
            if (drawElement instanceof Rectangle) {
                Rectangle rect = (Rectangle) drawElement;
                if (rect.getX() < minX) {
                    remove.add(rect);
                }
            } else if (drawElement instanceof Line) {
                Line line = (Line) drawElement;
                if (line.getStartX() < minX) {
                    remove.add(line);
                }
            }
        }
        this.getChildren().removeAll(remove);
    }

    /**
     * Deletes leaf nodes to remove from the scene.
     * @param maxX x-coordinate of nodes to remove.
     */
    private void removeNodesLeaf(double maxX) {
        ArrayList<javafx.scene.Node> remove = new ArrayList<>();
        for (javafx.scene.Node drawElement: this.getChildren()) {
            if (drawElement instanceof Rectangle) {
                Rectangle rect = (Rectangle) drawElement;
                if (rect.getX() > maxX) {
                    remove.add(rect);
                }
            } else if (drawElement instanceof Line) {
                Line line = (Line) drawElement;
                if (line.getStartX() > maxX) {
                    remove.add(line);
                }
            }
        }
        this.getChildren().removeAll(remove);
    }

    /**
     * Draws a line in the graphscene.
     * @param id The id of the line.
     * @param width The width of the line.
     * @param startX The starting X coordinate of the line.
     * @param startY The starting Y coordinate of the line.
     * @param endX The ending X coordinate of the line.
     * @param endY The ending Y coordinate of the line.
     */
    private void drawLine(String id, double width, double startX, double startY, double endX, double endY) {
        Line l = new Line();
        l.setId(id);
        l.setStrokeWidth(width);
        l.setStartX(startX);
        l.setStartY(startY);
        l.setEndX(endX);
        l.setEndY(endY);
        l.setOnMousePressed(click);
        this.getChildren().add(l);
    }
    /**
     * Switches event handler to center queries.
     */
    public void switchToCenter() {
        state = center;
    }

    /**
     * Switches event handler to graph information handling.
     */
    public void switchToInfo() {
        state = info;
    }

    /**
     * Getter for the current state.
     * @return the current state.
     */
    public INodeHandler getState() {
        return this.state;
    }

    /**
     * Getter for the center-state.
     * @return the center state.
     */
    public INodeHandler getCenter() {
        return this.center;
    }

    /**
     * Getter for the info-state.
     * @return the info state.
     */
    public INodeHandler getInfo() {
        return this.info;
    }

}
