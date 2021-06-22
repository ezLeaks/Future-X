package Method.Client.module.command;

import Method.Client.utils.visual.ChatUtils;
import java.awt.Desktop;
import net.minecraft.client.Minecraft;

public class OpenFolder extends Command {
  public OpenFolder() {
    super("OpenFolder ");
  }
  
  public void runCommand(String s, String[] args) {
    try {
      Desktop.getDesktop().open((Minecraft.getMinecraft()).gameDir);
      ChatUtils.message("Local .Minecraft Folder Opened");
    } catch (Exception e) {
      ChatUtils.error("Usage: " + getSyntax());
    } 
  }
  
  public String getDescription() {
    return "Opens Folder for .minecraft";
  }
  
  public String getSyntax() {
    return "OpenFolder";
  }
}
