# 1of90 Edition – Dokumentation

Die ausführliche Dokumentation wurde in den Ordner `documentation/` ausgelagert und sprachlich getrennt:

- Deutsche Dateien: `documentation/de/`
- Englische Dateien: `documentation/en/`

Bitte dort nachsehen für vollständige Guides (Maschinen, Multiblocks, API, Kompatibilität, Troubleshooting). Dieses README bleibt eine Kurzübersicht.

---
## Inhaltsverzeichnis
1. Überblick & Ziele
2. Paketstruktur
3. Architektur (High-Level)
4. Lebenszyklus & Initialisierung
5. Registrierungs-Workflow
6. Maschinen & Multiblocks (Kurzfassung)
7. Rendering & Modelle
8. Ressourcen & Naming
9. Öffentliche Erweiterungspunkte / API
10. Standards & Konventionen
11. Fehlerbehebung & Diagnose
12. Performance-Hinweise
13. Sicherheits- & Stabilitätsaspekte
14. Roadmap / Erweiterungsvorschläge
15. Weiterführende Detaildokumente

---
## 1. Überblick & Ziele
Das 1of90-Verzeichnis erlaubt das schrittweise Hinzufügen neuer Gameplay-Elemente (z.B. dekorative Blöcke, Maschinen, komplexe Multiblock-Strukturen) mit minimaler Kopplung zu HBM. Änderungen erfolgen ausschließlich über klar definierte Integrationspunkte, wodurch Updates des Hauptmods einfacher bleiben.

Primäre Ziele:
- Isolation: Keine direkten Modifikationen im HBM‑Core außer Initialisierung.
- Erweiterbarkeit: Schnelles Hinzufügen neuer Feature-Module.
- Lesbarkeit: Klare, sprechende Dateinamen und konsistente Namenskonventionen.
- Wartbarkeit: Reduzierte Merge-Konfliktgefahr bei Upstream‑Updates.

---
## 2. Paketstruktur

```
com/oneof90/
├── blocks/            → Einzelblöcke & Basis-Maschinen
│   └── machine/       → Multiblock-spezifische Blockklassen (extends BlockDummyable)
├── tileentity/        → TileEntities für Logik & Zustand
│   └── machine/       → Komplexe Maschinen- / Multiblock-TEs
├── render/            → TESR / Spezialrenderer
│   └── machine/       → Renderer für Multiblock-Kern & Modelle
├── main/              → Zentraler Registrierungs-/Initialisierungseintritt
├── MACHINE_CREATION_GUIDE.md  → Tiefgehende Erstellungsschritte
├── QUICK_REFERENCE.md         → Kompakte Referenz / Cheatsheet
└── (geplante) weitere Docs: ARCHITECTURE.md, API.md, …
```

---
## 3. Architektur (High-Level)
Kernidee: Jede Funktionseinheit (Block, TileEntity, Renderer) bleibt modular. Registrierungen erfolgen gebündelt in `ModBlocks1of90` und über `MainRegistry1of90.init()` sowie clientseitig `MainRegistry1of90.initClient()`. Multiblock-Strukturen nutzen HBM-Basisklassen wie `BlockDummyable` und adaptieren deren Lifecycle.

Wichtige Komponenten:
- Block-Schicht: Präsentation & Platzierungslogik
- TileEntity-Schicht: Serverseitige Status-/Prozesslogik, Persistenz (NBT)
- Renderer-Schicht: Clientseitige visuelle Darstellung (Rotation, Modelle)
- Integrations-Schicht: Hooks in HBM (`MainRegistry`, `ClientProxy`)

---
## 4. Lebenszyklus & Initialisierung
1. FML/Forge lädt HBM-Hauptmod.
2. Während der Haupt-Registrierungsphase ruft HBM `MainRegistry1of90.init()` auf → Blöcke & TileEntities werden registriert.
3. Clientseitig (nach allgemeiner Renderregistrierung) `MainRegistry1of90.initClient()` → TESR/Renderer-Bindings.
4. Laufzeit: TileEntities ticken (falls `canUpdate()` true), Multiblocks validieren ihre Struktur, Renderer liest Metadaten.

