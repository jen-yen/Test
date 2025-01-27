package hProjekt.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.beans.property.Property;

/**
 * Holds information on an edge connecting two tile centers.
 * An edge is defined by two adjacent {@link TilePosition}s.
 */
public interface Edge {

    /**
     * Returns the HexGrid instance this edge is placed in.
     *
     * @return the HexGrid instance this edge is placed in
     */
    HexGrid getHexGrid();

    /**
     * Returns the first position.
     *
     * @return the first position
     */
    TilePosition getPosition1();

    /**
     * Returns the second position.
     *
     * @return the second position
     */
    TilePosition getPosition2();

    /**
     * Returns {@code true} if the given edge connects to this edge and
     * {@code false} otherwise.
     *
     * @param other the other edge
     * @return whether the two edges are connected
     */
    boolean connectsTo(Edge other);

    /**
     * Returns the {@link TilePosition}s that this edge lies between.
     *
     * @return the adjacent tile positions
     */
    Set<TilePosition> getAdjacentTilePositions();

    /**
     * Returns all edges that connect to this edge in the grid.
     *
     * @return all edges connected to this one
     */
    Set<Edge> getConnectedEdges();

    /**
     * Returns {@code true} if a player has built a rail on this edge and
     * {@code false} otherwise.
     *
     * @return whether a player has placed a rail on this edge
     */
    boolean hasRail();

    /**
     * Adds a rail for the given player to this edge.
     * Checks if the player can build a rail on this edge.
     * This method does not verify that the player has enough credits to build a
     * rail.
     *
     * The following conditions must be met:
     * - The player hasn't already built a rail here
     * - The player has built a rail on a connected edge
     * - If the player hasn't built any rails yet, the edge must be connected to a
     * starting city
     *
     * @param player the player to add the rail for
     * @return {@code true} if the rail was added, {@code false} otherwise
     */
    boolean addRail(Player player);

    /**
     * Removes the rail of the given player from this edge.
     *
     * @param player the player to remove the rail for
     * @return {@code true} if the rail was removed, {@code false} otherwise
     */
    boolean removeRail(Player player);

    /**
     * Returns the rail's owners property
     *
     * @return the rail's owners property
     */
    Property<List<Player>> getRailOwnersProperty();

    /**
     * Returns the rail's owners, if a rail has been built on this edge.
     *
     * @return the rail's owners, if a rail has been built on this edge
     */
    List<Player> getRailOwners();

    /**
     * Returns the connected rails of the given player.
     *
     * @param player the player to check for.
     * @return the connected rails.
     */
    Set<Edge> getConnectedRails(Player player);

    /**
     * Returns the base cost of building a rail on this edge.
     *
     * @return the base cost of building a rail on this edge
     */
    int getBaseBuildingCost();

    /**
     * Returns the credits that need to be paid to each player that has already
     * built
     * on this edge.
     *
     * @param player the player to calculate the parallel cost for
     * @return a map of players and the credits that need to be paid to them
     */
    Map<Player, Integer> getParallelCostPerPlayer(Player player);

    /**
     * Returns the total sum of credits that needs to be paid by the player to other
     * players
     * when building a rail.
     *
     * @param player the player to calculate the total parallel cost for
     * @return the total sum of credits that needs to be paid by the player to build
     *         a rail
     */
    int getTotalParallelCost(Player player);

    /**
     * Returns the total cost the player has to pay to build a rail on this edge
     * including the parallel cost.
     * The total cost is the sum of the building cost and the total parallel cost.
     *
     * @param player the player to calculate the total cost for
     * @return the total cost the player has to pay to build a rail on this edge
     */
    int getTotalBuildingCost(Player player);

    /**
     * Returns the cost of driving along this edge.
     *
     * @param from the tile position the player is coming from
     * @return the cost of driving along this edge
     */
    int getDrivingCost(TilePosition from);

    /**
     * Returns the cost of renting the rail on this edge.
     *
     * @param player the player that wants to rent the rail
     * @return the rent that needs to be paid to the other players on this edge
     */
    Map<Player, Integer> getRentingCost(Player player);
}
