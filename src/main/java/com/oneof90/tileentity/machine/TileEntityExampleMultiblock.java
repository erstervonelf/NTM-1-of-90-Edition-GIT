package com.oneof90.tileentity.machine;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/**
 * Example Multiblock Machine TileEntity - Template for creating machine logic
 * 
 * This is the tile entity for the example multiblock machine.
 * It handles the machine's logic, data storage, and synchronization.
 */
public class TileEntityExampleMultiblock extends TileEntity {

	// Machine state
	private boolean isActive = false;
	private int progress = 0;
	private int maxProgress = 200; // 10 seconds at 20 ticks/second
	
	// Energy storage (example)
	private long energy = 0;
	private long maxEnergy = 100000;
	
	// Processing timer
	private int tickCounter = 0;

	@Override
	public void updateEntity() {
		// This is called every tick (20 times per second)
		
		if(!worldObj.isRemote) {
			// Server-side logic only
			
			tickCounter++;
			
			// Example: Process every second
			if(tickCounter >= 20) {
				tickCounter = 0;
				
				if(isActive && energy >= 100) {
					// Consume energy
					energy -= 100;
					
					// Increase progress
					progress++;
					
					// Check if processing is complete
					if(progress >= maxProgress) {
						progress = 0;
						isActive = false;
						
						// Output result here
						completeProcessing();
					}
				} else {
					// Not enough energy or not active
					isActive = false;
					progress = 0;
				}
			}
		}
	}
	
	/**
	 * Called when processing is complete
	 */
	private void completeProcessing() {
		// Add your output logic here
		// Example: Create items, produce fluids, etc.
	}
	
	/**
	 * Start the machine processing
	 */
	public void startProcessing() {
		if(canProcess()) {
			isActive = true;
			progress = 0;
		}
	}
	
	/**
	 * Check if the machine can start processing
	 */
	private boolean canProcess() {
		// Check if there's enough energy, input items, etc.
		return energy >= 100;
	}
	
	/**
	 * Add energy to the machine
	 */
	public long addEnergy(long amount) {
		long accepted = Math.min(amount, maxEnergy - energy);
		energy += accepted;
		return accepted;
	}
	
	// Getters and setters
	
	public boolean isActive() {
		return isActive;
	}
	
	public int getProgress() {
		return progress;
	}
	
	public int getMaxProgress() {
		return maxProgress;
	}
	
	public long getEnergy() {
		return energy;
	}
	
	public long getMaxEnergy() {
		return maxEnergy;
	}
	
	public int getProgressScaled(int scale) {
		return progress * scale / maxProgress;
	}
	
	public int getEnergyScaled(int scale) {
		return (int)(energy * scale / maxEnergy);
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		
		isActive = nbt.getBoolean("isActive");
		progress = nbt.getInteger("progress");
		energy = nbt.getLong("energy");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		
		nbt.setBoolean("isActive", isActive);
		nbt.setInteger("progress", progress);
		nbt.setLong("energy", energy);
	}
}
