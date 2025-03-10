package hProjekt.controller.gui;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import hProjekt.controller.GamePhase;
import hProjekt.controller.actions.*;
import org.tudalgo.algoutils.student.annotation.DoNotTouch;
import org.tudalgo.algoutils.student.annotation.StudentImplementationRequired;

import hProjekt.Config;
import hProjekt.controller.PlayerController;
import hProjekt.controller.PlayerObjective;
import hProjekt.controller.gui.scene.GameBoardController;
import hProjekt.model.Edge;
import hProjekt.model.Player;
import hProjekt.model.PlayerState;
import hProjekt.model.Tile;
import hProjekt.model.TilePosition;
import hProjekt.view.menus.overlays.ChosenCitiesOverlayView;
import hProjekt.view.menus.overlays.RollDiceOverlayView;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.event.ActionEvent;
import javafx.util.Pair;
import javafx.util.Subscription;
import java.util.stream.*;
/**
 * This class is responsible for handling all player actions performed through
 * the UI. It ensures that the correct buttons are enabled and disabled based on
 * the current player objective and state.
 * It also ensures that the correct actions are triggered when a button is
 * clicked and that the user is prompted when a action requires user input.
 * Additionally it triggers the respective actions based on the user input.
 *
 * <b>Do not touch any of the given attributes these are constructed in a way to
 * ensure thread safety.</b>
 */
public class PlayerActionsController {
    private final Property<PlayerController> playerControllerProperty = new SimpleObjectProperty<>();
    private final Property<PlayerState> playerStateProperty = new SimpleObjectProperty<>();
    private Subscription playerStateSubscription = Subscription.EMPTY;
    private final RollDiceOverlayView rollDiceOverlayView;
    private final ChosenCitiesOverlayView cityOverlayView;
    private final GameBoardController gameBoardController;
    private final ObservableSet<Edge> selectedEdges = FXCollections.observableSet();
    private final SetChangeListener<Edge> selctedEdgesListener = (change) -> {
        for (Edge edge : change.getSet()) {
            getHexGridController().getEdgeControllersMap().get(edge).highlight();
        }
    };
    private final Property<Tile> selectedTile = new SimpleObjectProperty<>();
    private Subscription selectedTileSubscription = Subscription.EMPTY;
    private ObservableList<Edge> selectedRailPath = FXCollections.observableArrayList();
    private final ListChangeListener<Edge> selectedRailPathListener = (change) -> {
        getHexGridController().getEdgeControllers().forEach(EdgeController::hideLabel);
        change.getList().forEach(edge -> {
            EdgeController edgeController = getHexGridController().getEdgeControllersMap().get(edge);
            edgeController.setCostLabel(edge.getBaseBuildingCost(),
                    edge.getTotalParallelCost(getPlayer()));
        });
    };

    /**
     * Creates a new PlayerActionsController.
     * It attaches listeners to populate the playerController, playerState and
     * playerObjective properties. This is necessary to ensure these properties are
     * always on the correct thread.
     * Additionally the PlayerActionsBuilder is created with all necessary event
     * handlers.
     *
     * <b>Do not touch this constructor.</b>
     *
     * @param gameBoardController      the game board controller
     * @param playerControllerProperty the property that contains the player
     *                                 controller that is currently active
     */
    @DoNotTouch
    public PlayerActionsController(
            final Property<PlayerController> playerControllerProperty,
            GameBoardController gameBoardController) {
        this.gameBoardController = gameBoardController;
        this.rollDiceOverlayView = new RollDiceOverlayView(this::rollDiceButtonAction);
        this.cityOverlayView = new ChosenCitiesOverlayView(this::chooseCitiesButtonAction);
        this.playerControllerProperty.subscribe((oldValue, newValue) -> {
            Platform.runLater(() -> {
                playerStateSubscription.unsubscribe();
                playerStateSubscription = newValue.getPlayerStateProperty().subscribe(
                        (oldState, newState) -> Platform.runLater(() -> this.playerStateProperty.setValue(newState)));
                this.playerStateProperty.setValue(newValue.getPlayerStateProperty().getValue());
            });
        });

        rollDiceOverlayView.disableRollDiceButton();

        playerControllerProperty.subscribe((oldValue, newValue) -> {
            Platform.runLater(() -> {
                if (newValue == null) {
                    return;
                }
                this.playerControllerProperty.setValue(newValue);
            });
        });
        Platform.runLater(() -> {
            this.playerControllerProperty.setValue(playerControllerProperty.getValue());
        });

        playerStateProperty.subscribe((oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            updateUIBasedOnObjective(newValue.playerObjective());
        });

        if (getPlayerController() != null) {
            updateUIBasedOnObjective(getPlayerObjective());
        }
    }

