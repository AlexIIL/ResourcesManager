package alexiil.mc.mod.meta.res.gui;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiScreen;

import alexiil.mc.mod.meta.res.scan.ResFile;
import alexiil.mc.mod.meta.res.scan.ResourceScanner;

public class GuiResourcesMain extends GuiScreen {
    public final GuiPanelFolderStructure panelFolders = new GuiPanelFolderStructure(this);
    private ResFile openFile = null;
    private GuiPanelFileInfo panelFileInfo = null;

    private final List<PanelHolder> holders = new ArrayList<>();
    private float scrollX = 0;
    private int totalWidth;

    /* TODO: Add a side panel (on the right) with viewing/forking info about a selected file */
    // Still needed :)

    public GuiResourcesMain() {
        holders.add(new PanelHolder(panelFolders));
        refreshFolders();
    }

    private void refreshFolders() {
        panelFileInfo = null;

        ResourceScanner.INSTANCE.scan();
        panelFolders.onOpen();
    }

    public void changeOpenFileDlg(ResFile to) {
        if (openFile == to) {
            return;
        }
        if (openFile != null) {
            for (int i = holders.size() - 1; i > 0; i--) {
                PanelHolder holder = holders.remove(i);
                holder.onClose();
            }
        }
        openFile = to;
        if (openFile != null) {
            GuiPanelFileInfo gpfi = new GuiPanelFileInfo(openFile);
            holders.add(new PanelHolder(gpfi));
            gpfi.onOpen();
        }
    }

    @Override
    public void initGui() {

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawBackground(0);
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (totalWidth < width) {
            scrollX = 0;
        }

        // TODO: Draw top bar

        int curX = (int) scrollX;
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        for (PanelHolder holder : holders) {
            int holderWidth = holder.getWidth();
            holder.drawArea = new Rectangle(curX, 30, holderWidth, this.height - 70);
            holder.drawScreen(mouseX, mouseY, partialTicks);
            curX += holderWidth + 30;
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        totalWidth = curX - (int) scrollX;

        if (totalWidth > this.width) {
            // start cap
            int left = 10;
            int top = this.height - 35;
            int right = left + 5;
            int bottom = this.height - 10;
            GuiUtilRM.drawRect(left, top, right, bottom, GuiUtilRM.HOVER_COLOUR);

            // line
            GuiUtilRM.drawRect(left + 5, this.height - 25, this.width - 15, this.height - 20, GuiUtilRM.HOVER_COLOUR);

            // end cap
            GuiUtilRM.drawRect(this.width - 15, top, this.width - 10, bottom, GuiUtilRM.HOVER_COLOUR);

            // draggable

            int slidableWidth = this.width - 40;
            float percent = Math.min(this.width / (float) totalWidth, 1);
            int w = (int) (slidableWidth * percent);
            int maxLeft = slidableWidth - w;
            int maxScroll = totalWidth - this.width;
            left = 20 + (int) (maxLeft * (-scrollX / maxScroll));
            GuiUtilRM.drawRect(left, this.height - 30, left + w, this.height - 15, GuiUtilRM.SELECTION_COLOUR);
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        for (PanelHolder holder : holders) {
            holder.updateScreen();
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        super.handleMouseInput();
        float scroll = Mouse.getEventDWheel();
        scroll /= 4.0F;

        for (PanelHolder holder : holders) {
            if (holder.mouseScroll(mouseX, mouseY, scroll)) break;
        }

        if (mouseY > this.height - 40) {
            scrollX -= scroll;
            if (totalWidth > width) {
                if (scrollX > 0) {
                    scrollX = 0;
                }
                int min = +this.width - totalWidth;
                if (scrollX < min) {
                    scrollX = min;
                }
            } else {
                scrollX = 0;
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        for (PanelHolder holder : holders) {
            if (holder.mouseClicked(mouseX, mouseY, mouseButton)) break;
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);

        for (PanelHolder holder : holders) {
            if (holder.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick)) break;
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);

        for (PanelHolder holder : holders) {
            if (holder.mouseReleased(mouseX, mouseY, state)) break;
        }
    }
}
