# API.md – Öffentliche Erweiterungspunkte (1of90)

Dieses Dokument listet wiederverwendbare Schnittstellen, Konventionen und Hook-Punkte für Entwickler auf.

## Übersicht
| Bereich | Typ | Zweck |
|--------|-----|-------|
| Blockregistrierung | Methode | Neue Blöcke/Multiblocks anmelden |
| TileEntity Logik | Interface | Energie, Flüssigkeit, GUI, Inventar |
| Rendering | TESR Bindings | Modelle & Spezialvisualisierung |
| Integration | Init-Hooks | Einbindung in HBM Lifecycle |

## Registrierungs-API (interne Abläufe)

### ModBlocks1of90
```java
public static void init();               // Erzeugt & konfiguriert Blockinstanzen
private static void registerBlocks();    // Führt GameRegistry.registerBlock aus
private static void registerTileEntities(); // RegisterTileEntity-Aufrufe
```
Erweiterung: Neue Blöcke in `init()` hinzufügen und anschließend `registerBlocks()` / `registerTileEntities()` anpassen (oder generisch halten).

### MainRegistry1of90
```java
public static void init();        // Serverseitige Registrierung (Blöcke/TE)
@SideOnly(Side.CLIENT)
public static void initClient();  // Clientseitige Renderer/TESR Binding
```
Erweiterung: Neue Renderklassen in `initClient()` über `ClientRegistry.bindTileEntitySpecialRenderer(TileEntityKlasse.class, new RenderKlasse())` anmelden.

## Energie-Interfaces
HBM stellt Energie MK2 bereit.
```java
public interface IEnergyReceiverMK2 {
    boolean canConnect(ForgeDirection dir);
    long transferPower(long power);      // Rückgabe = tatsächlich aufgenommene Energie
    long getPower();                     // Aktueller Energiepuffer
    long getMaxPower();                  // Maximaler Speicher
}
```
Implementierungs-Tipps:
- Begrenze `transferPower` auf freie Kapazität.
- Optional: Weiterleitung an interne Maschinenlogik (Verbrauch/Produktion).

## Fluid-Interfaces
Beispiel: `IFluidStandardReceiver` / `IFluidStandardTransceiver`.
```java
int fill(ForgeDirection from, FluidStack resource, boolean doFill); // Einfüllen
FluidTank[] getReceivingTanks();
FluidTank[] getAllTanks();
```
Konvention:
- Tanks in Konstruktor initialisieren.
- NBT Speichern: `tank.writeToNBT(tag)` / `tank.readFromNBT(tag)`.

## Inventar / Zugriff
Verwendung von `ISidedInventory`:
```java
int getSizeInventory();
ItemStack getStackInSlot(int slot);
void setInventorySlotContents(int slot, ItemStack stack);
boolean isItemValidForSlot(int slot, ItemStack stack);
int[] getAccessibleSlotsFromSide(int side);
boolean canInsertItem(int slot, ItemStack stack, int side);
boolean canExtractItem(int slot, ItemStack stack, int side);
```
Hinweise:
- Slots konsistent dokumentieren.
- Für Autosortierung klare Slotrollen definieren (Input/Output).

## GUI / Container
Interface: `IGUIProvider` (HBM-spezifisch)
```java
Container provideContainer(int ID, EntityPlayer player, World world, int x, int y, int z);
@SideOnly(Side.CLIENT)
GuiScreen provideGUI(int ID, EntityPlayer player, World world, int x, int y, int z);
```
Verwendung:
- Container-Klasse mit Slotdefinitionen.
- GUI-Klasse für Darstellung (Fortschritt, Energie, Flüssigkeit).

## Multiblock Mechanik (BlockDummyable)
Wichtige Methoden für Erweiterung:
```java
protected boolean checkRequirement(World world, int x, int y, int z, ForgeDirection dir, int offset);
public void fillSpace(World world, int x, int y, int z, ForgeDirection dir, int offset);
public int[] getDimensions();      // Rückgabe: {Breite, Höhe, Tiefe}
public int getOffset();            // Position des Kernblocks relativ zum Klickpunkt
```
Konventionen:
- `checkRequirement` nur Zustände prüfen (kein Platzieren!).
- `fillSpace` schreibt Dummy-Blöcke + Kern-Metadaten.
- Kern-Metadaten = `dir.ordinal() + 10` (≥12).

## Renderer (TESR) Binding
```java
ClientRegistry.bindTileEntitySpecialRenderer(TileEntityKlasse.class, new RenderKlasse());
```
Renderer-Grundstruktur:
```java
public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
    int meta = te.getBlockMetadata();
    if(meta < 12) return; // Nur Kern rendern
    // Rotation → meta - 10
    // Texture binden → bindTexture(...)
    // Modell rendern → model.renderAll();
}
```

## NBT Persistenz
Standardpattern:
```java
@Override
public void writeToNBT(NBTTagCompound nbt) {
    super.writeToNBT(nbt);
    nbt.setLong("energy", energy);
    nbt.setInteger("progress", progress);
    // Inventar + Tanks speichern
}
@Override
public void readFromNBT(NBTTagCompound nbt) {
    super.readFromNBT(nbt);
    energy = nbt.getLong("energy");
    progress = nbt.getInteger("progress");
}
```
Empfehlung: Einheitliche Schlüsselnamen, Kleinschreibung.

## Ereignis- / Zustandsänderungen
Optional könnten eigene Event-Dispatcher eingeführt werden (geplant). Aktuell: Direkte Abfrage der TE Felder.

## Erweiterungs-Checkliste
1. Namenskonvention einhalten.
2. Block + TileEntity erstellen.
3. Registrierung in `ModBlocks1of90`.
4. Renderer (falls nötig) in `MainRegistry1of90.initClient()`.
5. Assets + Lokalisierung.
6. NBT Save/Load implementieren.
7. Performance überprüfen (`canUpdate()` nur wenn nötig).
8. Dokumentationsverweis anpassen (README / GUIDE).

## Best Practices
- Keine Logik im Renderer.
- Strukturformation strikt trennen von Validierung.
- Früh return bei Dummy-Metadaten.
- Fortschrittsberechnungen nicht Frame-gebunden sondern Tick-orientiert.

## Geplante API-Erweiterungen
| Feature | Ziel |
|---------|-----|
| Ereignis-Hooks | Externe Reaktion auf Maschinenzustände |
| JSON Multiblock Definition | Datengesteuerte Strukturen |
| Abstrakte Basis TE | Vereinheitlichte Energie/Flüssigkeit/Inventar Logik |
| Synchronisations-Helfer | Vereinfachte Client-Updates |

## Lizenz / Nutzung
Die Dateien im 1of90 Paket folgen der Gesamtlizenz des Projekts. Erweiterungen sollen sich an Stil & Struktur halten, keine Lizenzheader hinzufügen ohne Notwendigkeit.

## Abschluss
Dieses API-Dokument bietet Entwickler*innen eine schnelle Orientierung für Erweiterungen des 1of90 Pakets.

### Querverweise
- `MACHINE_CREATION_GUIDE.md` – Schritt-für-Schritt Implementierung
- `QUICK_REFERENCE.md` – Metadaten & Patterns
- `EXTENDING.md` – Prozess für neue Feature-Kategorien
- `TROUBLESHOOTING.md` – Fehleranalyse