    /**
     * Updates the UI based on the given objective. This includes enabling and
     * disabling buttons and prompting the user if necessary.
     * Also redraws the game board and updates the player information.
     *
     * @param objective the objective to check
     */
    @StudentImplementationRequired("P4.1")
    private void updateUIBasedOnObjective(final PlayerObjective objective) {
        // TODO: P4.1
        resetUiToBaseState();
        removeAllHighlights();
        updatePlayerInformation();
        if (!getPlayer().isAi()) {
            if (objective.getAllowedActions().contains(BuildRailAction.class)) {
                addBuildHandlers();
            }
            if (objective.getAllowedActions().contains(RollDiceAction.class)){
                rollDiceOverlayView.enableRollDiceButton();
            }
            if (objective.getAllowedActions().contains(ChooseCitiesAction.class)) {
                cityOverlayView.enableChooseButton();
            }
            if (objective.getAllowedActions().contains(ChooseRailsAction.class)) {
                configureRailSelection();
            }
            if (objective.getAllowedActions().contains(ConfirmDrive.class)) {
                showRentingConfirmation();
            }
            if (objective.getAllowedActions().contains(DriveAction.class)) {
                updateDriveableTiles();
            }

        }
    }

    /**
     * Shows the renting confirmation overlay. This overlay is shown when the player
     * is in the driving phase and has to confirm the rented edges.
     */
    private void showRentingConfirmation() {
        getPlayerState().rentedEdges().stream().forEach(edge -> {
            getHexGridController().getEdgeControllersMap().get(edge).highlight();
        });
        if (getPlayerState().hasPath()) {
            gameBoardController.updateConfirmationOverlay(
                    getPlayerState().rentedEdges().isEmpty() ? "Drive on own rails?"
                            : "Rent highlighted rails and drive?",
                    () -> confirmDrive(true),
                    () -> confirmDrive(false));
        } else {
            gameBoardController.updateConfirmationOverlay(
                    "Could not find path to target city. Do you want to rent different rails?",
                    () -> confirmDrive(false), () -> confirmDrive(true));
        }
        selectedEdges.clear();
    }

    /**
     * Configures the edges that can be rented by the player so they can be selected
     * by the player.
     */
    private void configureRailSelection() {
        selectedEdges.clear();
        selectedEdges.addListener(selctedEdgesListener);
        addChooseEdgesHandlers();
        gameBoardController.updateConfirmationOverlay("Rent selected rails?", this::confirmSelectedRails, () -> {
            selectedEdges.clear();
        });
    }

    /**
     * Resets the UI to the base state. This includes disabling all buttons and
     * hiding all overlays.
     * Also removes listeners and subscriptions.
     */
    private void resetUiToBaseState() {
        rollDiceOverlayView.disableRollDiceButton();
        cityOverlayView.disableChooseButton();
        gameBoardController.hideConfirmationOverlay();
        selectedEdges.removeListener(selctedEdgesListener);
        selectedTileSubscription.unsubscribe();
        getHexGridController().getEdgeControllers().forEach(EdgeController::hideLabel);
        selectedRailPath.removeListener(selectedRailPathListener);
    }

