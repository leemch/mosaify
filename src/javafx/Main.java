package javafx;

import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

import javafx.PictureAPI;
import javafx.ImageResizer;
import javafx.MosaicImage;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;



public class Main extends Application {

	
	//Panel to display the images
    private ImageView imageView = new ImageView();
    private ScrollPane scrollPane = new ScrollPane();
    final DoubleProperty zoomProperty = new SimpleDoubleProperty(200);
    
    double accuracy = 0.0;
    
    
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
    
    

    //Size of images to import from API
    static int imgWidth = 640;
	static int imgHeight = 360;
	
	
	//Size of each tile in pixels
	static int tileSize = 30;
	
	
	//How many images to import from API
	static int maxTiles = 30;
	
	
	
	static int zoomLevel = 0;
	
	//Original input Image
	static BufferedImage img;
	
	//Base tiled image for blend mode mosaic
	static BufferedImage base; 
	
	
	
	BufferedImage blendMosaic;
	static BufferedImage traditionalMosaic;
	static BufferedImage finalResult; 
	
	
	static JFileChooser chooseImg;
	
	
	
	static String url = "https://picsum.photos/" + imgWidth + "/" + imgHeight + "/?random"; // grab random picture from picsum
	; // amount of starting images to get
	static PictureAPI imgSrc = new PictureAPI(url, maxTiles);
	//Array list of random images to be used as tiles
	static ArrayList<MosaicImage> imgAvgs = new ArrayList<MosaicImage>();

    @Override
    public void start(Stage stage) throws Exception {
    	
    	
    	
    	//get the avg color of all tile imgs and store them in an arraylist
    	for(int x = 0; x < maxTiles; x++){
			try {
				BufferedImage image = imgSrc.getImage(x);
				BufferedImage resized = ImageResizer.resize(image, tileSize, tileSize);
				imgAvgs.add(new MosaicImage(resized));
			} catch (IOException e) { 
				// TODO Auto-generated catch block
				e.printStackTrace();
			}				
		}
    	
    	//get image input file
    	loadImage();
		
		finalResult = img;
		
		//listener to handle zooming in.
        zoomProperty.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable arg0) {
                imageView.setFitWidth(zoomProperty.get() * 4);
                imageView.setFitHeight(zoomProperty.get() * 3-100);
            }
        });
        
        //listener to handle scrolling the image
        scrollPane.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                if (event.getDeltaY() > 0) {
                    zoomProperty.set(zoomProperty.get() * 1.1);
                } else if (event.getDeltaY() < 0) {
                    zoomProperty.set(zoomProperty.get() / 1.1);
                }
            }
        });



        /////////   setting control GUI data
        rbOriginal.setToggleGroup(modeGroup);
        rbOriginal.setSelected(true);
        rbOriginal.setUserData("Original Image");
        rbBase.setToggleGroup(modeGroup);
        rbBase.setUserData("Base");
        
        rbBlend.setToggleGroup(modeGroup);
        rbBlend.setUserData("Blend mosaic mode");
        rbTrad.setToggleGroup(modeGroup);
        rbTrad.setUserData("Traditional mosaic mode");
        
        
        applyBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                applySettings();
            }
        });
        
        loadImageBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                loadImage();
            }
        });
        
        refreshBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                getNewImages();
            }
        });
        
        
        modeGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
            public void changed(ObservableValue<? extends Toggle> ov,
                Toggle old_toggle, Toggle new_toggle) {
                    if (modeGroup.getSelectedToggle() != null) {
                    	switch(modeGroup.getSelectedToggle().getUserData().toString()){
                    	
                    	case "Original Image":
                    	finalResult = img;
                    	refresh();
                    	break;
                    	
                    	case "Blend mosaic mode":
                    		finalResult = blendMosaic;
                        	refresh();
                        	break;
                        	
                        	
                    	case "Traditional mosaic mode":
                    		finalResult = traditionalMosaic;
                        	refresh();
                        	break;
                    	case "Base":
                    		finalResult = base;
                        	refresh();
                        	break;
                    	
                    	}
                    	
                    	
                        
                    }                
                }
        });
        
        //setting up panel where image is to be displayed
        imageView.setImage(SwingFXUtils.toFXImage(finalResult, null));
        imageView.preserveRatioProperty().set(true);
        imageView.setFitHeight(500);
        scrollPane.setContent(imageView);
        
        StackPane layout = new StackPane();

        //panel where imageview is to be displayed
        VBox vbox = new VBox(8); // spacing = 8
        vbox.getChildren().addAll(scrollPane);
        

        vbox.setMaxWidth(1400);
        
        
        //adding components
        layout.getChildren().add(vbox);
        layout.getChildren().add(tileSizeLabel);
        layout.getChildren().add(tileSizeField);
        layout.getChildren().add(loadImageBtn);
        layout.getChildren().add(applyBtn);
        layout.getChildren().add(refreshBtn);
        layout.getChildren().add(rbBase);
        layout.getChildren().add(rbOriginal);
        layout.getChildren().add(rbBlend);
        layout.getChildren().add(rbTrad);
        

        
        
        StackPane.setAlignment(blankField,Pos.BOTTOM_RIGHT);
        StackPane.setAlignment(tileSizeLabel,Pos.TOP_RIGHT);
        StackPane.setAlignment(tileSizeField,Pos.TOP_RIGHT);
        StackPane.setAlignment(bankSizeLabel,Pos.BOTTOM_RIGHT);
        StackPane.setAlignment(bankSizeField,Pos.BOTTOM_RIGHT);
        
        
        tileSizeField.setTranslateY(200);
        tileSizeField.setMaxWidth(50);
        tileSizeLabel.setTranslateY(205);
        tileSizeField.setTranslateX(-125);
        tileSizeLabel.setTranslateX(-175);
        
        bankSizeField.setTranslateY(-60);
        

        
        StackPane.setAlignment(loadImageBtn,Pos.TOP_RIGHT);
        StackPane.setAlignment(applyBtn,Pos.TOP_RIGHT);
        StackPane.setAlignment(refreshBtn,Pos.TOP_RIGHT);
        StackPane.setAlignment(rbOriginal,Pos.TOP_RIGHT);
        StackPane.setAlignment(rbBase,Pos.TOP_RIGHT);
        StackPane.setAlignment(rbBlend,Pos.TOP_RIGHT);
        StackPane.setAlignment(rbTrad,Pos.TOP_RIGHT);
        
        rbOriginal.setTranslateY(20);
        rbOriginal.setTranslateX(-68);
        rbBase.setTranslateY(50);
        rbBase.setTranslateX(-134);
        rbBlend.setTranslateY(80);
        rbBlend.setTranslateX(-33);
        rbTrad.setTranslateY(110);
        
        
        
        loadImageBtn.setTranslateY(135);
        applyBtn.setTranslateY(200);
        refreshBtn.setTranslateY(300);
        
        System.out.println("Just before");
        
