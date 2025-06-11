package src;
import java.awt.*;
public class Bomb {
    public int x;
    public int y;
    public int width;
    public int height;
    public Image img;
    int originalY;
    double timeOffset;
    double waveAmplitude = 35;
    double waveSpeed = 0.01;
    public boolean collected = false;
// Slightly different speed for variety

    public Bomb(int x, int y, int width, int height, Image img) {
        this.x = x;
        this.y = y;
        this.originalY = y;
        this.width = 2*width;
        this.height = 2*height;
        this.img = img;
        this.timeOffset = Math.random() * 3.14 * 2;
    }

    public void updatePosition(double velocityX) {
        this.x += velocityX;

        double waveInput = (360 - this.x) * waveSpeed + timeOffset;
        this.y = (int)(originalY + waveAmplitude * Math.sin(waveInput));

        this.y = Math.max(20, Math.min(this.y, 640 - height - 20));
    }
}