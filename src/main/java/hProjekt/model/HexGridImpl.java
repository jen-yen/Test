package hProjekt.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.tudalgo.algoutils.student.annotation.DoNotTouch;
import org.tudalgo.algoutils.student.annotation.StudentImplementationRequired;

import hProjekt.Config;
import hProjekt.model.TilePosition.EdgeDirection;
import hProjekt.util.NameGenerator;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableDoubleValue;
import javafx.util.Pair;

/**
 * Default implementation of {@link HexGrid}.
 */
public class HexGridImpl implements HexGrid {

    private final Map<TilePosition, Tile> tiles = new HashMap<>();
    private final Map<Set<TilePosition>, Edge> edges = new HashMap<>();
    private final Map<TilePosition, City> cities = new HashMap<>();
    private final ObservableDoubleValue tileWidth;
    private final ObservableDoubleValue tileHeight;
    private final DoubleProperty tileSize = new SimpleDoubleProperty(50);
    private final Random random = Config.RANDOM;

    /**
     * Creates a new HexGrid with the given scale.
     *
     * @param scale          the scale of the grid
     * @param numberOfCities the number of cities to place
     * @param names          the names to use for the cities
     */
    @DoNotTouch
    public HexGridImpl(final int scale, final int numberOfCities, final String[] names) {
        this.tileHeight = Bindings.createDoubleBinding(() -> tileSize.get() * 2, tileSize);
        this.tileWidth = Bindings.createDoubleBinding(() -> Math.sqrt(3) * tileSize.get(), tileSize);
        initTiles(scale);
        initEdges();

        initCities(numberOfCities, new NameGenerator(names, 3, random));
    }

    /**
     * Creates a new HexGrid with the default values.
     *
     * @param names the names to use for the cities
     */
    public HexGridImpl(String[] names) {
        this(Config.MAP_SCALE, Config.NUMBER_OF_CITIES, names);
    }

    /**
     * Creates a new HexGrid with the given tiles, edges, and cities.
     *
     * @param tiles  the tiles
     * @param edges  the edges
     * @param cities the cities
     */
    public HexGridImpl(final Map<TilePosition, Tile> tiles, final Map<Set<TilePosition>, Edge> edges,
            final Map<TilePosition, City> cities) {
        this.tiles.putAll(tiles);
        this.edges.putAll(edges);
        this.cities.putAll(cities);
        this.tileHeight = Bindings.createDoubleBinding(() -> tileSize.get() * 2, tileSize);
        this.tileWidth = Bindings.createDoubleBinding(() -> Math.sqrt(3) * tileSize.get(), tileSize);
    }

    /**
     * Performs a random walk starting at the given position.
     * The walk will be of the given length.
     * Adds tiles of the given type.
     *
     * @param start  the starting position
     * @param type   the type of the tiles to add
     * @param length the length of the walk
     */
    @DoNotTouch
    private void doRandomWalk(final TilePosition start, final Tile.Type type, final int length) {
        TilePosition current = start;
        for (int i = 0; i < length; i++) {
            final TilePosition next = TilePosition.neighbour(current,
                    EdgeDirection.VALUES.get(random.nextInt(EdgeDirection.SIZE)));
            addTile(next, type);
            current = next;
        }
    }

    /**
     * Checks if the neighbouring tiles of the given position satisfy the given
     * predicate.
     * Checks the tiles in the given radius.
     *
     * @param center    the center position
     * @param predicate the predicate to check
     * @param radius    the radius to check
     * @return true if the predicate is satisfied by a neighbouring tile, false
     *         otherwise
     */
    @DoNotTouch
    private boolean isNear(final TilePosition center, final Predicate<Tile> predicate, final int radius) {
        boolean[] found = { false };
        TilePosition.forEachSpiral(center, radius, (position, params) -> {
            if (predicate.test(tiles.get(position))) {
                found[0] = true;
                return true;
            }
            return false;
        });
        return found[0];
    }

