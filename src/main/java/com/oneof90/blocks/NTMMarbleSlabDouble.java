package com.oneof90.blocks;

import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.client.renderer.texture.IIconRegister;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class NTMMarbleSlabDouble extends BlockSlab {
    public NTMMarbleSlabDouble() {
        super(true, Material.rock);
        this.setBlockName("ntm_marble_slab_double");
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setHardness(2.0F);
        this.setResistance(10.0F);
        this.useNeighborBrightness = true;
    }

    @Override
    public String func_150002_b(int meta) {
        return "marble";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconregister) {
        this.blockIcon = iconregister.registerIcon("hbm:marble");
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return this.blockIcon;
    }

    @Override
    protected ItemStack createStackedBlock(int meta) {
        return new ItemStack(com.oneof90.main.ModBlocks1of90.ntm_marble_slab_single, 2, meta & 7);
    }
}
