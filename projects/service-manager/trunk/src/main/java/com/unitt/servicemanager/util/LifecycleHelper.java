package com.unitt.servicemanager.util;

import com.unitt.commons.foundation.lifecycle.Destructable;
import com.unitt.commons.foundation.lifecycle.Initializable;

public class LifecycleHelper {
    public static void initialize(Object aToBeInitialized) {
        if (aToBeInitialized != null && aToBeInitialized instanceof Initializable) {
            Initializable initializable = (Initializable) aToBeInitialized;
            if (!initializable.isInitialized()) {
                initializable.initialize();
            }
        }
    }

    public static void destroy(Object aToBeDestroyed) {
        if (aToBeDestroyed != null && aToBeDestroyed instanceof Destructable) {
            ((Destructable) aToBeDestroyed).destroy();
        }
    }
}
