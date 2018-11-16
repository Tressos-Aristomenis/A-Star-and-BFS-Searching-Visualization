package panel;

import algorithms.PathFinder;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import entity.Node;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.FileChooser;
import parser.DataParser;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Controller implements Initializable, ChangeListener<Object> {

	@FXML
	private JFXButton initializeBtn, startAStarBtn, startBfsBtn;

    @FXML
    private JFXComboBox<String> nodeListComboBox;

    @FXML
    private JFXSlider delaySlideBar, graphFontSizeSlideBar;

    private ExecutorService threadPool = Executors.newWorkStealingPool();
    private PathFinder pathFinder;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        delaySlideBar.valueProperty().addListener(this);
        graphFontSizeSlideBar.valueProperty().addListener(this);
        disableButtons(true);
    }

    public void initializeGraphPane() {
        initializeBtn.setDisable(true);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setTitle("Select the file which contains the graph data");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            String filepath = selectedFile.getPath();
            DataParser.DATASET = filepath;
        } else {
            initializeBtn.setDisable(false);
            return;
        }

        try {
			// enables multi-threading.
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    pathFinder = new PathFinder();
                    delaySlideBar.setDisable(false);                        // enable slider
					graphFontSizeSlideBar.setDisable(false);
                    List<Node> nodeList = pathFinder.initializeGraph();     // initialize graph and get all nodes from it
                    setComboBoxValues(nodeList);                            // initialize comboBox values

					// enable buttons after graph is initialized.
                    disableButtons(false);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startAStarAlgorithm() {
        String startNode = nodeListComboBox.getValue();

        // if start node is not selected, do nothing
        if (startNode == null)
            return;

        // disable buttons
        disableButtons(true);

        try {
			// enables multi-threading.
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    pathFinder.runAStar(startNode);
                    disableButtons(false);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startBreadthFirstAlgorithm() {
        String startNode = nodeListComboBox.getValue();

        // if start node is not selected, do nothing
        if (startNode == null)
            return;

        // disable buttons
        disableButtons(true);

        try {
			// enables multi-threading.
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    pathFinder.runBreadthFirst(startNode);
                    disableButtons(false);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // add all node names to comboBox
    private void setComboBoxValues(List<Node> nodes) {
        for (Node node : nodes) {
            nodeListComboBox.getItems().add(node.getName());
        }
    }

    // set button status
    private void disableButtons(boolean status) {
        startAStarBtn.setDisable(status);
        startBfsBtn.setDisable(status);
    }

    @Override
    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
        int delay = (int) delaySlideBar.getValue();
        pathFinder.setVisualizationDelay(delay);

        int fontSize = (int) graphFontSizeSlideBar.getValue();
        pathFinder.setGraphFontSize(fontSize);
    }
}
