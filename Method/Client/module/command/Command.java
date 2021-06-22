package Method.Client.module.command;

import net.minecraft.client.Minecraft;

public abstract class Command {
  private final String command;
  
  public Command(String command) {
    this.command = command;
  }
  
  protected static Minecraft mc = Minecraft.getMinecraft();
  
  public abstract void runCommand(String paramString, String[] paramArrayOfString);
  
  public abstract String getDescription();
  
  public abstract String getSyntax();
  
  public String getCommand() {
    return this.command;
  }
}
