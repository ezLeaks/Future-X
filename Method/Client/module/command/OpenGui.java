package Method.Client.module.command;

import Method.Client.Main;
import Method.Client.utils.visual.ChatUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextFormatting;

public class OpenGui extends Command {
  Thread t;
  
  public OpenGui() {
    super("Gui");
  }
  
  public void runCommand(String s, String[] args) {
    try {
      mc.displayGuiScreen(null);
      Thread t = new Thread(new ThreadDemo());
      t.start();
      mc.mouseHelper.ungrabMouseCursor();
      ChatUtils.message(TextFormatting.GOLD + "Tried to open Gui");
    } catch (Exception e) {
      ChatUtils.error("Usage: " + getSyntax());
    } 
  }
  
  public String getDescription() {
    return "Opens gui";
  }
  
  public String getSyntax() {
    return "gui";
  }
  
  private static class ThreadDemo implements Runnable {
    private ThreadDemo() {}
    
    public void run() {
      try {
        Thread.sleep(25L);
      } catch (InterruptedException e) {
        e.printStackTrace();
      } 
      Command.mc.displayGuiScreen((GuiScreen)Main.ClickGui);
    }
  }
}
