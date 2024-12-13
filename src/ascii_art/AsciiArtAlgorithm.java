package ascii_art;

import image.Image;
import image.ImageConverter;
import image.PaddedImage;
import image_char_matching.SubImgCharMatcher;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AsciiArtAlgorithm {
    private final int resolution;
    private final SubImgCharMatcher subImgCharMatcher;
    private final PaddedImage paddedImage;
    private final ImageConverter imageConverter;

    public AsciiArtAlgorithm(Image originalImage, int resolution, char [] asciiChars) {
        this.resolution = resolution;
        this.subImgCharMatcher = new SubImgCharMatcher(asciiChars);
        this.paddedImage = new PaddedImage(originalImage);
        this.imageConverter = new ImageConverter(paddedImage, resolution);
    }

    private HashMap<Color[][], Character> matchAsciiToSubImage(
            HashMap<Color[][], Double> subImages,
            HashMap<Character, Double> asciiMap) {
        HashMap<Color[][], Character> resultMap = new HashMap<>();

        for (Map.Entry<Color[][], Double> subImageEntry : subImages.entrySet()) {
            Color[][] subImage = subImageEntry.getKey();
            Double subImageBrightness = subImageEntry.getValue();

            // Find the two closest characters
            Character closestChar = findClosestCharacters(asciiMap, subImageBrightness);
            resultMap.put(subImage, closestChar); // Store the closest characters
        }
        return resultMap;
    }

    private Character findClosestCharacters(HashMap<Character, Double> asciiMap, Double targetBrightness) {
        Character closestChar = null;
        double minDifference = Double.MAX_VALUE;
        double nextMinDifference = Double.MAX_VALUE;

        for (Map.Entry<Character, Double> asciiEntry : asciiMap.entrySet()) {
            char asciiChar = asciiEntry.getKey();
            Double asciiBrightness = asciiEntry.getValue();

            double difference = Math.abs(asciiBrightness - targetBrightness);

            if (difference < minDifference) {
                // Update second closest before replacing the closest
                nextMinDifference = minDifference;

                // Update closest
                closestChar = asciiChar;
                minDifference = difference;
            }
            else if (difference < nextMinDifference) {
                // Update second closest only
                nextMinDifference = difference;
            }
        }

        return closestChar;
    }

    private char[][] createAsciiImage(HashMap<Color[][], Character> resultMap) {
        // Create char array with the same dimensions as the padded image
        int paddedWidth = paddedImage.getImage().getWidth();
        int paddedHeight = paddedImage.getImage().getHeight();
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

    public char [][] run(){
        HashMap<Color[][],Double> subImages = imageConverter.getNewResolutionArray();
        HashMap<Character, Double> asciiMap = subImgCharMatcher.getNormalizedBrightnessMap();
        HashMap<Color[][], Character> resultMap = matchAsciiToSubImage(subImages, asciiMap);
        return createAsciiImage(resultMap);
    }
}
