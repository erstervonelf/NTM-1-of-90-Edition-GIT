package com.oneof90.render.block;

import com.oneof90.main.ResourceManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.client.model.obj.WavefrontObject;
import org.lwjgl.opengl.GL11;

public class RenderTileEntityCageLadder extends TileEntitySpecialRenderer {

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f) {
		// Bind texture - 1of90 steel beam texture
		bindTexture(new ResourceLocation("oneof90", "textures/models/cage_ladder.png"));

		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5, y, z + 0.5);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_CULL_FACE);

		// Get rotation from metadata
		int metadata = tileEntity.getBlockMetadata();
		float rotation = 0.0F;

		switch(metadata) {
			case 2: // North
				rotation = 0.0F;
				break;
			case 3: // South
				rotation = 180.0F;
				break;
			case 4: // West
				rotation = 90.0F;
				break;
			case 5: // East
				rotation = 270.0F;
				break;
		}

		GL11.glRotatef(rotation - 270.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);

		// Render the OBJ model if available (reuse the same model, just rotated)
		if (ResourceManager.cage_ladder != null) {
			((WavefrontObject) ResourceManager.cage_ladder).renderAll();
		}

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glPopMatrix();
	}

}
