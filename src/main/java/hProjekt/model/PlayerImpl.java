package hProjekt.model;

import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.Nullable;
import org.tudalgo.algoutils.student.annotation.DoNotTouch;
import org.tudalgo.algoutils.student.annotation.StudentImplementationRequired;

import hProjekt.Config;
import hProjekt.controller.AiController;
import javafx.scene.paint.Color;

/**
 * Default implementation of {@link Player}.
 */
public class PlayerImpl implements Player {
    private final HexGrid hexGrid;
    private final String name;
    private final int id;
    private final Color color;
    private final Class<? extends AiController> aiController;
    private int credits;

    @DoNotTouch("Please don't create a public Contructor, use the Builder instead.")
    private PlayerImpl(final HexGrid hexGrid, final Color color, final int id, final String name,
            final Class<? extends AiController> ai) {
        this.hexGrid = hexGrid;
        this.color = color;
        this.id = id;
        this.name = name;
        this.aiController = ai;
        this.credits = Config.STARTING_CREDITS;
    }

    @Override
    @StudentImplementationRequired("P1.1")
    public HexGrid getHexGrid() {
        return this.hexGrid;
    }

    @Override
    @StudentImplementationRequired("P1.1")
    public String getName() {
        return this.name;
    }

    @Override
    @StudentImplementationRequired("P1.1")
    public int getID() {
        return this.id;
    }

    @Override
    @StudentImplementationRequired("P1.1")
    public Color getColor() {
        return this.color;
    }

    @Override
    @StudentImplementationRequired("P1.1")
    public boolean isAi() {
        return this.aiController != null;
    }

    @Override
    @StudentImplementationRequired("P1.2")
    public int getCredits() {
        return this.credits;
    }

    @Override
    @StudentImplementationRequired("P1.2")
    public void addCredits(int amount) {
        this.credits += amount;
    }

    @Override
    @StudentImplementationRequired("P1.2")
    public boolean removeCredits(int amount) {
        this.credits -= amount;
        return amount > 0 && this.credits >= amount;
    }

    @Override
    public String toString() {
        return String.format("Player %d %s (%s)", getID(), getName(), getColor());
    }

    @Override
    public Class<? extends AiController> getAiController() {
        return aiController;
    }

    @Override
    public Map<Set<TilePosition>, Edge> getRails() {
        return getHexGrid().getRails(this);
    }

    /**
     * Builder for {@link PlayerImpl}.
     * Allows to create a new player and modify its properties before building it.
     */
    @DoNotTouch
    public static class Builder {
        private int id;
        private Color color;
        private @Nullable String name;
        private @Nullable Class<? extends AiController> aiController;

        /**
         * Creates a new builder for a player with the given id.
         *
         * @param id the id of the player to create
         */
        public Builder(final int id) {
            this.id = id;
            color(null);
        }

        /**
         * Returns the color of the player.
         *
         * @return the color of the player
         */
        public Color getColor() {
            return this.color;
        }

        /**
         * Sets the color of the player.
         *
         * @param playerColor the color of the player
         * @return this builder
         */
        public Builder color(final Color playerColor) {
            this.color = playerColor == null
                    ? new Color(
                            Config.RANDOM.nextDouble(),
                            Config.RANDOM.nextDouble(),
                            Config.RANDOM.nextDouble(),
                            1)
                    : playerColor;
            return this;
        }

        /**
         * Returns the name of the player.
         *
         * @return the name of the player
         */
        public @Nullable String getName() {
            return this.name;
        }

        /**
         * Sets the name of the player.
         *
         * @param playerName the name of the player
         * @return this builder
         */
        public Builder name(final @Nullable String playerName) {
            this.name = playerName;
            return this;
        }

        /**
         * Returns the name of the player or a default name if no name was set.
         * The default name is "Player" followed by the id of the player.
         *
         * @return the name of the player or a default name if no name was set
         */
        public String nameOrDefault() {
            return this.name == null ? String.format("Player%d", this.id) : this.name;
        }

        /**
         * Sets the id of the player.
         *
         * @param newId the id of the player
         * @return this builder
         */
        public Builder id(final int newId) {
            this.id = newId;
            return this;
        }

        /**
         * Returns the id of the player.
         *
         * @return the id of the player
         */
        public int getId() {
            return this.id;
        }

        /**
         * Returns whether the player is an AI.
         *
         * @return whether the player is an AI
         */
        public boolean isAi() {
            return this.aiController != null;
        }

        /**
         * Sets whether the player is an AI.
         *
         * @param ai the ai controller of the player
         * @return this builder
         */
        public Builder ai(final Class<? extends AiController> ai) {
            this.aiController = ai;
            return this;
        }

        /**
         * Builds the player with the properties set in this builder.
         *
         * @param grid the grid the player is on
         * @return the player with the properties set in this builder
         */
        public Player build(final HexGrid grid) {
            return new PlayerImpl(grid, this.color, this.id, nameOrDefault(), this.aiController);
        }
    }
}
