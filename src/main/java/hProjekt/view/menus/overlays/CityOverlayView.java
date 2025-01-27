package hProjekt.view.menus.overlays;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import hProjekt.model.City;
import hProjekt.model.GameState;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * Overlay for displaying the list of cities.
 * Divided into unvisited and visited cities.
 */
public class CityOverlayView extends VBox {

    private final VBox cityListContainer;
    private final GameState gameState;

    /**
     * Constructor for the CityOverlayView.
     *
     * @param gameState the game state to retrieve the cities from
     */
    public CityOverlayView(GameState gameState) {
        this.gameState = gameState;
        configureOverlayStyle();

        // Title label
        Label titleLabel = new Label("Cities");
        titleLabel.setFont(new javafx.scene.text.Font("Arial", 18));
        titleLabel.setTextFill(Color.WHITE);
        this.getChildren().add(titleLabel);

        // Toggle button to switch between visited and unvisited cities
        ToggleButton visitedButton = new ToggleButton("Visited");
        ToggleButton unvisitedButton = new ToggleButton("Unvisited");

        ToggleGroup toggleGroup = new ToggleGroup();
        visitedButton.setToggleGroup(toggleGroup);
        unvisitedButton.setToggleGroup(toggleGroup);
        visitedButton.setSelected(true); // Default selection

        visitedButton.getStyleClass().add("toggle-button");
        unvisitedButton.getStyleClass().add("toggle-button");

        HBox toggleContainer = new HBox(5, visitedButton, unvisitedButton);
        toggleContainer.setAlignment(Pos.CENTER);
        this.getChildren().add(toggleContainer);

        // Container for the city list
        cityListContainer = new VBox(5);
        cityListContainer.setPadding(new Insets(10));
        cityListContainer.setAlignment(Pos.TOP_LEFT);
        this.getChildren().add(cityListContainer);
        this.getStylesheets().add(getClass().getResource("/css/toggle.css").toExternalForm());

        visitedButton.setOnAction(e -> updateCityList(true));
        unvisitedButton.setOnAction(e -> updateCityList(false));
    }

    /**
     * Styles the overlay.
     */
    private void configureOverlayStyle() {
        this.setPadding(new Insets(10));
        this.setSpacing(10);
        this.setBackground(
                new Background(new BackgroundFill(Color.rgb(42, 42, 59, 0.8), new CornerRadii(8), Insets.EMPTY)));
        this.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); -fx-background-radius: 10; -fx-padding: 10;");
        this.setAlignment(Pos.TOP_LEFT);
        this.setMinWidth(150);
        this.setPrefHeight(USE_COMPUTED_SIZE);
    }

    /**
     * Updates the city list based on the selected toggle button.
     *
     * @param showVisited true if visited cities should be shown, false otherwise
     */
    public void updateCityList(boolean showVisited) {
        cityListContainer.getChildren().clear();

        Set<City> cities = showVisited ? gameState.getChosenCities()
                : gameState.getGrid().getCities().values().stream().filter(Predicate.not(
                        gameState.getChosenCities()::contains)).collect(Collectors.toSet());

        for (City city : cities) {
            Label cityLabel = new Label(city.getName());
            cityLabel.setFont(new javafx.scene.text.Font("Arial", 14));
            cityLabel.setTextFill(Color.WHITE);
            cityListContainer.getChildren().add(cityLabel);
        }
    }
}
