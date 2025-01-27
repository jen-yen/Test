package hProjekt.controller.gui.scene;

import hProjekt.view.menus.LeaderboardBuilder;
import javafx.scene.layout.Region;
import javafx.util.Builder;

/**
 * Controller for managing the Leaderboard Scene.
 * It uses the {@link LeaderboardBuilder} to construct the UI and handles
 * transitions to the main menu.
 */
public class LeaderboardSceneController implements SceneController {

    private final Builder<Region> builder;

    /**
     * Initializes the LeaderboardSceneController.
     * Sets up the builder for creating the leaderboard view and defines the action
     * for returning to the main menu.
     */
    public LeaderboardSceneController() {
        this.builder = new LeaderboardBuilder(SceneController::loadMainMenuScene);
    }

    /**
     * Returns the builder for the leaderboard scene.
     *
     * @return A {@link Builder} instance for constructing the leaderboard view.
     */
    @Override
    public Builder<Region> getBuilder() {
        return builder;
    }

    /**
     * Returns the title of the leaderboard scene.
     *
     * @return A string representing the scene title.
     */
    @Override
    public String getTitle() {
        return "Leaderboard";
    }
}
