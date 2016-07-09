package alexiil.mc.mod.meta.res.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import alexiil.mc.mod.meta.res.scan.AllBlockModels;
import alexiil.mc.mod.meta.res.scan.AllBlockModels.BlockModels;
import alexiil.mc.mod.meta.res.scan.AllItemModels;
import alexiil.mc.mod.meta.res.scan.AllItemModels.ItemModels;
import alexiil.mc.mod.meta.res.scan.ResourceScanner;

public class GuiResourcesMain extends GuiScreen {
    private final List<String> providersUsed = new ArrayList<>();
    private final List<String> domains = new ArrayList<>();

    private void initDomains() {
        providersUsed.clear();
        domains.clear();
        ResourceScanner.INSTANCE.scan();
        providersUsed.addAll(ResourceScanner.INSTANCE.providers.stream().map(provider -> provider.name).collect(Collectors.toList()));

        for (String s : providersUsed) {
            System.out.println("Used domain " + s);
        }

        for (ResourceLocation loc : ResourceScanner.INSTANCE.resources.descendingKeySet()) {
            if (!domains.contains(loc.toString())) {
                domains.add(loc.toString());
            }
        }
        Collections.sort(domains);
    }

    @Override
    public void initGui() {
        initDomains();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawBackground(0);
        super.drawScreen(mouseX, mouseY, partialTicks);

        int y = 30;
        for (String provider : providersUsed) {
            fontRendererObj.drawString(provider, 30, y, -1);
            y += 12;
        }

        y = 30;
        for (String domain : domains) {
            fontRendererObj.drawString(domain, 100, y, -1);
            y += 12;
        }

        y = 30;
        for (BlockModels b : AllBlockModels.blocks.values()) {
            Block block = b.block;

        }

        y = 30;
        for (ItemModels i : AllItemModels.items.values()) {
            Item item = i.item;
            ItemStack stack = new ItemStack(item);
            Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(stack, 200, y);
            String string = stack.getDisplayName();

            List<ItemStack> subs = new ArrayList<>();
            item.getSubItems(item, null, subs);
            if (subs.size() > 1) {
                string += "  (+" + (subs.size() - 1) + ")";
            }

            fontRendererObj.drawString(string, 220, y + 3, -1);
            y += 20;
        }
    }
}
