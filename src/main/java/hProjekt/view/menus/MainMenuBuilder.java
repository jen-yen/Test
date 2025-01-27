package hProjekt.view.menus;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Builder;

/**
 * Builder for the Main Menu.
 */
public class MainMenuBuilder implements Builder<Region> {
    private final Runnable loadSetupGameScene;
    private final Runnable loadSettingsAction;
    private final Runnable loadAboutSceneAction;
    private final Runnable loadLeaderboardAction;
    private final Runnable quitAction;

    /**
     * Constructor for the MainMenuBuilder.
     *
     * @param loadSetupGameScene    the action to load the setup game scene
     * @param loadLeaderboardAction the action to load the leaderboard
     * @param loadSettingsAction    the action to load the settings
     * @param quitAction            the action to quit the application
     * @param loadAboutSceneAction  the action to load the about scene
     */
    public MainMenuBuilder(Runnable loadSetupGameScene, Runnable loadLeaderboardAction, Runnable loadSettingsAction,
            Runnable quitAction, Runnable loadAboutSceneAction) {
        this.loadSetupGameScene = loadSetupGameScene;
        this.loadSettingsAction = loadSettingsAction;
        this.quitAction = quitAction;
        this.loadAboutSceneAction = loadAboutSceneAction;
        this.loadLeaderboardAction = loadLeaderboardAction;
    }

    @Override
    public Region build() {
        StackPane root = new StackPane();

        // Set background color
        root.setStyle("-fx-background-color: #1f1f2e;");

        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(50));

        // Use Image Logo instead of Text Font (as font is not loading...)
        ImageView logo;
        try {
            logo = new ImageView(new Image(getClass().getResourceAsStream("/images/dampfross_logo_white.png")));
            logo.setFitWidth(500);
            logo.setPreserveRatio(true);
        } catch (NullPointerException e) {
            logo = new ImageView();
            System.out.println("Error: Logo not found. Make sure the path is correct.");
        }

        // Create Start Game Button
        Button startGameButton = new Button("Create Game");
        startGameButton.setMinWidth(200);
        startGameButton.setOnAction(event -> {
            loadSetupGameScene.run();
        });
        startGameButton.getStyleClass().add("button");

        // Create Leaderboard Button
        Button leaderboardButton = new Button("Leaderboard");
        leaderboardButton.setMinWidth(200);
        leaderboardButton.setOnAction(event -> {
            loadLeaderboardAction.run();
        });
        leaderboardButton.getStyleClass().add("button");

        // Create Settings Button
        Button settingsButton = new Button("Settings");
        settingsButton.setMinWidth(200);
        settingsButton.setOnAction(event -> {
            loadSettingsAction.run();
        });
        settingsButton.getStyleClass().add("button");

        // Add them to the box in the middle
        centerBox.getChildren().addAll(logo, startGameButton, leaderboardButton, settingsButton);

        // Create box for Exit Button
        HBox bottomBox = new HBox();
        bottomBox.setPadding(new Insets(20));
        bottomBox.setAlignment(Pos.BOTTOM_LEFT);

        // Create Exit Button
        Button exitButton = new Button("Exit");
        exitButton.setMinWidth(150);
        exitButton.setOnAction(event -> {
            quitAction.run();
        });
        exitButton.getStyleClass().add("button");

        // Create About Button
        Button aboutButton = new Button("About");
        aboutButton.setMinWidth(150);
        aboutButton.setOnAction(event -> {
            loadAboutSceneAction.run();
        });
        aboutButton.getStyleClass().add("button");

        // Add both buttons to the bottom box with spacing
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        bottomBox.getChildren().addAll(exitButton, spacer, aboutButton);

        BorderPane layout = new BorderPane();
        layout.setCenter(centerBox);
        layout.setBottom(bottomBox);
        root.getChildren().add(layout);

        // Add CSS style
        root.getStylesheets().add(getClass().getResource("/css/mainmenu.css").toExternalForm());

        // Ensure no button is initially focused
        root.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                root.requestFocus();
            }
        });

        return root;
    }
}
