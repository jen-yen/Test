package hProjekt.view;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.IntBinaryOperator;
import java.util.function.ToIntFunction;

import hProjekt.model.City;
import hProjekt.model.HexGrid;
import hProjekt.model.Tile;
import hProjekt.model.TilePosition;
import javafx.beans.binding.Bindings;
import javafx.event.Event;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Builder;

/**
 * The builder for the {@link HexGrid}.
 * It creates a pane with the hex grid and handles the placement of the tiles
 * and edges.
 * The hex grid pane can be zoomed, panned and centered.
 */
public class HexGridBuilder implements Builder<Region> {
    private final HexGrid grid;
    private final BiConsumer<ScrollEvent, Region> scrollHandler;
    private final Consumer<MouseEvent> pressedHandler;
    private final BiConsumer<MouseEvent, Region> draggedHandler;
    private final Point2D maxPoint;
    private final Point2D minPoint;
    private final BiConsumer<Event, Region> centerButtonHandler;
    private final Set<EdgeLine> edgeLines;
    private final Set<TileBuilder> tileBuilders;
    private final Set<CityBuilder> cityBuilders;

    private final Pane hexGridPane = new Pane();

    /**
     * Creates a new hex grid builder with the given hex grid, intersection
     * builders, edge lines, tile builders and event handlers.
     *
     * @param grid                The hex grid.
     * @param edgeLines           The edge lines.
     * @param tileBuilders        The tile builders.
     * @param cityBuilders        The city builders.
     * @param scrollHandler       The handler for the scroll event. Used for
     *                            zooming.
     * @param pressedHandler      The handler for the mouse pressed event.
     * @param draggedHandler      The handler for the mouse dragged event. Used for
     *                            panning
     * @param centerButtonHandler The handler for the center button event.
     */
    public HexGridBuilder(
            final HexGrid grid,
            final Set<EdgeLine> edgeLines,
            final Set<TileBuilder> tileBuilders, final Set<CityBuilder> cityBuilders,
            final BiConsumer<ScrollEvent, Region> scrollHandler,
            final Consumer<MouseEvent> pressedHandler, final BiConsumer<MouseEvent, Region> draggedHandler,
            final BiConsumer<Event, Region> centerButtonHandler) {
        this.grid = grid;
        this.edgeLines = edgeLines;
        this.tileBuilders = tileBuilders;
        this.cityBuilders = cityBuilders;

        this.scrollHandler = scrollHandler;
        this.pressedHandler = pressedHandler;
        this.draggedHandler = draggedHandler;
        this.centerButtonHandler = centerButtonHandler;

        final BiFunction<ToIntFunction<TilePosition>, IntBinaryOperator, Integer> reduceTiles = (
                positionFunction,
                reduceFunction) -> grid.getTiles().values().stream().map(Tile::getPosition).mapToInt(positionFunction)
                        .reduce(reduceFunction).getAsInt();

        this.maxPoint = new Point2D(
                calculatePositionTranslation(new TilePosition(reduceTiles.apply(TilePosition::q, Integer::max), 0))
                        .getX(),
                calculatePositionTranslation(new TilePosition(0, reduceTiles.apply(TilePosition::r, Integer::max)))
                        .getY());
        this.minPoint = new Point2D(
                calculatePositionTranslation(new TilePosition(reduceTiles.apply(TilePosition::q, Integer::min), 0))
                        .getX(),
                calculatePositionTranslation(new TilePosition(0, reduceTiles.apply(TilePosition::r, Integer::min)))
                        .getY());
    }

    @Override
    public Region build() {
        hexGridPane.getChildren().clear();

        hexGridPane.getChildren().addAll(tileBuilders.stream().map(this::placeTile).toList());

        hexGridPane.maxWidthProperty().bind(Bindings
                .createDoubleBinding(
                        () -> Math.abs(minPoint.getX()) + maxPoint.getX() + grid.getTileWidth(),
                        grid.tileSizeProperty()));
        hexGridPane.maxHeightProperty().bind(Bindings
                .createDoubleBinding(
                        () -> Math.abs(minPoint.getY()) + maxPoint.getX() + grid.getTileHeight(),
                        grid.tileSizeProperty()));
        hexGridPane.minWidthProperty().bind(hexGridPane.maxWidthProperty());
        hexGridPane.minHeightProperty().bind(hexGridPane.maxHeightProperty());

        edgeLines.forEach(this::placeEdge);
        hexGridPane.getChildren().addAll(cityBuilders.stream().map(this::placeCity).toList());

        final StackPane mapPane = new StackPane(hexGridPane);
        mapPane.getStylesheets().add("css/hexmap.css");
        mapPane.getStyleClass().add("hex-grid");
        mapPane.setOnScroll(event -> scrollHandler.accept(event, hexGridPane));
        mapPane.setOnMousePressed(pressedHandler::accept);
        mapPane.setOnMouseDragged(event -> draggedHandler.accept(event, hexGridPane));

        final Button centerButton = new Button("Center map");
        centerButton.setOnAction(event -> centerButtonHandler.accept(event, hexGridPane));
        centerButton.translateXProperty().bind(Bindings
                .createDoubleBinding(
                        () -> (centerButton.getWidth() - mapPane.getWidth()) / 2 + 10,
                        mapPane.widthProperty()));
        centerButton.translateYProperty().bind(Bindings
                .createDoubleBinding(
                        () -> (mapPane.getHeight() - centerButton.getHeight()) / 2 - 10,
                        mapPane.heightProperty()));

        mapPane.getChildren().add(centerButton);

        return mapPane;
    }

