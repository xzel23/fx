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

package com.dua3.fx.util.imp;

import com.dua3.fx.util.FxUtil;
import com.dua3.utility.text.FontUtil;

import javafx.scene.text.Font;

public class FxFontUtil implements FontUtil<Font> {

    @Override
    public Font convert(com.dua3.utility.text.Font font) {
        return FxUtil.convert(font);
    }

    @Override
    public com.dua3.utility.text.FontUtil.Bounds getTextBounds(String s, com.dua3.utility.text.Font f) {
         var bounds = FxUtil.getTextBounds(s, f);
         return new com.dua3.utility.text.FontUtil.Bounds(bounds.getWidth(), bounds.getHeight());
    }

    @Override
    public double getTextWidth(String s, com.dua3.utility.text.Font f) {
        return FxUtil.getTextWidth(s, f);
    }

    @Override
    public double getTextHeight(String s, com.dua3.utility.text.Font f) {
        return FxUtil.getTextHeight(s, f);
    }

    public FxFontUtil() {
    }

}
