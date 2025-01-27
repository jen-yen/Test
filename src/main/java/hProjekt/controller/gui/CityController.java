package hProjekt.controller.gui;

import hProjekt.model.City;
import hProjekt.view.CityBuilder;

/**
 * The controller for the city.
 */
public class CityController implements Controller {
    private final CityBuilder builder;

    /**
     * Creates a new city controller for the given city.
     *
     * @param city the city to control
     */
    public CityController(final City city) {
        builder = new CityBuilder(city);
    }

    /**
     * Returns the city.
     *
     * @return the city
     */
    public City getCity() {
        return builder.getCity();
    }

    @Override
    public CityBuilder getBuilder() {
        return builder;
    }

    /**
     * Highlights the city.
     */
    public void highlight() {
        builder.highlight();
    }

    /**
     * Unhighlights the city.
     */
    public void unhighlight() {
        builder.unhighlight();
    }
}
