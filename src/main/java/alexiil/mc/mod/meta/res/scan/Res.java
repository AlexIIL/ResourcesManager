package alexiil.mc.mod.meta.res.scan;

public abstract class Res {
    public final String name;

    public Res(String name) {
        this.name = name;
    }

    public abstract boolean needsExporting();

    public abstract boolean hasMissing();

    public abstract boolean hasErrors();
}
