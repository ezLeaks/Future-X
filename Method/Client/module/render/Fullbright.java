package Method.Client.module.render;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import java.util.Objects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class Fullbright extends Module {
  private float oldBrightness;
  
  Setting mode;
  
  public Fullbright() {
    super("Fullbright", 0, Category.RENDER, "Makes the screen bright");
    this.mode = Main.setmgr.add(new Setting("Mode", this, "Potion", new String[] { "Gamma", "Potion" }));
  }
  
  public void PlayerRespawnEvent(PlayerEvent.PlayerRespawnEvent event) {
    PotionEffect nv = new PotionEffect(Objects.<Potion>requireNonNull(Potion.getPotionById(16)), 9999999, 3);
    mc.player.addPotionEffect(nv);
  }
  
  public void onEnable() {
    if (this.mode.getValString().equalsIgnoreCase("Gamma")) {
      this.oldBrightness = mc.gameSettings.gammaSetting;
      mc.gameSettings.gammaSetting = 10.0F;
    } 
    if (this.mode.getValString().equalsIgnoreCase("Potion")) {
      PotionEffect nv = new PotionEffect(Objects.<Potion>requireNonNull(Potion.getPotionById(16)), 9999999, 3);
      mc.player.addPotionEffect(nv);
    } 
    super.onEnable();
  }
  
  public void onDisable() {
    mc.gameSettings.gammaSetting = this.oldBrightness;
    mc.player.removeActivePotionEffect(Potion.getPotionById(16));
    super.onDisable();
  }
}
