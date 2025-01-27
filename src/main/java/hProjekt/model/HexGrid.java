package hProjekt.model;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

import org.tudalgo.algoutils.student.annotation.DoNotTouch;

import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ObservableDoubleValue;

/**
 * Holds all the information displayed on the hexagonal grid and information for
 * rendering. In short, the game board.
 * Specifically, information on...
 * <ul>
 * <li>tiles and their logical and graphical properties (position, height,
 * width, etc.)</li>
 * <li>edges</li>
 * <li>cities</li>
 * </ul>
 * are saved in and modified by instances of this interface.
 */
@DoNotTouch
public interface HexGrid {

    // Tiles

    /**
     * Returns the width of a tile.
     *
     * @return the width of a tile
     */
    double getTileWidth();

    /**
     * Returns the height of a tile.
     *
     * @return the height of a tile
     */
    double getTileHeight();

    /**
     * Returns the size of a tile.
     *
     * @return the size of a tile
     */
    double getTileSize();

    /**
     * Returns the width of a tile as an {@link ObservableDoubleValue}.
     *
     * @return the width of a tile as an {@link ObservableDoubleValue}
     */
    ObservableDoubleValue tileWidthProperty();

    /**
     * Returns the height of a tile as an {@link ObservableDoubleValue}.
     *
     * @return the height of a tile as an {@link ObservableDoubleValue}
     */
    ObservableDoubleValue tileHeightProperty();

    /**
     * Returns the size of a tile as an {@link DoubleProperty}.
     *
     * @return the size of a tile as an {@link DoubleProperty}
     */
    DoubleProperty tileSizeProperty();

    /**
     * Returns all tiles of the grid as a set.
     *
     * @return all tiles of the grid as a set
     */
    Map<TilePosition, Tile> getTiles();

    /**
     * Returns the tile at the given q and r coordinate.
     *
     * @param q the q-coordinate of the tile
     * @param r the r-coordinate of the tile
     * @return the tile at the given row and column
     */
    Tile getTileAt(int q, int r);

    /**
     * Returns the tile at the given position.
     *
     * @param position the position of the tile
     * @return the tile at the given position
     */
    Tile getTileAt(TilePosition position);

    // Edges / Roads

    /**
     * Returns all edges of the grid.
     *
     * @return all edges of the grid
     */
    Map<Set<TilePosition>, Edge> getEdges();

    /**
     * Returns the edge between the given positions.
     *
     * @param position0 the first position
     * @param position1 the second position
     * @return the edge between the given intersections
     */
    Edge getEdge(TilePosition position0, TilePosition position1);

    /**
     * Returns all cities of the grid.
     *
     * @return all cities of the grid
     */
    Map<TilePosition, City> getCities();

    /**
     * Returns the city at the given position or {@code null} if there is no city.
     *
     * @param position the position of the city
     * @return the city at the given position or {@code null} if there is no city
     */
    City getCityAt(TilePosition position);

    /**
     * Returns all cities connected to a rail.
     *
     * @return all cities connected to a rail
     */
    Map<TilePosition, City> getConnectedCities();

    /**
     * Returns all cities that are not connected to a rail.
     *
     * @return all cities that are not connected to a rail
     */
    Map<TilePosition, City> getUnconnectedCities();

    /**
     * Returns all cities that are stating cities.
     *
     * @return all cities that are stating cities.
     */
    Map<TilePosition, City> getStartingCities();

    /**
     * Returns all rails of the given player.
     *
     * @param player the player to get the rails of
     * @return all rails of the given player
     */
    Map<Set<TilePosition>, Edge> getRails(Player player);

    /**
     * Finds the shortest path between start and end using the available edges and
     * the edgeCostFunction.
     *
     * @param start            the start position
     * @param end              the end position
     * @param availabeEdges    the edges to search for the path
     * @param edgeCostFunction the function to calculate the cost of an edge
     *                         receives the two position of the edge as inputs.
     *                         First the current position, second the next position.
     * @return the shortest path between start and end
     */
    List<Edge> findPath(TilePosition start, TilePosition end, Set<Edge> availabeEdges,
            BiFunction<TilePosition, TilePosition, Integer> edgeCostFunction);
}
