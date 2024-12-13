import ascii_art.AsciiArtAlgorithm;
import ascii_output.ConsoleAsciiOutput;
import image.Image;

public class main {

    public static void main(String[] args) {
        Image image = null;
        try {
            image = new Image("/C://Users//User//Documents//OOP//OOP-EX3//src//board.jpeg/");
        } catch (Exception e) {
            e.printStackTrace();
        }


        char[] asciiChars = {'m', 'o'};

        int resolution = 2;

        AsciiArtAlgorithm algo = new AsciiArtAlgorithm(image, resolution, asciiChars);
        char[][] asciiImage = algo.run();

        ConsoleAsciiOutput asciiOutput = new ConsoleAsciiOutput();
        asciiOutput.out(asciiImage);




    }
}
