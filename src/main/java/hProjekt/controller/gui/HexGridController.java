package hProjekt.controller.gui;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.tudalgo.algoutils.student.annotation.DoNotTouch;

import hProjekt.model.City;
import hProjekt.model.Edge;
import hProjekt.model.HexGrid;
import hProjekt.model.Tile;
import hProjekt.view.HexGridBuilder;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Region;

/**
 * The controller for the hex grid.
 */
@DoNotTouch
public class HexGridController implements Controller {
    private final HexGrid hexGrid;
    private final HexGridBuilder builder;
    private final Map<Edge, EdgeController> edgeControllers;
    private final Map<Tile, TileController> tileControllers;
    private final Map<City, CityController> cityControllers;
    private static double lastX, lastY;

    /**
     * Creates a new hex grid controller.
     *
     * @param hexGrid the hex grid to render
     */
    public HexGridController(final HexGrid hexGrid) {
        this.edgeControllers = hexGrid.getEdges().values().stream().map(EdgeController::new)
                .collect(Collectors.toMap(EdgeController::getEdge, controller -> controller));
        this.tileControllers = hexGrid.getTiles().values().stream().map(TileController::new)
                .collect(Collectors.toMap(TileController::getTile, controller -> controller));
        this.cityControllers = hexGrid.getCities().values().stream().map(CityController::new).collect(
                Collectors.toMap(CityController::getCity, controller -> controller));
        this.builder = new HexGridBuilder(hexGrid,
                edgeControllers.values().stream().map(EdgeController::getEdgeLine).collect(Collectors.toSet()),
                tileControllers.values().stream().map(TileController::getBuilder).collect(Collectors.toSet()),
                cityControllers.values().stream().map(CityController::getBuilder).collect(Collectors.toSet()),
                this::zoomHandler, this::mousePressedHandler, this::mouseDraggedHandler, this::centerPaneHandler);
        this.hexGrid = hexGrid;
    }

    /**
     * The handler for the center pane button.
     * <p>
     * Centers the given pane.
     *
     * @param event the event that triggered the handler
     * @param pane  the pane to center
     */
    private void centerPaneHandler(final Event event, final Region pane) {
        pane.setTranslateX(0);
        pane.setTranslateY(0);
    }

    /**
     * The handler for the mouse dragged event.
     * <p>
     * Moves the pane when the mouse is dragged.
     *
     * @param event the event that triggered the handler
     * @param pane  the pane to move
     */
    private void mouseDraggedHandler(final MouseEvent event, final Region pane) {
        if (event.isPrimaryButtonDown()) {
            pane.setTranslateX(pane.getTranslateX() + (event.getX() - lastX));
            pane.setTranslateY(pane.getTranslateY() + (event.getY() - lastY));
        }
        lastX = event.getX();
        lastY = event.getY();
    }

    /**
     * The handler for the mouse pressed event.
     * <p>
     * Saves the last x and y position of the mouse. Used for moving the pane.
     *
     * @param event the event that triggered the handler
     */
    private void mousePressedHandler(final MouseEvent event) {
        lastX = event.getX();
        lastY = event.getY();
    }

    /**
     * The handler for the zoom event.
     * <p>
     * Zooms the pane in and out.
     *
     * @param event the event that triggered the handler
     * @param pane  the pane to zoom
     */
    private void zoomHandler(final ScrollEvent event, final Region pane) {
        final double minTileScale = 0.1;

        if (pane.getScaleX() <= minTileScale && event.getDeltaY() < 0 || event.getDeltaY() == 0) {
            return;
        }
        pane.setScaleX(pane.getScaleX() + event.getDeltaY() / 500);
        pane.setScaleY(pane.getScaleY() + event.getDeltaY() / 500);
    }

    /**
     * Returns the edge controllers.
     *
     * @return the edge controllers
     */
    public Set<EdgeController> getEdgeControllers() {
        return edgeControllers.values().stream().collect(Collectors.toSet());
    }

    /**
     * Returns the edge controllers as a map.
     *
     * @return the edge controllers as a map
     */
    public Map<Edge, EdgeController> getEdgeControllersMap() {
        return Collections.unmodifiableMap(edgeControllers);
    }

    /**
     * Returns the tile controllers.
     *
     * @return the tile controllers
     */
    public Set<TileController> getTileControllers() {
        return tileControllers.values().stream().collect(Collectors.toSet());
    }

    /**
     * Returns the tile controllers as a map of tiles to tile controllers.
     *
     * @return the tile controllers as a map
     */
    public Map<Tile, TileController> getTileControllersMap() {
        return Collections.unmodifiableMap(tileControllers);
    }

    /**
     * Returns the city controllers.
     *
     * @return the city controllers
     */
    public Set<CityController> getCityControllers() {
        return cityControllers.values().stream().collect(Collectors.toSet());
    }

    /**
     * Returns the city controllers as a map of cities to city controllers.
     *
     * @return the city controllers as a map
     */
    public Map<City, CityController> getCityControllersMap() {
        return Collections.unmodifiableMap(cityControllers);
    }

    /**
     * Returns the hex grid.
     *
     * @return the hex grid
     */
    public HexGrid getHexGrid() {
        return hexGrid;
    }

    /**
     * Highlights the tiles.
     *
     * @param handler the handler to call when a tile is clicked
     */
    public void highlightTiles(final Consumer<Tile> handler) {
        tileControllers.values().forEach(controller -> controller.highlight(handler));
    }

    /**
     * Unhighlights the tiles.
     */
    public void unhighlightTiles() {
        tileControllers.values().forEach(TileController::unhighlight);
    }

    /**
     * Draws all tiles again.
     */
    public void drawTiles() {
        Platform.runLater(() -> builder.drawTiles());
    }

    /**
     * Draws all edges again.
     */
    public void drawEdges() {
        Platform.runLater(() -> builder.drawEdges());
    }

    @Override
    public HexGridBuilder getBuilder() {
        return builder;
    }
}
