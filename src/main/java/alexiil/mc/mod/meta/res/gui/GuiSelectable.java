package alexiil.mc.mod.meta.res.gui;

import java.util.List;
import java.util.function.BiConsumer;

import net.minecraft.client.gui.GuiScreen;

public class GuiSelectable<S extends GuiDrawable> {
    public int x, width;
    public final BiConsumer<GuiSelectable<S>, S> onSelect;
    private final GuiScreen gui;

    private List<S> possible;
    private boolean mouseInWhole = false;
    private int selected = -1, mouseOver = -1;
    private float scrollPos;
    private int maxY = -1;

    public GuiSelectable(GuiScreen gui, int x, int width, BiConsumer<GuiSelectable<S>, S> onSelect) {
        this.gui = gui;
        this.x = x;
        this.width = width;
        this.onSelect = onSelect;
    }

    public void setList(List<S> possible) {
        this.possible = possible;
        selected = -1;
        scrollPos = 0;
        width = 30;
        if (possible != null) {
            for (S element : possible) {
                width = Math.max(width, element.getWidth() + 6);
            }
        }
    }

    public boolean isSelected() {
        return selected >= 0;
    }

    public S getSelected() {
        return isSelected() ? possible.get(selected) : null;
    }

    public void tick() {
        if (scrollPos > 0) {
            scrollPos -= (scrollPos / 10) + 1;
            if (scrollPos < 0) {
                scrollPos = 0;
            }
        }
        if (maxY < gui.height && scrollPos < 0) {
            scrollPos += (-scrollPos / 10) + 1;
            if (scrollPos > 0) {
                scrollPos = 0;
            }
        }
    }

    public void draw(int mouseX, int mouseY) {
        int xPos = x + 3;
        int y = 30 + (int) scrollPos;
        int i = 0;
        mouseOver = -1;
        mouseInWhole = mouseX > x && mouseX < x + width;
        if (possible == null) {
            return;
        }

        gui.mc.fontRendererObj.drawString("" + maxY, 0, xPos, -1);
        gui.mc.fontRendererObj.drawString("" + ((int) scrollPos), 0, xPos + 12, -1);
        gui.mc.fontRendererObj.drawString("" + gui.height, 0, xPos + 24, -1);

        for (S drawable : possible) {
            int height = drawable.draw(xPos, y);
            if (selected == i) {
                GuiUtil.drawSelectionBox(x, y - 3, width, height + 6);
            }
            if (mouseX > x && mouseX <= x + width && mouseY > y && mouseY < y + height + 6) {
                mouseOver = i;
                GuiUtil.drawHoverBox(x, y - 3, width, height + 6);
            }
            y += height + 6;
            i++;
        }
        maxY = y;
    }

    public void onClick() {
        selected = mouseOver;
        onSelect.accept(this, getSelected());
    }

    public void onDrag() {

    }

    public void onRelease() {

    }

    public void onScroll(float by) {
        if (mouseInWhole) {
            scrollPos += by;
        }
    }
}
