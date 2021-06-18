package com.dua3.fx.util;

import com.dua3.utility.data.ImageUtil;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Optional;

public final class FxImageUtil implements ImageUtil<Image> {
    
    @Override
    public FxImage load(InputStream inputStream) throws IOException {
        return new FxImage(new Image(inputStream));
    }

    @Override
    public Image convert(com.dua3.utility.data.Image img) {
        if (!(img instanceof FxImage)) {
            throw new UnsupportedOperationException("unsupported image class: "+img.getClass());
        }
        return ((FxImage) img).fxImage();
    }

    @Override
    public FxImage convert(Image img) {
        return new FxImage(img);
    }

    @Override
    public FxImage create(int w, int h, int[] argb) {
        WritableImage wr = new WritableImage(w, h);
        PixelWriter pw = wr.getPixelWriter();
        pw.setPixels(0, 0, w, h, PixelFormat.getIntArgbInstance(), argb, 0, w);
        return new FxImage(wr);
    }    
}