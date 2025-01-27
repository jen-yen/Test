package hProjekt.model;

import java.util.List;

import hProjekt.controller.AiController;

/**
 * Holds information on how to set up a game.
 */
public interface GameSetup {

    /**
     * Adds a player with the specified name and AI status.
     *
     * @param playerName name of the player
     * @param isAi       whether the player is an AI
     * @param color      the color of the player
     */
    void addOrUpdatePlayer(String playerName, Class<? extends AiController> aiController, int playerIndex,
            String color);

    /**
     * Removes a player at the specified index.
     *
     * @param playerIndex index of the player to remove
     */
    void removePlayer(int playerIndex);

    /**
     * Sets the player names.
     *
     * @param playerNames list of player names
     */
    void setPlayerNames(List<String> playerNames);

    /**
     * Returns the list of player names.
     *
     * @return list of player names
     */
    List<String> getPlayerNames();

    /**
     * Sets whether a player is an AI or not.
     *
     * @param playerIndex index of the player
     * @param isAi        true if the player is an AI, false otherwise
     */
    void setPlayerAsAi(int playerIndex, Class<? extends AiController> aiController);

    /**
     * Returns the AI controller for a player.
     *
     * @param playerIndex index of the player
     * @return the AI controller for the player
     */
    Class<? extends AiController> getPlayerAiController(int playerIndex);

    /**
     * Returns whether a player is an AI or not.
     *
     * @param playerIndex index of the player
     * @return true if the player is an AI, false otherwise
     */
    boolean isPlayerAi(int playerIndex);

    /**
     * Sets the color for a player.
     *
     * @param playerIndex index of the player
     * @param color       the color to set for the player
     */
    void setPlayerColor(int playerIndex, String color);

    /**
     * Gets the color of a player.
     *
     * @param playerIndex index of the player
     * @return the color of the player
     */
    String getPlayerColor(int playerIndex);

    /**
     * Sets the map selection.
     *
     * @param map the map selected
     */
    void setMapSelection(String map);

    /**
     * Returns the selected map.
     *
     * @return the selected map
     */
    String getMapSelection();
}
