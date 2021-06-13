/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.components.entity;

/**
 * The interface Rankable.
 */
public interface Rankable extends Comparable<Rankable> {

    /**
     * Gets point.
     *
     * @return the point
     */
    int getPoint();

    /**
     * Gets id.
     *
     * @return the id
     */
    String getID();
}
