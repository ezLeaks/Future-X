package Method.Client.module.player;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Wrapper;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.Items;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Disconnect extends Module {
  Setting leaveHealth = Main.setmgr.add(new Setting("LeaveHealth", this, 4.0D, 0.0D, 20.0D, true));
  
  Setting Totem = Main.setmgr.add(new Setting("Totem", this, false));
  
  Setting Playersight = Main.setmgr.add(new Setting("Player", this, false));
  
  public Disconnect() {
    super("Auto Disconnect", 0, Category.PLAYER, "Disconnect on low health");
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (mc.player.getHealth() <= this.leaveHealth.getValDouble()) {
      if (!this.Totem.getValBoolean()) {
        Quit();
      } else if (Totemcount() < 1) {
        Quit();
      } 
      toggle();
    } 
  }
  
  public static int Totemcount() {
    int totem = 0;
    if (mc.player.getHeldItemOffhand().getItem().equals(Items.TOTEM_OF_UNDYING))
      totem++; 
    for (int i = 9; i <= 44; i++) {
      if (MC.player.inventoryContainer.getSlot(i).getStack().getItem() == Items.TOTEM_OF_UNDYING)
        totem++; 
    } 
    return totem;
  }
  
  private void Quit() {
    boolean flag = Wrapper.INSTANCE.mc().isIntegratedServerRunning();
    mc.world.sendQuittingDisconnectingPacket();
    Wrapper.INSTANCE.mc().loadWorld(null);
    if (flag) {
      Wrapper.INSTANCE.mc().displayGuiScreen((GuiScreen)new GuiMainMenu());
    } else {
      Wrapper.INSTANCE.mc().displayGuiScreen((GuiScreen)new GuiMultiplayer((GuiScreen)new GuiMainMenu()));
    } 
  }
}
