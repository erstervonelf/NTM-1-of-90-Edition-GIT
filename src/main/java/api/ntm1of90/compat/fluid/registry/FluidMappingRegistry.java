package api.ntm1of90.compat.fluid.registry;

import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;

import api.ntm1of90.compat.fluid.render.ColoredForgeFluid;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Synchronized mapping registry shared by both the fluidmk2 API and the NTM1of90 ForgeFluid compat layer.
 *
 * This is the single source of truth for the FluidType <-> Forge-Fluid mapping. The fluidmk2 API
 * owns registration + mapping initialization via CompatFluidMK2Registry; the NTM compat layer
 * reuses it so both APIs stay synchronized on one mapping.
 *
 * For backward compatibility with existing NTM call sites, this class still exposes the legacy
 * static API; those methods delegate to the shared instance.
 */
public final class FluidMappingRegistry {

    public static final FluidMappingRegistry INSTANCE = new FluidMappingRegistry();

    private final Map<String, FluidType> forgeToHbm = new HashMap<>();
    private final Map<FluidType, String> hbmToForge = new HashMap<>();
    private boolean initialized = false;

    private FluidMappingRegistry() {
    }

    // ---- legacy static API (delegates to the shared instance) ----

    public static void initialize() {
        INSTANCE.s_init();
    }

    public static FluidType getHbmFluidType(Fluid fluid) {
        return INSTANCE.s_getHbmFluidType(fluid);
    }

    public static Fluid getForgeFluid(FluidType type) {
        return INSTANCE.s_getForgeFluid(type);
    }

    public static String getForgeFluidName(FluidType type) {
        return INSTANCE.s_getForgeFluidName(type);
    }

    public static int getForgeFluidCount() {
        return INSTANCE.s_getForgeFluidCount();
    }

    // ---- instance API used by CompatFluidMK2Registry and NTM compat startup ----

    public int s_getForgeFluidCount() {
        return forgeToHbm.size();
    }

    public void s_init() {
        if (initialized) {
            s_refreshFromFluidMK2();
            return;
        }
        s_buildFromFluidMK2();
        initialized = true;
    }

    private void s_buildFromFluidMK2() {
        for (FluidType hbmFluid : Fluids.getAll()) {
            if (hbmFluid == Fluids.NONE) {
                continue;
            }
            if (hbmFluid.hasNoContainer() || hbmFluid.hasNoID()) {
                continue;
            }
            String forgeName = hbmFluid.getName().toLowerCase(Locale.US);
            s_registerMapping(forgeName, hbmFluid, false);
        }
    }

    private void s_refreshFromFluidMK2() {
        Map<String, FluidType> desired = new HashMap<>();
        for (FluidType hbmFluid : Fluids.getAll()) {
            if (hbmFluid == Fluids.NONE || hbmFluid.hasNoContainer() || hbmFluid.hasNoID()) {
                continue;
            }
            String forgeName = hbmFluid.getName().toLowerCase(Locale.US);
            desired.put(forgeName, hbmFluid);
        }
        for (FluidType hbmFluid : Fluids.getAll()) {
            if (hbmFluid == Fluids.NONE || hbmFluid.hasNoContainer() || hbmFluid.hasNoID()) {
                continue;
            }
            for (String alias : s_aliasNames(hbmFluid)) {
                desired.put(alias, hbmFluid);
            }
        }
        for (FluidType hbmFluid : Fluids.getAll()) {
            String existing = hbmToForge.get(hbmFluid);
            if (existing != null && desired.containsKey(existing)) {
                desired.put(existing, hbmFluid);
            }
        }
        forgeToHbm.clear();
        hbmToForge.clear();
        for (Map.Entry<String, FluidType> e : desired.entrySet()) {
            s_registerMapping(e.getKey(), e.getValue(), true);
        }
    }

    private void s_registerMapping(String forgeName, FluidType hbmType, boolean quiet) {
        Fluid existing = FluidRegistry.getFluid(forgeName);
        if (existing == null) {
            int color = hbmType.getColor();
            Fluid fluid = new ColoredForgeFluid(forgeName, color);
            FluidRegistry.registerFluid(fluid);
            if (!quiet) {
                System.out.println("[fluidmk2] Registered Forge fluid '" + forgeName + "' -> "
                    + hbmType.getName() + " (0x" + Integer.toHexString(color) + ")");
            }
            existing = fluid;
        }
        forgeToHbm.put(forgeName, hbmType);
        hbmToForge.put(hbmType, forgeName);
    }

