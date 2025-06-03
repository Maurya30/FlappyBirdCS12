package src;
import java.awt.Image;

public class Eagle {
    public int x;
    public int y;
    public int width;
    public int height;
    public Image img;
    int originalY; // Store the original Y position for sin wave calculation
    long spawnTime; // Track when this eagle was created
    double waveAmplitude = 30; // How far up/down the sin wave goes
    double waveFrequency = 0.03; // How fast the wave oscillates

    public Eagle(int x, int y, int width, int height, Image img) {
        this.x = x;
        this.y = y;
        this.originalY = y; // Remember the center line
        this.width = width;
        this.height = height;
        this.img = img;
        this.spawnTime = System.currentTimeMillis();
    }

    public void updatePosition(double velocityX) {
        // Move horizontally (same as pipes)
        this.x += velocityX;

        // Calculate sin wave movement for Y position
        long currentTime = System.currentTimeMillis();
        double timeElapsed = (currentTime - spawnTime) / 1000.0; // Time in seconds

        // Sin wave: y = originalY + amplitude * sin(frequency * time)
        this.y = (int)(originalY + waveAmplitude * Math.sin(waveFrequency * timeElapsed * 100));

        // Keep within screen bounds
        this.y = Math.max(10, Math.min(this.y, 590)); // Stay within reasonable bounds
    }
}