    /**
     * Updates the drivable tiles for the player. This includes highlighting the
     * tiles that can be driven to and animating the player driving to the clicked
     * tile.
     */
    private void updateDriveableTiles() {
        gameBoardController.getPlayerAnimationController(getPlayer())
                .setPosition(gameBoardController.getPlayerPosition(getPlayer()));
        gameBoardController.getPlayerAnimationController(getPlayer()).showTrain();
        getPlayerState().rentedEdges().stream().forEach(edge -> {
            getHexGridController().getEdgeControllersMap().get(edge).highlight();
        });
        getPlayerState().drivableTiles().keySet().stream().forEach(tile -> {
            getHexGridController().getTileControllersMap().get(tile).highlight(e -> {
                getHexGridController().unhighlightTiles();
                gameBoardController.getPlayerAnimationController(getPlayer())
                        .animatePlayer(getPlayerState().drivableTiles().get(tile))
                        .setOnFinished(actionEvent -> getPlayerController()
                                .triggerAction(new DriveAction(tile)));
            });
        });
    }

    /**
     * Updates the player information in the game board.
     */
    @DoNotTouch
    private void updatePlayerInformation() {
        gameBoardController.updatePlayerInformation();
    }

    /**
     * Updates the city overlay with the given cities.
     *
     * @param fromCity     the city to start from
     * @param toCity       the city to drive to
     * @param allCityNames all city names
     */
    public void updateCityOverlay(String fromCity, String toCity, List<String> allCityNames) {
        cityOverlayView.spinCities(fromCity, toCity, allCityNames);
    }

    /**
     * Returns the player controller that is currently active.
     * Please do not use this method to get the playerState or playerObjective.
     * Use the {@link #getPlayerState()} and {@link #getPlayerObjective()} instead.
     *
     * @return the player controller that is currently active
     */
    @DoNotTouch
    private PlayerController getPlayerController() {
        return playerControllerProperty.getValue();
    }

    /**
     * Returns the player state of the player that is currently active.
     *
     * @return the player state of the player that is currently active
     */
    @DoNotTouch
    private PlayerState getPlayerState() {
        return playerStateProperty.getValue();
    }

    /**
     * Returns the player objective of the player that is currently active.
     *
     * @return the player objective of the player that is currently active
     */
    @DoNotTouch
    private PlayerObjective getPlayerObjective() {
        return getPlayerState().playerObjective();
    }

    /**
     * Returns the player that is currently active.
     *
     * @return the player that is currently active
     */
    @DoNotTouch
    private Player getPlayer() {
        return getPlayerController().getPlayer();
    }

    private HexGridController getHexGridController() {
        return gameBoardController.getHexGridController();
    }

    /**
     * Removes all highlights from the game board.
     */
    @DoNotTouch
    private void removeAllHighlights() {
        getHexGridController().getEdgeControllers().forEach(EdgeController::unhighlight);
        getHexGridController().unhighlightTiles();
        getHexGridController().getTileControllers().forEach(TileController::removeMouseEnteredHandler);
    }

    /**
     * Returns the roll dice overlay view.
     *
     * @return the roll dice overlay view
     */
    public RollDiceOverlayView getRollDiceOverlayView() {
        return rollDiceOverlayView;
    }

    /**
     * Returns the city overlay view.
     *
     * @return the city overlay view
     */
    public ChosenCitiesOverlayView getChosenCitiesOverlayView() {
        return cityOverlayView;
    }

    /**
     * The action that is triggered when the roll dice button is clicked.
     *
     * @param event the event that triggered the action
     */
    @DoNotTouch
    public void rollDiceButtonAction(final ActionEvent event) {
        getPlayerController().triggerAction(new RollDiceAction());
    }

    /**
     * The action that is triggered when the choose cities button is clicked.
     *
     * @param event the event that triggered the action
     */
    public void chooseCitiesButtonAction(final ActionEvent event) {
        getPlayerController().triggerAction(new ChooseCitiesAction());
    }

