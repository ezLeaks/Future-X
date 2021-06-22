package Method.Client.module.Profiles;

import Method.Client.Main;
import Method.Client.managers.FileManager;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.module.ModuleManager;
import java.util.ArrayList;

public class Profiletem extends Module {
  public String name;
  
  public Profiletem(String name) {
    super(name, 0, Category.PROFILES, name);
    this.name = name;
  }
  
  public void setsave() {
    this.StoredModules.clear();
    this.StoredModules.addAll(ModuleManager.toggledModules);
    this.StoredModules.removeIf(module -> (module.getCategory().equals(Category.PROFILES) || module.getCategory().equals(Category.ONSCREEN)));
    this.StoredSettings.clear();
    for (Module storedModule : this.StoredModules) {
      for (Setting setting : Main.setmgr.getSettingsByMod(storedModule))
        this.StoredSettings.add(new Setting(setting)); 
    } 
    FileManager.savePROFILES();
  }
  
  public void setdelete() {
    this.StoredModules.clear();
    this.StoredSettings.clear();
  }
  
  public void onEnable() {
    ArrayList<Module> remove = new ArrayList<>();
    ModuleManager.toggledModules.forEach(module -> {
          if (!module.getCategory().equals(Category.PROFILES) && !module.getCategory().equals(Category.ONSCREEN))
            remove.add(module); 
        });
    remove.forEach(module -> {
          if (!module.getCategory().equals(Category.PROFILES) && !module.getCategory().equals(Category.ONSCREEN))
            module.setToggled(false); 
        });
    ModuleManager.toggledModules.removeAll(remove);
    for (Module N : this.StoredModules) {
      if (!N.getCategory().equals(Category.PROFILES) && !N.getCategory().equals(Category.ONSCREEN)) {
        N.toggle();
        ArrayList<Setting> change = new ArrayList<>();
        for (Setting setting : this.StoredSettings) {
          if (setting.getParentMod().equals(N))
            change.add(setting); 
        } 
        Main.setmgr.setSettingsByMod(N, change);
      } 
    } 
    toggle();
  }
}
