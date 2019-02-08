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

import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class FxUtil {

    public static Font getFxFont(com.dua3.utility.text.Font font) {
        return new Font(font.getFamily(), font.getSizeInPoints());
    }

    public static float getTextWidth(String s, com.dua3.utility.text.Font font) {
        Font fxFont = getFxFont(font);
        Text text = new Text(s);
        text.setFont(fxFont);
        return (float) text.getBoundsInLocal().getWidth();
    }

    private FxUtil() {
        // utility class
    }

}