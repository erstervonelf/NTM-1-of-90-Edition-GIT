# API (DE, verschoben)

Öffentliche Schnittstellen & Muster zur Erweiterung. Detail übernommen aus Original.

## Registrierungs-Flow
`ModBlocks1of90.init()` → Blöcke & TE, `MainRegistry1of90.initClient()` → Renderer.

## Energie / Flüssigkeit / Inventar / GUI Interfaces
Siehe vollständige ursprüngliche Beschreibungen.

## Multiblock Kern
`checkRequirement` (nur Prüfung) vs `fillSpace` (Platzierung), Kernmeta = `dir.ordinal()+10`.

## NBT Standard
Speichern: Energie, Fortschritt, Inventar, Tanks konsistent.

## Best Practices
- Keine Logik im Renderer
- Dummy früh return
- Einheitliche Schlüsselnamen

## Querverweise
`MACHINE_CREATION_GUIDE.md` • `QUICK_REFERENCE.md` • `EXTENDING.md`
