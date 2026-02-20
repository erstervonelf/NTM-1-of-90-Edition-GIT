package com.oneof90.main;

import com.hbm.main.MainRegistry;
import com.oneof90.blocks.InvBlock;
import com.oneof90.blocks.generic.CageLadder;
import com.oneof90.blocks.generic.CageLadderBase;
import com.oneof90.blocks.generic.NTMSteelBeam;
import com.oneof90.blocks.generic.NTMSteelBeamVertical;
import com.oneof90.tileentity.TileEntityCageLadder;
import com.oneof90.tileentity.TileEntityCageLadderBase;
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

	// Define Block instances for 1of90 content
	public static Block ntm_steel_beam;
	public static Block ntm_steel_beam_vertical;
	public static Block cage_ladder;
	public static Block cage_ladder_base;
	public static Block inv_block;

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
		cage_ladder = new CageLadder(Material.iron)
			.setBlockName("cage_ladder")
			.setCreativeTab(MainRegistry.blockTab)
			.setHardness(2.0F)
			.setResistance(10.0F)
			.setBlockTextureName("hbm:cage_ladder");
		cage_ladder_base = new CageLadderBase(Material.iron)
			.setBlockName("cage_ladder_base")
			.setCreativeTab(MainRegistry.blockTab)
			.setHardness(2.0F)
			.setResistance(10.0F)
			.setBlockTextureName("hbm:cage_ladder_base");
		inv_block = new InvBlock(Material.air)
			.setBlockName("inv_block")
			.setBlockTextureName("hbm:inv_block");

		registerBlocks();
		registerTileEntities();
	}

	/**
	 * Register all blocks with the game registry
	 */
	private static void registerBlocks() {
		GameRegistry.registerBlock(ntm_steel_beam, ntm_steel_beam.getUnlocalizedName());
		GameRegistry.registerBlock(ntm_steel_beam_vertical, ntm_steel_beam_vertical.getUnlocalizedName());
		GameRegistry.registerBlock(cage_ladder, cage_ladder.getUnlocalizedName());
		GameRegistry.registerBlock(cage_ladder_base, cage_ladder_base.getUnlocalizedName());
		GameRegistry.registerBlock(inv_block, inv_block.getUnlocalizedName());
	}

	/**
	 * Register all tile entities
	 */
	private static void registerTileEntities() {
		GameRegistry.registerTileEntity(TileEntityNTMSteelBeam.class, "tileentity_ntmsteelbeam");
		GameRegistry.registerTileEntity(TileEntityNTMSteelBeamVertical.class, "tileentity_ntmsteelbeamvertical");
		GameRegistry.registerTileEntity(TileEntityCageLadder.class, "tileentity_cageladder");
		GameRegistry.registerTileEntity(TileEntityCageLadderBase.class, "tileentity_cageladderbase");
	}
}
