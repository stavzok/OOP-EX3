package image;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * The ImageConverter class splits a padded image into sub-images at a given resolution,
 * calculates the grayscale brightness of each sub-image, and provides access to the processed results.
 *
 * @author inbar.el and stavzok
 */
public class ImageConverter {

    /* The padded image to be converted into sub-images. */
    private final PaddedImage paddedImage;

    /* The resolution, i.e., number of columns in the resulting ASCII art. */
    private final int resolution;

    /* A map of sub-images and their corresponding grayscale brightness values. */
    private HashMap<Color[][], Double> subImages;

    /* Original width of the padded image. */
    private final int oldWidth;

    /* Original height of the padded image. */
    private final int oldHeight;

    /* Constants for grayscale calculation based on RGB weights. */
    private final double RED_FACTOR = 0.2126;
    private final double GREEN_FACTOR = 0.7152;
    private final double BLUE_FACTOR = 0.0722;

    /* The maximum possible RGB score for normalization. */
    private final int MAX_RGB_SCORE = 255;

    /* List of sub-images in the form of 2D Color arrays. */
    private final ArrayList<Color[][]> subImagesArray = new ArrayList<>();

    /**
     * Constructs an ImageConverter instance.
     * Processes the padded image into sub-images and calculates their brightness.
     *
     * @param paddedImage The padded image to be processed.
     * @param resolution  The number of columns for the ASCII art resolution.
     */
    public ImageConverter(PaddedImage paddedImage, int resolution) {
        this.paddedImage = paddedImage;
        this.oldWidth = paddedImage.getImage().getWidth();
        this.oldHeight = paddedImage.getImage().getHeight();
        this.resolution = resolution;
        subImages = new HashMap<>();
        createSubImages();
    }

    /*
     * Converts a pixel's color into its grayscale brightness.
     *
     * @param color The color of the pixel.
     * @return The grayscale brightness value of the pixel.
     */
    private double paintPixelGray(Color color) {
        double red = color.getRed()*RED_FACTOR;
        double green = color.getGreen()*GREEN_FACTOR;
        double blue = color.getBlue()*BLUE_FACTOR;
        return red + green + blue;
    }

    /*
     * Extracts a sub-image from the padded image based on its index, width, and height.
     *
     * @param newWidth       The width of the sub-image.
     * @param newHeight      The height of the sub-image.
     * @param subImageIndex  The index of the sub-image in row-major order.
     * @return A 2D Color array representing the sub-image.
     */
    private Color [][] processSubImage(int newWidth, int newHeight, int subImageIndex) {

        Color [][] subImage = new Color[newWidth][newHeight];
        int subImageRow = subImageIndex / (oldWidth / newWidth);
        int subImageCol = subImageIndex % (oldWidth / newWidth);
        for (int i=0; i<newHeight; i++) {
            for (int j=0; j<newWidth; j++) {
                subImage[i][j] =  paddedImage.getImage().getPixel(
                        i + subImageRow*newHeight, j + subImageCol*newWidth);
            }
        }
        return subImage;
    }

    /*
     * Calculates the average grayscale brightness of a sub-image.
     *
     * @param subImage A 2D Color array representing the sub-image.
     * @return The normalized grayscale brightness value of the sub-image.
     */
    private Double calculateSubImageBrightness(Color [][] subImage) {
        double graySum = 0;
        for (int i=0; i<subImage.length; i++) {
            for (int j=0; j<subImage[i].length; j++) {
                graySum += paintPixelGray(subImage[i][j]);

            }
        }
        return graySum/ ((subImage.length*subImage[0].length) * MAX_RGB_SCORE);
    }

    /*
     * Creates and processes all sub-images from the padded image.
     * Each sub-image's brightness is calculated and stored.
     */
    private void createSubImages() {
        int subImageWidth = oldWidth / resolution;     // width of each subimage
        int subImageHeight = oldHeight / resolution;   // height of each subimage
        int numberOfSubImages = resolution * (oldHeight / subImageHeight);
        for (int i = 0; i < numberOfSubImages; i++) {
            Color [][] subImage = processSubImage(subImageWidth, subImageHeight, i);
            subImagesArray.add(subImage);
            subImages.put(subImage,calculateSubImageBrightness(subImage));
        }
    }

    /**
     * Sets new sub-images and their brightness values.
     * This allows bypassing recalculation of brightness for identical images.
     *
     * @param newSubImages A map of sub-images and their brightness values.
     */
    public void setSubImages(HashMap<Color[][], Double> newSubImages) {
        subImages = newSubImages;

    }

    /**
     * Retrieves the sub-images and their brightness values.
     *
     * @return A map of sub-images and their corresponding brightness values.
     */
    public HashMap<Color[][],Double> getNewResolutionArray(){
        return subImages;
    }

    /**
     * Retrieves the list of sub-images as 2D Color arrays.
     *
     * @return An ArrayList containing all sub-images.
     */
    public ArrayList<Color[][]> getSubImagesArray() {
        return subImagesArray;
    }

    /**
     * Retrieves the padded image being processed.
     *
     * @return The padded image.
     */
    public PaddedImage getPaddedImage() {return paddedImage;}
}
