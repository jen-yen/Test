package hProjekt.controller.gui;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.tudalgo.algoutils.student.annotation.DoNotTouch;

import hProjekt.controller.GameController;
import hProjekt.controller.gui.scene.AboutSceneController;
import hProjekt.controller.gui.scene.EndScreenSceneController;
import hProjekt.controller.gui.scene.GameBoardController;
import hProjekt.controller.gui.scene.LeaderboardSceneController;
import hProjekt.controller.gui.scene.MainMenuSceneController;
import hProjekt.controller.gui.scene.SceneController;
import hProjekt.controller.gui.scene.SettingsSceneController;
import hProjekt.controller.gui.scene.SetupGameSceneController;
import hProjekt.model.Player;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * A SceneSwitcher is responsible for switching between the different
 * {@link Scene}s.
 * It is a singleton and can be accessed via {@link #getInstance()}.
 */
@DoNotTouch
public class SceneSwitcher {
    private final Stage stage;
    private GameController gameController;
    private static SceneSwitcher INSTANCE;
    private final Consumer<GameController> gameLoopStarter;

    /**
     * Creates a new SceneSwitcher.
     *
     * @param stage           The {@link Stage} to show the {@link Scene} on.
     * @param gameLoopStarter The consumer that starts the game loop.
     */
    @DoNotTouch
    private SceneSwitcher(final Stage stage, final Consumer<GameController> gameLoopStarter) {
        this.stage = stage;
        this.gameLoopStarter = gameLoopStarter;
    }

    /**
     * Returns the instance of the SceneSwitcher.
     * Creates a new instance if it does not exist yet.
     *
     * @param stage           The {@link Stage} to show the {@link Scene} on.
     * @param gameLoopStarter The consumer that starts the game loop.
     * @return The instance of the SceneSwitcher.
     */
    @DoNotTouch
    public static SceneSwitcher getInstance(final Stage stage, final Consumer<GameController> gameLoopStarter) {
        if (INSTANCE == null) {
            INSTANCE = new SceneSwitcher(stage, gameLoopStarter);
        }
        return INSTANCE;
    }

    /**
     * Returns the instance of the SceneSwitcher.
     * Throws an {@link IllegalStateException} if the instance does not exist yet.
     *
     * @return The instance of the SceneSwitcher.
     */
    @DoNotTouch
    public static SceneSwitcher getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("SceneSwitcher has not been initialized yet.");
        }
        return INSTANCE;
    }

    /**
     * The different types of scenes that can be loaded.
     */
    public enum SceneType {
        GAME_BOARD(() -> {
            final GameBoardController controller = new GameBoardController(getInstance().gameController.getState(),
                    getInstance().gameController.activePlayerControllerProperty(),
                    getInstance().gameController.currentDiceRollProperty(),
                    getInstance().gameController.roundCounterProperty(),
                    getInstance().gameController.chosenCitiesProperty());
            getInstance().gameLoopStarter.accept(getInstance().gameController);
            return controller;
        }),

        MAIN_MENU(MainMenuSceneController::new),
        ABOUT(AboutSceneController::new),
        SETUP_GAME_MENU(() -> {
            getInstance().gameController = new GameController();
            return new SetupGameSceneController(getInstance().gameController.getState());
        }),
        LEADERBOARD(LeaderboardSceneController::new),
        END_SCREEN(() -> {
            List<Player> players = getInstance().gameController.getState().getPlayers();
            return new EndScreenSceneController(players, getInstance().gameController);
        }),
        SETTINGS(SettingsSceneController::new);

        private final Supplier<SceneController> controller;

        /**
         * Creates a new SceneType.
         *
         * @param controller The controller to use for the scene.
         */
        SceneType(final Supplier<SceneController> controller) {
            this.controller = controller;
        }
    }

    /**
     * Loads the given {@link SceneType} and shows it on the {@link Stage}.
     *
     * @param sceneType The type of the scene to load.
     */
    @DoNotTouch
    public void loadScene(final SceneType sceneType) {
        Platform.runLater(() -> {
            final SceneController newController = sceneType.controller.get();
            System.out.println("Loading scene: " + sceneType);
            Region newRoot = newController.buildView();
            if (stage.getScene() == null) {
                Scene initialScene = new Scene(new StackPane(), 800, 600); // Default
                initialScene.setFill(javafx.scene.paint.Color.web("#1f1f2e"));
                stage.setScene(initialScene);
            }
            stage.getScene().setFill(javafx.scene.paint.Color.web("#1f1f2e"));
            stage.getScene().setRoot(newRoot);
            stage.setTitle(newController.getTitle());
        });
    }
}
