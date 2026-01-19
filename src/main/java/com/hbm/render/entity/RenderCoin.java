package com.hbm.render.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

/**
 * Minimal stub renderer for coin entities.
 */
public class RenderCoin extends Render {

	@Override
	public void doRender(Entity entity, double x, double y, double z, float yaw, float interp) {
		// Minimal implementation: no rendering for now
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return null;
	}
}
