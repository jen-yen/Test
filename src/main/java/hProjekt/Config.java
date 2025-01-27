package hProjekt;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import hProjekt.controller.AiController;
import hProjekt.controller.BasicAiController;
import hProjekt.model.Tile;
import javafx.util.Pair;

public class Config {
    /**
     * The global source of randomness.
     */
    public static final Random RANDOM = new Random();

    /**
     * The number of sides on each die.
     * Maximum is 9.
     */
    public static int DICE_SIDES = 6;

    /**
     * The number of starting cities.
     */
    public static int NUMBER_OF_STARTING_CITIES = 3;

    /**
     * The number of cities on the board. Needs to be divisible by 2.
     */
    public static int NUMBER_OF_CITIES = 10;

    /**
     * The number of unconnected cities left to start the driving phase.
     */
    public static int UNCONNECTED_CITIES_START_THRESHOLD = 3;

    /**
     * The base probability of a city being generated on a tile.
     */
    public static double CITY_BASE_PROBABILTY = 0.3;

    /**
     * The probability of a city being generated on a tile if it is at the coast.
     */
    public static double CITY_AT_COAST_PROBABILTY = 0.1;

    /**
     * The probability of a city being generated on a tile if it is near a mountain.
     */
    public static double CITY_NEAR_MOUNTAIN_PROBABILTY = 0.05;

    /**
     * The radius around a tile to check for mountains.
     */
    public static int CITY_NEAR_MOUNTAIN_RADIUS = 1;

    /**
     * The probability of a city being generated on a tile if it is near a city.
     */
    public static double CITY_NEAR_CITY_PROBABILTY = 0.001;

    /**
     * The radius around a tile to check for other cities.
     */
    public static int CITY_NEAR_CITY_RADIUS = 3;

    /**
     * The minimum required number of players in a game.
     */
    public static int MIN_PLAYERS = 2;

    /**
     * The maximum allowed number of players in a game.
     */
    public static int MAX_PLAYERS = 6;

    /**
     * The number of credits each player starts with.
     */
    public static int STARTING_CREDITS = 20;

    /**
     * The number of credits a player receives if he is the first to connect the
     * city.
     */
    public static int CITY_CONNECTION_BONUS = 6;

    /**
     * The scale of the map, bigger values mean a bigger map.
     */
    public static int MAP_SCALE = 5;

    /**
     * The maximum number of tiles a player can rent.
     */
    public static int MAX_RENTABLE_DISTANCE = 10;

    /**
     * A map storing information on how much it costs to build a rail between two
     * tiles depending on their type.
     */
    public static Map<Set<Tile.Type>, Integer> TILE_TYPE_TO_BUILDING_COST = Map.of(
            Set.of(Tile.Type.PLAIN), 1, Set.of(Tile.Type.PLAIN, Tile.Type.MOUNTAIN), 3,
            Set.of(Tile.Type.MOUNTAIN), 5);

    /**
     * A map storing information on how much it costs to drive between two tiles
     * depending on their type.
     */
    public static Map<Pair<Tile.Type, Tile.Type>, Integer> TILE_TYPE_TO_DRIVING_COST = Map.of(
            new Pair<>(Tile.Type.PLAIN, Tile.Type.MOUNTAIN), 2,
            new Pair<>(Tile.Type.PLAIN, Tile.Type.PLAIN), 1,
            new Pair<>(Tile.Type.MOUNTAIN, Tile.Type.MOUNTAIN), 1,
            new Pair<>(Tile.Type.MOUNTAIN, Tile.Type.PLAIN), 1);

    /**
     * The credits a player receives for arriving as the i-th player during the
     * driving phase.
     */
    public static List<Integer> WINNING_CREDITS = List.of(20, 10);

    /**
     * The maximum number of credits a player can spend on building during the
     * driving phase
     */
    public static int MAX_BUILDINGBUDGET_DRIVING_PHASE = 10;

    /**
     * The path where the leaderboard CSV file is stored.
     */
    public static Path CSV_PATH = Paths.get("src/main/resources/leaderboard.csv");

    /**
     * A set of AI controllers that are available for the game.
     */
    public static final Set<Class<? extends AiController>> AVAILABLE_AI_CONTROLLER = Set.of(BasicAiController.class);

    /**
     * A list of town names to train the name generator on.
     */
    public static final String[] TOWN_NAMES;

    /**
     * Loads the town names from a file.
     */
    static {
        String[] names = new String[0];
        try {
            names = Files.readAllLines(Paths.get(Config.class.getResource("/town_names_ger.txt").toURI()))
                    .toArray(String[]::new);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        TOWN_NAMES = names;
    }
}
