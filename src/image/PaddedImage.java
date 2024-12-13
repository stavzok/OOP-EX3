package image;

import java.awt.*;

public class PaddedImage{
    Image image;
    Color [] [] pixelArray;
    public PaddedImage(Image oldImage) {
        int newWidth = closestPowerOfTwo(oldImage.getWidth());
        int newHeight = closestPowerOfTwo(oldImage.getHeight());
        this.pixelArray = extendPixleArray(oldImage, newWidth, newHeight);
        image = new Image(pixelArray, newWidth, newHeight);
    }

    private Color[][] extendPixleArray(Image oldImage, int newWidth, int newHeight) {
        int diffWidth = (oldImage.getWidth() - newWidth)/2;
        int diffHeight = (oldImage.getHeight() - newHeight)/2;
        Color [][] newPixelArray = new Color[newWidth][newHeight];
        for (int i = 0; i < newWidth; i++) {
            for (int j = 0; j < newHeight; j++) {
                if (i < diffWidth || i > (newWidth - diffWidth) - 1 || j <diffHeight || j > (newHeight - diffHeight) - 1 ) {
                    newPixelArray[i][j] = new Color(0,0,0);
                }
                else {
                    newPixelArray[i][j] = oldImage.getPixel(i - diffWidth,j - diffHeight);
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
