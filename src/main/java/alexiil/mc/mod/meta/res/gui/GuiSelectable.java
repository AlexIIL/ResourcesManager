package alexiil.mc.mod.meta.res.gui;

import java.util.List;
import java.util.function.BiConsumer;

public class GuiSelectable<S extends GuiDrawable> {
    public int x, width;
    public final BiConsumer<GuiSelectable<S>, S> onSelect;

    public int drawHeight;

    private List<S> possible;
    private boolean mouseInWhole = false;
    private int selected = -1, mouseOver = -1;
    private float scrollPos;
    private int maxY = -1;

    public GuiSelectable(int x, int width, BiConsumer<GuiSelectable<S>, S> onSelect) {
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
        if (maxY < drawHeight && scrollPos < 0) {
            scrollPos += ((drawHeight - maxY) / 10) + 1;
        }
    }

    public void draw(int mouseX, int mouseY) {
        if (possible == null) {
            return;
        }
        int xPos = x + 3;
        int y = 5 + (int) scrollPos;
        int i = 0;
        mouseOver = -1;
        mouseInWhole = mouseX > x && mouseX < x + width;

        for (S drawable : possible) {
            int height = drawable.draw(xPos, y);
            if (selected == i) {
                GuiUtilRM.drawSelectionBox(x, y - 3, width, height + 6);
            }
            if (mouseX > x && mouseX <= x + width && mouseY > y && mouseY < y + height + 6) {
                mouseOver = i;
                GuiUtilRM.drawHoverBox(x, y - 3, width, height + 6);
            }
            y += height + 6;
            i++;
        }
        maxY = y;

        // draw scroll bar
        int totalHeight = maxY - (int) scrollPos;
        if (totalHeight > drawHeight) {
            GuiUtilRM.drawRect(x + width + 4, 0, x + width + 5, drawHeight, GuiUtilRM.HOVER_COLOUR);

            int actualHeight = (int) (drawHeight * drawHeight / (double) totalHeight);
            int actualPos = (-(int) scrollPos) * drawHeight / (totalHeight);
            GuiUtilRM.drawRect(x + width + 2, actualPos, x + width + 7, actualPos + actualHeight, GuiUtilRM.SELECTION_COLOUR);
        }
    }

    public void onClick() {
        if (possible == null) {
            return;
        }
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
