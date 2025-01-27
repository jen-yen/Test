package hProjekt.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.tudalgo.algoutils.student.annotation.DoNotTouch;
import org.tudalgo.algoutils.student.annotation.StudentImplementationRequired;

import hProjekt.Config;
import hProjekt.controller.actions.IllegalActionException;
import hProjekt.controller.actions.PlayerAction;
import hProjekt.model.Edge;
import hProjekt.model.GameState;
import hProjekt.model.Player;
import hProjekt.model.PlayerState;
import hProjekt.model.Tile;
import hProjekt.model.TilePosition;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Pair;

/**
 * The PlayerController class represents a controller for a {@link Player} in
 * the game.
 * It manages the player's state, objectives, actions and all methods the UI
 * needs to interact with.
 * It receives objectives the player wants to achieve and waits for the UI or AI
 * to trigger any allowed actions. It then executes the actions and updates the
 * player's state.
 */
public class PlayerController {
    private final Player player;

    private final GameController gameController;

    private final BlockingDeque<PlayerAction> actions = new LinkedBlockingDeque<>();

    private final Property<PlayerState> playerStateProperty = new SimpleObjectProperty<>(
            new PlayerState(Set.of(), PlayerObjective.IDLE, Set.of(), Set.of(), false, Map.of(), 0));

    private PlayerObjective playerObjective = PlayerObjective.IDLE;

    private int buildingBudget = 0; // Budget during building phase

    private Set<Edge> rentedEdges = new HashSet<>();

    private boolean hasPath = false;

    private boolean hasConfirmedPath = false;

    /**
     * Creates a new {@link PlayerController} with the given {@link GameController}
     * and {@link Player}.
     *
     * @param gameController the {@link GameController} that manages the game logic
     *                       and this controller is part of.
     * @param player         the {@link Player} this controller belongs to. It is
     *                       assumed that the player is valid.
     */
    @DoNotTouch
    public PlayerController(final GameController gameController, final Player player) {
        this.gameController = gameController;
        this.player = player;
    }

    /**
     * Returns the {@link Player}.
     *
     * @return the {@link Player}.
     */
    @DoNotTouch
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns the {@link GameState}.
     *
     * @return the {@link GameState}.
     */
    private GameState getState() {
        return gameController.getState();
    }

    /**
     * Returns a {@link Property} with the current {@link PlayerState}.
     *
     * @return a {@link Property} with the current {@link PlayerState}.
     */
    @DoNotTouch
    public Property<PlayerState> getPlayerStateProperty() {
        return playerStateProperty;
    }

    /**
     * Returns the current {@link PlayerState}.
     *
     * @return the current {@link PlayerState}.
     */
    public PlayerState getPlayerState() {
        return playerStateProperty.getValue();
    }

    /**
     * Updates the {@link #playerStateProperty} with the current
     * {@link PlayerState}.
     */
    @DoNotTouch
    private void updatePlayerState() {
        playerStateProperty
                .setValue(new PlayerState(getBuildableRails(), getPlayerObjective(), getChooseableEdges(),
                        getRentedEdges(), hasPath(), getDrivableTiles(), getBuildingBudget()));
    }

    /**
     * Returns the current {@link PlayerObjective}
     *
     * @return the current {@link PlayerObjective}
     */
    @DoNotTouch
    public PlayerObjective getPlayerObjective() {
        return playerObjective;
    }

    /**
     * Sets the player objective.
     * Also updates the player state, when the objective is IDLE.
     *
     * @param nextObjective the objective to set
     */
    @DoNotTouch
    public void setPlayerObjective(final PlayerObjective nextObjective) {
        playerObjective = nextObjective;
        if (PlayerObjective.IDLE.equals(nextObjective)) {
            updatePlayerState();
        }
    }

    /**
     * Returns the building budget during the building phase.
     *
     * @return the building budget during the building phase
     */
    public int getBuildingBudget() {
        return buildingBudget;
    }

    /**
     * Sets the building budget during the building phase,
     *
     * @param amount the anount to set the building budget to
     */
    public void setBuildingBudget(int amount) {
        buildingBudget = amount;
    }

    /**
     * Returns true if the player has a path to drive, false otherwise.
     *
     * @return true if the player has a path to drive, false otherwise
     */
    public boolean hasPath() {
        return hasPath;
    }

    /**
     * Resets the hasPath flag.
     */
    public void resetHasPath() {
        this.hasPath = false;
    }

