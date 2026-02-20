package com.oneof90.blocks.generic;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

/**
 * Einfacher Test-Block für 1of90-Edition.
 * Wird in ModBlocks1of90 als "test1of90" registriert.
 */
public class Test extends Block {

    public Test(Material material) {
        super(material);
        // Basis-Eigenschaften, können in ModBlocks1of90 überschrieben werden
        this.setHardness(1.5F);
        this.setResistance(10.0F);
    }
}

