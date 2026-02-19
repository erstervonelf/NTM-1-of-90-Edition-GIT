package com.oneof90.blocks.generic;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class CageLadder extends Block {

	public CageLadder(Material material) {
		super(material);
	}

	//@Override
	//public int getRenderType() {
	//	return -1; // Use TESR (TileEntitySpecialRenderer)
	//}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	//@Override
	//public boolean renderAsNormalBlock() {
	//	return false;
	//}

}
