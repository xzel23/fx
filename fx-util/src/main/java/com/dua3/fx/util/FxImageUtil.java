package com.dua3.fx.util;

import com.dua3.utility.data.ImageUtil;
import javafx.scene.image.Image;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public final class FxImageUtil implements ImageUtil<Image> {
    
    @Override
    public Optional<FxImage> load(InputStream inputStream) throws IOException {
        return Optional.of(new FxImage(new Image(inputStream)));
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
    
}
