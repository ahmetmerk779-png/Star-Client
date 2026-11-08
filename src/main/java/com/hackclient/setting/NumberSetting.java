package com.hackclient.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class NumberSetting extends Setting {
    private double value;
    private final double min;
    private final double max;
    private final double step;

    public NumberSetting(String name, String description, double value, double min, double max, double step) {
        super(name, description);
        this.min = min;
        this.max = max;
        this.step = step;
        set(value);
    }

    public double get() { return value; }
    public float getFloat() { return (float) value; }
    public double getMin() { return min; }
    public double getMax() { return max; }
    public double getStep() { return step; }

    public void set(double v) {
        v = Math.max(min, Math.min(max, v));
        if (step > 0) v = Math.round((v - min) / step) * step + min;
        this.value = Math.max(min, Math.min(max, v));
    }

    /** t: 0..1 */
    public void setFromRatio(double t) {
        set(min + (max - min) * Math.max(0, Math.min(1, t)));
    }

    public double ratio() {
        return max == min ? 0 : (value - min) / (max - min);
    }

    @Override
    public JsonElement toJson() { return new JsonPrimitive(value); }

    @Override
    public void fromJson(JsonElement element) {
        try { set(element.getAsDouble()); } catch (Exception ignored) {}
    }
}
