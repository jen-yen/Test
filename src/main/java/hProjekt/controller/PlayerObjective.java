package hProjekt.controller;

import java.util.Set;

import hProjekt.controller.actions.BuildRailAction;
import hProjekt.controller.actions.ChooseCitiesAction;
import hProjekt.controller.actions.ChooseRailsAction;
import hProjekt.controller.actions.ConfirmBuildAction;
import hProjekt.controller.actions.ConfirmDrive;
import hProjekt.controller.actions.DriveAction;
import hProjekt.controller.actions.PlayerAction;
import hProjekt.controller.actions.RollDiceAction;

/**
 * This enum represents the different objectives a player can have and what
 * actions are allowed when the player has this
 * objective.
 */
public enum PlayerObjective {
    PLACE_RAIL(Set.of(BuildRailAction.class, ConfirmBuildAction.class)),
    ROLL_DICE(Set.of(RollDiceAction.class)),
    CHOOSE_CITIES(Set.of(ChooseCitiesAction.class)),
    DRIVE(Set.of(DriveAction.class)),
    CHOOSE_PATH(Set.of(ChooseRailsAction.class)),
    CONFIRM_PATH(Set.of(ConfirmDrive.class)),
    IDLE(Set.of());

    final Set<Class<? extends PlayerAction>> allowedActions;

    PlayerObjective(Set<Class<? extends PlayerAction>> allowedActions) {
        this.allowedActions = allowedActions;
    }

    /**
     * Returns the actions that are allowed when the player has this objective.
     *
     * @return the actions that are allowed when the player has this objective
     */
    public Set<Class<? extends PlayerAction>> getAllowedActions() {
        return allowedActions;
    }
}
