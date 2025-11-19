package com.oneof90.blocks;

import com.hbm.blocks.BlockBase;

import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.client.renderer.texture.IIconRegister;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Simple half-height marble slab block. Does not combine into double slabs.
 */
public class NTMMarbleSlab extends BlockBase {

    public NTMMarbleSlab(Material material) {
        super(material);
        // set default bounds to half height
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconregister) {
        this.blockIcon = iconregister.registerIcon("hbm:marble");
    }
}
