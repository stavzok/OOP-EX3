package ascii_art;

import image.ImageConverter;
import image_char_matching.SubImgCharMatcher;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The AsciiArtAlgorithm class generates ASCII art from an image.
 * It converts sub-images into ASCII characters based on brightness values.
 *
 * @author inbar.el and stavzok
 */
public class AsciiArtAlgorithm {

    /*
     * Constant for the rounding method "up".
     */
    private static final String ROUND_METHOD_UP = "up";

    /*
     * Constant for the rounding method "down".
     */
    private static final String ROUND_METHOD_DOWN = "down";

    /*
     * Constant for the rounding method "abs".
     */
    private static final String ROUND_METHOD_ABS = "abs";

    /*
     * Resolution determines the number of columns for the ASCII output.
     */
    private final int resolution;

    /*
     * Matcher to map sub-images to ASCII characters based on brightness.
     */
    private final SubImgCharMatcher subImgCharMatcher;

    /*
     * Image converter to process the image and split it into sub-images.
     */
    private final ImageConverter imageConverter;

    /*
     * Rounding method for matching brightness ("up", "down", or "abs").
     */
    private final String roundMethod;


    /**
     * Constructs an AsciiArtAlgorithm instance.
     *
     * @param resolution         The number of columns in the ASCII output.
     * @param subImgCharMatcher  A matcher for comparing brightness values.
     * @param roundMethod        The rounding method for brightness matching (e.g., "up", "down", "abs").
     * @param imageConverter     The converter for splitting the image into sub-images.
     */
    public AsciiArtAlgorithm(int resolution, SubImgCharMatcher subImgCharMatcher,
                             String roundMethod, ImageConverter imageConverter) {
        this.resolution = resolution;
        this.subImgCharMatcher = subImgCharMatcher;
        this.imageConverter = imageConverter;
        this.roundMethod = roundMethod;
    }

    /*
     * Matches each sub-image to its closest ASCII character based on brightness.
     *
     * @param subImages A map of sub-images and their corresponding brightness values.
     * @param asciiMap  A map of ASCII characters and their normalized brightness values.
     * @return A map of sub-images and their matched ASCII characters.
     */
    private HashMap<Color[][], Character> matchAsciiToSubImage(
            HashMap<Color[][], Double> subImages,
            HashMap<Character, Double> asciiMap) {
        HashMap<Color[][], Character> resultMap = new HashMap<>();

        for (Map.Entry<Color[][], Double> subImageEntry : subImages.entrySet()) {
            Color[][] subImage = subImageEntry.getKey();
            Double subImageBrightness = subImageEntry.getValue();

            // Find the closest character for the sub-image brightness
            Character closestChar = findClosestCharacters(asciiMap, subImageBrightness);
            resultMap.put(subImage, closestChar); // Store the closest character
        }
        return resultMap;
    }

    /*
     * Finds the closest ASCII character to a given brightness value.
     *
     * @param asciiMap         A map of ASCII characters and their brightness values.
     * @param targetBrightness The brightness value of the sub-image.
     * @return The closest ASCII character based on the selected rounding method.
     */
    private Character findClosestCharacters(HashMap<Character, Double> asciiMap, Double targetBrightness) {
        Character closestChar = null;
        double minDifference = Double.MAX_VALUE;

        // Use absolute matching if roundMethod is "abs"
        if (roundMethod.equals(ROUND_METHOD_ABS)){
            return subImgCharMatcher.getCharByImageBrightness(targetBrightness);
        }

        // Find the closest character based on the selected rounding method
        for (Map.Entry<Character, Double> asciiEntry : asciiMap.entrySet()) {
            char asciiChar = asciiEntry.getKey();
            Double asciiBrightness = asciiEntry.getValue();

            double difference = Math.abs(asciiBrightness - targetBrightness);

            // Check rounding mode to decide the closest match
            switch (roundMethod) {

                case ROUND_METHOD_UP: // Find the smallest value bigger than or equal to the target
                    if (asciiBrightness >= targetBrightness && difference < minDifference) {
                        closestChar = asciiChar;
                        minDifference = difference;
                    }
                    break;

                case ROUND_METHOD_DOWN: // Find the largest value smaller than or equal to the target
                    if (asciiBrightness <= targetBrightness && difference < minDifference) {
                        closestChar = asciiChar;
                        minDifference = difference;
                    }
                    break;
            }
        }

        return closestChar;
    }

    /*
     * Creates a 2D ASCII art representation from matched sub-images and characters.
     *
     * @param resultMap A map of sub-images and their matched ASCII characters.
     * @return A 2D char array representing the ASCII art.
     */

    private char[][] createAsciiImage(HashMap<Color[][], Character> resultMap) {
        // Create char array with the same dimensions as the padded image
        int paddedWidth = imageConverter.getPaddedImage().getImage().getWidth();
        int paddedHeight = imageConverter.getPaddedImage().getImage().getHeight();
        char[][] asciiArt = new char[paddedHeight][paddedWidth];

        // Fill with spaces initially
        for (int i = 0; i < paddedHeight; i++) {
            for (int j = 0; j < paddedWidth; j++) {
                asciiArt[i][j] = ' ';
            }
        }
        int numOfCols = resolution;
        int numOfRows = paddedHeight / (paddedWidth / resolution);

        // Calculate starting position to center the ASCII art
        int startX = (paddedWidth - numOfCols) / 2;
        int startY = (paddedHeight - numOfRows) / 2;

        int subImageIndex = 0;
        ArrayList<Color[][]> orderedSubImages = imageConverter.getSubImagesArray();
        for (int row = 0; row < numOfRows; row++) {
            for (int col = 0; col < numOfCols; col++) {
                char matchedChar = resultMap.get(orderedSubImages.get(subImageIndex));
                asciiArt[startY + row][startX + col] = matchedChar;
                subImageIndex++;
            }
        }
        return asciiArt;
    }

    /**
     * Runs the ASCII art generation process.
     *
     * @return A 2D char array representing the generated ASCII art.
     */
    public char [][] run(){
        HashMap<Color[][],Double> subImages = imageConverter.getNewResolutionArray();
        HashMap<Character, Double> asciiMap = subImgCharMatcher.getNormalizedBrightnessMap();
        HashMap<Color[][], Character> resultMap = matchAsciiToSubImage(subImages, asciiMap);
        return createAsciiImage(resultMap);
    }
}
