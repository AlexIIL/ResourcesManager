package alexiil.mc.mod.meta.res;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.resources.I18n;

import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import alexiil.mc.mod.meta.res.gui._new_windows.GuiResources;

@Mod(modid = ResourcesManager.MODID, version = ResourcesManager.VERSION, dependencies = "required-after:buildcraftlib@[8.0.0-alpha,)", clientSideOnly = true)
public class ResourcesManager {
    public static final String MODID = "resources_manager";
    public static final String VERSION = "${version}";
    public static final String MC_VERSION = "${mcversion}";

    private static final int BUTTON_ID = 10004;
    private static final int BUTTON_WIDTH = 98;
    private static final int BUTTON_HEIGHT = 20;

    @EventHandler
    public static void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(ResourcesManager.class);
    }

    @SubscribeEvent
    public static void onInitGuiScreen(InitGuiEvent.Post event) {
        if (event.getGui() instanceof GuiMainMenu) {
            GuiMainMenu gui = (GuiMainMenu) event.getGui();
            event.getButtonList().add(ButtonGotoResourcesManager.create(gui));
        }
    }

    public static final class ButtonGotoResourcesManager extends GuiButton {
        public static ButtonGotoResourcesManager create(GuiMainMenu gui) {
            int x = gui.width / 2 - 100;
            int y = gui.height / 4 + 156;
            return new ButtonGotoResourcesManager(x, y);
        }

        private ButtonGotoResourcesManager(int x, int y) {
            super(BUTTON_ID, x, y, BUTTON_WIDTH, BUTTON_HEIGHT, "Resources...");
        }

        @Override
        public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
            if (super.mousePressed(mc, mouseX, mouseY)) {
                // mc.displayGuiScreen(new GuiResourcesMain());
                mc.displayGuiScreen(new GuiResources());
                return true;
            }
            return false;
        }
    }
}
