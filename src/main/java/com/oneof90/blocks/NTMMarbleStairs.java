package com.oneof90.blocks;

import com.hbm.blocks.generic.BlockGenericStairs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

/**
 * Marble stairs wrapper using the project's generic stairs class.
 */
public class NTMMarbleStairs extends BlockGenericStairs {

    public NTMMarbleStairs(Block modelBlock, int modelMeta) {
        super(modelBlock, modelMeta);
        // Use marble texture
        this.setBlockTextureName("hbm:marble");
    }
}
