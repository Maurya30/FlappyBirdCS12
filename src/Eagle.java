package src;

import java.awt.*;

public class Eagle {
    public int x;
    public int y;
    public int width;
    public int height;
    public Image img;
    int originalY; // Store the original Y position for sin wave calculation
    double timeOffset; // Each eagle gets a unique time offset for wave variation
    double waveAmplitude = 40; // How far up/down the sin wave goes
    double waveSpeed = 0.01; // How fast the wave oscillates

    public Eagle(int x, int y, int width, int height, Image img) {
        this.x = x;
        this.y = y;
        this.originalY = y; // Remember the center line
        this.width = width;
        this.height = height;
        this.img = img;
        this.timeOffset = Math.random() * Math.PI * 2; // Random starting point in wave
    }

    public void updatePosition(double velocityX) {
        // Move horizontally from right to left (same speed as pipes)
        this.x += 2*velocityX;

        // Calculate sin wave movement based on horizontal position
        // Using x position for wave calculation makes it smooth and consistent
        double waveInput = (360 - this.x) * waveSpeed + timeOffset;
        this.y = (int)(originalY + waveAmplitude * Math.sin(waveInput));

        // Keep within screen bounds
        this.y = Math.max(20, Math.min(this.y, 640 - height - 20));
    }
}