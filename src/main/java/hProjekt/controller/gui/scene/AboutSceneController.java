package hProjekt.controller.gui.scene;

import hProjekt.view.menus.AboutBuilder;
import javafx.scene.layout.Region;
import javafx.util.Builder;

/**
 * The controller for the about scene.
 */
public class AboutSceneController implements SceneController {
    private final Builder<Region> builder;

    /**
     * Creates a new about scene controller.
     */
    public AboutSceneController() {
        builder = new AboutBuilder(SceneController::loadMainMenuScene);
    }

    @Override
    public String getTitle() {
        return "About";
    }

    @Override
    public Builder<Region> getBuilder() {
        return builder;
    }
}
