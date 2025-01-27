package hProjekt.view;

import hProjekt.model.City;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.TextAlignment;
import javafx.util.Builder;

/**
 * A Builder to create views for {@link City}s.
 * Renders the {@link City} as a circle with a label.
 */
public class CityBuilder implements Builder<Region> {
    private final City city;
    private final VBox pane = new VBox();
    private Label label;
    private Node marker;

    /**
     * Creates a new CityBuilder for the given {@link City}.
     *
     * @param city the city to render
     */
    public CityBuilder(final City city) {
        this.city = city;
    }

    /**
     * Returns the {@link City} this builder renders.
     *
     * @return the city
     */
    public City getCity() {
        return city;
    }

    /**
     * Returns the width of the marker.
     *
     * @return the width of the marker
     */
    public double getMarkerWidth() {
        return marker.getLayoutBounds().getWidth();
    }

    /**
     * Returns the height of the marker.
     *
     * @return the height of the marker
     */
    public double getMarkerHeight() {
        return marker.getLayoutBounds().getHeight();
    }

    @Override
    public Region build() {
        pane.getChildren().clear();

        final Circle circle = new Circle(10);
        marker = circle;
        circle.setStroke(Color.BLACK);
        circle.setFill(city.isStartingCity() ? Color.RED : Color.BLACK);
        pane.getChildren().add(circle);

        label = new Label(city.getName());
        label.setTextAlignment(TextAlignment.CENTER);
        label.getStyleClass().add("highlighted-label");
        pane.getChildren().add(label);

        pane.setAlignment(Pos.CENTER);
        // pane.setBackground(Background.fill(Color.WHITE));
        pane.setMouseTransparent(true);
        return pane;
    }

    /**
     * Highlights the city.
     */
    public void highlight() {
        label.getStyleClass().add("selected");
    }

    /**
     * Unhighlights the city.
     */
    public void unhighlight() {
        label.getStyleClass().remove("selected");
    }
}
