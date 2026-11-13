package api.hbm.fluidmk2.forge;

import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTank;
import com.hbm.uninos.UniNodespace;

import api.hbm.fluidmk2.FluidNetMK2;
import api.hbm.fluidmk2.FluidNode;
import api.hbm.fluidmk2.IFluidConnectorMK2;
import api.hbm.fluidmk2.IFluidPipeMK2;
import api.hbm.fluidmk2.IFluidPipeSingle;
import api.hbm.fluidmk2.IFluidUserMK2;
import api.ntm1of90.compat.fluid.registry.FluidMappingRegistry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

/**
 * Implementation body for the Forge fluid handler defaults on {@link IFluidConnectorMK2}.
 *
 * Two kinds of HBM blocks answer to Forge's fluid API through this class:
 * - machines / tanks / ports ({@link IFluidUserMK2}) expose their FluidTanks directly,
 * - ducts and pipes ({@link IFluidPipeMK2}) act as a live tap into their FluidNetMK2,
 *   so a foreign pump that fills a duct pushes the fluid straight into the HBM network,
 *   and a foreign pipe that drains a duct pulls from the network's providers.
 */
public final class ForgeFluidEndpoint {

	/** safety cap per single transfer, purely to keep overflowing int math out of the network */
	public static final int TRANSFER_CAP = 1_000_000;

	private ForgeFluidEndpoint() { }

	private static void markDirty(Object tile) {
		if(tile instanceof TileEntity) ((TileEntity) tile).markDirty();
	}

	// ------------------------------------------------------------------ pipes

	public static FluidType getPipeType(IFluidPipeMK2 pipe) {
		if(pipe instanceof IFluidPipeSingle) {
			FluidType type = ((IFluidPipeSingle) pipe).getType();
			if(type != null) return type;
		}
		return Fluids.NONE;
	}

	/** Looks up the fluid node sitting on the given pipe's own position, and returns its network if it has one */
	public static FluidNetMK2 getNet(IFluidConnectorMK2 connector, FluidType type) {
		if(!(connector instanceof TileEntity)) return null;
		if(type == null || type == Fluids.NONE) return null;

		TileEntity tile = (TileEntity) connector;
		World world = tile.getWorldObj();
		if(world == null || world.isRemote) return null;

		FluidNode node = (FluidNode) UniNodespace.getNode(world, tile.xCoord, tile.yCoord, tile.zCoord, type.getNetworkProvider());
		if(node == null || !node.hasValidNet()) return null;

		return node.net;
	}

	public static int fillPipe(IFluidPipeMK2 pipe, ForgeDirection from, FluidStack resource, boolean doFill) {
		if(resource == null || resource.amount <= 0) return 0;

		FluidType pushed = FluidMappingRegistry.getHbmFluidType(resource.getFluid());
		if(pushed == null || pushed == Fluids.NONE) return 0;

		FluidType pipeType = getPipeType(pipe);
		FluidType lookup = pipeType == Fluids.NONE ? pushed : pipeType;
		if(pipeType != Fluids.NONE && pipeType != pushed) return 0;

		FluidNetMK2 net = getNet(pipe, lookup);
		if(net == null) return 0;
		if(net.getType() != pushed) return 0;

		int amount = Math.min(resource.amount, TRANSFER_CAP);
		if(!doFill) return amount;

		long accepted = net.inject(pushed, amount);
		if(accepted <= 0) return 0;

		markDirty(pipe);
		return (int) Math.min(accepted, Integer.MAX_VALUE);
	}

	public static FluidStack drainPipe(IFluidPipeMK2 pipe, ForgeDirection from, FluidStack resource, int maxDrain, boolean doDrain) {
		if(maxDrain <= 0) return null;

		FluidType pipeType = getPipeType(pipe);
		if(pipeType == Fluids.NONE) return null;

		if(resource != null) {
			FluidType requested = FluidMappingRegistry.getHbmFluidType(resource.getFluid());
			if(requested != pipeType) return null;
		}

		Fluid forgeFluid = FluidMappingRegistry.getForgeFluid(pipeType);
		if(forgeFluid == null) return null;

		FluidNetMK2 net = getNet(pipe, pipeType);
		if(net == null) return null;

		int amount = Math.min(maxDrain, TRANSFER_CAP);
		long taken = doDrain ? net.extract(pipeType, amount) : net.simulateExtract(pipeType, amount);
		if(taken <= 0) return null;

		if(doDrain) markDirty(pipe);
		return new FluidStack(forgeFluid, (int) Math.min(taken, Integer.MAX_VALUE));
	}

