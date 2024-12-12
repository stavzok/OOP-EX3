package ascii_art;

import image.Image;
import image.ImageConverter;
import image.PaddedImage;
import image_char_matching.SubImgCharMatcher;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class AsciiArtAlgorithm {
    private final Image originalImage;
    private final int resolution;
    private final char[] asciiChars;
    private final SubImgCharMatcher subImgCharMatcher;

    public AsciiArtAlgorithm(Image originalImage, int resolution, char [] asciiChars) {
        this.originalImage = originalImage;
        this.resolution = resolution;
        this.asciiChars = asciiChars;
        this.subImgCharMatcher = new SubImgCharMatcher(asciiChars);
    }


    private HashMap<Color[][], Character> matchAsciiToSubImage(
            HashMap<Color[][], Double> subImages,
            HashMap<Character, Double> asciiMap
    ) {
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
            } else if (difference < nextMinDifference) {
                // Update second closest only
                nextMinDifference = difference;
            }
        }

        return closestChar;
    }


    public char [][] run(){
        PaddedImage paddedImage = new PaddedImage(originalImage);
        ImageConverter imageConverter = new ImageConverter(paddedImage, resolution);
        HashMap<Color[][],Double> subImages = imageConverter.getNewResolutionArray();
        HashMap<Character, Double> asciiMap = subImgCharMatcher.getNormalizedBrightnessMap();
        Color [][] piexlArray= new Color[paddedImage.getImage().getWidth()][paddedImage.getImage().getHeight()];



    }
}
