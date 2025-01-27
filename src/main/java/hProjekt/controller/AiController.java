package hProjekt.controller;

import hProjekt.model.City;
import hProjekt.model.GameState;
import hProjekt.model.HexGrid;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.util.Pair;

/**
 * Represents an AI controller that can execute actions based on a player's
 * objective.
 * Gets all information that could be needed to execute actions.
 * Automatically subscribes to the player objective property to execute actions
 * when the player's objective changes.
 */
public abstract class AiController {
    protected final PlayerController playerController;
    protected final HexGrid hexGrid;
    protected final GameState gameState;
    protected final Property<PlayerController> activePlayerController;

    /**
     * Creates a new AI controller with the given player controller, hex grid, game
     * state and active player controller.
     * Adds a subscription to the player objective property to execute actions when
     * the player's objective changes.
     *
     * @param playerController       the player controller
     * @param hexGrid                the hex grid
     * @param gameState              the game state
     * @param activePlayerController the active player controller
     */
    protected AiController(
            final PlayerController playerController, final HexGrid hexGrid, final GameState gameState,
            final Property<PlayerController> activePlayerController, final IntegerProperty diceRollProperty,
            final IntegerProperty roundCounterProperty, final ReadOnlyProperty<Pair<City, City>> chosenCitiesProperty) {
        this.playerController = playerController;
        this.hexGrid = hexGrid;
        this.gameState = gameState;
        this.activePlayerController = activePlayerController;
        playerController.getPlayerStateProperty()
                .subscribe(state -> this.executeActionBasedOnObjective(state.playerObjective()));
    }

    /**
     * Executes an action that is allowed by the given player objective.
     * May perform multiple actions if necessary and allowed.
     *
     * @param objective the player objective
     */
    protected abstract void executeActionBasedOnObjective(final PlayerObjective objective);
}
