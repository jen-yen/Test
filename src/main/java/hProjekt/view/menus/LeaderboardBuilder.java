package hProjekt.view.menus;

import java.util.List;

import hProjekt.controller.LeaderboardController;
import hProjekt.controller.LeaderboardEntry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Builder;

/**
 * Builder for the Leaderboard view.
 * It creates the UI for displaying leaderboard entries and provides
 * functionality for returning to the main menu.
 */
public class LeaderboardBuilder implements Builder<Region> {

    private final Runnable loadMainMenuAction;
    // This is ugly I know but i don't care cause it works
    private final TableColumn<LeaderboardEntry, Integer> scoreColumn = new TableColumn<>("Score");

    /**
     * Constructor for the LeaderboardBuilder.
     *
     * @param loadMainMenuAction A {@link Runnable} to handle navigation back to the
     *                           main menu.
     */
    public LeaderboardBuilder(Runnable loadMainMenuAction) {
        this.loadMainMenuAction = loadMainMenuAction;
    }

    /**
     * Builds the leaderboard view with a title, table of entries, and a back
     * button.
     *
     * @return The constructed leaderboard view as a {@link Region}.
     */
    @Override
    public Region build() {
        // Main container
        BorderPane root = new BorderPane();
        root.getStyleClass().add("leaderboard-root");

        // Title text
        Text title = new Text("Leaderboard");
        title.getStyleClass().add("text-title");
        title.setFill(Color.WHITE);

        // TableView for displaying leaderboard data
        TableView<LeaderboardEntry> tableView = new TableView<>();
        tableView.getStyleClass().add("leaderboard-table");
        setupTableColumns(tableView);
        ObservableList<LeaderboardEntry> entries = FXCollections
                .observableArrayList(LeaderboardController.loadLeaderboardData());
        tableView.setItems(entries);
        tableView.getSortOrder().setAll(List.of(scoreColumn));
        tableView.sort();

        // VBox to center content (title and table)
        VBox contentContainer = new VBox(20);
        contentContainer.setAlignment(Pos.CENTER);
        contentContainer.setPadding(new Insets(20));
        contentContainer.getChildren().addAll(title, tableView);

        // Back button for returning to the main menu
        Button backButton = new Button("Back to Main Menu");
        backButton.setOnAction(event -> loadMainMenuAction.run());
        backButton.getStyleClass().add("button");

        // Layout configuration
        BorderPane.setMargin(backButton, new Insets(10));
        root.setTop(backButton);
        root.setCenter(contentContainer);

        // Load external CSS styles
        root.getStylesheets().add(getClass().getResource("/css/leaderboard.css").toExternalForm());

        return root;
    }

    /**
     * Configures the columns for the leaderboard table.
     *
     * @param tableView The {@link TableView} to set up columns for.
     */
    private void setupTableColumns(TableView<LeaderboardEntry> tableView) {
        TableColumn<LeaderboardEntry, String> playerColumn = new TableColumn<>("Player Name");
        playerColumn.setCellValueFactory(cellData -> cellData.getValue().playerNameProperty());
        playerColumn.setPrefWidth(0.3); // 30% width

        TableColumn<LeaderboardEntry, Boolean> aiColumn = new TableColumn<>("CPU AI");
        aiColumn.setCellValueFactory(cellData -> cellData.getValue().aiProperty());
        aiColumn.setPrefWidth(0.2); // 20% width

        TableColumn<LeaderboardEntry, String> timestampColumn = new TableColumn<>("Timestamp");
        timestampColumn.setCellValueFactory(cellData -> cellData.getValue().timestampProperty());
        timestampColumn.setPrefWidth(0.3); // 30% width

        scoreColumn.setCellValueFactory(cellData -> cellData.getValue().scoreProperty().asObject());
        scoreColumn.setPrefWidth(0.2); // 20% width
        scoreColumn.setSortType(TableColumn.SortType.DESCENDING);

        // Distribute columns across the full width of the table
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        // Add columns to the table
        tableView.getColumns().addAll(List.of(playerColumn, aiColumn, timestampColumn, scoreColumn));
    }
}
