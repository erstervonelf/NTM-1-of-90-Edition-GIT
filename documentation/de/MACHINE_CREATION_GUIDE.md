# Machine Creation Guide (DE, verschoben)

Vollständige Anleitung zum Erstellen einfacher und Multiblock-Maschinen. Ursprünglicher Inhalt aus `com/oneof90/MACHINE_CREATION_GUIDE.md` (gekürzt). Englisches Original blieb gemischt; diese Fassung verbleibt primär deutsch mit Codebeispielen.

## Einfache Maschine Grundschritte
1. Blockklasse erstellen (`extends BlockContainer`)
2. TileEntity mit Logik (`updateEntity`) & NBT
3. Registrierung (Block + TE)
4. Optional TESR für Spezialmodell

## Multiblock Grundmuster
Kern vs. Dummy über Metadaten (Kern ≥12). `checkRequirement()` prüft freien Raum, `fillSpace()` schreibt Dummies.

## TileEntity Beispiel
```java
public class TileEntityMyMachine extends TileEntity {
  private int energy, progress; 
  public void updateEntity() { if(!worldObj.isRemote && canProcess()) process(); }
}
```

## Renderer Kernrotation
Siehe `RENDERING.md` – Rotation aus `meta - 10`.

## Erweiterte Features
- Inventar (`ISidedInventory`)
- Flüssigkeiten (`IFluidStandardReceiver`)
- GUI (`IGUIProvider`)

## NBT Muster
`energy`, `progress`, Inventar-Schleifen, Tanks per Tag.

## Prüf-Checkliste
| Punkt | OK? |
|-------|-----|
| Metadaten korrekt | |
| NBT vollständig | |
| Performance | |
| Dummy-Platzierung | |

## Querverweise
`QUICK_REFERENCE.md` • `MULTIBLOCKS.md` • `API.md`
