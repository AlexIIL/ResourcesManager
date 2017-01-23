package alexiil.mc.mod.meta.res.gui;

import alexiil.mc.mod.meta.res.scan.ResFolder;

public class DisplayableFolder extends DisplayableFilePart {
    public final ResFolder folder;

    public DisplayableFolder(ResFolder folder) {
        super(folder);
        this.folder = folder;
    }

    @Override
    public void render(GuiPanelFolders panel, int x, int y, float partialTicks) {

        renderOverlays(panel, x, y);
    }
}
