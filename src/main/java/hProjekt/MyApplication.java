package hProjekt;

import java.awt.Taskbar;
import java.awt.Taskbar.Feature;
import java.awt.Toolkit;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.function.Consumer;

import org.tudalgo.algoutils.student.annotation.DoNotTouch;

import hProjekt.controller.GameController;
import hProjekt.controller.gui.SceneSwitcher;
import hProjekt.controller.gui.SceneSwitcher.SceneType;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * The main application of the game.
 *
 * <b>Only touch when you really know what you are doing!</b>
 */
@DoNotTouch
public class MyApplication extends Application {
    private final Consumer<GameController> gameLoopStart = gc -> {
        final Thread gameLoopThread = new Thread(gc::startGame);
        gameLoopThread.setName("GameLoopThread");
        gameLoopThread.setDaemon(true);
        gameLoopThread.start();
    };

    @Override
    public void start(final Stage stage) throws Exception {
        // Don't ask JavaFX does some weird shit and sucks up errors otherwise...
        System.setErr(new PrintStream(new OutputStream() {
            @Override
            public void write(final int b) {
                System.out.write(b);
            }
        }));

        stage.setMinWidth(1000);
        stage.setMinHeight(520);
        stage.setWidth(1280);
        stage.setHeight(720);

        // Set custom icon in the task bar
        var appIcon = new Image("/images/stage-icon.png");
        stage.getIcons().add(appIcon);
        if (Taskbar.isTaskbarSupported()) {
            var taskbar = Taskbar.getTaskbar();
            if (taskbar.isSupported(Feature.ICON_IMAGE)) {
                final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
                var dockIcon = defaultToolkit.getImage(getClass().getResource("/images/stage-icon.png"));
                taskbar.setIconImage(dockIcon);
            }
        }
        stage.show();

        SceneSwitcher.getInstance(stage, gameLoopStart).loadScene(SceneType.MAIN_MENU);
    }

    /**
     * The main method of the application.
     *
     * @param args The launch arguments of the application.
     */
    public static void main(final String[] args) {
        launch(args);
    }
}
