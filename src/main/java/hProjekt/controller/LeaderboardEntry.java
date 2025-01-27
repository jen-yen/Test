package hProjekt.controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Represents a single entry in the leaderboard.
 * This class holds the data for a player's performance, including their name,
 * AI status, timestamp of the game, and score.
 * Properties are used to allow easy integration with JavaFX bindings for UI
 * elements.
 */
public class LeaderboardEntry {
    private final StringProperty playerName;
    private final BooleanProperty ai;
    private final StringProperty timestamp;
    private final IntegerProperty score;

    /**
     * Constructs a LeaderboardEntry with the specified data.
     *
     * @param playerName The name of the player.
     * @param ai         Whether the player is an AI (true) or human (false).
     * @param timestamp  The timestamp of when the entry was recorded.
     * @param score      The player's score.
     */
    public LeaderboardEntry(String playerName, boolean ai, String timestamp, int score) {
        this.playerName = new SimpleStringProperty(playerName);
        this.ai = new SimpleBooleanProperty(ai);
        this.timestamp = new SimpleStringProperty(timestamp);
        this.score = new SimpleIntegerProperty(score);
    }

    /**
     * Gets the player's name.
     *
     * @return The player's name as a String.
     */
    public String getPlayerName() {
        return playerName.get();
    }

    /**
     * Returns the player's name as a JavaFX property for UI binding.
     *
     * @return A StringProperty for the player's name.
     */
    public StringProperty playerNameProperty() {
        return playerName;
    }

    /**
     * Checks whether the player is an AI.
     *
     * @return True if the player is an AI, false otherwise.
     */
    public boolean isAi() {
        return ai.get();
    }

    /**
     * Returns the AI status as a JavaFX property for UI binding.
     *
     * @return A BooleanProperty representing whether the player is an AI.
     */
    public BooleanProperty aiProperty() {
        return ai;
    }

    /**
     * Gets the timestamp for the entry.
     *
     * @return The timestamp as a String.
     */
    public String getTimestamp() {
        return timestamp.get();
    }

    /**
     * Returns the timestamp as a JavaFX property for UI binding.
     *
     * @return A StringProperty for the timestamp.
     */
    public StringProperty timestampProperty() {
        return timestamp;
    }

    /**
     * Gets the player's score.
     *
     * @return The score as an integer.
     */
    public int getScore() {
        return score.get();
    }

    /**
     * Returns the player's score as a JavaFX property for UI binding.
     *
     * @return An IntegerProperty for the player's score.
     */
    public IntegerProperty scoreProperty() {
        return score;
    }
}
