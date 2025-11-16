# Machine Creation Guide - 1of90 Edition

Complete guide for creating simple blocks and multiblock machines in the 1of90 package.

---

## Table of Contents

1. [Simple Blocks/Machines](#simple-blocksmachines)
2. [Multiblock Machines](#multiblock-machines)
3. [Advanced Features](#advanced-features)
4. [Registration](#registration)
5. [Resources](#resources)

---

## Simple Blocks/Machines

### Step 1: Create the Block Class

Create your block class in `com.oneof90.blocks/`:

```java
package com.oneof90.blocks;

import com.hbm.lib.RefStrings;
import com.oneof90.tileentity.TileEntityMyMachine;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class MyMachineBlock extends BlockContainer {

    public MyMachineBlock(Material material) {
        super(material);
    }
    
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityMyMachine();
    }
    
    @Override
    public int getRenderType() {
        return -1; // Use TESR for custom rendering
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
```

### Step 2: Create the TileEntity

Create your tile entity in `com.oneof90.tileentity/`:

```java
package com.oneof90.tileentity;

import net.minecraft.tileentity.TileEntity;

public class TileEntityMyMachine extends TileEntity {

    @Override
    public void updateEntity() {
        // Your machine logic here
        if(!worldObj.isRemote) {
            // Server-side logic
        }
    }
    
    @Override
    public boolean canUpdate() {
        return true; // Set to false if no update logic needed
    }
}
```

### Step 3: Register the Block

Add to `ModBlocks1of90.java`:

```java
public static Block my_machine;

public static void init() {
    my_machine = new MyMachineBlock(Material.iron)
        .setBlockName("my_machine_1of90")
        .setCreativeTab(MainRegistry.machineTab)
        .setHardness(5.0F)
        .setResistance(10.0F);
    
    registerBlocks();
    registerTileEntities();
}

private static void registerBlocks() {
    GameRegistry.registerBlock(my_machine, "my_machine_1of90");
}

private static void registerTileEntities() {
    GameRegistry.registerTileEntity(TileEntityMyMachine.class, "tile_my_machine_1of90");
}
```

---

## Multiblock Machines

Multiblock machines extend `BlockDummyable` from HBM's core. They consist of a main core block and dummy blocks that form the structure.

### Step 1: Create the Multiblock Block Class

Create your block in `com.oneof90.blocks.machine/`:

```java
package com.oneof90.blocks.machine;

import com.hbm.blocks.BlockDummyable;
import com.oneof90.tileentity.machine.TileEntityMyMultiblock;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class MachineMyMultiblock extends BlockDummyable {

    public MachineMyMultiblock(Material mat) {
        super(mat);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        // meta < 12 means it's a dummy block
        // meta >= 12 means it's the core
        if(meta >= 12) {
            return new TileEntityMyMultiblock();
        }
        return null;
    }

    @Override
    public int[] getDimensions() {
        // Returns [width, height, depth] of the structure
        // Example: 3x3x3 machine
        return new int[] {3, 3, 3};
    }

    @Override
    public int getOffset() {
        // How far forward from the placement point the core should be
        // Usually 1 for most machines
        return 1;
    }

    @Override
    protected boolean checkRequirement(World world, int x, int y, int z, ForgeDirection dir, int o) {
        // Check if the area is clear for the multiblock
        // This is called before fillSpace
        
        int[] dim = getDimensions();
        int width = dim[0];
        int height = dim[1];
        int depth = dim[2];
        
        // Check if area is clear
        for(int a = -width/2; a <= width/2; a++) {
            for(int b = 0; b < height; b++) {
                for(int c = 0; c < depth; c++) {
                    int xPos = x + a;
                    int yPos = y + b;
                    int zPos = z + c;
                    
                    // Adjust for direction
                    if(dir == ForgeDirection.NORTH) {
                        zPos = z - c;
                    } else if(dir == ForgeDirection.SOUTH) {
                        zPos = z + c;
                    } else if(dir == ForgeDirection.WEST) {
                        xPos = x - c;
                    } else if(dir == ForgeDirection.EAST) {
                        xPos = x + c;
                    }
                    
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
        // Fill the multiblock structure with dummy blocks
        
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
                    int xPos = coreX + a;
                    int yPos = coreY + b;
                    int zPos = coreZ + c;
                    
                    // Adjust for direction
                    if(dir == ForgeDirection.NORTH) {
                        zPos = coreZ - c;
                    } else if(dir == ForgeDirection.SOUTH) {
                        zPos = coreZ + c;
                    } else if(dir == ForgeDirection.WEST) {
                        xPos = coreX - c;
                    } else if(dir == ForgeDirection.EAST) {
                        xPos = coreX + c;
                    }
                    
                    // Don't place dummy at core position
                    if(xPos == coreX && yPos == coreY && zPos == coreZ) {
                        continue;
                    }
                    
                    // Place dummy block
                    world.setBlock(xPos, yPos, zPos, this, dir.ordinal(), 3);
                }
            }
        }
        
        // Set the core metadata
        world.setBlockMetadataWithNotify(coreX, coreY, coreZ, dir.ordinal() + 10, 3);
    }

    @Override
    public int getRenderType() {
        return -1; // Use TESR
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
```

### Step 2: Create the TileEntity

Create in `com.oneof90.tileentity.machine/`:

```java
package com.oneof90.tileentity.machine;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityMyMultiblock extends TileEntity {

    // Energy, fluids, inventory, etc.
    private int energy = 0;
    private int maxEnergy = 100000;
    
    @Override
    public void updateEntity() {
        if(!worldObj.isRemote) {
            // Server-side machine logic
            
            // Example: Process items, consume energy, etc.
        }
    }
    
    @Override
    public boolean canUpdate() {
        return true;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        energy = nbt.getInteger("energy");
    }
    
    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("energy", energy);
    }
}
```

### Step 3: Create the Renderer (Optional)

If you want custom 3D rendering, create in `com.oneof90.render.machine/`:

```java
package com.oneof90.render.machine;

import org.lwjgl.opengl.GL11;
import com.hbm.main.ResourceManager;
import com.oneof90.tileentity.machine.TileEntityMyMultiblock;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class RenderMyMultiblock extends TileEntitySpecialRenderer {

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f) {
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y, z + 0.5);
        GL11.glEnable(GL11.GL_LIGHTING);
        
        // Get rotation from metadata
        int metadata = tileEntity.getBlockMetadata();
        if(metadata >= 12) {
            // It's a core block, render the full model
            float rotation = 0.0F;
            switch(metadata - 10) {
                case 2: rotation = 0.0F; break;   // North
                case 3: rotation = 180.0F; break; // South
                case 4: rotation = 90.0F; break;  // West
                case 5: rotation = 270.0F; break; // East
            }
            GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
            
            // Bind your texture
            bindTexture(new ResourceLocation("hbm", "textures/models/1of90_my_multiblock.png"));
            
            // Render your model here
            // if(ResourceManager.my_model != null) {
            //     ((WavefrontObject) ResourceManager.my_model).renderAll();
            // }
        }
        
        GL11.glPopMatrix();
    }
}
```

---

## Advanced Features

### Adding Inventory

Implement `IInventory` or `ISidedInventory`:

```java
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;

public class TileEntityMyMultiblock extends TileEntity implements ISidedInventory {
    private ItemStack[] inventory = new ItemStack[9];
    
    @Override
    public int getSizeInventory() {
        return inventory.length;
    }
    
    @Override
    public ItemStack getStackInSlot(int slot) {
        return inventory[slot];
    }
    
    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        inventory[slot] = stack;
    }
    
    // Implement other IInventory methods...
    
    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8};
    }
    
    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        return true;
    }
    
    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        return true;
    }
}
```

### Adding Energy Support (HBM Energy)

Implement HBM's energy interfaces:

```java
import com.hbm.interfaces.IEnergyReceiverMK2;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityMyMultiblock extends TileEntity implements IEnergyReceiverMK2 {
    private long energy = 0;
    private long maxEnergy = 100000;
    
    @Override
    public boolean canConnect(ForgeDirection dir) {
        return true;
    }
    
    @Override
    public long transferPower(long power) {
        long received = Math.min(power, maxEnergy - energy);
        energy += received;
        return received;
    }
    
    @Override
    public long getPower() {
        return energy;
    }
    
    @Override
    public long getMaxPower() {
        return maxEnergy;
    }
}
```

### Adding Fluid Support

Implement fluid interfaces:

```java
import com.hbm.interfaces.IFluidStandardReceiver;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityMyMultiblock extends TileEntity implements IFluidStandardReceiver {
    public FluidTank[] tanks = new FluidTank[2];
    
    public TileEntityMyMultiblock() {
        tanks[0] = new FluidTank(16000);
        tanks[1] = new FluidTank(16000);
    }
    
    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return tanks[0].fill(resource, doFill);
    }
    
    @Override
    public FluidTank[] getReceivingTanks() {
        return tanks;
    }
    
    @Override
    public FluidTank[] getAllTanks() {
        return tanks;
    }
}
```

### Adding GUI Support

```java
import com.hbm.interfaces.IGUIProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class TileEntityMyMultiblock extends TileEntity implements IGUIProvider {
    
    @Override
    public Container provideContainer(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return new ContainerMyMultiblock(player.inventory, this);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public GuiScreen provideGUI(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return new GUIMyMultiblock(player.inventory, this);
    }
}
```

---

## Registration

### Register Block and TileEntity

Update `ModBlocks1of90.java`:

```java
public static Block my_multiblock;

public static void init() {
    my_multiblock = new MachineMyMultiblock(Material.iron)
        .setBlockName("machine_my_multiblock_1of90")
        .setCreativeTab(MainRegistry.machineTab)
        .setHardness(5.0F)
        .setResistance(10.0F);
    
    registerBlocks();
    registerTileEntities();
}

private static void registerBlocks() {
    GameRegistry.registerBlock(my_multiblock, "machine_my_multiblock_1of90");
}

private static void registerTileEntities() {
    GameRegistry.registerTileEntity(TileEntityMyMultiblock.class, "tile_my_multiblock_1of90");
}
```

### Register Renderer

Update `MainRegistry1of90.java`:

```java
@SideOnly(Side.CLIENT)
private static void registerTileEntityRenderers() {
    ClientRegistry.bindTileEntitySpecialRenderer(
        TileEntityMyMultiblock.class, 
        new RenderMyMultiblock()
    );
}
```

---

## Resources

### Textures

Place textures in:
- `src/main/resources/assets/hbm/textures/blocks/` - Block textures
- `src/main/resources/assets/hbm/textures/models/` - Model textures

### 3D Models

Place OBJ models in:
- `src/main/resources/assets/hbm/models/`

### Localization

Add to `src/main/resources/assets/hbm/lang/en_US.lang`:

```
tile.my_machine_1of90.name=My Custom Machine
tile.machine_my_multiblock_1of90.name=My Multiblock Machine
```

---

## Tips and Best Practices

### Multiblock Metadata

- **0-5**: Dummy blocks facing different directions
- **6-11**: Extra dummy blocks (for special cases)
- **12-15**: Core block with rotation (12=N, 13=S, 14=W, 15=E)

### Multiblock Placement

1. Player right-clicks with the block
2. `checkRequirement()` is called to verify the area is clear
3. `fillSpace()` places dummy blocks around the core
4. Core block is placed with metadata >= 12

### Direction Mapping

```java
ForgeDirection.NORTH  -> metadata 2  -> core 12
ForgeDirection.SOUTH  -> metadata 3  -> core 13
ForgeDirection.WEST   -> metadata 4  -> core 14
ForgeDirection.EAST   -> metadata 5  -> core 15
```

### Rendering Tips

- Only render full model when `metadata >= 12` (core block)
- Apply rotation based on `(metadata - 10)`
- Use `GL11` transforms for positioning
- Bind textures before rendering models

### Performance

- Set `canUpdate() = false` if tile doesn't need ticking
- Only sync to clients when necessary
- Cache calculations instead of recalculating every tick
- Use scheduled updates instead of constant checking

### Debugging

- F3 shows block metadata
- Check if dummies are placed correctly
- Test multiblock in all 4 directions
- Verify core block has correct metadata (12-15)

---

## Example: Complete Simple Machine

See `NTMSteelBeam` in the codebase for a complete working example of a simple rotatable block.

## Example: Complete Multiblock Machine

Look at HBM's existing multiblock machines like:
- `MachineFusionBoiler`
- `MachineAssemblyMachine`
- `MachineOrbus`

These show advanced patterns for multiblock structures.
