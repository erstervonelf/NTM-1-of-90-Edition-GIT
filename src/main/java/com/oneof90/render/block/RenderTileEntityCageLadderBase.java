package com.oneof90.render.block;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import com.hbm.main.ResourceManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.client.model.obj.WavefrontObject;
import org.lwjgl.opengl.GL11;

public class RenderTileEntityCageLadderBase extends TileEntitySpecialRenderer {

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f) {
		// Bind texture - 1of90 steel beam texture
		bindTexture(new ResourceLocation("oneof90", "textures/models/cage_ladder_base.png"));

		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5, y + 1, z + 0.5);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glRotatef(0.0f, 0.0f, 0.0f, 0.0f); // Rotate model 90° around X-axis to make it vertical

		// Render the OBJ model if available (reuse the same model, just rotated)
		if (ResourceManager.cage_ladder_base != null) {
			((WavefrontObject) ResourceManager.cage_ladder_base).renderAll();
		}

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glPopMatrix();
	}

}
