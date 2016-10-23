package alexiil.mc.mod.meta.res.gui;

import net.minecraft.client.Minecraft;

public class GuiString extends GuiDrawable implements Comparable<GuiString> {
    public final String text, id;

    public GuiString(String text) {
        this(text, text);
    }

    public GuiString(String text, String id) {
        this.text = text;
        this.id = id;
    }

    @Override
    public int draw(int x, int y) {
        Minecraft.getMinecraft().fontRendererObj.drawString(text, x, y, -1);
        return 12;
    }

    @Override
    public int compareTo(GuiString o) {
        return text.compareTo(o.text);
    }

    @Override
    public int getWidth() {
        return Minecraft.getMinecraft().fontRendererObj.getStringWidth(text);
    }

    @Override
    public String toString() {
        return text + "(" + id + ")";
    }
}