	public static boolean canFillPipe(IFluidPipeMK2 pipe, ForgeDirection from, Fluid fluid) {
		if(fluid == null) return getPipeType(pipe) != Fluids.NONE;

		FluidType pushed = FluidMappingRegistry.getHbmFluidType(fluid);
		if(pushed == null || pushed == Fluids.NONE) return false;

		FluidType pipeType = getPipeType(pipe);
		return pipeType == Fluids.NONE || pipeType == pushed;
	}

	public static boolean canDrainPipe(IFluidPipeMK2 pipe, ForgeDirection from, Fluid fluid) {
		FluidType pipeType = getPipeType(pipe);
		if(pipeType == Fluids.NONE) return false;
		if(fluid == null) return true;
		return FluidMappingRegistry.getHbmFluidType(fluid) == pipeType;
	}

	// ------------------------------------------------------------------ tanks

	public static int fillTanks(IFluidUserMK2 user, ForgeDirection from, FluidStack resource, boolean doFill) {
		if(resource == null || resource.amount <= 0) return 0;

		FluidType pushed = FluidMappingRegistry.getHbmFluidType(resource.getFluid());
		if(pushed == null || pushed == Fluids.NONE) return 0;
		if(from != null && from != ForgeDirection.UNKNOWN && !user.canConnect(pushed, from)) return 0;

		FluidTank[] tanks = user.getAllTanks();
		if(tanks == null) return 0;

		int requested = Math.min(resource.amount, TRANSFER_CAP);
		int remaining = requested;

		for(FluidTank tank : tanks) {
			if(tank == null || remaining <= 0) continue;

			int fill = tank.getFill();
			FluidType current = tank.getTankType();
			if(fill > 0 && current != pushed) continue;

			int space = tank.getMaxFill() - fill;
			if(space <= 0) continue;

			int toFill = Math.min(space, remaining);
			if(toFill <= 0) continue;

			if(doFill) {
				if(fill <= 0) tank.setTankType(pushed);
				tank.setFill(fill + toFill);
			}
			remaining -= toFill;
		}

		int filled = requested - remaining;
		if(filled > 0 && doFill) markDirty(user);
		return filled;
	}

	public static FluidStack drainTanks(IFluidUserMK2 user, ForgeDirection from, FluidStack resource, int maxDrain, boolean doDrain) {
		if(maxDrain <= 0) return null;

		FluidTank[] tanks = user.getAllTanks();
		if(tanks == null) return null;

		FluidType wanted = null;
		if(resource != null) {
			wanted = FluidMappingRegistry.getHbmFluidType(resource.getFluid());
			if(wanted == null || wanted == Fluids.NONE) return null;
			if(from != null && from != ForgeDirection.UNKNOWN && !user.canConnect(wanted, from)) return null;
		}

		int requested = Math.min(maxDrain, TRANSFER_CAP);
		int remaining = requested;
		FluidType drainedType = null;
		Fluid forged = null;

		for(FluidTank tank : tanks) {
			if(tank == null || remaining <= 0) continue;

			int fill = tank.getFill();
			FluidType current = tank.getTankType();
			if(fill <= 0 || current == null || current == Fluids.NONE) continue;
			if(wanted != null && current != wanted) continue;
			if(drainedType != null && current != drainedType) continue;

			Fluid forgeFluid = FluidMappingRegistry.getForgeFluid(current);
			if(forgeFluid == null) continue;

			int toDrain = Math.min(fill, remaining);
			if(toDrain <= 0) continue;

			if(doDrain) tank.setFill(fill - toDrain);
			remaining -= toDrain;
			drainedType = current;
			forged = forgeFluid;
		}

		int drained = requested - remaining;
		if(drained <= 0 || drainedType == null || forged == null) return null;
		if(doDrain) markDirty(user);

		return new FluidStack(forged, drained);
	}

	public static boolean canFillTanks(IFluidUserMK2 user, ForgeDirection from, Fluid fluid) {
		FluidTank[] tanks = user.getAllTanks();
		if(tanks == null) return false;

		FluidType pushed = fluid == null ? null : FluidMappingRegistry.getHbmFluidType(fluid);
		if(fluid != null && (pushed == null || pushed == Fluids.NONE)) return false;
		if(pushed != null && from != null && from != ForgeDirection.UNKNOWN && !user.canConnect(pushed, from)) return false;

		for(FluidTank tank : tanks) {
			if(tank == null) continue;
			int fill = tank.getFill();
			FluidType current = tank.getTankType();
			if(fill >= tank.getMaxFill()) continue;
			if(pushed == null || fill <= 0 || current == pushed) return true;
		}

		return false;
	}

