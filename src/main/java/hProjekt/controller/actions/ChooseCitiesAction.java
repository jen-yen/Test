package hProjekt.controller.actions;

import hProjekt.controller.PlayerController;

/**
 * An action to choose cities to drive to.
 */
public class ChooseCitiesAction implements PlayerAction {

    @Override
    public void execute(PlayerController pc) throws IllegalActionException {
        pc.chooseCities();
    }
}
