package wmx2obj;

import java.io.File;
import java.io.IOException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Wmx2obj extends Application {

    // texts
    private final Text titleText = new Text("Instructions:");
    private final Text startText = new Text("Starting point:");
    private final Text endText = new Text("Ending point:");
    private final Text instructionText = new Text(
            "1. Import world map (wmx.obj)" +
                    "\n2. Choose segments you want to import "
                    + "(1-768 for the map as in game)\n(768-835 segments are "
                    + "placeholders placed according to story)" +
            "\n3. Export .obj and .mtl to same directory"
    );
    private final Text noticeText = new Text(
            "The import and export processes might take a while"
            + " due to large file sizes."
    );
    private final Text progressText = new Text(
            "Import wmx.obj to proceed."
    );
    private final Text creditsText = new Text(
            "\u00A9 2015 Aleksanteri Hirvonen" + "\nGeometry export by "
            + "Aleksanteri Hirvonen" + "\nTexture support by Simo "
            + "\"Halfer\" Ollonen"
            + "\n\nforums.qhimm.com for more information"
    );

    // text fields
    private final TextField startTextField = new TextField("1");
    private final TextField endTextField = new TextField("835");

    // buttons
    private final Button importButton = new Button("Import FF8 wmx.obj");
    private final Button exportButton = new Button("Export to Wavefront .obj");

    // file input and output
    private final FileChooser fileChooser = new FileChooser();
    private final FileChooser.ExtensionFilter openExt = new FileChooser.ExtensionFilter(
            "FF8 world map file (.obj)", "wmx.obj"
    );
    private final FileChooser.ExtensionFilter saveExt = new FileChooser.ExtensionFilter(
            "Wavefront .obj (*.obj)", "*.obj"
    );
    private final FileChooser fileChooser1 = new FileChooser();
    private final FileChooser.ExtensionFilter saveExt1 = new FileChooser.ExtensionFilter(
            "Material template library .mtl (*.mtl)", "*.mtl"
    );

    // containers
    private final VBox vBox = new VBox(20);
    private final GridPane gridPane = new GridPane();

    // controller
    private final Controller controller = new Controller();

    @Override
    public void start(Stage primaryStage) {
        initView();
        initButtons(primaryStage);
        primaryStage.setTitle("wmx2obj");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(vBox));
        primaryStage.show();
    }

    public void initView() {
        instructionText.setWrappingWidth(260);
        noticeText.setWrappingWidth(260);
        gridPane.setAlignment(Pos.BASELINE_LEFT);
        gridPane.setHgap(20);
        gridPane.setVgap(20);
        gridPane.add(startText, 0, 0);
        gridPane.add(endText, 1, 0);
        gridPane.add(startTextField, 0, 1);
        gridPane.add(endTextField, 1, 1);
        gridPane.add(importButton, 0, 2);
        gridPane.add(exportButton, 1, 2);
        vBox.setPadding(new Insets(10, 20, 0, 20));
        vBox.getChildren().add(titleText);
        vBox.getChildren().add(instructionText);
        vBox.getChildren().add(noticeText);
        noticeText.setFill(Color.RED);
        vBox.getChildren().add(gridPane);
        vBox.getChildren().add(progressText);
        progressText.setFont(Font.font(30));
        progressText.setStroke(Color.BLACK);
        progressText.setStrokeWidth(0.2);
        vBox.getChildren().add(creditsText);
    }

    public void initButtons(Stage primaryStage) {
        // add functionality to buttons
        importButton.setOnAction((ActionEvent event) -> {
            progressText.setText("File import in progress.");
            progressText.setFill(Color.YELLOW);
            fileChooser.getExtensionFilters().clear();
            fileChooser.getExtensionFilters().add(openExt);
            final File importFile = fileChooser.showOpenDialog(primaryStage);

            // check if file was actually opened
            if (importFile == null) {
                progressText.setText("File import canceled.");
                progressText.setFill(Color.RED);
            } else {
                try {
                    // read from file
                    controller.importFile(importFile);
                    progressText.setText("File import successful.");
                    progressText.setFill(Color.GREEN);
                } catch (IOException ex) {
                    progressText.setText("File import error.");
                    progressText.setFill(Color.RED);
                }
            }
        });
        exportButton.setOnAction((ActionEvent event) -> {
            progressText.setText("File export in progress.");
            progressText.setFill(Color.YELLOW);
            fileChooser.getExtensionFilters().clear();
            fileChooser.getExtensionFilters().add(saveExt);
            fileChooser1.setInitialFileName("wmxtextures");
            fileChooser1.getExtensionFilters().add(saveExt1);
            final File exportFile = fileChooser.showSaveDialog(primaryStage);
            final File exportMtl = fileChooser1.showSaveDialog(primaryStage);
            // check if save location was actually chosen
            if (exportFile == null && exportMtl == null) {
                progressText.setText("File export canceled.");
            } else if (exportMtl == null){
                progressText.setText("Export mtl file also!");
                progressText.setFill(Color.RED);
            }else {
                try {
                    // check if given range of segments is valid
                    if (controller.isExportRangeValid(
                            startTextField.getText(),
                            endTextField.getText()
                    )) {
                        // apply program logic
                        controller.generateSegments();
                          
                        // read from file
                        controller.exportFile(exportFile);
                        controller.exportMtl(exportMtl);
                        progressText.setText("File export successful.");
                        progressText.setFill(Color.GREEN);
                    } else {
                        // note user if given range of segments wasn't valid
                        progressText.setText(
                                "Range of segments must be between 1 and 835."
                        );
                        progressText.setFill(Color.RED);
                    }
                } catch (IOException ex) {
                    progressText.setText("File export error.");
                    progressText.setFill(Color.RED);
                }
            }
        });
        
    }

    public static void main(String[] args) {
        launch(args);
    }

}