package Method.Client.module.command;

import Method.Client.Main;
import Method.Client.managers.FileManager;
import Method.Client.managers.Setting;
import Method.Client.module.Module;
import Method.Client.module.ModuleManager;
import Method.Client.utils.visual.ChatUtils;

public class Reset extends Command {
  public Reset() {
    super("Reset");
  }
  
  public void runCommand(String s, String[] args) {
    try {
      Module m = ModuleManager.getModuleByName(args[0]);
      if (m != null) {
        if (m.isToggled())
          m.toggle(); 
        if (Main.setmgr.getSettingsByMod(m) != null) {
          for (Setting SET : Main.setmgr.getSettingsByMod(m))
            Main.setmgr.getSettings().remove(SET); 
          m.setup();
          FileManager.SaveMods();
          FileManager.saveframes();
        } 
        ModuleManager.addModule(m);
        ChatUtils.message(m + " Returned to Factory");
      } 
    } catch (Exception e) {
      ChatUtils.error("Usage: " + getSyntax());
    } 
  }
  
  public String getDescription() {
    return "Resets a module to factory defaults";
  }
  
  public String getSyntax() {
    return "Reset <module>";
  }
}
