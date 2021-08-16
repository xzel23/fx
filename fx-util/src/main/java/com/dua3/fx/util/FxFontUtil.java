// Copyright 2019 Axel Howind
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//     http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.dua3.fx.util;

import com.dua3.utility.math.geometry.Dimension2f;
import com.dua3.utility.text.FontUtil;

import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class FxFontUtil implements FontUtil<Font> {

    @Override
    public Font convert(com.dua3.utility.text.Font font) {
        return FxUtil.convert(font);
    }

    @Override
    public Dimension2f getTextDimension(CharSequence s, com.dua3.utility.text.Font f) {
         var bounds = FxUtil.getTextBounds(s, f);
         return Dimension2f.of((float) bounds.getWidth(), (float) bounds.getHeight());
    }

    @Override
    public double getTextWidth(CharSequence s, com.dua3.utility.text.Font f) {
        return FxUtil.getTextWidth(s, f);
    }

    @Override
    public double getTextHeight(CharSequence s, com.dua3.utility.text.Font f) {
        return FxUtil.getTextHeight(s, f);
    }

    @Override
    public List<com.dua3.utility.text.Font> loadFonts(InputStream in) throws IOException {
        return FxUtil.loadFonts(in);
    }

    @Override
    public List<String> getFamilies(FontTypes types) {
        List<String> fonts = Font.getFamilies();

        boolean mono;
        switch (types) {
            case ALL:
                return fonts;
            case MONOSPACED:
                mono = true;
                break;
            case PROPORTIONAL:
                mono = false;
                break;
            default:
                throw new IllegalArgumentException("unknown value: "+types);
        }

        List<String> list = new ArrayList<>();

        Text thin = new Text("1 l");
        Text thick = new Text("M_W");
        for (String family: fonts) {
            Font font = Font.font(family, FontWeight.NORMAL, FontPosture.REGULAR, 14.0d);
            thin.setFont(font);
            thick.setFont(font);
            boolean monospaced = thin.getLayoutBounds().getWidth() == thick.getLayoutBounds().getWidth();
            if (mono == monospaced) {
                list.add(family);
            }
        }

        return list;
    }

    public FxFontUtil() {
    }

}
