package Method.Client.module.player;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraftforge.event.entity.living.LivingEvent;

public class SmallShield extends Module {
  Setting MainHand;
  
  Setting OffHand;
  
  Setting armPitch;
  
  Setting armYaw;
  
  public SmallShield() {
    super("SmallShield", 0, Category.PLAYER, "SmallShield");
    this.MainHand = Main.setmgr.add(new Setting("MainHand", this, 1.0D, 0.0D, 2.0D, true));
    this.OffHand = Main.setmgr.add(new Setting("OffHand", this, 2.0D, 0.0D, 2.0D, true));
    this.armPitch = Main.setmgr.add(new Setting("Arm Pitch", this, 0.0D, 90.0D, 360.0D, true));
    this.armYaw = Main.setmgr.add(new Setting("Arm Yaw", this, 0.0D, 220.0D, 360.0D, true));
  }
  
  public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
    ItemRenderer itemRenderer = mc.entityRenderer.itemRenderer;
    itemRenderer.equippedProgressOffHand = (float)(0.5D * this.OffHand.getValDouble());
    itemRenderer.equippedProgressMainHand = (float)(0.5D * this.MainHand.getValDouble());
    mc.player.renderArmPitch = (float)this.armPitch.getValDouble();
    mc.player.renderArmYaw = (float)this.armYaw.getValDouble();
  }
}