    /**
     * Returns true if the player has confirmed a path to drive, false otherwise.
     *
     * @return true if the player has confirmed a path to drive, false otherwise
     */
    public boolean hasConfirmedPath() {
        return hasConfirmedPath;
    }

    /**
     * Resets the hasConfirmedPath flag.
     */
    public void resetHasConfirmedPath() {
        this.hasConfirmedPath = false;
    }

    /**
     * Resets the rented edges.
     */
    public void resetRentedEdges() {
        rentedEdges = new HashSet<>();
    }

    /**
     * Resets several flags and values that are relevant during the driving phase.
     * This method should be called when the player chooses the path to drive.
     * It resets the hasPath flag, the hasConfirmedPath flag and the rented edges.
     */
    public void resetDrivingPhase() {
        resetHasConfirmedPath();
        resetHasPath();
        resetRentedEdges();
    }

    /**
     * Rolls the dice.
     */
    public void rollDice() {
        gameController.castDice();
    }

    /**
     * Chooses the cities to drive to.
     *
     * @see GameController#chooseCities()
     */
    public void chooseCities() {
        gameController.chooseCities();
    }

    // Process Actions

    /**
     * Gets called from viewer thread to trigger an Action. This action will then be
     * waited for using the method {@link #waitForNextAction()}.
     *
     * @param action The Action that should be triggered next
     */
    @DoNotTouch
    public void triggerAction(final PlayerAction action) {
        actions.add(action);
    }

    /**
     * Takes the next action from the queue. This method blocks until an action is
     * in the queue.
     *
     * @return The next action
     * @throws InterruptedException if the thread is interrupted while waiting for
     *                              the next action
     */
    @DoNotTouch
    public PlayerAction blockingGetNextAction() throws InterruptedException {
        return actions.take();
    }

    /**
     * Waits for the next action and executes it.
     *
     * @param nextObjective the objective to set before the action is awaited
     * @return the executed action
     */
    @DoNotTouch
    public PlayerAction waitForNextAction(final PlayerObjective nextObjective) {
        setPlayerObjective(nextObjective);
        return waitForNextAction();
    }

    /**
     * Waits for a action to be triggered, checks if the action is allowed and then
     * executes it.
     * If a {@link IllegalActionException} is thrown, the action is ignored and the
     * next action is awaited. This is done to ensure only allowed actions are
     * executed.
     *
     * @return the executed action
     */
    @DoNotTouch
    public PlayerAction waitForNextAction() {
        try {
            updatePlayerState();
            // blocking, waiting for viewing thread
            final PlayerAction action = blockingGetNextAction();

            System.out.println("TRIGGER " + action + " [" + player.getName() + "]");

            if (!getPlayerObjective().allowedActions.contains(action.getClass())) {
                throw new IllegalActionException(String.format("Illegal Action %s performed. Allowed Actions: %s",
                        action, getPlayerObjective().getAllowedActions()));
            }
            action.execute(this);
            return action;
        } catch (final IllegalActionException e) {
            // Ignore and keep going
            e.printStackTrace();
            return waitForNextAction();
        } catch (final InterruptedException e) {
            throw new RuntimeException("Main thread was interrupted!", e);
        }
    }

    /**
     * Determines if the player can build a rail on the given edge.
     * Checks if the player has enough credits. If the game is in the building
     * phase, the building cost is checked against the
     * building budget and not the players credits.
     * If the player already owns a rail on the edge, the player cannot build on it.
     *
     * @param edge the edge to check
     * @return {@code true} if the player can build a rail on the given edge,
     */
    @StudentImplementationRequired("P2.1")
    public boolean canBuildRail(Edge edge) {
        // TODO: P2.1
        return org.tudalgo.algoutils.student.Student.crash("P2.1 - Remove if implemented");
    }

    /**
     * Returns all edges the player can build a rail on.
     *
     * @return all edges the player can build a rail on
     */
    @StudentImplementationRequired("P2.1")
    public Set<Edge> getBuildableRails() {
        // TODO: P2.1
        return org.tudalgo.algoutils.student.Student.crash("P2.1 - Remove if implemented");
    }

    /**
     * Tries to build a rail on the given edge.
     * Also removes the cost of building the rail from the player's credits or
     * building budget if the game is in the building phase.
     *
     * @param edge the edge to build the rail on
     * @throws IllegalActionException if the player cannot build a rail on the given
     *                                edge
     */
    @StudentImplementationRequired("P2.2")
    public void buildRail(final Edge edge) throws IllegalActionException {
        // TODO: P2.2
        org.tudalgo.algoutils.student.Student.crash("P2.2 - Remove if implemented");
    }

