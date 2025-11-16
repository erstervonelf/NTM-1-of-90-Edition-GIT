package com.oneof90.blocks.machine;

import com.hbm.blocks.BlockDummyable;
import com.oneof90.tileentity.machine.TileEntityExampleMultiblock;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Example Multiblock Machine - Template for creating new multiblock machines
 * 
 * This is a 3x3x3 multiblock machine that serves as a template.
 * Copy this file and modify it for your own multiblock machines.
 */
public class MachineExampleMultiblock extends BlockDummyable {

	public MachineExampleMultiblock(Material mat) {
		super(mat);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		// meta < 12 = dummy block (no tile entity)
		// meta >= 12 = core block (has tile entity)
		if(meta >= 12) {
			return new TileEntityExampleMultiblock();
		}
		return null;
	}

	@Override
	public int[] getDimensions() {
		// Returns [width, height, depth] of the multiblock structure
		// Width: left-right when facing the machine
		// Height: bottom-top
		// Depth: front-back (into the machine)
		return new int[] {3, 3, 3}; // 3x3x3 cube
	}

	@Override
	public int getOffset() {
		// How many blocks forward from the placement point should the core be placed
		// 1 = core is 1 block forward from where you click
		return 1;
	}

	@Override
	protected boolean checkRequirement(World world, int x, int y, int z, ForgeDirection dir, int o) {
		// Check if the area is clear for the multiblock to be placed
		// Return false to prevent placement and refund the block
		
		int[] dim = getDimensions();
		int width = dim[0];
		int height = dim[1];
		int depth = dim[2];
		
		// Calculate core position
		int coreX = x + dir.offsetX * o;
		int coreY = y + dir.offsetY * o;
		int coreZ = z + dir.offsetZ * o;
		
		// Check all positions in the multiblock area
		for(int a = -width/2; a <= width/2; a++) {
			for(int b = 0; b < height; b++) {
				for(int c = 0; c < depth; c++) {
					int xPos = coreX;
					int yPos = coreY + b;
					int zPos = coreZ;
					
					// Adjust position based on direction
					switch(dir) {
						case NORTH:
							xPos += a;
							zPos -= c;
							break;
						case SOUTH:
							xPos -= a;
							zPos += c;
							break;
						case WEST:
							xPos -= c;
							zPos -= a;
							break;
						case EAST:
							xPos += c;
							zPos += a;
							break;
						default:
							break;
					}
					
					// Check if this position is replaceable (air, grass, etc.)
					if(!world.getBlock(xPos, yPos, zPos).isReplaceable(world, xPos, yPos, zPos)) {
						return false;
					}
				}
			}
		}
		
		return true;
	}

	@Override
	public void fillSpace(World world, int x, int y, int z, ForgeDirection dir, int o) {
		// Place dummy blocks to fill the multiblock structure
		// The core block is already placed by the parent class
		
		int[] dim = getDimensions();
		int width = dim[0];
		int height = dim[1];
		int depth = dim[2];
		
		// Calculate core position
		int coreX = x + dir.offsetX * o;
		int coreY = y + dir.offsetY * o;
		int coreZ = z + dir.offsetZ * o;
		
		// Place dummy blocks
		for(int a = -width/2; a <= width/2; a++) {
			for(int b = 0; b < height; b++) {
				for(int c = 0; c < depth; c++) {
					int xPos = coreX;
					int yPos = coreY + b;
					int zPos = coreZ;
					
					// Adjust position based on direction
					switch(dir) {
						case NORTH:
							xPos += a;
							zPos -= c;
							break;
						case SOUTH:
							xPos -= a;
							zPos += c;
							break;
						case WEST:
							xPos -= c;
							zPos -= a;
							break;
						case EAST:
							xPos += c;
							zPos += a;
							break;
						default:
							break;
					}
					
					// Skip the core position
					if(xPos == coreX && yPos == coreY && zPos == coreZ) {
						continue;
					}
					
					// Place dummy block with directional metadata
					world.setBlock(xPos, yPos, zPos, this, dir.ordinal(), 3);
				}
			}
		}
		
		// Set the core block metadata (10 + direction ordinal = 12-15)
		world.setBlockMetadataWithNotify(coreX, coreY, coreZ, dir.ordinal() + 10, 3);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		// Handle right-click interaction
		// This is called on dummy blocks too, so find the core first
		
		if(world.isRemote) {
			return true;
		}
		
		// Get the core position from this block (works for both core and dummies)
		int[] pos = this.findCore(world, x, y, z);
		if(pos == null) {
			return false;
		}
		
		TileEntity te = world.getTileEntity(pos[0], pos[1], pos[2]);
		if(te instanceof TileEntityExampleMultiblock) {
			// Open GUI or perform action
			// Example: FMLNetworkHandler.openGui(player, YourMod.instance, guiID, world, pos[0], pos[1], pos[2]);
			player.addChatMessage(new net.minecraft.util.ChatComponentText("Example Multiblock Machine activated!"));
			return true;
		}
		
		return false;
	}

	@Override
	public int getRenderType() {
		return -1; // Use TESR (TileEntitySpecialRenderer)
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
}
