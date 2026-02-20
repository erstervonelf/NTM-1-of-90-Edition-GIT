package com.oneof90.main;

import com.hbm.lib.RefStrings;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

public class ResourceManager {
	//1of90
	public static final IModelCustom ntm_steel_beam = AdvancedModelLoader.loadModel(new ResourceLocation("oneof90","models/blocks/generic/ntm_steel_beam.obj"));
	public static final IModelCustom cage_ladder = AdvancedModelLoader.loadModel(new ResourceLocation( "oneof90","models/blocks/generic/cage_ladder.obj"));
	public static final IModelCustom cage_ladder_base = AdvancedModelLoader.loadModel(new ResourceLocation("oneof90","models/blocks/generic/cage_ladder_base.obj"));
	public static final IModelCustom dieselgen = AdvancedModelLoader.loadModel(new ResourceLocation("oneof90","models/blocks/machines/dieselgen.obj"));
}
