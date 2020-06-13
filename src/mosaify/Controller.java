package mosaify;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.stage.FileChooser;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class Controller {
    public ImageView mosaicView;
    public ImageView blendFilterView;
    public ImageView blendFilterBaseView;
    public ImageView originalView;

    final DoubleProperty zoomProperty = new SimpleDoubleProperty(200);

    double accuracy = 0.0;

    public ScrollPane mosaicScrollPane;
    public ScrollPane blendFilterScrollPane;
    public ScrollPane blendFilterBaseScrollPane;
    public ScrollPane originalScrollPane;

    public ProgressBar loadingProgressBar;
    public Label statusLabel;

    FileChooser chooseImg;
    FileChooser saveImgChooser;

    public Spinner<Integer> tileSizeSpinner;

    //Mode toggles
    ToggleGroup modeGroup = new ToggleGroup();
    RadioButton rbOriginal = new RadioButton("Original image");
    RadioButton rbbImg_base = new RadioButton("bImg_base");
    RadioButton rbBlend = new RadioButton("Blend mosaic mode");
    RadioButton rbTrad = new RadioButton("Traditional mosaic mode");

    //Size of images to import from API
    static int imgWidth = 200;
    //static int imgHeight = 200;


    //Size of each tile in pixels
    static int tileSize = 15;
    final int MIN_TILE_SIZE = 5;
    final int MAX_TILE_SIZE = 100;
    final int TILE_STEP_SIZE = 1;


    //How many images to import from API
    static int maxTiles = 30;

    static int zoomLevel = 0;

    //Original input Image
    static BufferedImage bImg_original;

    //bImg_base tiled image for blend mode mosaic
    static BufferedImage bImg_base;
    static BufferedImage bImg_blendFilter;
    static BufferedImage bImg_mosaic;
    static BufferedImage finalResult;

    static String url = "https://picsum.photos/" + imgWidth;
    static PictureAPI imgSrc;

    //Array list of random images to be used as tiles
    static ArrayList<MosaicImage> imgAvgs = new ArrayList<MosaicImage>();


    public void initialize()
    {
        imgSrc = new PictureAPI(url);

        chooseImg = new FileChooser();
        saveImgChooser = new FileChooser();

        FileChooser.ExtensionFilter jpgExtension = new FileChooser.ExtensionFilter("JPEG", "*.jpg");
        FileChooser.ExtensionFilter pngExtension = new FileChooser.ExtensionFilter("PNG", "*.png");
        //chooseImg.getExtensionFilters().addAll(jpgExtension, pngExtension);
        saveImgChooser.getExtensionFilters().addAll(jpgExtension, pngExtension);

        //setup tile size spinner
        //tileSizeSpinner = new Spinner<Integer>();
        SpinnerValueFactory.IntegerSpinnerValueFactory intFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(MIN_TILE_SIZE, MAX_TILE_SIZE, 0 , TILE_STEP_SIZE);
        tileSizeSpinner.setValueFactory(intFactory);
        tileSizeSpinner.getValueFactory().setValue(tileSize);

        //listener to handle zooming in.
        zoomProperty.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable arg0) {
                mosaicView.setFitWidth(zoomProperty.get() * 4);
                mosaicView.setFitHeight(zoomProperty.get() * 3 - 100);

                blendFilterView.setFitWidth(zoomProperty.get() * 4);
                blendFilterView.setFitHeight(zoomProperty.get() * 3 - 100);

                blendFilterBaseView.setFitWidth(zoomProperty.get() * 4);
                blendFilterBaseView.setFitHeight(zoomProperty.get() * 3 - 100);

                originalView.setFitWidth(zoomProperty.get() * 4);
                originalView.setFitHeight(zoomProperty.get() * 3 - 100);
            }
        });

        //listeners to handle scrolling the image
        mosaicScrollPane.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                if (event.getDeltaY() > 0) {
                    zoomProperty.set(zoomProperty.get() * 1.1);
                } else if (event.getDeltaY() < 0) {
                    zoomProperty.set(zoomProperty.get() / 1.1);
                }
            }
        });

        blendFilterScrollPane.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                if (event.getDeltaY() > 0) {
                    zoomProperty.set(zoomProperty.get() * 1.1);
                } else if (event.getDeltaY() < 0) {
                    zoomProperty.set(zoomProperty.get() / 1.1);
                }
            }
        });

        blendFilterBaseScrollPane.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                if (event.getDeltaY() > 0) {
                    zoomProperty.set(zoomProperty.get() * 1.1);
                } else if (event.getDeltaY() < 0) {
                    zoomProperty.set(zoomProperty.get() / 1.1);
                }
            }
        });

        originalScrollPane.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                if (event.getDeltaY() > 0) {
                    zoomProperty.set(zoomProperty.get() * 1.1);
                } else if (event.getDeltaY() < 0) {
                    zoomProperty.set(zoomProperty.get() / 1.1);
                }
            }
        });
    }

    public void getRandomImages() {

        if(imgAvgs.isEmpty()) {
            //double progress = 0.0;
            Platform.runLater(() -> {
                statusLabel.setText("Getting random images...");
            });
            //get the avg color of all tile imgs and store them in an arraylist
            for(int x = 0; x < maxTiles; x++){
                try {
                    imgSrc.addImage();
                    BufferedImage image = imgSrc.getImage(x);
                    BufferedImage resized = ImageResizer.resize(image, tileSize, tileSize);
                    imgAvgs.add(new MosaicImage(resized));
                    //progress = imgAvgs.size() / (double)tileSize;
                    Platform.runLater(() -> {
                        double progress = imgAvgs.size() / (double)maxTiles;
                        loadingProgressBar.setProgress(progress);
                    });
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }


    }


    public void saveImage() {
        File result = saveImgChooser.showSaveDialog(null);
        String name = result.getName();
        String extension = name.substring(1+name.lastIndexOf(".")).toLowerCase();
        if(result != null) {
            try {
                ImageIO.write(bImg_mosaic, extension, result);
            } catch(IOException e) {

            }
        }
    }

    public void loadImage() {

        File imgFile;
        imgFile = chooseImg.showOpenDialog(null);

        if(imgFile != null) {

            Thread newThread = new Thread(() -> {
                getRandomImages();
                //imageContainer.setImage(new Image(imgFile.toURI().toString()));
                //img = new Image(imgFile.toURI().toString());
                try {
                    bImg_original = ImageIO.read(imgFile);
                    Platform.runLater(() -> {
                        statusLabel.setText("Processing mosaic image...");
                    });
                    applySettings();
                    Platform.runLater(() -> {
                        statusLabel.setText("Done!");
                    });
                } catch(IOException e) {

                }
                return;
            });
            newThread.start();
        }
    }



    //listener to handle scrolling the image
    public void onImageScroll(ScrollEvent event) {

        if (event.getDeltaY() > 0) {
            zoomProperty.set(zoomProperty.get() * 1.1);
        } else if (event.getDeltaY() < 0) {
            zoomProperty.set(zoomProperty.get() / 1.1);
        }
    }



    //Function to clear all stored tile images and retrieve new ones
    public void getNewImages() {
        accuracy = 0.0;
        imgSrc.clearAll();
        imgSrc.updateAmount(maxTiles);

        bImg_base = createMosaicBase();
        bImg_blendFilter = createMosaic();
        bImg_mosaic = createTradMosaic();

        switch (modeGroup.getSelectedToggle().getUserData().toString()) {

            case "Original Image":
                finalResult = bImg_original;
                refresh();
                break;

            case "Blend mosaic mode":
                finalResult = bImg_blendFilter;
                refresh();
                break;

            case "Traditional mosaic mode":
                finalResult = bImg_mosaic;
                refresh();
                break;

            case "bImg_base":
                finalResult = bImg_base;
                refresh();
                break;
        }
        refresh();
    }


    //Update all images after settings have been changed such as tile size.
    public void applySettings() {
        accuracy = 0.0;
        tileSize = tileSizeSpinner.getValue();

        bImg_mosaic = createTradMosaic();
        bImg_base = createMosaicBase();
        bImg_blendFilter = createMosaic();
        refresh();

    }




    //Draws a tile image at a specific location on screen.
    public static void drawTile(BufferedImage src, int x, int y, int index) {

        try {
            BufferedImage resizedTile = ImageResizer.resize(imgSrc.getImage(index), tileSize, tileSize);

            for (int i = 0; i < tileSize; i++)
                for (int j = 0; j < tileSize; j++) {
                    if ((x + j) < src.getWidth() && (y + i) < src.getHeight()) {
                        int tilergb = resizedTile.getRGB(j, i);
                        src.setRGB(x + j, y + i, tilergb);
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

    //extract Red color
    protected static int getRed(int pixel) {
        return (pixel >>> 16) & 0xFF;
    }

    //extract Green color
    protected static int getGreen(int pixel) {
        return (pixel >>> 8) & 0xFF;
    }

    //extract Blue color
    protected static int getBlue(int pixel) {
        return pixel & 0xFF;
    }


    //Creates the blend mode mosaic
    public static BufferedImage createMosaic() {
        BufferedImage result = new BufferedImage(bImg_original.getWidth(), bImg_original.getHeight(), bImg_original.getType());

        float[] hsb = new float[3];
        float[] desthsb = new float[3];

        for (int i = 0; i < result.getWidth(); i++) {
            for (int j = 0; j < result.getHeight(); j++) {

                //base Img hsv
                int tilergb = bImg_base.getRGB(i, j);
                //input image hsv
                int rgb = bImg_original.getRGB(i, j);
                int R = getRed(tilergb);
                int G = getGreen(tilergb);
                int B = getBlue(tilergb);

                int destR = getRed(rgb);
                int destG = getGreen(rgb);
                int destB = getBlue(rgb);

                Color.RGBtoHSB(destR, destG, destB, desthsb);
                Color.RGBtoHSB(R, G, B, hsb);

                //Use Hue and brightness from input image and saturation from bImg_base
                result.setRGB(i, j, Color.HSBtoRGB(desthsb[0], hsb[1], desthsb[2]));
            }
        }
        return result;
    }

    //creates the mosaic bImg_base for blend mode by pasting tile images uniformly
    public static BufferedImage createMosaicBase() {

        BufferedImage result = new BufferedImage(bImg_original.getWidth(), bImg_original.getHeight(), bImg_original.getType());

        int index = 0;

        for (int i = 0; i < result.getHeight(); i += tileSize) {
            for (int j = 0; j < result.getWidth(); j += tileSize) {
                index = (int) (Math.random() * maxTiles);
                drawTile(result, j, i, index);
            }
        }
        return result;
    }

    //locates the tile image with the closest avg color to a subset of the input image based on tile size.
    public int findClosestImg(int rgb) {

        int diff = 0;
        int index = 0;

        for (int x = 0; x < imgAvgs.size(); x++) {

            int tileAvg = imgAvgs.get(x).avg();
            if (x == 0) {
                index = 0;
                //calculate the difference of all 3 channels
                diff = Math.abs(getRed(rgb) - getRed(tileAvg)) + Math.abs(getGreen(rgb) - getGreen(tileAvg) + Math.abs(getBlue(rgb) - getBlue(tileAvg)));
            } else {
                if (Math.abs(getRed(rgb) - getRed(tileAvg)) + Math.abs(getGreen(rgb) - getGreen(tileAvg) + Math.abs(getBlue(rgb) - getBlue(tileAvg))) < diff) {
                    diff = Math.abs(getRed(rgb) - getRed(tileAvg)) + Math.abs(getGreen(rgb) - getGreen(tileAvg) + Math.abs(getBlue(rgb) - getBlue(tileAvg)));
                    index = x;
                }
            }
        }

        accuracy += (diff / 765.0);

        return index;

    }

    //returns a buffered image of a subset of a src img at an x,y and size in pixels
    public BufferedImage getImgSubset(BufferedImage src, int x, int y, int size) {
        BufferedImage result = new BufferedImage(size,
                size, src.getType());

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if ((x + i) < src.getWidth() && (y + j) < src.getHeight())
                    result.setRGB(i, j, src.getRGB(x + i, y + j));
            }
        }

        return result;
    }


    //creates the traditional mosaic by comparing the avg color of a subset of the input image and all the avg colors in the imgAvgs array list.
    public BufferedImage createTradMosaic() {
        int width = bImg_original.getWidth();
        int height = bImg_original.getHeight();
        double totalPixels = width * height;
        //double progress = 0.0;
        BufferedImage result = new BufferedImage(width, height, bImg_original.getType());

        for (int i = 0; i < width; i += tileSize) {
            for (int j = 0; j < height; j += tileSize) {
                BufferedImage subset = getImgSubset(bImg_original, i, j, tileSize);
                int avgRgb = MosaicImage.getAvgRgb(subset);
                drawTile(result, i, j, findClosestImg(avgRgb));//gets the index of the tile img with the least difference and draws it.

                double currentPixels = j * i;
//                Platform.runLater(() -> {
//                    double progress = (currentPixels) / totalPixels;
//                    loadingProgressBar.setProgress(progress);
//                });

            }
        }

        System.out.println(accuracy);
        return result;
    }


    //refreshes the imageView after a mode has been changed
    public void refresh() {
        Image mosaicImg = SwingFXUtils.toFXImage(bImg_mosaic, null);
        Image blendFilterImg = SwingFXUtils.toFXImage(bImg_blendFilter, null);
        Image blendFilterBaseImg = SwingFXUtils.toFXImage(bImg_base, null);
        Image originalImage = SwingFXUtils.toFXImage(bImg_original, null);

        mosaicView.setImage(mosaicImg);
        blendFilterView.setImage(blendFilterImg);
        blendFilterBaseView.setImage(blendFilterBaseImg);
        originalView.setImage(originalImage);
    }


}
