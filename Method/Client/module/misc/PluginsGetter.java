package Method.Client.module.misc;

import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import Method.Client.utils.system.Wrapper;
import Method.Client.utils.visual.ChatUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import joptsimple.internal.Strings;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.network.play.server.SPacketTabComplete;

public class PluginsGetter extends Module {
  public PluginsGetter() {
    super("PluginsGetter", 0, Category.MISC, "Trys Plugins Getter");
  }
  
  public void onEnable() {
    if (mc.player == null)
      return; 
    Wrapper.INSTANCE.sendPacket((Packet)new CPacketTabComplete("/", null, false));
    super.onEnable();
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (packet instanceof SPacketTabComplete) {
      SPacketTabComplete s3APacketTabComplete = (SPacketTabComplete)packet;
      List<String> plugins = new ArrayList<>();
      String[] commands = s3APacketTabComplete.getMatches();
      for (String s : commands) {
        String[] command = s.split(":");
        if (command.length > 1) {
          String pluginName = command[0].replace("/", "");
          if (!plugins.contains(pluginName))
            plugins.add(pluginName); 
        } 
      } 
      Collections.sort(plugins);
      if (!plugins.isEmpty()) {
        ChatUtils.message("Plugins §7(§8" + plugins.size() + "§7): §9" + Strings.join(plugins.<String>toArray(new String[0]), "§7, §9"));
      } else {
        ChatUtils.error("No plugins found.");
      } 
      toggle();
    } 
    return true;
  }
}
