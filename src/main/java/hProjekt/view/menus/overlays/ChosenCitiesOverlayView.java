package hProjekt.view.menus.overlays;

import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 * Overlay for selecting random cities (From -> To) with a spinning animation.
 */
public class ChosenCitiesOverlayView extends StackPane {

    private final Label fromCityLabel;
    private final Label toCityLabel;
    private final Button spinButton;

    /**
     * Constructor for the ChosenCitiesOverlayView.
     *
     * @param chooseCitiesAction the action to execute when the choose button is
     *                           clicked
     */
    public ChosenCitiesOverlayView(final Consumer<ActionEvent> chooseCitiesAction) {
        // Configure the main container
        VBox container = new VBox(15); // Adjust spacing
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(10));
        container.setBackground(new Background(new BackgroundFill(
                Color.rgb(42, 42, 59, 0.8), new CornerRadii(8), Insets.EMPTY)));
        container.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); -fx-background-radius: 10; -fx-padding: 10;");
        container.setMaxWidth(300);
        container.setMaxHeight(100);

        // Ensure the overlay does not block interactions outside
        this.setPickOnBounds(false);

        // From label
        Label fromLabel = new Label("From:");
        fromLabel.setTextFill(Color.WHITE);
        fromLabel.setFont(new Font("Arial", 14));

        fromCityLabel = new Label("???");
        fromCityLabel.setTextFill(Color.WHITE);
        fromCityLabel.setFont(new Font("Arial", 14));
        fromCityLabel.setStyle("-fx-background-color: #2b2b3a; -fx-padding: 5px; -fx-background-radius: 5px;");
        fromCityLabel.setMinWidth(120);
        fromCityLabel.setAlignment(Pos.CENTER);

        // To label
        Label toLabel = new Label("To:");
        toLabel.setTextFill(Color.WHITE);
        toLabel.setFont(new Font("Arial", 14));

        toCityLabel = new Label("???");
        toCityLabel.setTextFill(Color.WHITE);
        toCityLabel.setFont(new Font("Arial", 14));
        toCityLabel.setStyle("-fx-background-color: #2b2b3a; -fx-padding: 5px; -fx-background-radius: 5px;");
        toCityLabel.setMinWidth(120);
        toCityLabel.setAlignment(Pos.CENTER);

        // Arrow
        Label arrowLabel = new Label(">");
        arrowLabel.setTextFill(Color.WHITE);
        arrowLabel.setFont(new Font("Arial", 16));

        // GridPane to organize the From and To labels
        GridPane citySelectionPane = new GridPane();
        citySelectionPane.setHgap(20); // Horizontal gap
        citySelectionPane.setVgap(10); // Vertical gap

        // Add the labels to the GridPane
        citySelectionPane.add(fromLabel, 0, 0); // "From:" label in the first column
        citySelectionPane.add(toLabel, 2, 0); // "To:" label in the third column
        citySelectionPane.add(fromCityLabel, 0, 1); // From city label in the first column
        citySelectionPane.add(arrowLabel, 1, 1); // Arrow in the second column
        citySelectionPane.add(toCityLabel, 2, 1); // To city label in the third column

        citySelectionPane.setAlignment(Pos.CENTER);

        // Spin button
        spinButton = new Button("Spin");
        spinButton.setFont(new Font("Arial", 14));
        spinButton.setTextFill(Color.WHITE);
        spinButton.setStyle(
                "-fx-background-color: #0078d7; -fx-background-radius: 10px; -fx-padding: 8px 15px; -fx-cursor: hand");
        spinButton.setDisable(true); // Disabled by default
        spinButton.setOnAction(chooseCitiesAction::accept); // Set action for the button

        // Add components to the container
        container.getChildren().addAll(citySelectionPane, spinButton);

        // Add the container to the overlay
        this.getChildren().add(container);
        this.setAlignment(Pos.BOTTOM_RIGHT);
        this.setPadding(new Insets(10));
    }

    /**
     * Spins and randomly selects From and To cities.
     *
     * @param fromCity  The "From" city name for the final selection.
     * @param toCity    The "To" city name for the final selection.
     * @param cityNames The list of all available city names for the animation.
     */
    public void spinCities(String fromCity, String toCity, List<String> cityNames) {
        if (cityNames.size() < 2) {
            System.out.println("Not enough cities to spin.");
            return;
        }

        Random random = new Random();
        Timeline timeline = new Timeline();
        int iterations = 15;

        // Animation for spinning effect
        for (int i = 0; i < iterations; i++) {
            int indexFrom = random.nextInt(cityNames.size());
            int indexTo = random.nextInt(cityNames.size());
            while (indexFrom == indexTo) {
                indexTo = random.nextInt(cityNames.size());
            }

            int finalIndexFrom = indexFrom;
            int finalIndexTo = indexTo;

            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(i * 50), event -> {
                fromCityLabel.setText(cityNames.get(finalIndexFrom));
                toCityLabel.setText(cityNames.get(finalIndexTo));
            }));
        }

        // Final selection
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(iterations * 50), event -> {
            fromCityLabel.setText(fromCity);
            toCityLabel.setText(toCity);
            System.out.println("Final From: " + fromCity + ", To: " + toCity);
        }));

        timeline.play();
    }

    /**
     * Enables the Spin button.
     */
    public void enableChooseButton() {
        spinButton.setDisable(false);
    }

    /**
     * Disables the Spin button.
     */
    public void disableChooseButton() {
        spinButton.setDisable(true);
    }
}
