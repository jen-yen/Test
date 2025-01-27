package hProjekt.view;

import java.util.function.Consumer;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Builder;

/**
 * The builder for the game board.
 * It creates the layout for the game board, including the map, overlays, and
 * the end game button.
 */
public class GameBoardBuilder implements Builder<Region> {
    private final Region map;
    private final Consumer<ActionEvent> endButtonAction;
    private Region gameInfoOverlay;
    private Region playerOverlay;
    private Region rollDiceOverlay;
    private Region spinCityOverlay;
    private Region cityOverlay;
    private Region confirmationOverlay;
    private Pane confirmationOverlayContainer;
    private HBox bottomCenterContainer;

    /**
     * Creates a new game board builder.
     *
     * @param map                 the map to display
     * @param gameInfoOverlay     the overlay for game information
     * @param playerOverlay       the overlay for player information
     * @param rollDiceOverlay     the overlay for rolling the dice
     * @param spinCityOverlay     the overlay for choosing the city
     * @param cityOverlay         the overlay for city information
     * @param confirmationOverlay the overlay for confirmation dialogs
     * @param endButtonAction     the action to execute when the end button is
     *                            pressed
     */
    public GameBoardBuilder(final Region map, final Region gameInfoOverlay, final Region playerOverlay,
            final Region rollDiceOverlay, final Region spinCityOverlay, final Region cityOverlay,
            final Region confirmationOverlay,
            Consumer<ActionEvent> endButtonAction) {
        this.map = map;
        this.gameInfoOverlay = gameInfoOverlay;
        this.playerOverlay = playerOverlay;
        this.rollDiceOverlay = rollDiceOverlay;
        this.spinCityOverlay = spinCityOverlay;
        this.cityOverlay = cityOverlay;
        this.confirmationOverlay = confirmationOverlay;
        this.endButtonAction = endButtonAction;
    }

    @Override
    public Region build() {
        BorderPane mapRoot = new BorderPane();
        mapRoot.setCenter(map);

        // Debug Button
        Button endScreenButton = new Button("Stop Game");
        endScreenButton.setOnAction(endButtonAction::accept);
        endScreenButton.getStylesheets().add(getClass().getResource("/css/setupgamemenu.css").toExternalForm());
        endScreenButton.getStyleClass().add("button");

        // Wrap the button in a VBox for padding and alignment
        VBox topRightContainer = new VBox(endScreenButton);
        topRightContainer.setPadding(new Insets(10));
        topRightContainer.setAlignment(Pos.TOP_RIGHT); // Ensure alignment within the VBox
        topRightContainer.setMaxWidth(Region.USE_PREF_SIZE); // Prevent stretching
        topRightContainer.setMaxHeight(Region.USE_PREF_SIZE); // Prevent stretching

        // Create containers for overlays
        VBox playerOverlayContainer = new VBox(playerOverlay);
        playerOverlayContainer.setPadding(new Insets(10));
        playerOverlayContainer.setMaxHeight(Region.USE_PREF_SIZE);
        playerOverlayContainer.setMaxWidth(Region.USE_PREF_SIZE);

        VBox cityOverlayContainer = new VBox(cityOverlay);
        cityOverlayContainer.setPadding(new Insets(10));
        cityOverlayContainer.setMaxHeight(Region.USE_PREF_SIZE);
        cityOverlayContainer.setMaxWidth(Region.USE_PREF_SIZE);

        confirmationOverlayContainer = new VBox(confirmationOverlay);
        confirmationOverlayContainer.setPadding(new Insets(10));
        confirmationOverlayContainer.setMaxHeight(Region.USE_PREF_SIZE);
        confirmationOverlayContainer.setMaxWidth(playerOverlayContainer.getMaxWidth());
        confirmationOverlayContainer.setPrefWidth(playerOverlayContainer.getPrefWidth());
        confirmationOverlayContainer.setMinWidth(playerOverlayContainer.getPrefWidth());

        VBox gameInfoOverlayContainer = new VBox(gameInfoOverlay);
        gameInfoOverlayContainer.setPadding(new Insets(10));
        gameInfoOverlayContainer.setMaxHeight(Region.USE_PREF_SIZE);
        gameInfoOverlayContainer.setMaxWidth(Region.USE_PREF_SIZE);

        VBox topLeftContainer = new VBox(10);
        topLeftContainer.setPadding(new Insets(10));
        topLeftContainer.setMaxWidth(Region.USE_PREF_SIZE);
        topLeftContainer.setAlignment(Pos.TOP_LEFT);

        // Füge beide Container hinzu
        topLeftContainer.getChildren().addAll(playerOverlayContainer, cityOverlayContainer);

        bottomCenterContainer = new HBox(rollDiceOverlay);
        bottomCenterContainer.setAlignment(Pos.BOTTOM_CENTER);

        // Root layout
        StackPane root = new StackPane();
        root.getChildren().addAll(mapRoot, topLeftContainer, gameInfoOverlayContainer, spinCityOverlay,
                bottomCenterContainer, topRightContainer);

        // Position the overlays
        StackPane.setAlignment(topLeftContainer, Pos.TOP_LEFT);
        StackPane.setAlignment(gameInfoOverlayContainer, Pos.TOP_CENTER);
        StackPane.setAlignment(topRightContainer, Pos.TOP_RIGHT);
        StackPane.setAlignment(spinCityOverlay, Pos.BOTTOM_RIGHT);
        StackPane.setAlignment(bottomCenterContainer, Pos.BOTTOM_CENTER); // Change to BOTTOM_CENTER

        // Allow the map to process mouse events when overlays don't consume them
        makeOverlayTransparentForMouseEvents(playerOverlayContainer);
        makeOverlayTransparentForMouseEvents(cityOverlayContainer);
        makeOverlayTransparentForMouseEvents(gameInfoOverlayContainer);
        makeOverlayTransparentForMouseEvents(rollDiceOverlay);
        makeOverlayTransparentForMouseEvents(topRightContainer);
        makeOverlayTransparentForMouseEvents(spinCityOverlay);
        makeOverlayTransparentForMouseEvents(confirmationOverlayContainer);
        makeOverlayTransparentForMouseEvents(bottomCenterContainer);

        return root;
    }

    /**
     * Ensures that a Node only consumes mouse events it actually needs and passes
     * through all others.
     *
     * @param node the node to make transparent for unnecessary mouse events
     */
    private void makeOverlayTransparentForMouseEvents(Region node) {
        node.setPickOnBounds(false); // Ensures only visible parts react to mouse events
    }

    /**
     * Adds the confirmation overlay to the bottom center of the screen.
     */
    public void addConfirmationOverlay() {
        if (bottomCenterContainer.getChildren().contains(confirmationOverlayContainer)) {
            return;
        }
        bottomCenterContainer.getChildren().add(confirmationOverlayContainer);
    }

    /**
     * Removes the confirmation overlay from the bottom center of the screen.
     */
    public void removeConfirmationOverlay() {
        bottomCenterContainer.getChildren().remove(confirmationOverlayContainer);
    }
}
