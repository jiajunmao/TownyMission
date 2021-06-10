/*
 * Copyright (c) 2021 NatureCraft. All Rights Reserved. You may not distribute, decompile, and modify the plugin consent without explicit written consent from NatureCraft devs.
 */

package world.naturecraft.townymission.components.gui;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GlowEnchant extends Enchantment {

    public GlowEnchant(@NotNull NamespacedKey key) {
        super(key);
    }

    @Override
    public boolean canEnchantItem(ItemStack arg0) {
        return false;
    }

    @Override
    public boolean conflictsWith(Enchantment arg0) {
        return false;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return null;
    }

    /**
     * Checks if this enchantment is a treasure enchantment.
     * <br>
     * Treasure enchantments can only be received via looting, trading, or
     * fishing.
     *
     * @return true if the enchantment is a treasure enchantment
     */
    @Override
    public boolean isTreasure() {
        return false;
    }

    /**
     * Checks if this enchantment is a cursed enchantment
     * <br>
     * Cursed enchantments are found the same way treasure enchantments are
     *
     * @return true if the enchantment is cursed
     * @deprecated cursed enchantments are no longer special. Will return true
     * only for {@link Enchantment#BINDING_CURSE} and
     * {@link Enchantment#VANISHING_CURSE}.
     */
    @Override
    public boolean isCursed() {
        return false;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }
}
