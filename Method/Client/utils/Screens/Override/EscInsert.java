package Method.Client.utils.Screens.Override;

import Method.Client.Main;
import Method.Client.utils.Screens.Screen;
import Method.Client.utils.system.WorldDownloader;
import Method.Client.utils.system.Wrapper;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.client.event.GuiScreenEvent;

public class EscInsert extends Screen {
  boolean Disconnect = false;
  
  ServerData lastserver;
  
  public void GuiScreenEventPre(GuiScreenEvent.ActionPerformedEvent.Pre event) {
    if (event.getGui() instanceof GuiIngameMenu && 
      (event.getButton()).id == 1 && !(mc.currentScreen instanceof GuiYesNo)) {
      mc.displayGuiScreen((GuiScreen)new GuiYesNo((GuiYesNoCallback)event.getGui(), "Disconnect", "Are you sure?", 1));
      this.Disconnect = true;
      event.setCanceled(true);
    } 
  }
  
  public void GuiScreenEventInit(GuiScreenEvent.InitGuiEvent.Post event) {
    if (event.getGui() instanceof GuiIngameMenu) {
      event.getButtonList().add(new GuiButton(554, (event.getGui()).width / 2 - 150, (event.getGui()).height / 4 + 32, 50, 20, "Relog"));
      event.getButtonList().add(new GuiButton(555, (event.getGui()).width / 2 - 150, (event.getGui()).height / 4 + 56, 50, 20, "Download"));
      event.getButtonList().add(new GuiButton(556, (event.getGui()).width / 2 - 150, (event.getGui()).height / 4 + 80, 50, 20, "ClickGui"));
    } 
  }
  
  public void GuiScreenEventPost(GuiScreenEvent.ActionPerformedEvent.Post event) {
    if (event.getGui() instanceof GuiYesNo && this.Disconnect) {
      if ((event.getButton()).id == 0) {
        this.Disconnect = false;
        mc.world.sendQuittingDisconnectingPacket();
        mc.loadWorld(null);
        mc.displayGuiScreen((GuiScreen)new GuiMainMenu());
      } 
      if ((event.getButton()).id == 1)
        mc.displayGuiScreen((GuiScreen)new GuiIngameMenu()); 
    } 
    if (event.getGui() instanceof GuiIngameMenu) {
      if ((event.getButton()).id == 554 && !mc.isIntegratedServerRunning()) {
        this.lastserver = mc.getCurrentServerData();
        this.Disconnect = false;
        mc.world.sendQuittingDisconnectingPacket();
        mc.loadWorld(null);
        Wrapper.INSTANCE.mc().displayGuiScreen((GuiScreen)new GuiMultiplayer((GuiScreen)new GuiMainMenu()));
        ServerAddress serveraddress = ServerAddress.fromString(this.lastserver.serverIP);
        mc.displayGuiScreen((GuiScreen)new GuiConnecting((GuiScreen)new GuiMainMenu(), mc, this.lastserver.serverIP, serveraddress.getPort()));
      } 
      if ((event.getButton()).id == 555)
        if (!WorldDownloader.Saving) {
          WorldDownloader.start();
        } else {
          WorldDownloader.stop();
        }  
      if ((event.getButton()).id == 556)
        mc.displayGuiScreen((GuiScreen)Main.ClickGui); 
    } 
  }
}
