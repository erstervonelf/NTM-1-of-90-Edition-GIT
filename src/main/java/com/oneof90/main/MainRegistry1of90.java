package com.oneof90.main;

import com.oneof90.render.RenderNTMSteelBeam;
import com.oneof90.render.RenderNTMSteelBeamVertical;
import com.oneof90.tileentity.TileEntityNTMSteelBeam;
import com.oneof90.tileentity.TileEntityNTMSteelBeamVertical;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * 1of90 Edition Main Registry
 * 
 * Central initialization point for the 1of90 mod extension.
 * This allows adding new content independently from HBM's code.
 */
public class MainRegistry1of90 {

	/**
	 * Initialize all 1of90 content (called during mod pre-initialization)
	 */
	public static void init() {
		ModBlocks1of90.init();
	}

	/**
	 * Register client-side renderers (called during client initialization)
	 */
	@SideOnly(Side.CLIENT)
	public static void initClient() {
		registerTileEntityRenderers();
	}

	/**
	 * Register all tile entity special renderers
	 */
	@SideOnly(Side.CLIENT)
	private static void registerTileEntityRenderers() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityNTMSteelBeam.class, new RenderNTMSteelBeam());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityNTMSteelBeamVertical.class, new RenderNTMSteelBeamVertical());
	}
}
