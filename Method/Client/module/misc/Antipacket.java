package Method.Client.module.misc;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Screens.Custom.Packet.AntiPacketGui;
import Method.Client.utils.Screens.Custom.Packet.AntiPacketPacket;
import Method.Client.utils.system.Connection;
import net.minecraft.client.gui.GuiScreen;

public class Antipacket extends Module {
  Setting Gui;
  
  public Antipacket() {
    super("Antipacket", 0, Category.MISC, "Cancel Packets");
    this.Gui = Main.setmgr.add(new Setting("Gui", this, (GuiScreen)Main.AntiPacketgui));
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    System.out.println(packet.toString());
    for (AntiPacketPacket packet2 : AntiPacketGui.GetPackets()) {
      if (packet.getClass().isInstance(packet2.packet))
        return false; 
    } 
    return true;
  }
}
