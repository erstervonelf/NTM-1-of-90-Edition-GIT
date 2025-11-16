# MULTIBLOCKS.md – Detaillierte Multiblock-Dokumentation (1of90)

## Überblick
Multiblock-Maschinen bündeln mehrere Blockpositionen zu einer logischen Einheit. Das 1of90 Paket verwendet HBM's `BlockDummyable` als Basis zur Strukturvalidierung und Dummy-Platzierung.

## Kernprinzipien
| Prinzip | Beschreibung |
|---------|--------------|
| Trennung | Validierung und Platzierung in unterschiedlichen Methoden |
| Leichtgewicht | Dummy-Blöcke besitzen keine eigenen TileEntities (Meta < 12) |
| Kernblock | Nur ein zentraler Kern verwaltet Logik & Zustand |
| Metadaten | Rotation & Kernstatus über Metadaten kodiert |

## Metadaten-Layout
| Bereich | Beschreibung |
|---------|--------------|
| 0–5 | Dummy-Rotation (Basisrichtungen) |
| 6–11 | Erweiterte Dummy-Metadaten (Spezialanschlüsse) |
| 12–15 | Kernblock (N,S,W,E) → Rotation = `metadata - 10` |

## Ablauf bei Platzierung
1. Spieler interagiert / platziert Block.
2. `checkRequirement(world, x, y, z, dir, offset)` prüft freien Raum.
3. Bei `true` wird Kernblock gesetzt (`dir.ordinal() + 10`).
4. `fillSpace(...)` platziert Dummy-Blöcke (Meta = `dir.ordinal()`).
5. TileEntity am Kern initialisiert internen Zustand.

## Beispiel Minimaler Multiblock
```java
public class MachineMyMultiblock extends BlockDummyable {
    public MachineMyMultiblock(Material mat) { super(mat); }
    @Override public int[] getDimensions() { return new int[]{3,3,3}; }
    @Override public int getOffset() { return 1; }
    @Override
    protected boolean checkRequirement(World w, int x, int y, int z, ForgeDirection dir, int o) {
        int[] d = getDimensions();
        int coreX = x + dir.offsetX * o;
        int coreY = y + dir.offsetY * o;
        int coreZ = z + dir.offsetZ * o;
        for(int a=-d[0]/2; a<=d[0]/2; a++)
            for(int b=0; b<d[1]; b++)
                for(int c=0; c<d[2]; c++) {
                    int px = coreX + a;
                    int py = coreY + b;
                    int pz = coreZ + c;
                    if(!w.getBlock(px, py, pz).isReplaceable(w, px, py, pz)) return false;
                }
        return true;
    }
    @Override
    public void fillSpace(World w, int x, int y, int z, ForgeDirection dir, int o) {
        int[] d = getDimensions();
        int coreX = x + dir.offsetX * o;
        int coreY = y + dir.offsetY * o;
        int coreZ = z + dir.offsetZ * o;
        for(int a=-d[0]/2; a<=d[0]/2; a++)
            for(int b=0; b<d[1]; b++)
                for(int c=0; c<d[2]; c++) {
                    int px = coreX + a;
                    int py = coreY + b;
                    int pz = coreZ + c;
                    if(px == coreX && py == coreY && pz == coreZ) continue; // Kern überspringen
                    w.setBlock(px, py, pz, this, dir.ordinal(), 3);
                }
        w.setBlockMetadataWithNotify(coreX, coreY, coreZ, dir.ordinal() + 10, 3);
    }
}
```

## TileEntity am Kern
```java
public class TileEntityMyMultiblock extends TileEntity {
    private int progress;
    private int maxProgress = 200;
    private long energy;
    private long maxEnergy = 100000;
    @Override
    public void updateEntity() {
        if(worldObj.isRemote) return; // Nur Serverlogik
        if(energy >= 100) { progress++; energy -= 100; }
        if(progress >= maxProgress) { progress = 0; onProcessComplete(); }
    }
    private void onProcessComplete() { /* Output, Effekte, etc. */ }
}
```

## Rotation im Renderer
```java
int meta = te.getBlockMetadata();
if(meta >= 12) {
    float rot = switch(meta - 10) {
        case 2 -> 0f; case 3 -> 180f; case 4 -> 90f; case 5 -> 270f; default -> 0f;
    };
    GL11.glRotatef(rot, 0, 1, 0);
}
```

