package hProjekt.view.menus;

import hProjekt.controller.gui.SettingsController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Builder;

/**
 * Builder for the Settings view.
 */
public class SettingsBuilder implements Builder<Region> {
    private final Runnable returnToMainMenuAction;
    private final SettingsController settingsController = new SettingsController();

    /**
     * Constructor for the SettingsBuilder.
     *
     * @param returnToMainMenuAction the action to return to the main menu
     */
    public SettingsBuilder(Runnable returnToMainMenuAction) {
        this.returnToMainMenuAction = returnToMainMenuAction;
    }

    @Override
    public Region build() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1f1f2e;");
        root.setPadding(new Insets(20));

        // Top Buttons
        HBox topButtons = new HBox(10);
        topButtons.setAlignment(Pos.CENTER_LEFT);
        Button backButton = new Button("Back to Main Menu");
        backButton.setOnAction(event -> {
            returnToMainMenuAction.run();
            updateUIFromController();
        });
        backButton.getStyleClass().add("button");

        Button resetButton = new Button("Back to Default Values");
        resetButton.setOnAction(event -> {
            settingsController.resetToDefaults();
            updateUIFromController();
        });
        resetButton.getStyleClass().add("button");

        Label titleLabel = new Label("Settings");
        titleLabel.getStyleClass().add("text-title");
        titleLabel.setAlignment(Pos.CENTER);

