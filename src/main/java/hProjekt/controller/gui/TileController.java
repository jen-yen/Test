package hProjekt.controller.gui;

import java.util.function.Consumer;

import hProjekt.model.Tile;
import hProjekt.view.TileBuilder;
import javafx.scene.input.MouseEvent;

/**
 * The controller for a tile.
 */
public class TileController implements Controller {
    private final TileBuilder builder;

    /**
     * Creates a new tile controller.
     *
     * @param tile the tile to render
     */
    public TileController(final Tile tile) {
        builder = new TileBuilder(tile);
    }

    /**
     * Returns the tile.
     *
     * @return the tile
     */
    public Tile getTile() {
        return builder.getTile();
    }

    /**
     * Highlights the tile and overwrites the click handler.
     *
     * @param handler the handler to call when the tile is clicked
     */
    public void highlight(final Consumer<Tile> handler) {
        builder.highlight(() -> handler.accept(getTile()));
    }

    /**
     * Unhighlights the tile and removes the click handler.
     */
    public void unhighlight() {
        builder.unhighlight();
    }

    /**
     * Sets the mouse entered handler.
     *
     * @param handler the handler to call when the mouse enters the tile
     */
    public void setMouseEnteredHandler(final Consumer<MouseEvent> handler) {
        builder.setMouseEnteredHandler(handler);
    }

    /**
     * Removes the mouse entered handler.
     */
    public void removeMouseEnteredHandler() {
        builder.removeMouseEnteredHandler();
    }

    /**
     * Sets the mouse clicked handler.
     *
     * @param handler the handler to call when the tile is clicked
     */
    public void setMouseClickedHandler(final Consumer<MouseEvent> handler) {
        builder.setMouseClickedHandler(handler);
    }

    /**
     * Removes the mouse clicked handler.
     */
    public void removeMouseClickedHandler() {
        builder.removeMouseClickedHandler();
    }

    /**
     * Returns whether the tile has a mouse clicked handler.
     *
     * @return true if the tile has a mouse clicked handler, false otherwise
     */
    public boolean hasMouseClickedHandler() {
        return builder.hasMouseClickedHandler();
    }

    @Override
    public TileBuilder getBuilder() {
        return builder;
    }
}
