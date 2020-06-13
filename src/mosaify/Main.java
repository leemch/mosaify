package mosaify;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.StageStyle;

public class Main extends Application {

    //Buttons
    Button loadImageBtn = new Button("Load image");
    Button applyBtn = new Button("Apply settings");
    Button refreshBtn = new Button("Refresh Images");

    //Mode toggles
    ToggleGroup modeGroup = new ToggleGroup();
    RadioButton rbOriginal = new RadioButton("Original image");
    RadioButton rbBase = new RadioButton("Base");
    RadioButton rbBlend = new RadioButton("Blend mosaic mode");
    RadioButton rbTrad = new RadioButton("Traditional mosaic mode");

    Label tileSizeLabel = new Label("Tile size:");
    TextField tileSizeField = new TextField ("30");
    TextField blankField = new TextField ();
    Label bankSizeLabel = new Label("Number of images:");
    TextField bankSizeField = new TextField ("50");

    Label tradMosaicAccuracy = new Label("Traditional mosaic accuracy: ");




    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("mosaifyGUI.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Mosaify");
        //primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }





}
