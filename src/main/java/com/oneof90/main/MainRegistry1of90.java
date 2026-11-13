package com.oneof90.main;

import com.oneof90.render.block.*;
import com.oneof90.tileentity.*;

import api.hbm.fluidmk2.forge.CompatFluidMK2Registry;

import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraftforge.client.MinecraftForgeClient;
import com.oneof90.render.item.ItemRenderNTMSteelBeam;
import com.oneof90.render.item.ItemRenderNTMSteelBeamVertical;
import com.oneof90.main.ResourceManager;
import net.minecraft.item.Item;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MainRegistry1of90 {

	/**
	 * Initialize all 1of90 content (called during mod pre-initialization)
	 */
	public static void init() {
		ModBlocks1of90.init();
		ModItems1of90.init();

		// Synchronize the fluidmk2 API with the ForgeFluid (oneof90) API so every fluidmk2 fluid
		// is automatically a Forge fluid and both APIs share one mapping.
		CompatFluidMK2Registry.initialize();

		api.ntm1of90.NTM1of90API.initialize();
	}

	public static void mainRegistry() {
		ModItems1of90.mainRegistry();
	}

	/**
	 * Register client-side renderers (called during client initialization)
	 */
	@SideOnly(Side.CLIENT)
	public static void initClient() {
		registerTileEntityRenderers();
		registerItemRenderers();
	}

	/**
	 * Register all tile entity special renderers
	 */
	@SideOnly(Side.CLIENT)
	private static void registerTileEntityRenderers() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityNTMSteelBeam.class, new RenderNTMSteelBeam());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityNTMSteelBeamVertical.class, new RenderNTMSteelBeamVertical());
	}

	/**
	 * Register item renderers for the NTM blocks so their item forms
	 * use the custom OBJ-based item renderers.
	 */
	@SideOnly(Side.CLIENT)
	private static void registerItemRenderers() {
		// Horizontal beam item
		Item itemBeam = Item.getItemFromBlock(ModBlocks1of90.ntm_steel_beam);
		if(itemBeam != null)
			MinecraftForgeClient.registerItemRenderer(itemBeam, new ItemRenderNTMSteelBeam());

		// Vertical beam item
		Item itemBeamV = Item.getItemFromBlock(ModBlocks1of90.ntm_steel_beam_vertical);
		if(itemBeamV != null)
			MinecraftForgeClient.registerItemRenderer(itemBeamV, new ItemRenderNTMSteelBeamVertical());
	}
}
