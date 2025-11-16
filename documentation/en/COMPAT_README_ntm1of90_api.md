# NTM1of90 API & Compatibility (EN)

Summary of compatibility subpackages originally under `api/ntm1of90/`.

## Modules
- Fluid compatibility (Forge bridge, adapters, mapping)
- AE2 integration (fluid colors, cell rendering, GUI)
- Core utilities (initialization, discovery, registry)

## Init
```java
NTM1of90API.initialize();
ForgeFluidCompatManager.initialize(); // if using fluids
```

## Fluid Mapping Sample
```java
FluidType hbmFluid = FluidMappingRegistry.getHbmFluidType(forgeFluid);
Fluid forgeFluid = FluidMappingRegistry.getForgeFluid(hbmFluid);
```

## AE2 Refresh Cycle
Colors & items refreshed every 100 client ticks.

## References
`COMPATIBILITY_API_TUTORIAL.md` (DE + EN), fluid README details.
