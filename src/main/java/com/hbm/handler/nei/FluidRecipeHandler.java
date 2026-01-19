package com.hbm.handler.nei;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.List;

import com.hbm.handler.imc.ICompatNHNEI;
import com.hbm.inventory.recipes.MachineRecipes;
import com.hbm.items.ModItems;
import com.hbm.lib.RefStrings;
import com.hbm.util.Tuple.Triplet;

import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

public class FluidRecipeHandler extends TemplateRecipeHandler implements ICompatNHNEI {
	@Override
	public ItemStack[] getMachinesForRecipe() {
		return new ItemStack[]{
				new ItemStack(ModItems.fluid_barrel_empty),
				new ItemStack(ModItems.fluid_tank_empty),
				new ItemStack(ModItems.fluid_tank_lead_empty),
				new ItemStack(ModItems.canister_empty),
				new ItemStack(ModItems.gas_empty),
				new ItemStack(ModItems.cell_empty),
				new ItemStack(ModItems.disperser_canister_empty),
				new ItemStack(ModItems.glyphid_gland_empty)};
	}
	@Override
	public String getRecipeID() {
		return "fluidcons";
	}

    public class SmeltingSet extends TemplateRecipeHandler.CachedRecipe
    {
    	PositionedStack input;
        PositionedStack result;
    	
        public SmeltingSet(ItemStack input, ItemStack result) {
        	input.stackSize = 1;
            this.input = new PositionedStack(input, 83 - 27 - 18 + 1, 5 + 18 + 1);
            this.result = new PositionedStack(result, 83 + 27 + 18 + 1 - 18, 5 + 18 + 1);
        }

        @Override
		public List<PositionedStack> getIngredients() {
            return getCycledIngredients(cycleticks / 48, Arrays.asList(new PositionedStack[] {input}));
        }

        @Override
		public PositionedStack getResult() {
            return result;
        }
    }
    
	@Override
	public String getRecipeName() {
		return "Fluid Containers";
	}

	@Override
	public String getGuiTexture() {
		return RefStrings.MODID + ":textures/gui/nei/gui_nei_fluid.png";
	}
	
	@Override
	public void loadCraftingRecipes(String outputId, Object... results) {
		if ((outputId.equals("fluidcons")) && getClass() == FluidRecipeHandler.class) {
			List<Triplet<ItemStack, ItemStack, ItemStack>> recipes = MachineRecipes.instance().getFluidContainers();
			for (Triplet<ItemStack, ItemStack, ItemStack> recipe : recipes) {
				this.arecipes.add(new SmeltingSet(recipe.getY(), recipe.getZ()));
			}
		} else {
			super.loadCraftingRecipes(outputId, results);
		}
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		List<Triplet<ItemStack, ItemStack, ItemStack>> recipes = MachineRecipes.instance().getFluidContainers();
		for (Triplet<ItemStack, ItemStack, ItemStack> recipe : recipes) {
			if (NEIServerUtils.areStacksSameType(recipe.getZ(), result) || compareFluidStacks(result, recipe.getY()))
				this.arecipes.add(new SmeltingSet(recipe.getY(), recipe.getZ()));
		}
	}

	@Override
	public void loadUsageRecipes(String inputId, Object... ingredients) {
		if ((inputId.equals("fluidcons")) && getClass() == FluidRecipeHandler.class) {
			loadCraftingRecipes("fluidcons", new Object[0]);
		} else {
			super.loadUsageRecipes(inputId, ingredients);
		}
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		List<Triplet<ItemStack, ItemStack, ItemStack>> recipes = MachineRecipes.instance().getFluidContainers();
		for (Triplet<ItemStack, ItemStack, ItemStack> recipe : recipes) {
			if (NEIServerUtils.areStacksSameType(recipe.getZ(), ingredient) || compareFluidStacks(ingredient, recipe.getY()))
				this.arecipes.add(new SmeltingSet(recipe.getY(), recipe.getZ()));				
		}
	}
	
	private boolean compareFluidStacks(ItemStack sta1, ItemStack sta2) {
		return sta1.getItem() == sta2.getItem() && sta1.getItemDamage() == sta2.getItemDamage();
	}

    @Override
    public Class<? extends GuiContainer> getGuiClass() {
        //return GUIMachineShredder.class;
    	return null;
    }
    
    @Override
    public void loadTransferRects() {
        
        transferRects.add(new RecipeTransferRect(new Rectangle(74 + 6 - 18, 23, 42, 18), "fluidcons"));
        RecipeTransferRectHandler.registerRectsToGuis(getRecipeTransferRectGuis(), transferRects);
    }
}
