package hProjekt.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tudalgo.algoutils.student.annotation.DoNotTouch;

import hProjekt.controller.PlayerObjective;

/**
 * Holds information on a player's state.
 * that can be upgraded
 *
 * @param buildableRailEdges a set of edges where rail can be built
 * @param playerObjective    the player's objective
 * @param choosableEdges     a set of edges the player can choose from. For
 *                           example to rent them.
 * @param rentedEdges        a set of edges the player has rented
 * @param hasPath            whether the player has a path to the target city
 * @param drivableTiles      a map of tiles that can be driven to
 * @param buildingBudget     the player's budget for building
 */
@DoNotTouch
public record PlayerState(
        Set<Edge> buildableRailEdges,
        PlayerObjective playerObjective, Set<Edge> choosableEdges, Set<Edge> rentedEdges, boolean hasPath,
        Map<Tile, List<Tile>> drivableTiles, int buildingBudget) {
}
