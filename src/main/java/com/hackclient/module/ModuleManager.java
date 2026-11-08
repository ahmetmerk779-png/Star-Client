package com.hackclient.module;

import com.hackclient.module.impl.FlyModule;
import com.hackclient.module.impl.SprintModule;
import com.hackclient.module.impl.StepModule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ModuleManager {
    private static final List<Module> MODULES = new ArrayList<>();

    private ModuleManager() {}

    public static void init() {
        MODULES.clear();
        MODULES.add(new SprintModule());
        MODULES.add(new FlyModule());
        MODULES.add(new StepModule());
    }

    public static List<Module> all() {
        return Collections.unmodifiableList(MODULES);
    }
}
