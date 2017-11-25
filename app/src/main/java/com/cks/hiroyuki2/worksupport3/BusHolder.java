package com.cks.hiroyuki2.worksupport3;

import org.jetbrains.annotations.Contract;

/**
 * EventBus用のsingleton
 */

public class BusHolder {
    private static final BusHolder ourInstance = new BusHolder();

    @Contract(pure = true)
    public static BusHolder getInstance() {
        return ourInstance;
    }

    private BusHolder() {}
}
