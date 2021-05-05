/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.utils;

import java.util.ArrayList;
import java.util.List;

public class MultilineBuilder {

    private String header;
    private List<String> lines;
    private String footer;

    public MultilineBuilder(String header) {
        this.header = header;
        lines = new ArrayList<>();
        footer = "&e------------------";
    }

    public void add(String line) {
        lines.add(line);
    }

    public String toString() {
        String finalDisplay = header + "\n";
        for (String str : lines) {
            finalDisplay += str + "\n";
        }
        finalDisplay += footer;
        return Util.translateColor(finalDisplay);
    }
}
