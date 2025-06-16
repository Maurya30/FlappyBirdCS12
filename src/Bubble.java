package src;

public class Bubble {
    public int x, y;
    public int size;
    public int velocityY;
    public long spawnTime;
    public static final int lifespan = 4000;

    public Bubble(int x, int y, int size, int velocityY) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.velocityY = velocityY;
        this.spawnTime = System.currentTimeMillis();
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - spawnTime > lifespan || y + size < 0;
    }

    public void move() {
        y += velocityY;
    }
}