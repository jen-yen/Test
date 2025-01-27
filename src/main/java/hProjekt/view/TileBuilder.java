package hProjekt.view;

import java.util.function.Consumer;

import hProjekt.model.Tile;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Builder;

/**
 * A Builder to create views for {@link Tile}s.
 * Renders the {@link Tile} as a hexagon.
 * Has methods to highlight and unhighlight the tile.
 */
public class TileBuilder implements Builder<Region> {
    private final Tile tile;
    private final StackPane pane = new StackPane();
    private boolean hasMouseClickedHandler = false;

    /**
     * Creates a new TileBuilder for the given {@link Tile}.
     *
     * @param tile the tile to render
     */
    public TileBuilder(final Tile tile) {
        this.tile = tile;
        styleAndSizeTile(pane);
    }

    /**
     * Returns the {@link Tile} this builder renders.
     *
     * @return the tile
     */
    public Tile getTile() {
        return tile;
    }

    @Override
    public Region build() {
        pane.getChildren().clear();

        final VBox mainBox = new VBox(5); // Add spacing between labels
        mainBox.setAlignment(Pos.CENTER);

        final Label positionLabel = createPositionLabel();
        final Label resourceLabel = createResourceLabel();

        // Uncomment to display position and resource labels. Makes everything quite
        // laggy
        // mainBox.getChildren().addAll(positionLabel, resourceLabel);
        pane.getChildren().addAll(mainBox);

        return pane;
    }

    /**
     * Styles and resizes the tile so it is rendered as a hexagon via CSS.
     *
     * @param stackPane the pane to style
     */
    private void styleAndSizeTile(final StackPane stackPane) {
        stackPane.getStylesheets().add("css/hexmap.css");
        stackPane.getStyleClass().add("hex-tile");
        stackPane.maxHeightProperty().bind(tile.heightProperty());
        stackPane.maxWidthProperty().bind(tile.widthProperty());
        stackPane.minHeightProperty().bind(tile.heightProperty());
        stackPane.minWidthProperty().bind(tile.widthProperty());
        stackPane.setBackground(Background.fill(tile.getType().color));
    }

    /**
     * Creates a label displaying the tile's position in the format (q, r, s).
     *
     * @return the position label
     */
    private Label createPositionLabel() {
        final Label positionLabel = new Label(tile.getPosition().toString());
        positionLabel.setFont(new Font("Arial", 12));
        positionLabel.getStyleClass().add("tile-position-label");
        return positionLabel;
    }

    /**
     * Creates a label displaying the tile's type (e.g., PLAIN, MOUNTAIN).
     *
     * @return the type label
     */
    private Label createResourceLabel() {
        final Label resourceLabel = new Label(tile.getType().toString());
        resourceLabel.setFont(new Font("Arial", 12));
        resourceLabel.getStyleClass().add("tile-resource-label");
        return resourceLabel;
    }

    /**
     * Highlights the tile and sets a handler for mouse clicks.
     *
     * @param handler the handler to call when the tile is clicked
     */
    public void highlight(final Runnable handler) {
        pane.getStyleClass().addAll("selectable");
        setMouseClickedHandler(e -> handler.run());
    }

    /**
     * Removes the highlight and the handler for mouse clicks.
     */
    public void unhighlight() {
        pane.getStyleClass().removeAll("selectable");
        removeMouseClickedHandler();
    }

    /**
     * Sets a handler for when the mouse enters the tile.
     *
     * @param handler the handler to call when the mouse enters the tile
     */
    public void setMouseEnteredHandler(final Consumer<MouseEvent> handler) {
        pane.setOnMouseEntered(handler::accept);
    }

    /**
     * Removes the handler for when the mouse enters the tile.
     */
    public void removeMouseEnteredHandler() {
        pane.setOnMouseEntered(null);
    }

    /**
     * Sets a handler for when the tile is clicked.
     *
     * @param handler the handler to call when the tile is clicked
     */
    public void setMouseClickedHandler(final Consumer<MouseEvent> handler) {
        hasMouseClickedHandler = true;
        pane.setOnMouseClicked(handler::accept);
    }

    /**
     * Removes the handler for when the tile is clicked.
     */
    public void removeMouseClickedHandler() {
        hasMouseClickedHandler = false;
        pane.setOnMouseClicked(null);
    }

    /**
     * Returns whether the tile has a handler for mouse clicks.
     *
     * @return true if the tile has a handler for mouse clicks, false otherwise
     */
    public boolean hasMouseClickedHandler() {
        return hasMouseClickedHandler;
    }
}