	public static boolean canDrainTanks(IFluidUserMK2 user, ForgeDirection from, Fluid fluid) {
		FluidTank[] tanks = user.getAllTanks();
		if(tanks == null) return false;

		FluidType wanted = fluid == null ? null : FluidMappingRegistry.getHbmFluidType(fluid);
		if(fluid != null && (wanted == null || wanted == Fluids.NONE)) return false;

		for(FluidTank tank : tanks) {
			if(tank == null) continue;
			int fill = tank.getFill();
			FluidType current = tank.getTankType();
			if(fill <= 0 || current == null || current == Fluids.NONE) continue;
			if(wanted == null || current == wanted) return true;
		}

		return false;
	}

	public static FluidTankInfo[] getTankInfoTanks(IFluidUserMK2 user) {
		FluidTank[] tanks = user.getAllTanks();
		if(tanks == null) return new FluidTankInfo[0];

		FluidTankInfo[] info = new FluidTankInfo[tanks.length];
		for(int i = 0; i < tanks.length; i++) {
			FluidTank tank = tanks[i];
			if(tank == null) {
				info[i] = new FluidTankInfo(null, 0);
				continue;
			}

			int fill = tank.getFill();
			FluidType current = tank.getTankType();
			FluidStack stack = null;

			if(fill > 0 && current != null && current != Fluids.NONE) {
				Fluid forgeFluid = FluidMappingRegistry.getForgeFluid(current);
				if(forgeFluid != null) stack = new FluidStack(forgeFluid, fill);
			}

			info[i] = new FluidTankInfo(stack, tank.getMaxFill());
		}

		return info;
	}

	// ------------------------------------------------------------------ shared

	public static int fill(IFluidConnectorMK2 connector, ForgeDirection from, FluidStack resource, boolean doFill) {
		if(connector instanceof IFluidPipeMK2) return fillPipe((IFluidPipeMK2) connector, from, resource, doFill);
		if(connector instanceof IFluidUserMK2) return fillTanks((IFluidUserMK2) connector, from, resource, doFill);
		return 0;
	}

	public static FluidStack drain(IFluidConnectorMK2 connector, ForgeDirection from, FluidStack resource, int maxDrain, boolean doDrain) {
		if(connector instanceof IFluidPipeMK2) return drainPipe((IFluidPipeMK2) connector, from, resource, maxDrain, doDrain);
		if(connector instanceof IFluidUserMK2) return drainTanks((IFluidUserMK2) connector, from, resource, maxDrain, doDrain);
		return null;
	}

	public static FluidStack drainAny(IFluidConnectorMK2 connector, ForgeDirection from, int maxDrain, boolean doDrain) {
		return drain(connector, from, null, maxDrain, doDrain);
	}

	public static boolean canFill(IFluidConnectorMK2 connector, ForgeDirection from, Fluid fluid) {
		if(connector instanceof IFluidPipeMK2) return canFillPipe((IFluidPipeMK2) connector, from, fluid);
		if(connector instanceof IFluidUserMK2) return canFillTanks((IFluidUserMK2) connector, from, fluid);
		return false;
	}

	public static boolean canDrain(IFluidConnectorMK2 connector, ForgeDirection from, Fluid fluid) {
		if(connector instanceof IFluidPipeMK2) return canDrainPipe((IFluidPipeMK2) connector, from, fluid);
		if(connector instanceof IFluidUserMK2) return canDrainTanks((IFluidUserMK2) connector, from, fluid);
		return false;
	}

	public static FluidTankInfo[] getTankInfo(IFluidConnectorMK2 connector) {
		if(connector instanceof IFluidPipeMK2) {
			FluidType pipeType = getPipeType((IFluidPipeMK2) connector);
			Fluid forgeFluid = pipeType == Fluids.NONE ? null : FluidMappingRegistry.getForgeFluid(pipeType);
			return new FluidTankInfo[] { new FluidTankInfo(forgeFluid == null ? null : new FluidStack(forgeFluid, 0), TRANSFER_CAP) };
		}
		if(connector instanceof IFluidUserMK2) return getTankInfoTanks((IFluidUserMK2) connector);
		return new FluidTankInfo[0];
	}
}