        Region leftSpacer = new Region();
        Region rightSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);

        // Add all elements
        topButtons.getChildren().addAll(backButton, leftSpacer, titleLabel, rightSpacer, resetButton);
        root.setTop(topButtons);

        // Main Layout: Grid Pane for 3x2 matrix
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setAlignment(Pos.CENTER);

        VBox gameSettingsBox = createCategoryBox("Game Settings", createGameSettingsContent());
        VBox creditSettingsBox = createCategoryBox("Credit Settings", createCreditSettingsContent());
        VBox mapSettingsBox = createCategoryBox("Map Settings", createMapSettingsContent());
        VBox mapBalancingBox = createCategoryBox("Map Balancing Settings", createMapBalancingContent());
        VBox buildingCostsBox = createCategoryBox("Building Costs", createBuildingCostContent());
        VBox drivingCostsBox = createCategoryBox("Driving Costs", createDrivingCostContent());

        gameSettingsBox.setPrefSize(380, 250);
        creditSettingsBox.setPrefSize(380, 250);
        mapSettingsBox.setPrefSize(380, 250);
        mapBalancingBox.setPrefSize(380, 250);
        buildingCostsBox.setPrefSize(380, 250);
        drivingCostsBox.setPrefSize(380, 250);

        grid.add(gameSettingsBox, 0, 0);
        grid.add(mapSettingsBox, 1, 0);
        grid.add(buildingCostsBox, 2, 0);
        grid.add(creditSettingsBox, 0, 1);
        grid.add(mapBalancingBox, 1, 1);
        grid.add(drivingCostsBox, 2, 1);

        root.setCenter(grid);

        // Bottom Buttons
        HBox bottomButtons = new HBox(20);
        bottomButtons.setAlignment(Pos.CENTER);
        bottomButtons.setPadding(new Insets(20, 0, 0, 0));

        Button saveButton = new Button("Save");
        saveButton.setOnAction(event -> settingsController.applySettings());
        saveButton.getStyleClass().add("save-button");

        bottomButtons.getChildren().addAll(saveButton);
        root.setBottom(bottomButtons);

        root.getStylesheets().add(getClass().getResource("/css/settings.css").toExternalForm());
        return root;
    }

    private VBox createCategoryBox(String title, Pane content) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));
        box.setAlignment(Pos.TOP_CENTER);
        box.setStyle("-fx-background-color: #2b2b3b; -fx-background-radius: 10; -fx-border-radius: 10;");

        Label boxTitle = new Label(title);
        boxTitle.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
        box.getChildren().addAll(boxTitle, content);
        return box;
    }

    /**
     * Creates the content for the Game Settings category.
     *
     * @return the content pane
     */
    private Pane createGameSettingsContent() {
        GridPane pane = createBaseGridPane();
        addDefaultHeader(pane);

        // Dice Sides
        Label diceSidesLabel = new Label("Dice Sides:");
        Spinner<Integer> diceSidesSpinner = new Spinner<>(1, 9, settingsController.diceSides.get());
        diceSidesSpinner.valueProperty()
                .addListener((obs, oldValue, newValue) -> settingsController.diceSides.set(newValue));
        settingsController.diceSides.addListener(
                (obs, oldValue, newValue) -> diceSidesSpinner.getValueFactory().setValue(newValue.intValue()));
        Label diceDefault = new Label("6");

        // Min Players
        Label minPlayersLabel = new Label("Min Players:");
        Spinner<Integer> minPlayersSpinner = new Spinner<>(2, 6, settingsController.minPlayers.get());
        minPlayersSpinner.valueProperty()
                .addListener((obs, oldValue, newValue) -> settingsController.minPlayers.set(newValue));
        settingsController.minPlayers.addListener(
                (obs, oldValue, newValue) -> minPlayersSpinner.getValueFactory().setValue(newValue.intValue()));
        Label minPlayersDefault = new Label("2");

        // Max Players
        Label maxPlayersLabel = new Label("Max Players:");
        Spinner<Integer> maxPlayersSpinner = new Spinner<>(2, 6, settingsController.maxPlayers.get());
        maxPlayersSpinner.valueProperty()
                .addListener((obs, oldValue, newValue) -> settingsController.maxPlayers.set(newValue));
        settingsController.maxPlayers.addListener(
                (obs, oldValue, newValue) -> maxPlayersSpinner.getValueFactory().setValue(newValue.intValue()));
        Label maxPlayersDefault = new Label("6");

        // Add components to the pane
        pane.add(diceSidesLabel, 0, 1);
        pane.add(diceSidesSpinner, 1, 1);
        pane.add(diceDefault, 2, 1);
        pane.add(minPlayersLabel, 0, 2);
        pane.add(minPlayersSpinner, 1, 2);
        pane.add(minPlayersDefault, 2, 2);
        pane.add(maxPlayersLabel, 0, 3);
        pane.add(maxPlayersSpinner, 1, 3);
        pane.add(maxPlayersDefault, 2, 3);

        return pane;
    }

    /**
     * Creates the content for the Credit Settings category.
     *
     * @return the content pane
     */
    private Pane createCreditSettingsContent() {
        GridPane pane = createBaseGridPane();
        addDefaultHeader(pane);

        // Starting Credits
        Label startingCreditsLabel = new Label("Starting Credits:");
        Spinner<Integer> startingCreditsSpinner = new Spinner<>(10, 50, settingsController.startingCredits.get());
        startingCreditsSpinner.valueProperty()
                .addListener((obs, oldValue, newValue) -> settingsController.startingCredits.set(newValue));
        settingsController.startingCredits.addListener(
                (obs, oldValue, newValue) -> startingCreditsSpinner.getValueFactory().setValue(newValue.intValue()));
        Label startingCreditsDefault = new Label("20");

        // City Connection Bonus
        Label cityConnectionBonusLabel = new Label("City Connection Bonus:");
        Spinner<Integer> cityConnectionBonusSpinner = new Spinner<>(1, 20,
                settingsController.cityConnectionBonus.get());
        cityConnectionBonusSpinner.valueProperty()
                .addListener((obs, oldValue, newValue) -> settingsController.cityConnectionBonus.set(newValue));
        settingsController.cityConnectionBonus.addListener((obs, oldValue, newValue) -> cityConnectionBonusSpinner
                .getValueFactory().setValue(newValue.intValue()));
        Label cityBonusDefault = new Label("6");

        // Add components to the pane
        pane.add(startingCreditsLabel, 0, 1);
        pane.add(startingCreditsSpinner, 1, 1);
        pane.add(startingCreditsDefault, 2, 1);
        pane.add(cityConnectionBonusLabel, 0, 2);
        pane.add(cityConnectionBonusSpinner, 1, 2);
        pane.add(cityBonusDefault, 2, 2);

        return pane;
    }

    /**
     * Creates the content for the Map Settings category.
     *
     * @return the content pane
     */
    private Pane createMapSettingsContent() {
        GridPane pane = createBaseGridPane();
        addDefaultHeader(pane);

        // Map Scale
        Label mapScaleLabel = new Label("Map Scale:");
        Slider mapScaleSlider = createSlider(10, 30, settingsController.mapScale.get());
        mapScaleSlider.valueProperty()
                .addListener((obs, oldValue, newValue) -> settingsController.mapScale.set(newValue.intValue()));
        settingsController.mapScale
                .addListener((obs, oldValue, newValue) -> mapScaleSlider.setValue(newValue.intValue()));
        Label mapScaleDefault = new Label("15");

        // Starting Cities
        Label startingCitiesLabel = new Label("Starting Cities:");
        Spinner<Integer> startingCitiesSpinner = new Spinner<>(1, 10, settingsController.startingCities.get());
        startingCitiesSpinner.valueProperty()
                .addListener((obs, oldValue, newValue) -> settingsController.startingCities.set(newValue));
        settingsController.startingCities.addListener(
                (obs, oldValue, newValue) -> startingCitiesSpinner.getValueFactory().setValue(newValue.intValue()));
        Label startingCitiesDefault = new Label("3");

        // Number of Cities
        Label numberOfCitiesLabel = new Label("Cities amount:");
        Slider numberOfCitiesSlider = createSlider(2, 100, settingsController.numberOfCities.get());
        numberOfCitiesSlider.setSnapToTicks(true);
        numberOfCitiesSlider.setMajorTickUnit(10);
        numberOfCitiesSlider.setMinorTickCount(4);
        numberOfCitiesSlider.valueProperty()
                .addListener((obs, oldValue, newValue) -> settingsController.numberOfCities.set(newValue.intValue()));
        settingsController.numberOfCities
                .addListener((obs, oldValue, newValue) -> numberOfCitiesSlider.setValue(newValue.intValue()));
        Label numberOfCitiesDefault = new Label("36");

        // Mountain Radius
        Label mountainRadiusLabel = new Label("Mountain Radius:");
        Spinner<Integer> mountainRadiusSpinner = new Spinner<>(1, 5, settingsController.mountainRadius.get());
        mountainRadiusSpinner.valueProperty()
                .addListener((obs, oldValue, newValue) -> settingsController.mountainRadius.set(newValue));
        settingsController.mountainRadius.addListener(
                (obs, oldValue, newValue) -> mountainRadiusSpinner.getValueFactory().setValue(newValue.intValue()));
        Label mountainRadiusDefault = new Label("1");

        // City Radius
        Label cityRadiusLabel = new Label("City Radius:");
        Spinner<Integer> cityRadiusSpinner = new Spinner<>(1, 5, settingsController.cityRadius.get());
        cityRadiusSpinner.valueProperty()
                .addListener((obs, oldValue, newValue) -> settingsController.cityRadius.set(newValue));
        settingsController.cityRadius.addListener(
                (obs, oldValue, newValue) -> cityRadiusSpinner.getValueFactory().setValue(newValue.intValue()));
        Label cityRadiusDefault = new Label("3");

        // Add components to the pane
        pane.add(mapScaleLabel, 0, 1);
        pane.add(mapScaleSlider, 1, 1);
        pane.add(mapScaleDefault, 2, 1);
        pane.add(startingCitiesLabel, 0, 2);
        pane.add(startingCitiesSpinner, 1, 2);
        pane.add(startingCitiesDefault, 2, 2);
        pane.add(numberOfCitiesLabel, 0, 3);
        pane.add(numberOfCitiesSlider, 1, 3);
        pane.add(numberOfCitiesDefault, 2, 3);
        pane.add(mountainRadiusLabel, 0, 4);
        pane.add(mountainRadiusSpinner, 1, 4);
        pane.add(mountainRadiusDefault, 2, 4);
        pane.add(cityRadiusLabel, 0, 5);
        pane.add(cityRadiusSpinner, 1, 5);
        pane.add(cityRadiusDefault, 2, 5);

        return pane;
    }

    /**
     * Creates the content for the Map Balancing Settings category.
     *
     * @return the content pane
     */
    private Pane createMapBalancingContent() {
        GridPane pane = createBaseGridPane();
        addDefaultHeader(pane);

        // City Base Probability
        Label cityBaseProbLabel = new Label("City Base Probability:");
        Slider cityBaseProbSlider = createSlider(0.0, 1.0, settingsController.cityBaseProbability.get());
        cityBaseProbSlider.valueProperty().addListener(
                (obs, oldValue, newValue) -> settingsController.cityBaseProbability.set(newValue.doubleValue()));
        settingsController.cityBaseProbability
                .addListener((obs, oldValue, newValue) -> cityBaseProbSlider.setValue(newValue.doubleValue()));
        Label cityBaseDefault = new Label("0.3");

        // City At Coast Probability
        Label cityAtCoastProbLabel = new Label("City At Coast Probability:");
        Slider cityAtCoastProbSlider = createSlider(0.0, 0.5, settingsController.cityAtCoastProbability.get());
        cityAtCoastProbSlider.valueProperty().addListener(
                (obs, oldValue, newValue) -> settingsController.cityAtCoastProbability.set(newValue.doubleValue()));
        settingsController.cityAtCoastProbability
                .addListener((obs, oldValue, newValue) -> cityAtCoastProbSlider.setValue(newValue.doubleValue()));
        Label cityAtCoastDefault = new Label("0.1");

        // Near Mountain Probability
        Label nearMountainProbLabel = new Label("Near Mountain Probability:");
        Slider nearMountainProbSlider = createSlider(0.0, 0.25, settingsController.nearMountainProbability.get());
        nearMountainProbSlider.valueProperty().addListener(
                (obs, oldValue, newValue) -> settingsController.nearMountainProbability.set(newValue.doubleValue()));
        settingsController.nearMountainProbability
                .addListener((obs, oldValue, newValue) -> nearMountainProbSlider.setValue(newValue.doubleValue()));
        Label nearMountainDefault = new Label("0.05");

        // Near City Probability
        Label nearCityProbLabel = new Label("Near City Probability:");
        Slider nearCityProbSlider = createSlider(0.000, 0.01, settingsController.nearCityProbability.get());
        nearCityProbSlider.valueProperty().addListener(
                (obs, oldValue, newValue) -> settingsController.nearCityProbability.set(newValue.doubleValue()));
        settingsController.nearCityProbability
                .addListener((obs, oldValue, newValue) -> nearCityProbSlider.setValue(newValue.doubleValue()));
        Label nearCityDefault = new Label("0.001");

        // Add components to the pane
        pane.add(cityBaseProbLabel, 0, 1);
        pane.add(cityBaseProbSlider, 1, 1);
        pane.add(cityBaseDefault, 2, 1);
        pane.add(cityAtCoastProbLabel, 0, 2);
        pane.add(cityAtCoastProbSlider, 1, 2);
        pane.add(cityAtCoastDefault, 2, 2);
        pane.add(nearMountainProbLabel, 0, 3);
        pane.add(nearMountainProbSlider, 1, 3);
        pane.add(nearMountainDefault, 2, 3);
        pane.add(nearCityProbLabel, 0, 4);
        pane.add(nearCityProbSlider, 1, 4);
        pane.add(nearCityDefault, 2, 4);

        return pane;
    }

    /**
     * Creates the content for the Building Costs category.
     *
     * @return the content pane
     */
    private Pane createBuildingCostContent() {
        GridPane pane = createBaseGridPane();
        addDefaultHeader(pane);

        // Plain <-> Plain
        Label plainToPlainLabel = new Label("Plain <-> Plain:");
        Spinner<Integer> plainToPlainSpinner = new Spinner<>(1, 10, settingsController.plainToPlainBuildingCost.get());
        plainToPlainSpinner.valueProperty()
                .addListener((obs, oldValue, newValue) -> settingsController.plainToPlainBuildingCost.set(newValue));
        settingsController.plainToPlainBuildingCost.addListener(
                (obs, oldValue, newValue) -> plainToPlainSpinner.getValueFactory().setValue(newValue.intValue()));
        Label plainToPlainDefault = new Label("1");

        // Plain <-> Mountain
        Label plainToMountainLabel = new Label("Plain <-> Mountain:");
        Spinner<Integer> plainToMountainSpinner = new Spinner<>(1, 10,
                settingsController.plainToMountainBuildingCost.get());
        plainToMountainSpinner.valueProperty()
                .addListener((obs, oldValue, newValue) -> settingsController.plainToMountainBuildingCost.set(newValue));
        settingsController.plainToMountainBuildingCost.addListener(
                (obs, oldValue, newValue) -> plainToMountainSpinner.getValueFactory().setValue(newValue.intValue()));
        Label plainToMountainDefault = new Label("3");

        // Mountain <-> Mountain
        Label mountainToMountainLabel = new Label("Mountain <-> Mountain:");
        Spinner<Integer> mountainToMountainSpinner = new Spinner<>(1, 10,
                settingsController.mountainToMountainBuildingCost.get());
        mountainToMountainSpinner.valueProperty().addListener(
                (obs, oldValue, newValue) -> settingsController.mountainToMountainBuildingCost.set(newValue));
        settingsController.mountainToMountainBuildingCost.addListener(
                (obs, oldValue, newValue) -> mountainToMountainSpinner.getValueFactory().setValue(newValue.intValue()));
        Label mountainToMountainDefault = new Label("5");

        // Add components to the pane
        pane.add(plainToPlainLabel, 0, 1);
        pane.add(plainToPlainSpinner, 1, 1);
        pane.add(plainToPlainDefault, 2, 1);
        pane.add(plainToMountainLabel, 0, 2);
        pane.add(plainToMountainSpinner, 1, 2);
        pane.add(plainToMountainDefault, 2, 2);
        pane.add(mountainToMountainLabel, 0, 3);
        pane.add(mountainToMountainSpinner, 1, 3);
        pane.add(mountainToMountainDefault, 2, 3);

        return pane;
    }

    /**
     * Creates the content for the Driving Costs category.
     *
     * @return the content pane
     */
    private Pane createDrivingCostContent() {
        GridPane pane = createBaseGridPane();
        addDefaultHeader(pane);

        // Plain <-> Plain
        Label plainToPlainLabel = new Label("Plain <-> Plain:");
        Spinner<Integer> plainToPlainSpinner = new Spinner<>(1, 10, settingsController.plainToPlainDrivingCost.get());
        plainToPlainSpinner.valueProperty()
                .addListener((obs, oldValue, newValue) -> settingsController.plainToPlainDrivingCost.set(newValue));
        settingsController.plainToPlainDrivingCost.addListener(
                (obs, oldValue, newValue) -> plainToPlainSpinner.getValueFactory().setValue(newValue.intValue()));
        Label plainToPlainDefault = new Label("1");

        // Plain <-> Mountain
        Label plainToMountainLabel = new Label("Plain <-> Mountain:");
        Spinner<Integer> plainToMountainSpinner = new Spinner<>(1, 10,
                settingsController.plainToMountainDrivingCost.get());
        plainToMountainSpinner.valueProperty()
                .addListener((obs, oldValue, newValue) -> settingsController.plainToMountainDrivingCost.set(newValue));
        settingsController.plainToMountainDrivingCost.addListener(
                (obs, oldValue, newValue) -> plainToMountainSpinner.getValueFactory().setValue(newValue.intValue()));
        Label plainToMountainDefault = new Label("2");

        // Mountain <-> Mountain
        Label mountainToMountainLabel = new Label("Mountain <-> Mountain:");
        Spinner<Integer> mountainToMountainSpinner = new Spinner<>(1, 10,
                settingsController.mountainToMountainDrivingCost.get());
        mountainToMountainSpinner.valueProperty().addListener(
                (obs, oldValue, newValue) -> settingsController.mountainToMountainDrivingCost.set(newValue));
        settingsController.mountainToMountainDrivingCost.addListener(
                (obs, oldValue, newValue) -> mountainToMountainSpinner.getValueFactory().setValue(newValue.intValue()));
        Label mountainToMountainDefault = new Label("1");

        // Add components to the pane
        pane.add(plainToPlainLabel, 0, 1);
        pane.add(plainToPlainSpinner, 1, 1);
        pane.add(plainToPlainDefault, 2, 1);
        pane.add(plainToMountainLabel, 0, 2);
        pane.add(plainToMountainSpinner, 1, 2);
        pane.add(plainToMountainDefault, 2, 2);
        pane.add(mountainToMountainLabel, 0, 3);
        pane.add(mountainToMountainSpinner, 1, 3);
        pane.add(mountainToMountainDefault, 2, 3);

        return pane;
    }

    /**
     * Updates the UI from the controller.
     *
     * This is absolutely not a good way to do this, but it works.
     */
    private void updateUIFromController() {
        // Game Settings
        settingsController.diceSides.set(settingsController.diceSides.get());
        settingsController.minPlayers.set(settingsController.minPlayers.get());
        settingsController.maxPlayers.set(settingsController.maxPlayers.get());

        // Credit Settings
        settingsController.startingCredits.set(settingsController.startingCredits.get());
        settingsController.cityConnectionBonus.set(settingsController.cityConnectionBonus.get());

        // Map Settings
        settingsController.mapScale.set(settingsController.mapScale.get());
        settingsController.startingCities.set(settingsController.startingCities.get());
        settingsController.mountainRadius.set(settingsController.mountainRadius.get());
        settingsController.cityRadius.set(settingsController.cityRadius.get());

        // Map Balancing Settings
        settingsController.cityBaseProbability.set(settingsController.cityBaseProbability.get());
        settingsController.cityAtCoastProbability.set(settingsController.cityAtCoastProbability.get());
        settingsController.nearMountainProbability.set(settingsController.nearMountainProbability.get());
        settingsController.nearCityProbability.set(settingsController.nearCityProbability.get());

        // Building Costs
        settingsController.plainToPlainBuildingCost.set(settingsController.plainToPlainBuildingCost.get());
        settingsController.plainToMountainBuildingCost.set(settingsController.plainToMountainBuildingCost.get());
        settingsController.mountainToMountainBuildingCost.set(settingsController.mountainToMountainBuildingCost.get());

        // Driving Costs
        settingsController.plainToPlainDrivingCost.set(settingsController.plainToPlainDrivingCost.get());
        settingsController.plainToMountainDrivingCost.set(settingsController.plainToMountainDrivingCost.get());
        settingsController.mountainToMountainDrivingCost.set(settingsController.mountainToMountainDrivingCost.get());
    }

    /**
     * Creates a default header for the settings categories.
     *
     * @param pane the pane to add the header to
     */
    private void addDefaultHeader(GridPane pane) {
        Label defaultHeader = new Label("Default");
        defaultHeader.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");
        pane.add(defaultHeader, 2, 0);
    }

    /**
     * Creates a base GridPane with default settings.
     *
     * @return the GridPane
     */
    private GridPane createBaseGridPane() {
        GridPane pane = new GridPane();
        pane.setHgap(5);
        pane.setVgap(5);
        pane.setAlignment(Pos.CENTER);
        return pane;
    }

    /**
     * Creates a Slider with default settings.
     *
     * @param min   the minimum value
     * @param max   the maximum value
     * @param value the initial value
     * @return the Slider
     */
    private Slider createSlider(double min, double max, double value) {
        Slider slider = new Slider(min, max, value);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit((max - min) / 5);
        return slider;
    }
}
