package hProjekt.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.tudalgo.algoutils.student.annotation.DoNotTouch;
import org.tudalgo.algoutils.student.annotation.StudentImplementationRequired;

import hProjekt.Config;
import hProjekt.controller.actions.ConfirmBuildAction;
import hProjekt.controller.actions.PlayerAction;
import hProjekt.model.City;
import hProjekt.model.GameState;
import hProjekt.model.HexGrid;
import hProjekt.model.HexGridImpl;
import hProjekt.model.Player;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Pair;

/**
 * The GameController class represents the controller for the game logic.
 * It manages the game state, player controllers, dice rolling and the overall
 * progression of the game.
 * It tells the players controllers what to do and when to do it.
 */
public class GameController {
    private final GameState state;
    private final Map<Player, PlayerController> playerControllers;
    private final List<AiController> aiControllers = new ArrayList<>();
    private final Supplier<Integer> dice;
    private final IntegerProperty currentDiceRoll = new SimpleIntegerProperty(0);
    private final IntegerProperty roundCounter = new SimpleIntegerProperty(0);
    private final Property<Pair<City, City>> chosenCitiesProperty = new SimpleObjectProperty<>();

    private final Property<PlayerController> activePlayerController = new SimpleObjectProperty<>();

    private boolean stopped = false;

    /**
     * Creates a new GameController with the given game state and dice supplier.
     *
     * @param state the game state
     * @param dice  the dice supplier
     */
    public GameController(GameState state, Supplier<Integer> dice) {
        this.state = state;
        this.playerControllers = new HashMap<>();
        this.dice = dice;
    }

    /**
     * Creates a new GameController with the given game state.
     *
     * @param state the game state
     */
    public GameController(GameState state) {
        this(state, () -> Config.RANDOM.nextInt(1, Config.DICE_SIDES + 1));
    }

    /**
     * Creates a new GameController with a new game state and a random dice
     * supplier.
     */
    public GameController() {
        this(new GameState(new HexGridImpl(Config.TOWN_NAMES), new ArrayList<>()),
                () -> Config.RANDOM.nextInt(1, Config.DICE_SIDES + 1));
    }

    /**
     * Returns the game state.
     *
     * @return the game state
     */
    public GameState getState() {
        return state;
    }

    /**
     * Returns a map from players to player controllers.
     *
     * @return a map from players to player controllers
     */
    public Map<Player, PlayerController> getPlayerControllers() {
        return playerControllers;
    }

    /**
     * Returns a property that contains the active player controller.
     *
     * @return a property that contains the active player controller
     */
    public Property<PlayerController> activePlayerControllerProperty() {
        return activePlayerController;
    }

    /**
     * Returns the active player controller.
     *
     * @return the active player controller
     */
    public PlayerController getActivePlayerController() {
        return activePlayerController.getValue();
    }

    /**
     * Returns the current dice roll property.
     *
     * @return the current dice roll property
     */
    public IntegerProperty currentDiceRollProperty() {
        return currentDiceRoll;
    }

    /**
     * Returns the current dice roll.
     *
     * @return the current dice roll
     */
    public int getCurrentDiceRoll() {
        return currentDiceRoll.get();
    }

    /**
     * Returns the round counter property.
     *
     * @return the round counter property
     */
    public IntegerProperty roundCounterProperty() {
        return roundCounter;
    }

    /**
     * Returns the chosen cities property that contains the starting and target city
     * as a javaFX Pair.
     *
     * @return the chosen cities property
     */
    public ReadOnlyProperty<Pair<City, City>> chosenCitiesProperty() {
        return chosenCitiesProperty;
    }

    /**
     * Returns the starting city.
     *
     * @return the starting city
     */
    public City getStartingCity() {
        return chosenCitiesProperty.getValue().getKey();
    }

    /**
     * Returns the target city.
     *
     * @return the target city
     */
    public City getTargetCity() {
        return chosenCitiesProperty.getValue().getValue();
    }

    /**
     * Casts the dice and returns the result.
     *
     * @return the result of the dice roll
     */
    public int castDice() {
        currentDiceRoll.set(dice.get());
        return currentDiceRoll.get();
    }

    /**
     * Stops the game and the Thread.
     */
    public void stop() {
        stopped = true;
    }

