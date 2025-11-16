# ARCHITECTURE.md – 1of90 Paketarchitektur

## Ziel
Dieses Dokument vertieft die Architektur des 1of90-Erweiterungspakets und beschreibt Schichten, Abhängigkeiten und typische Datenflüsse.

## Übersicht (Layer)
```
+--------------------------------------------------+
|                HBM Core (Upstream)              |
|  MainRegistry / ClientProxy / Infrastruktur     |
+--------------------------+-----------------------+
                           ^ (Hooks)
                           |
+--------------------------------------------------+
|                1of90 Integration Layer           |
|  MainRegistry1of90 / ModBlocks1of90              |
+--------------------------+-----------------------+
                           ^
                           |
+--------------------------------------------------+
|             1of90 Feature Modules                |
|  blocks/  tileentity/  render/  machine/         |
+--------------------------------------------------+
```

## Komponentenbeschreibung
| Komponente | Aufgabe | Typische Dateien |
|------------|---------|------------------|
| Integration Layer | Einstieg, Registrierungen | `MainRegistry1of90`, `ModBlocks1of90` |
| Block-Layer | Platzierung, Metadaten, Strukturvalidierung | `NTMSteelBeam`, `MachineExampleMultiblock` |
| TileEntity Layer | Persistente Zustände, Logik, Verarbeitung | `TileEntityExampleMultiblock` |
| Renderer Layer | Visuelle Darstellung, Rotation, Modelle | `RenderExampleMultiblock` |

## Datenfluss Beispiel (Multiblock)
1. Spieler platziert Kernblock (oder Klick zur Strukturbildung).
2. `onBlockPlacedBy` (von `BlockDummyable`) → ruft `checkRequirement()`.
3. Bei Erfolg → `fillSpace()` setzt Dummy-Blöcke + Kern-Metadaten.
4. TileEntity (Kern) initialisiert Zustand (`energy = 0`, etc.).
5. Tick-Zyklus: `updateEntity()` berechnet Fortschritt, speichert NBT.
6. Renderer liest Metadaten ≥12 zur Rotationsberechnung und rendert Modell.

## Metadatenstrategie
- Dummy: 0–5 / 6–11 für Sonderfälle
- Kern: 12–15 (Rotation ableitbar über `metadata - 10` → 2–5)

## Persistenz & Synchronisation
| Bereich | Mechanismus |
|---------|-------------|
| Energie / Fortschritt | NBT: `writeToNBT` / `readFromNBT` |
| Client-Rendering | Metadaten (Blockstate alt) |
| Inventar | NBT + ggf. Container/GUI | 
| Netzwerk (optional) | HBM PacketDispatcher oder Forge-Packets |

## Erweiterbarkeit
Neue Module folgen dem Muster:
1. Blockklasse erstellen (ggf. `extends BlockDummyable`).
2. TileEntity für Logik.
3. Renderer (nur falls Spezial-Darstellung).
4. Registrierung im Integration Layer.
5. Ressourcen + Lokalisierung.

## Abhängigkeitsregeln
- Feature-Module dürfen **nicht** direkt HBM interne Registrierlisten modifizieren.
- Kommunikation nur über offizielle Interfaces (z.B. Energie, Fluidhandlung).
- Renderer dürfen keine Spielzustände mutieren.

## Fehlerresistenz
- Strukturvalidierung bricht früh ab (Performance).
- Defensive Nullprüfungen bei Weltzugriff.
- Keine Annahmen über Inventory-Indizes ohne Bounds-Check.

## Performance-Ziele
| Ziel | Maßnahme |
|------|----------|
| Minimale Renderkosten | Nur Kernblöcke rendern |
| Wenige Netzwerkpakete | Zustand nur bei Änderung senden |
| Reduzierte Ticklast | Nicht benötigende Tiles: `canUpdate() = false` |
| Schnelle Strukturprüfung | Iteration & frühzeitiger Abbruch |

## Sicherheit / Stabilität
- Kein reflektives Schreiben in HBM private Felder.
- Keine Blockersetzung ohne Replaceable-Check.
- Validierung vor Dummy-Platzierung.

## Roadmap-Vorschläge (Architektur)
- Abstrakte Basisklasse `AbstractMultiblockTileEntity` für standardisierte Energie/Progress/Inventory.
- Strukturdefinitionen als Daten (JSON) → dynamisches Parsen & Platzierung.
- Event-Hooks für Maschinenzustandsänderungen (Start/Stop/Complete).

## Diagramm – Multiblock Lebenszyklus
```
[Player Action]
     |
     v
[BlockDummyable.onBlockPlacedBy]
     |
     +--> checkRequirement() --(false)--> [Abbruch + Rückgabe Item]
     |                    
     +--(true)--> fillSpace() -> Set Dummy + Kern Meta
                          |
                          v
                [TileEntityExampleMultiblock.init]
                          |
                     Tick Loop (updateEntity)
                          |
               +----------+-----------+
               |                      |
           Rendering (meta>=12)   NBT Persistenz
```

## FAQ (Architektur)
| Frage | Antwort |
|-------|---------|
| Warum eigene Präfixe? | Vermeidung von Ressourcen-Konflikten & klare Herkunft. |
| Warum Metadaten statt BlockState? | MC 1.7.10 Limitierung – Metadaten sind kompakt & etabliert. |
| Wie erkenne ich Kernblock schnell? | `meta >= 12` genügt. |
| Kann man mehrere Kerne nutzen? | Nicht empfohlen – eine Kern-TileEntity pro Struktur für Übersichtlichkeit. |

## Abschluss
Diese Architekturdefinition soll konsistente Erweiterungen sicherstellen. Vor komplexen Neuerungen zuerst prüfen, ob bestehende Patterns (Beispiel-Multiblock) wiederverwendet werden können.

### Querverweise
- Siehe `API.md` für Interface-Details.
- Siehe `MULTIBLOCKS.md` für Strukturvalidierung & Beispiele.
- Siehe `RENDERING.md` für Rotations-/TESR-Muster.