    /**
     * Calculates the driving cost function between two tiles.
     *
     * @param from the starting tile
     * @param to   the target tile
     * @return the driving cost between the two tiles
     */
    private Integer drivingCostFunction(TilePosition from, TilePosition to) {
        return getHexGridController().getHexGrid().getEdge(from, to).getDrivingCost(from);
    }

    /**
     * Finds the path between the hovered tile and the selected tile based on all
     * edges in the grid.
     *
     * @param hoveredTile  the hovered/start tile
     * @param selectedTile the selected/target tile
     * @return the path between the hovered and selected tile
     */
    private List<Edge> findBuildPath(Tile hoveredTile, Tile selectedTile) {
        return getHexGridController().getHexGrid().findPath(
                selectedTile.getPosition(),
                hoveredTile.getPosition(),
                getHexGridController().getHexGrid().getEdges().values().stream()
                        .collect(Collectors.toSet()),
                this::drivingCostFunction);
    }

    /**
     * Limits the given path with the given function and highlights it.
     *
     * @param terminateFunction the function that limits the path
     * @param pathToHoveredTile the path to the hovered tile
     */
    private void highlightTrimmedPath(BiFunction<Pair<Integer, Integer>, Integer, Boolean> terminateFunction,
            List<Edge> pathToHoveredTile) {
        highlightTrimmedPath(terminateFunction, pathToHoveredTile, List.of());
    }

    /**
     * Limits the given path with the given function and highlights it.
     * Also unhighlights all edges except the given highlighted edges.
     *
     * @param terminateFunction the function that limits the path, gets a pair of
     *                          the building costs and the parallel costs and the
     *                          distance. Returns true if the path shouldn't be
     *                          longer.
     * @param pathToHoveredTile the path to highlight
     * @param highlightedEdges  the edges that are already highlighted
     */
    private void highlightTrimmedPath(BiFunction<Pair<Integer, Integer>, Integer, Boolean> terminateFunction,
            List<Edge> pathToHoveredTile, Collection<Edge> highlightedEdges) {
        getHexGridController().getEdgeControllers().stream().filter(ec -> !highlightedEdges.contains(ec.getEdge()))
                .forEach(EdgeController::unhighlight);

        selectedRailPath.setAll(trimPath(terminateFunction, pathToHoveredTile));

        highlightPath(selectedRailPath);
    }

    /**
     * Trims the path tile based on the given function.
     * The function gets the building costs, the parallel costs and the distance and
     * returns true if the path should be terminated.
     *
     * @param terminateFunction the function that limits the path, gets a pair of
     *                          the building costs and the parallel costs and the
     *                          distance. Returns true if the path shouldn't be
     *                          longer
     * @param path              the path to trim
     */
    @StudentImplementationRequired("P4.2")
    private List<Edge> trimPath(BiFunction<Pair<Integer, Integer>, Integer, Boolean> terminateFunction,
            List<Edge> path) {
        // TODO: P4.2
        for (int i = 0; i < path.size(); i++) {
            List<Edge> list1 = new LinkedList<>();
            int basisGesamt = 0;
            int parallelGesamt = 0;
            int distanzGesamt = 0;
            for (int j = 0; j <= i; j++) {
                list1.add(path.get(j));
                basisGesamt = basisGesamt + list1.get(j).getBaseBuildingCost();
                parallelGesamt = parallelGesamt + list1.get(j).getTotalParallelCost(getPlayer());
                distanzGesamt = distanzGesamt + 1;
            }
            Pair<Integer, Integer> pair1 = new Pair<>(basisGesamt,parallelGesamt);
            if (terminateFunction.apply(pair1, distanzGesamt)) {
                return list1;
            }

        }
        return path;

    }

    /**
     * Highlights the edges of the given path.
     *
     * @param path the path to highlight.
     */
    @StudentImplementationRequired("P4.2")
    private void highlightPath(List<Edge> path) {
        // TODO: P4.2
       getHexGridController().getEdgeControllers().stream().filter(s -> path.contains(s.getEdge())).forEach(s -> s.highlight());
    }

