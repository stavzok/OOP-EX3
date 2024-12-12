package image_char_matching;
import java.util.Collections;
import java.util.HashMap;


public class SubImgCharMatcher {
    private final char[] charArray;
    private final HashMap<Character, Double> brightnessMap;
    private final HashMap<Character, Double> normalizedBrightnessMap;
    private double maxBrightness;
    private double minBrightness;


    public SubImgCharMatcher(char[] charset) {
        this.charArray = charset;
        this.normalizedBrightnessMap = new HashMap<>();
        this.brightnessMap = new HashMap<>();
        calculateBrightness();
        normalizeBrightness();


    }

    private double calculateSingleCharBrightness(char c) {
        boolean[][] tempArray = new boolean[16][16];
        tempArray = CharConverter.convertToBoolArray(c);
        int count = 0;
        for (int i = 0; i < tempArray.length; i++) {
            for (int j = 0; j < tempArray[i].length; j++) {
                if (tempArray[i][j]) {
                    count++;
                }
            }
        }
        double brightness = count / (double) tempArray.length * tempArray[0].length;
        brightnessMap.put(c, brightness);
        return brightness;

    }

    private void calculateBrightness() {
        for (char c : charArray) {
            calculateSingleCharBrightness(c);
        }
    }

    private void normalizeBrightness() {
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


    public void addChar(char c){
        double charBrightness = calculateSingleCharBrightness(c);
        if (charBrightness < minBrightness || charBrightness > maxBrightness) {
            normalizeBrightness();
        }
        else{

            Double newCharBrightness =
                    (charBrightness - minBrightness) / (maxBrightness - minBrightness);
            normalizedBrightnessMap.put(c, newCharBrightness);

        }



    }


    public void removeChar(char c){
        Double charBrightness = normalizedBrightnessMap.get(c);
        normalizedBrightnessMap.remove(c);
        brightnessMap.remove(c);
        if (charBrightness == minBrightness || charBrightness == maxBrightness) {
            normalizeBrightness();
        }

    }

    public HashMap<Character, Double> getNormalizedBrightnessMap(){
        return normalizedBrightnessMap;
    }


}
