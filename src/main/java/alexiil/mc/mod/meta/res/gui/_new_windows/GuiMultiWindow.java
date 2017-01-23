package alexiil.mc.mod.meta.res.gui._new_windows;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiScreen;

import alexiil.mc.mod.meta.res.gui.GuiUtilRM;

import buildcraft.lib.gui.pos.IGuiArea;
import buildcraft.lib.gui.pos.MousePosition;
import buildcraft.lib.misc.GuiUtil;

// Container class doesn't matter: this is just for the util
public class GuiMultiWindow extends GuiScreen {

    public static final int BOTTOM_SIZE = 20;

    public final PanelWindow rootPanel;
    public final List<PanelWindow> openWindows = new ArrayList<>();
    // public final List<PanelWindow> minimizedWindows = new ArrayList<>();

    private final Deque<Rectangle> scissorRegions = new ArrayDeque<>();

    public final MousePosition mouse = new MousePosition();

    public GuiMultiWindow(Function<GuiMultiWindow, PanelWindow> rootPanel) {
        this.rootPanel = rootPanel.apply(this);
        this.rootPanel.fullscreen = true;
    }

    public GuiMultiWindow(PanelWindow rootPanel) {
        this.rootPanel = rootPanel;
        this.rootPanel.fullscreen = true;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        long start = System.nanoTime();
        mouse.setMousePosition(mouseX, mouseY);
        drawBackgroundLayer(partialTicks);
        long diff = System.nanoTime() - start;
        double tpf = (diff / 1000) / 1000.0;
        drawString(fontRendererObj, tpf + " ms/f", 0, 0, -1);
    }

    protected void drawBackgroundLayer(float partialTicks) {
        pushScissor(0, 0, width, height - BOTTOM_SIZE);
        GuiUtilRM.drawRect(rootPanel.wholeWindow, 0xFF_00_00_00);
        rootPanel.draw(partialTicks);

        for (PanelWindow window : openWindows) {
            window.correctPosition();
            pushScissor(window);
            window.drawEdges();
            popScissor();
            pushScissor(window.insideWindow);
            window.draw(partialTicks);
            popScissor();
        }
        popScissor();
        GuiUtilRM.drawRect(0, height - BOTTOM_SIZE, width, height, -1);
    }

    public void pushScissor(Rectangle area) {
        scissorRegions.push(area);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GuiUtil.scissor(area.x, area.y, area.width, area.height);
    }

    public void pushScissor(IGuiArea area) {
        pushScissor(new Rectangle(area.getX(), area.getY(), area.getWidth(), area.getHeight()));
    }

    public void pushScissor(int x, int y, int width, int height) {
        pushScissor(new Rectangle(x, y, width, height));
    }

    public void popScissor() {
        scissorRegions.pop();
        if (scissorRegions.isEmpty()) {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        } else {
            Rectangle area = scissorRegions.peek();
            GuiUtil.scissor(area.x, area.y, area.width, area.height);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        mouse.setMousePosition(mouseX, mouseY);
        fireMouse((panel, hit) -> panel.onMouseClicked0(hit, mouseButton));
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        mouse.setMousePosition(mouseX, mouseY);
        fireMouse((panel, hit) -> panel.onMouseDragged0(hit, clickedMouseButton, timeSinceLastClick));
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        mouse.setMousePosition(mouseX, mouseY);
        fireMouse((panel, hit) -> panel.onMouseReleased0(hit, state));
    }

    public interface MouseMethod {
        /** @param panel The panel to call the method for
         * @param hit If true then this method has already hit a window higher up the trace, and so shouldn't affect
         *            this window for normal operations.
         * @return */
        boolean call(PanelWindow panel, boolean hit);
    }

    protected void fireMouse(MouseMethod method) {
        List<PanelWindow> reversed = new ArrayList<>(openWindows);
        Collections.reverse(reversed);
        boolean hit = false;
        for (PanelWindow window : reversed) {
            hit |= method.call(window, hit);
        }
        method.call(rootPanel, hit);
    }
}
