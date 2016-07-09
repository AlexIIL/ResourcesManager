package alexiil.mc.mod.meta.res.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class GuiUtil {

    public static final int SELECTION_COLOUR = 0xFF_DD_DD_DD;
    public static final int HOVER_COLOUR = 0xFF_AA_AA_AA;

    /** Draws a solid color rectangle with the specified coordinates and color. */
    public static void drawRect(int left, int top, int right, int bottom, int color) {
        if (left < right) {
            int i = left;
            left = right;
            right = i;
        }

        if (top < bottom) {
            int j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (color >> 24 & 255) / 255.0F;
        float f = (color >> 16 & 255) / 255.0F;
        float f1 = (color >> 8 & 255) / 255.0F;
        float f2 = (color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(f, f1, f2, f3);
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION);
        vertexbuffer.pos(left, bottom, 0.0D).endVertex();
        vertexbuffer.pos(right, bottom, 0.0D).endVertex();
        vertexbuffer.pos(right, top, 0.0D).endVertex();
        vertexbuffer.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    /** Draw a 1 pixel wide vertical line. Args : x, y1, y2, color */
    public static void drawVerticalLine(int x, int startY, int endY, int color) {
        if (endY < startY) {
            int i = startY;
            startY = endY;
            endY = i;
        }

        drawRect(x, startY + 1, x + 1, endY, color);
    }

    /** Draws a thin horizontal line between two points. */
    public static void drawHorizontalLine(int startX, int endX, int y, int color) {
        if (endX < startX) {
            int i = startX;
            startX = endX;
            endX = i;
        }

        drawRect(startX, y, endX + 1, y + 1, color);
    }

    public static void drawSelectionBox(int x, int y, int width, int height) {
        drawHorizontalLine(x, x + width, y, SELECTION_COLOUR);
        drawHorizontalLine(x, x + width, y + height, SELECTION_COLOUR);
        drawVerticalLine(x, y, y + height, SELECTION_COLOUR);
        drawVerticalLine(x + width, y, y + height, SELECTION_COLOUR);
    }

    public static void drawHoverBox(int x, int y, int width, int height) {
        drawHorizontalLine(x, x + width, y, HOVER_COLOUR);
        drawHorizontalLine(x, x + width, y + height, HOVER_COLOUR);
        drawVerticalLine(x, y, y + height, HOVER_COLOUR);
        drawVerticalLine(x + width, y, y + height, HOVER_COLOUR);
    }
}
