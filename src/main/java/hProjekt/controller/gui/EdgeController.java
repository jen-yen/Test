package hProjekt.controller.gui;

import java.util.function.Consumer;

import hProjekt.model.Edge;
import hProjekt.view.EdgeLine;
import javafx.scene.input.MouseEvent;

/**
 * The controller for an edge.
 */
public class EdgeController {
    private final EdgeLine line;

    /**
     * Creates a new edge controller.
     *
     * @param edge the edge to render
     */
    public EdgeController(final Edge edge) {
        this.line = new EdgeLine(edge);
    }

    /**
     * Returns the edge.
     *
     * @return the edge
     */
    public Edge getEdge() {
        return line.getEdge();
    }

    /**
     * Returns the edge line.
     *
     * @return the edge line
     */
    public EdgeLine getEdgeLine() {
        return line;
    }

    /**
     * Highlights the edge and calls the given handler when the edge is clicked.
     * Overwrites any existing click handler.
     *
     * @param handler the handler to call when the edge is clicked
     */
    public void highlight(final Consumer<MouseEvent> handler) {
        line.highlight(handler);
    }

    /**
     * Highlights the edge without a click handler.
     */
    public void highlight() {
        line.highlight();
    }

    /**
     * Highlights the edge as selected and calls the given handler when the edge is
     * clicked.
     * Overwrites any existing click handler.
     *
     * @param deselectHandler the handler to call when the edge is deselected
     */
    public void selected(final Consumer<MouseEvent> deselectHandler) {
        line.selected(deselectHandler);
    }

    /**
     * Sets the label of the edge.
     *
     * @param text the text to set
     */
    public void setLabel(final String text) {
        line.setLabel(text);
    }

    /**
     * Sets the cost label of the edge.
     * The costs are formatted as "x1, x2, x3, ...".
     *
     * @param costs the costs to set
     */
    public void setCostLabel(Integer... costs) {
        line.setCostLabel(costs);
    }

    /**
     * Hides the label of the edge.
     */
    public void hideLabel() {
        line.hideLabel();
    }

    /**
     * Unhighlights the edge and removes the click handler.
     */
    public void unhighlight() {
        line.unhighlight();
    }
}
