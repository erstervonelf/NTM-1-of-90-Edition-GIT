# NTM1of90 Kompatibilitäts-API Tutorial

(Dies ist die verschobene deutsche Originalfassung.)

*Inhalt siehe frühere Root-Datei; zur Trennung von Code und Dokumentation jetzt unter `documentation/de/`.*

---
## 1. Ziel & Konzept
Die Kompatibilitäts-API verbindet das interne NTM Fluid-/Tank-System mit externen Mods (Forge FluidHandler, AE2). Kernbausteine:
- Initialisierung (`ForgeFluidCompatManager.initialize()`)
- Registries & Adapter
- Event Hooks (Interaktion, Tick, Welt-Unload)
- Transfer-Brücke (NTM ↔ Forge)
- Clientseitige Farb- & Renderpfade (AE2, Inventar)

## 2. Initialisierung
```java
ForgeFluidCompatManager.initialize();
```
Führt intern mehrere Setup-Schritte aus (Registries, Hooks, Client-Komponenten). Einmal pro Mod-Lifecycle.

## 3. TileEntity kompatibel machen
Voraussetzung: Implementiert `IFluidUserMK2` + besitzt einen oder mehrere `FluidTank`.
Adapter lazy über `ForgeFluidAdapterRegistry.getFluidHandler(tile)`.

## 4. Transfers
```java
HBMForgeFluidBlockCompat.transferToForgeBlock(...);
HBMForgeFluidBlockCompat.transferFromForgeBlock(...);
```
Vorher prüfbar: `canForgeBlockAcceptFluid`, `canForgeBlockProvideFluid`.

## 5. AE2 Client Integration
Automatisch aktiv, wenn AE2 geladen. Farben via Reflection auf `FluidRenderMap` registriert; Fallback über Core-Patcher.

## 6. FluidDisplayItem
Erzeugt ItemStacks zur Darstellung registrierter Fluids. Metadaten-Mapping dynamisch.

## 7. Checkliste
1. `IFluidUserMK2` implementiert
2. Tanks initialisiert
3. Init-Aufruf durchgeführt
4. Transferlogik tick-sparsam
5. AE2 Farben sichtbar

## 8. Fehlerbehebung (Auszug)
| Problem | Lösungskern |
|---------|-------------|
| Kein Transfer | Adapter prüfen |
| Falsche Farbe | `ColoredForgeFluid` Klasse / Registrierung kontrollieren |
| Duplikate Items | Registrierungsfrequenz reduzieren |

## 9. Best Practices
Adapter nicht manuell cachen • Transfers bündeln • Richtung konsequent nutzen • Logging nur bei Bedarf.

## 10. Beispiel
```java
int moved = HBMForgeFluidBlockCompat.transferToForgeBlock(tank, worldObj, xCoord, yCoord, zCoord-1, ForgeDirection.NORTH, 1000);
```

## 11. Zukunft / Erweiterung
Abstrakte Adapterbasis & Vorbereitung höherer Forge-Versionen.

## 12. Referenzen
`ForgeFluidCompatManager`, `ForgeFluidCapabilityHook`, `HBMForgeFluidBlockCompat`, `AE2FluidCompat`, `FluidDisplayItem`.

## 13. FAQ (Kurz)
Q: Eigener `IFluidHandler` nötig? A: Nein, Adapter generiert.
Q: Performance? A: Tick-Frequenz & Mengen begrenzen.

## 14. Nächste Schritte
Multiblock-Maschinen anschließen (gleiches Transfer-Pattern), Logging temporär erhöhen.
