package api.hbm.fluidmk2;

import com.hbm.inventory.fluid.FluidType;

import api.hbm.fluidmk2.forge.ForgeFluidEndpoint;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

/**
 * Base interface of every HBM fluid connector.
 *
 * Extending Forge's {@link IFluidHandler} is what makes HBM fluid blocks visible to Forge fluid pipes
 * (BuildCraft and friends): those pipes only attach to neighbors that answer the Forge fluid API.
 * The defaults below route the Forge calls into the HBM side:
 * - ducts / pipes ({@link IFluidPipeMK2}) tap straight into their {@link FluidNetMK2},
 * - machines, tanks and ports ({@link IFluidUserMK2}) expose their FluidTanks.
 */
public interface IFluidConnectorMK2 extends IFluidHandler {
	
	/**
	 * Whether the given side can be connected to
	 * @param dir
	 * @return
	 */
	public default boolean canConnect(FluidType type, ForgeDirection dir) {
		return dir != ForgeDirection.UNKNOWN;
	}

	/**
	 * API hook for synchronized Forge-fluid connection handling.
	 * If a tile returns a non-null handler here, the compat layer may treat that Forge handler
	 * as a valid fluid connection target in the HBM connection map.
	 * By default the connector itself is a Forge fluid handler, see {@link ForgeFluidEndpoint}.
	 */
	public default IFluidHandler getForgeFluidHandler(ForgeDirection dir) {
		return this;
	}

	@Override
	public default int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return ForgeFluidEndpoint.fill(this, from, resource, doFill);
	}

	@Override
	public default FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if(resource == null) return null;
		return ForgeFluidEndpoint.drain(this, from, resource, resource.amount, doDrain);
	}

	@Override
	public default FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return ForgeFluidEndpoint.drainAny(this, from, maxDrain, doDrain);
	}

	@Override
	public default boolean canFill(ForgeDirection from, Fluid fluid) {
		return ForgeFluidEndpoint.canFill(this, from, fluid);
	}

	@Override
	public default boolean canDrain(ForgeDirection from, Fluid fluid) {
		return ForgeFluidEndpoint.canDrain(this, from, fluid);
	}

	@Override
	public default FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return ForgeFluidEndpoint.getTankInfo(this);
	}
}
