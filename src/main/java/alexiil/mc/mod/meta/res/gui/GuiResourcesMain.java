package alexiil.mc.mod.meta.res.gui;

import java.io.IOException;
import java.util.*;

import org.lwjgl.input.Mouse;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import alexiil.mc.mod.meta.res.scan.AllBlockModels;
import alexiil.mc.mod.meta.res.scan.AllItemModels;
import alexiil.mc.mod.meta.res.scan.ResourceScanner;

public class GuiResourcesMain extends GuiScreen {
    private final GuiSelectable<GuiString> selectMod;
    private final GuiSelectable<GuiString> selectType;
    private final GuiSelectable<GuiString> selectThing;

    private final GuiString typeBlocks = new GuiString("Blocks");
    private final GuiString typeItems = new GuiString("Items");
    private final GuiString typeTextures = new GuiString("Textures");

    // Map of mod -> things
    private final Map<GuiString, List<GuiString>> blocks = new HashMap<>();
    private final Map<GuiString, List<GuiString>> items = new HashMap<>();
    private final Map<GuiString, List<GuiString>> textures = new HashMap<>();

    private void initDomains() {
        ResourceScanner.INSTANCE.scan();
        List<GuiString> types = new ArrayList<>();
        types.add(typeBlocks);
        types.add(typeItems);
        types.add(typeTextures);
        Collections.sort(types);
        selectType.setList(types);

        List<GuiString> guiMods = new ArrayList<>();
        selectMod.setList(guiMods);
        List<String> mods = new ArrayList<>();

        for (Block b : AllBlockModels.blocks.descendingKeySet()) {
            ResourceLocation loc = b.getRegistryName();
            final GuiString mod;
            if (!mods.contains(loc.getResourceDomain())) {
                mods.add(loc.getResourceDomain());
                guiMods.add(mod = new GuiString(loc.getResourceDomain()));
            } else {
                mod = guiMods.get(mods.indexOf(loc.getResourceDomain()));
            }
            if (!blocks.containsKey(mod)) {
                blocks.put(mod, new ArrayList<>());
            }
            blocks.get(mod).add(new GuiString(b.getLocalizedName()));
        }

        for (List<GuiString> list : blocks.values()) {
            Collections.sort(list);
        }

        for (Item i : AllItemModels.items.descendingKeySet()) {
            ResourceLocation loc = i.getRegistryName();
            final GuiString mod;
            if (!mods.contains(loc.getResourceDomain())) {
                mods.add(loc.getResourceDomain());
                guiMods.add(mod = new GuiString(loc.getResourceDomain()));
            } else {
                mod = guiMods.get(mods.indexOf(loc.getResourceDomain()));
            }
            if (!items.containsKey(mod)) {
                items.put(mod, new ArrayList<>());
            }
            items.get(mod).add(new GuiString(new ItemStack(i).getDisplayName()));
        }

        for (List<GuiString> list : items.values()) {
            Collections.sort(list);
        }

        Collections.sort(mods);
    }

    public GuiResourcesMain() {
        selectMod = new GuiSelectable<>(this, 30, 100, this::onSelectMod);
        selectType = new GuiSelectable<>(this, 140, 100, this::onSelectType);
        selectThing = new GuiSelectable<>(this, 250, 100, this::onSelectThing);
        initDomains();
    }

    private void onSelectMod(GuiString mod) {
        if (selectType.isSelected()) {
            onSelectType(selectType.getSelected());
        }
    }

    private void onSelectType(GuiString type) {
        if (type == typeBlocks) {
            selectThing.setList(blocks.get(selectMod.getSelected()));
        } else if (type == typeItems) {
            selectThing.setList(items.get(selectMod.getSelected()));
        } else {
            selectThing.setList(null);
        }
    }

    private void onSelectThing(GuiString thing) {

    }

    @Override
    public void initGui() {

    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        selectMod.tick();
        selectType.tick();
        selectThing.tick();
//        selectThing.setList(blocks.values().stream().filter((v) -> v.size() > 10).findAny().get());
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawBackground(0);
        super.drawScreen(mouseX, mouseY, partialTicks);

        selectMod.draw(mouseX, mouseY);
        if (selectMod.isSelected() || true) {
            selectType.draw(mouseX, mouseY);
            if (selectType.isSelected() || true) {
                selectThing.draw(mouseX, mouseY);
            }
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
        super.handleMouseInput();
        float scroll = Mouse.getEventDWheel();
        scroll /= 4.0F;
        tryScroll(mouseX, scroll, selectMod);
        tryScroll(mouseX, scroll, selectType);
        tryScroll(mouseX, scroll, selectThing);
    }

    private static void tryScroll(int mouseX, float scroll, GuiSelectable<GuiString> selected) {
        if (mouseX > selected.x && mouseX <= selected.x + selected.width) {
            selected.onScroll(scroll);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
    }
}
