package alexiil.mc.mod.meta.res.gui._new_windows;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

import alexiil.mc.mod.meta.res.gui.GuiUtilRM;

import buildcraft.lib.client.sprite.ISprite;
import buildcraft.lib.client.sprite.RawSprite;
import buildcraft.lib.client.sprite.SpriteNineSliced;
import buildcraft.lib.gui.GuiRectangle;
import buildcraft.lib.gui.pos.IGuiArea;
import buildcraft.lib.gui.pos.MousePosition;

public abstract class PanelWindow implements IGuiArea {
    public static final int DECOR_EDGE = 5;
    public static final int DECOR_TOP = 16;

    private static final ResourceLocation RES_BACKGROUND = new ResourceLocation("resourcemanager:textures/gui/background.png");
    public static final SpriteNineSliced SPRITE_BACKGROUND = new RawSprite(RES_BACKGROUND, 0, 0, 1, 1).slice(12, 12, 64 - 12, 64 - 12, 64);

    private static final ResourceLocation RES_ICONS = new ResourceLocation("resourcemanager:textures/gui/icons.png");
    public static final RawSprite SPRITE_ICONS = new RawSprite(RES_ICONS, 0, 0, 1, 1);
    public static final ISprite SPRITE_MINIMIZE_NORMAL = SPRITE_ICONS.subAbsolute(0, 8, 8, 16, 256);
    public static final ISprite SPRITE_MINIMIZE_HOVERED = SPRITE_ICONS.subAbsolute(0, 16, 8, 24, 256);
    public static final ISprite SPRITE_MAXIMIZE_NORMAL = SPRITE_ICONS.subAbsolute(8, 8, 16, 16, 256);
    public static final ISprite SPRITE_MAXIMIZE_HOVERED = SPRITE_ICONS.subAbsolute(8, 16, 16, 24, 256);
    public static final ISprite SPRITE_CLOSE_NORMAL = SPRITE_ICONS.subAbsolute(16, 8, 24, 16, 256);
    public static final ISprite SPRITE_CLOSE_HOVERED = SPRITE_ICONS.subAbsolute(16, 16, 24, 24, 256);

    public final GuiMultiWindow gui;
    public String title = "Window";

    public int x, y;
    public int width = 200, height = 200;
    public boolean fullscreen = false;

    public final IGuiArea wholeWindow;
    public final IGuiArea insideWindow;

    private MousePosition windowDragStart = null;
    private EnumWindowChange dragType = null;

    enum EnumWindowChange {
        MOVE(null, null),
        RESIZE_BOTTOM(ResizePart.NONE, ResizePart.MAX),
        RESIZE_LEFT(ResizePart.MIN, ResizePart.NONE),
        RESIZE_RIGHT(ResizePart.MAX, ResizePart.NONE),
        RESIZE_LEFT_BOTTOM(ResizePart.MIN, ResizePart.MAX),
        RESIZE_RIGHT_BOTTOM(ResizePart.MAX, ResizePart.MAX);

        public final ResizePart resizeX;
        public final ResizePart resizeY;

        private EnumWindowChange(ResizePart resizeX, ResizePart resizeY) {
            this.resizeX = resizeX == null ? ResizePart.NONE : resizeX;
            this.resizeY = resizeY == null ? ResizePart.NONE : resizeY;
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
        wholeWindow = IGuiArea.create(//
            () -> fullscreen ? -DECOR_EDGE : x,//
            () -> fullscreen ? 0 : y,//
            () -> getWidth() + (fullscreen ? DECOR_EDGE * 2 : 0),//
            () -> getHeight() + (fullscreen ? DECOR_TOP : 0)//
        );
        insideWindow = IGuiArea.create(//
            () -> wholeWindow.getX() + DECOR_EDGE,//
            () -> wholeWindow.getY() + DECOR_TOP,//
            () -> wholeWindow.getWidth() - DECOR_EDGE * 2,//
            () -> wholeWindow.getHeight() - DECOR_EDGE - DECOR_TOP//
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
        SPRITE_BACKGROUND.draw(wholeWindow);

        int barX = getEndX() - 45;
        int barY = getY() + 4;
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
        FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
        font.drawString(title, getCenterX() - font.getStringWidth(title) / 2, getY() + 5, 0);
    }

    public void draw(float partialTicks) {}

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

    public final void correctPosition() {
        if (windowDragStart == null) return;
        if (dragType == EnumWindowChange.MOVE) {
            x += gui.mouse.getX() - windowDragStart.getX();
            y += gui.mouse.getY() - windowDragStart.getY();
        } else if (dragType.isResize()) {
            dragType.resizeX.resizeX(this);
            dragType.resizeY.resizeY(this);
        }
        windowDragStart.setMousePosition(gui.mouse.getX(), gui.mouse.getY());
    }

    public final boolean onMouseDragged0(boolean hit, int clickedMouseButton, long timeSinceLastClick) {
        if (windowDragStart != null) {
            correctPosition();
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
