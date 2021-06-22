package Method.Client.module.command;

import Method.Client.clickgui.ClickGui;
import Method.Client.clickgui.component.Frame;
import Method.Client.utils.visual.ChatUtils;

public class ResetGui extends Command {
  public ResetGui() {
    super("ResetGui");
  }
  
  public void runCommand(String s, String[] args) {
    try {
      int xOffset = 5;
      for (Frame frame : ClickGui.frames) {
        frame.setY(20);
        frame.setX(xOffset + 10);
        xOffset += frame.getWidth();
      } 
      ChatUtils.message("Guireset!");
    } catch (Exception e) {
      ChatUtils.error("Usage: " + getSyntax());
    } 
  }
  
  public String getDescription() {
    return "ResetGui";
  }
  
  public String getSyntax() {
    return "ResetGui";
  }
}
