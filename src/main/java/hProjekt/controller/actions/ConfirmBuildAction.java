package hProjekt.controller.actions;

import hProjekt.controller.PlayerController;

/**
 * An action to confirm that the player has built all the rails they wanted to
 * build.
 */
public record ConfirmBuildAction() implements PlayerAction {

    @Override
    public void execute(PlayerController pc) throws IllegalActionException {
        // stays empty
    }
}
