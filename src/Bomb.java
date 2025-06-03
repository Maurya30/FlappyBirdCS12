package src;

import java.awt.*;

public class Bomb {
    public int x;
    public int y;
    public int width;
    public int height;
    public Image img;
    int originalY; // Store the original Y position for sin wave calculation
    long spawnTime; // Track when this bomb was created
    double waveAmplitude = 40; // Slightly larger amplitude than eagles
    double waveFrequency = 0.025; // Slightly different frequency for variety

    public Bomb(int x, int y, int width, int height, Image img) {
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

        // Sin wave with different parameters than eagles
        this.y = (int)(originalY + waveAmplitude * Math.sin(waveFrequency * timeElapsed * 100));

        // Keep within screen bounds
        this.y = Math.max(10, Math.min(this.y, 600)); // Stay within reasonable bounds
    }
}