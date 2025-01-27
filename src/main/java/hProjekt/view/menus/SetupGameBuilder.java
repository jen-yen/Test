package hProjekt.view.menus;

import java.util.ArrayList;
import java.util.List;

import hProjekt.Config;
import hProjekt.controller.AiController;
import hProjekt.model.GameSetup;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Builder;
import javafx.util.Callback;

/**
 * Builder for the Setup Game menu.
 */
public class SetupGameBuilder implements Builder<Region> {
    private final Runnable loadMainMenuAction;
    private final Runnable loadGameSceneAction;
    private final GameSetup gameSetup;

    private final VBox playerContainer;
    private final List<HBox> playerBoxes;
    private final Button addPlayerButton;
    private final int maxPlayers = Config.MAX_PLAYERS;
    private final List<String> availableColors = List.of(
            "#ea3323", "#ff8b00", "#fde01a", "#1eb253", "#017cf3", "#9c78fe");

    /**
     * Constructor for the SetupGameBuilder.
     *
     * @param loadGameSceneAction the action to load the game scene
     * @param loadMainMenuAction  the action to load the main menu
     * @param gameSetup           the game setup object
     */
    public SetupGameBuilder(Runnable loadGameSceneAction, Runnable loadMainMenuAction, GameSetup gameSetup) {
        this.loadGameSceneAction = loadGameSceneAction;
        this.loadMainMenuAction = loadMainMenuAction;
        this.gameSetup = gameSetup;

        playerBoxes = new ArrayList<>();
        playerContainer = new VBox(10);
        playerContainer.setAlignment(Pos.CENTER);

        addPlayerButton = new Button("+ Add Player");
        addPlayerButton.getStyleClass().add("button-add");
        addPlayerButton.setOnAction(event -> addPlayer());
    }

    @Override
    public Region build() {
        BorderPane root = new BorderPane();

        // Create "Back to Main Menu" button
        Button backButton = new Button("Back to Main Menu");
        backButton.getStyleClass().add("button-back");
        backButton.setOnAction(event -> loadMainMenuAction.run());
        HBox backButtonContainer = new HBox(backButton);
        backButtonContainer.setAlignment(Pos.TOP_LEFT);
        backButtonContainer.setPadding(new Insets(10));

        // Create "Start Game" button
        Button startGameButton = new Button("Start Game");
        startGameButton.getStyleClass().add("start-game-button");
        startGameButton.setOnAction(event -> {
            List<String> playerNames = new ArrayList<>();
            List<Class<? extends AiController>> aiControllerList = new ArrayList<>();

            for (int i = 0; i < playerBoxes.size(); i++) {
                HBox playerBox = playerBoxes.get(i);
                TextField playerNameField = (TextField) playerBox.lookup(".text-field");
                String playerName = playerNameField != null && !playerNameField.getText().trim().isEmpty()
                        ? playerNameField.getText()
                        : "Player " + (i + 1);
                playerNames.add(playerName);

                ComboBox<Class<? extends AiController>> cpuSelection = (ComboBox<Class<? extends AiController>>) playerBox
                        .lookup(".combo-box"); // This isn't possible in a better way without rewriting this whole class
                aiControllerList.add(cpuSelection.getValue());
            }

            gameSetup.setPlayerNames(playerNames);
            for (int i = 0; i < aiControllerList.size(); i++) {
                gameSetup.setPlayerAsAi(i, aiControllerList.get(i));
            }

            loadGameSceneAction.run();
        });
        HBox startButtonContainer = new HBox(startGameButton);
        startButtonContainer.setAlignment(Pos.TOP_RIGHT);
        startButtonContainer.setPadding(new Insets(10));

        // Top bar layout containing both buttons
        BorderPane topBar = new BorderPane();
        topBar.setLeft(backButtonContainer);
        topBar.setRight(startButtonContainer);

        // Main content in center
        VBox mainContent = new VBox(20);
        mainContent.setAlignment(Pos.CENTER);
        mainContent.setPadding(new Insets(20));

        // Title
        Label titleLabel = new Label("Setup Game");
        titleLabel.getStyleClass().add("text-title");

        addPlayer(); // Start with Player 1
        addPlayer(); // Start with Player 2

        // Map selection container
        VBox mapSelectionWrapper = new VBox();
        mapSelectionWrapper.setAlignment(Pos.CENTER); // Center the inner container
        mapSelectionWrapper.setPadding(new Insets(20)); // Padding around the entire box

        HBox mapSelectionContainer = new HBox(10);
        mapSelectionContainer.setAlignment(Pos.CENTER_LEFT);
        mapSelectionContainer.setPadding(new Insets(10, 20, 10, 20));
        mapSelectionContainer
                .setStyle("-fx-background-color: #2a2a3b; -fx-background-radius: 8px; -fx-border-radius: 8px;");
        mapSelectionContainer.setMaxWidth(400); // Limit the width of the box

        Label mapLabel = new Label("Select a Map:");
        mapLabel.getStyleClass().add("label");

        ComboBox<String> mapSelector = new ComboBox<>();
        mapSelector.getItems().addAll("Generate Random Map"); // No other maps implemented yet
        mapSelector.setMaxWidth(200);
        mapSelector.setValue("Generate Random Map");
        mapSelector.getStyleClass().add("combo-box");
        mapSelector.setOnAction(event -> {
            if (gameSetup != null) {
                gameSetup.setMapSelection(mapSelector.getValue());
            }
        });

        // Add label and dropdown to the container
        mapSelectionContainer.getChildren().addAll(mapLabel, mapSelector);
        mapSelectionWrapper.getChildren().add(mapSelectionContainer);

        mainContent.getChildren().addAll(titleLabel, playerContainer, addPlayerButton, mapSelectionWrapper);
        root.setTop(topBar);
        root.setCenter(mainContent);
        root.getStylesheets().add(getClass().getResource("/css/setupgamemenu.css").toExternalForm());

        return root;
    }

