package hProjekt.controller.gui.scene;

import java.util.List;

import hProjekt.controller.GameController;
import hProjekt.controller.LeaderboardController;
import hProjekt.model.Player;
import hProjekt.view.menus.EndScreenBuilder;
import javafx.scene.layout.Region;
import javafx.util.Builder;

/**
 * The controller for the end screen scene.
 */
public class EndScreenSceneController implements SceneController {
    private final Builder<Region> builder;

    /**
     * Creates a new end screen scene controller.
     * Saves the data of all the players to the leaderboard.
     *
     * @param players the players to display on the end screen
     */
    public EndScreenSceneController(List<Player> players, GameController gameController) {
        for (Player player : players) {
            LeaderboardController.savePlayerData(player.getName(), player.getCredits(), player.isAi());
        }
        this.builder = new EndScreenBuilder(SceneController::loadMainMenuScene, players);

        gameController.stop();
    }

    @Override
    public String getTitle() {
        return "End Screen";
    }

    @Override
    public Builder<Region> getBuilder() {
        return builder;
    }
}
