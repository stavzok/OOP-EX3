package ascii_art;

import ascii_output.AsciiOutput;
import ascii_output.ConsoleAsciiOutput;
import java.util.Arrays;
import java.util.HashSet;

import ascii_output.HtmlAsciiOutput;
import image.Image;
import image_char_matching.SubImgCharMatcher;


public class Shell {
    private final int DEFAULT_RESOLUTION = 2;
    private final String ROUNDING_MODE = "Abs";
    private final char[] DEFAULT_CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private final AsciiOutput DEFAULT_OUTPUT = new ConsoleAsciiOutput();
    private HashSet<Character> charSet;
    private final HashSet<Character> ASCII_CHARS;
    {
        ASCII_CHARS = new HashSet<>();
        for (char c = ' '; c <= '~'; c++) {
            ASCII_CHARS.add(c);
        }
    }
    public Shell() {
        this.charSet = new HashSet<>();
        for (char c : DEFAULT_CHARS) {
            charSet.add(c);
        }
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
        } else if (command.equals("space")) {
            charSet.add(' ');
        } else if (command.length() == 1 && ASCII_CHARS.contains(command.charAt(0))) {
            charSet.add(command.charAt(0));
        } else if (command.length() == 3 && ASCII_CHARS.contains(command.charAt(0)) && ASCII_CHARS.contains(command.charAt(2)) &&
                command.charAt(1) == '-') {
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
        } else {
            throw new IllegalArgumentException("Did not add due to incorrect format.\n");


        }
    }

    public void run(String imageName) {
        while(true) {
            System.out.print(">>> ");
            String inputAnswer = KeyboardInput.readLine();
            if (inputAnswer.equals("exit")) {return;}
            if(inputAnswer.equals("chars")) {
                printCharArray();
            }
            if (inputAnswer.split(" ")[0].equals("add")) {
                handleAdd(inputAnswer.split(" ")[1]);




                }
        }

//        //Image image = null;
//        try {
//            image = new Image("C:/Users/stav/IdeaProjects/OOP-EX3/examples/cat.jpeg");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//        char[] asciiChars = new char[95]; // 126 - 32 + 1 = 95 characters
//        for (int i = 0; i < asciiChars.length; i++) {
//            asciiChars[i] = (char)(i + 32);
//        }
//        int resolution = 256;
//
//        AsciiArtAlgorithm algo = new AsciiArtAlgorithm(image, resolution, asciiChars);
//        char[][] asciiImage = algo.run();
//        HtmlAsciiOutput asciiOutput = new HtmlAsciiOutput("C:/Users/stav/IdeaProjects/OOP-EX3/examples/catNew.html", "Courier New");
//        asciiOutput.out(asciiImage);




    }



    public static void main(String[] args) {
        Shell shell = new Shell();
        shell.run("cat");

    }




}


