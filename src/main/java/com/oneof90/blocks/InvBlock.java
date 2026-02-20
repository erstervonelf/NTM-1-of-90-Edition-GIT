package com.oneof90.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

public class InvBlock extends Block {

	// Constructor for the invisible block
	public InvBlock(Material material) {
		super(material);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1F, 1F, 1F);
		this.setBlockUnbreakable();
		this.setResistance(6000000.0F);
	}

	// set render type to invisible
	@Override
	public int getRenderType() {
		return -1; // Unsichtbar
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	// cannot be placed manually
	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		return false; // Kann nicht manuell platziert werden
	}
}
