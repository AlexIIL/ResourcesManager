package alexiil.mc.mod.meta.res.gui._new_windows;

import net.minecraft.client.Minecraft;

import alexiil.mc.mod.meta.res.gui.GuiUtilRM;

import buildcraft.lib.gui.GuiRectangle;
import buildcraft.lib.gui.pos.IGuiArea;
import buildcraft.lib.gui.pos.MousePosition;

public abstract class PanelWindow implements IGuiArea {
    public static final int DECOR_EDGE = 5;
    public static final int DECOR_TOP = 12;

    public final GuiMultiWindow gui;
    public String title = "Window";

    public int x, y;
    public int width, height;
    public boolean fullscreen = false;

    public final IGuiArea wholeWindow = this;
    public final IGuiArea insideWindow;

    private MousePosition windowDragStart = null;
    private EnumWindowChange dragType = null;

    enum EnumWindowChange {
        MOVE(ResizePart.NONE, ResizePart.NONE),
        RESIZE_BOTTOM(ResizePart.NONE, ResizePart.MAX),
        RESIZE_LEFT(ResizePart.MIN, ResizePart.NONE),
        RESIZE_RIGHT(ResizePart.MAX, ResizePart.NONE),
        RESIZE_LEFT_BOTTOM(ResizePart.MIN, ResizePart.MAX),
        RESIZE_RIGHT_BOTTOM(ResizePart.MAX, ResizePart.MAX);

        public final ResizePart resizeX;
        public final ResizePart resizeY;

        private EnumWindowChange(ResizePart resizeX, ResizePart resizeY) {
            this.resizeX = resizeX;
            this.resizeY = resizeY;
        }

        public boolean isResize() {
            return resizeX != ResizePart.NONE || resizeY != ResizePart.NONE;
        }
    }

    enum ResizePart {
        NONE,
        MIN,
        MAX;

        public void resizeX(PanelWindow panel) {
            int diff = panel.gui.mouse.getX() - panel.windowDragStart.getX();
            switch (this) {
                case NONE:
                    return;
                case MIN: {
                    panel.width += diff;
                    panel.x -= diff;
                    return;
                }
                case MAX: {
                    panel.width += diff;
                    return;
                }
                default: {
                    throw new IllegalStateException("Unknown resize " + this);
                }
            }
        }

        public void resizeY(PanelWindow panel) {
            int diff = panel.gui.mouse.getY() - panel.windowDragStart.getY();
            switch (this) {
                case NONE:
                    return;
                case MIN: {
                    panel.height += diff;
                    panel.y -= diff;
                    return;
                }
                case MAX: {
                    panel.height += diff;
                    return;
                }
                default: {
                    throw new IllegalStateException("Unknown resize " + this);
                }
            }
        }
    }

    public PanelWindow(GuiMultiWindow gui) {
        this.gui = gui;
        insideWindow = IGuiArea.create(//
            () -> DECOR_EDGE + getX(),//
            () -> DECOR_TOP + getY(),//
            () -> getWidth() - DECOR_EDGE * 2,//
            () -> getHeight() - DECOR_EDGE - DECOR_TOP//
        );
    }

    @Override
    public int getX() {
        if (fullscreen) {
            return 0;
        } else {
            return x;
        }
    }

    @Override
    public int getY() {
        if (fullscreen) {
            return 0;
        } else {
            return y;
        }
    }

    @Override
    public int getWidth() {
        if (fullscreen) {
            return gui.width;
        } else {
            return width;
        }
    }

    @Override
    public int getHeight() {
        if (fullscreen) {
            return gui.height - GuiMultiWindow.BOTTOM_SIZE;
        } else {
            return height;
        }
    }

