# NTM1of90 Kompatibilitäts-API Tutorial

Dieses Tutorial führt dich praxisnah durch die Nutzung der Kompatibilitäts-API im Verzeichnis `api/ntm1of90/compat`. Fokus: Fluid-Kompatibilität (Forge 1.7.10, AE2 Integration) und Adapter-Hooks für TileEntities.

---
## 1. Ziel & Konzept
Die NTM1of90 Kompatibilitäts-API abstrahiert Interaktionen zwischen internem NTM Fluid-/Tank-System und externen Mods (Forge FluidHandler, Applied Energistics 2). Sie stellt:
- Zentrale Initialisierung (`ForgeFluidCompatManager.initialize()`)
- Adapter-/Registry-Schichten für Mapping und Handler-Erzeugung
- Ereignis-Hooks (Spieler-Interaktion, Tick, Welt-Unload)
- Brückenlogik für bidirektionale Fluid-Transfers
- Clientseitige Farb- und Render-Pipeline für Inventar & AE2 GUI

---
## 2. Initialisierung (Lifecycle)
Rufe während der Mod-Init (typisch `FMLInitializationEvent`) einmalig:
```java
ForgeFluidCompatManager.initialize();
```
Das triggert intern:
- `FluidRegistry.initialize()` – Registrierung interner Fluids
- `FluidMappingRegistry.initialize()` – Zuordnung Forge Fluid <-> HBM FluidType
- `ForgeFluidAdapterRegistry.initialize()` – Adapter Cache für TileEntities
- `ForgeFluidCapabilityHook.initialize()` – Events (Interaktion, Tick, Welt-Unload)
- `NTMFluidLocalization.initialize()` – Lokalisierung/Anzeige
- `NTMFluidCompat.initialize()` – Erweiterte Kompatibilitätsfunktionalitäten
- Clientseitig zusätzliche Render/Texture Mapper

Prüfen ob schon initialisiert: `ForgeFluidCompatManager.isInitialized()`

---
## 3. Eigene TileEntity kompatibel machen
Voraussetzung: Deine TileEntity implementiert `IFluidUserMK2` (internes NTM Fluid Interface) und besitzt mindestens einen `FluidTank`.

### 3.1 Adapter-Erzeugung
Der Capability-Hook registriert automatisch Adapter beim Server-Tick. Du kannst explizit anfordern:
```java
IFluidHandler handler = ForgeFluidAdapterRegistry.getFluidHandler(tileEntity);
```
Falls noch keiner existiert, wird ein entsprechender Forge-kompatibler Handler erzeugt.

### 3.2 Mehrere Tanks
Bei mehreren Tanks kannst du über Hilfsmethoden (siehe `HBMForgeFluidBlockCompat`) kombinierte Handler erzeugen:
```java
IFluidHandler forgeHandler = HBMForgeFluidBlockCompat.createForgeFluidHandler(world, x, y, z, tanksArray);
```

### 3.3 Interaktion mit Eimern/Containern
Spieler-Rechtsklick wird von `ForgeFluidCapabilityHook.onPlayerInteract` abgefangen:
- Erkennt `IFluidUserMK2` TileEntities ohne native `IFluidHandler`
- Nutzt interne `handleFluidContainerInteraction(...)` Logik für Befüllen/Entleeren

Keine zusätzliche Implementierung nötig, solange Adapter verfügbar ist.

---
## 4. Fluid Transfers (Brückenlogik)
Nutze statische Methoden in `HBMForgeFluidBlockCompat` für gerichtete Transfers:
```java
int moved = HBMForgeFluidBlockCompat.transferToForgeBlock(hbmTank, world, x, y, z, ForgeDirection.NORTH, 4000);
int received = HBMForgeFluidBlockCompat.transferFromForgeBlock(world, x, y, z, hbmTank, ForgeDirection.NORTH, 4000);
```
Prüfe Akzeptanz/Fähigkeit vorab:
```java
boolean canAccept = HBMForgeFluidBlockCompat.canForgeBlockAcceptFluid(world, x, y, z, fluidType, dir);
boolean canProvide = HBMForgeFluidBlockCompat.canForgeBlockProvideFluid(world, x, y, z, fluidType, dir);
```
Die internen Methoden delegieren an `NTMFluidNetworkBridge` zur Mengenberechnung & Validierung.

---
## 5. AE2 Kompatibilität (Client)
### 5.1 Aktivierung
`AE2FluidCompat.initialize();` wird implizit aus `ForgeFluidCompatManager.initialize()` aufgerufen (falls AE2 erkannt). Bedingungen:
- Nur wenn `Loader.isModLoaded("appliedenergistics2")`
- Clientseitig (`@SideOnly(Side.CLIENT)`)

### 5.2 Funktionsumfang
- Reflektion auf `appeng.client.texture.FluidRenderMap` zum Registrieren von Farben
- Periodisches Auffrischen (alle 100 Ticks im Client-End-Phase Tick)
- Patchen von `FluidCellInventory` für Farb- und Item-Mapping
- Nutzung von `FluidDisplayItem` zur Anzeige einzelner Fluids als ItemStacks in AE2 ME Inventar

### 5.3 Farbzuordnung
Fluids vom Typ `ColoredForgeFluid` liefern ARGB via `getColorARGB()`; wird in AE2 Registriermethoden injiziert.
Fallback in `AE2CorePatcher` falls direkte Map-Patch scheitert: Versuch `registerFluid(fluid, color)`.

