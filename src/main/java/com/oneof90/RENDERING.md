# RENDERING.md – Rendering & Modelle (1of90)

## Ziel
Dieses Dokument beschreibt Prinzipien, Patterns und Optimierungen für Renderer (TESR) und Modellintegration im 1of90 Paket.

## Grundlagen
- Minecraft 1.7.10 nutzt häufig TileEntitySpecialRenderer (TESR) für dynamische Modelle.
- Rotation & Kontext werden aus Block-Metadaten und Weltzustand abgeleitet.
- Kern-Multiblock: Nur Meta ≥ 12 rendern (Performance).

## Standard-Renderer Skeleton
```java
public class RenderExampleMultiblock extends TileEntitySpecialRenderer {
    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
        int meta = te.getBlockMetadata();
        if(meta < 12) return; // Nur Kern
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y, z + 0.5);
        float rot = switch(meta - 10) {
            case 2 -> 0f; case 3 -> 180f; case 4 -> 90f; case 5 -> 270f; default -> 0f;
        };
        GL11.glRotatef(rot, 0, 1, 0);
        bindTexture(new ResourceLocation("hbm", "textures/models/1of90_example_multiblock.png"));
        // Falls Wavefront-Modell vorhanden:
        // ((WavefrontObject) ResourceManager.example_multiblock_model).renderAll();
        GL11.glPopMatrix();
    }
}
```

## Modellquellen
| Typ | Beschreibung | Vorteile |
|-----|--------------|----------|
| Wavefront (.obj) | Mesh aus externem Modellierungsprogramm | Einfache Erstellung, weit kompatibel |
| Java-Hardcoded | Manuell definierte Quads | Kein externes Asset nötig, aber arbeitsintensiv |
| Hybrid | Kombination aus .obj + dynamischen Teilen | Flexibel für animierte Komponenten |

## Texturen
- Ablage: `assets/hbm/textures/models/`
- Benennung: Präfix `1of90_` (z.B. `1of90_my_machine.png`)
- Größe: Power-of-Two, 256×256 oder kleiner halten, wenn möglich.

## Rotationslogik
```java
float rotation;
int meta = te.getBlockMetadata();
if(meta >= 12) {
    rotation = switch(meta - 10) {
        case 2 -> 0f; case 3 -> 180f; case 4 -> 90f; case 5 -> 270f; default -> 0f;
    };
}
```

## Mehrteilige Modelle
Strategie:
1. Basisblock (statisch)
2. Bewegliche Teile (z.B. Kolben) separat rendern
```java
// Beispiel pseudo:
renderBase();
GL11.glPushMatrix();
GL11.glTranslatef(0, animatedOffset, 0);
renderMovingPart();
GL11.glPopMatrix();
```
Animation: Fortschritt aus TileEntity (z.B. `progress / (float) maxProgress`).

## Lighting & Culling
- `GL11.glEnable(GL11.GL_LIGHTING)` aktivieren für korrekte Schattierung.
- Culling temporär deaktivieren bei Innenflächen: `GL11.glDisable(GL11.GL_CULL_FACE)`.
- Nach Render wieder aktivieren: `GL11.glEnable(GL11.GL_CULL_FACE)`.

## Performance-Tipps
| Tipp | Nutzen |
|------|-------|
| Nur Kern rendern | Minimiert Draw Calls |
| Rotation caching | Vermeidet wiederholte Switch-Auswertung |
| Früh return bei Meta < 12 | Spart Matrix-Operationen |
| Modell-Nullprüfung | Verhindert Crash bei fehlendem ResourceManager-Laden |

## Fehlerdiagnose
| Problem | Ursache | Lösung |
|---------|---------|-------|
| Schwarzes Modell | Fehlende/inkorrekte Lichtaktivierung | `GL11.glEnable(GL11.GL_LIGHTING)` vor Render |
| Falsche Richtung | Meta falsch gesetzt | Kern-Meta = `dir.ordinal() + 10` prüfen |
| Keine Textur | ResourceLocation Pfad fehlerhaft | Namespace/Dateiname kontrollieren |
| Crash bei Render | Null in Model-Referenz | Nullprüfungen vor Nutzung |

## Beispiel: Animierter Fortschrittsring
```java
float pct = te.getProgress() / (float) te.getMaxProgress();
GL11.glPushMatrix();
GL11.glRotatef(pct * 360f, 0, 1, 0);
renderRing();
GL11.glPopMatrix();
```

## Teil-Rendering nach Zustand
```java
if(te.isActive()) {
    renderActiveGlow();
} else {
    renderIdleFrame();
}
```

## Ressourcenverwaltung
- Modell- und Texturinstanzen beim ResourceManager zentral halten.
- Lazy Loading vermeiden beim Tick – nur beim ersten Zugriff initialisieren.

## Skalierung
```java
GL11.glScalef(1.0F, 1.0F, 1.0F); // Basisgröße
// Für größere Darstellung
GL11.glScalef(1.25F, 1.25F, 1.25F);
```
Nicht zu stark skalieren → Clipping / Überschneidung.

## Transparenz / Spezialeffekte
```java
GL11.glEnable(GL11.GL_BLEND);
GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
// Halbtransparentes Overlay
renderOverlay();
GL11.glDisable(GL11.GL_BLEND);
```
Alpha-Kanal korrekt definieren (PNG mit Transparenz).

## Debug-Overlay (optional)
In Entwicklungsphase: Kleine Boxen für Kern / Dummy visualisieren.
```java
// Nicht produktiv belassen.
renderWireframeBoundingBox();
```

## Roadmap Rendering-Erweiterungen
| Feature | Nutzen |
|---------|-------|
| Shader-Effekte (optional) | Visuelle Tiefe |
| Animations-Helper-Klasse | Vereinheitlichte Bewegung |
| Zustandssymbole (Icons) | Schnelles Feedback für Spieler |
| Kameraorientiertes Billboard | Partikel-/Effektanzeige |

## Abschluss
Das Rendering im 1of90 Paket folgt Minimalismus & Effizienz. Nur das Notwendige zeichnen, komplexe Logik strikt im TileEntity belassen. Für tiefe Integration: Erweiterbare Tools in zukünftiger Roadmap.

### Querverweise
- `MULTIBLOCKS.md` für Kern-/Dummy-Erkennung
- `RESOURCES.md` für Textur-/Modellkonventionen
- `API.md` für Binding-Schnittstellen