    private List<String> s_aliasNames(FluidType hbmFluid) {
        List<String> out = new ArrayList<>();
        String name = hbmFluid.getName();
        if (name.equals("OIL")) out.add("crude_oil");
        else if (name.equals("HEAVYOIL")) out.add("heavy_oil");
        else if (name.equals("LIGHTOIL")) out.add("light_oil");
        else if (name.equals("GAS")) out.add("natural_gas");
        else if (name.equals("PETROIL")) out.add("fuel");
        else if (name.equals("SULFURIC_ACID")) out.add("sulfuricacid");
        else if (name.equals("UF6")) out.add("uranium_hexafluoride");
        else if (name.equals("BIOFUEL")) out.add("ethanol");
        else if (name.equals("HELIUM3")) out.add("helium");
        else if (name.equals("CARBONDIOXIDE")) out.add("carbon_dioxide");
        String lower = name.toLowerCase(Locale.US);
        if (lower.contains("_")) {
            out.add(lower.replace("_", ""));
        }
        return out;
    }

    public FluidType s_getHbmFluidType(Fluid fluid) {
        if (fluid == null) return Fluids.NONE;
        String name = fluid.getName().toLowerCase(Locale.US);
        FluidType hit = forgeToHbm.get(name);
        if (hit != null) return hit;
        for (FluidType hbm : Fluids.getAll()) {
            if (hbm == Fluids.NONE) continue;
            String hbmName = hbm.getName().toLowerCase(Locale.US);
            if (hbmName.equals(name)) {
                s_registerMapping(name, hbm, true);
                return hbm;
            }
            if (hbmName.replace("_", "").equals(name.replace("_", ""))) {
                s_registerMapping(name, hbm, true);
                return hbm;
            }
        }
        for (FluidType hbm : Fluids.getAll()) {
            if (hbm == Fluids.NONE) continue;
            String hbmName = hbm.getName().toLowerCase(Locale.US);
            if (hbmName.contains(name) || name.contains(hbmName)) {
                s_registerMapping(name, hbm, true);
                return hbm;
            }
            if (name.startsWith("fluid") && name.substring(5).equals(hbmName)) {
                s_registerMapping(name, hbm, true);
                return hbm;
            }
        }
        if (name.equals("fuel")) {
            FluidType p = forgeToHbm.get("petroleum");
            if (p != null) {
                s_registerMapping(name, p, true);
                return p;
            }
        }
        if (name.equals("ethanol")) {
            FluidType b = forgeToHbm.get("biofuel");
            if (b != null) {
                s_registerMapping(name, b, true);
                return b;
            }
        }
        if (name.equals("crude_oil")) {
            FluidType o = forgeToHbm.get("oil");
            if (o != null) {
                s_registerMapping(name, o, true);
                return o;
            }
        }
        return Fluids.NONE;
    }

    public Fluid s_getForgeFluid(FluidType type) {
        if (type == null || type == Fluids.NONE) return null;
        String name = hbmToForge.get(type);
        if (name != null) {
            Fluid f = FluidRegistry.getFluid(name);
            if (f != null) return f;
        }
        String lower = type.getName().toLowerCase(Locale.US);
        Fluid f = FluidRegistry.getFluid(lower);
        if (f != null) {
            s_registerMapping(lower, type, true);
            return f;
        }
        for (String forgeName : FluidRegistry.getRegisteredFluids().keySet()) {
            String cleanHbm = lower.replace("_", "");
            String cleanForge = forgeName.replace("_", "");
            if (cleanHbm.equals(cleanForge)) {
                s_registerMapping(forgeName, type, true);
                return FluidRegistry.getFluid(forgeName);
            }
        }
        for (String forgeName : FluidRegistry.getRegisteredFluids().keySet()) {
            String lower2 = type.getName().toLowerCase(Locale.US);
            if (forgeName.contains(lower2) || lower2.contains(forgeName)) {
                s_registerMapping(forgeName, type, true);
                return FluidRegistry.getFluid(forgeName);
            }
            if (forgeName.startsWith("fluid") && forgeName.substring(5).equals(lower)) {
                s_registerMapping(forgeName, type, true);
                return FluidRegistry.getFluid(forgeName);
            }
        }
        if (type == Fluids.PETROIL) {
            Fluid fuel = FluidRegistry.getFluid("fuel");
            if (fuel != null) {
                s_registerMapping("fuel", type, true);
                return fuel;
            }
        } else if (type == Fluids.BIOFUEL) {
            Fluid eth = FluidRegistry.getFluid("ethanol");
            if (eth != null) {
                s_registerMapping("ethanol", type, true);
                return eth;
            }
        } else if (type == Fluids.OIL) {
            Fluid crude = FluidRegistry.getFluid("crude_oil");
            if (crude != null) {
                s_registerMapping("crude_oil", type, true);
                return crude;
            }
        }
        if (FluidRegistry.getFluid(lower) == null) {
            Fluid newFluid = new ColoredForgeFluid(lower, type.getColor());
            FluidRegistry.registerFluid(newFluid);
            s_registerMapping(lower, type, true);
            System.out.println("[fluidmk2] Created Forge fluid '" + lower + "' for "
                + type.getName() + " (0x" + Integer.toHexString(type.getColor()) + ")");
            return newFluid;
        }
        s_registerMapping(lower, type, true);
        return null;
    }

    public String s_getForgeFluidName(FluidType type) {
        if (type == null || type == Fluids.NONE) return null;
        return hbmToForge.get(type);
    }
}