---
## 6. FluidDisplayItem Nutzung
Ermöglicht AE2 (und andere GUIs) jedes registrierte Fluid darzustellen.
```java
ItemStack display = FluidDisplayItem.getItemStackForFluid(fluid);
FluidStack stack = FluidDisplayItem.getFluidStackForItemStack(display, 1000);
String name = display.getDisplayName();
```
Metadaten-Mapping geschieht dynamisch (inkrementell über `registerFluid`).

---
## 7. Typische Integrationsschritte (Checkliste)
1. TileEntity implementiert `IFluidUserMK2`
2. Tanks sauber initialisiert (Kapazitäten > 0)
3. Mod Init ruft `ForgeFluidCompatManager.initialize()`
4. (Optional) Client-spezifische Render logik nach Init sicherstellen
5. Transfer-Logik im Update-Tick oder Nachbarschaftsprüfung einbauen
6. AE2 vorhanden? Farb-/Item Mapping verifizieren (Debug-Ausgaben beobachten)

---
## 8. Fehlerbehebung
| Problem | Ursache | Lösung |
|---------|---------|-------|
| Kein Fluid-Transfer | Adapter nicht erstellt | Prüfe `ForgeFluidAdapterRegistry.getFluidHandler(...)` Rückgabe ≠ null |
| Eimer reagiert nicht | Spieler-Event nicht erreicht | Sicherheit: `ForgeFluidCapabilityHook.initialize()` wurde ausgeführt |
| Falsche Fluidfarbe in AE2 | ColoredForgeFluid fehlt / Patch nicht aktiv | Sicherstellen Fluid-Klasse erbt korrekt; Log-Ausgaben von AE2FluidCompat prüfen |
| Duplizierte Items | Mehrfach-Registrierung bei Weltwechsel | Prüfen ob `registerFluidItems()` mehrfach pro Tick ausgelöst (Timing reduzieren) |
| Kein AE2 Mapping | AE2 nicht geladen | Mod-Präsenz prüfen (`Loader.isModLoaded`) |

---
## 9. Best Practices
- Adapter Lazy: Nicht selbst cachen, `ForgeFluidAdapterRegistry` übernimmt das.
- Farbupdates throttlen: Nur bei tatsächlicher Änderung forcieren, Standard-Tick (alle 100) genügt.
- Richtungskontext: Nutze konsistent `ForgeDirection` beim Transfer (UNKNOWN nur für Container-Interaktion).
- Logging: Verwende vorhandene `[NTM]` Präfix-Ausgaben zur Diagnose.
- Defensive Checks: Vor jedem Transfer `canForgeBlockAcceptFluid` / `canForgeBlockProvideFluid` nutzen.

---
## 10. Beispiel-End-to-End
```java
public class TileEntityMyFluidMachine extends TileEntity implements IFluidUserMK2 {
    private final FluidTank tank = new FluidTank(8000);

    @Override
    public void updateEntity() {
        if (worldObj.isRemote) return;
        // Nachbar prüfen (z.B. Norden)
        int moved = HBMForgeFluidBlockCompat.transferToForgeBlock(tank, worldObj, xCoord, yCoord, zCoord - 1, ForgeDirection.NORTH, 1000);
        if (moved > 0) {
            // interne Logik / Fortschritt erhöhen
        }
    }
}

// Mod Init
@EventHandler
public void init(FMLInitializationEvent e) {
    ForgeFluidCompatManager.initialize();
}
```

---
## 11. Erweiterung & Zukunft
- Geplante Vereinheitlichung: Abstrakte FluidAdapter-Basis zur Reduktion reflexiver AE2 Zugriffe
- Potenzielle Migration: Capability Proxy für spätere Forge-Versionen vorbereiten
- Validierung: Unit-Test ähnlicher Ansatz wie `AE2CompatTest.initialize()` für neue Mods

---
## 12. Referenzen
- `ForgeFluidCompatManager`
- `ForgeFluidCapabilityHook`
- `HBMForgeFluidBlockCompat`
- `AE2FluidCompat`, `AE2CorePatcher`
- `FluidDisplayItem`
- Registries unter `compat/fluid/registry`

Weitere generische API Hinweise: siehe `API.md`, erweitertes Ökosystem: `EXTENDING.md`, Fehleranalyse: `TROUBLESHOOTING.md`.

---
## 13. Schnell-FAQ
Q: Muss ich selbst einen `IFluidHandler` implementieren?  
A: Nein, Adapter werden automatisch erzeugt, nutze Registry-Abfrage.

Q: Wie erkenne ich ob Initialisierung schief lief?  
A: Konsole auf `[NTM] Forge Fluid Compatibility API initialized` prüfen.

Q: Kann ich Transferdrosselung implementieren?  
A: Ja, einfach maximale Menge (`maxAmount`) oder Tick-Frequenz reduzieren.

Q: Brauche ich Clientcode ohne AE2?  
A: Nein, AE2 Teil wird übersprungen falls Mod fehlt.

---
## 14. Nächste Schritte
- Multiblock Maschinen anbinden: Verwende gleiche Transfer-Methoden innerhalb Kern-TileEntity.
- Performance Review: Sammeltransfers in Batches statt Einzelaufrufen.
- Monitoring: Logging temporär erhöhen um frühe Integrationsfehler zu erkennen.

Viel Erfolg bei der Integration deiner Maschinen mit externen Fluid-Systemen!
