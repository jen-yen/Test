package hProjekt.controller.gui.scene;

import hProjekt.view.menus.SettingsBuilder;
import javafx.scene.layout.Region;
import javafx.util.Builder;

/**
 * The controller for the settings scene.
 */
public class SettingsSceneController implements SceneController {
    private final Builder<Region> builder;

    /**
     * Creates a new settings scene controller.
     */
    public SettingsSceneController() {
        builder = new SettingsBuilder(
                SceneController::loadMainMenuScene);
    }

    @Override
    public String getTitle() {
        return "Settings";
    }

    @Override
    public Builder<Region> getBuilder() {
        return builder;
    }
}
