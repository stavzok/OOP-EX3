package image;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ImageConverter {
    private final PaddedImage paddedImage;
    private final int resolution;
    private HashMap<Color[][], Double> subImages;
    private final int oldWidth;
    private final int oldHeight;
    private final double RED_FACTOR = 0.2126;
    private final double GREEN_FACTOR = 0.7152;
    private final double BLUE_FACTOR = 0.0722;
    private final int MAX_RGB_SCORE = 255;
    private final ArrayList<Color[][]> subImagesArray = new ArrayList<>();

    public ImageConverter(PaddedImage paddedImage, int resolution) {
        this.paddedImage = paddedImage;
        this.oldWidth = paddedImage.getImage().getWidth();
        this.oldHeight = paddedImage.getImage().getHeight();
        this.resolution = resolution;
        subImages = new HashMap<>();
        createSubImages();
    }

    private double paintPixelGray(Color color) {
        double red = color.getRed()*RED_FACTOR;
        double green = color.getGreen()*GREEN_FACTOR;
        double blue = color.getBlue()*BLUE_FACTOR;
        return red + green + blue;
    }

    private Color [][] processSubImage(int newWidth, int newHeight, int subImageIndex) {

        Color [][] subImage = new Color[newWidth][newHeight];
        int subImageRow = subImageIndex / (oldWidth / newWidth);
        int subImageCol = subImageIndex % (oldWidth / newWidth);
        for (int i=0; i<newHeight; i++) {
            for (int j=0; j<newWidth; j++) {
                int index1 = j + subImageCol*newWidth;
                int index2 = i + subImageRow*newHeight;
                subImage[i][j] =  paddedImage.getImage().getPixel(
                        i + subImageRow*newHeight, j + subImageCol*newWidth);
            }
        }
        return subImage;
    }

    private Double calculateSubImageBrightness(Color [][] subImage) {
        double graySum = 0;
        for (int i=0; i<subImage.length; i++) {
            for (int j=0; j<subImage[i].length; j++) {
                graySum += paintPixelGray(subImage[i][j]);

            }
        }
        return graySum/ ((subImage.length*subImage[0].length) * MAX_RGB_SCORE);
    }

    private void createSubImages() {
        System.out.println("calculating sub images...");
        int subImageWidth = oldWidth / resolution;     // width of each subimage
        int subImageHeight = oldHeight / resolution;   // height of each subimage
        int numberOfSubImages = resolution * (oldHeight / subImageHeight);
        for (int i = 0; i < numberOfSubImages; i++) {
            Color [][] subImage = processSubImage(subImageWidth, subImageHeight, i);
            subImagesArray.add(subImage);
            subImages.put(subImage,calculateSubImageBrightness(subImage));
        }
    }

    public void setSubImages(HashMap<Color[][], Double> newSubImages) {
        subImages = newSubImages;

    }

    public HashMap<Color[][],Double> getNewResolutionArray(){
        return subImages;
    }

    public ArrayList<Color[][]> getSubImagesArray() {
        return subImagesArray;
    }

    public PaddedImage getPaddedImage() {return paddedImage;}
}
