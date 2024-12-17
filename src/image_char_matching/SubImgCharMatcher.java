package image_char_matching;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

/**
 * The SubImgCharMatcher class calculates and manages the brightness values
 * of ASCII characters to match them to sub-image brightness values.
 */

public class SubImgCharMatcher {
    /* Set of characters being used for matching. */

    private HashSet<Character> charSet;
    /* Map to store the raw brightness values for each character. */

    private HashMap<Character, Double> brightnessMap;
    /* Map to store the normalized brightness values for each character. */

    private HashMap<Character, Double> normalizedBrightnessMap;
    /* The maximum and minimum brightness values for normalization. */

    private double maxBrightness;
    private double minBrightness;

    /**
     * Constructs a SubImgCharMatcher instance.
     * Initializes the character set and calculates their brightness values.
     *
     * @param charArray An array of characters to be used for matching.
     */
    public SubImgCharMatcher(char[] charArray) {
        charSet = new HashSet<>();
        for (char c : charArray) {
            charSet.add(c);
        }
        this.normalizedBrightnessMap = new HashMap<>();
        this.brightnessMap = new HashMap<>();
        calculateBrightness();
    }

    /*
     * Calculates the brightness of a single character by analyzing its boolean representation.
     *
     * @param c The character whose brightness is to be calculated.
     * @return The calculated brightness value of the character.
     */
    private double calculateSingleCharBrightness(char c) {
        System.out.println("Calculating single char brightness...");
        boolean[][] tempArray;
        tempArray = CharConverter.convertToBoolArray(c);
        int count = 0;
        for (int i = 0; i < tempArray.length; i++) {
            for (int j = 0; j < tempArray[i].length; j++) {
                if (tempArray[i][j]) {
                    count++;
                }
            }
        }
        double brightness = count / ((double) tempArray.length * tempArray[0].length);
        brightnessMap.put(c, brightness);
        return brightness;

    }

    /*
     * Calculates brightness values for all characters in the set.
     */
    private void calculateBrightness() {
        for (char c : charSet) { // Iterate over the HashSet
            System.out.println("Calculating brightness...");
            calculateSingleCharBrightness(c);
        }
    }

    /**
     * Normalizes the brightness values of all characters.
     * Scales the brightness values to a range between 0 and 1.
     */
    public void normalizeBrightness() {
        System.out.println("Normalizing...");
        minBrightness = Collections.min(brightnessMap.values());
        maxBrightness = Collections.max(brightnessMap.values());
        for (HashMap.Entry<Character, Double> entry : brightnessMap.entrySet()) {
            Character key = entry.getKey();
            Double value = entry.getValue();
            Double newCharBrightness =
                    (value - minBrightness) / (maxBrightness - minBrightness);
            normalizedBrightnessMap.put(key, newCharBrightness);
        }
    }

    /**
     * Adds a character to the set and calculates its brightness value.
     *
     * @param c The character to be added.
     */
    public char getCharByImageBrightness(double brightness){
        char closestChar = '\0'; // Default value (null character)
        double minDifference = Double.MAX_VALUE; // Start with the largest possible difference
        for (HashMap.Entry<Character, Double> entry : normalizedBrightnessMap.entrySet()) {
            double difference = Math.abs(entry.getValue() - brightness); // Absolute difference
            if (difference < minDifference || (difference == minDifference && entry.getKey() < closestChar)) {
                minDifference = difference;
                closestChar = entry.getKey(); // Update the closest character
            }
        }
        return closestChar;
    }

    /**
     * Adds a character to the set and calculates its brightness value.
     *
     * @param c The character to be added.
     */

    public void addChar(char c){
        int oldSize = charSet.size();
        charSet.add(c);
        int newSize = charSet.size();
        if (oldSize != newSize) {
            double charBrightness = calculateSingleCharBrightness(c);
            brightnessMap.put(c, charBrightness);
        }
    }

    /**
     * Removes a character from the set and its associated brightness values.
     *
     * @param c The character to be removed.
     */

    public void removeChar(char c){
        charSet.remove(c);
        Double charBrightness = normalizedBrightnessMap.get(c);
        normalizedBrightnessMap.remove(c);
        brightnessMap.remove(c);
    }

    /**
     * Sets a precomputed normalized brightness map.
     *
     * @param newNormalizedBrightnessMap A map of characters and their normalized brightness values.
     */
    public void setNormalizedBrightnessMap(HashMap<Character, Double> newNormalizedBrightnessMap) {
        normalizedBrightnessMap = newNormalizedBrightnessMap;
    }
    /**
     * Retrieves the current set of characters.
     *
     * @return A HashSet containing the characters.
     */

    public HashSet<Character> getCharSet(){
        return charSet;
    }
    /**
     * Retrieves the raw brightness values for the characters.
     *
     * @return A HashMap containing characters and their brightness values.
     */

    public HashMap<Character, Double> getBrightnessMap(){return brightnessMap;}

    /**
     * Retrieves the normalized brightness values for the characters.
     *
     * @return A HashMap containing characters and their normalized brightness values.
     */

    public HashMap<Character, Double> getNormalizedBrightnessMap(){
        return normalizedBrightnessMap;
    }

}
