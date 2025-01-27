package hProjekt.model;

import java.util.ArrayList;
import java.util.List;

import hProjekt.controller.AiController;

public class GameSetupImpl implements GameSetup {
    private List<String> playerNames;
    private List<Class<? extends AiController>> aiControllerList;
    private List<String> playerColors;
    private String mapSelection;

    public GameSetupImpl() {
        this.playerNames = new ArrayList<>();
        this.aiControllerList = new ArrayList<>();
        this.playerColors = new ArrayList<>();
        this.mapSelection = "";
    }

    @Override
    public void addOrUpdatePlayer(String playerName,
            Class<? extends AiController> aiController, int playerIndex, String color) {
        if (playerIndex >= 0 && playerIndex < playerNames.size()) {
            // Update existing player
            playerNames.set(playerIndex, playerName);
            aiControllerList.set(playerIndex, aiController);
            playerColors.set(playerIndex, color);
        } else if (playerIndex >= playerNames.size()) {
            // Add new player
            playerNames.add(playerName);
            aiControllerList.add(aiController);
            playerColors.add(color);
        }
    }

    @Override
    public void removePlayer(int playerIndex) {
        if (playerIndex >= 0 && playerIndex < playerNames.size()) {
            playerNames.remove(playerIndex);
            aiControllerList.remove(playerIndex);
            if (playerIndex < playerColors.size()) {
                playerColors.remove(playerIndex);
            }
        }
    }

    @Override
    public void setPlayerNames(List<String> playerNames) {
        this.playerNames = new ArrayList<>(playerNames);
        while (aiControllerList.size() < playerNames.size()) {
            aiControllerList.add(null); // Default to non-AI
        }
        while (playerColors.size() < playerNames.size()) {
            playerColors.add(""); // Default to no color
        }
    }

    @Override
    public List<String> getPlayerNames() {
        return new ArrayList<>(playerNames);
    }

    @Override
    public void setPlayerAsAi(int playerIndex, Class<? extends AiController> aiController) {
        if (playerIndex >= 0 && playerIndex < aiControllerList.size()) {
            aiControllerList.set(playerIndex, aiController);
        }
    }

    @Override
    public boolean isPlayerAi(int playerIndex) {
        return playerIndex >= 0 && playerIndex < aiControllerList.size() && aiControllerList.get(playerIndex) != null;
    }

    @Override
    public void setMapSelection(String map) {
        this.mapSelection = map;
    }

    @Override
    public String getMapSelection() {
        return mapSelection;
    }

    @Override
    public void setPlayerColor(int playerIndex, String color) {
        if (playerIndex >= 0 && playerIndex < playerColors.size()) {
            playerColors.set(playerIndex, color);
        } else if (playerIndex >= playerColors.size()) {
            // Add color if the index is beyond the current size
            for (int i = playerColors.size(); i < playerIndex; i++) {
                playerColors.add(""); // Fill empty slots
            }
            playerColors.add(color);
        }
    }

    @Override
    public String getPlayerColor(int playerIndex) {
        if (playerIndex >= 0 && playerIndex < playerColors.size()) {
            return playerColors.get(playerIndex);
        }
        return ""; // Return empty string if index is invalid
    }

    @Override
    public Class<? extends AiController> getPlayerAiController(int playerIndex) {
        if (playerIndex >= 0 && playerIndex < aiControllerList.size()) {
            return aiControllerList.get(playerIndex);
        }
        return null;
    }
}
