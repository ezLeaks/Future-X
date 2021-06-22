package Method.Client.module.player;

import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.EnumHand;

public class AutoFish extends Module {
  public AutoFish() {
    super("AutoFish", 0, Category.PLAYER, "AutoFish");
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (packet instanceof SPacketSoundEffect) {
      SPacketSoundEffect packet2 = (SPacketSoundEffect)packet;
      if (packet2.getSound().equals(SoundEvents.ENTITY_BOBBER_SPLASH))
        (new Thread(() -> {
              try {
                mc.playerController.processRightClick((EntityPlayer)mc.player, mc.player.world, EnumHand.MAIN_HAND);
                Thread.sleep(300L);
                mc.playerController.processRightClick((EntityPlayer)mc.player, mc.player.world, EnumHand.MAIN_HAND);
              } catch (Exception exception) {}
            })).start(); 
    } 
    return true;
  }
}
