package com.hbm.tileentity.network;

import java.util.ArrayList;
import java.util.List;

import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.util.fauxpointtwelve.BlockPos;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

public class TileEntityPipeAnchor extends TileEntity {
	
	private FluidType type = null;
	private List<int[]> connected = new ArrayList<>();

	public FluidType getType() {
		return this.type;
	}

	public void setType(FluidType type) {
		this.type = type;
		this.markDirty();
	}

	public int[][] getConnected() {
		int[][] result = new int[connected.size()][3];
		for(int i = 0; i < connected.size(); i++) {
			result[i] = connected.get(i);
		}
		return result;
	}

	public void addConnection(int x, int y, int z) {
		connected.add(new int[]{x, y, z});
	}

	public void clearConnections() {
		connected.clear();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		
		// Read fluid type
		if(nbt.hasKey("fluidType")) {
			FluidType t = Fluids.fromName(nbt.getString("fluidType"));
			if(t != null) this.type = t;
		}
		
		// Read connections
		this.connected.clear();
		NBTTagList connList = nbt.getTagList("connections", 10);
		for(int i = 0; i < connList.tagCount(); i++) {
			NBTTagCompound connTag = connList.getCompoundTagAt(i);
			this.connected.add(new int[]{connTag.getInteger("x"), connTag.getInteger("y"), connTag.getInteger("z")});
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		
		// Write fluid type
		if(this.type != null) {
			nbt.setString("fluidType", this.type.getName());
		}
		
		// Write connections
		NBTTagList connList = new NBTTagList();
		for(int[] pos : this.connected) {
			NBTTagCompound connTag = new NBTTagCompound();
			connTag.setInteger("x", pos[0]);
			connTag.setInteger("y", pos[1]);
			connTag.setInteger("z", pos[2]);
			connList.appendTag(connTag);
		}
		nbt.setTag("connections", connList);
	}

}

