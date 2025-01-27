package hProjekt.controller.actions;

import hProjekt.controller.PlayerController;
import hProjekt.model.Tile;

/**
 * An action to let the player drive to the chosen tile.
 *
 * @param targetTile The tile the player wants to drive to.
 */
public record DriveAction(Tile targetTile) implements PlayerAction {

    @Override
    public void execute(PlayerController pc) throws IllegalActionException {
        pc.drive(targetTile);
    }
}
