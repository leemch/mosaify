package mosaify;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class MosaicImage {

    BufferedImage image;
    int avgRgb;

    public MosaicImage(BufferedImage img) {
        image = img;
        avgRgb = getAvgRgb(img);
    }


    public int avg(){
        return avgRgb;
    }

    public BufferedImage image(){
        return image;
    }

    public static int getAvgRgb(BufferedImage img){

        int avg, r = 0,g = 0,b = 0;
        int count = 0;

        for (int i = 0; i < img.getWidth(); i++)
            for (int j = 0; j < img.getHeight(); j++) {
                int rgb = img.getRGB(i, j);
                r += Controller.getRed(rgb);
                g += Controller.getGreen(rgb);
                b += Controller.getBlue(rgb);
                count++;
            }

        avg = new Color(r/count, g/count, b/count).getRGB();
        return avg;
    }
}

