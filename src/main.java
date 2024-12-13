import ascii_art.AsciiArtAlgorithm;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import image.Image;

public class main {

    public static void main(String[] args) {
        Image image = null;
        try {
            image = new Image("C:/Users/User/Documents/OOP/OOP-EX3/examples/cat.jpeg");
        } catch (Exception e) {
            e.printStackTrace();
        }


        char[] asciiChars = {'0', '1', '2', '3', '4', '5', '6', '7','8','9'};

        int resolution = 128;

        AsciiArtAlgorithm algo = new AsciiArtAlgorithm(image, resolution, asciiChars);
        char[][] asciiImage = algo.run();
        HtmlAsciiOutput asciiOutput = new HtmlAsciiOutput("C:/Users/User/Documents/OOP/OOP-EX3/examples/cat1.html",
                "Courier New");
        asciiOutput.out(asciiImage);

    }
}
