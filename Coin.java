import java.awt.*;

public class Coin {

        int x, y, width, height;
        Image img;
        boolean collected = false;

        public Coin(int x, int y, int width, int height, Image img) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.img = img;
        }


}
