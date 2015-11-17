package wmx2obj;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
//import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
//import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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
    
    //tabs
    private final TabPane tabs = new TabPane();
    private final Tab tab1 = new Tab();
    private final Tab tab2 = new Tab();
    private final HBox hbox = new HBox();

    
    // text fields
    private final TextField startTextField = new TextField("1");
    private final TextField endTextField = new TextField("835");

    // buttons wmx2obj
    private final Button importButton = new Button("Import FF8 wmx.obj");
    private final Button exportButton = new Button("Export to Wavefront .obj");
    private final Button exportButton2 = new Button("Export .mtl file for textures");
    
    // buttons obj2wmx
    private final Button importWavefront = new Button("Import .OBJ Wavefront file");
    private final Button exportWMX = new Button("Export to FF8 world map WMX.obj");
    

    // file input and output
    private final FileChooser fileChooser = new FileChooser();
    private final FileChooser.ExtensionFilter ff8obj = new FileChooser.ExtensionFilter(
            "FF8 world map file (.obj)", "*.obj"
    );
    private final FileChooser.ExtensionFilter wavefront = new FileChooser.ExtensionFilter(
            "Wavefront .obj (*.obj)", "*.obj"
    );
    private final FileChooser fileChooser1 = new FileChooser();
    private final FileChooser.ExtensionFilter material = new FileChooser.ExtensionFilter(
            "Material template library .mtl (*.mtl)", "*.mtl"
    );

    // containers
    private final VBox vBox = new VBox(20);
    private final GridPane gridPane1 = new GridPane();
    private final GridPane gridPane2 = new GridPane();

    // controller
    private final Controller controller = new Controller();

    @Override
    public void start(Stage primaryStage) {
        initView();
        initButtonsWMX2OBJ(primaryStage);
        initButtonsOBJ2WMX(primaryStage);
        primaryStage.setTitle("FF8 World map converter");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(vBox));
        primaryStage.show();
    }
    
    public void tabController(){
        
    }
    
    public void initView() {
        tab1.setText("wmx2obj");
        tab2.setText("obj2wmx");
        
        tab1.setClosable(false);
        tab2.setClosable(false);

        tabs.getTabs().add(tab1);
        tabs.getTabs().add(tab2);
        
        instructionText.setWrappingWidth(260);
        noticeText.setWrappingWidth(260);
        gridPane1.setAlignment(Pos.BASELINE_LEFT);
        gridPane1.setHgap(20);
        gridPane1.setVgap(20);
        
        gridPane2.setAlignment(Pos.BASELINE_LEFT);
        gridPane2.setHgap(20);
        gridPane2.setVgap(20);

        gridPane1.add(titleText, 0, 1);
        gridPane1.add(instructionText, 0, 2);
        gridPane1.add(noticeText, 0, 3);
        gridPane1.add(startText, 0, 4);
        gridPane1.add(endText, 1, 4);
        gridPane1.add(startTextField, 0, 5);
        gridPane1.add(endTextField, 1, 5);
        gridPane1.add(importButton, 0, 6);
        gridPane1.add(exportButton, 1, 6);
        gridPane1.add(exportButton2, 1, 7); 
        //gridPane.add(progressText, 0, 8);
        
        gridPane2.add(importWavefront, 0, 0);
        gridPane2.add(exportWMX,1,0);
        
        vBox.setPadding(new Insets(10, 20, 0, 20));
        
        noticeText.setFill(Color.RED);
        
        vBox.getChildren().add(tabs);
        //vBox.getChildren().add(gridPane1);
        vBox.getChildren().add(progressText);
        vBox.getChildren().add(creditsText);
        
        progressText.setFont(Font.font(30));
        progressText.setStroke(Color.BLACK);
        progressText.setStrokeWidth(0.2);

        tab1.setContent(gridPane1);
        tab2.setContent(gridPane2);

    }   

    public void initButtonsWMX2OBJ(Stage primaryStage) {
        // add functionality to buttons
        
        //********************WMX2OBJ import FF8 World Map**********************
        
        importButton.setOnAction((ActionEvent event) -> {
            progressText.setText("File import in progress.");
            progressText.setFill(Color.YELLOW);
            fileChooser.getExtensionFilters().clear();
            fileChooser.getExtensionFilters().add(ff8obj);
            
            final File importFile = fileChooser.showOpenDialog(primaryStage);

            // check if file was actually opened
            if (importFile == null) {
                progressText.setText("File import cancelled.");
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
        
        //********************WMX2OBJ export material .mtl file*****************
        
        exportButton2.setOnAction((ActionEvent event) -> {
            fileChooser1.setInitialFileName("wmxtextures");
            fileChooser1.getExtensionFilters().add(material);
            
            final File exportMtl = fileChooser1.showSaveDialog(primaryStage);
            
            if (exportMtl == null){
                progressText.setText("MTL export cancelled");
            }else{
                try {
                    controller.exportMtl(exportMtl);
                    
                    progressText.setText("MTL file exported");
                    progressText.setFill(Color.GREEN);
                } catch (IOException ex) {
                    progressText.setText("MTL export error.");
                    progressText.setFill(Color.RED);
                }
            }
        });
        
        //********************WMX2OBJ export FF8 world map to .OBJ wavefront****
        
        exportButton.setOnAction((ActionEvent event) -> {
            progressText.setText("File export in progress.");
            progressText.setFill(Color.YELLOW);
            fileChooser.getExtensionFilters().clear();
            fileChooser.getExtensionFilters().add(wavefront);
            
            final File exportFile = fileChooser.showSaveDialog(primaryStage);

            // check if save location was actually chosen
            if (exportFile == null) {
                progressText.setText("File export cancelled.");
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
    
    public void initButtonsOBJ2WMX(Stage primaryStage){
        
        //********************OBJ2WMX import FF8 World Map********************
        
        importWavefront.setOnAction((ActionEvent event) -> {
            
            fileChooser.getExtensionFilters().clear();
            fileChooser.getExtensionFilters().add(wavefront);
            
            final File importFile = fileChooser.showOpenDialog(primaryStage);
            
            if (importFile == null) {
                progressText.setText("File import cancelled.");
            }else {
                try {
                    // read from file
                    controller.importOBJ(importFile);
                    progressText.setText("File import successful.");
                    progressText.setFill(Color.GREEN);
                } catch (IOException ex) {
                    progressText.setText("File import error.");
                    progressText.setFill(Color.RED);
                }
            }
        });
        
        exportWMX.setOnAction((ActionEvent event) -> {
            
            fileChooser.getExtensionFilters().clear();
            fileChooser.getExtensionFilters().add(ff8obj);
            
            final File exportFile = fileChooser.showSaveDialog(primaryStage);
            
            try {
                /*
                if (exportFile == null) {
                progressText.setText("File export cancelled.");
                }else {
                //try {
                
                controller.exportToFF8(exportFile);
                
                progressText.setText("File export successful.");
                progressText.setFill(Color.GREEN);
                
                } catch (IOException ex) {
                progressText.setText("File export error.");
                progressText.setFill(Color.RED);
                }
                }
                */
                controller.test(exportFile);
            } catch (IOException ex) {
                Logger.getLogger(Wmx2obj.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

}