    /**
     * Draws the tiles on the hex grid.
     */
    public void drawTiles() {
        tileBuilders.forEach(TileBuilder::build);
    }

    /**
     * Places a tile on the hex grid.
     *
     * @param builder The tile builder.
     * @return The region of the tile.
     */
    private Region placeTile(final TileBuilder builder) {
        final Region tileView = builder.build();
        final Tile tile = builder.getTile();
        final TilePosition position = tile.getPosition();
        final Point2D translatedPoint = calculatePositionTranslationOffset(position);
        tileView.translateXProperty().bind(
                Bindings.createDoubleBinding(() -> (translatedPoint.getX()), tile.widthProperty()));
        tileView.translateYProperty().bind(
                Bindings.createDoubleBinding(() -> translatedPoint.getY(), tile.heightProperty()));
        return tileView;
    }

    /**
     * Draws the edges on the hex grid.
     */
    public void drawEdges() {
        edgeLines.forEach(EdgeLine::init);
    }

    /**
     * Places an edge on the hex grid.
     *
     * @param edgeLine The edge line.
     */
    private void placeEdge(final EdgeLine edgeLine) {
        final Point2D translatedStart = calculatePositionCenterOffset(edgeLine.getEdge().getPosition1());
        final Point2D translatedEnd = calculatePositionCenterOffset(edgeLine.getEdge().getPosition2());
        edgeLine.setStartX(translatedStart.getX());
        edgeLine.setStartY(translatedStart.getY());
        edgeLine.setEndX(translatedEnd.getX());
        edgeLine.setEndY(translatedEnd.getY());
        edgeLine.init();
        hexGridPane.getChildren().addAll(edgeLine.getOutline());
        hexGridPane.getChildren().add(edgeLine);
    }

    /**
     * Draws the cities on the hex grid.
     */
    public void drawCities() {
        cityBuilders.forEach(CityBuilder::build);
    }

    /**
     * Places a city on the hex grid.
     *
     * @param builder The city builder.
     * @return The region of the city.
     */
    private Region placeCity(final CityBuilder builder) {
        final Region cityView = builder.build();
        final City city = builder.getCity();
        final TilePosition position = city.getPosition();
        final Point2D translatedPoint = calculatePositionCenterOffset(position);
        cityView.translateXProperty().bind(
                Bindings.createDoubleBinding(() -> translatedPoint.getX() - cityView.getWidth() / 2,
                        cityView.widthProperty()));
        cityView.translateYProperty().bind(
                Bindings.createDoubleBinding(
                        () -> translatedPoint.getY() - builder.getMarkerHeight() / 2,
                        cityView.heightProperty()));
        return cityView;
    }

    /**
     * Calculates the upper left corner of the tile region.
     *
     * @param position The position of the tile.
     * @return The point of the upper left corner.
     */
    private Point2D calculatePositionTranslation(final TilePosition position) {
        return new Point2D(
                grid.getTileSize() * (Math.sqrt(3) * position.q() + Math.sqrt(3) / 2 * position.r()),
                grid.getTileSize() * (3.0 / 2 * position.r()));
    }

    /**
     * Calculates the upper left corner of the tile region with an offset to move
     * the coordinate system center to the center of the hex grid.
     *
     * @param position The position of the tile.
     * @return The point of the upper left corner.
     */
    private Point2D calculatePositionTranslationOffset(final TilePosition position) {
        return calculatePositionTranslation(position).add(Math.abs(minPoint.getX()), Math.abs(minPoint.getY()));
    }

    /**
     * Calculates the center of the tile region with an offset to move the
     * coordinate system center to the center of the hex grid.
     *
     * @param position The position of the tile.
     * @return The point of the center.
     */
    public Point2D calculatePositionCenterOffset(final TilePosition position) {
        return calculatePositionTranslationOffset(position).add(grid.getTileWidth() / 2, grid.getTileHeight() / 2);
    }

    /**
     * Returns the pane with the hex grid.
     *
     * @return The pane with the hex grid.
     */
    public Pane getHexGridPane() {
        return hexGridPane;
    }

}
