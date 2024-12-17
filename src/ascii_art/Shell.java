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

public class Shell {
    private final int DEFAULT_RESOLUTION = 256;
    private final String ROUNDING_MODE = "abs";
    private final char[] DEFAULT_CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private final AsciiOutput DEFAULT_OUTPUT = new ConsoleAsciiOutput();
    private final HashSet<Character> ASCII_CHARS;
    private int resolution;
    private String roundMethod;
    private AsciiOutput outputFormat;
    private final String font = "Courier new";
    private final String outputFile = "out.html";
    private SubImgCharMatcher subImgCharMatcher;
    private ImageConverter imageConverter;
    private PaddedImage paddedImage;
    private int asciiCounter = 0;
    private Memento memento;



    public Shell() {
        this.ASCII_CHARS = buildAsciiChars();
        this.resolution = DEFAULT_RESOLUTION;
        this.roundMethod = ROUNDING_MODE;
        this.outputFormat = DEFAULT_OUTPUT;
        this.subImgCharMatcher = new SubImgCharMatcher(DEFAULT_CHARS);
    }

    private HashSet<Character> buildAsciiChars() {
        HashSet<Character> asciiChars = new HashSet<>();
        for (char c = ' '; c <= '~'; c++) {
            asciiChars.add(c);
        }
        return asciiChars;
    }

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

    private int handleResChange(String resCommand) throws IOException {
        int newRes = 0;
        if(resCommand.equals("up")) {
            newRes = resolution * 2;
        }
        else if (resCommand.equals("down")) {
            newRes = resolution = 2;
        }
        else {
            throw new IOException();
        }
        return newRes;
    }

    private void handleRound(String command) throws IOException {
        switch (command) {
            case "up" -> roundMethod = "up";
            case "down" -> roundMethod = "down";
            case "abs" -> roundMethod = "abs";
            default -> throw new IOException();
        }
    }

    private void handleOutputMethod(String command) throws IOException {
        switch (command) {
            case "console" -> outputFormat = new ConsoleAsciiOutput();
            case "html" -> outputFormat = new HtmlAsciiOutput(outputFile, font);
            default -> throw new IOException();
        }
    }


    public void run(String imageName) throws IllegalArgumentException, IOException {

        Image image;
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
                        newRes = handleResChange(inputAnswer.split(" ")[1]);
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
                    catch (IOException e) {
                        System.out.println("Did not change rounding method due to incorrect format.");

                    }
                    break;
                case "output":
                    try {
                        handleOutputMethod(command);
                    }
                    catch (IOException e) {
                        System.out.println("Did not change output method due to incorrect format.");
                    }
                    break;

                case "asciiArt":

                    if(subImgCharMatcher.getCharSet().size() < 2) {
                        System.out.println("Did not execute. Charset is too small");
                        return;
                    }
                    compareToMemento();
                    paddedImage = new PaddedImage(image);
                    imageConverter = new ImageConverter(paddedImage, resolution);
                    AsciiArtAlgorithm algo = new AsciiArtAlgorithm(resolution, subImgCharMatcher, roundMethod, imageConverter);
                    char[][] asciiImage = algo.run();
                    outputFormat.out(asciiImage);
                    memento = saveToMemento();
                    subImgCharMatcher = new SubImgCharMatcher(DEFAULT_CHARS);
                    resolution = DEFAULT_RESOLUTION;
                    break;
                default:
                    System.out.println("Did not execute due to incorrect command.");
            }
        }
    }
    private void compareToMemento() {
        if (memento != null) {
            // 1. Avoid recalculating brightness values for subimages
            if (memento.oldResolution == resolution && memento.subImageBrightnessMap != null) {
                imageConverter.setSubImages(memento.subImageBrightnessMap);
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
            }
        }
    }

    public Memento saveToMemento() {
        return new Memento(subImgCharMatcher, imageConverter, resolution);
    }

    public void restoreFromMemento(Memento memento) {

    }




    public static class Memento {
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

    public static void main(String[] args) {
        Shell shell = new Shell();
        try {
            shell.run("C:/Users/stav/IdeaProjects/OOP-EX3/examples/cat.jpeg");
        }
        catch (IOException e) {
            throw new RuntimeException(e); // think about it!!
        }
    }
}