## Häufige Fehler & Lösungen
| Problem | Ursache | Lösung |
|---------|---------|-------|
| Platzierung fehlschlägt | Raumprüfung zu streng/falsch | Replaceable-Logik prüfen (Erde/Gras erlaubt?) |
| Dummy überschreibt wichtige Blöcke | Keine Freiraumvalidierung | `checkRequirement` erweitern |
| Rotation falsch | Falsche Metadaten gesetzt | Kern = `dir.ordinal() + 10` sicherstellen |
| TE fehlt | Meta < 12 beim Kern | Block-Meta beim Setzen prüfen |
| Performance schwach | Großes d³ bei jeder Tickprüfung | Struktur nur einmal validieren, Ergebnis cachen |

## Best Practices
- Strukturbildung nur einmal; spätere Integrität optional prüfen.
- Falls Struktur > 7×7×7: Chunk-Grenzen dokumentieren und testweise validieren.
- NBT Minimal halten – keine großen Arrays pro Tick modifizieren.
- Dummy-Blöcke ohne TileEntity belassen (Speicher sparen).

## Erweiterte Strukturvarianten
| Variante | Beschreibung | Hinweis |
|----------|--------------|--------|
| Kompakt (3×3×3) | Geringe Ressourcen, schneller Aufbau | Ideal für Testmaschinen |
| Flach (5×3×5) | Mehr Flächenbedarf, guter Zugriff | GUI-Zugriff zentral halten |
| Turm (3×n×3) | Vertikale Tiefe, segmentiertes Fortschrittssystem | Höhe limitieren (Serverlast) |
| Modular (Kern + Erweiterungsringe) | Erweiterbare Funktionalität | Zusätzliche Kompatibilitätschecks |

## Struktur-Validierung optimieren
- Frühzeitiger Abbruch bei erstem blockierten Block.
- Bounding Box vor Iteration berechnen.
- Optional: Vorabliste akzeptierter Replaceable-Blöcke cachen.

## Geplante Verbesserungen
| Idee | Nutzen |
|------|-------|
| JSON definierte Struktur | Einfacheres Hinzufügen ohne Codeänderung |
| Selbstheilungsroutine | Ersetzt beschädigte Dummy-Blöcke |
| Dynamische Kernverlagerung | Strukturmodifikation im Betrieb |

## Debug-Strategie
1. Mit F3 Metadaten prüfen (Kern >=12?).
2. Strukturrahmen temporär einfärben (Entwicklungshelfer / DebugBlock).
3. Stückweise Aufbau testen (erst 3×3×1, dann weitere Ebenen).
4. Log-Ausgaben in `checkRequirement` bei Fehlschlag.

## Beispiel: Erweiterte fillSpace (mit Hohlraum)
```java
public void fillSpace(...) {
    int[] d = getDimensions();
    int coreX = ...; int coreY = ...; int coreZ = ...;
    for(int a=-d[0]/2; a<=d[0]/2; a++)
        for(int b=0; b<d[1]; b++)
            for(int c=0; c<d[2]; c++) {
                boolean hohlraum = (a == 0 && c == 0 && b > 0); // Kanal freilassen
                if(hohlraum) continue;
                // Platzierung wie üblich
            }
    world.setBlockMetadataWithNotify(coreX, coreY, coreZ, dir.ordinal()+10, 3);
}
```

## Sicherheit / Konsistenz
- Niemals Kernel manipulieren ohne strukturelle Konsistenz.
- Keine direkten Blockupdates spammen – `world.setBlock` sparsam nutzen.
- Bei Abbau des Kernblocks Dummy-Blöcke via Schleife entfernen (optional Rückgabeitems).

## Abbau / Demontage Muster
```java
public void dismantle(World w, int coreX, int coreY, int coreZ) {
    int[] d = getDimensions();
    for(int a=-d[0]/2; a<=d[0]/2; a++)
        for(int b=0; b<d[1]; b++)
            for(int c=0; c<d[2]; c++) {
                int px = coreX + a; int py = coreY + b; int pz = coreZ + c;
                Block bRef = w.getBlock(px, py, pz);
                if(bRef == this) w.setBlockToAir(px, py, pz);
            }
}
```

## Abschluss
Die Multiblock-Infrastruktur eröffnet flexible Maschinenaufbauten mit kontrollierter Komplexität. Konsistente Anwendung der hier beschriebenen Muster sorgt für Stabilität, gute Performance und wartbare Erweiterungen.

### Querverweise
- `ARCHITECTURE.md` für Gesamtzusammenhang
- `RENDERING.md` für Rotations-Handling im TESR
- `TROUBLESHOOTING.md` für Fehlerbehebung bei Strukturproblemen
