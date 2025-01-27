package hProjekt.view.menus;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Builder;

/**
 * Builder for the About screen.
 */
public class AboutBuilder implements Builder<Region> {

    private final Runnable loadMainMenuAction;

    /**
     * Constructor for the AboutBuilder.
     *
     * @param loadMainMenuAction the action to load the main menu
     */
    public AboutBuilder(Runnable loadMainMenuAction) {
        this.loadMainMenuAction = loadMainMenuAction;
    }

    @Override
    public VBox build() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        // Logo
        ImageView logo = new ImageView(new Image(getClass().getResourceAsStream("/images/dampfross_logo_white.png")));
        logo.setFitWidth(300);
        logo.setPreserveRatio(true);

        Text gameDescription = new Text(
                "Dampfross is a strategic train simulation game where you manage railway networks and expand your empire. Based on the board game 'Dampfross' by David G. Watts.");
        gameDescription.getStyleClass().add("text-description");
        gameDescription.setFill(Color.WHITE);

        // Developer Information
        Text developerInfo = new Text(
                "Developed by Technical University of Darmstadt, Department of Computer Science, Chair of Algorithms.");
        developerInfo.getStyleClass().add("text-subheading");
        developerInfo.setFill(Color.WHITE);

        // License Information
        Text licenseInfo = new Text("Built using JavaFX.\n" +
                "Licensed under the MIT License.");
        licenseInfo.getStyleClass().add("text-license");
        licenseInfo.setFill(Color.WHITE);

        // Legal Information
        Text legalInfo = new Text("© 2024 TU Darmstadt Chair of Algorithms. All rights reserved.\n" +
                "All trademarks are the property of their respective owners.");
        legalInfo.getStyleClass().add("text-legal");
        legalInfo.setFill(Color.WHITE);

        // Back to Main Menu Button
        Button backButton = new Button("Back to Main Menu");
        backButton.setOnAction(event -> loadMainMenuAction.run());
        backButton.getStyleClass().add("button");
        root.getChildren().addAll(logo, gameDescription, developerInfo, licenseInfo, legalInfo, backButton);

        // Add css style
        root.getStylesheets().add(getClass().getResource("/css/about.css").toExternalForm());

        return root;
    }
}
