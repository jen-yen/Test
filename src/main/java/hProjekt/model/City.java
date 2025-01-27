package hProjekt.model;

/**
 * Represents a city on the board.
 */
public interface City {
    /**
     * Returns the name of the city.
     *
     * @return the name of the city
     */
    String getName();

    /**
     * Returns the position of the city.
     *
     * @return the position of the city
     */
    TilePosition getPosition();

    /**
     * Returns the HexGrid instance this city is placed in.
     *
     * @return the HexGrid instance this city is placed in
     */
    HexGrid getHexGrid();

    /**
     * Returns whether the city is a starting city.
     *
     * @return whether the city is a starting city
     */
    boolean isStartingCity();
}
