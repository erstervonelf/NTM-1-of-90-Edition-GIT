package com.hbm.inventory.recipes;

import java.util.ArrayList;
import java.util.List;

import com.hbm.inventory.recipes.loader.GenericRecipe;
import com.hbm.util.BobMathUtil;
import com.hbm.util.i18n.I18nUtil;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.item.ItemStack;
import com.hbm.inventory.RecipesCommon.AStack;
import com.hbm.inventory.FluidStack;
import com.hbm.inventory.recipes.loader.GenericRecipes.IOutput;

public class FusionRecipe extends GenericRecipe {
	
	// minimum klystron energy to ignite the plasma
	public long ignitionTemp;
	// plasma output energy at full blast
	public long outputTemp;
	// neutron output energy at full blast
	public double neutronFlux;
	
	public float r = 1F;
	public float g = 0.2F;
	public float b = 0.6F;

	public FusionRecipe(String name) { super(name); }

	public FusionRecipe setInputEnergy(long ignitionTemp) { this.ignitionTemp = ignitionTemp; return this; }
	public FusionRecipe setOutputEnergy(long outputTemp) { this.outputTemp = outputTemp; return this; }
	public FusionRecipe setOutputFlux(double neutronFlux) { this.neutronFlux = neutronFlux; return this; }
	public FusionRecipe setRGB(float r, float g, float b) { this.r = r; this.g = g; this.b = b; return this; }

	public List<String> print() {
		List<String> list = new ArrayList();
		list.add(EnumChatFormatting.YELLOW + this.getLocalizedName());

		duration(list);
		power(list);
		list.add(EnumChatFormatting.LIGHT_PURPLE + I18nUtil.resolveKey("gui.recipe.fusionIn") + ": " + BobMathUtil.getShortNumber(ignitionTemp) + "KyU/t");
		list.add(EnumChatFormatting.LIGHT_PURPLE + I18nUtil.resolveKey("gui.recipe.fusionOut") + ": " + BobMathUtil.getShortNumber(outputTemp) + "TU/t");
		list.add(EnumChatFormatting.LIGHT_PURPLE + I18nUtil.resolveKey("gui.recipe.fusionFlux") + ": " + ((int)(neutronFlux * 10)) / 10D + " flux/t");
		input(list);
		output(list);

		return list;
	}
	
	protected void duration(List<String> list) {
		if(duration > 0) {
			double seconds = this.duration / 20D;
			list.add(EnumChatFormatting.RED + I18nUtil.resolveKey("gui.recipe.duration") + ": " + seconds + "s");
		}
	}
	
	protected void power(List<String> list) {
		if(power > 0) {
			list.add(EnumChatFormatting.RED + I18nUtil.resolveKey("gui.recipe.consumption") + ": " + BobMathUtil.getShortNumber(power) + "HE/t");
		}
	}
	
	protected void input(List<String> list) {
		list.add(EnumChatFormatting.BOLD + I18nUtil.resolveKey("gui.recipe.input") + ":");
		if(inputItem != null) for(AStack stack : inputItem) {
			ItemStack display = stack.extractForCyclingDisplay(20);
			if(display != null)
				list.add("  " + EnumChatFormatting.GRAY + display.stackSize + "x " + display.getDisplayName());
		}
		if(inputFluid != null) for(FluidStack fluid : inputFluid) {
			list.add("  " + EnumChatFormatting.BLUE + fluid.fill + "mB " + fluid.type.getLocalizedName());
		}
	}
	
	protected void output(List<String> list) {
		list.add(EnumChatFormatting.BOLD + I18nUtil.resolveKey("gui.recipe.output") + ":");
		if(outputItem != null) for(IOutput output : outputItem) {
			for(String line : output.getLabel()) list.add("  " + line);
		}
		if(outputFluid != null) for(FluidStack fluid : outputFluid) {
			list.add("  " + EnumChatFormatting.BLUE + fluid.fill + "mB " + fluid.type.getLocalizedName());
		}
	}
}

