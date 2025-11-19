package com.oneof90.main;

import com.hbm.main.MainRegistry;
import com.oneof90.blocks.NTMSteelBeam;
import com.oneof90.blocks.NTMSteelBeamVertical;
import com.oneof90.tileentity.TileEntityNTMSteelBeam;
import com.oneof90.tileentity.TileEntityNTMSteelBeamVertical;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

/**
 * 1of90 Edition Block Registry
 * 
 * Separate block registration system to allow adding new content
 * without modifying HBM's original code structure.
 */
public class ModBlocks1of90 {

	// Steel Beams
	public static Block ntm_steel_beam;
	public static Block ntm_steel_beam_vertical;
	// Marble
	public static Block ntm_marble;
	// Marble Slab variants & Stairs
	public static Block ntm_marble_slab_single;
	public static Block ntm_marble_tile_slab_low;
	public static Block ntm_marble_tile_slab_high;
	public static Block ntm_marble_stairs;

	/**
	 * Initialize all 1of90 blocks
	 */
	public static void init() {
		
		// Steel Beams
		ntm_steel_beam = new NTMSteelBeam(Material.iron)
			.setBlockName("ntm_steel_beam")
			.setCreativeTab(MainRegistry.blockTab)
			.setHardness(5.0F)
			.setResistance(200.0F)
			.setBlockTextureName("hbm:ntm_steel_beam");
		
		ntm_steel_beam_vertical = new NTMSteelBeamVertical(Material.iron)
			.setBlockName("ntm_steel_beam_vertical")
			.setCreativeTab(MainRegistry.blockTab)
			.setHardness(5.0F)
			.setResistance(200.0F)
			.setBlockTextureName("hbm:ntm_steel_beam_vertical");

		// Marble
		ntm_marble = new com.oneof90.blocks.NTMMarble(Material.rock)
			.setBlockName("ntm_marble")
			.setCreativeTab(MainRegistry.blockTab)
			.setHardness(1.5F)
			.setResistance(10.0F)
			.setBlockTextureName("hbm:marble");


		// Marble Slabs (single and tile slabs)
		ntm_marble_slab_single = new com.oneof90.blocks.NTMMarbleSlabSingle();
		ntm_marble_tile_slab_low = new com.oneof90.blocks.NTMMarbleTileSlabLow();

		// Marble Stairs
		ntm_marble_stairs = new com.oneof90.blocks.NTMMarbleStairs(ntm_marble, 0)
			.setBlockName("ntm_marble_stairs")
			.setCreativeTab(MainRegistry.blockTab)
			.setHardness(1.5F)
			.setResistance(10.0F);
		
		registerBlocks();
		registerTileEntities();
	}

	/**
	 * Register all blocks with the game registry
	 */
	private static void registerBlocks() {
		GameRegistry.registerBlock(ntm_steel_beam, ntm_steel_beam.getUnlocalizedName());
		GameRegistry.registerBlock(ntm_steel_beam_vertical, ntm_steel_beam_vertical.getUnlocalizedName());
		GameRegistry.registerBlock(ntm_marble, ntm_marble.getUnlocalizedName());
		GameRegistry.registerBlock(ntm_marble_slab_single, ntm_marble_slab_single.getUnlocalizedName());
		GameRegistry.registerBlock(ntm_marble_tile_slab_low, ntm_marble_tile_slab_low.getUnlocalizedName());
		GameRegistry.registerBlock(ntm_marble_stairs, ntm_marble_stairs.getUnlocalizedName());
		// marble saw not registered
	}

	/**
	 * Register all tile entities
	 */
	private static void registerTileEntities() {
		GameRegistry.registerTileEntity(TileEntityNTMSteelBeam.class, "tileentity_ntmsteelbeam");
		GameRegistry.registerTileEntity(TileEntityNTMSteelBeamVertical.class, "tileentity_ntmsteelbeamvertical");
		// marble saw tile entity removed
	}
}
