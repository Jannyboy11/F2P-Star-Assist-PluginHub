package com.janboerman.f2pstarassist.plugin;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.*;

import java.awt.*;
import net.runelite.api.Point;

import javax.inject.Inject;

//adapted from GroundMarkerOverlay
public class DoubleHoppingTilesOverlay extends Overlay {

    private static final class DoubleHoppingTile {
        private WorldPoint worldPoint;
        private String label;
        private DoubleHoppingTile(String label, WorldPoint worldPoint) {
            this.worldPoint = worldPoint;
            this.label = label;
        }
    }

    private static final DoubleHoppingTile[] doubleHoppingTiles = {
            new DoubleHoppingTile("Crafting Guild / Rimmington hopping location", new WorldPoint(2985, 3265, 0)),
            new DoubleHoppingTile("Desert mine / Duel Arena hopping location", new WorldPoint(3318, 3282, 0)),
            new DoubleHoppingTile("Varrock west bank hopping location", new WorldPoint(3264, 3384, 0)),
            new DoubleHoppingTile("Varrock south east mine hopping location", new WorldPoint(3264, 3383, 0)),
    };

    private static final int MAX_DRAW_DISTANCE = 32;

    private final Client client;
    private final StarAssistConfig config;

    @Inject
    DoubleHoppingTilesOverlay(Client client, StarAssistConfig config) {
        this.client = client;
        this.config = config;

        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.LOW);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {

        if (!config.markDoubleHoppingTiles()) return null;

        Stroke stroke = new BasicStroke(2F);
        for (final DoubleHoppingTile tile : doubleHoppingTiles) {
            if (tile.worldPoint.getPlane() == client.getPlane()) {
                Color tileColour = new Color(0, 94, 53);
                drawTile(graphics, tile.worldPoint, tileColour, tile.label, stroke);
            }
        }

        return null;
    }

    private void drawTile(Graphics2D graphics, WorldPoint point, Color colour, String label, Stroke borderStroke) {
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();

        if (point.distanceTo(playerLocation) >= MAX_DRAW_DISTANCE) return;

        LocalPoint lp = LocalPoint.fromWorld(client, point);
        if (lp == null) return;

        Polygon polygon = Perspective.getCanvasTilePoly(client, lp);
        if (polygon != null) {
            OverlayUtil.renderPolygon(graphics, polygon, colour, new Color(0, 0, 0, /*fill opacity*/50), borderStroke);
        }

        Point canvasTextLocation = Perspective.getCanvasTextLocation(client, graphics, lp, label, 0);
        if (canvasTextLocation != null) {
            OverlayUtil.renderTextLocation(graphics, canvasTextLocation, label, colour);
        }
    }

}
