package Method.Client.module.movement;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Patcher.Events.EntityPlayerJumpEvent;
import net.minecraft.init.MobEffects;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Jump extends Module {
  Setting mode = Main.setmgr.add(new Setting("Mode", this, "PotionHJ", new String[] { "PotionHJ", "Ymotion", "JumpPos", "Random", "Packet", "None" }));
  
  Setting height = Main.setmgr.add(new Setting("height", this, 1.0D, 0.5D, 20.0D, true, this.mode, "PotionHJ", 1));
  
  Setting Ymotion = Main.setmgr.add(new Setting("Ymotion", this, 1.0D, 0.0D, 2.0D, false, this.mode, "Ymotion", 1));
  
  Setting AirJump = Main.setmgr.add(new Setting("AirJump", this, false));
  
  Setting RapidJump = Main.setmgr.add(new Setting("RapidJump", this, false));
  
  public Jump() {
    super("Jump", 0, Category.MOVEMENT, "Jump Mod");
  }
  
  public void onPlayerTick(TickEvent.PlayerTickEvent event) {
    if (this.mode.getValString().equalsIgnoreCase("PotionHJ")) {
      PotionEffect nv = new PotionEffect(MobEffects.JUMP_BOOST, 3, (int)this.height.getValDouble());
      mc.player.addPotionEffect(nv);
    } 
    if (this.mode.getValString().equalsIgnoreCase("JumpPos") && mc.gameSettings.keyBindJump.pressed)
      mc.player.setPosition(mc.player.lastTickPosX, ((float)mc.player.serverPosY + 0.139F), mc.player.serverPosZ); 
    if (this.mode.getValString().equalsIgnoreCase("Packet")) {
      mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.41999998688698D, mc.player.posZ, true));
      mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.7531999805211997D, mc.player.posZ, true));
      mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.00133597911214D, mc.player.posZ, true));
      mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.16610926093821D, mc.player.posZ, true));
    } 
    if (this.AirJump.getValBoolean() && !mc.player.onGround && 
      mc.gameSettings.keyBindJump.isPressed())
      mc.player.jump(); 
    if (this.RapidJump.getValBoolean() && mc.player.onGround && mc.gameSettings.keyBindJump.pressed)
      mc.player.jump(); 
  }
  
  public void onPlayerJump(EntityPlayerJumpEvent event) {
    if (this.mode.getValString().equalsIgnoreCase("Random")) {
      mc.player.motionY += Math.random() / 10.0D;
      mc.player.posY += Math.random() / 10.0D;
    } 
    if (this.mode.getValString().equalsIgnoreCase("Ymotion"))
      mc.player.motionY *= this.Ymotion.getValDouble(); 
  }
  
  public void onDisable() {
    mc.player.removeActivePotionEffect(MobEffects.JUMP_BOOST);
    super.onDisable();
  }
}
