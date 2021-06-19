/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.core.utils;

import world.naturecraft.townymission.bukkit.utils.BukkitUtil;
import world.naturecraft.townymission.core.services.ChatService;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Multiline builder.
 */
public class MultilineBuilder {

    private final String header;
    private final List<String> lines;
    private final String footer;

    /**
     * Instantiates a new Multiline builder.
     *
     * @param header the header
     */
    public MultilineBuilder(String header) {
        this.header = header;
        lines = new ArrayList<>();
        footer = "&7------------------";
    }

    /**
     * Add.
     *
     * @param line the line
     */
    public void add(String line) {
        lines.add(line);
    }

    public String toString() {
        String finalDisplay = header + "\n";
        for (String str : lines) {
            finalDisplay += str + "\n";
        }
        finalDisplay += footer;
        return ChatService.getInstance().translateColor(finalDisplay);
    }
}
