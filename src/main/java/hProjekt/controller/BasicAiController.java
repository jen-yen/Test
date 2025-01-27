package hProjekt.controller;

import java.util.List;
import java.util.Set;

import hProjekt.Config;
import hProjekt.controller.actions.BuildRailAction;
import hProjekt.controller.actions.ChooseCitiesAction;
import hProjekt.controller.actions.ChooseRailsAction;
import hProjekt.controller.actions.ConfirmBuildAction;
import hProjekt.controller.actions.ConfirmDrive;
import hProjekt.controller.actions.DriveAction;
import hProjekt.controller.actions.PlayerAction;
import hProjekt.controller.actions.RollDiceAction;
import hProjekt.model.City;
import hProjekt.model.Edge;
import hProjekt.model.GameState;
import hProjekt.model.HexGrid;
import hProjekt.model.Tile;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.util.Pair;

/**
 * A basic AI controller as an example for how to implement an AI controller.
 */
public class BasicAiController extends AiController {

    /**
     * Creates a new basic AI controller with the given player controller, hex grid,
     * game state and active player controller.
     * Adds a subscription to the player objective property to execute actions when
     * the player's objective changes.
     *
     * @param playerController       the player controller
     * @param hexGrid                the hex grid
     * @param gameState              the game state
     * @param activePlayerController the active player controller
     */
    public BasicAiController(final PlayerController playerController, final HexGrid hexGrid, final GameState gameState,
            final Property<PlayerController> activePlayerController, final IntegerProperty diceRollProperty,
            final IntegerProperty roundCounterProperty, final ReadOnlyProperty<Pair<City, City>> chosenCitiesProperty) {
        super(playerController, hexGrid, gameState, activePlayerController, diceRollProperty, roundCounterProperty,
                chosenCitiesProperty);
    }

    @Override
    protected void executeActionBasedOnObjective(PlayerObjective objective) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException("Main thread was interrupted", e);
        }

        final Set<Class<? extends PlayerAction>> allowedActions = playerController.getPlayerObjective()
                .getAllowedActions();
        if (allowedActions.contains(RollDiceAction.class)) {
            playerController.triggerAction(new RollDiceAction());
        }
        if (allowedActions.contains(BuildRailAction.class)
                && !playerController.getPlayerState().buildableRailEdges().isEmpty()) {
            int randomIndex = Config.RANDOM.nextInt(playerController.getPlayerState().buildableRailEdges().size());
            playerController.triggerAction(new BuildRailAction(
                    List.of(playerController.getPlayerState().buildableRailEdges()
                            .toArray(Edge[]::new)[randomIndex])));
        }
        if (allowedActions.contains(ConfirmBuildAction.class) && playerController.getPlayerState().buildableRailEdges()
                .isEmpty()) {
            playerController.triggerAction(new ConfirmBuildAction());
        }
        if (allowedActions.contains(ChooseCitiesAction.class)) {
            playerController.triggerAction(new ChooseCitiesAction());
        }
        if (allowedActions.contains(ConfirmDrive.class)) {
            playerController.triggerAction(new ConfirmDrive(true));
        }
        if (allowedActions.contains(DriveAction.class)) {
            int randomIndex = Config.RANDOM.nextInt(playerController.getPlayerState().drivableTiles().size());
            playerController
                    .triggerAction(new DriveAction(
                            playerController.getPlayerState().drivableTiles().keySet()
                                    .toArray(Tile[]::new)[randomIndex]));
        }
        if (allowedActions.contains(ChooseRailsAction.class)) {
            playerController.triggerAction(new ChooseRailsAction(Set.of()));
        }
    }
}