//        Label label = new Label("Hello World");
//        label.setAlignment(Pos.CENTER);
//        stage.setScene(new Scene(label, 300, 250));
//        stage.setTitle("Hello World Application");
//        stage.show();

        stage.setTitle("Image Mosaic Generator");
        stage.setScene(new Scene(layout, 800, 600));
        
        stage.show();
        /////////////////////////////////////////////////////
    }
    

	
	
    
//    //Function to clear all stored tile images and retrieve new ones
    public void getNewImages(){
    	accuracy = 0.0;
    	imgSrc.clearAll();
    	imgSrc.updateAmount(maxTiles);
    	
    	base= createMosaicBase();
		blendMosaic=createMosaic();
		traditionalMosaic = createTradMosaic();
		
		switch(modeGroup.getSelectedToggle().getUserData().toString()){
    	
    	case "Original Image":
    	finalResult = img;
    	refresh();
    	break;
    	
    	case "Blend mosaic mode":
    		finalResult = blendMosaic;
        	refresh();
        	break;
        	
        	
    	case "Traditional mosaic mode":
    		finalResult = traditionalMosaic;
        	refresh();
        	break;
        	
    	case "Base":
    		finalResult = base;
        	refresh();
        	break;
        	
    	
    	}
		refresh();
    	
    	
    }
    
    
    //Update all images after settings have been changed such as tile size.
    public void applySettings(){
    	accuracy = 0.0;
    	tileSize = Integer.parseInt(tileSizeField.getText());
    	
    	
		base= createMosaicBase();
		blendMosaic=createMosaic();
		traditionalMosaic = createTradMosaic();
		
		switch(modeGroup.getSelectedToggle().getUserData().toString()){
    	
    	case "Original Image":
    	finalResult = img;
    	refresh();
    	break;
    	
    	case "Blend mosaic mode":
    		finalResult = blendMosaic;
        	refresh();
        	break;
        	
        	
    	case "Traditional mosaic mode":
    		finalResult = traditionalMosaic;
        	refresh();
        	break;
        	
    	case "Base":
    		finalResult = base;
        	refresh();
        	break;
        	
    	
    	}
		
		refresh();
    	
    }
    
    //Loads a new input image and refreshes
	protected void loadImage(){
		chooseImg = new JFileChooser();
		chooseImg.showOpenDialog(null);
		accuracy = 0.0;
		try {
			img = ImageIO.read(new File(chooseImg.getSelectedFile().getAbsolutePath()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		base= createMosaicBase();
		blendMosaic=createMosaic();
		traditionalMosaic = createTradMosaic();
		finalResult = img;
		modeGroup.selectToggle(rbOriginal);
		refresh();
		
	}
//
//
//	
//	
	//Draws a tile image at a specific location on screen.
	public static void drawTile(BufferedImage src, int x, int y, int index){
		
		try {
			BufferedImage resizedTile = ImageResizer.resize(imgSrc.getImage(index), tileSize, tileSize);
			
			for (int i = 0; i < tileSize; i++)
				for (int j = 0; j < tileSize; j++) {
					if((x+j) < src.getWidth() && (y+i) < src.getHeight()){
						int tilergb = resizedTile.getRGB(j, i);
						src.setRGB(x+j, y+i, tilergb);
					}
				}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private int clip(int v) {
		v = v > 255 ? 255 : v;
		v = v < 0 ? 0 : v;
		return v;
	}

	protected static int getRed(int pixel) {
		return (pixel >>> 16) & 0xFF;
	}

	protected static int getGreen(int pixel) {
		return (pixel >>> 8) & 0xFF;
	}

	protected static int getBlue(int pixel) {
		return pixel & 0xFF;
	}
//	
//	
	//Creates the blend mode mosaic
	public static BufferedImage createMosaic() {
		BufferedImage result = new BufferedImage(img.getWidth(),
				img.getHeight(), img.getType());
		
		float[] hsb = new float[3];	
		float[] desthsb = new float[3];
		
		for (int i = 0; i < result.getWidth(); i++)
		{

			for (int j = 0; j < result.getHeight(); j++) {

					//base Img hsv
					int tilergb = base.getRGB(i, j);
					//input image hsv
					int rgb = img.getRGB(i, j);
					int R = getRed(tilergb);
					int G = getGreen(tilergb);
					int B = getBlue(tilergb);
					
						int destR = getRed(rgb);
				int destG = getGreen(rgb);
				int destB = getBlue(rgb);
				
				Color.RGBtoHSB(destR, destG, destB, desthsb);
				Color.RGBtoHSB(R, G, B, hsb);
					
	
				//Use Hue and brightness from input image and saturation from base
				result.setRGB(i, j, Color.HSBtoRGB(desthsb[0], hsb[1], desthsb[2]));
			}
		}
		return result;
	}
	
	//creates the mosaic base for blend mode by pasting tile images uniformly
public static BufferedImage createMosaicBase(){
		
		BufferedImage result = new BufferedImage(img.getWidth(),
				img.getHeight(), img.getType());	
		
		int index = 0;
		
		for (int i = 0; i < result.getHeight(); i+=tileSize)
		{

			for (int j = 0; j < result.getWidth(); j+=tileSize) {
	
				index = (int) (Math.random()*maxTiles);
				drawTile(result,  j, i, index);
			
			}
		}
		
		return result;
	}

//locates the tile image with the closest avg color to a subset of the input image based on tile size.
public int findClosestImg(int rgb){
	

	int diff = 0;
	int index = 0;
	
	
	for(int x = 0; x < imgAvgs.size(); x++){
		
		
		int tileAvg = imgAvgs.get(x).avg();
		if(x == 0){
			index = 0;
			
			//calculate the difference of all 3 channels
		diff = Math.abs(getRed(rgb)-getRed(tileAvg)) + Math.abs(getGreen(rgb)-getGreen(tileAvg)+Math.abs(getBlue(rgb)-getBlue(tileAvg)));
		
		}
		else
		{
			if(Math.abs(getRed(rgb)-getRed(tileAvg)) + Math.abs(getGreen(rgb)-getGreen(tileAvg)+Math.abs(getBlue(rgb)-getBlue(tileAvg))) < diff){
				diff = Math.abs(getRed(rgb)-getRed(tileAvg)) + Math.abs(getGreen(rgb)-getGreen(tileAvg)+Math.abs(getBlue(rgb)-getBlue(tileAvg)));
				index = x;
			}
		}
		
	}
	
	accuracy += (diff/765.0);
	
	return index;
	
}

//returns a buffered image of a subset of a src img at an x,y and size in pixels
public BufferedImage getImgSubset(BufferedImage src,int x, int y, int size) {
	BufferedImage result = new BufferedImage(size,
			size, src.getType());
	
	for (int i = 0; i < size; i++)
	{
		for (int j = 0; j < size; j++) {
			
			if((x+i) < src.getWidth() && (y+j) < src.getHeight())
			result.setRGB(i, j, src.getRGB(x+i, y+j));
		}
	}

	return result;
	
}







//creates the traditional mosaic by comparing the avg color of a subset of the input image and all the avg colors in the imgAvgs array list.
public BufferedImage createTradMosaic() {
	BufferedImage result = new BufferedImage(img.getWidth(),
			img.getHeight(), img.getType());
	

	for (int i = 0; i < result.getWidth(); i+=tileSize)
	{

		for (int j = 0; j < result.getHeight(); j+=tileSize) {
			
			
			BufferedImage subset = getImgSubset(img,i, j, tileSize);
			int avgRgb = MosaicImage.getAvgRgb(subset);
			
			drawTile(result, i,j, findClosestImg(avgRgb));//gets the index of the tile img with the least difference and draws it.
		}
	}
	
	System.out.println(accuracy);
	
	return result;
}



//refreshes the imageView after a mode has been changed
public void refresh(){
	
	
	javafx.scene.image.Image p = SwingFXUtils.toFXImage(finalResult, null);
	imageView.setImage(p);
	
	
}

    /**
     * @param args
     */
    public static void main(String[] args) {
    	System.out.println("Here");
        launch(args);
    }
}