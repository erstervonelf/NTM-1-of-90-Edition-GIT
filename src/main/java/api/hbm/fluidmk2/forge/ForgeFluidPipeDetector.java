package api.hbm.fluidmk2.forge;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import api.hbm.fluidmk2.IFluidConnectorMK2;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.IFluidHandler;

/**
 * Identifies neighboring tiles that belong to a foreign (Forge) fluid transport system rather than to HBM's own
 * fluidmk2 connector API, so HBM ducts can link up with them.
 *
 * A neighbor counts as foreign when it is a Forge {@link IFluidHandler} and not an HBM connector itself, or when it
 * implements a BuildCraft-style {@code IPipeConnection} (BuildCraft family pipes are not IFluidHandlers; they push
 * and pull through the neighbor's Forge handler instead).
 */
public final class ForgeFluidPipeDetector {

	private static final Map<Class<?>, Boolean> PIPE_CONNECTION_CACHE = new ConcurrentHashMap<Class<?>, Boolean>();

	private ForgeFluidPipeDetector() { }

	public static boolean isForeignFluidHandler(TileEntity tile) {
		if(tile == null) return false;
		if(tile instanceof IFluidConnectorMK2) return false; // HBM's own connections are handled by the connector API
		if(tile instanceof IFluidHandler) return true;
		if(implementsPipeConnection(tile.getClass())) return true;
		return looksLikePipe(tile.getClass());
	}

	/** Last resort for pipe mods that expose neither a Forge handler nor an IPipeConnection */
	private static boolean looksLikePipe(Class<?> clazz) {
		String name = clazz.getSimpleName().toLowerCase(java.util.Locale.US);
		if(name.isEmpty()) return false;
		return name.contains("pipe") || name.contains("duct") || name.contains("conduit");
	}

	private static boolean implementsPipeConnection(Class<?> clazz) {
		Boolean cached = PIPE_CONNECTION_CACHE.get(clazz);
		if(cached != null) return cached.booleanValue();

		boolean result = checkInterfaces(clazz);
		PIPE_CONNECTION_CACHE.put(clazz, Boolean.valueOf(result));
		return result;
	}

	private static boolean checkInterfaces(Class<?> clazz) {
		for(Class<?> current = clazz; current != null; current = current.getSuperclass()) {
			for(Class<?> iface : current.getInterfaces()) {
				if(iface.getName().endsWith("IPipeConnection")) return true;
				if(checkInterfaces(iface)) return true;
			}
		}
		return false;
	}
}
