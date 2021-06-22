package Method.Client.module.movement;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Utils;
import Method.Client.utils.system.Connection;
import Method.Client.utils.system.Wrapper;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Sneak extends Module {
  Setting mode = Main.setmgr.add(new Setting("Mode", this, "Legit", new String[] { "Legit", "Packet" }));
  
  Setting Antisneak = Main.setmgr.add(new Setting("Antisneak", this, false));
  
  Setting fullSprint = Main.setmgr.add(new Setting("FullSprint", this, false, this.Antisneak, 2));
  
  public Sneak() {
    super("Sneak", 0, Category.MOVEMENT, "Sneak");
  }
  
  public void onDisable() {
    if (this.mode.getValString().equalsIgnoreCase("legit"))
      mc.gameSettings.keyBindSneak.pressed = false; 
    if (this.mode.getValString().equalsIgnoreCase("Packet"))
      mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.STOP_SNEAKING)); 
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (this.Antisneak.getValBoolean()) {
      EntityPlayerSP player = mc.player;
      GameSettings settings = Wrapper.INSTANCE.mcSettings();
      if (player.onGround && settings.keyBindSneak.isKeyDown()) {
        if (!this.fullSprint.getValBoolean() && settings.keyBindForward.isKeyDown()) {
          player.setSprinting(Utils.isMoving((Entity)player));
        } else if (this.fullSprint.getValBoolean()) {
          player.setSprinting(Utils.isMoving((Entity)player));
        } 
        if (settings.keyBindRight.isKeyDown() || settings.keyBindLeft
          .isKeyDown() || settings.keyBindBack
          .isKeyDown()) {
          if (settings.keyBindBack.isKeyDown()) {
            player.motionX *= 1.268D;
            player.motionZ *= 1.268D;
          } else {
            player.motionX *= 1.252D;
            player.motionZ *= 1.252D;
          } 
        } else {
          player.motionX *= 1.2848D;
          player.motionZ *= 1.2848D;
        } 
      } 
    } 
    if (this.mode.getValString().equalsIgnoreCase("Legit"))
      mc.gameSettings.keyBindSneak.pressed = true; 
    if (this.mode.getValString().equalsIgnoreCase("Packet")) {
      EntityPlayerSP player = mc.player;
      if (!Utils.isMoving((Entity)mc.player)) {
        player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)player, CPacketEntityAction.Action.START_SNEAKING));
        player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)player, CPacketEntityAction.Action.STOP_SNEAKING));
      } 
      if (Utils.isMoving((Entity)mc.player)) {
        player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)player, CPacketEntityAction.Action.STOP_SNEAKING));
        player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)player, CPacketEntityAction.Action.START_SNEAKING));
      } 
    } 
    super.onClientTick(event);
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (this.Antisneak.getValBoolean() && 
      side == Connection.Side.OUT && packet instanceof CPacketEntityAction) {
      CPacketEntityAction p = (CPacketEntityAction)packet;
      return (p.getAction() != CPacketEntityAction.Action.STOP_SNEAKING);
    } 
    return true;
  }
}
