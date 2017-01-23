package alexiil.mc.mod.meta.res.gui._new_windows;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import alexiil.mc.mod.meta.res.gui.GuiUtilRM;
import alexiil.mc.mod.meta.res.scan.Res;
import alexiil.mc.mod.meta.res.scan.ResFile;
import alexiil.mc.mod.meta.res.scan.ResFolder;

import buildcraft.lib.gui.GuiRectangle;

public class PanelExplorer extends PanelWindow {

    private ResFolder folder;

    public PanelExplorer(GuiMultiWindow gui) {
        super(gui);
    }

    public void setFolder(ResFolder folder) {
        if (this.folder != null) {
            // close the folder?
        }
        this.folder = folder;
        title = folder.guiElement.text;
    }

    @Override
    public void drawBackground(float partialTicks) {
        super.drawBackground(partialTicks);
        GuiUtilRM.drawRect(insideWindow, 0xFF_00_00_00);

        FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
        for (ResourceGuiLocation resLoc : getAllGuiResources()) {
            Res res = resLoc.res;
            String name = res == folder.parent ? "<UP>" : res.name;

            GuiRectangle rect = resLoc.rect;
            int colour = rect.contains(gui.mouse) ? 0xFF_00_00_00 : 0xFF_00_00_00 | name.hashCode();

            if (res instanceof ResFolder) {

            } else if (res instanceof ResFile) {

            }

            GuiUtilRM.drawRect(rect.x, rect.y, rect.getEndX(), rect.getEndY(), colour);
            int strW = font.getStringWidth(name);
            while (strW > 55) {
                name = name.substring(0, name.length() - 1);
                strW = font.getStringWidth(name);
            }

            font.drawString(name, rect.getCenterX() - strW / 2, rect.getEndY() - 12, -1);
        }
    }

    @Override
    protected void onMouseClicked(int button) {
        if (button == 0) {
            for (ResourceGuiLocation resLoc : getAllGuiResources()) {
                Res res = resLoc.res;
                GuiRectangle rect = resLoc.rect;
                if (rect.contains(gui.mouse)) {
                    if (res instanceof ResFolder) {
                        setFolder((ResFolder) res);
                    }
                    break;
                }
            }
        }
    }

    @Override
    protected void onMouseReleased(int state) {

    }

    private static class ResourceGuiLocation {
        public final Res res;
        public final GuiRectangle rect;

        public ResourceGuiLocation(Res res, GuiRectangle rect) {
            this.res = res;
            this.rect = rect;
        }
    }

    private List<Res> getAllResources() {
        List<Res> list = new ArrayList<>((folder.parent == null ? 0 : 1) + folder.folders.size() + folder.files.size());
        if (folder.parent != null) {
            list.add(folder.parent);
        }
        list.addAll(folder.folders.values());
        list.addAll(folder.files.values());
        return list;
    }

    private List<ResourceGuiLocation> getAllGuiResources() {

        int scale = 64;

        int gap = 10;

        int sx = insideWindow.getX() + gap;
        int sy = insideWindow.getY() + gap;

        int fx = 0;
        int fy = 0;
        int fMax = (insideWindow.getWidth() - gap * 2) / scale - 1;

        List<Res> resources = getAllResources();

        List<ResourceGuiLocation> list = new ArrayList<>(resources.size());
        for (Res res : resources) {
            int ix = sx + fx * scale;
            int iy = sy + fy * scale;
            GuiRectangle rect = new GuiRectangle(ix, iy, scale, scale);
            list.add(new ResourceGuiLocation(res, rect));
            fx++;
            if (fx > fMax) {
                fx = 0;
                fy++;
            }
        }
        return list;
    }
}
