package Method.Client.module.command;

import Method.Client.utils.visual.ChatUtils;

public class ClearChat extends Command {
  public ClearChat() {
    super("Clear");
  }
  
  public void runCommand(String s, String[] args) {
    try {
      mc.ingameGUI.getChatGUI().clearChatMessages(true);
      ChatUtils.message("Cleared Chat");
    } catch (Exception e) {
      ChatUtils.error("Usage: " + getSyntax());
    } 
  }
  
  public String getDescription() {
    return "Clears Chat";
  }
  
  public String getSyntax() {
    return "Clear";
  }
}
