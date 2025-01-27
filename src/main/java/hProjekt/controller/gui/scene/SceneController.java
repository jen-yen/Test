package hProjekt.controller.gui.scene;

import org.tudalgo.algoutils.student.annotation.DoNotTouch;

import hProjekt.controller.gui.Controller;
import hProjekt.controller.gui.SceneSwitcher;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * The controller for a scene.
 */
@DoNotTouch
public interface SceneController extends Controller {
    /**
     * Specifies the title of the {@link Stage}.
     *
     * @return The title of the {@link Stage}.
     */
    String getTitle();

    // --Setup Methods-- //

    /**
     * Terminates the application.
     */
    static void quit() {
        Platform.exit();
    }

    /**
     * Loads the main menu scene.
     */
    static void loadMainMenuScene() {
        SceneSwitcher.getInstance().loadScene(SceneSwitcher.SceneType.MAIN_MENU);
    }

    /**
     * Loads the setup game scene.
     */
    static void loadSetupGameScene() {
        SceneSwitcher.getInstance().loadScene(SceneSwitcher.SceneType.SETUP_GAME_MENU);
    }

    /**
     * Loads the create game scene.
     */
    static void loadCreateGameScene() {
        SceneSwitcher.getInstance().loadScene(SceneSwitcher.SceneType.SETUP_GAME_MENU);
    }

    /**
     * Loads the settings scene.
     */
    static void loadSettingsScene() {
        SceneSwitcher.getInstance().loadScene(SceneSwitcher.SceneType.SETTINGS);
    }

    /**
     * Loads the leaderboard editor scene.
     */
    static void loadLeaderboardScene() {
        SceneSwitcher.getInstance().loadScene(SceneSwitcher.SceneType.LEADERBOARD);
    }

    /**
     * Loads the game scene.
     */
    static void loadGameScene() {
        SceneSwitcher.getInstance().loadScene(SceneSwitcher.SceneType.GAME_BOARD);
    }

    /**
     * Loads the about scene.
     */
    static void loadAboutScene() {
        SceneSwitcher.getInstance().loadScene(SceneSwitcher.SceneType.ABOUT);
    }

    /**
     * Loads the end screen scene with the given GameState.
     */
    static void loadEndScreenScene() {
        SceneSwitcher.getInstance().loadScene(SceneSwitcher.SceneType.END_SCREEN);
    }

}
