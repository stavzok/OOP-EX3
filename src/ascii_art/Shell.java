package ascii_art;
import ascii_output.AsciiOutput;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import image.Image;

import java.io.IOException;
import java.util.HashSet;

public class Shell {
    private final int DEFAULT_RESOLUTION = 2;
    private final String ROUNDING_MODE = "abs";
    private final char[] DEFAULT_CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private final AsciiOutput DEFAULT_OUTPUT = new ConsoleAsciiOutput();
    private HashSet<Character> charSet;
    private final HashSet<Character> ASCII_CHARS;
    private int resolution;
    private String roundMethod;
    private AsciiOutput outputFormat;
    private final String font = "Courier new";
    private final String outputFile = "out.html";

    public Shell() {
        this.charSet = new HashSet<>();
        for (char c : DEFAULT_CHARS) {
            charSet.add(c);
        }
        this.ASCII_CHARS = buildAsciiChars();
        this.resolution = DEFAULT_RESOLUTION;
        this.roundMethod = ROUNDING_MODE;
        this.outputFormat = DEFAULT_OUTPUT;
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
        char[] sortedChars = charSet.stream()
                .map(String::valueOf) // Convert each Character to String
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append) // Combine to a single String
                .toString()
                .toCharArray();

        for (char c : sortedChars) {
            System.out.print(c + " ");
        }
        System.out.println();
    }

    private void handleAdd(String command) {
        if (command.equals("all")) {
            for (char c : ASCII_CHARS) {
                charSet.add(c);
            }
        }

        else if (command.equals("space")) {
            charSet.add(' ');
        }

        else if (command.length() == 1 && ASCII_CHARS.contains(command.charAt(0))) {
            charSet.add(command.charAt(0));
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
                charSet.add(c);
            }
        }
        else {
            throw new IllegalArgumentException();
        }
    }

    private void handleRemove(String command) {
        if (command.equals("all")) {
            for (char c : ASCII_CHARS) {
                charSet.remove(c);
            }
        }

        else if (command.equals("space")) {
            charSet.remove(' ');
        }

        else if (command.length() == 1 && ASCII_CHARS.contains(command.charAt(0))) {
            charSet.remove(command.charAt(0));
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
                charSet.remove(c);
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

    private char[] convertCharSetToArray(HashSet<Character> charSet) {
        // Create a char[] with the same size as the HashSet
        char[] charArray = new char[charSet.size()];

        // Use a loop to copy characters from the HashSet to the array
        int index = 0;
        for (Character c : charSet) {
            charArray[index++] = c; // Auto-unbox Character to char
        }
        return charArray;
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
                    char[] chars = convertCharSetToArray(charSet);
                    if(chars.length < 2) {
                        System.out.println("Did not execute. Charset is too small");
                        return;
                    }
                    AsciiArtAlgorithm algo = new AsciiArtAlgorithm(image, resolution, chars, roundMethod);
                    char[][] asciiImage = algo.run();
                    outputFormat.out(asciiImage);
                    break;
                default:
                    System.out.println("Did not execute due to incorrect command.");
            }
        }
    }

    public static void main(String[] args) {
        Shell shell = new Shell();
        try {
            shell.run("cat");
        }
        catch (IOException e) {
            throw new RuntimeException(e); // think about it!!
        }
    }
}


