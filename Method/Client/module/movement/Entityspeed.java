package Method.Client.module.movement;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Entityspeed extends Module {
  Setting mode = Main.setmgr.add(new Setting("Mode", this, "Vanilla", new String[] { "Vanilla", "Glide", "Tp", "TpUpdate" }));
  
  Setting speed = Main.setmgr.add(new Setting("Speed", this, 1.0D, 0.01D, 2.0D, false));
  
  Setting foodview = Main.setmgr.add(new Setting("foodbar view", this, true));
  
  Setting antiStuck = Main.setmgr.add(new Setting("antiStuck", this, true));
  
  Setting Jump = Main.setmgr.add(new Setting("Horse Jump", this, false));
  
  Setting isAirBorne = Main.setmgr.add(new Setting("Airmode Mode", this, "Default", new String[] { "Default", "False", "True" }));
  
  Setting Modifyfalldist = Main.setmgr.add(new Setting("No Falldist", this, false));
  
  Setting Yawlock = Main.setmgr.add(new Setting("Yawlock", this, false));
  
  Setting boatInputsfalse = Main.setmgr.add(new Setting("Dont Row Boat", this, false));
  
  public Entityspeed() {
    super("EntitySpeed", 0, Category.MOVEMENT, "Entity Speed + Control");
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (this.Jump.getValBoolean()) {
      mc.player.horseJumpPower = 1.0F;
      mc.player.horseJumpPowerCounter = -10;
    } 
    if (mc.world != null && mc.player.getRidingEntity() != null) {
      Entity riding = mc.player.getRidingEntity();
      if (this.Modifyfalldist.getValBoolean())
        riding.fallDistance = 0.0F; 
      riding.isAirBorne = this.isAirBorne.getValString().equalsIgnoreCase("Default") ? riding.isAirBorne : this.isAirBorne.getValString().equalsIgnoreCase("True");
      if (mc.player.isRiding())
        steerEntity(riding); 
    } 
  }
  
  private void steerEntity(Entity entity) {
    double[] directionSpeedVanilla = Utils.directionSpeed(this.speed.getValDouble());
    if ((this.mode.getValString().equalsIgnoreCase("Glide") && Utils.isMovinginput()) || this.mode.getValString().equalsIgnoreCase("Vanilla")) {
      entity.motionX = directionSpeedVanilla[0];
      entity.motionZ = directionSpeedVanilla[1];
    } 
    if (this.mode.getValString().equalsIgnoreCase("Tp"))
      entity.setPosition(entity.posX + directionSpeedVanilla[0], entity.posY, entity.posZ + directionSpeedVanilla[1]); 
    if (this.mode.getValString().equalsIgnoreCase("TpUpdate"))
      entity.setPositionAndRotation(entity.posX + directionSpeedVanilla[0], entity.posY, entity.posZ + directionSpeedVanilla[1], entity.rotationYaw, entity.rotationPitch); 
    if (isBorderingChunk(entity, directionSpeedVanilla[0], directionSpeedVanilla[1])) {
      entity.motionX = 0.0D;
      entity.motionZ = 0.0D;
    } 
    if (this.Yawlock.getValBoolean())
      entity.rotationYaw = mc.player.rotationYaw; 
    if (entity instanceof EntityBoat && this.boatInputsfalse.getValBoolean())
      ((EntityBoat)entity).updateInputs(false, false, false, false); 
  }
  
  public boolean isBorderingChunk(Entity entity, double motX, double motZ) {
    return (this.antiStuck.getValBoolean() && mc.world.getChunk((int)(entity.posX + motX) >> 4, (int)(entity.posZ + motZ) >> 4) instanceof net.minecraft.world.chunk.EmptyChunk);
  }
  
  public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
    GuiIngameForge.renderFood = this.foodview.getValBoolean();
  }
}
