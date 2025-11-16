# 1of90 Edition - Separate Content Package

This package (`com.oneof90`) provides a separate, modular structure for adding new content to NTM without modifying the original HBM codebase directly.

## 📖 Documentation

**[⭐ MACHINE CREATION GUIDE ⭐](MACHINE_CREATION_GUIDE.md)** - Complete guide for creating simple blocks and multiblock machines

## Package Structure

```
com/oneof90/
├── blocks/          - Custom block classes
│   └── machine/     - Multiblock machine blocks
├── tileentity/      - Custom tile entities
│   └── machine/     - Machine tile entities
├── render/          - Custom renderers (TESR, etc.)
│   └── machine/     - Machine renderers
└── main/            - Registry and initialization classes
```

## Example Templates

Reference implementations are provided:
- **Simple Block**: `NTMSteelBeam` and `NTMSteelBeamVertical`
- **Multiblock Machine**: `MachineExampleMultiblock` with `TileEntityExampleMultiblock`

## Philosophy

The 1of90 package follows these principles:

1. **Separation of Concerns**: Keep 1of90 additions separate from HBM's original code
2. **Minimal Integration**: Only add initialization hooks in HBM's main classes
3. **Consistent Structure**: Mirror HBM's package structure for familiarity
4. **Easy Maintenance**: Changes to 1of90 content don't affect HBM's code

## Current Content

### Steel Beams
- `NTMSteelBeam` - Horizontal steel beam with rotation (1of90 variant)
- `NTMSteelBeamVertical` - Vertical steel beam (1of90 variant)

## Adding New Content

To add new blocks/items:

1. Create your classes in the appropriate `com.oneof90.*` package
2. Register them in `ModBlocks1of90.java` or create a new registry class
3. Add client-side renderers to `MainRegistry1of90.initClient()` if needed

Example:
```java
// In ModBlocks1of90.java
public static Block my_new_block;

public static void init() {
    my_new_block = new MyNewBlock(Material.iron)
        .setBlockName("my_new_block_1of90")
        .setCreativeTab(MainRegistry.blockTab)
        .setHardness(5.0F);
    
    GameRegistry.registerBlock(my_new_block, "my_new_block_1of90");
}
```

## Integration Points

The 1of90 package is integrated into HBM at two points:

1. **MainRegistry.PreLoad()**: `MainRegistry1of90.init()` - Registers blocks, items, etc.
2. **ClientProxy.registerRenderInfo()**: `MainRegistry1of90.initClient()` - Registers client renderers

## Resources

Textures and models should be placed in:
- `src/main/resources/assets/hbm/textures/blocks/` for block textures
- `src/main/resources/assets/hbm/models/` for 3D models
- Use the prefix `1of90_` for resource names to avoid conflicts

## Naming Convention

- Classes: Use `NTM` prefix (e.g., `NTMSteelBeam`)
- Block names: Use `_1of90` suffix (e.g., `ntm_steel_beam_1of90`)
- Textures: Use `1of90_` prefix (e.g., `1of90_steel_beam.png`)
- TileEntities: Use `TileEntityNTM` prefix (e.g., `TileEntityNTMSteelBeam`)

## Benefits

- **No HBM Code Modification**: Add features without touching HBM's classes (except init hooks)
- **Easy Updates**: When HBM updates, merge conflicts are minimized
- **Clear Ownership**: It's obvious which code is 1of90 vs original HBM
- **Modular**: Can be easily extracted or disabled if needed
