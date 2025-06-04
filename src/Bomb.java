package src;

import java.awt.*;

public class Bomb {
    public int x;
    public int y;
    public int width;
    public int height;
    public Image img;
    int originalY; // Store the original Y position for sin wave calculation
    double timeOffset; // Each bomb gets a unique time offset
    double waveAmplitude = 35; // Slightly different amplitude than eagles
    double waveSpeed = 0.01;
    public boolean collected = false;
// Slightly different speed for variety

    public Bomb(int x, int y, int width, int height, Image img) {
        this.x = x;
        this.y = y;
        this.originalY = y; // Remember the center line
        this.width = 2*width;
        this.height = 2*height;
        this.img = img;
        this.timeOffset = Math.random() * Math.PI * 2; // Random starting point in wave
    }

    public void updatePosition(double velocityX) {
        // Move horizontally from right to left (same speed as pipes)
        this.x += velocityX;

        // Calculate sin wave movement with different parameters than eagles
        double waveInput = (360 - this.x) * waveSpeed + timeOffset;
        this.y = (int)(originalY + waveAmplitude * Math.sin(waveInput));

        // Keep within screen bounds
        this.y = Math.max(20, Math.min(this.y, 640 - height - 20));
    }
}