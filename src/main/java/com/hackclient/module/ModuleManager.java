package com.hackclient.module;

import com.hackclient.module.impl.FlyModule;
import com.hackclient.module.impl.HudModule;
import com.hackclient.module.impl.KillAuraModule;
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
        MODULES.add(new KillAuraModule());
        MODULES.add(new SprintModule());
        MODULES.add(new FlyModule());
        MODULES.add(new StepModule());
        MODULES.add(new HudModule());
        // HUD varsayilan olarak acik
        get(HudModule.class).setEnabled(true);
    }

    public static List<Module> all() {
        return Collections.unmodifiableList(MODULES);
    }

    public static List<Module> inCategory(Category category) {
        List<Module> out = new ArrayList<>();
        for (Module m : MODULES) {
            if (m.getCategory() == category) out.add(m);
        }
        return out;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Module> T get(Class<T> type) {
        for (Module m : MODULES) {
            if (type.isInstance(m)) return (T) m;
        }
        throw new IllegalStateException("Module not found: " + type.getSimpleName());
    }
}