---
## 5. Registrierungs-Workflow (Kurz)
| Schritt | Ort | Aufgabe |
|--------|-----|---------|
| Block-Instanz erzeugen | `ModBlocks1of90.init()` | Eigenschaften setzen (Härte, Tab, Name) |
| Block registrieren | `registerBlocks()` | `GameRegistry.registerBlock(...)` |
| TileEntity registrieren | `registerTileEntities()` | `GameRegistry.registerTileEntity(...)` |
| Renderer binden | `MainRegistry1of90.initClient()` | `ClientRegistry.bindTileEntitySpecialRenderer(...)` |

Detailabläufe siehe `MACHINE_CREATION_GUIDE.md`.

---
## 6. Maschinen & Multiblocks (Kurzfassung)
Einfache Maschinen: `BlockContainer` + eigene TileEntity. 
Multiblocks: Verwenden `BlockDummyable` → `checkRequirement()` prüft freien Raum, `fillSpace()` platziert Dummy-Blöcke (Metadaten 0–11), Kernblock nutzt Metadaten 12–15 (Rotation). Beispiele: `MachineExampleMultiblock`, `TileEntityExampleMultiblock`.

Metadaten-Konzept:
- Dummy: 0–5 Grundrichtungen, 6–11 Sonderfälle
- Kern: 12–15 (N/S/W/E) für Renderer-Rotation (Berechnung `metadata - 10`).

---
## 7. Rendering & Modelle
Grundpattern TESR:
```java
int meta = te.getBlockMetadata();
if(meta >= 12) { // Kernblock
    float rot = switch(meta - 10) {
        case 2 -> 0f; case 3 -> 180f; case 4 -> 90f; case 5 -> 270f; default -> 0f;
    };
    GL11.glRotatef(rot, 0, 1, 0);
    bindTexture(new ResourceLocation("hbm", "textures/models/1of90_example.png"));
    // model.renderAll();
}
```
Wichtig: Performance – nur Kernblock rendern, früh abbrechen bei Dummy-Metadaten <12.

---
## 8. Ressourcen & Naming
Präfixe & Suffixe reduzieren Konflikte:
- Texturen: `1of90_*.png`
- Blocknamen: `*_1of90`
- TileEntity-Namen: `tile_*_1of90`
- Sprachdateien: Einträge in `assets/hbm/lang/*` → Eindeutigkeit wahren.
Verzeichnisziele: Modelle/Textures innerhalb `assets/hbm/…` (keine eigenen Namespaces nötig, da Präfix trennt).

---
## 9. Öffentliche Erweiterungspunkte / API
Erweiterbare Bereiche:
- Neue Block-/Maschinenklassen in `blocks/` oder `blocks/machine/`
- TileEntities inkl. Energie-/Fluid-Interfaces (z.B. `IEnergyReceiverMK2`, `IFluidStandardReceiver`)
- GUI-Anbindung über `IGUIProvider`
- Renderer via TESR-Binding (`ClientRegistry.bindTileEntitySpecialRenderer`)

Empfohlene Vorgehensweise für neue Feature-Module:
1. Block + TE + Renderer entwerfen
2. Registrierungen hinzufügen
3. Ressourcen erstellen & benennen
4. Sprach- und ggf. Rezeptintegration ergänzen

---
## 10. Standards & Konventionen
- Keine Logik in Renderer (nur Darstellung)
- Serverlogik: Schutz mit `if(!worldObj.isRemote)`
- NBT: Konsistente Schlüssel (`energy`, `progress`, `isActive` …)
- Keine direkten Upstream-Klassen überschreiben, nur nutzen
- Methoden klar trennen: Validierung (`checkRequirement`) vs. Platzierung (`fillSpace`)

---
## 11. Fehlerbehebung & Diagnose (Kurz)
| Problem | Ursache | Lösung |
|---------|---------|-------|
| Multiblock platziert nicht | `checkRequirement` liefert `false` | Freiraum prüfen, Replaceable-Checks anpassen |
| Kein Renderer sichtbar | Falsche Metadaten (<12) | Kern-Meta setzen: `dir.ordinal() + 10` |
| Rotation falsch | Berechnung fehlerhaft | `rotation = metadata - 10` und Mapping prüfen |
| Inventar verliert Items | NBT Save/Load unvollständig | `writeToNBT`/`readFromNBT` für alle Slots implementieren |
| Performanceeinbruch | Unnötiges Ticken | `canUpdate()` auf `false`, Tick-Frequenzen reduzieren |

Ausführliche Liste siehe `TROUBLESHOOTING.md`.

