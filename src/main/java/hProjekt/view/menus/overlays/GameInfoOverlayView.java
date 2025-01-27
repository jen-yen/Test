
package hProjekt.view.menus.overlays;

import hProjekt.model.Player;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 * Overlay for displaying game information.
 * Contains the current phase, round, and active player.
 */
public class GameInfoOverlayView extends HBox {

    private final Label phaseLabel;
    private final Label roundLabel;
    private final Label playerStatusLabel;

    /**
     * Constructor for the GameInfoOverlayView.
     */
    public GameInfoOverlayView() {

        this.setSpacing(15);
        this.setPadding(new Insets(10));
        this.setAlignment(Pos.CENTER);
        this.setBackground(
                new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.5), new CornerRadii(10), Insets.EMPTY)));

        // Phase label with styling
        phaseLabel = createStyledLabel("Building phase");

        // Round label with styling
        roundLabel = createStyledLabel("Round: 1");

        // Player status label (initially no active player)
        playerStatusLabel = new Label("No active player");
        playerStatusLabel.setTextFill(Color.WHITE);
        playerStatusLabel.setFont(new Font("Arial", 14));
        playerStatusLabel.setPadding(new Insets(5));
        playerStatusLabel.setBackground(
                new Background(new BackgroundFill(Color.rgb(33, 33, 44), new CornerRadii(5), Insets.EMPTY)));

        // Add all labels to the HBox
        this.getChildren().addAll(phaseLabel, roundLabel, playerStatusLabel);
    }

    /**
     * Creates a styled label with a background and padding.
     *
     * @param text the text to display on the label
     * @return the styled label
     */
    private Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.setTextFill(Color.WHITE);
        label.setFont(new Font("Arial", 14));
        label.setPadding(new Insets(5, 15, 5, 15));
        label.setBackground(
                new Background(new BackgroundFill(Color.rgb(33, 33, 44), new CornerRadii(5), Insets.EMPTY)));
        return label;
    }

    /**
     * Updates the phase label with the current phase.
     *
     * @param phase the current phase
     */
    public void setPhase(String phase) {
        phaseLabel.setText("Phase: " + phase);
    }

    /**
     * Updates the round label with the current round.
     *
     * @param round the current round
     */
    public void setRound(int round) {
        roundLabel.setText("Round: " + round);
    }

    /**
     * Updates the player status label with the active player.
     *
     * @param player the active player
     */
    public void setPlayerStatus(Player player) {
        String previousPlayer = playerStatusLabel.getText();

        if (player != null) {
            String playerName = player.getName();
            Color playerColor = player.getColor();
            playerStatusLabel.setText(playerName + " is active");
            playerStatusLabel.setTextFill(playerColor); // Set the color to the player's color
        } else {
            playerStatusLabel.setText("No active player");
            playerStatusLabel.setTextFill(Color.WHITE); // Default color if no active player
        }
        if (!playerStatusLabel.getText().equals(previousPlayer)) {
            playPulseAnimation(playerStatusLabel);
        }

    }

    /**
     * Plays a pulse animation on the given label.
     *
     * @param label the label to animate
     */
    private void playPulseAnimation(Label label) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(300), label);
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(2.0);
        scaleTransition.setToY(2.0);
        scaleTransition.setCycleCount(2);
        scaleTransition.setAutoReverse(true);
        scaleTransition.play();
    }
}