    /**
     * Builds rails on the given edges.
     *
     * @param edges the edges to build the rails on
     * @throws IllegalActionException if the player cannot build rails on the given
     *                                edges
     */
    public void buildRails(final List<Edge> edges) throws IllegalActionException {
        Set<Edge> buildableRails = getBuildableRails();

        if (buildableRails.isEmpty()) {
            throw new IllegalActionException("Cannot build rails");
        }

        for (Edge edge : edges) {
            buildRail(edge);
        }
    }

    /**
     * Returns all edges the player can choose to rent.
     *
     * @return all edges the player can choose to rent
     */
    public Set<Edge> getChooseableEdges() {
        if (player.getCredits() == 0 || !getState().getGamePhaseProperty().getValue().equals(GamePhase.DRIVING_PHASE)) {
            return Set.of();
        }

        Set<Edge> builtEdges = getState().getGrid().getRails(player).values().stream().collect(Collectors.toSet());
        Set<Edge> chooseableEdges = new HashSet<>();
        chooseableEdges.addAll(builtEdges.stream()
                .flatMap(edge -> edge.getConnectedEdges().stream().filter(Edge::hasRail)
                        .filter(e -> !e.getRailOwners().contains(player)))
                .distinct().toList());

        if (chooseableEdges.isEmpty()) {
            return chooseableEdges;
        }

        final List<Pair<Edge, Integer>> edgeQueue = new ArrayList<>(
                chooseableEdges.stream().map(edge -> new Pair<>(edge, 1)).toList());
        while (!edgeQueue.isEmpty()) {
            final Pair<Edge, Integer> currentPair = edgeQueue.removeFirst();
            for (Edge edge : currentPair.getKey().getConnectedEdges().stream()
                    .filter(Edge::hasRail)
                    .filter(edge -> !edge.getRailOwners().contains(player))
                    .filter(Predicate.not(chooseableEdges::contains)).toList()) {
                int newDistance = currentPair.getValue() + 1;
                if (newDistance <= Math.min(player.getCredits(), Config.MAX_RENTABLE_DISTANCE)) {
                    edgeQueue.add(new Pair<>(edge, newDistance));
                    chooseableEdges.add(edge);
                }
            }
        }

        return chooseableEdges;
    }

    /**
     * Chooses the edges to rent.
     *
     * @param edges the edges to rent
     * @throws IllegalActionException if the player cannot rent the chosen edges or
     *                                if the player cannot afford to rent the chosen
     *                                edges or if the player chooses more than 10
     *                                edges
     */
    public void chooseEdges(final Set<Edge> edges) throws IllegalActionException {
        Set<Edge> chooseableEdges = getChooseableEdges();
        hasPath = false;

        if (!chooseableEdges.containsAll(edges)) {
            throw new IllegalActionException("Cannot choose edges");
        }
        if (edges.size() > Config.MAX_RENTABLE_DISTANCE) {
            throw new IllegalActionException("Cannot choose more than 10 edges");
        }
        if (edges.stream().reduce(0, (previous, edge) -> {
            return previous + edge.getRentingCost(player).values().stream().reduce(0, Integer::sum);
        }, Integer::sum) > player.getCredits()) {
            throw new IllegalArgumentException("Player cannot afford to rent the chosen edges");
        }

        Set<Edge> allAvailableEdges = List.of(getState().getGrid().getRails(player).values(), edges).stream()
                .flatMap(set -> set.stream())
                .filter(Edge::hasRail).collect(Collectors.toSet());
        List<Edge> pathEdges = getState().getGrid().findPath(gameController.getStartingCity().getPosition(),
                gameController.getTargetCity().getPosition(), allAvailableEdges,
                (from, to) -> getState().getGrid().getEdge(from, to).getDrivingCost(from));
        if (pathEdges.isEmpty()) {
            rentedEdges = new HashSet<>();
            return;
        }

        hasPath = true;
        rentedEdges = pathEdges.stream().filter(edge -> !edge.getRailOwners().contains(player))
                .collect(Collectors.toSet());
    }

    /**
     * Returns the edges the player has rented as an unmodifiable set.
     *
     * @return the edges the player has rented as an unmodifiable set
     */
    private Set<Edge> getRentedEdges() {
        return Collections.unmodifiableSet(rentedEdges);
    }

