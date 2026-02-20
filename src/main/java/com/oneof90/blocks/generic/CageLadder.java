package com.oneof90.blocks.generic;

import com.oneof90.blocks.InvBlock;
import com.oneof90.main.ModBlocks1of90;
import com.oneof90.tileentity.TileEntityCageLadder;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class CageLadder extends BlockContainer {

	// set general block properties
	public CageLadder(Material material){
		super(material);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.55F, 1.0F, 1.0F);
	}

	// use TESR
	@Override
	public int getRenderType() {
		return -1; // Use TESR (TileEntitySpecialRenderer)
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityCageLadder();
	}

	// set ladder behavior so entities can climb it
	@Override
	public boolean isLadder(IBlockAccess world, int x, int y, int z, EntityLivingBase entity) {
		return true;
	}

	// place invisible blocks around the ladder when it's placed
	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);

		if (!world.isRemote) {
			for (int dx = 0; dx <= 1; dx++) {
				for (int dz = -1; dz <= 1; dz++) {
					if (dx == 0 && dz == 0) {
						world.setBlock(dx,y,dz, ModBlocks1of90.cage_ladder, 0, 2);
					}

					int barrierX = x + dx;
					int barrierZ = z + dz;

					if (world.getBlock(barrierX, y, barrierZ).getMaterial() == Material.air) {
						world.setBlock(barrierX, y, barrierZ, ModBlocks1of90.inv_block, 0, 2);
					}

				}
			}
		}
	}

	// break multiblock structure when the main block is broken
	@Override
	public void breakBlock(World world, int x, int y, int z, net.minecraft.block.Block block, int meta) {
		if (!world.isRemote) {
			for (int dx = 0; dx <= 1; dx++) {
				for (int dz = -1; dz <= 1; dz++) {
					if (dx == 0 && dz == 0) continue;

					int barrierX = x + dx;
					int barrierZ = z + dz;
					net.minecraft.block.Block barrierBlock = world.getBlock(barrierX, y, barrierZ);

					if (barrierBlock instanceof InvBlock) {
						world.setBlockToAir(barrierX, y, barrierZ);
					}
				}
			}
		}

		super.breakBlock(world, x, y, z, block, meta);
	}

	// set collision box to only the ladder part
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return AxisAlignedBB.getBoundingBox(
			x, y, z,
			x + 0.25, y + 1, z + 1
		);
	}
}
