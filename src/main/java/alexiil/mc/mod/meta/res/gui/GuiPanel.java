package alexiil.mc.mod.meta.res.gui;

import java.awt.Rectangle;
import java.io.IOException;

/** All of the methods arguments have already been modified to the correct values for you. */
public abstract class GuiPanel {
    public Rectangle drawArea;

    public abstract int getWidth();

    public void updateScreen() {}

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {}

    public void mouseScroll(int mouseX, int mouseY, float scroll) {}

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {}

    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {}

    public void mouseReleased(int mouseX, int mouseY, int state) {}

    public void onOpen() {}

    public void onClose() {}
}
