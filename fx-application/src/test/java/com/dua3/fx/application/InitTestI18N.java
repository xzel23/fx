package com.dua3.fx.application;

import com.dua3.utility.i18n.I18N;
import com.dua3.utility.i18n.I18NProvider;

import java.util.Locale;

public class InitTestI18N implements I18NProvider {
    private static final I18N I_18_N = I18N.create(InitTestI18N.class.getPackageName() + ".application", Locale.getDefault());

    @Override
    public I18N i18n() {
        return I_18_N;
    }
}
