# Quick Reference - 1of90 Multiblock Machines

Quick reference for common patterns when creating multiblock machines.

---

## Multiblock Metadata Reference

| Metadata | Type | Description |
|----------|------|-------------|
| 0-5 | Dummy | Dummy block facing N/S/W/E/U/D |
| 6-11 | Extra | Extra dummy (special cases) |
| 12 | Core | Core facing NORTH |
| 13 | Core | Core facing SOUTH |
| 14 | Core | Core facing WEST |
| 15 | Core | Core facing EAST |

---

## Direction Mapping

```java
ForgeDirection.NORTH  = ordinal 2  -> core metadata 12
ForgeDirection.SOUTH  = ordinal 3  -> core metadata 13
ForgeDirection.WEST   = ordinal 4  -> core metadata 14
ForgeDirection.EAST   = ordinal 5  -> core metadata 15
```

---

## Common Multiblock Dimensions

| Size | getDimensions() | Description |
|------|----------------|-------------|
| 1x1x1 | `{1, 1, 1}` | Single block |
| 3x3x3 | `{3, 3, 3}` | Small cube |
| 3x4x3 | `{3, 4, 3}` | Tall machine |
| 5x3x5 | `{5, 3, 5}` | Wide platform |
| 7x5x7 | `{7, 5, 7}` | Large structure |

**Format**: `{width, height, depth}`
- Width: left-right when facing machine
- Height: bottom-top
- Depth: front-back into machine

---

## Rotation to Rendering

```java
int metadata = tileEntity.getBlockMetadata();
float rotation = 0.0F;

if(metadata >= 12) { // Core block
    switch(metadata - 10) {
        case 2: rotation = 0.0F; break;    // NORTH
        case 3: rotation = 180.0F; break;  // SOUTH
        case 4: rotation = 90.0F; break;   // WEST
        case 5: rotation = 270.0F; break;  // EAST
    }
    GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
}
```

---

## Position Calculation Pattern

```java
// In fillSpace() or checkRequirement()
int[] dim = getDimensions();
int width = dim[0];
int height = dim[1];
int depth = dim[2];

int coreX = x + dir.offsetX * getOffset();
int coreY = y + dir.offsetY * getOffset();
int coreZ = z + dir.offsetZ * getOffset();

for(int a = -width/2; a <= width/2; a++) {
    for(int b = 0; b < height; b++) {
        for(int c = 0; c < depth; c++) {
            int xPos = coreX;
            int yPos = coreY + b;
            int zPos = coreZ;
            
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
            }
            
            // Skip core position
            if(xPos == coreX && yPos == coreY && zPos == coreZ) {
                continue;
            }
            
            // Place dummy or check position
            world.setBlock(xPos, yPos, zPos, this, dir.ordinal(), 3);
        }
    }
}
```

---

## Interface Implementations

### Energy (HBM)

```java
implements IEnergyReceiverMK2 {
    private long energy = 0;
    private long maxEnergy = 100000;
    
    public boolean canConnect(ForgeDirection dir) { return true; }
    public long transferPower(long power) { /*...*/ }
    public long getPower() { return energy; }
    public long getMaxPower() { return maxEnergy; }
}
```

### Inventory

```java
implements ISidedInventory {
    private ItemStack[] inventory = new ItemStack[9];
    
    public int getSizeInventory() { return inventory.length; }
    public ItemStack getStackInSlot(int slot) { return inventory[slot]; }
    public void setInventorySlotContents(int slot, ItemStack stack) { /*...*/ }
    public int[] getAccessibleSlotsFromSide(int side) { return new int[]{0,1,2,3,4,5,6,7,8}; }
    // ... other IInventory methods
}
```

### Fluids

```java
implements IFluidStandardReceiver {
    public FluidTank[] tanks = new FluidTank[2];
    
    public TileEntity() {
        tanks[0] = new FluidTank(16000);
        tanks[1] = new FluidTank(16000);
    }
    
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) { /*...*/ }
    public FluidTank[] getReceivingTanks() { return tanks; }
    public FluidTank[] getAllTanks() { return tanks; }
}
```

### GUI

```java
implements IGUIProvider {
    public Container provideContainer(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return new ContainerMyMachine(player.inventory, this);
    }
    
    @SideOnly(Side.CLIENT)
    public GuiScreen provideGUI(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return new GUIMyMachine(player.inventory, this);
    }
}
```

---

## Common NBT Patterns

