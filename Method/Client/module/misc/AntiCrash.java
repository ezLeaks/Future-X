package Method.Client.module.misc;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import java.util.Objects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AntiCrash extends Module {
  Setting slime;
  
  Setting offhand;
  
  Setting Sound;
  
  public AntiCrash() {
    super("AntiCrash", 0, Category.MISC, "Anti Crash");
    this.slime = Main.setmgr.add(new Setting("slime", this, true));
    this.offhand = Main.setmgr.add(new Setting("offhand", this, true));
    this.Sound = Main.setmgr.add(new Setting("Sound", this, true));
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (side == Connection.Side.IN && packet instanceof SPacketSoundEffect && this.offhand.getValBoolean())
      return (((SPacketSoundEffect)packet).getSound() != SoundEvents.ITEM_ARMOR_EQUIP_GENERIC); 
    if (packet instanceof SPacketSoundEffect && this.Sound.getValBoolean()) {
      SPacketSoundEffect packet2 = (SPacketSoundEffect)packet;
      return (packet2.getCategory() != SoundCategory.PLAYERS || packet2.getSound() != SoundEvents.ITEM_ARMOR_EQUIP_GENERIC);
    } 
    return true;
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (Objects.nonNull(mc.world) && this.slime.getValBoolean())
      mc.world.loadedEntityList.forEach(e -> {
            if (e instanceof EntitySlime) {
              EntitySlime slime = (EntitySlime)e;
              if (slime.getSlimeSize() > 4)
                mc.world.removeEntity(e); 
            } 
          }); 
  }
}
