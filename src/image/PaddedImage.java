package image;

import java.awt.*;

/**
 * The PaddedImage class ensures that an image's dimensions (width and height)
 * are extended to the closest power of two. It pads the image with white pixels
 * when necessary to meet the new dimensions.
 */

public class PaddedImage{
    /* The padded image with dimensions extended to powers of two. */

    Image image;
    /* The original image provided as input. */

    Image oldImage;
    /* A 2D array of Colors representing the padded pixel array. */

    Color [] [] pixelArray;
    /**
     * Constructs a PaddedImage instance.
     * If the image dimensions are already powers of two, no changes are made.
     * Otherwise, the image is padded with white pixels to the closest power of two.
     *
     * @param oldImage The original image to be padded.
     */

    public PaddedImage(Image oldImage) {
        this.oldImage = oldImage;
        int newWidth = closestPowerOfTwo(oldImage.getWidth());
        int newHeight = closestPowerOfTwo(oldImage.getHeight());
        // Image dimensions are already powers of two; no padding needed.

        if (newWidth == oldImage.getWidth() && newHeight == oldImage.getHeight()) {
            image = oldImage;
        }
        else {
            this.pixelArray = extendPixelArray(oldImage, newWidth, newHeight);
            image = new Image(pixelArray, newWidth, newHeight);
        }
    }

    /*
     * Pads the given image with white pixels to meet the specified width and height.
     *
     * @param oldImage The original image to be padded.
     * @param newWidth The new width, closest power of two.
     * @param newHeight The new height, closest power of two.
     * @return A 2D Color array representing the padded pixel array.
     */

    private Color[][] extendPixelArray(Image oldImage, int newWidth, int newHeight) {
        int diffWidth = (newWidth - oldImage.getWidth())/2;
        int diffHeight = (newHeight - oldImage.getHeight())/2;
        Color [][] newPixelArray = new Color[newHeight][newWidth];
        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < newWidth; j++) {
                if (i < diffHeight || i > (newHeight - diffHeight) - 1 || j <diffWidth || j > (newWidth - diffWidth) - 1 ) {
                    newPixelArray[i][j] = new Color(255,255,255);
                }
                else {
                    newPixelArray[i][j] = oldImage.getPixel(i - diffHeight,j - diffWidth);
                }
            }
        }
        return newPixelArray;
    }

    /*
     * Finds the closest power of two greater than or equal to the given number.
     *
     * @param number The input number.
     * @return The closest power of two greater than or equal to the input number.
     */

    private int closestPowerOfTwo(int number) {
        int power = 1;
        while (power < number) {
            power *= 2;
        }
        return power;
    }

    /**
     * Retrieves the padded image.
     *
     * @return The padded image with dimensions as powers of two.
     */

    public Image getImage() {
        return image;
    }
}
