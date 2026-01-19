package com.hbm.render.tileentity;

import org.lwjgl.opengl.GL11;

import com.hbm.blocks.ModBlocks;
import com.hbm.render.item.ItemRenderBase;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;

public class RenderAssemblyFactory extends TileEntitySpecialRenderer implements IItemRendererProvider {

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float interp) {
        // Minimal no-op renderer to satisfy binding; real rendering exists upstream
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y, z + 0.5);
        GL11.glPopMatrix();
    }

    @Override
    public Item getItemForRenderer() {
        return Item.getItemFromBlock(ModBlocks.machine_assembly_factory);
    }

    @Override
    public IItemRenderer getRenderer() {
        return new ItemRenderBase() {
            public void renderInventory() {
                GL11.glTranslated(0, -1.5, 0);
                GL11.glScaled(3, 3, 3);
            }
            public void renderCommonWithStack(ItemStack item) {
                // Intentionally left blank; upstream model rendering not included here
            }
        };
    }
}
