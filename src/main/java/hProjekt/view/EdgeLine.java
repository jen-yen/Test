package hProjekt.view;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import hProjekt.model.Edge;
import hProjekt.model.EdgeImpl;
import hProjekt.model.Player;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;

/**
 * A Line that represents an {@link EdgeImpl}. Has methods to highlight and
 * unhighlight itself.
 */
public class EdgeLine extends Line {
    private final Edge edge;
    private double distance = 0;
    private final int strokeWidth = 5;
    private final double positionOffset = 10;
    private final Line outline = new Line();
    private final Label label = new Label();

    /**
     * Creates a new EdgeLine for the given {@link EdgeImpl}.
     *
     * @param edge the edge to represent
     */
    public EdgeLine(final Edge edge) {
        this.edge = edge;
        outline.startXProperty().bind(startXProperty());
        outline.startYProperty().bind(startYProperty());
        outline.endXProperty().bind(endXProperty());
        outline.endYProperty().bind(endYProperty());
        outline.strokeDashOffsetProperty().bind(strokeDashOffsetProperty());
        outline.setStrokeWidth(strokeWidth * 1.4);
        outline.setStroke(Color.TRANSPARENT);
        getStrokeDashArray().subscribe(() -> {
            outline.getStrokeDashArray().setAll(getStrokeDashArray());
        });
        setMouseTransparent(true);
        outline.setMouseTransparent(true);
        label.getStyleClass().add("highlighted-label");
        label.setMouseTransparent(true);
    }

    /**
     * Returns the {@link Edge} this EdgeLine represents.
     *
     * @return the edge
     */
    public Edge getEdge() {
        return edge;
    }

    /**
     * Returns the outline of the EdgeLine.
     *
     * @return the outline
     */
    public List<Node> getOutline() {
        return List.of(outline, label);
    }

    /**
     * Initializes the EdgeLine with a dashScale of 1.
     *
     * @see #init(double)
     */
    public void init() {
        init(1);
    }

    /**
     * Initializes the EdgeLine with the given dashScale.
     *
     * @param dashScale factor to scale the dash length by
     */
    public void init(final double dashScale) {
        this.distance = new Point2D(getStartX(), getStartY()).distance(getEndX(), getEndY());
        if (edge.hasRail()) {
            final List<Player> railOwners = edge.getRailOwners().stream()
                    .sorted((p1, p2) -> Integer.compare(p1.getID(), p2.getID())).toList();
            double offset = 1.0 / railOwners.size();
            List<Stop> stops = new ArrayList<>();
            for (int i = 0; i < railOwners.size(); i++) {
                final Player player = railOwners.get(i);
                stops.addAll(
                        List.of(new Stop(i * offset, player.getColor()),
                                new Stop((i + 1) * offset - 0.02, player.getColor())));
                if (i < railOwners.size() - 1) {
                    stops.addAll(List.of(new Stop((i + 1) * offset - 0.02, Color.BLACK),
                            new Stop((i + 1) * offset, Color.BLACK)));
                }
            }

            LinearGradient lg1 = new LinearGradient(
                    getStartX(), getStartY(), getEndX(), getEndY(),
                    false,
                    CycleMethod.NO_CYCLE, stops);
            setStroke(lg1);
        } else {
            setStroke(Color.TRANSPARENT);
        }
        setStrokeWidth(strokeWidth);
        setStrokeDashOffset(-positionOffset / 2);
        getStrokeDashArray().clear();
        getStrokeDashArray().add((distance - positionOffset) * dashScale);
        if (edge.hasRail()) {
            outline.setStroke(Color.BLACK);
        }
    }

    /**
     * Sets the label of the EdgeLine.
     *
     * @param text the text to set the label to
     */
    public void setLabel(final String text) {
        label.setVisible(true);
        label.setText(text);
        label.setLayoutX(((getStartX() + getEndX()) / 2) - label.getWidth() / 2);
        label.setLayoutY(((getStartY() + getEndY()) / 2) - label.getHeight() / 2);
        label.toFront();
    }

    /**
     * Sets the label of the EdgeLine to the given costs.
     * Format: "x1, x2, x3, ..."
     *
     * @param costs the costs to set the label to
     */
    public void setCostLabel(Integer... costs) {
        String text = "";
        for (int i = 0; i < costs.length; i++) {
            if (costs[i] == 0) {
                continue;
            }
            if (i > 0) {
                text += " + ";
            }
            text += String.format(" %d ", costs[i]);
        }
        setLabel(text);
    }

    /**
     * Hides the label of the EdgeLine.
     */
    public void hideLabel() {
        label.setVisible(false);
    }

    /**
     * Highlights the EdgeLine without a click handler.
     */
    public void highlight() {
        init();
        outline.getStyleClass().add("selected");
        outline.setStrokeWidth(strokeWidth * 2);
    }

    /**
     * Highlights the EdgeLine with the given handler.
     *
     * @param handler the handler to call when the EdgeLine is clicked
     */
    public void highlight(final Consumer<MouseEvent> handler) {
        init(0.1);
        outline.setStroke(Color.GRAY);
        outline.setStrokeWidth(strokeWidth * 2);
        outline.getStyleClass().add("selectable");
        getStrokeDashArray().add(10.0);
        // setStrokeWidth(strokeWidth * 1.2);
        outline.setOnMouseClicked(handler::accept);
        outline.setMouseTransparent(false);
    }

    /**
     * Highlights the EdgeLine and sets a handler to deselect it.
     * The EdgeLine gets a different style class to indicate that it is selected.
     *
     * @param deselectHandler the handler to call when the EdgeLine is deselected
     */
    public void selected(final Consumer<MouseEvent> deselectHandler) {
        highlight(event -> {
            outline.getStyleClass().remove("selected");
            deselectHandler.accept(event);
        });
        outline.getStyleClass().add("selected");
    }

    /**
     * Removes the highlight from the EdgeLine.
     */
    public void unhighlight() {
        outline.setStroke(Color.TRANSPARENT);
        outline.setStrokeWidth(strokeWidth * 1.4);
        outline.setOnMouseClicked(null);
        outline.getStyleClass().clear();
        init();
        outline.setMouseTransparent(true);
    }
}