    /**
     * Highlights the tiles from which the player can start building or selecting
     * rails.
     * When a tile is clicked all other tiles are unhighlighted and the clicked tile
     * is highlighted.
     * If the selectedTile is clicked again all tiles are highlighted again and the
     * selectedTile is set to null.
     */
    @StudentImplementationRequired("P4.3")
    private void highlightStartingTiles() {
        // TODO: P4.3
       /* selectedTile.setValue(null);
        if (getPlayer().getRails().isEmpty()) {
            List<TilePosition> list1 = getHexGridController().getHexGrid().getStartingCities().entrySet().stream().map(s -> s.getKey()).toList();
            Stream<Tile> stream1 = getHexGridController().getTileControllers().stream().map(s -> s.getTile()).filter(s -> list1.contains(s.getPosition()));
            List<Tile> list2 = stream1.toList();
            getHexGridController().getTileControllers().stream().filter(s-> list2.contains(s)).forEach(s -> s.highlight(t -> {if (selectedTile.getValue() == null) {selectedTile.setValue(t);} else {selectedTile.setValue(null);};}));
        }
        else {

        }*/
        selectedTile.setValue(null);
        if (getPlayer().getRails().isEmpty()) {
            Map<TilePosition,Tile> tiles = getHexGridController().getHexGrid().getTiles();
            Stream<TileController> tileControllersStream =
            getHexGridController().getCityControllersMap().keySet().stream()
                .filter(city -> city.isStartingCity())
                .map(city -> city.getPosition())
                .map(tilePosition -> tiles.get(tilePosition))
                .map(tile -> getHexGridController().getTileControllersMap().get(tile));
                tileControllersStream.forEach(tileController -> tileController.highlight(
                    (tile) -> {
                        if (selectedTile == null) {
                            selectedTile.setValue(tile);
                            List<TileController> TileControllersWithoutTileList = tileControllersStream.toList();
                            TileControllersWithoutTileList.remove(tile);
                            Stream<TileController> TileControllersWithoutTileStream = TileControllersWithoutTileList.stream();
                            TileControllersWithoutTileStream.forEach(tileController2 -> tileController2.unhighlight());
                        }
                        else {
                            selectedTile.setValue(null);
                            highlightStartingTiles();
                        }
                    }
                ));
        }
        else {
            Stream<TileController> tileControllersStream =
            getHexGridController().getTileControllersMap().entrySet().stream()
                .filter(entry -> !entry.getKey().getRails(getPlayer()).isEmpty())
                .map(entry -> entry.getValue());
                tileControllersStream.forEach(tileController -> tileController.highlight(
                    (tile) -> {
                        if (selectedTile == null) {
                            selectedTile.setValue(tile);
                            List<TileController> TileControllersWithoutTileList = tileControllersStream.toList();
                            TileControllersWithoutTileList.remove(tile);
                            Stream<TileController> TileControllersWithoutTileStream = TileControllersWithoutTileList.stream();
                            TileControllersWithoutTileStream.forEach(tileController2 -> tileController2.unhighlight());
                        }
                        else {
                            selectedTile.setValue(null);
                            highlightStartingTiles();
                        }
                    }
                ));
        }
    }

