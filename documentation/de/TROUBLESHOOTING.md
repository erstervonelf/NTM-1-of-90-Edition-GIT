# TROUBLESHOOTING – Fehleranalyse & Lösungen (1of90)

Inhalt identisch zur bisherigen deutschen Version aus dem Quellverzeichnis `src/main/java/com/oneof90/TROUBLESHOOTING.md`. Die Datei wurde hierher verschoben, um Code und Dokumentation zu trennen.

## Zweck
Schnelle Hilfe bei typischen Problemen mit Blöcken, TileEntities, Multiblocks, Rendering und Ressourcen.

## Übersicht nach Kategorien
| Kategorie | Typische Probleme |
|-----------|-------------------|
| Platzierung | Struktur passt nicht, falsche Rotation |
| TileEntity | Kein Tick, Datenverlust, NBT Fehler |
| Rendering | Falsche Ausrichtung, fehlende Textur |
| Ressourcen | Pink/Schwarz Fehler, falsche Pfade |
| Performance | Lag, hoher Tick-Aufwand |
| Netzwerk / GUI | GUI öffnet nicht, falsche Werte |

---
## Platzierungsprobleme
| Symptom | Ursache | Lösung |
|---------|---------|-------|
| Multiblock reagiert nicht | `checkRequirement()` liefert false | Debug-Ausgaben hinzufügen, Replaceable-Blöcke prüfen |
| Kern hat falsche Rotation | Meta nicht `dir.ordinal() + 10` | Setzen in `fillSpace()` anpassen |
| Dummy-Blöcke fehlen | Schleife bricht zu früh ab | Iterationsgrenzen der Dimensionen prüfen |
| Struktur verschiebt sich | Falscher Offset | `getOffset()` korrekt zurückgeben (meist 1) |

### Debug-Tipp
```java
player.addChatMessage(new ChatComponentText("Check failed at x=" + xPos));
```

---
## TileEntity Probleme
| Symptom | Ursache | Lösung |
|---------|---------|-------|
| TE existiert nicht | Meta < 12 (Dummy statt Kern) | Kern-Metadaten prüfen |
| Kein Fortschritt | `canProcess()` false | Logik / Energie / Input prüfen |
| Energie springt | Überlauf in Berechnung | Max-Werte begrenzen (`Math.min`) |
| Datenverlust nach Reload | NBT Schlüssel fehlen | Alle relevanten Felder speichern |
| Inventar leert sich | `readFromNBT` fehlerhaft | Slot-Schleife & Tag-Namen prüfen |

### NBT Überprüfung
```java
System.out.println(nbt);
```

---
## Rendering Probleme
| Symptom | Ursache | Lösung |
|---------|---------|-------|
| Modell rotiert falsch | Falscher Metawert | `rotation = meta - 10` anwenden |
| Textur fehlt | Pfad falsch | ResourceLocation prüfen |
| Modell flackert | Mehrfaches Rendern | Früh `return` bei Meta < 12 |
| Schatten seltsam | Lighting deaktiviert | `GL11.glEnable(GL11.GL_LIGHTING)` |
| Durchsichtige Teile schwarz | Blend fehlt | `GL11.glEnable(GL11.GL_BLEND)` + BlendFunc |

---
## Ressourcen Probleme
| Symptom | Ursache | Lösung |
|---------|---------|-------|
| Pink/Schwarz Checkers | Falscher Texturpfad | Dateiname + Namespace prüfen |
| Falsches Modellscaling | Ursprung nicht korrekt | Modell in Blender zentrieren |
| Sound spielt endlos | Loop nicht gestoppt | Abbruchbedingung im Tick ergänzen |
| Lokalisierung fehlt | Schlüssel fehlt | `lang/en_US.lang` ergänzen |

---
## Performance Probleme
| Symptom | Ursache | Lösung |
|---------|---------|-------|
| Lag bei vielen Maschinen | Jede Maschine tickt | `canUpdate()` konditional machen |
| Hohe CPU Nutzung | Große Scans jeden Tick | Cache + einmalige Validierung |
| FPS Einbruch | Alle Dummy-Blöcke rendern | Nur Kern rendern |
| Speicheranstieg | Sammlungen wachsen | Periodisches Bereinigen |

---
## Netzwerk / GUI Probleme
| Symptom | Ursache | Lösung |
|---------|---------|-------|
| GUI öffnet nicht | Falscher Seitentest | `if(!world.isRemote)` beim Öffnen |
| Falsche Werte | Keine Sync | Server→Client Packet bei Änderung |
| Crash GUI | Nullpointer | Slots prüfen/initialisieren |
| Kein Fortschritt sichtbar | Progress nicht übertragen | Getter + Sync Packet |

---
## Allgemeine Debugstrategie
1. Minimale Reproduktion
2. Metadaten (F3) prüfen
3. NBT vor/nach Reload loggen
4. Renderer vereinfachen
5. Schrittweises Reaktivieren

## Prüfliste
- [ ] Keine `System.out.println` Reste
- [ ] NBT vollständig
- [ ] Kern-Meta korrekt (≥12)
- [ ] Ressourcen vorhanden
- [ ] Performance ok
- [ ] Docs aktuell

## Querverweise
`MULTIBLOCKS.md` • `RENDERING.md` • `EXTENDING.md`
