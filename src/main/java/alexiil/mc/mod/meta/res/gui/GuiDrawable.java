package alexiil.mc.mod.meta.res.gui;

public abstract class GuiDrawable {
    /** @param x
     * @param y
     * @return The height that this component was. */
    public abstract int draw(int x, int y);

    public abstract int getWidth();
}