---
## 12. Performance-Hinweise
- TESR nur für Kernblöcke
- Komplexe Berechnungen cachen (Rotation, Strukturvalidierung)
- Flüssigkeits-/Energie-Updates batchen statt Tick-für-Tick
- Netzwerk: Nur senden wenn Zustand tatsächlich geändert

---
## 13. Sicherheits- & Stabilitätsaspekte
- Defensive Checks bei Weltzugriff (Block existiert? Null vermeiden)
- Keine Annahmen über ItemStack-Inhalte → Validierung
- Strukturvalidierung früh abbrechen bei erstem Fehler
- Auf Chunk-Grenzen achten (Große Multiblocks testen)

---
## 14. Roadmap / Erweiterungsvorschläge
- Energie-Overlay für Kern-TileEntities
- Vereinheitlichte Rezeptregistrierung für 1of90 eigene Maschinen
- Abstrakte Basisklasse für häufige Multiblock-Muster
- Automatisierte Selbstreparatur fehlerhafter Dummy-Blöcke
- Optionales Datenexport-Interface (z.B. für externe Analyse)

---
## 15. Weiterführende Detaildokumente
Siehe Sprachordner:

DE (`documentation/de/`):
- MACHINE_CREATION_GUIDE.md
- QUICK_REFERENCE.md
- ARCHITECTURE.md (falls vorhanden)
- API.md
- MULTIBLOCKS.md
- RENDERING.md
- RESOURCES.md
- TROUBLESHOOTING.md
- COMPATIBILITY_API_TUTORIAL.md

EN (`documentation/en/`):
- TROUBLESHOOTING.md
- COMPATIBILITY_API_TUTORIAL.md
(Weitere Übersetzungen folgen bei Bedarf.)

---
## Beispiel-Templates

Referenzimplementierungen:
- Einfache Blöcke: `NTMSteelBeam`, `NTMSteelBeamVertical`
- Multiblock: `MachineExampleMultiblock`, `TileEntityExampleMultiblock`, `RenderExampleMultiblock`

## Philosophie (Kurz)
Siehe Abschnitt 1 – ergänzt um: Konsistente Erweiterbarkeit, konfliktarme Updates & klare Verantwortlichkeiten.

## Aktueller Beispielinhalt
### Stahlträger
- `NTMSteelBeam` – Horizontaler Träger (Rotation)
- `NTMSteelBeamVertical` – Vertikaler Träger

## Neues hinzufügen (Kurz)
1. Klasse erstellen (`blocks/`, optional `machine/`)
2. Registrierung in `ModBlocks1of90.init()`
3. TileEntity & Renderer (falls nötig)
4. Assets + Sprachdatei + (optional) Rezepte
Ausführlich: `MACHINE_CREATION_GUIDE.md`.

## Integrationspunkte (Detail)
| Zeitpunkt | Methode | Ziel |
|----------|---------|------|
| PreInit / Init | `MainRegistry1of90.init()` | Block-/TileEntity-Registrierung |
| Client Init | `MainRegistry1of90.initClient()` | Renderer-Bindings |
| Laufzeit | TileEntity `updateEntity()` | Maschinenlogik |

## Ressourcen (Kurz)
Siehe Abschnitt 8; vertiefte Planung folgt in `RESOURCES.md`.

## Namenskonventionen (Erweitert)
- Klassen: Präfix `NTM` oder `Machine`, eindeutige Funktion
- Blöcke: Suffix `_1of90` → Konfliktfreiheit
- Texturen: Präfix `1of90_` → klare Zuordnung
- TileEntities: `TileEntity*` + sinnvolle Basis (`ExampleMultiblock`, `MyMachine`)

## Vorteile
Strikte Trennung von Code und Dokumenten (Versionierung, Übersetzbarkeit), konfliktarme Weiterentwicklung und klare Verantwortlichkeiten.
- Erweiterbar & modular deaktivierbar
- Skalierbar für komplexere Feature-Sets

---
## Abschluss
Dieses README fungiert als zentrales Portal. Für tiefe technische Details bitte die Unterdokumente (Guide, Quick Reference und geplante Dateien) nutzen. Pull Requests sollten neuen Code strikt in diesem Paket halten und Konventionstabellen beachten.

Bei Fragen oder Erweiterungsvorschlägen: Roadmap-Abschnitt konsultieren und konsistente Patterns übernehmen.
