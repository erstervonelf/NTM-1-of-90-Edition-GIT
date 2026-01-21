package com.hbm.items;

import com.hbm.render.anim.BusAnimation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface IAnimatedItem<T> {

	@SideOnly(Side.CLIENT)
	public BusAnimation getAnimation(NBTTagCompound data, ItemStack stack);

	@SideOnly(Side.CLIENT)
	public boolean shouldPlayerModelAim(ItemStack stack);
}
