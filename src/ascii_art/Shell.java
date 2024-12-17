package ascii_art;
import ascii_output.AsciiOutput;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import image.Image;
import image.ImageConverter;
import image.PaddedImage;
import image_char_matching.SubImgCharMatcher;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;


/**
 * Shell class provides a command-line interface for generating ASCII art from images.
 * The user can customize character sets, resolution, rounding methods, and output format.
 *
 * @author stavzok, inbar.el
 */

public class Shell {
    /* Default resolution for ASCII art generation. */
    private final int DEFAULT_RESOLUTION = 256;
    /* Default rounding mode for brightness calculations. */

    private final String ROUNDING_MODE = "abs";
    /* Default set of characters used for ASCII art. */

    private final char[] DEFAULT_CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    /* Default output format for ASCII art (console output). */
    private final AsciiOutput DEFAULT_OUTPUT = new ConsoleAsciiOutput();
    /* Set of all valid ASCII characters. */

    private final HashSet<Character> ASCII_CHARS;
    /* Current resolution for the ASCII art generation. */

    private int resolution;
    /* Rounding method for brightness calculations (e.g., abs, up, down). */

    private String roundMethod;
    /* Output format for ASCII art. */

    private AsciiOutput outputFormat;
    /* Font used for HTML output. */

    private final String font = "Courier new";
    /* Output file name for HTML output. */

    private final String outputFile = "out.html";
    /* Matcher to map sub-images to ASCII characters based on brightness. */

    private SubImgCharMatcher subImgCharMatcher;
    /* Image converter for generating ASCII-compatible sub-images. */

    private ImageConverter imageConverter;
    /* Image padded to fit the resolution constraints. */

    private PaddedImage paddedImage;
    /* A snapshot of the previous state to optimize performance. */

    private Memento memento;

    /**
     * Constructs a new Shell instance with default parameters.
     */

    public Shell() {
        this.ASCII_CHARS = buildAsciiChars();
        this.resolution = DEFAULT_RESOLUTION;
        this.roundMethod = ROUNDING_MODE;
        this.outputFormat = DEFAULT_OUTPUT;
        this.subImgCharMatcher = new SubImgCharMatcher(DEFAULT_CHARS);
    }

    /*
     * Builds the set of all printable ASCII characters.
     *
     * @return A HashSet containing all valid ASCII characters.
     */


    private HashSet<Character> buildAsciiChars() {
        HashSet<Character> asciiChars = new HashSet<>();
        for (char c = ' '; c <= '~'; c++) {
            asciiChars.add(c);
        }
        return asciiChars;
    }

    /*
     * Prints the current set of characters used for ASCII art, sorted alphabetically.
     */

    private void printCharArray() {
        // Print the sorted characters
        HashSet<Character> charSet = subImgCharMatcher.getCharSet();
        char[] sortedChars = charSet.stream()
                .sorted() // Sort the stream of characters
                .map(String::valueOf) // Convert each Character to String
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append) // Combine to a single String
                .toString()
                .toCharArray();

