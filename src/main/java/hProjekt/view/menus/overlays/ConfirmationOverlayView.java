package hProjekt.view.menus.overlays;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * Overlay for displaying a confirmation dialog.
 * Contains a message and "Yes" and "No" buttons.
 */
public class ConfirmationOverlayView extends VBox {

    private final Label messageLabel;
    private final Button yesButton;
    private final Button noButton;
    private final HBox buttonContainer;

    /**
     * Constructor for the ConfirmationOverlayView.
     */
    public ConfirmationOverlayView() {
        configureOverlayStyle();
        this.getStylesheets().add(ConfirmationOverlayView.class.getResource("/css/confirmation.css").toExternalForm());
        // Label for the message
        messageLabel = new Label();
        messageLabel.getStyleClass().add("label-message");
        messageLabel.setWrapText(true);
        this.getChildren().add(messageLabel);

        // Buttons for "Yes" and "No"
        yesButton = new Button("Yes");
        yesButton.getStyleClass().add("button-yes");
        noButton = new Button("No");
        noButton.getStyleClass().add("button-no");

        // Layout for buttons
        buttonContainer = new HBox(10, yesButton, noButton); // 10px spacing between buttons
        buttonContainer.setAlignment(Pos.CENTER);

        this.getChildren().add(buttonContainer);
    }

    /**
     * Styles the overlay.
     */
    private void configureOverlayStyle() {
        this.setPadding(new Insets(10));
        this.setSpacing(10);
        this.setBackground(
                new Background(new BackgroundFill(Color.rgb(42, 42, 59, 0.8), new CornerRadii(8), Insets.EMPTY)));
        this.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); -fx-background-radius: 10; -fx-padding: 10;");
        this.setAlignment(Pos.BOTTOM_LEFT);
        this.setMaxWidth(300);
        this.setMaxHeight(100);
    }

    /**
     * Updates the message text displayed above the buttons.
     *
     * @param message the new message to display
     */
    public void setMessage(String message) {
        messageLabel.setText(message);
    }

    /**
     * Updates the action for the "Yes" button.
     *
     * @param onYesAction the action to run when "Yes" is clicked
     */
    public void setOnYesAction(Runnable onYesAction) {
        yesButton.setOnAction(event -> {
            if (onYesAction != null) {
                onYesAction.run();
            }
        });
    }

    /**
     * Updates the action for the "No" button.
     * If the action is null, the button is removed.
     *
     * @param onNoAction the action to run when "No" is clicked
     */
    public void setOnNoAction(Runnable onNoAction) {
        if (onNoAction == null) {
            buttonContainer.getChildren().remove(noButton);
            return;
        }
        if (!buttonContainer.getChildren().contains(noButton)) {
            buttonContainer.getChildren().add(noButton);
        }
        noButton.setOnAction(event -> {
            if (onNoAction != null) {
                onNoAction.run();
            }
        });
    }
}