    /**
     * Confirms the path the player has chosen to drive.
     * If the player confirms the path, the player pays the renting costs for the
     * rented edges.
     *
     * @param confirm {@code true} if the player confirms the path, {@code false}
     *                otherwise
     */
    public void confirmPath(boolean confirm) {
        if (!confirm) {
            hasConfirmedPath = false;
            rentedEdges = new HashSet<>();
            return;
        }

        hasConfirmedPath = true;
        if (hasPath) {
            rentedEdges.stream().flatMap(edge -> edge.getRentingCost(player).entrySet().stream()).forEach(entry -> {
                entry.getKey().addCredits(entry.getValue());
                if (!player.removeCredits(entry.getValue())) {
                    throw new RuntimeException("Player unexpectedly ran out of credits");
                }
            });
            getState().addDrivingPlayer(player);
            return;
        }
    }

    /**
     * Returns {@code true} if the player can drive, {@code false} otherwise.
     * The player can drive if the game is in the driving phase and the player is a
     * driving player.
     *
     * @return {@code true} if the player can drive, {@code false} otherwise
     */
    @StudentImplementationRequired("P2.5")
    public boolean canDrive() {
        // TODO: P2.5
        return org.tudalgo.algoutils.student.Student.crash("P2.5 - Remove if implemented");
    }

    /**
     * Returns a map of drivable tiles and the path to drive to the target tile.
     * The path is a list of tiles starting from the current player position to the
     * target tile.
     * A tile can be driven to if it is reachable with the current dice roll.
     *
     * @return a map of drivable tiles and the path to drive to the target tile
     */
    public Map<Tile, List<Tile>> getDrivableTiles() {
        if (!canDrive()) {
            return Map.of();
        }

        final Set<Edge> allAvailableEdges = List.of(getState().getGrid().getRails(player).values(), rentedEdges)
                .stream()
                .flatMap(set -> set.stream())
                .filter(Edge::hasRail).collect(Collectors.toSet());
        final Tile startNode = getState().getGrid().getTileAt(getState().getPlayerPositions().get(getPlayer()));
        final Set<Tile> visitedNodes = new HashSet<>(Set.of(startNode));
        final List<Pair<Tile, List<Tile>>> positionQueue = new ArrayList<>(List.of(new Pair<>(startNode, List.of(
                startNode))));
        final List<Integer> distanceQueue = new ArrayList<>(List.of(0));
        final Map<Tile, List<Tile>> drivableTiles = new HashMap<>();

        while (!positionQueue.isEmpty()) {
            final Pair<Tile, List<Tile>> currentPair = positionQueue.removeFirst();
            final TilePosition currentPosition = currentPair.getKey().getPosition();
            final int currentDistance = distanceQueue.removeFirst();
            for (Tile tile : currentPair.getKey().getConnectedNeighbours(allAvailableEdges)) {
                if (visitedNodes.contains(tile)) {
                    continue;
                }

                final int drivingCost = getState().getGrid().getEdge(currentPosition, tile.getPosition())
                        .getDrivingCost(currentPosition);
                int newDistance = currentDistance + drivingCost;

                if (newDistance <= gameController.getCurrentDiceRoll()) {
                    List<Tile> path = new ArrayList<>(currentPair.getValue());
                    path.add(currentPair.getKey());

                    if (gameController.getTargetCity().getPosition().equals(tile.getPosition())) {
                        path.add(tile);
                        return Map.of(tile, path);
                    }

                    if (newDistance < gameController.getCurrentDiceRoll()) {
                        positionQueue.add(new Pair<>(tile, path));
                        distanceQueue.add(newDistance);
                    } else {
                        path.add(tile);
                        drivableTiles.put(tile, path);
                    }
                } else {
                    if (!drivableTiles.containsKey(currentPair.getValue().getLast())) {
                        List<Tile> path = new ArrayList<>(currentPair.getValue());
                        path.add(currentPair.getKey());
                        drivableTiles.put(currentPair.getKey(), path);
                    }
                }
            }
            visitedNodes.add(currentPair.getKey());
        }
        return drivableTiles;
    }

    /**
     * Drives to the target tile.
     * If the player drives to the target city, the point surplus is added for the
     * player based on the remaining dice roll.
     * The player poisition is set to the target tile position if the player can
     * drive to the target tile.
     *
     * @param targetTile the tile to drive to
     * @throws IllegalActionException if the player cannot drive or if the player
     *                                cannot drive to the target tile
     */
    @StudentImplementationRequired("P2.5")
    public void drive(final Tile targetTile) throws IllegalActionException {
        // TODO: P2.5
        org.tudalgo.algoutils.student.Student.crash("P2.5 - Remove if implemented");
    }
}
