package api.hbm.fluidmk2.forge;

import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.lib.RefStrings;

import api.ntm1of90.compat.fluid.render.ColoredForgeFluid;
import api.ntm1of90.compat.fluid.registry.FluidMappingRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Forge-Fluid-compatibility hook for the fluidmk2 API.
 *
 * This is the piece that synchronizes the fluidmk2 API with the ForgeFluid (oneof90) API.
 * On initialization it:
 *  - registers every HBM FluidType as a Forge Fluid (ColoredForgeFluid) in FluidRegistry,
 *  - keeps a FluidType <-> Forge-Fluid mapping in FluidMappingRegistry so the NTM compat layer
 *    and the fluidmk2 API share one synchronized mapping,
 *  - stitches fluid textures on TextureStitchEvent.Pre using the same texture path that
 *    fluidmk2 already defines (textures/gui/fluids/<name>.png).
 *
 * This is intended to be called once from MainRegistry1of90.init() (or from the fluidmk2
 * initialization path) so that everything that uses fluidmk2 is automatically Forge-fluid-compatible.
 */
public class CompatFluidMK2Registry {

    private static boolean initialized = false;

    /** access to the synchronized mapping that both fluidmk2 and NTM compat share */
    public static FluidMappingRegistry mapping() {
        return FluidMappingRegistry.INSTANCE;
    }

    // backwards compatibility: existing NTM code may still call these static helpers
    public static FluidType getHbmFluidType(net.minecraftforge.fluids.Fluid fluid) {
        return FluidMappingRegistry.getHbmFluidType(fluid);
    }

    public static net.minecraftforge.fluids.Fluid getForgeFluid(com.hbm.inventory.fluid.FluidType type) {
        return FluidMappingRegistry.getForgeFluid(type);
    }

    /**
     * Initialize the fluidmk2 <-> Forge fluid synchronization.
     * Safe to call multiple times; does nothing after the first successful init.
     */
    public static void initialize() {
        if (initialized) {
            return;
        }
        if (cpw.mods.fml.common.FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new CompatFluidMK2Registry());
        }

        FluidMappingRegistry.INSTANCE.s_init();

        if (cpw.mods.fml.common.FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            System.out.println("[fluidmk2] ForgeFluid synchronization initialized: "
                + FluidMappingRegistry.INSTANCE.getForgeFluidCount() + " Forge fluids mapped");
        }
        initialized = true;
    }

    /**
     * Re-run the registration in case fluids were reloaded (e.g. config reload).
     * The Forge FluidRegistry does not support truly reloading fluids at runtime, so this is
     * primarily useful for keeping the mapping correct after a fluid list refresh.
     */
    public static void reload() {
        if (!initialized) {
            initialize();
            return;
        }
        FluidMappingRegistry.INSTANCE.s_init();
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent.Pre event) {
        TextureMap map = event.map;
        int mapType = map.getTextureType(); // 0 = blocks, 1 = items

        for (FluidType hbmFluid : Fluids.getAll()) {
            if (hbmFluid == Fluids.NONE) {
                continue;
            }
            Fluid forgeFluid = mapping().getForgeFluid(hbmFluid);
            if (forgeFluid == null) {
                continue;
            }
            if (forgeFluid instanceof ColoredForgeFluid) {
                if (mapType == 0) {
                    registerBlockTextures(map, (ColoredForgeFluid) forgeFluid, hbmFluid);
                } else if (mapType == 1) {
                    registerItemTexture(map, (ColoredForgeFluid) forgeFluid, hbmFluid);
                }
            }
        }
    }

    private static void registerBlockTextures(TextureMap map, ColoredForgeFluid forgeFluid, FluidType hbmFluid) {
        IIcon still = map.registerIcon(RefStrings.MODID + ":" + hbmFluid.getTexture().getResourcePath());
        forgeFluid.setIcons(still, still);
        forgeFluid.setHasCustomTexture(true);
        forgeFluid.setColor(hbmFluid.getColor());
    }

    private static void registerItemTexture(TextureMap map, ColoredForgeFluid forgeFluid, FluidType hbmFluid) {
        IIcon inv = map.registerIcon(RefStrings.MODID + ":" + hbmFluid.getTexture().getResourcePath());
        forgeFluid.setInventoryIcon(inv);
    }
}
