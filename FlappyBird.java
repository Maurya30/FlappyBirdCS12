import java.awt.*;
import java.util.*;
import javax.swing.*;
public class FlappyBird {
    public static void main(String[] args) {
        int width = 360;
        int length = 640;

        Image backgroundImg;
        Image birdImg;
        Image topPipeImg;
        Image bottomPipeImg;

        int birdX = boardWidth/8;
        int birdY = boardHeight/2;
        int birdWidth = 34;
        int birdHeight = 24;

        class Bird {
            int x = birdX;
            int y = birdY;
            int width = birdWidth;
            int height = birdHeight;
            Image img;

            Bird(Image img) {
                this.img = img;
            }
            
        }
    }
}
