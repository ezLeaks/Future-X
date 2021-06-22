package Method.Client.module.player;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Patcher.Events.PlayerMoveEvent;
import Method.Client.utils.system.Connection;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class God extends Module {
  Setting mode;
  
  Setting Footsteps;
  
  private Entity riding;
  
  public God() {
    super("God", 0, Category.PLAYER, "Take no damage in certain situations");
    this.mode = Main.setmgr.add(new Setting("God mode", this, "Vanilla", new String[] { "Vanilla", "TickMode", "Riding" }));
    this.Footsteps = Main.setmgr.add(new Setting("Footsteps", this, false, this.mode, "Riding", 1));
  }
  
  public void onEnable() {
    if (this.mode.getValString().equalsIgnoreCase("Riding"))
      if (mc.player.getRidingEntity() != null) {
        this.riding = mc.player.getRidingEntity();
        mc.player.dismountRidingEntity();
        mc.world.removeEntity(this.riding);
        mc.player.setPosition(mc.player.getPosition().getX(), (mc.player.getPosition().getY() - 1), mc.player.getPosition().getZ());
      }  
  }
  
  public void onDisable() {
    if (this.mode.getValString().equalsIgnoreCase("Riding") && 
      this.riding != null)
      mc.player.connection.sendPacket((Packet)new CPacketUseEntity(this.riding, EnumHand.MAIN_HAND)); 
    if (this.mode.getValString().equalsIgnoreCase("TickMode"))
      mc.player.respawnPlayer(); 
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (this.mode.getValString().equalsIgnoreCase("Riding") && side == Connection.Side.OUT) {
      if (packet instanceof CPacketUseEntity) {
        CPacketUseEntity packet2 = (CPacketUseEntity)packet;
        if (this.riding != null) {
          Entity entity = packet2.getEntityFromWorld((World)mc.world);
          if (entity != null) {
            this.riding.posX = entity.posX;
            this.riding.posY = entity.posY;
            this.riding.posZ = entity.posZ;
            this.riding.rotationYaw = mc.player.rotationYaw;
            Movepackets(mc);
          } 
        } 
      } 
      if (packet instanceof CPacketPlayer.Position || packet instanceof CPacketPlayer.PositionRotation)
        return false; 
    } 
    return (!(packet instanceof net.minecraft.network.play.client.CPacketConfirmTeleport) || !this.mode.getValString().equalsIgnoreCase("Vanilla"));
  }
  
  private void Movepackets(Minecraft mc) {
    mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(mc.player.rotationYaw, mc.player.rotationPitch, true));
    mc.player.connection.sendPacket((Packet)new CPacketInput(mc.player.movementInput.moveForward, mc.player.movementInput.moveStrafe, false, false));
    mc.player.connection.sendPacket((Packet)new CPacketVehicleMove(this.riding));
  }
  
  public void onPlayerMove(PlayerMoveEvent event) {
    if (this.mode.getValString().equalsIgnoreCase("Riding") && 
      this.riding != null) {
      this.riding.posX = mc.player.posX;
      this.riding.posY = mc.player.posY + (this.Footsteps.getValBoolean() ? 0.3F : 0.0F);
      this.riding.posZ = mc.player.posZ;
      this.riding.rotationYaw = mc.player.rotationYaw;
      Movepackets(mc);
    } 
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (this.mode.getValString().equalsIgnoreCase("TickMode")) {
      mc.player.setHealth(20.0F);
      mc.player.getFoodStats().setFoodLevel(20);
      mc.player.isDead = false;
      if (mc.currentScreen instanceof net.minecraft.client.gui.GuiGameOver)
        mc.displayGuiScreen(null); 
    } 
    if (mc.currentScreen instanceof net.minecraft.client.gui.GuiGameOver && this.mode.getValString().equalsIgnoreCase("Tickmode"))
      try {
        mc.player.respawnPlayer();
      } catch (Exception e) {
        e.printStackTrace();
      }  
  }
}
