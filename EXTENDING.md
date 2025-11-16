# EXTENDING.md – Neue Features & Erweiterungsrichtlinien (1of90)

## Ziel
Beschreibt schrittweise den Prozess zur Einführung neuer Feature-Kategorien (z.B. Maschinenfamilien, Prozessketten, Ressourcen) im 1of90 Paket.

## Arten von Erweiterungen
| Typ | Beschreibung | Beispiel |
|-----|--------------|---------|
| Dekorative Blöcke | Rein visuell | Stahlträgervarianten |
| Funktionale Einzelmaschinen | Ein Block verarbeitet Items | Kompressor, Extraktor |
| Multiblock-Maschinen | Größere Struktur mit Kernlogik | Reaktor, Raffinerie |
| Infrastruktur | Energie-, Fluid-, Item-Verteilung | Kabel, Rohre |
| Benutzeroberflächen | GUI zur Steuerung | Maschinensteuerpult |
| Interaktion / Gameplay | Neue Mechaniken | Fortschrittsketten, Upgrades |

## Erweiterungs-Planung
1. Ziel definieren (Welche Lücke schließt das Feature?).
2. Ressourcenbedarf (Texturen, Modelle, Sounds?) einschätzen.
3. Datenstruktur (Energie? Inventar? Flüssigkeiten?) festlegen.
4. API / Interfaces auswählen (IEnergyReceiverMK2, IFluidStandardReceiver, ISidedInventory, IGUIProvider).
5. Modularität prüfen: Kann das Feature deaktiviert/extrahiert werden?

## Schritt-für-Schritt Beispiel: Neue Verarbeitungsmaschine
| Schritt | Datei / Ort | Aktion |
|---------|-------------|--------|
| 1 | `blocks/` | `MyProcessorBlock` erstellen (extends `BlockContainer`) |
| 2 | `tileentity/` | `TileEntityMyProcessor` mit Fortschritt + Energie |
| 3 | `render/` (optional) | TESR falls Spezialmodell nötig |
| 4 | `ModBlocks1of90.init()` | Block instanziieren & registrieren |
| 5 | `registerTileEntities()` | TileEntity anmelden |
| 6 | Assets | Textur `1of90_my_processor.png` hinzufügen |
| 7 | Lokalisierung | `tile.my_processor_1of90.name=My Processor` |
| 8 | (Optional) GUI | Container + GUI + PacketHandler |
| 9 | Dokumentation | README & GUIDE ergänzen |

## Fortschrittssystem hinzufügen
```java
private int progress = 0;
private int maxProgress = 400; // ~20s
private boolean canProcess() { return energy >= 200 && hasValidInput(); }
private void doProcessTick() {
    energy -= 200; progress++;
    if(progress >= maxProgress) { progress = 0; outputResult(); }
}
@Override
public void updateEntity() {
    if(worldObj.isRemote) return;
    if(canProcess()) doProcessTick();
}
```

## Upgrade-Slots (Beispiel)
```java
private ItemStack[] upgrades = new ItemStack[2];
private float getSpeedMultiplier() {
    float mult = 1.0F;
    for(ItemStack u : upgrades) {
        if(u != null && u.getItem() == ModItems.speed_upgrade) mult += 0.25F;
    }
    return mult;
}
```
Integration: Fortschrittsberechnung anpassen: `progress += getSpeedMultiplier();`

## Balancing-Richtlinien
| Parameter | Empfehlung | Wirkung |
|-----------|------------|---------|
| Energieverbrauch | Skalierbar nach Output-Seltenheit | Höhere Kosten bei seltenen Items |
| Verarbeitungsgeschwindigkeit | Zwischen 5–30 Sekunden | Verhindert Spam & behält Wert |
| Inventargrößen | Input/Output klar getrennt | Übersichtlichkeit |
| Upgrade-Kapazität | 1–4 Slots | Vermeidet Überoptimierung |

## Fehlerresistenz
- Nullprüfungen bei Inventarslots.
- `canUpdate()` nur `true`, wenn Logik notwendig.
- Fortschritt nicht negativ werden lassen.
- Abbruchbedingungen für unvollständige Strukturen (bei Multiblocks).

## Logging / Debugging
```java
if(DEBUG && !worldObj.isRemote) {
    MainRegistry.logger.info("MyProcessor progress=" + progress + "/" + maxProgress);
}
```
`DEBUG` als statische Konstante steuerbar (z.B. über Config).

## Feature-Deaktivierung (Optional)
Konfigurationsflag (z.B. `enableMyProcessor=true`) in separater Config-Klasse prüfen:
```java
if(!GeneralConfig.enableMyProcessor) return; // Registrierung überspringen
```

## Erweiterung mit Netzwerk
- Nutzung vorhandener PacketDispatcher (HBM).
- Nur State-Änderungen senden (z.B. Start/Stop Ereignisse).

## Beispiel: GUI Öffnung
```java
@Override
public boolean onBlockActivated(World w, int x, int y, int z, EntityPlayer p, int side, float hitX, float hitY, float hitZ) {
    if(w.isRemote) return true;
    FMLNetworkHandler.openGui(p, MainRegistry.instance, GUI_ID_MY_PROCESSOR, w, x, y, z);
    return true;
}
```

## Erweiterte Ideen
| Idee | Beschreibung |
|------|--------------|
| Rezeptsystem | Zentrale Verwaltung anstatt Hardcode |
| Energie-Typen | Verschiedene Qualitätsstufen (Effizienzfaktoren) |
| Wartungsstatus | Maschine benötigt periodische Reparatur |
| Überhitzung | Dynamisches Risiko bei Dauernutzung |

## Dokumentationspflege
Bei jeder neuen Maschine:
1. README – Abschnitt „Aktueller Inhalt“ ergänzen.
2. MACHINE_CREATION_GUIDE – Relevanten Schritt anpassen oder neuen Abschnitt.
3. QUICK_REFERENCE – Falls neue Metadaten oder Patterns.

## Checkliste vor PR
- [ ] Alle Registrierungen vorhanden
- [ ] Keine direkten HBM-Core Modifikationen
- [ ] Ressourcen korrekt benannt
- [ ] NBT Persistenz implementiert
- [ ] Performance akzeptabel (kein unnötiges Ticken)
- [ ] Dokumentation aktualisiert

## Abschluss
Dieses Dokument führt durch den Erweiterungsprozess und hilft konsistent, wartbare Features im 1of90 Paket unterzubringen. Vor komplexen neuen Systemen prüfen, ob bestehende Basisklassen reutilisierbar sind.

### Querverweise
- `API.md` für verfügbare Interfaces
- `MACHINE_CREATION_GUIDE.md` für praktische Implementierung
- `QUICK_REFERENCE.md` für schnelle Patterns
- `TROUBLESHOOTING.md` für Problembehebung
