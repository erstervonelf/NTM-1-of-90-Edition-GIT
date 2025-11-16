# ARCHITECTURE.md – 1of90 Paketarchitektur (verschoben)

Inhalt übernommen aus `src/main/java/com/oneof90/ARCHITECTURE.md` zur Trennung von Code und Dokumentation.

## Ziel
Vertiefung der Architektur des 1of90-Erweiterungspakets: Schichten, Abhängigkeiten, Datenflüsse.

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
| Block-Layer | Platzierung, Metadaten, Struktur | `NTMSteelBeam`, `MachineExampleMultiblock` |
| TileEntity Layer | Persistenz, Prozesslogik | `TileEntityExampleMultiblock` |
| Renderer Layer | Visuelle Darstellung | `RenderExampleMultiblock` |

## Datenfluss (Multiblock)
1. Kernblock platziert → `checkRequirement()`
2. `fillSpace()` setzt Dummy + Kernmeta
3. TileEntity init
4. Tick: Logik & Fortschritt
5. Renderer nutzt Metadaten ≥12

## Metadatenstrategie
- Dummy: 0–11
- Kern: 12–15 (Rotation = `meta - 10` → Werte 2–5)

## Persistenz & Sync
| Bereich | Mechanismus |
|---------|-------------|
| Energie/Fortschritt | NBT read/write |
| Rendering | Metadaten |
| Inventar | NBT + Container |
| Netzwerk | Packets optional |

## Erweiterbarkeit
Muster: Block → TE → Renderer → Registrierung → Ressourcen.

## Abhängigkeitsregeln
- Keine direkten Core-Modifikationen
- Nutzung offizieller Interfaces
- Renderer ohne Zustand-Manipulation

## Performance-Ziele
| Ziel | Maßnahme |
|------|----------|
| Wenig Renderkosten | Nur Kern rendern |
| Minimale Netzlast | Zustandsänderungen bündeln |
| Reduzierte Ticklast | `canUpdate()` selektiv |
| Schnelle Prüfung | Früher Abbruch bei Fehler |

## Sicherheit
- Replaceable prüfen vor Platzierung
- Nullchecks bei Weltzugriff
- Keine reflektiven Eingriffe

## Roadmap (Architektur)
- `AbstractMultiblockTileEntity`
- JSON Strukturen
- Maschinenzustands-Hooks

## Lebenszyklusdiagramm
```
[Player Action]
     -> [onBlockPlacedBy] -> checkRequirement() -> fillSpace() -> TE init -> Tick Loop -> Rendering + Persistenz
```

## FAQ
| Frage | Antwort |
|-------|---------|
| Kern erkennen? | `meta >= 12` |
| Mehrere Kerne? | Vermeiden, Komplexität steigt |
| Warum Präfixe? | Ressourcenkonflikte vermeiden |

## Querverweise
`MULTIBLOCKS.md` • `RENDERING.md` • `API.md`
