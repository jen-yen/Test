package hProjekt.controller.actions;

import hProjekt.controller.PlayerController;

/**
 * An action to confirm whether the player wants to drive on the shown path or
 * not.
 *
 * @param accept Whether the player accepts the path or not.
 */
public record ConfirmDrive(boolean accept) implements PlayerAction {

    @Override
    public void execute(PlayerController pc) throws IllegalActionException {
        pc.confirmPath(accept);
    }
}
