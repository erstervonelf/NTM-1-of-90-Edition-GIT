package com.hbm.inventory.recipes;

import java.util.ArrayList;
import java.util.List;

import com.hbm.inventory.recipes.loader.GenericRecipe;
import com.hbm.util.BobMathUtil;
import com.hbm.util.i18n.I18nUtil;

import net.minecraft.util.EnumChatFormatting;

public class FusionRecipe extends GenericRecipe {
	
	// minimum klystron energy to ignite the plasma
	public long ignitionTemp;
	// plasma output energy at full blast
	public long outputTemp;

	public FusionRecipe(String name) { super(name); }

	public FusionRecipe setInputEnergy(long ignitionTemp) { this.ignitionTemp = ignitionTemp; return this; }
	public FusionRecipe setOutputEnergy(long outputTemp) { this.outputTemp = outputTemp; return this; }

	public List<String> print() {
		List<String> list = new ArrayList();
		list.add(EnumChatFormatting.YELLOW + this.getLocalizedName());

		duration(list);
		power(list);
		list.add(EnumChatFormatting.RED + I18nUtil.resolveKey("gui.recipe.fusionIn") + ": " + BobMathUtil.getShortNumber(ignitionTemp) + "KyU/t");
		list.add(EnumChatFormatting.RED + I18nUtil.resolveKey("gui.recipe.fusionOut") + ": " + BobMathUtil.getShortNumber(outputTemp) + "TU/t");
		input(list);
		output(list);

		return list;
	}
	
	private void duration(List<String> list) {
		if(duration > 0) {
			double seconds = this.duration / 20D;
			list.add(EnumChatFormatting.RED + I18nUtil.resolveKey("gui.recipe.duration") + ": " + seconds + "s");
		}
	}
	
	private void power(List<String> list) {
		if(power > 0) {
			list.add(EnumChatFormatting.RED + I18nUtil.resolveKey("gui.recipe.consumption") + ": " + BobMathUtil.getShortNumber(power) + "HE/t");
		}
	}
	
	private void input(List<String> list) {
		list.add(EnumChatFormatting.BOLD + I18nUtil.resolveKey("gui.recipe.input") + ":");
		if(inputItem != null) for(ItemStack stack : inputItem) {
			ItemStack display = stack.extractForCyclingDisplay(20);
			list.add("  " + EnumChatFormatting.GRAY + display.stackSize + "x " + display.getDisplayName());
		}
		if(inputFluid != null) for(FluidStack fluid : inputFluid) {
			list.add("  " + EnumChatFormatting.BLUE + fluid.fill + "mB " + fluid.type.getLocalizedName());
		}
	}
	
	private void output(List<String> list) {
		list.add(EnumChatFormatting.BOLD + I18nUtil.resolveKey("gui.recipe.output") + ":");
		if(outputItem != null) for(ItemStack output : outputItem) {
			for(String line : output.getLabel()) list.add("  " + line);
		}
		if(outputFluid != null) for(FluidStack fluid : outputFluid) {
			list.add("  " + EnumChatFormatting.BLUE + fluid.fill + "mB " + fluid.type.getLocalizedName());
		}
	}
}

