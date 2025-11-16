# TROUBLESHOOTING.md – Fehleranalyse & Lösungen (1of90)

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
| Struktur verschiebt sich | Falscher Offset | `getOffset()` zurückgeben was erwartet (meist 1) |

### Debug-Tipp
Füge temporär Chat-Ausgaben hinzu:
```java
player.addChatMessage(new ChatComponentText("Check failed at x=" + xPos));
```

---
## TileEntity Probleme
| Symptom | Ursache | Lösung |
|---------|---------|-------|
| TE existiert nicht | Meta < 12 (Dummy statt Kern) | Kern-Metadaten prüfen |
| Kein Fortschritt | `canProcess()` liefert false | Logik / Energie / Input prüfen |
| Energie springt | Überlauf in Berechnung | Max-Werte begrenzen (`Math.min`) |
| Datenverlust nach Reload | NBT Schlüssel fehlen | Alle relevanten Felder speichern |
| Inventar leert sich | `readFromNBT` fehlerhaft | Slot-Schleife & Tag-Namen prüfen |

### NBT Überprüfung
NBT mit Tools oder Debug-Ausgabe inspizieren:
```java
System.out.println(nbt);
```

---
## Rendering Probleme
| Symptom | Ursache | Lösung |
|---------|---------|-------|
| Modell rotiert falsch | Falscher Metawert | `rotation = meta - 10` anwenden |
| Textur fehlt | Fehlerhafter Pfad | ResourceLocation überprüfen |
| Modell flackert | Mehrfaches Rendern | Früh `return` bei Meta < 12 |
| Schatten seltsam | Lighting deaktiviert | `GL11.glEnable(GL11.GL_LIGHTING)` setzen |
| Durchsichtige Teile schwarz | Blend fehlt | `GL11.glEnable(GL11.GL_BLEND)` + BlendFunc |

### Schnelltest
- Singleplayer Welt starten, F3: Metadaten beobachten.
- Fehlende Textur → Konsolenwarnung für ResourceLocation hinzufügen.

---
## Ressourcen Probleme
| Symptom | Ursache | Lösung |
|---------|---------|-------|
| Pink/Schwarz Checkers | Texturpfad falsch / Datei fehlt | Dateiname + Namespace prüfen |
| Falsches Modellscaling | Ursprung nicht korrekt | Modell in Blender zentrieren |
| Sound spielt endlos | Loop nicht gestoppt | Bedingung für Stop im Tick prüfen |
| Lokalisierung fehlt | Schlüssel nicht vorhanden | `lang/en_US.lang` ergänzen |

### Pfaddesign
```java
new ResourceLocation("hbm", "textures/models/1of90_my_multiblock.png")
```

---
## Performance Probleme
| Symptom | Ursache | Lösung |
|---------|---------|-------|
| Lag bei vielen Maschinen | Ticken jede Maschine ohne Bedarf | `canUpdate()` nur true wenn nötig |
| Hohe CPU Nutzung | Große Strukturscans jedes Tick | Struktur einmal validieren & Cache |
| Render FPS Einbruch | Alle Dummy-Blöcke gerendert | Nur Kernblöcke rendern |
| Speicheranstieg | Große Sammlungen ohne Clear | Listen/Maps regelmäßig bereinigen |

### Profilieren
- Reduziere Testwelt auf einzelne Maschine.
- Log-Ausgaben überwachen (Tick-Frequenz, Schleifenlängen).

---
## Netzwerk / GUI Probleme
| Symptom | Ursache | Lösung |
|---------|---------|-------|
| GUI öffnet nicht | `openGui` nicht auf Server ausgeführt | Aufruf nur `if(!world.isRemote)` |
| Falsche Werte im GUI | Sync fehlt | Server → Client Packet nach Änderung senden |
| Crash bei GUI | Nullpointer im Container | Slots initialisieren & prüfen |
| Kein Fortschritt sichtbar | Progress nicht an GUI übergeben | Getter implementieren & abfragen |

### Beispiel Packet (Pseudo)
```java
// Senden wenn energy/progress geändert
PacketDispatcher.sendToAllAround(new PacketMachineUpdate(x,y,z,energy,progress), targetPoint);
```

---
## Allgemeine Debugstrategie
1. Kleinste Reproduktion erstellen (nur neue Maschine in leerer Welt).
2. Metadaten mit F3 prüfen.
3. NBT vor und nach Weltreload loggen.
4. Renderer testweise minimalisieren (nur farbiger Würfel).
5. Schrittweise Teile wieder aktivieren, um Fehlerquelle zu isolieren.

---
## Schnelle Prüfliste vor Release
- [ ] Keine `System.out.println` Reste
- [ ] NBT vollständig (Energie, Fortschritt, Inventar)
- [ ] Metadaten korrekt gesetzt (Kern ≥ 12)
- [ ] Ressourcen vorhanden & korrekt benannt
- [ ] Performance akzeptabel (Benchmark: 50 Maschinen ohne starken Lag)
- [ ] Dokumentation aktualisiert

---
## Häufige Fallstricke
| Fallstrick | Erklärung | Prävention |
|------------|-----------|-----------|
| Verwechslung Dummy vs. Kern | Kern-Meta nicht gesetzt | Einheitliche Utility-Methode für Kernprüfung |
| Zu frühes Platzieren | Platzierung vor Validierung | Immer erst `checkRequirement()` |
| Inkonsistente Schlüssel | Unterschiedliche NBT-Namen | Zentral definierte Konstanten |
| Tick-Logik auf Client | Redundante Berechnung | Guard `if(worldObj.isRemote) return;` |

---
## Erweiterte Diagnose (Optional)
Ein Debug-Flag implementieren:
```java
public static final boolean DEBUG_MB = true;
if(DEBUG_MB) MainRegistry.logger.info("Multiblock scan OK");
```

World Inspector (Pseudo):
```java
for(int dy=0; dy<height; dy++)
  for(int dx=-r; dx<=r; dx++)
    for(int dz=-r; dz<=r; dz++)
      logBlock(coreX+dx, coreY+dy, coreZ+dz);
```

---
## Abschluss
Diese Fehlerliste unterstützt schnelle Problemlösung und Qualitätssicherung. Bei neuen Fehlerklassen bitte dieses Dokument erweitern, um langfristige Wartbarkeit zu sichern.

### Querverweise
- `MULTIBLOCKS.md` für Strukturvalidierung
- `RENDERING.md` für Rotations-/Texturprobleme
- `EXTENDING.md` für korrekte Implementierungsabläufe