    public final void drawEdges() {
        GuiUtilRM.drawRect(getX(), getY(), getEndX(), getEndY(), 0xFF_CC_CC_CC);

        int barX = getEndX() - 45;
        int barY = getY() + 1;
        int iconSize = 10;
        // minimize
        GuiUtilRM.drawRect(barX + 1, barY + 4, barX + 9, barY + 6, 0xFF_33_33_33);
        barX += 15;

        int colour = 0xFF_33_33_33;
        GuiRectangle buttonChangeSize = new GuiRectangle(getEndX() - 30, getY() + 1, iconSize, iconSize);
        if (buttonChangeSize.contains(gui.mouse)) {
            colour = 0xFF_77_77_77;
        }
        // maximize
        GuiUtilRM.drawRect(barX, barY + 4, barX + iconSize, barY + 6, colour);
        GuiUtilRM.drawRect(barX + 4, barY, barX + 6, barY + iconSize, colour);
        barX += 15;
        // close
        GuiUtilRM.drawRect(barX, barY, barX + iconSize, barY + iconSize, 0xFF_33_33_33);

        // Title
        Minecraft.getMinecraft().fontRendererObj.drawString(title, getX() + 5, getY() + 2, 0);
    }

    public void drawBackground(float partialTicks) {}

    public void drawForeground(float partialTicks) {}

    protected void onMouseClicked(int button) {}

    protected void onMouseDragged(int clickedMouseButton, long timeSinceLastClick) {}

    protected void onMouseReleased(int state) {}

    public final boolean onMouseClicked0(boolean hit, int button) {
        if (!hit && insideWindow.contains(gui.mouse)) {
            onMouseClicked(button);
            return true;
        } else if (contains(gui.mouse)) {
            if (hit) {
                return true;
            }
            if (button == 0) {
                int iconSize = 10;
                GuiRectangle buttonChangeSize = new GuiRectangle(getEndX() - 30, getY() + 1, iconSize, iconSize);
                if (buttonChangeSize.contains(gui.mouse)) {
                    fullscreen = !fullscreen;
                    return true;
                }

                if (gui.mouse.getY() < getY() + DECOR_TOP) {
                    dragType = EnumWindowChange.MOVE;
                } else if (gui.mouse.getY() + DECOR_EDGE > getEndY()) {
                    if (gui.mouse.getX() + DECOR_EDGE < getX()) {
                        dragType = EnumWindowChange.RESIZE_LEFT_BOTTOM;
                    } else if (gui.mouse.getX() + DECOR_EDGE > getEndX()) {
                        dragType = EnumWindowChange.RESIZE_RIGHT_BOTTOM;
                    } else {
                        dragType = EnumWindowChange.RESIZE_BOTTOM;
                    }
                } else if (gui.mouse.getX() + DECOR_EDGE < getX()) {
                    dragType = EnumWindowChange.RESIZE_LEFT;
                } else if (gui.mouse.getX() + DECOR_EDGE > getEndX()) {
                    dragType = EnumWindowChange.RESIZE_RIGHT;
                } else {
                    return true;
                }
                windowDragStart = new MousePosition();
                windowDragStart.setMousePosition(gui.mouse.getX(), gui.mouse.getY());
            }
            return true;
        } else {
            return false;
        }
    }

    public final boolean onMouseDragged0(boolean hit, int clickedMouseButton, long timeSinceLastClick) {
        if (windowDragStart != null) {
            if (dragType == EnumWindowChange.MOVE) {
                x += gui.mouse.getX() - windowDragStart.getX();
                y += gui.mouse.getY() - windowDragStart.getY();
            } else if (dragType.isResize()) {
                dragType.resizeX.resizeX(this);
                dragType.resizeY.resizeY(this);
            }
            windowDragStart.setMousePosition(gui.mouse.getX(), gui.mouse.getY());
            return true;
        } else if (!hit && insideWindow.contains(gui.mouse)) {
            onMouseDragged(clickedMouseButton, timeSinceLastClick);
            return true;
        } else {
            return contains(gui.mouse);
        }
    }

    public final boolean onMouseReleased0(boolean hit, int state) {
        dragType = null;
        windowDragStart = null;
        if (insideWindow.contains(gui.mouse)) {
            if (!hit) {
                onMouseReleased(state);
            }
            return true;
        } else {
            return contains(gui.mouse);
        }
    }
}
