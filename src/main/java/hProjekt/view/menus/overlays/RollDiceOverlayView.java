package hProjekt.view.menus.overlays;

import java.util.Random;
import java.util.function.Consumer;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 * Overlay for rolling dice with a button and displaying the dice result as an
 * image.
 */
public class RollDiceOverlayView extends StackPane {
    private final ImageView diceImageView;
    private final Button rollDiceButton;
    private final Random random = new Random();

    public RollDiceOverlayView(final Consumer<ActionEvent> rollDiceAction) {
        // Configure the main container
        VBox container = new VBox(10);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(10));
        container.setBackground(new Background(new BackgroundFill(
                Color.rgb(42, 42, 59, 0.8), new CornerRadii(8), Insets.EMPTY)));
        container.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); -fx-background-radius: 10; -fx-padding: 10;");
        container.setMaxWidth(200);
        container.setMaxHeight(100);

        // Ensure the overlay does not block interactions outside
        this.setPickOnBounds(false);

        // ImageView to display the dice image
        diceImageView = new ImageView();
        diceImageView.setFitWidth(50);
        diceImageView.setFitHeight(50);
        diceImageView.setPreserveRatio(true);
        diceImageView.setImage(new Image(getClass().getResourceAsStream("/images/dice/dice1.png")));

        // Button to trigger dice roll
        rollDiceButton = new Button("Roll Dice!");
        rollDiceButton.setFont(new Font("Arial", 14));
        rollDiceButton.setTextFill(Color.WHITE);
        rollDiceButton.setStyle(
                "-fx-background-color: #0078d7; -fx-background-radius: 10px; -fx-padding: 8px 15px; -fx-text-fill: white;");
        rollDiceButton.setCursor(Cursor.HAND);
        rollDiceButton.setOnMouseEntered(event -> rollDiceButton.setCursor(Cursor.HAND));
        rollDiceButton.setOnMouseExited(event -> rollDiceButton.setCursor(Cursor.DEFAULT));
        rollDiceButton.setOnAction(rollDiceAction::accept);

        // Add components to the container
        container.getChildren().addAll(diceImageView, rollDiceButton);

        // Add the container to the overlay
        this.getChildren().add(container);
        this.setAlignment(Pos.BOTTOM_CENTER); // Align at the bottom center
        this.setPadding(new Insets(10)); // Same padding as PlayerOverlayView
    }

    /**
     * Rolls the dice with an animation and updates the dice image based on the
     * result.
     *
     * @param rolledNumber the number rolled on the dice
     */
    public void rollDice(int rolledNumber) {
        // Animation to simulate rolling dice
        Timeline timeline = new Timeline();
        for (int i = 0; i < 15; i++) {
            int randomDice = random.nextInt(6) + 1; // Random number between 1 and 6
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(i * 50), event -> {
                String diceImagePath = "/images/dice/dice" + randomDice + ".png";
                diceImageView.setImage(new Image(getClass().getResourceAsStream(diceImagePath)));
            }));
        }

        // Final frame to set the actual dice roll
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(750), event -> {
            String finalDiceImagePath = "/images/dice/dice" + rolledNumber + ".png";
            diceImageView.setImage(new Image(getClass().getResourceAsStream(finalDiceImagePath)));
        }));

        timeline.play();
    }

    public void enableRollDiceButton() {
        rollDiceButton.setDisable(false);
    }

    public void disableRollDiceButton() {
        rollDiceButton.setDisable(true);
    }
}