    /**
     * Adds a player to the player container.
     */
    private void addPlayer() {
        if (playerBoxes.size() >= maxPlayers)
            return;

        HBox outerBox = new HBox();
        outerBox.setAlignment(Pos.CENTER);

        HBox playerBox = new HBox(5);
        playerBox.setAlignment(Pos.CENTER_LEFT);

        ImageView trashIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/trash.png")));
        trashIcon.setFitWidth(20);
        trashIcon.setFitHeight(20);
        trashIcon.setPreserveRatio(true);

        Button removeButton = new Button();
        removeButton.setGraphic(trashIcon);
        removeButton.setStyle("-fx-background-color: transparent; -fx-padding: 5;");
        removeButton.setOnAction(event -> removePlayer(outerBox));

        if (playerBoxes.size() < 2) {
            removeButton.setVisible(false);
        }

        int playerNumber = playerBoxes.size() + 1;
        TextField playerNameField = new TextField("Player " + playerNumber);
        playerNameField.setPromptText("Enter Player Name");
        playerNameField.setMaxWidth(150);

        ComboBox<Class<? extends AiController>> cpuSelection = new ComboBox<>();
        cpuSelection.getStyleClass().add("combo-box");
        cpuSelection.getItems().add(null);
        cpuSelection.getItems().addAll(Config.AVAILABLE_AI_CONTROLLER);
        Callback<ListView<Class<? extends AiController>>, ListCell<Class<? extends AiController>>> listCellFactory = p -> {
            return new ListCell<>() {
                @Override
                protected void updateItem(Class<? extends AiController> item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else if (item == null) {
                        setText("Human Player");
                    } else {
                        setText(item.getSimpleName());
                    }
                }
            };
        };
        cpuSelection.setCellFactory(listCellFactory);
        cpuSelection.setButtonCell(listCellFactory.call(null));
        cpuSelection.setValue(null);

        HBox.setMargin(cpuSelection, new Insets(0, 5, 0, 5));
        playerBox.getChildren().addAll(removeButton, playerNameField, cpuSelection);

        // Add color selection buttons
        HBox colorContainer = new HBox(5);
        colorContainer.setAlignment(Pos.CENTER_LEFT);
        List<Button> colorButtons = new ArrayList<>();
        for (int i = 0; i < availableColors.size(); i++) {
            String colorHex = availableColors.get(i);
            Button colorButton = new Button();
            colorButton.setStyle("-fx-background-color: " + colorHex
                    + "; -fx-min-width: 20px; -fx-min-height: 20px; -fx-max-width: 20px; -fx-max-height: 20px; -fx-background-radius: 6; -fx-border-radius: 6;");

            // Default selection logic
            if (i == playerNumber - 1) {
                colorButton.setStyle(colorButton.getStyle()
                        + " -fx-border-color: white; -fx-border-width: 3px; -fx-border-insets: -3px;");
                gameSetup.setPlayerColor(playerNumber - 1, colorHex);
            }

            colorButton.setOnAction(event -> {
                colorButtons.forEach(btn -> btn.setStyle(btn.getStyle()
                        .replaceAll("-fx-border-color: white; -fx-border-width: 3px; -fx-border-insets: -3px;", "")));
                colorButton.setStyle(colorButton.getStyle()
                        + " -fx-border-color: white; -fx-border-width: 3px; -fx-border-insets: -3px;");
                gameSetup.setPlayerColor(playerNumber - 1, colorHex);
            });

            colorButtons.add(colorButton);
            colorContainer.getChildren().add(colorButton);
        }

        playerBox.getChildren().add(colorContainer);
        HBox.setMargin(colorContainer, new Insets(0, 20, 0, 0));

        outerBox.getChildren().add(playerBox);
        playerContainer.getChildren().add(outerBox);
        playerBoxes.add(outerBox);

        updateAddPlayerButtonVisibility();
    }

    /**
     * Removes a player from the player container.
     *
     * @param outerBox the outer box containing the player
     */
    private void removePlayer(HBox outerBox) {
        int indexToRemove = playerBoxes.indexOf(outerBox);
        if (playerBoxes.size() > 2) {
            playerContainer.getChildren().remove(outerBox);
            playerBoxes.remove(outerBox);
            if (gameSetup != null) {
                gameSetup.removePlayer(indexToRemove);
            }
            updateAddPlayerButtonVisibility();
        }
    }

    /**
     * Updates the visibility of the "Add Player" button.
     */
    private void updateAddPlayerButtonVisibility() {
        addPlayerButton.setVisible(playerBoxes.size() < maxPlayers);
    }
}
