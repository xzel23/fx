package com.dua3.fx.application;

import com.dua3.utility.i18n.I18N;

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
        super(I18N.getInstance(), null);
    }

}