    /**
     * Initializes the tiles in this grid.
     * Performs several random walks to create a random map.
     * The map will contain plains and mountains.
     *
     * @param grid_scale the scale of the grid
     */
    @DoNotTouch
    private void initTiles(final int grid_scale) {
        final TilePosition center = new TilePosition(0, 0);
        addTile(center, Tile.Type.PLAIN);

        for (int i = 0; i < 10 * grid_scale; i++) {
            TilePosition start = tiles.keySet().stream().skip(random.nextInt(tiles.size())).findFirst().get();
            doRandomWalk(start, Tile.Type.PLAIN, 3 * grid_scale);
        }

        for (int i = 0; i < 4 * grid_scale; i++) {
            TilePosition start = tiles.keySet().stream().skip(random.nextInt(tiles.size())).findFirst().get();
            doRandomWalk(start, Tile.Type.MOUNTAIN, (int) (0.5 * grid_scale));
        }
    }

    /**
     * Initializes the cities in this grid.
     * The cities will be placed randomly on the map.
     * The names of the cities will be generated using the given name generator.
     * The cities are placed based on certain rules:
     * - Cities will only be placed on plains
     * - Cities are placed with a base probability of 0.3
     * - If the tile is at the coast the probability is 0.1
     * - If the tile is near a mountain the probability is 0.05
     * - If the tile is near another city the probability is 0.001
     *
     * @param amount        the amount of cities to place
     * @param nameGenerator the name generator to use
     */
    @DoNotTouch
    private void initCities(int amount, NameGenerator nameGenerator) {
        int startingCitiesAdded = 0;

        while (cities.size() < amount) {
            Tile tile = tiles.values().stream().skip(random.nextInt(tiles.size())).findFirst().get();

            if (tile.getType() != Tile.Type.PLAIN) {
                continue;
            }

            double probability = Config.CITY_BASE_PROBABILTY;

            if (tile.isAtCoast()) {
                probability = Config.CITY_AT_COAST_PROBABILTY;
            }

            if (isNear(tile.getPosition(), t -> t != null && t.getType() == Tile.Type.MOUNTAIN,
                    Config.CITY_NEAR_MOUNTAIN_RADIUS)) {
                probability = Config.CITY_NEAR_MOUNTAIN_PROBABILTY;
            }

            if (isNear(tile.getPosition(), t -> t != null && cities.get(t.getPosition()) != null,
                    Config.CITY_NEAR_CITY_RADIUS)) {
                probability = Config.CITY_NEAR_CITY_PROBABILTY;
            }

            if (random.nextDouble() < probability) {
                boolean isStartingCity = false;
                if (startingCitiesAdded < Config.NUMBER_OF_STARTING_CITIES
                        && (amount - cities.size() <= Config.NUMBER_OF_STARTING_CITIES - startingCitiesAdded
                                || random.nextBoolean())) {
                    isStartingCity = true;
                    startingCitiesAdded++;
                }

                final City city = new CityImpl(tile.getPosition(), nameGenerator.generateName(10), isStartingCity,
                        this);
                this.cities.put(tile.getPosition(), city);
            }
        }
    }

    /**
     * Initializes the edges in this grid.
     */
    @DoNotTouch
    private void initEdges() {
        for (final var tile : this.tiles.values()) {
            Arrays.stream(TilePosition.EdgeDirection.values())
                    .filter(ed -> this.tiles.containsKey(TilePosition.neighbour(tile.getPosition(), ed)))
                    .forEach(
                            ed -> this.edges.putIfAbsent(
                                    Set.of(
                                            tile.getPosition(),
                                            TilePosition.neighbour(tile.getPosition(), ed)),
                                    new EdgeImpl(
                                            this,
                                            tile.getPosition(),
                                            TilePosition.neighbour(tile.getPosition(), ed),
                                            new SimpleObjectProperty<>(new ArrayList<>()))));
        }
    }

    // Tiles

    @Override
    public double getTileWidth() {
        return tileWidth.get();
    }

    @Override
    public double getTileHeight() {
        return tileHeight.get();
    }

    @Override
    public double getTileSize() {
        return tileSize.get();
    }

    @Override
    public ObservableDoubleValue tileWidthProperty() {
        return tileWidth;
    }

    @Override
    public ObservableDoubleValue tileHeightProperty() {
        return tileHeight;
    }

    @Override
    public DoubleProperty tileSizeProperty() {
        return tileSize;
    }

    @Override
    public Map<TilePosition, Tile> getTiles() {
        return Collections.unmodifiableMap(tiles);
    }

    @Override
    public Tile getTileAt(final int q, final int r) {
        return getTileAt(new TilePosition(q, r));
    }

