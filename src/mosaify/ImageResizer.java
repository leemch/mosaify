package mosaify;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ImageResizer {

//    /**
//     * Resizes an image to a absolute width and height (the image may not be
//     * proportional)
//     * @param inputImagePath Path of the original image
//     * @param outputImagePath Path to save the resized image
//     * @param scaledWidth absolute width in pixels
//     * @param scaledHeight absolute height in pixels
//     * @throws IOException
//     */
    public static BufferedImage resize(BufferedImage inputImage, int scaledWidth, int scaledHeight)
            throws IOException {
        // creates output image
        BufferedImage outputImage = new BufferedImage(scaledWidth,
                scaledHeight, inputImage.getType());
        //Image resizedImg=inputImage.getScaledInstance(scaledWidth,scaledHeight,Image.SCALE_FAST);

        // scales the input image to the output image
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(inputImage, 0, 0,scaledWidth,scaledHeight, null);
        g2d.dispose();
        return outputImage;
    }

}
