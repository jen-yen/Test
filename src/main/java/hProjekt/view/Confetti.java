package hProjekt.view;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/**
 * The Confetti class represents a single confetti particle
 * that can be animated to simulate a confetti effect. The confetti
 * spawns from either the top-left or top-right corner of the screen
 * and animates across the screen with a fading effect.
 */
public class Confetti extends Circle {
    private static final double RADIUS = 10.0; // Radius of the confetti circle
    private static final double DURATION = 4000; // Duration of the translation animation (ms)
    private static final double FADE_DURATION = 3500; // Duration of the fade-out animation (ms)
    private static final double MAX_X_DISTANCE = 3000; // Maximum horizontal travel distance
    private static final double MAX_Y_DISTANCE = 3000; // Maximum vertical travel distance

    /**
     * Constructs a confetti particle with a specified color and
     * initializes its starting position based on the pane dimensions.
     *
     * @param color      The color of the confetti particle.
     * @param paneWidth  The width of the pane in which the confetti spawns.
     * @param paneHeight The height of the pane in which the confetti spawns.
     */
    public Confetti(Color color, double paneWidth, double paneHeight) {
        super(RADIUS, color);
        boolean spawnFromLeft = Math.random() < 0.5;
        if (spawnFromLeft) {
            setTranslateX(Math.random() * paneWidth / 2 - paneWidth / 2); // From the left
        } else {
            setTranslateX(Math.random() * paneWidth / 2 + paneWidth / 2); // From the right
        }

        setTranslateY(Math.random() * paneHeight / 2 - paneHeight / 2);
    }

    /**
     * Animates the confetti particle with a translation and fade-out effect.
     * The particle moves randomly within a defined range and fades out as it
     * animates.
     */
    public void animate() {

        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(DURATION), this);
        translateTransition.setByX(Math.random() * MAX_X_DISTANCE - MAX_X_DISTANCE / 2);
        translateTransition.setByY(Math.random() * MAX_Y_DISTANCE - MAX_Y_DISTANCE / 2);
        translateTransition.setCycleCount(1);
        translateTransition.setInterpolator(javafx.animation.Interpolator.LINEAR);

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(FADE_DURATION), this);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.setCycleCount(1);

        translateTransition.play();
        fadeTransition.play();
    }
}
