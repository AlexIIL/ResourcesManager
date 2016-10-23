package alexiil.mc.mod.meta.res.gui;

import java.awt.Rectangle;
import java.io.IOException;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class PanelHolder {
    public final GuiPanel contained;
    public Rectangle drawArea;

    public PanelHolder(GuiPanel contained) {
        this.contained = contained;
    }

    public int getWidth() {
        return contained.getWidth();
    }

    public void updateScreen() {
        contained.updateScreen();
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GuiScreen current = Minecraft.getMinecraft().currentScreen;
        if (current == null) return;
        int mult = (Minecraft.getMinecraft().displayHeight + 1) / current.height;
        GL11.glScissor(drawArea.x * mult, (current.height - drawArea.y - drawArea.height) * mult, drawArea.width * mult, drawArea.height * mult);
        GL11.glPushMatrix();
        GL11.glTranslated(drawArea.x, drawArea.y, 0);
        contained.drawArea = drawArea;
        contained.drawScreen(mouseX - drawArea.x, mouseY - drawArea.y, partialTicks);
        GL11.glPopMatrix();
    }

    public boolean mouseScroll(int mouseX, int mouseY, float scroll) {
        if (drawArea.contains(mouseX, mouseY)) {
            contained.mouseScroll(mouseX - drawArea.x, mouseY - drawArea.y, scroll);
            return true;
        }
        return false;
    }

    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (drawArea.contains(mouseX, mouseY)) {
            contained.mouseClicked(mouseX - drawArea.x, mouseY - drawArea.y, mouseButton);
            return true;
        }
        return false;
    }

    public boolean mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (drawArea.contains(mouseX, mouseY)) {
            contained.mouseClickMove(mouseX - drawArea.x, mouseY - drawArea.y, clickedMouseButton, timeSinceLastClick);
            return true;
        }
        return false;
    }

    public boolean mouseReleased(int mouseX, int mouseY, int state) {
        if (drawArea.contains(mouseX, mouseY)) {
            contained.mouseReleased(mouseX - drawArea.x, mouseY - drawArea.y, state);
            return true;
        }
        return false;
    }

    public void onOpen() {
        contained.onOpen();
    }

    public void onClose() {
        contained.onClose();
    }
}