        // Print the sorted characters
        for (char c : sortedChars) {
            System.out.print(c + " ");
        }
        System.out.println();
    }

    /*
     * Handles the addition of characters to the character set.
     *
     * @param command The command specifying the characters to add.
     * @throws IllegalArgumentException If the command format is invalid.
     */

    private void handleAdd(String command) {
        if (command.equals("all")) {
            for (char c : ASCII_CHARS) {
                subImgCharMatcher.addChar(c);
            }
        }

        else if (command.equals("space")) {
            subImgCharMatcher.addChar(' ');
        }

        else if (command.length() == 1 && ASCII_CHARS.contains(command.charAt(0))) {
            subImgCharMatcher.addChar(command.charAt(0));
        }

        else if (command.length() == 3 && ASCII_CHARS.contains(command.charAt(0)) &&
                ASCII_CHARS.contains(command.charAt(2)) && command.charAt(1) == '-') {
            char start = command.charAt(0);
            char end = command.charAt(2);

            // Ensure start is the smaller character
            if (start > end) {
                char temp = start;
                start = end;
                end = temp;
            }
            for (char c = start; c <= end; c++) {
                subImgCharMatcher.addChar(c);
            }
        }
        else {
            throw new IllegalArgumentException();
        }
    }

    /*
     * Handles the removal of characters from the character set.
     *
     * @param command The command specifying the characters to remove.
     * @throws IllegalArgumentException If the command format is invalid.
     */

    private void handleRemove(String command) {
        if (command.equals("all")) {
            for (char c : ASCII_CHARS) {
                subImgCharMatcher.removeChar(c);
            }
        }

        else if (command.equals("space")) {
            subImgCharMatcher.removeChar(' ');
        }

        else if (command.length() == 1 && ASCII_CHARS.contains(command.charAt(0))) {
            subImgCharMatcher.removeChar(command.charAt(0));
        }

        else if (command.length() == 3 && ASCII_CHARS.contains(command.charAt(0)) &&
                ASCII_CHARS.contains(command.charAt(2)) && command.charAt(1) == '-') {
            char start = command.charAt(0);
            char end = command.charAt(2);

            // Ensure start is the smaller character
            if (start > end) {
                char temp = start;
                start = end;
                end = temp;
            }
            for (char c = start; c <= end; c++) {
                subImgCharMatcher.removeChar(c);
            }
        }
        else {
            throw new IllegalArgumentException();
        }
    }

    /*
     * Handles changes to the resolution of the ASCII art.
     *
     * @param resCommand The command indicating whether to increase ("up") or reset ("down") the resolution.
     * @return The new resolution value after applying the change.
     * @throws IOException If the command is invalid.
     */
    private int handleResChange(String resCommand) {
        int newRes = 0;
        if(resCommand.equals("up")) {
            newRes = resolution * 2;
        }
        else if (resCommand.equals("down")) {
            newRes = resolution = 2;
        }
        else {
            throw new IllegalArgumentException();
        }
        return newRes;
    }

    /*
     * Handles changes to the rounding method used in brightness calculations.
     *
     * @param command The new rounding method ("up", "down", or "abs").
     * @throws IOException If the provided command is invalid.
     */
    private void handleRound(String command) {
        switch (command) {
            case "up" -> roundMethod = "up";
            case "down" -> roundMethod = "down";
            case "abs" -> roundMethod = "abs";
            default -> throw new IllegalArgumentException();
        }
    }

    /*
     * Handles changes to the output format for the ASCII art.
     *
     * @param command The new output method ("console" for console output or "html" for HTML file output).
     * @throws IOException If the provided command is invalid.
     */
    private void handleOutputMethod(String command) {
        switch (command) {
            case "console" -> outputFormat = new ConsoleAsciiOutput();
            case "html" -> outputFormat = new HtmlAsciiOutput(outputFile, font);
            default -> throw new IllegalArgumentException();
        }
    }


    /**
     * Runs the command-line interface for generating ASCII art.
     *
     * @param imageName The name of the image file to process.
     */
    public void run(String imageName) {

        Image image;
        try {
            image = new Image(imageName);
            int maxResolution = image.getWidth();
            int minResolution = Math.max(1, image.getWidth() / image.getHeight());

            while (true) {
                System.out.print(">>> ");
                String inputAnswer = KeyboardInput.readLine();
                String command = (inputAnswer.split(" ").length > 1) ? inputAnswer.split(" ")[1] : "";
                switch (inputAnswer.split(" ")[0]) {
                    case "exit":
                        return;
                    case "chars":
                        printCharArray();
                        break;
                    case "res":
                        int newRes = resolution;
                        if (inputAnswer.split(" ").length > 1) {
                            try {
                                newRes = handleResChange(inputAnswer.split(" ")[1]);
                            }
                            catch (IllegalArgumentException e) {
                                System.out.println("Did not change resolution due to incorrect format.");
                            }
                        }
                        if (newRes > maxResolution || newRes < minResolution) {
                            System.out.println("Did not change resolution due to exceeding boundaries");
                            return;
                        }
                        resolution = newRes;
                        System.out.println("Resolution set to " + resolution);
                        break;
                    case "add":
                        try {
                            handleAdd(command);
                        }
                        catch (IllegalArgumentException e) {
                            System.out.println("Did not add due to incorrect format.");
                        }
                        break;
                    case "remove":
                        try {
                            handleRemove(command);
                        }
                        catch (IllegalArgumentException e) {
                            System.out.println("Did not remove due to incorrect format.");
                        }
                        break;

                        case "round":
                        try {
                            handleRound(command);
                        }
                        catch (IllegalArgumentException e) {
                            System.out.println("Did not change rounding method due to incorrect format.");

                        }
                        break;
                    case "output":
                        try {
                            handleOutputMethod(command);
                        }
                        catch (IllegalArgumentException e) {
                            System.out.println("Did not change output method due to incorrect format.");
                        }
                        break;

                    case "asciiArt":
                        if(subImgCharMatcher.getCharSet().size() < 2) {
                            System.out.println("Did not execute. Charset is too small");
                            return;
                        }
                        if(!compareToMemento()[0]) {
                            paddedImage = new PaddedImage(image);
                            imageConverter = new ImageConverter(paddedImage, resolution);
                        }

                        if(!compareToMemento()[1]) {
                            subImgCharMatcher.normalizeBrightness();
                        }

                        AsciiArtAlgorithm algo = new AsciiArtAlgorithm(resolution, subImgCharMatcher, roundMethod, imageConverter);
                        char[][] asciiImage = algo.run();
                        outputFormat.out(asciiImage);
                        memento = saveToMemento();
                        resolution = DEFAULT_RESOLUTION;
                        break;
                    default:
                        // replace with try catch?
                        System.out.println("Did not execute due to incorrect command.");
                }
            }
        }
        catch (IOException e) {

        }

    }


    /**
     * Compares the current state with the saved Memento to avoid redundant recalculations.
     *
     * @return A boolean array where:
     *         - index 0 is true if subimage brightness values can be reused.
     *         - index 1 is true if the normalized brightness map can be reused.
     */


    private boolean[] compareToMemento() {
        boolean flag[] = {false, false};
        if (memento != null) {
            // 1. Avoid recalculating brightness values for subimages
            if (memento.oldResolution == resolution && memento.subImageBrightnessMap != null) {
                imageConverter.setSubImages(memento.subImageBrightnessMap);
                flag[0] = true;
            }

            // 2. Avoid recalculating brightness values of characters already processed
            HashSet<Character> currentCharSet = subImgCharMatcher.getCharSet();
            HashSet<Character> oldCharSet = memento.oldCharSet;

            if (oldCharSet != null) {
                for (char c : oldCharSet) {
                    if (currentCharSet.contains(c) && !subImgCharMatcher.getBrightnessMap().containsKey(c)) {
                        // Reuse old brightness values
                        subImgCharMatcher.getBrightnessMap().put(c, memento.oldCharBrightnessMap.get(c));
                    }
                }
            }

            // 3. Avoid recalculating normalized brightness map
            if (currentCharSet.equals(oldCharSet)) {
                subImgCharMatcher.setNormalizedBrightnessMap(memento.oldCharNormalizedBrightnessMap);
                flag[1] = true;
            }
        }
        return flag;
    }

    /* Saves the current state into a Memento object. */

    private Memento saveToMemento() {
        return new Memento(subImgCharMatcher, imageConverter, resolution);
    }

    /**
     * Inner class representing a snapshot of the Shell state for optimization.
     */

    private static class Memento {
        private  HashSet<Character> oldCharSet;
        private  HashMap<Character, Double> oldCharBrightnessMap;
        private  HashMap<Character, Double> oldCharNormalizedBrightnessMap;
        private HashMap<Color[][], Double> subImageBrightnessMap;
        private int oldResolution;
        private Memento(SubImgCharMatcher subImgCharMatcher, ImageConverter imageConverter, int resolution) {
            this.oldCharSet = subImgCharMatcher.getCharSet();
            this.oldCharBrightnessMap = subImgCharMatcher.getBrightnessMap();
            this.oldCharNormalizedBrightnessMap = subImgCharMatcher.getNormalizedBrightnessMap();
            this.subImageBrightnessMap = imageConverter.getNewResolutionArray();
            this.oldResolution = resolution;
        }
        
    }

    /**
     * The main method to run the Shell program.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        Shell shell = new Shell();
        shell.run("C:/Users/User/Documents/OOP/OOP-EX3/examples/cat.jpeg");
    }
}