    @Override
    public Tile getTileAt(final TilePosition position) {
        return tiles.get(position);
    }

    /**
     * Adds a new tile to the grid.
     *
     * @param position position of the new tile
     * @param type     type of the new tile
     */
    private void addTile(final TilePosition position, final Tile.Type type) {
        tiles.put(position, new TileImpl(position, type, tileHeight, tileWidth, this));
    }

    // Edges / Roads

    @Override
    public Map<Set<TilePosition>, Edge> getEdges() {
        return Collections.unmodifiableMap(edges);
    }

    @Override
    public Edge getEdge(final TilePosition position0, final TilePosition position1) {
        return edges.get(Set.of(position0, position1));
    }

    @Override
    @StudentImplementationRequired("P1.3")
    public Map<Set<TilePosition>, Edge> getRails(final Player player) {
        // TODO: P1.3
        return org.tudalgo.algoutils.student.Student.crash("P1.3 - Remove if implemented");
    }

    @Override
    public Map<TilePosition, City> getCities() {
        return Collections.unmodifiableMap(cities);
    }

    @Override
    public City getCityAt(TilePosition position) {
        return cities.get(position);
    }

    @Override
    @StudentImplementationRequired("P1.4")
    public Map<TilePosition, City> getConnectedCities() {
        // Convert to stream, filter by checking if at least a city edge is connected then collect to map
        return cities.entrySet().stream()
            .filter(entry -> {
                City city = entry.getValue();
                // Loop over all edges and check if the condition that the edge has a rail built on and if the current
                // city is next to this edge on either positions is satisfied at least once
                return getEdges().values().stream()
                    .anyMatch(edge -> edge.hasRail() &&
                        (edge.getPosition1().equals(city.getPosition()) || edge.getPosition2().equals(city.getPosition())));
            })
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    }

    @Override
    @StudentImplementationRequired("P1.4")
    public Map<TilePosition, City> getUnconnectedCities() {
        // Convert to stream, filter by checking if no city edge is connected then collect to map
        return cities.entrySet().stream()
            .filter(entry -> {
                City city = entry.getValue();
                // Loop over all edges and check if the condition that the edge has a rail built on and if the current
                // city is next to this edge on either positions is never satisfied
                return getEdges().values().stream()
                    .noneMatch(edge -> edge.hasRail() &&
                        (edge.getPosition1().equals(city.getPosition()) || edge.getPosition2().equals(city.getPosition())));
            })
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    @StudentImplementationRequired("P1.4")
    public Map<TilePosition, City> getStartingCities() {
        // Convert to stream, filter by checking if the city is a starting city then collect to map
        return cities.entrySet().stream().filter((entry) -> entry.getValue().isStartingCity()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public List<Edge> findPath(TilePosition start, TilePosition target, Set<Edge> availableEdges,
            BiFunction<TilePosition, TilePosition, Integer> edgeCostFunction) {
        PriorityQueue<Pair<TilePosition, Integer>> positionQueue = new PriorityQueue<>(
                (pair1, pair2) -> Integer.compare(pair1.getValue(), pair2.getValue()));
        Map<TilePosition, TilePosition> previous = new HashMap<>();
        Map<TilePosition, Integer> distance = new HashMap<>();
        positionQueue.add(new Pair<>(start, 0));
        previous.put(start, start);
        distance.put(start, 0);

        while (!positionQueue.isEmpty()) {
            TilePosition current = positionQueue.poll().getKey();
            if (current.equals(target)) {
                break;
            }
            for (TilePosition next : getTileAt(current).getConnectedNeighbours(availableEdges).stream()
                    .map(Tile::getPosition).toList()) {
                int newDistance = distance.get(current)
                        + edgeCostFunction.apply(current, next);
                if (!distance.containsKey(next) || newDistance < distance.get(next)) {
                    distance.put(next, newDistance);
                    previous.put(next, current);
                    positionQueue.add(new Pair<>(next, newDistance));
                }
            }
        }

        if (!previous.containsKey(target)) {
            return List.of();
        }

        TilePosition current = target;
        List<Edge> pathEdges = new ArrayList<>();

        while (!current.equals(start)) {
            TilePosition previousPosition = previous.get(current);
            pathEdges.add(getEdge(previousPosition, current));
            current = previousPosition;
        }
        return pathEdges.reversed();
    }
}
