package hProjekt.view.menus.overlays;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hProjekt.model.Player;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

/**
 * Overlay for displaying player information.
 * Contains the player names and their credits.
 */
public class PlayerOverlayView extends VBox {
    private Map<Player, Label> playerCreditsLabels = new HashMap<>();

    /**
     * Constructor for the PlayerOverlayView.
     *
     * @param players the list of players to display
     */
    public PlayerOverlayView(List<Player> players) {
        configureOverlayStyle();

        // Title label
        Label titleLabel = new Label("Players");
        titleLabel.setFont(new javafx.scene.text.Font("Arial", 18));
        titleLabel.setTextFill(Color.WHITE);
        this.getChildren().add(titleLabel);

        // Player information
        for (Player player : players) {
            this.getChildren().add(createPlayerBox(player));
        }
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
        this.setMaxWidth(200);
        this.setPrefHeight(USE_COMPUTED_SIZE);
    }

    /**
     * Creates a HBox with player information.
     *
     * @param player the player to display
     * @return the HBox with player information
     */
    private HBox createPlayerBox(Player player) {
        HBox playerBox = new HBox(10);
        playerBox.setAlignment(Pos.CENTER_LEFT);

        // Player Names with custom player color
        Label nameLabel = new Label(player.getName());
        nameLabel.setFont(new javafx.scene.text.Font("Arial", 14));
        nameLabel.setTextFill(player.getColor());

        // Credits Score
        Label creditsLabel = new Label("Credits: " + player.getCredits());
        creditsLabel.setFont(new javafx.scene.text.Font("Arial", 14));
        creditsLabel.setTextFill(Color.WHITE);
        playerCreditsLabels.put(player, creditsLabel);

        playerBox.getChildren().addAll(nameLabel, creditsLabel);

        // Add "CPU" indicator if the player is an AI
        if (player.isAi()) {
            Label cpuLabel = new Label("CPU");
            cpuLabel.setFont(new javafx.scene.text.Font("Arial", 10));
            cpuLabel.setTextFill(Color.WHITE);
            cpuLabel.setPadding(new Insets(2, 6, 2, 6)); // Padding for better appearance
            cpuLabel.setBackground(new Background(new BackgroundFill(Color.GRAY, new CornerRadii(5), Insets.EMPTY)));
            playerBox.getChildren().add(cpuLabel);
        }

        return playerBox;
    }

    /**
     * Updates the player credits labels.
     *
     * @param players the list of players to update
     */
    public void updatePlayerCredits(List<Player> players) {
        for (Player player : players) {
            Label creditsLabel = playerCreditsLabels.get(player);
            if (creditsLabel != null) {
                int currentCredits = extractCredits(creditsLabel.getText());
                int targetCredits = player.getCredits();
                if (currentCredits != targetCredits) {
                    animateCreditChange(creditsLabel, currentCredits, targetCredits);
                }
            }
            playerCreditsLabels.get(player).setText("Credits: " + player.getCredits());
        }
    }

    /**
     * Extrahiert die numerischen Credits aus einem Textstring
     *
     * @param text Der Text, z.B. "Credits: 25"
     * @return Der extrahierte Creditwert oder 0 bei Fehlern
     */
    private int extractCredits(String text) {
        try {
            return Integer.parseInt(text.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Animiert die schrittweise Aktualisierung der Credits
     *
     * @param creditsLabel Das anzuzeigende Label
     * @param start        Startwert der Credits
     * @param end          Zielwert der Credits
     */
    private void animateCreditChange(Label creditsLabel, int start, int end) {
        int steps = Math.abs(end - start);
        int totalDuration = 800;
        double stepTime = (double) totalDuration / steps;
        Timeline timeline = new Timeline();

        creditsLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));

        for (int i = 0; i <= steps; i++) {
            int value = start + (int) Math.signum(end - start) * i;
            KeyFrame keyFrame = new KeyFrame(
                    Duration.millis(i * stepTime),
                    event -> creditsLabel.setText("Credits: " + value));
            timeline.getKeyFrames().add(keyFrame);
        }

        KeyFrame endFrame = new KeyFrame(
                Duration.millis(totalDuration),
                event -> creditsLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14)));
        timeline.getKeyFrames().add(endFrame);
        timeline.play();
    }

}
