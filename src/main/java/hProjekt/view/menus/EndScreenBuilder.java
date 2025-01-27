package hProjekt.view.menus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import hProjekt.controller.LeaderboardController;
import hProjekt.controller.LeaderboardEntry;
import hProjekt.model.Player;
import hProjekt.view.Confetti;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Builder;

/**
 * Builder for the End Screen.
 */
public class EndScreenBuilder implements Builder<Region> {
    private final Runnable loadMainMenuAction;
    private final List<Player> players;
    private final int CREDITS_PER_STAR = 40; // Determines how many credits are required for a star

    /**
     * Constructor for the EndScreenBuilder.
     *
     * @param loadMainMenuAction the action to load the main menu
     * @param players            the list of players to display
     */
    public EndScreenBuilder(Runnable loadMainMenuAction, List<Player> players) {
        this.loadMainMenuAction = loadMainMenuAction;
        this.players = players;
    }

    @Override
    public Region build() {

        // Sort players by credits (descending)
        List<Player> sortedPlayers = new ArrayList<>(players);
        sortedPlayers.sort(Comparator.comparingInt(Player::getCredits).reversed());

        int currentHighscore = LeaderboardController.loadLeaderboardData().stream()
                .sorted((score1, score2) -> Integer.compare(score2.getScore(), score1.getScore())).findFirst()
                .map(LeaderboardEntry::getScore)
                .orElse(0);

        // Root container for the entire screen
        StackPane rootContainer = new StackPane();
        rootContainer.setStyle("-fx-background-color: #1f1f2e;");

        // Add confetti background
        Pane confettiPane = createConfettiBackground();
        confettiPane.setPickOnBounds(false);
        rootContainer.getChildren().add(confettiPane);

        // Leaderboard container
        VBox leaderboard = new VBox(20);
        leaderboard.setAlignment(Pos.CENTER_LEFT);
        leaderboard.setPadding(new Insets(0, 0, 0, 150));
        leaderboard.setMaxWidth(850);
        leaderboard.setMinWidth(850);

        // Leaderboard title
        Label title = new Label("Congratulations, " + sortedPlayers.get(0).getName() + "!");
        title.setStyle(
                "-fx-font-size: 36px; -fx-text-fill: #ffffff; -fx-font-family: Arial, sans-serif; -fx-padding: 15px 0; -fx-alignment: center;");
        title.setAlignment(Pos.CENTER);

        StackPane.setAlignment(title, Pos.TOP_CENTER);
        StackPane.setMargin(title, new Insets(40, 0, 0, 0));
        rootContainer.getChildren().add(title);

        // Highscore Label
        HBox highscoreBox = new HBox();
        highscoreBox.setAlignment(Pos.CENTER);
        highscoreBox.setPadding(new Insets(10));
        highscoreBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.35); -fx-background-radius: 10px;");
        highscoreBox.setPrefWidth(200);
        highscoreBox.setMaxWidth(200);
        highscoreBox.setPrefHeight(40);
        highscoreBox.setMaxHeight(40);

        Label highscoreLabel = new Label("Highscore: " + currentHighscore);
        highscoreLabel.setFont(new Font("Arial", 18));
        highscoreLabel.setTextFill(Color.WHITE);
        highscoreBox.getChildren().add(highscoreLabel);

        // Align highscoreBox to the top-right corner
        StackPane.setAlignment(highscoreBox, Pos.TOP_RIGHT);
        StackPane.setMargin(highscoreBox, new Insets(40, 40, 0, 0));
        rootContainer.getChildren().add(highscoreBox);

        // Table settings
        double rankWidth = 70;
        double namePlaceholderWidth = 150;
        double scorePlaceholderWidth = 60;

