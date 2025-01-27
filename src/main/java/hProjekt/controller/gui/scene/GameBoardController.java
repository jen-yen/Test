package hProjekt.controller.gui.scene;

import java.util.HashMap;
import java.util.Map;

import org.tudalgo.algoutils.student.annotation.DoNotTouch;

import hProjekt.controller.GamePhase;
import hProjekt.controller.PlayerController;
import hProjekt.controller.gui.HexGridController;
import hProjekt.controller.gui.PlayerActionsController;
import hProjekt.controller.gui.PlayerAnimationController;
import hProjekt.model.City;
import hProjekt.model.GameState;
import hProjekt.model.Player;
import hProjekt.model.TilePosition;
import hProjekt.view.GameBoardBuilder;
import hProjekt.view.menus.overlays.ChosenCitiesOverlayView;
import hProjekt.view.menus.overlays.CityOverlayView;
import hProjekt.view.menus.overlays.ConfirmationOverlayView;
import hProjekt.view.menus.overlays.GameInfoOverlayView;
import hProjekt.view.menus.overlays.PlayerOverlayView;
import hProjekt.view.menus.overlays.RollDiceOverlayView;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.scene.layout.Region;
import javafx.util.Builder;
import javafx.util.Pair;

/**
 * The controller for the game board scene.
 */
@DoNotTouch
public class GameBoardController implements SceneController {
    private final HexGridController hexGridController;
    private final GameBoardBuilder builder;
    private final GameInfoOverlayView gameInfoOverlayView;
    private final PlayerOverlayView playerOverlayView;
    private final RollDiceOverlayView rollDiceOverlayView;
    private final ChosenCitiesOverlayView chosenCitiesOverlayView;
    private final CityOverlayView cityOverlayView;
    private final ConfirmationOverlayView confirmationOverlayView;
    private final GameState gameState;
    private final Map<Player, PlayerAnimationController> playerAnimationControllers = new HashMap<>();

    /**
     * Creates a new game board controller.
     * Configures the UI to show the correct player information, round, dice roll
     * and chosen cities.
     * Triggers the end screen scene when the game is over.
     *
     * <b>Do not touch this constructor!</b>
     *
     * @param gameState                      the game state
     * @param activePlayerControllerProperty the active player controller property
     * @param diceRollProperty               the dice roll property
     * @param roundCounterProperty           the round counter property
     * @param chosenCitiesProperty           the chosen cities property
     */
    @DoNotTouch
    public GameBoardController(final GameState gameState,
            final Property<PlayerController> activePlayerControllerProperty, final IntegerProperty diceRollProperty,
            final IntegerProperty roundCounterProperty, final ReadOnlyProperty<Pair<City, City>> chosenCitiesProperty) {
        this.gameState = gameState;
        this.hexGridController = new HexGridController(gameState.getGrid());
        this.gameInfoOverlayView = new GameInfoOverlayView();
        this.playerOverlayView = new PlayerOverlayView(gameState.getPlayers());
        this.cityOverlayView = new CityOverlayView(gameState);
        this.confirmationOverlayView = new ConfirmationOverlayView();
        PlayerActionsController playerActionsController = new PlayerActionsController(activePlayerControllerProperty,
                this);
        this.chosenCitiesOverlayView = playerActionsController.getChosenCitiesOverlayView();
        this.rollDiceOverlayView = playerActionsController.getRollDiceOverlayView();
        this.builder = new GameBoardBuilder(hexGridController.buildView(), gameInfoOverlayView, playerOverlayView,
                rollDiceOverlayView, chosenCitiesOverlayView, cityOverlayView, confirmationOverlayView, event -> {
                    SceneController.loadEndScreenScene();
                });
        for (Player player : gameState.getPlayers()) {
            playerAnimationControllers.put(player,
                    new PlayerAnimationController(hexGridController.getBuilder(), player.getColor()));
        }
        activePlayerControllerProperty.subscribe((oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            System.out.println("Active player: " + newValue.getPlayer().getName());
            Platform.runLater(() -> {
                gameInfoOverlayView.setPlayerStatus(newValue.getPlayer());
                updatePlayerInformation();
            });
        });
        roundCounterProperty.subscribe((oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            Platform.runLater(() -> {
                gameInfoOverlayView.setRound(newValue.intValue());
                playerAnimationControllers.values().forEach(pa -> pa.hideTrain());
            });
        });
        diceRollProperty.subscribe((oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            Platform.runLater(() -> {
                rollDiceOverlayView.rollDice(newValue.intValue());
            });
        });
        chosenCitiesProperty.subscribe((oldValue, newValue) -> {
            if (newValue == null) {
                getHexGridController().getCityControllers().forEach(cc -> cc.unhighlight());
                return;
            }
            Platform.runLater(() -> {
                getHexGridController().getCityControllers().forEach(cc -> cc.unhighlight());
                chosenCitiesOverlayView.spinCities(newValue.getKey().getName(), newValue.getValue().getName(),
                        gameState.getGrid().getCities().values().stream().map(City::getName).toList());
                updateCityOverlay();
                getHexGridController().getCityControllersMap().get(newValue.getKey()).highlight();
                getHexGridController().getCityControllersMap().get(newValue.getValue()).highlight();
            });
        });
        gameState.getWinnerProperty().subscribe((oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            Platform.runLater(SceneController::loadEndScreenScene);
        });
        gameState.getGamePhaseProperty().subscribe((oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            Platform.runLater(() -> {
                gameInfoOverlayView.setPhase(newValue.toString());
                updatePlayerInformation();
            });
        });
    }

    /**
     * Returns the hex grid controller.
     *
     * @return the hex grid controller
     */
    public HexGridController getHexGridController() {
        return hexGridController;
    }

    /**
     * Returns the player animation controller for the given player.
     *
     * @param player the player
     * @return the player animation controller
     */
    public PlayerAnimationController getPlayerAnimationController(Player player) {
        return playerAnimationControllers.get(player);
    }

    /**
     * Updates the player information.
     */
    public void updatePlayerInformation() {
        Platform.runLater(() -> {
            playerOverlayView.updatePlayerCredits(gameState.getPlayers());
        });
    }

    /**
     * Returns the current game phase.
     *
     * @return the current game phase
     */
    public GamePhase getGamePhase() {
        return gameState.getGamePhaseProperty().getValue();
    }

    /**
     * Updates the city overlay.
     */
    public void updateCityOverlay() {
        Platform.runLater(() -> {
            System.out.println("Update City Overlay");
            cityOverlayView.updateCityList(true);
        });
    }

    /**
     * Updates the confirmation overlay with the given message and actions.
     *
     * @param message     the message to show
     * @param onYesAction the action to execute when the yes button is clicked
     * @param onNoAction  the action to execute when the no button is clicked
     */
    public void updateConfirmationOverlay(String message, Runnable onYesAction, Runnable onNoAction) {
        builder.addConfirmationOverlay();
        Platform.runLater(() -> {
            confirmationOverlayView.setMessage(message);
            confirmationOverlayView.setOnYesAction(onYesAction);
            confirmationOverlayView.setOnNoAction(onNoAction);
        });
    }

    /**
     * Hides the confirmation overlay.
     */
    public void hideConfirmationOverlay() {
        builder.removeConfirmationOverlay();
    }

    /**
     * Returns the position of the player.
     *
     * @param player the player
     * @return the position of the player
     */
    public TilePosition getPlayerPosition(Player player) {
        return gameState.getPlayerPositions().get(player);
    }

    @Override
    public Builder<Region> getBuilder() {
        return builder;
    }

    @Override
    public String getTitle() {
        return "Map";
    }
}
