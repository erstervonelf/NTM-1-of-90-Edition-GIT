package com.oneof90.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.util.IIcon;
import net.minecraft.client.renderer.texture.IIconRegister;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Marble tile slab block (low): supports metadata 0..15 representing heights 1..16 pixels
 */
public class NTMMarbleTileSlabLow extends Block {

    public NTMMarbleTileSlabLow() {
        super(Material.rock);
        this.setBlockName("ntm_marble_tile_slab_low");
        this.setHardness(1.0F);
        this.setResistance(5.0F);
        this.setBlockTextureName("hbm:marble");
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z) & 15;
        int pixels = Math.max(1, Math.min(16, meta + 1)); // 1..16
        float height = pixels / 16.0F;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, height, 1.0F);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        this.setBlockBoundsBasedOnState(world, x, y, z);
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, net.minecraft.item.ItemStack stack) {
        // preserve metadata from item if provided
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        this.blockIcon = iconRegister.registerIcon("hbm:marble");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return this.blockIcon;
    }

    @Override
    public boolean isOpaqueCube() { return false; }

    @Override
    public boolean renderAsNormalBlock() { return false; }
}
