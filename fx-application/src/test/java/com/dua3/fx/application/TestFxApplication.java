package com.dua3.fx.application;

import com.dua3.utility.i18n.I18N;
import javafx.scene.Parent;

import java.util.Locale;

/**
 * A test implementation of FxApplication that handles the case where the I18N instance returns a null locale.
 * This class is used for testing purposes only.
 *
 * @param <A> the application class
 * @param <C> the controller class
 */
public abstract class TestFxApplication<A extends TestFxApplication<A, C>, C extends FxController<A, C, ?>> extends FxApplication<A, C> {

    /**
     * Constructor.
     */
    protected TestFxApplication() {
        super(I18N.getInstance());
    }

    /**
     * Override the mergeBundle method to handle the case where the I18N instance returns a null locale.
     * This is needed for testing purposes.
     */
    @Override
    public void init() {
        // Use a default locale if the I18N instance returns a null locale
        Locale locale = i18n.getLocale();
        if (locale == null) {
            locale = Locale.ENGLISH;
        }
        
        // Merge the bundle with the locale
        i18n.mergeBundle(FxApplication.class.getPackageName() + ".application", locale);
    }
}