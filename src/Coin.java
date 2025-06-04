package src;

import java.awt.*;

public class Coin {

        public int x;
    public int y;
    public int width;
    public int height;
        public Image img;
        public boolean collected = false;

        public Coin(int x, int y, int width, int height, Image img) {
            this.x = x;
            this.y = y;
            this.width = 2*width;
            this.height = 2*height;
            this.img = img;
        }


}
