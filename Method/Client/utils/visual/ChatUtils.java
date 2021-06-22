package Method.Client.utils.visual;

import Method.Client.utils.system.Wrapper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class ChatUtils {
  public static void component(ITextComponent component) {
    if (Wrapper.INSTANCE.player() == null)
      return; 
    (Wrapper.INSTANCE.mc()).ingameGUI.getChatGUI();
    (Wrapper.INSTANCE.mc()).ingameGUI.getChatGUI()
      .printChatMessage((new TextComponentTranslation("", new Object[0]))
        .appendSibling(component));
  }
  
  public static void message(String message) {
    component((ITextComponent)new TextComponentTranslation("§8FutureX§7 " + message, new Object[0]));
  }
  
  public static void warning(String message) {
    message("§8[§eWARNING§8]§e " + message);
  }
  
  public static void error(String message) {
    message("§8[§4ERROR§8]§c " + message);
  }
}
