package alexiil.mc.mod.meta.res.gui;

import net.minecraft.client.gui.FontRenderer;

import alexiil.mc.mod.meta.res.scan.Res;

public abstract class DisplayableFilePart {
    private final Res object;

    public DisplayableFilePart(Res object) {
        this.object = object;
    }

    public abstract void render(GuiPanelFolders panel, int x, int y, float partialTicks);

    protected void renderOverlays(GuiPanelFolders panel, int x, int y) {
        
        

        FontRenderer fr = panel.gui.getFontRenderer();
        int width = fr.getStringWidth(object.name);
        fr.drawString(object.name, x - width / 2, y + 40, -1);
    }
}
