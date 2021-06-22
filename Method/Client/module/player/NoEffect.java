package Method.Client.module.player;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.proxy.Overrides.EntityRenderMixin;
import Method.Client.utils.system.Connection;
import java.util.Objects;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class NoEffect extends Module {
  Setting hurtcam = Main.setmgr.add(new Setting("hurtcam", this, false));
  
  Setting Levitate = Main.setmgr.add(new Setting("Levitate", this, false));
  
  Setting weather = Main.setmgr.add(new Setting("weather", this, false));
  
  Setting Time = Main.setmgr.add(new Setting("Time", this, 0.0D, 0.0D, 18000.0D, true));
  
  Setting Settime = Main.setmgr.add(new Setting("Settime", this, false));
  
  Setting fire = Main.setmgr.add(new Setting("fire", this, false));
  
  Setting push = Main.setmgr.add(new Setting("push", this, false));
  
  Setting NoVoid = Main.setmgr.add(new Setting("NoVoid", this, false));
  
  public static Setting NoScreenEvents;
  
  public NoEffect() {
    super("NoEffect", 0, Category.PLAYER, "Prevent effects such as weather");
  }
  
  public void setup() {
    Main.setmgr.add(NoScreenEvents = new Setting("NoScreenEvents", this, false));
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    return (!(packet instanceof net.minecraft.network.play.server.SPacketTimeUpdate) || !this.Settime.getValBoolean());
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    EntityRenderMixin.Camswitch = !this.hurtcam.getValBoolean();
    if (this.weather.getValBoolean()) {
      mc.world.getWorldInfo().setRaining(false);
      mc.world.setRainStrength(0.0F);
      mc.world.getWorldInfo().setThunderTime(0);
      mc.world.getWorldInfo().setThundering(false);
    } 
    if (this.push.getValBoolean())
      mc.player.entityCollisionReduction = 1.0F; 
    if (this.Levitate.getValBoolean() && 
      mc.player.isPotionActive(Objects.<Potion>requireNonNull(Potion.getPotionById(25))))
      mc.player.removeActivePotionEffect(Potion.getPotionById(25)); 
    if (this.NoVoid.getValBoolean()) {
      if (mc.player.posY <= 0.5D) {
        mc.player.moveVertical = 10.0F;
        mc.player.jump();
      } 
      mc.player.motionY += 0.1D;
    } 
  }
  
  public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
    if (mc.player.isBurning() && this.fire.getValBoolean()) {
      mc.player.connection.sendPacket((Packet)new CPacketPlayer(mc.player.onGround));
      event.getEntityLiving().extinguish();
      mc.player.setFire(0);
    } 
    if (this.Settime.getValBoolean())
      mc.world.setWorldTime((long)this.Time.getValDouble()); 
  }
}
