package com.hbm.particle;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

//gracefully copy-pasted from xR
@SideOnly(Side.CLIENT)
public class ParticleRBMKMush extends EntityFX {

	private static final ResourceLocation texture = new ResourceLocation(com.hbm.lib.RefStrings.MODID + ":textures/particle/rbmk_mush.png");
	private TextureManager theRenderEngine;
	private int age;
	private int maxAge;
	private float baseScale;
	private float alpha;

	public ParticleRBMKMush(TextureManager p_i1213_1_, World p_i1218_1_, double p_i1218_2_, double p_i1218_4_, double p_i1218_6_, float scale) {
		super(p_i1218_1_, p_i1218_2_, p_i1218_4_, p_i1218_6_);
		theRenderEngine = p_i1213_1_;
		maxAge = (int) Math.max(20, 50 * scale);

		this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
		this.baseScale = scale;
		this.particleScale = scale;
		this.alpha = 1.0F;

		// give a bit of outward velocity and upward lift for mushroom cloud
		this.motionX = (worldObj.rand.nextDouble() - 0.5D) * 0.15D * scale;
		this.motionY = 0.05D + worldObj.rand.nextDouble() * 0.05D * scale;
		this.motionZ = (worldObj.rand.nextDouble() - 0.5D) * 0.15D * scale;
	}

	public ParticleRBMKMush(TextureManager p_i1213_1_, World p_i1218_1_, double p_i1218_2_, double p_i1218_4_, double p_i1218_6_, float red, float green, float blue, float scale) {
		super(p_i1218_1_, p_i1218_2_, p_i1218_4_, p_i1218_6_);
		theRenderEngine = p_i1213_1_;
		maxAge = (int) Math.max(20, 50 * scale);

		this.particleRed = red;
		this.particleGreen = green;
		this.particleBlue = blue;

		this.baseScale = scale;
		this.particleScale = scale;
		this.alpha = 1.0F;

		this.motionX = (worldObj.rand.nextDouble() - 0.5D) * 0.15D * scale;
		this.motionY = 0.05D + worldObj.rand.nextDouble() * 0.05D * scale;
		this.motionZ = (worldObj.rand.nextDouble() - 0.5D) * 0.15D * scale;
	}

	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		++this.age;

		if(this.age == this.maxAge) {
			this.setDead();
			return;
		}

		// simple motion integration with slight drag and upward acceleration
		this.motionX *= 0.98D;
		this.motionY += 0.0025D; // gentle rise over time
		this.motionY *= 0.995D;
		this.motionZ *= 0.98D;

		this.posX += this.motionX;
		this.posY += this.motionY;
		this.posZ += this.motionZ;

		// grow over lifetime for a puffing effect
		float life = this.age / (float) this.maxAge;
		this.particleScale = this.baseScale * (1.0F + life * 2.0F);

		// fade out smoothly
		this.alpha = 1.0F - life;
	}

	public int getFXLayer() {
		return 3;
	}

	public void renderParticle(Tessellator tessellator, float p_70539_2_, float x, float y, float z, float sx, float sz) {

		this.theRenderEngine.bindTexture(texture);

		int segs = 30;

		// the size of one frame
		double frame = 1D / (double) segs;
		// how many frames we're in
		int prog = age * segs / maxAge;

		GL11.glColor4f(this.particleRed, this.particleGreen, this.particleBlue, this.alpha);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0);
		GL11.glDepthMask(false);
		RenderHelper.disableStandardItemLighting();

		boolean fog = GL11.glIsEnabled(GL11.GL_FOG);
		if(fog) GL11.glDisable(GL11.GL_FOG);

		tessellator.startDrawingQuads();

		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		tessellator.setBrightness(240);

		float scale = this.particleScale;
		float pX = (float) ((this.prevPosX + (this.posX - this.prevPosX) * (double) p_70539_2_ - interpPosX));
		float pY = (float) ((this.prevPosY + (this.posY - this.prevPosY) * (double) p_70539_2_ - interpPosY)) + particleScale;
		float pZ = (float) ((this.prevPosZ + (this.posZ - this.prevPosZ) * (double) p_70539_2_ - interpPosZ));

		tessellator.addVertexWithUV((double) (pX - x * scale - sx * scale), (double) (pY - y * scale), (double) (pZ - z * scale - sz * scale), 1, (prog + 1) * frame);
		tessellator.addVertexWithUV((double) (pX - x * scale + sx * scale), (double) (pY + y * scale), (double) (pZ - z * scale + sz * scale), 1, prog * frame);
		tessellator.addVertexWithUV((double) (pX + x * scale + sx * scale), (double) (pY + y * scale), (double) (pZ + z * scale + sz * scale), 0, prog * frame);
		tessellator.addVertexWithUV((double) (pX + x * scale - sx * scale), (double) (pY - y * scale), (double) (pZ + z * scale - sz * scale), 0, (prog + 1) * frame);
		tessellator.draw();

		if(fog) GL11.glEnable(GL11.GL_FOG);
		GL11.glPolygonOffset(0.0F, 0.0F);
		GL11.glEnable(GL11.GL_LIGHTING);
	}
}
