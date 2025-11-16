# NTM1of90 Compatibility API Tutorial

English companion guide to the German original. Provides a concise walkthrough for integrating NTM's fluid compatibility layer (Forge 1.7.10 + AE2) with custom machines.

## 1. Purpose & Concept
Bridge between internal NTM fluid/tank system and external mods (Forge `IFluidHandler`, Applied Energistics 2). Components:
- Central init (`ForgeFluidCompatManager.initialize()`)
- Registries & adapter creation
- Event hooks (player interact, tick, world unload)
- Transfer bridge (NTM ↔ Forge)
- Client color & render pipeline (AE2, inventory display)

## 2. Initialization
Call once in mod init:
```java
ForgeFluidCompatManager.initialize();
```
Guards against double init (`isInitialized()`). Sets up mapping, adapters, hooks, localization, client renderers.

## 3. Making a TileEntity Compatible
Requirements: Implements `IFluidUserMK2` and owns one or more `FluidTank` instances.
Lazy adapter retrieval:
```java
IFluidHandler handler = ForgeFluidAdapterRegistry.getFluidHandler(tile);
```
Multiple tanks: use `HBMForgeFluidBlockCompat.createForgeFluidHandler(...)`.

## 4. Fluid Transfer Utilities
Directional transfer:
```java
int moved = HBMForgeFluidBlockCompat.transferToForgeBlock(tank, world, x,y,z, ForgeDirection.NORTH, 4000);
int received = HBMForgeFluidBlockCompat.transferFromForgeBlock(world, x,y,z, tank, ForgeDirection.NORTH, 4000);
```
Pre-flight capability checks:
```java
canForgeBlockAcceptFluid(...); canForgeBlockProvideFluid(...);
```

## 5. AE2 Client Integration
Auto-enabled if AE2 present (`Loader.isModLoaded("appliedenergistics2")`). Registers fluid colors via reflection, patches inventory maps, periodically refreshes every 100 ticks.

## 6. FluidDisplayItem
Provides an ItemStack representation of any registered fluid for AE2 or custom GUIs:
```java
ItemStack display = FluidDisplayItem.getItemStackForFluid(fluid);
FluidStack stack = FluidDisplayItem.getFluidStackForItemStack(display, 1000);
```
Metadata assigned dynamically.

## 7. Integration Checklist
1. `IFluidUserMK2` implemented
2. Tanks initialized
3. Initialization invoked
4. Transfer logic throttled (batch vs. per-tick spam)
5. AE2 fluid colors visible (log confirms registration)

## 8. Troubleshooting (Quick)
| Issue | Core Fix |
|-------|----------|
| No transfer | Adapter null → request via registry |
| Wrong AE2 color | Ensure fluid extends `ColoredForgeFluid` |
| Duplicate items | Reduce registration frequency |
| Missing AE2 mapping | AE2 not loaded → ignore gracefully |

## 9. Best Practices
Avoid manual adapter caching • Batch transfers • Use consistent `ForgeDirection` • Guard client-only code • Log sparingly.

## 10. Minimal Example
```java
int moved = HBMForgeFluidBlockCompat.transferToForgeBlock(tank, worldObj, xCoord, yCoord, zCoord-1, ForgeDirection.NORTH, 1000);
```

## 11. Forward Looking
Abstract adapter base, potential migration to newer Forge capability proxy, test harness akin to `AE2CompatTest` for other mods.

## 12. References
`ForgeFluidCompatManager`, `ForgeFluidCapabilityHook`, `HBMForgeFluidBlockCompat`, `AE2FluidCompat`, `FluidDisplayItem`.

## 13. FAQ (Short)
Q: Implement my own `IFluidHandler`? A: No—use adapters.
Q: Performance concerns? A: Limit per-tick transfer size; batch operations.
Q: Need client code when AE2 absent? A: No, integration skipped.

## 14. Next Steps
Integrate multiblock core TE with same transfer pattern; temporarily elevate logging while validating.
