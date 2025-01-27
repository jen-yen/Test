package hProjekt.controller.actions;

import java.util.List;

import hProjekt.controller.PlayerController;
import hProjekt.model.Edge;

/**
 * An action to build rails.
 *
 * @param edges The edges to build the rails on must be in the correct order so
 *              there is always a connection to a previous or already built
 *              edge.
 */
public record BuildRailAction(List<Edge> edges) implements PlayerAction {
    @Override
    public void execute(PlayerController pc) throws IllegalActionException {
        pc.buildRails(edges);
    }
}
