# EXTENDING.md – Erweiterungsrichtlinien (verschoben)

Übernommen aus ursprünglicher Datei zur sauberen Dokumentationsstruktur.

## Erweiterungstypen
| Typ | Beispiel | Zweck |
|-----|---------|-------|
| Dekorativ | Stahlträger | Optik |
| Einzelmaschine | Kompressor | Verarbeitung |
| Multiblock | Reaktor | Komplexe Logik |
| Infrastruktur | Kabel | Verteilung |
| GUI/Interaktion | Bedienpult | Steuerung |

## Planungscheck
1. Ziel definieren
2. Ressourcenbedarf abschätzen
3. Daten (Energie/Inventar/Fluids) festlegen
4. Interfaces wählen
5. Modularität prüfen

## Beispiel Schritte
Block → TileEntity → (Renderer) → Registrierung → Assets → Lokalisierung → GUI → Dokumentation.

## Fortschrittssystem
Siehe Originalcode: `progress`, `maxProgress`, `canProcess()`; Energieverbrauch begrenzen.

## Balancing-Tabelle
| Parameter | Empfehlung |
|-----------|-----------|
| Energie | Skaliert mit Seltenheit |
| Dauer | 5–30s |
| Inventar | Klar getrennt |
| Upgrades | 1–4 Slots |

## Fehlerresistenz
- Nullprüfungen Inventar
- `canUpdate()` nur wenn nötig
- Fortschritt nie negativ

## Logging
`if(DEBUG && !worldObj.isRemote)` für gezielte Ausgaben.

## Deaktivierbarkeit
Konfig-Flag vor Registrierung prüfen.

## Netzwerk
Nur State-Änderungen senden.

## Checkliste vor Einbindung
| Check | Erledigt? |
|-------|-----------|
| Registrierungen |  |
| NBT Persistenz |  |
| Ressourcen korrekt |  |
| Performance OK |  |
| Doku aktualisiert |  |

## Querverweise
`API.md` • `MACHINE_CREATION_GUIDE.md` • `QUICK_REFERENCE.md` • `TROUBLESHOOTING.md`