    /**
     * Configures the UI so the user can select tiles to build rails.
     * Highlights tiles that can be selected and sets up the necessary event
     * handlers so the user can build rails to where he moves the mouse.
     */
    @StudentImplementationRequired("P4.4")
    public void addBuildHandlers() {
        // TODO: P4.4
        //Jiawen write on 10.03
        selectedRailPath.clear();
        selectedTileSubscription.unsubscribe();
        showConfirmBuildDialog(); //Daraufhin wird dem Spieler der Bestätigungs-Dialog angezeigt.
        if(playerStateProperty.getValue().buildingBudget() > 0){//aktuelle Baubudget nicht aufgebraucht
            selectedRailPath.addListener(selectedRailPathListener);

            BiFunction<Pair<Integer, Integer>, Integer, Boolean> terminateFunction = (pairOfCost, length) ->{
                if(pairOfCost.getKey() > getPlayerState().buildingBudget() || pairOfCost.getValue() > getPlayer().getCredits()){
                    //Die Basis-Baukosten übersteigen das Baubudget.
                    //Die Parallelkosten sind höher als der aktuelle Kontostand des Spielers.
                    return true;
                }
                if(gameBoardController.getGamePhase().equals(GamePhase.DRIVING_PHASE) && pairOfCost.getKey()+ pairOfCost.getValue() > getPlayerState().buildingBudget()){
                    //Wenn das Spiel sich in der Fahrphase befindet,
                    // dürfen die Gesamtkosten nicht den aktuellen Kontostand des Spielers überschreiten.
                    return true;
                }
                return false;
            };

            BiConsumer<TileController, Tile> handleTileHover = (tileController, tile) -> {
                tileController.highlight(
                    (tile) -> {
                        if (selectedTile == null) {
                            selectedTile.setValue(tile);
                            List<TileController> TileControllersWithoutTileList = tileControllersStream.toList();
                            TileControllersWithoutTileList.remove(tile);
                            Stream<TileController> TileControllersWithoutTileStream = TileControllersWithoutTileList.stream();
                            TileControllersWithoutTileStream.forEach(tileController2 -> tileController2.unhighlight());
                        }
                        else {
                            selectedTile.setValue(null);
                            highlightStartingTiles();
                        }
                );
                getHexGridController().getTileControllersMap().get(tile).highlight(
                    (tile) -> {
                        if (selectedTile == null) {
                            selectedTile.setValue(tile);
                            List<TileController> TileControllersWithoutTileList = tileControllersStream.toList();
                            TileControllersWithoutTileList.remove(tile);
                            Stream<TileController> TileControllersWithoutTileStream = TileControllersWithoutTileList.stream();
                            TileControllersWithoutTileStream.forEach(tileController2 -> tileController2.unhighlight());
                        }
                        else {
                            selectedTile.setValue(null);
                            highlightStartingTiles();
                        }
                );
                List<Edge> pathToHoveredTile = findBuildPath(tileController.getTile(), tile);
                highlightTrimmedPath(terminateFunction, pathToHoveredTile);
            };
            Consumer<TileController> handleTileClick = (tileController) -> {
                tileController.highlight(
                    (tile) -> {
                        if (selectedTile == null) {
                            selectedTile.setValue(tile);
                            List<TileController> TileControllersWithoutTileList = tileControllersStream.toList();
                            TileControllersWithoutTileList.remove(tile);
                            Stream<TileController> TileControllersWithoutTileStream = TileControllersWithoutTileList.stream();
                            TileControllersWithoutTileStream.forEach(tileController2 -> tileController2.unhighlight());
                        }
                        else {
                            selectedTile.setValue(null);
                            highlightStartingTiles();
                        }
                );
            };
            
            setupTileSelectionHandlers(handleTileHover, handleTileClick, (Set<Edge>) selectedRailPath);


            // Wenn ein Tile angeklickt wird, soll
            // der selectedRailPath in Form einer BuildRailAction an den PlayerController übermittelt werden.
            if(!selectedRailPath.isEmpty()){
                for(Edge path : selectedRailPath){
                    try{
                        getPlayerController().buildRail(path);
                    }
                    catch(IllegalActionException e){
                        throw new RuntimeException(e.getMessage());
                    }
                }
            }

        }
        //org.tudalgo.algoutils.student.Student.crash("P4.4 - Remove if implemented");
    }

    /**
     * Shows the confirm build dialog.
     */
    private void showConfirmBuildDialog() {
        gameBoardController.updateConfirmationOverlay(
                String.format("Finish building? (%s budget left)", getPlayerState().buildingBudget()),
                () -> getPlayerController().triggerAction(new ConfirmBuildAction()), null);
    }

    /**
     * Highlights the tiles that can be selected by the player and sets up the other
     * tiles so they can be hovered and selected.
     *
     * @param handleTileHover the function that is called when a tile is hovered
     * @param handleTileClick the function that is called when a tile is clicked
     */
    private void setupTileSelectionHandlers(BiConsumer<TileController, Tile> handleTileHover,
            Consumer<TileController> handleTileClick) {
        setupTileSelectionHandlers(handleTileHover, handleTileClick, Set.of());
    }

    /**
     * Highlights the tiles that can be selected by the player and sets up the other
     * tiles so the player can buld rails to them.
     * Also unhighlights all edges except the given highlighted edges.
     *
     * @param handleTileHover  the function that is called when a tile is hovered
     * @param handleTileClick  the function that is called when a tile is clicked
     * @param highlightedEdges the edges that are already highlighted
     */
    private void setupTileSelectionHandlers(BiConsumer<TileController, Tile> handleTileHover,
            Consumer<TileController> handleTileClick, Set<Edge> highlightedEdges) {
        highlightStartingTiles();
        selectedTileSubscription = selectedTile.subscribe((oldValue, newValue) -> {
            if (newValue == null) {
                getHexGridController().getEdgeControllers().stream()
                        .filter(ec -> !highlightedEdges.contains(ec.getEdge()))
                        .forEach(EdgeController::unhighlight);
                getHexGridController().getTileControllers().forEach(TileController::removeMouseEnteredHandler);
                selectedRailPath.clear();
                return;
            }
            getHexGridController().getTileControllers().stream().filter(tc -> !tc.hasMouseClickedHandler())
                    .forEach(tc -> {
                        tc.setMouseEnteredHandler(e -> {
                            handleTileHover.accept(tc, newValue);
                        });
                        tc.setMouseClickedHandler(e -> {
                            if (selectedRailPath != null && !selectedRailPath.isEmpty()) {
                                handleTileClick.accept(tc);
                            }
                        });
                    });
        });
    }

    /**
     * Finds the path between the hovered tile and the selected tile based on the
     * chooseable edges and the player rails.
     *
     * @param hoveredTile  the hovered tile
     * @param selectedTile the selected tile
     * @return the path between the hovered and selected tile
     */
    private List<Edge> findChoosenEdgesPath(Tile hoveredTile, Tile selectedTile) {
        return getHexGridController().getHexGrid().findPath(selectedTile.getPosition(), hoveredTile.getPosition(),
                Set.of(getPlayerState().choosableEdges(), getPlayer()
                        .getRails().values()).stream().flatMap(set -> set.stream()).collect(Collectors.toSet()),
                this::drivingCostFunction);
    }

    /**
     * Highlights the tiles that can be selected by the player to rent.
     * Also unhighlights all edges except the already rented rails.
     */
    public void addChooseEdgesHandlers() {
        selectedRailPath.clear();
        selectedTileSubscription.unsubscribe();

        if (selectedEdges.size() == Config.MAX_RENTABLE_DISTANCE) {
            return;
        }

        setupTileSelectionHandlers((tc, selectedTile) -> highlightTrimmedPath(
                (costs, distance) -> {
                    distance += selectedEdges.size();
                    return distance > Config.MAX_RENTABLE_DISTANCE || distance > getPlayer().getCredits();
                },
                findChoosenEdgesPath(tc.getTile(), selectedTile), selectedEdges),
                tc -> selectedEdges.addAll(selectedRailPath), selectedEdges);
    }

    /**
     * Triggers the chooseRailsAction with the selected edges.
     */
    public void confirmSelectedRails() {
        getPlayerController().triggerAction(new ChooseRailsAction(selectedEdges));
    }

    /**
     * Triggers the confirm drive action with the given accept value.
     *
     * @param accept whether the player accepts the path or not
     */
    public void confirmDrive(boolean accept) {
        getPlayerController().triggerAction(new ConfirmDrive(accept));
    }
}
