package panel;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.swing.JOptionPane;
public class Main extends Application {
	private final String ICON_PATH = "resources/icon.png";

    @Override
    public void start(Stage primaryStage) throws Exception{
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		
        Parent root = FXMLLoader.load(getClass().getResource("panel.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("stylesheet.css").toExternalForm());

        primaryStage.setTitle("Graph Visualization");
        primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.getIcons().add(new Image(ICON_PATH));		// set application icon.
        primaryStage.show();
    }

    public static void main(String[] args) {
		try {
			launch(args);
		}
        catch(Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e);
		}
    }
}
