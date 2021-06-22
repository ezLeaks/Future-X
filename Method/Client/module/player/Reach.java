package Method.Client.module.player;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import net.minecraft.entity.player.EntityPlayer;

public class Reach extends Module {
  Setting Reach = Main.setmgr.add(this.Reach = new Setting("Reach", this, 10.0D, 0.0D, 20.0D, true));
  
  public Reach() {
    super("Reach", 0, Category.PLAYER, "Reach");
  }
  
  public void onEnable() {
    mc.player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).setBaseValue(this.Reach.getValDouble());
  }
  
  public void onDisable() {
    mc.player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).setBaseValue(5.0D);
  }
}
