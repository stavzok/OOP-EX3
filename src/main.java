import ascii_art.AsciiArtAlgorithm;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import image.Image;

public class main {

    public static void main(String[] args) {
        Image image = null;
        try {
            image = new Image("C:/Users/stav/IdeaProjects/OOP-EX3/examples/cat.jpeg");
        } catch (Exception e) {
            e.printStackTrace();
        }


        char[] asciiChars = new char[95]; // 126 - 32 + 1 = 95 characters
        for (int i = 0; i < asciiChars.length; i++) {
            asciiChars[i] = (char)(i + 32);
        }
        int resolution = 256;

        AsciiArtAlgorithm algo = new AsciiArtAlgorithm(image, resolution, asciiChars, "abs");
        char[][] asciiImage = algo.run();
        HtmlAsciiOutput asciiOutput = new HtmlAsciiOutput("C:/Users/stav/IdeaProjects/OOP-EX3/examples/catNew.html", "Courier New");
        asciiOutput.out(asciiImage);

    }
}
