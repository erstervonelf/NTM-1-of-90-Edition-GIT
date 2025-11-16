package com.oneof90.render.machine;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import com.hbm.main.ResourceManager;
import com.oneof90.tileentity.machine.TileEntityExampleMultiblock;

/**
 * Example Multiblock Machine Renderer - Template for custom 3D rendering
 * 
 * This renderer handles the 3D model display for the multiblock machine.
 * Only renders at the core block position (metadata >= 12).
 */
public class RenderExampleMultiblock extends TileEntitySpecialRenderer {

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTicks) {
		
		if(!(tileEntity instanceof TileEntityExampleMultiblock)) {
			return;
		}
		
		GL11.glPushMatrix();
		
		// Translate to block position (center of block)
		GL11.glTranslated(x + 0.5, y, z + 0.5);
		
		// Enable lighting for proper shading
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_CULL_FACE);
		
		// Get the block metadata to determine rotation
		int metadata = tileEntity.getBlockMetadata();
		
		// Only render if this is the core block (metadata >= 12)
		if(metadata >= 12) {
			
			// Calculate rotation based on direction
			float rotation = 0.0F;
			
			switch(metadata - 10) { // Subtract 10 to get the direction ordinal
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
			
			// Apply rotation around Y axis (vertical)
			GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
			
			// Bind the texture for this machine
			bindTexture(new ResourceLocation("hbm", "textures/models/1of90_example_multiblock.png"));
			
			// Render the 3D model
			// Example: If you have a model loaded in ResourceManager
			// if(ResourceManager.example_multiblock_model != null) {
			//     ((WavefrontObject) ResourceManager.example_multiblock_model).renderAll();
			// }
			
			// Alternatively, render a simple cube as placeholder
			renderPlaceholderCube();
		}
		
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glPopMatrix();
	}
	
	/**
	 * Render a simple placeholder cube (for testing without a model)
	 */
	private void renderPlaceholderCube() {
		// This is just a placeholder - replace with actual model rendering
		GL11.glPushMatrix();
		
		// Scale to multiblock size (3x3x3)
		GL11.glScalef(1.5F, 1.5F, 1.5F);
		
		// You can use Minecraft's default cube rendering here
		// or render your custom model
		
		GL11.glPopMatrix();
	}
}
