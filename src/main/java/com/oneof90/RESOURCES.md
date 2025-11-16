# RESOURCES.md – Assets & Ressourcenverwaltung (1of90)

## Ziel
Definiert Namenskonventionen, Ablageorte und Empfehlungen für Texturen, Modelle, Lokalisierung und zukünftige Datenressourcen.

## Verzeichnisübersicht (assets)
```
assets/hbm/
├── textures/
│   ├── blocks/            → Blocktexturen (Standard Tiles)
│   ├── items/             → Item Icons
│   └── models/            → TESR / 3D Modelltexturen
├── models/                → Wavefront .obj Modelle
├── lang/                  → Sprachdateien (en_US.lang, de_DE.lang ...)
└── sounds/ (optional)     → Audiodateien (Vorfix 1of90_ für Konfliktfreiheit)
```

## Namensschema
| Typ | Pattern | Beispiel |
|-----|---------|----------|
| Blocktextur | `1of90_<funktion>.png` | `1of90_steel_beam.png` |
| Itemtextur | `1of90_<item>.png` | `1of90_circuit_plate.png` |
| Modelltextur | `1of90_<modell>.png` | `1of90_multiblock_core.png` |
| OBJ Modell | `<funktion>_1of90.obj` | `fusion_chamber_1of90.obj` |
| Sound | `1of90_<sound>.ogg` | `1of90_machine_hum.ogg` |
| Sprachschlüssel | `tile.<name>.name` | `tile.machine_my_multiblock_1of90.name` |

## Modellintegration
Wavefront (.obj) Modelle werden extern erstellt (Blender etc.).
Empfehlungen:
- Ursprung (0,0,0) zentriert auf Blockmitte.
- Skalierung an Vanilla Blockgröße anpassen (1 Einheit = 1 Block).
- Keine überflüssigen Dreiecke (Optimierung: Triangulation + Decimation bei komplexen Shapes).

## Texturhinweise
| Empfehlung | Vorteil |
|------------|--------|
| Power-of-Two Größe (64/128/256 px) | GPU-Kompatibilität |
| Einheitliche Farbpalette | Stil-Konsistenz |
| Kompression ohne Artefakte | Speicherplatz sparen |
| Alphakanal nur wo nötig | Reduktion von Overdraw |

## Mehrsprachigkeit
Sprachdateien erweitern:
```
# en_US.lang
tile.machine_my_multiblock_1of90.name=My Multiblock Machine
item.my_component_1of90.name=My Component
```
Internationalisierung:
- Konsistente Schlüssel (Prefix `tile.` / `item.` / `gui.`)
- Vermeidung harter Strings innerhalb von TileEntities / GUIs → immer Übersetzungs-Layer.

## Soundeinbindung (optional)
Registrierung über HBM Soundsystem oder Minecraft Basis. 
Konventionen:
- Kurze Loops für Maschinen (≤ 5s).
- Dezente Lautstärke (Mix mit vorhandenen HBM Effekten testen).

## Geplante Datenressourcen
| Ressource | Nutzen |
|-----------|-------|
| JSON Multiblock Definitionen | Datengesteuerte Struktur statt Hardcode |
| Rezept-JSON (Custom) | Vereinheitlichte Rezeptverwaltung |
| Energie-/Fluid-Konfiguration | Balancing ohne Codeänderung |

## Platzhalter / Fallback Strategie
Wenn Ressource fehlt:
- Renderer prüft `if(model == null)` → einfache Cube-Darstellung oder NOP.
- Fehlende Texturen: Temporäre Grautextur mit diagonaler Streifenkennung.
- Logausgabe (Debug): `logger.warn("1of90: Missing resource <pfad>");`

## Qualitätskontrolle
Checkliste vor Einbindung neuer Assets:
- [ ] Dateiname korrektes Präfix (`1of90_`)
- [ ] Größe angemessen (≤ 256px Standard)
- [ ] Alpha / Transparenz geprüft
- [ ] Keine überflüssigen leeren Pixelränder
- [ ] Lizenzkompatibilität (Eigenkreation oder Projektlizenz)

## Typische Fehler
| Fehler | Ursache | Lösung |
|--------|---------|-------|
| Pink/Schwarze Textur | Falscher Pfad / Name | ResourceLocation prüfen |
| Verzerrtes Modell | Falsche Skalierung / Ursprung | Modell neu zentrieren (Blender: Origin to Geometry) |
| Flackern bei Transparenz | Fehlendes Blending oder DepthSort | `GL11.glEnable(GL11.GL_BLEND)` + korrekte Reihenfolge |
| Hohe VRAM-Nutzung | Zu große Texturen | Downscaling oder Mipmaps aktivieren |

## Lade-Reihenfolge Empfehlung
1. Statische Ressourcen (Texturen, Modelle) beim Spielstart laden (ResourceManager Hook).
2. Dynamische / optionale Ressourcen nur bei Bedarf (Lazy).
3. Fehlermeldungen eindeutig, keine stillen NOPs bei kritischen Modellen.

## Beispiel ResourceLocation Nutzung
```java
bindTexture(new ResourceLocation("hbm", "textures/models/1of90_my_machine.png"));
```
Namespace bleibt `hbm` → Trennung erfolgt über Präfix im Dateinamen.

## Roadmap (Assets)
| Feature | Nutzen |
|---------|-------|
| Automatisches Asset-Validierungs-Skript | Früher Fehlerfund |
| Spriteatlas für häufige Icons | Weniger Bind-Aufrufe |
| Dynamische Farbvarianten (Shader/Palette-Swap) | Mehr Vielfalt ohne extra Dateien |
| Emissive Layer für Leuchtelemente | Realistischere Maschinenoptik |

## Abschluss
Assets bilden die visuelle Identität der 1of90 Erweiterung. Konsistente Benennung und leichtgewichtige Dateien sichern stabile Performance und erleichtern spätere Skalierung.

### Querverweise
- `RENDERING.md` für Nutzung der Texturen & Modelle
- `EXTENDING.md` für Einbindung neuer Feature-Assets
- `TROUBLESHOOTING.md` für Fehler bei Ressourcen
