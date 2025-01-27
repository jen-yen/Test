package hProjekt.model;

import java.util.Set;

import org.tudalgo.algoutils.student.annotation.DoNotTouch;

import hProjekt.model.TilePosition.EdgeDirection;
import javafx.beans.value.ObservableDoubleValue;
import javafx.scene.paint.Color;

/**
 * Represents a tile in the game grid.
 * A tile has a position, a type, may have a city and is connected to other
 * tiles via edges.
 */
@DoNotTouch
public interface Tile {

    /**
     * Returns the position of this tile.
     *
     * @return the position of this tile
     */
    TilePosition getPosition();

    /**
     * Returns the type of this tile.
     *
     * @return the type of this tile
     */
    Type getType();

    /**
     * Returns the height of this tile.
     *
     * @return the height of this tile
     */
    ObservableDoubleValue heightProperty();

    /**
     * Returns the width of this tile.
     *
     * @return the width of this tile
     */
    ObservableDoubleValue widthProperty();

    /**
     * Returns the hex grid this tile is part of.
     *
     * @return the hex grid this tile is part of
     */
    HexGrid getHexGrid();

    /**
     * Returns all neighbours of this tile.
     *
     * @return all neighbours of this tile
     */
    Set<Tile> getNeighbours();

    /**
     * Returns all neighbouring tiles connected by the given edges.
     *
     * @param connectingEdges the edges connecting the tiles
     * @return all neighbouring tiles connected by the given edges
     */
    Set<Tile> getConnectedNeighbours(Set<Edge> connectingEdges);

    /**
     * Returns the tile next in the given direction.
     *
     * @param direction the direction of the edge
     * @return the neighbouring tile
     */
    Tile getNeighbour(final EdgeDirection direction);

    /**
     * Returns whether this tile is at the coast.
     *
     * @return whether this tile is at the coast
     */
    boolean isAtCoast();

    /**
     * Returns the edge in the given direction.
     *
     * @param direction the direction of the edge
     * @return the edge in the given direction
     */
    Edge getEdge(EdgeDirection direction);

    /**
     * Returns all edges connected to this tile.
     *
     * @return all edges connected to this tile
     */
    Set<Edge> getEdges();

    /**
     * Returns whether this tile has a city.
     *
     * @return whether this tile has a city
     */
    boolean hasCity();

    /**
     * Returns all rails the given player has build on this tile.
     *
     * @param player the player
     * @return all rails the given player has build on this tile
     */
    Set<Edge> getRails(Player player);

    /**
     * An enumeration containing all available tile types.
     * Custom tile types need to be added to this list manually.
     */
    enum Type {
        PLAIN(Color.GREEN),
        MOUNTAIN(Color.BROWN);

        /**
         * The color of the tile.
         */
        public final Color color;

        Type(final Color color) {
            this.color = color;
        }
    }
}
