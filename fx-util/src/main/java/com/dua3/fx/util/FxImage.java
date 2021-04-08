package com.dua3.fx.util;

import com.dua3.utility.data.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Objects;

public class FxImage extends Image {
    
    private final javafx.scene.image.Image fxImage;
    
    public static void write(javafx.scene.image.Image fxImage, OutputStream out) throws IOException {
        PixelReader reader = fxImage.getPixelReader();
        int width = (int) Math.round(fxImage.getWidth());
        int height = (int) Math.round(fxImage.getHeight());
        byte[] buffer = new byte[width * height * 4];
        WritablePixelFormat<ByteBuffer> format = PixelFormat.getByteBgraInstance();
        reader.getPixels(0, 0, width, height, format, buffer, 0, width * 4);

        for(int count = 0; count < buffer.length; count += 4) {
            out.write(buffer[count + 2]);
            out.write(buffer[count + 1]);
            out.write(buffer[count]);
            out.write(buffer[count + 3]);
        }
    }

    public FxImage(javafx.scene.image.Image fxImage) {
        this.fxImage = Objects.requireNonNull(fxImage);
    }
    
    @Override
    public void write(OutputStream out) throws IOException {
        write(fxImage, out);
    }

    @Override
    public int width() {
        return (int) Math.round(fxImage.getWidth());
    }

    @Override
    public int height() {
        return (int) Math.round(fxImage.getHeight());
    }

    public javafx.scene.image.Image fxImage() {
        return fxImage;
    }
}
