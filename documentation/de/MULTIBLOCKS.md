# MULTIBLOCKS (DE, verschoben)

Übernahme der detaillierten Multiblock-Dokumentation. Strukturprinzipien, Dummy/Kern, Metadaten, Validierung.

## Kernpunkte
- Dummy ohne TileEntity
- Kern steuert Logik
- Metadaten 12–15 Rotation

## Validierungsablauf
1. Spieleraktion
2. `checkRequirement`
3. `fillSpace`
4. Kern gesetzt
5. TE init

## Performance
Früh abbrechen, Dummy sparsam setzen, Struktur ggf. cachen.

## Häufige Fehler Kurzliste
| Problem | Lösung |
|---------|-------|
| Rotation falsch | Kernmeta = dir.ordinal()+10 |
| TE fehlt | Meta <12 am Kern | 
| Platzierung scheitert | Replaceable-Check erweitern |

## Querverweise
`RENDERING.md` • `ARCHITECTURE.md` • `TROUBLESHOOTING.md`
