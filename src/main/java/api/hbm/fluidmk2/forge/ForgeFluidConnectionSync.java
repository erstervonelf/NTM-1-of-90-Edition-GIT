package api.hbm.fluidmk2.forge;

import api.hbm.fluidmk2.IFluidConnectorBlockMK2;
import api.hbm.fluidmk2.IFluidConnectorMK2;
import com.hbm.inventory.fluid.FluidType;
import api.ntm1of90.compat.fluid.registry.FluidMappingRegistry;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;

/**
 * API-only synchronization point for Forge fluid connection handling inside the fluidmk2 API.
 *
 * This is the piece that makes the HBM fluid connection map treat Forge fluid pipes/handlers as
 * valid connection targets without editing block or pipe implementations: the compat layer calls
 * this during API initialization, and connection checks can consult
 * {@link ForgeFluidConnectionBridge} through the API.
 */
public class ForgeFluidConnectionSync {

    private static boolean initialized = false;

    /**
     * Activate Forge-fluid connection synchronization for the fluidmk2 API.
     * Safe to call multiple times.
     */
    public static void initialize() {
        if (initialized) {
            return;
        }
        if (FluidMappingRegistry.INSTANCE.s_getForgeFluidCount() == 0) {
            FluidMappingRegistry.INSTANCE.s_init();
        }
        initialized = true;
    }

    /**
     * Check whether the given fluidmk2 connector can accept the given HBM fluid from the given side,
     * including Forge-handler compatibility obtained through the API hooks.
     */
    public static boolean canConnect(IFluidConnectorMK2 connector, FluidType type, ForgeDirection dir) {
        if (connector == null || type == null || dir == null) {
            return false;
        }
        if (!connector.canConnect(type, dir)) {
            return false;
        }
        if (type == null) {
            return true;
        }
        IFluidHandler forgeHandler = connector.getForgeFluidHandler(dir);
        if (forgeHandler != null) {
            return ForgeFluidConnectionBridge.canConnectAsHbmPort(forgeHandler, type, dir);
        }
        return true;
    }

    /**
     * Check whether the given block can connect to the given HBM fluid on the given face,
     * including Forge-handler compatibility obtained through the block API hook.
     */
    public static boolean canConnectBlock(IFluidConnectorBlockMK2 block, FluidType type, net.minecraft.world.IBlockAccess world, int x, int y, int z, ForgeDirection dir) {
        if (block == null || type == null || dir == null || world == null) {
            return false;
        }
        if (!block.canConnect(type, world, x, y, z, dir)) {
            return false;
        }
        IFluidHandler forgeHandler = block.getForgeFluidHandler(world, x, y, z, dir);
        if (forgeHandler != null) {
            return ForgeFluidConnectionBridge.canConnectAsHbmPort(forgeHandler, type, dir);
        }
        return true;
    }
}
