package browser;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Krishnasis
 */
public class Browser extends Application {
    
    @Override
    public void start(Stage primaryStage) throws IOException {
      
        Parent root = FXMLLoader.load(getClass().getResource("Browser.fxml"));
        
        Scene scene = new Scene(root);
        
        primaryStage.setTitle("Krishnasis FX");
        primaryStage.setFullScreen(true);
        
        scene.getStylesheets().add(getClass().getResource("browser.css").toExternalForm());

        primaryStage.setScene(scene);

        primaryStage.initStyle(StageStyle.DECORATED);

        primaryStage.setResizable(true);

        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