    /**
     * Initializes the player controllers for each player in the game state.
     * If a player is an AI, it creates an AI controller for the player.
     */
    private void initPlayerControllers() {
        for (Player player : state.getPlayers()) {
            playerControllers.put(player, new PlayerController(this, player));
            if (player.isAi()) {
                try {
                    aiControllers.add(player.getAiController()
                            .getConstructor(PlayerController.class, HexGrid.class, GameState.class, Property.class,
                                    IntegerProperty.class, IntegerProperty.class, ReadOnlyProperty.class)
                            .newInstance(playerControllers.get(player), state.getGrid(), state,
                                    activePlayerController, currentDiceRoll, roundCounter, chosenCitiesProperty));
                } catch (NoSuchMethodException e) {
                    System.err.println("Could not create ai controller for player " + player.getName());
                    System.err.println("You probably forgot to implement the constructor in your ai controller.");
                    System.err.println("The full error message: " + e.getMessage());
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    System.err.println("Could not create ai controller for player " + player.getName());
                    System.err.println("You probably do not have all necessary parameters in your constructor.");
                    System.err.println("The full error message: " + e.getMessage());
                    e.printStackTrace();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    System.err.println("Could not create ai controller for player " + player.getName());
                    System.err.println("An error occurred while trying to create the ai controller.");
                    System.err.println("The full error message: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Starts the game and handles the game loop.
     *
     * The game consists of two phases: the building phase and the driving phase.
     *
     * @throws IllegalStateException if there are not enough playerss
     */
    public void startGame() {
        if (this.state.getPlayers().size() < Config.MIN_PLAYERS) {
            throw new IllegalStateException("Not enough players");
        }
        if (playerControllers.isEmpty()) {
            initPlayerControllers();
        }

        // Bauphase
        getState().getGamePhaseProperty().setValue(GamePhase.BUILDING_PHASE);
        executeBuildingPhase();

        // Fahrphase
        getState().getGamePhaseProperty().setValue(GamePhase.DRIVING_PHASE);
        roundCounter.set(0);
        executeDrivingPhase();

        getState().getWinnerProperty().setValue(getState().getPlayers().stream()
                .max((p1, p2) -> Integer.compare(p1.getCredits(), p2.getCredits())).get());
    }

    /**
     * Executes the building phase of the game.
     * The building phase consists of the following steps:
     * - While there are unconnected cities, let a player roll the dice
     * - Starting with the player that rolled the dice, let the players build until
     * all players have built
     * - The players are given a building budget according to the dice roll
     * - Repeat until there are only
     * {@link Config#UNCONNECTED_CITIES_START_THRESHOLD} unconnected cities left
     */
    @StudentImplementationRequired("P2.3")
    private void executeBuildingPhase() {
        // TODO: P2.3
        org.tudalgo.algoutils.student.Student.crash("P2.3 - Remove if implemented");
    }

    /**
     * Chooses two random cities from the grid and sets them as starting and target
     * city.
     * The chosen cities are stored in the chosen cities property.
     */
    @StudentImplementationRequired("P2.4")
    public void chooseCities() {
        // TODO: P2.4
        org.tudalgo.algoutils.student.Student.crash("P2.4 - Remove if implemented");
    }

    /**
     * Let the players build during the driving phase.
     * The players are sorted by their credits in ascending order ensuring that the
     * player with the least credits builds first.
     * Players are given a fixed building budget of
     * {@link Config#MAX_BUILDINGBUDGET_DRIVING_PHASE}.
     */
    private void buildingDuringDrivingPhase() {
        getState().getPlayers().stream().sorted((p1, p2) -> Integer.compare(p1.getCredits(), p2.getCredits()))
                .forEachOrdered((player) -> {
                    final PlayerController pc = playerControllers.get(player);
                    pc.setBuildingBudget(Config.MAX_BUILDINGBUDGET_DRIVING_PHASE);
                    waitForBuild(pc);
                });
    }

    /**
     * Let the players choose the rails they want to rent and confirm the calculated
     * path.
     */
    @StudentImplementationRequired("P2.6")
    private void letPlayersChoosePath() {
        // TODO: P2.6
        //Jiawen write on 26-02-2025
        //Zuerst wird der PlayerController für die Fahrphase zurückgesetzt
        getState().getGamePhaseProperty().setValue(GamePhase.DRIVING_PHASE);
        roundCounter.set(0);
        executeDrivingPhase();
        //get all players, sort by credits
        getState().getPlayers().stream()
            .sorted(Comparator.comparing(Player::getCredits).reversed())
            .forEachOrdered((player) -> {
                //die Spielerposition auf die Startstadt gesetzt.
                state.setPlayerPositon(player, getStartingCity().getPosition());
                //set each player to active in turn, player must perform CHOOSE_PATH and CONFIRM_PATH
                final PlayerController pc = playerControllers.get(player);
                withActivePlayer(pc, () -> {
                    pc.waitForNextAction(PlayerObjective.CHOOSE_PATH);
                    pc.waitForNextAction(PlayerObjective.CONFIRM_PATH);
                });
            });
        //org.tudalgo.algoutils.student.Student.crash("P2.6 - Remove if implemented");
    }

    /**
     * Handles the driving.
     * If only one player is driving, the player automatically reaches the target
     * city.
     * While there are players that have not reached the target city and there are
     * still credits to win, let the players roll the dice and drive.
     * If a player reaches the target city, all other players surplus will be
     * reduced by {@link Config#DICE_SIDES} before each round.
     * The players are sorted by their credits in descending order ensuring that the
     * player with the most credits drives first.
     */
    @StudentImplementationRequired("P2.7")
    private void handleDriving() {
        // TODO: P2.7
        //Jiawen write on 27-02-2025

        long playerArrivedTarget = getState().getPlayerPositions().entrySet().stream()
            .filter(entry -> entry.getValue().equals(getTargetCity().getPosition()))
            .count();
        // Dieser Vorgang wiederholt sich, bis so viele Spieler die Zielstadt erreicht haben,
        // wie Config.WINNING_CREDITS lang ist oder alle fahrende Spieler die Zielstadt erreicht haben.
        while(playerArrivedTarget  ==  Config.WINNING_CREDITS.size()
            || playerArrivedTarget  == getState().getDrivingPlayers().size()){
            //if there is only one driving player, set player position to target
            if (getState().getDrivingPlayers().size() == 1){
                getState().getPlayers().stream()
                    .forEach((player) -> getState().setPlayerPositon(player, getTargetCity().getPosition()));
                break;
            } else if (getState().getDrivingPlayers().size() > 1) {
                //if more than one driving player
                //Sofern bereits ein Spieler die Zielstadt erreicht hat, wird zu Beginn jeder Runde jedem Spieler, der noch nicht in
                // der Zielstadt ist, der Wert von Config.DICE_SIDES vom Würfelüberschuss (surplus) abgezogen.
                if(playerArrivedTarget  > 0){
                    getState().getPlayerPositions().entrySet().stream()
                        .filter(entry -> !entry.getValue().equals(getTargetCity().getPosition()))
                        .peek(entry -> getState().addPlayerPointSurplus(entry.getKey(), -Config.DICE_SIDES));
                }
                //Danach werden die fahrenden Spieler nach Credits sortiert, der Spieler mit den meisten Credits fährt zuerst.
                getState().getDrivingPlayers().stream().sorted(Comparator.comparing(Player::getCredits).reversed())
                    //Ein Spieler, der bereits die Zielstadt erreicht hat, tut nichts.
                    .filter(player -> !getState().getPlayerPositions().get(player).equals(getTargetCity().getPosition()))
                    //Jeder fahrende Spieler würfelt zuerst, wie weit er fahren kann (PlayerObjective.ROLL_DICE).
                    // Daraufhin wird die gewürfelte Distanz gefahren (PlayerObjective.DRIVE).
                    .forEachOrdered((player) -> {
                            final PlayerController pc = playerControllers.get(player);
                            withActivePlayer(pc, () -> {
                                pc.waitForNextAction(PlayerObjective.ROLL_DICE);
                                pc.waitForNextAction(PlayerObjective.DRIVE);
                            });
                        }
                    );
            }
            //renew playerArrivedTarget
            playerArrivedTarget = getState().getPlayerPositions().entrySet().stream()
                .filter(entry -> entry.getValue().equals(getTargetCity().getPosition()))
                .count();
        }
        //org.tudalgo.algoutils.student.Student.crash("P2.7 - Remove if implemented");
    }

    /**
     * Returns the winners of a round.
     * The winners are the players that have reached the target city. If multiple
     * players have reached the target city, the player with the biggest point
     * surplus wins.
     *
     * @return the winners of a round
     */
    @StudentImplementationRequired("P2.8")
    private List<Player> getWinners() {
        // TODO: P2.8
        //Jiawen write on 26-02-2025
        return getState().getPlayers().stream()
            //get the players, who reached the target
            .filter(player -> state.getPlayerPositions().values().equals(getTargetCity().getPosition()))
            //sorted by surplus point
            .sorted(Comparator.comparingInt(player -> state.getPlayerPointSurplus().get(player)).reversed())
            //only first two player get credits
            .limit(Config.WINNING_CREDITS.size())
            .collect(Collectors.toList());
        //return org.tudalgo.algoutils.student.Student.crash("P2.8 - Remove if implemented");
    }

    /**
     * Executes the driving phase of the game.
     * The driving phase consists of the following steps:
     * - If the round counter is a multiple of 3, let the players build during the
     * driving phase
     * - Let a player choose the cities to drive to
     * - Let the players choose their path
     * - Let the players that are driving roll the dice and drive
     * - Check if a player has reached the target city and if so, add credits to the
     * player
     * - Repeat until all cities were chosen
     */
    @StudentImplementationRequired("P2.9")
    private void executeDrivingPhase() {
        // TODO: P2.9
        //Jiawen write on 28-02-2025
        while(getState().getChosenCities().size() < getState().getGrid().getCities().size()){
            //Erhöhe die Anzahl der Runden um 1.
            roundCounter.add(1);
            //Setze die Liste der fahrenden Spieler, der Positionen der Spieler,
            // die aktuell am Rennen teilnehmen und die Überschüsse (surplus) im GameState zurück
            getState().resetDrivingPlayers();
            getState().resetPlayerPositions();
            getState().resetPlayerSurplus();
            //Wenn die Anzahl der Runden ein Vielfaches von 3 ist,
            // löse die Bauphase durch die Methode buildingDuringDrivingPhase() aus.
            if (roundCounter.get() % 3 == 0){
                buildingDuringDrivingPhase();
            }

            //Der ((roundCounter.get() 1) mod state.getPlayers().size())te Spieler
            // wird mit dem Objective CHOOSE_CITIES dazu aufgefordert, die Start- und Zielstadt zu wählen
            Player chooseCityPlayer = getState().getPlayers().get((roundCounter.get()-1) % state.getPlayers().size());
            playerControllers.get(chooseCityPlayer).waitForNextAction(PlayerObjective. CHOOSE_CITIES);

            //Lass die Spieler eine Strecke wählen, die sie fahren möchten, mittels der Methode letPlayersChoosePath()
            letPlayersChoosePath();

            //Fahre die gewählte Strecke mit der Methode handleDriving().
            handleDriving();

            //Registriere die Punktzahl der Gewinner mit der Methode getWinners()
            List<Player> winners = getWinners();
            //und aktualisiere die Credits der Spieler entsprechend.
            winners.get(0).addCredits(Config.WINNING_CREDITS.get(0));
            winners.get(1).addCredits(Config.WINNING_CREDITS.get(1));

        }
        //org.tudalgo.algoutils.student.Student.crash("P2.9 - Remove if implemented");
    }

    /**
     * Waits for the player to build.
     *
     * @param pc The {@link PlayerController} to wait for.
     */
    private void waitForBuild(final PlayerController pc) {
        withActivePlayer(pc, () -> {
            PlayerAction action = pc.waitForNextAction(PlayerObjective.PLACE_RAIL);
            while (!(action instanceof ConfirmBuildAction)) {
                action = pc.waitForNextAction();
            }
        });
    }

    /**
     * Executes the given {@link Runnable} and set the active player to the given
     * {@link PlayerController}.
     * After the {@link Runnable} is executed, the active player is set to
     * {@code null} and the objective is set to {@link PlayerObjective#IDLE}.
     *
     * @param pc The {@link PlayerController} to set as active player.
     * @param r  The {@link Runnable} to execute.
     */
    @DoNotTouch
    public void withActivePlayer(final PlayerController pc, final Runnable r) {
        if (stopped) {
            throw new RuntimeException("Game was stopped");
        }
        activePlayerController.setValue(pc);
        r.run();
        pc.setPlayerObjective(PlayerObjective.IDLE);
        activePlayerController.setValue(null);
    }
}
