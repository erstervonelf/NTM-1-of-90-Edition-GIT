package com.hbm.items.weapon.sedna.mods;

import com.hbm.util.EnchantmentUtil;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;

public class WeaponModDrillFortune extends WeaponModBase {
	
	int addFortune = 0;
	
	public WeaponModDrillFortune(int id, String slot, int fortune) {
		super(id, slot);
		this.setPriority(PRIORITY_ADDITIVE);
		this.addFortune = fortune;
	}

	@Override
	public <T> T eval(T base, ItemStack gun, String key, Object parent) {
		return base;
	}

	@Override
	public void onInstall(ItemStack gun, ItemStack mod, int index) {
		// Fortune enchantment handling is not available in this version
		// This mod requires API updates
	}
	
	@Override
	public void onUninstall(ItemStack gun, ItemStack mod, int index) {
		// Fortune enchantment handling is not available in this version
		// This mod requires API updates
	}
}
