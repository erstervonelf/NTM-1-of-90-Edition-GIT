# TROUBLESHOOTING – Diagnostics & Solutions (1of90)

English translation of the original German troubleshooting guide. Use this to quickly identify and resolve common issues in blocks, TileEntities, multiblocks, rendering and resources.

## Categories Overview
| Category | Common Issues |
|----------|---------------|
| Placement | Structure invalid, wrong rotation |
| TileEntity | No tick, data loss, NBT errors |
| Rendering | Wrong orientation, missing texture |
| Resources | Pink/black checker, path mismatch |
| Performance | Lag, heavy tick cost |
| Network / GUI | GUI won't open, wrong values |

---
## Placement Issues
| Symptom | Cause | Fix |
|---------|-------|-----|
| Multiblock inactive | `checkRequirement()` returns false | Add debug output, verify replaceable blocks |
| Core rotation wrong | Meta not `dir.ordinal() + 10` | Adjust setting in `fillSpace()` |
| Missing dummy blocks | Loop stops too early | Validate dimension iteration bounds |
| Structure offset | Wrong offset logic | Return correct value from `getOffset()` (usually 1) |

### Debug Tip
```java
player.addChatMessage(new ChatComponentText("Check failed at x=" + xPos));
```

---
## TileEntity Issues
| Symptom | Cause | Fix |
|---------|-------|-----|
| TE absent | Meta < 12 (dummy not core) | Check core metadata |
| No progress | `canProcess()` false | Validate logic / energy / inputs |
| Energy jumps | Overflow in math | Clamp using `Math.min` |
| Data lost after reload | Missing NBT keys | Persist all relevant fields |
| Inventory emptied | Faulty `readFromNBT` | Check slot loop & tag names |

### NBT Inspection
```java
System.out.println(nbt);
```

---
## Rendering Issues
| Symptom | Cause | Fix |
|---------|-------|-----|
| Wrong rotation | Incorrect meta | Use `rotation = meta - 10` |
| Missing texture | Bad path | Verify `ResourceLocation` |
| Flickering model | Double render | Early `return` for meta < 12 |
| Odd shadows | Lighting disabled | `GL11.glEnable(GL11.GL_LIGHTING)` |
| Transparent parts black | Blend disabled | Enable blend + proper BlendFunc |

---
## Resource Issues
| Symptom | Cause | Fix |
|---------|-------|-----|
| Pink/black checker | Missing texture or wrong path | Verify filename + namespace |
| Wrong model scale | Origin off | Center model in Blender |
| Sound loops forever | Not stopped | Add stop condition in tick |
| Missing localization | Key absent | Add entry in `lang/en_US.lang` |

---
## Performance Issues
| Symptom | Cause | Fix |
|---------|-------|-----|
| Lag with many machines | All tick unconditionally | Gate via `canUpdate()` |
| High CPU usage | Large scans every tick | Cache & validate once |
| FPS drop | Rendering all dummies | Render only core block |
| Memory growth | Collections never cleared | Periodically purge lists/maps |

---
## Network / GUI Issues
| Symptom | Cause | Fix |
|---------|-------|-----|
| GUI won't open | Called only client side | Guard with `if(!world.isRemote)` |
| Wrong GUI values | No sync | Send server→client packet on change |
| GUI crash | Null pointer in container | Initialize all slots, check null |
| Invisible progress | Not exposed | Implement getter & sync packet |

---
## General Debug Strategy
1. Minimal reproduction world.
2. Inspect metadata (F3).
3. Log NBT before/after world reload.
4. Simplify renderer (colored cube).
5. Re-enable modules stepwise.

## Release Checklist
- [ ] No stray `System.out.println`
- [ ] NBT sane & complete
- [ ] Core meta set (>=12)
- [ ] Assets present & named correctly
- [ ] Acceptable performance (stress test)
- [ ] Documentation updated

## Cross References
`MULTIBLOCKS.md` • `RENDERING.md` • `EXTENDING.md`
