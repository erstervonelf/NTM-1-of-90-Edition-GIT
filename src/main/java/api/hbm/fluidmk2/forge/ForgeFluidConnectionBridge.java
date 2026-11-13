package api.hbm.fluidmk2.forge;

import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;

import api.ntm1of90.compat.fluid.registry.FluidMappingRegistry;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidHandler;

/**
 * API-only bridge that synchronizes Forge fluid connection queries with HBM's fluidmk2
 * connection map.
 *
 * This is intended to be called from the API compat layer during initialization so that
 * Forge fluid pipes and blocks that expose {@link IFluidHandler} can be treated as valid
 * connection targets inside the HBM fluid connection system, using the same synchronized
 * fluid mapping that the ForgeFluid compat layer already uses.
 *
 * This file is intentionally API-only: it does not patch block/pipe implementations.
 * Instead it provides the connection semantics the HBM system can consult.
 */
public class ForgeFluidConnectionBridge {

    /**
     * Whether a Forge fluid handler can act as a valid connection target for the given HBM fluid
     * on the given side, from the perspective of the HBM connection map.
     *
     * This mirrors what HBM expects from IFluidConnectorMK2.canConnect, but for Forge-side
     * handlers that do not implement fluidmk2 interfaces.
     */
    public static boolean canConnectAsHbmPort(IFluidHandler forgeHandler, FluidType hbmFluid, ForgeDirection from) {
        if (forgeHandler == null || hbmFluid == Fluids.NONE || from == ForgeDirection.UNKNOWN) {
            return false;
        }
        if (!forgeHandler.canFill(from, null)) {
            return false;
        }
        Fluid forgeFluid = FluidMappingRegistry.getForgeFluid(hbmFluid);
        if (forgeFluid == null) {
            return false;
        }
        return forgeHandler.canFill(from, forgeFluid);
    }

    /**
     * Whether a Forge fluid handler can supply the given HBM fluid to the HBM system on the given
     * side.
     */
    public static boolean canProvideAsHbmPort(IFluidHandler forgeHandler, FluidType hbmFluid, ForgeDirection from) {
        if (forgeHandler == null || hbmFluid == Fluids.NONE || from == ForgeDirection.UNKNOWN) {
            return false;
        }
        if (!forgeHandler.canDrain(from, null)) {
            return false;
        }
        Fluid forgeFluid = FluidMappingRegistry.getForgeFluid(hbmFluid);
        if (forgeFluid == null) {
            return false;
        }
        return forgeHandler.canDrain(from, forgeFluid);
    }

    /**
     * Forge-fluid-name based connection probe, useful when the HBM side only knows the Forge fluid
     * name but still wants to use HBM-side connection semantics.
     */
    public static boolean canConnectForgeFluid(IFluidHandler forgeHandler, Fluid forgeFluid, ForgeDirection from) {
        if (forgeHandler == null || forgeFluid == null || from == ForgeDirection.UNKNOWN) {
            return false;
        }
        return forgeHandler.canFill(from, forgeFluid);
    }

    /**
     * Try to treat a Forge fluid handler as an HBM fluid receiver for one transfer.
     */
    public static int fillFromHbm(IFluidHandler forgeHandler, FluidType hbmFluid, ForgeDirection from, int hbmAmount) {
        if (forgeHandler == null || hbmFluid == Fluids.NONE || hbmAmount <= 0) {
            return 0;
        }
        Fluid forgeFluid = FluidMappingRegistry.getForgeFluid(hbmFluid);
        if (forgeFluid == null) {
            return 0;
        }
        int forgeCapacity = forgeHandler.fill(from, new net.minecraftforge.fluids.FluidStack(forgeFluid, 0), false);
        if (forgeCapacity <= 0) {
            return 0;
        }
        int available = forgeCapacity;
        int toSend = Math.min(hbmAmount, available);
        if (toSend <= 0) {
            return 0;
        }
        int actuallyFilled = forgeHandler.fill(from, new net.minecraftforge.fluids.FluidStack(forgeFluid, toSend), true);
        if (actuallyFilled <= 0) {
            return 0;
        }
        return actuallyFilled;
    }

    /**
     * Try to drain from a Forge fluid handler into the HBM system.
     */
    public static net.minecraftforge.fluids.FluidStack drainToHbm(IFluidHandler forgeHandler, FluidType hbmFluid, ForgeDirection from, int maxDrain) {
        if (forgeHandler == null || hbmFluid == Fluids.NONE || maxDrain <= 0) {
            return null;
        }
        Fluid forgeFluid = FluidMappingRegistry.getForgeFluid(hbmFluid);
        if (forgeFluid == null) {
            return null;
        }
        int maxForge = forgeHandler.drain(from, maxDrain, false) == null ? 0 : maxDrain;
        if (maxForge <= 0) {
            return null;
        }
        return forgeHandler.drain(from, maxForge, true);
    }
}
