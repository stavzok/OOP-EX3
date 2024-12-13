package image;

import java.awt.*;

public class PaddedImage{
    Image image;
    Color [][] pixelArray;
    public PaddedImage(Image oldImage) {
        int newWidth = closestPowerOfTwo(oldImage.getWidth());
        int newHeight = closestPowerOfTwo(oldImage.getHeight());
        if (newWidth == oldImage.getWidth() && newHeight == oldImage.getHeight()) {
            image = oldImage;
        }
        else {
            this.pixelArray = extendPixleArray(oldImage, newWidth, newHeight);
            image = new Image(pixelArray, newWidth, newHeight);
        }
    }

    private Color[][] extendPixleArray(Image oldImage, int newWidth, int newHeight) {
        int diffWidth = (newWidth - oldImage.getWidth())/2;
        int diffHeight = (newHeight - oldImage.getHeight())/2;
        Color [][] newPixelArray = new Color[newHeight][newWidth];
        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < newWidth; j++) {
                if (i < diffHeight || i > (newHeight - diffHeight) - 1 || j <diffWidth || j > (newWidth - diffWidth) - 1 ) {
                    newPixelArray[i][j] = new Color(0,0,0);
                }
                else {
                    newPixelArray[i][j] = oldImage.getPixel(i - diffHeight,j - diffWidth);
                }
            }
        }
        return newPixelArray;
    }

    private int closestPowerOfTwo(int number) {
        int power = 1;
        while (power < number) {
            power *= 2;
        }
        return power;
    }

    public Image getImage() {
        return image;
    }
}