        // Add players to the leaderboard
        for (int i = 0; i < sortedPlayers.size(); i++) {
            Player player = sortedPlayers.get(i);

            HBox row = new HBox(20);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPrefWidth(850);
            row.setMinHeight(30);
            row.setMaxHeight(30);

            // Rank column
            HBox rankBox = new HBox();
            rankBox.setAlignment(Pos.CENTER);
            rankBox.setMinWidth(rankWidth);
            if (i < 3) {
                ImageView medal = new ImageView();
                medal.setFitHeight(40);
                medal.setFitWidth(40);
                switch (i) {
                    case 0 -> medal.setImage(new Image(getClass().getResourceAsStream("/images/gold_medal.png")));
                    case 1 -> medal.setImage(new Image(getClass().getResourceAsStream("/images/silver_medal.png")));
                    case 2 -> medal.setImage(new Image(getClass().getResourceAsStream("/images/bronze_medal.png")));
                }
                rankBox.getChildren().add(medal);
            } else {
                Label rankLabel = new Label((i + 1) + ".");
                rankLabel.setFont(new Font("Arial", 18));
                rankLabel.setTextFill(Color.WHITE);
                rankBox.getChildren().add(rankLabel);
            }

            // Player name
            Label nameLabel = new Label(player.getName());
            nameLabel.setFont(new Font("Arial", 18));
            nameLabel.setTextFill(player.getColor());
            nameLabel.setMinWidth(namePlaceholderWidth);
            nameLabel.setMaxWidth(namePlaceholderWidth);
            nameLabel.setAlignment(Pos.CENTER_LEFT);

            // Star rating
            int credits = player.getCredits();
            int stars = Math.max(1, Math.min(credits / CREDITS_PER_STAR, 5));
            HBox starsBox = new HBox(5);
            starsBox.setAlignment(Pos.CENTER_LEFT);
            for (int j = 0; j < stars; j++) {
                Label star = new Label("★");
                star.setFont(new Font("Arial", 21));
                star.setTextFill(Color.GOLD);
                starsBox.getChildren().add(star);
            }
            for (int j = stars; j < 5; j++) {
                Label star = new Label("☆");
                star.setFont(new Font("Arial", 21));
                star.setTextFill(Color.GRAY);
                starsBox.getChildren().add(star);
            }

            // Credits
            Label creditsLabel = new Label(String.valueOf(credits));
            creditsLabel.setFont(new Font("Arial", 18));
            creditsLabel.setTextFill(Color.WHITE);
            creditsLabel.setMinWidth(scorePlaceholderWidth);
            creditsLabel.setAlignment(Pos.CENTER_RIGHT);

            // Add all elements to the row
            row.getChildren().addAll(rankBox, nameLabel, starsBox, creditsLabel);
            leaderboard.getChildren().add(row);
        }

        // Back to Main Menu Button
        Button backToMenuButton = new Button("Back to Main Menu");
        backToMenuButton.setFont(new Font("Arial", 18));
        backToMenuButton.setOnAction(event -> loadMainMenuAction.run());
        backToMenuButton.setStyle(
                "-fx-background-color: #2a2a3b; -fx-text-fill: #ffffff; -fx-font-size: 16px; -fx-font-family: Arial, sans-serif; -fx-padding: 10px 20px; -fx-background-radius: 12px; -fx-border-radius: 12px; -fx-border-color: transparent; -fx-cursor: hand;");
        backToMenuButton.setOnMouseEntered(event -> backToMenuButton.setStyle(
                "-fx-background-color: #3a3a4f;-fx-text-fill: #ffffff;-fx-font-size: 16px;-fx-font-family: Arial, sans-serif;-fx-padding: 10px 20px;-fx-background-radius: 12px;-fx-border-radius: 12px;-fx-border-color: transparent;-fx-cursor: hand;"));
        backToMenuButton.setOnMouseExited(event -> backToMenuButton.setStyle(
                "-fx-background-color: #2a2a3b;-fx-text-fill: #ffffff;-fx-font-size: 16px;-fx-font-family: Arial, sans-serif;-fx-padding: 10px 20px;-fx-background-radius: 12px;-fx-border-radius: 12px;-fx-border-color: transparent;-fx-cursor: hand;"));

        StackPane.setAlignment(backToMenuButton, Pos.BOTTOM_CENTER);
        StackPane.setMargin(backToMenuButton, new Insets(20));
        rootContainer.getChildren().addAll(leaderboard, backToMenuButton);

        return rootContainer;
    }

    /**
     * Creates a Pane with confetti particles in the background.
     *
     * @return the Pane with confetti particles
     */
    private Pane createConfettiBackground() {
        Pane confettiPane = new Pane();
        confettiPane.setPickOnBounds(false);
        for (int i = 0; i < 1000; i++) {
            Confetti confetti = new Confetti(randomColor(), 800, 600);
            confetti.animate();
            confettiPane.getChildren().add(confetti);
        }
        return confettiPane;
    }

    /**
     * Returns a random color.
     *
     * @return the random color
     */
    private Color randomColor() {
        return Color.color(Math.random(), Math.random(), Math.random());
    }
}
