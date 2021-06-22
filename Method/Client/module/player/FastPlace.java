package Method.Client.module.player;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import net.minecraftforge.event.entity.living.LivingEvent;

public class FastPlace extends Module {
  Setting Delay = Main.setmgr.add(new Setting("Delay", this, 0.0D, 0.0D, 20.0D, false));
  
  Setting XP = Main.setmgr.add(new Setting("XP Only", this, false));
  
  Setting Crystal = Main.setmgr.add(new Setting("Crystal Only", this, false));
  
  public FastPlace() {
    super("FastPlace", 0, Category.PLAYER, "Place Blocks Faster");
  }
  
  public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
    if (this.XP.getValBoolean() && (mc.player.getHeldItemMainhand().getItem() instanceof net.minecraft.item.ItemExpBottle || mc.player
      .getHeldItemOffhand().getItem() instanceof net.minecraft.item.ItemExpBottle))
      mc.rightClickDelayTimer = (int)this.Delay.getValDouble(); 
    if (this.Crystal.getValBoolean() && (mc.player.getHeldItemMainhand().getItem() instanceof net.minecraft.item.ItemEndCrystal || mc.player
      .getHeldItemOffhand().getItem() instanceof net.minecraft.item.ItemEndCrystal))
      mc.rightClickDelayTimer = (int)this.Delay.getValDouble(); 
    if (!this.XP.getValBoolean() || !this.Crystal.getValBoolean())
      mc.rightClickDelayTimer = (int)this.Delay.getValDouble(); 
  }
}