```java
@Override
public void readFromNBT(NBTTagCompound nbt) {
    super.readFromNBT(nbt);
    
    energy = nbt.getLong("energy");
    progress = nbt.getInteger("progress");
    isActive = nbt.getBoolean("isActive");
    
    // Inventory
    NBTTagList list = nbt.getTagList("Items", 10);
    inventory = new ItemStack[getSizeInventory()];
    for(int i = 0; i < list.tagCount(); i++) {
        NBTTagCompound tag = list.getCompoundTagAt(i);
        int slot = tag.getByte("Slot") & 255;
        if(slot >= 0 && slot < inventory.length) {
            inventory[slot] = ItemStack.loadItemStackFromNBT(tag);
        }
    }
    
    // Fluids
    if(nbt.hasKey("tank0")) {
        tanks[0].readFromNBT(nbt.getCompoundTag("tank0"));
    }
}

@Override
public void writeToNBT(NBTTagCompound nbt) {
    super.writeToNBT(nbt);
    
    nbt.setLong("energy", energy);
    nbt.setInteger("progress", progress);
    nbt.setBoolean("isActive", isActive);
    
    // Inventory
    NBTTagList list = new NBTTagList();
    for(int i = 0; i < inventory.length; i++) {
        if(inventory[i] != null) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setByte("Slot", (byte)i);
            inventory[i].writeToNBT(tag);
            list.appendTag(tag);
        }
    }
    nbt.setTag("Items", list);
    
    // Fluids
    if(tanks[0] != null) {
        NBTTagCompound tankTag = new NBTTagCompound();
        tanks[0].writeToNBT(tankTag);
        nbt.setTag("tank0", tankTag);
    }
}
```

---

## Registration Pattern

```java
// In ModBlocks1of90.java
public static Block my_machine;

public static void init() {
    my_machine = new MachineMyMultiblock(Material.iron)
        .setBlockName("machine_my_multiblock_1of90")
        .setCreativeTab(MainRegistry.machineTab)
        .setHardness(5.0F)
        .setResistance(10.0F)
        .setStepSound(Block.soundTypeMetal);
    
    registerBlocks();
    registerTileEntities();
}

private static void registerBlocks() {
    GameRegistry.registerBlock(my_machine, "machine_my_multiblock_1of90");
}

private static void registerTileEntities() {
    GameRegistry.registerTileEntity(TileEntityMyMultiblock.class, "tile_my_multiblock_1of90");
}

// In MainRegistry1of90.java
@SideOnly(Side.CLIENT)
private static void registerTileEntityRenderers() {
    ClientRegistry.bindTileEntitySpecialRenderer(
        TileEntityMyMultiblock.class, 
        new RenderMyMultiblock()
    );
}
```

---

## Block Activation Pattern

```java
@Override
public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, 
                                int side, float hitX, float hitY, float hitZ) {
    if(world.isRemote) {
        return true;
    }
    
    // Find core from any block (dummy or core)
    int[] pos = this.findCore(world, x, y, z);
    if(pos == null) {
        return false;
    }
    
    TileEntity te = world.getTileEntity(pos[0], pos[1], pos[2]);
    if(te instanceof TileEntityMyMultiblock) {
        // Open GUI
        FMLNetworkHandler.openGui(player, MainRegistry.instance, 
                                  MyGuiID, world, pos[0], pos[1], pos[2]);
        return true;
    }
    
    return false;
}
```

---

## Render Optimization

```java
@Override
public void renderTileEntityAt(TileEntity te, double x, double y, double z, float f) {
    int meta = te.getBlockMetadata();
    
    // Only render at core
    if(meta < 12) return;
    
    GL11.glPushMatrix();
    GL11.glTranslated(x + 0.5, y, z + 0.5);
    
    // Rotation
    float rot = 0.0F;
    switch(meta - 10) {
        case 2: rot = 0.0F; break;
        case 3: rot = 180.0F; break;
        case 4: rot = 90.0F; break;
        case 5: rot = 270.0F; break;
    }
    GL11.glRotatef(rot, 0.0F, 1.0F, 0.0F);
    
    // Render model
    bindTexture(texture);
    model.renderAll();
    
    GL11.glPopMatrix();
}
```

---

## Debugging Checklist

- [ ] Block registered in ModBlocks1of90
- [ ] TileEntity registered in ModBlocks1of90
- [ ] Renderer registered in MainRegistry1of90.initClient()
- [ ] getDimensions() returns correct size
- [ ] getOffset() is appropriate (usually 1)
- [ ] checkRequirement() properly validates space
- [ ] fillSpace() places dummies correctly
- [ ] Core metadata is 12-15 (not 0-11)
- [ ] Renderer only activates when metadata >= 12
- [ ] Rotation calculation uses (metadata - 10)
- [ ] NBT save/load implemented
- [ ] canUpdate() returns appropriate value

---

## Common Issues

**Machine doesn't place**: Check `checkRequirement()` - area must be clear

**No TileEntity**: Check `createNewTileEntity()` - must check `if(meta >= 12)`

**Wrong rotation**: Check rotation calculation - should be `metadata - 10`, not just `metadata`

**Render at wrong blocks**: Check render condition - should be `if(metadata >= 12)`

**Breaks on chunk reload**: Implement proper NBT save/load

**Dummies disappear**: Make sure `fillSpace()` sets correct metadata (direction ordinal, not direction + 10)

---

For complete examples and detailed explanations, see [MACHINE_CREATION_GUIDE.md](MACHINE_CREATION_GUIDE.md)
