package hProjekt.model;

import java.util.Map;
import java.util.Set;

import org.tudalgo.algoutils.student.annotation.DoNotTouch;

import hProjekt.controller.AiController;
import javafx.scene.paint.Color;

/**
 * Represents a player in the game.
 * Players have a name, a color, a number of credits, and a set of rails.
 */
@DoNotTouch
public interface Player {

    /**
     * Returns the hexGrid instance
     *
     * @return the hexGrid instance
     */
    HexGrid getHexGrid();

    /**
     * Returns the name of the player.
     *
     * @return the name of the player
     */
    String getName();

    /**
     * Returns the Player ID, aka the Index of the Player, starting with 1
     *
     * @return the Player ID
     */
    int getID();

    /**
     * Returns the color of the player.
     *
     * @return the color of the player
     */
    Color getColor();

    /**
     * Returns true if the player is an AI, false otherwise.
     *
     * @return true if the player is an AI, false otherwise
     */
    default boolean isAi() {
        return false;
    }

    /**
     * Returns the ai controller for the player or null if the player is not an ai.
     *
     * @return the ai controller for the player or null if the player is not an ai
     */
    Class<? extends AiController> getAiController();

    /**
     * Returns the number of credits the player has.
     *
     * @return the number of credits the player has
     */
    public int getCredits();

    /**
     * Adds the given amount of credits to the player.
     *
     * @param amount the amount of credits to add
     */
    public void addCredits(int amount);

    /**
     * Tries to remove the given amount of credits from the player.
     * Expects a positive amount.
     *
     * @param amount the amount of credits to remove
     * @return {@code true} if the player had enough credits and they were removed,
     *         {@code false} otherwise
     */
    public boolean removeCredits(int amount);

    /**
     * Returns all rails of the player.
     *
     * @return all rails of the player
     */
    Map<Set<TilePosition>, Edge> getRails();
}
