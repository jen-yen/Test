package hProjekt.controller.actions;

import java.util.Set;

import hProjekt.controller.PlayerController;
import hProjekt.model.Edge;

/**
 * An action to choose rails to rent.
 *
 * @param choosenEdges The chosen edges
 */
public record ChooseRailsAction(Set<Edge> choosenEdges) implements PlayerAction {

    @Override
    public void execute(PlayerController pc) throws IllegalActionException {
        pc.chooseEdges(choosenEdges);
    }

}
