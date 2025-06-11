package src;
import java.awt.*;
public class Eagle {
    public int x;
    public int y;
    public int width;
    public int height;
    public Image img;
    int originalY;
    double timeOffset;
    double waveAmplitude = 40;
    double waveSpeed = 0.01;

    public Eagle(int x, int y, int width, int height, Image img) {
        this.x = x;
        this.y = y;
        this.originalY = y;
        this.width = width;
        this.height = height;
        this.img = img;
        this.timeOffset = Math.random() * Math.PI * 2;
    }
    public void updatePosition(double velocityX) {
        this.x += 2*velocityX;

        double waveInput = (360 - this.x) * waveSpeed + timeOffset;
        this.y = (int)(originalY + waveAmplitude * Math.sin(waveInput));

        this.y = Math.max(20, Math.min(this.y, 640 - height - 20));
    }
}