package RenderingModes;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

public class Texture {
    private final Image image;
    private final PixelReader reader;
    private final int width;
    private final int height;

    public Texture(Image image) {
        if (image == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }
        this.image = image;
        this.width = (int) image.getWidth();
        this.height = (int) image.getHeight();

        PixelReader pixelReader = image.getPixelReader();
        if (pixelReader == null) {
            throw new IllegalArgumentException("Image pixel reader cannot be null");
        }
        this.reader = pixelReader;
    }

    public Image getImage() {
        return image;
    }

    public Color sample(float u, float v) {
        // Ограничиваем координаты UV в пределах [0, 1]
        u = Math.max(0.0f, Math.min(1.0f, u));
        v = Math.max(0.0f, Math.min(1.0f, v));

        // Конвертируем в пиксельные координаты (V инвертируется)
        int x = (int) (u * (width - 1));
        int y = (int) ((1.0f - v) * (height - 1));

        // Гарантируем, что координаты в пределах изображения
        x = Math.max(0, Math.min(width - 1, x));
        y = Math.max(0, Math.min(height - 1, y));

        return reader.getColor(x, y);
    }

    public boolean isLoaded() {
        return image != null;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}