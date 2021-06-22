package Method.Client.module.render;

import Method.Client.Main;
import Method.Client.managers.FriendManager;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.visual.ChatUtils;
import Method.Client.utils.visual.ColorUtils;
import Method.Client.utils.visual.RenderUtils;
import com.google.common.collect.Lists;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Visualrange extends Module {
  Setting playSound = Main.setmgr.add(new Setting("playSound", this, true));
  
  Setting leaving = Main.setmgr.add(new Setting("leaving", this, true));
  
  Setting Box = Main.setmgr.add(new Setting("Box", this, true));
  
  Setting color = Main.setmgr.add(new Setting("Logoff", this, 1.0D, 1.0D, 1.0D, 1.0D));
  
  Setting Mode = Main.setmgr.add(new Setting("Mode", this, "Outline", BlockEspOptions()));
  
  Setting LineWidth = Main.setmgr.add(new Setting("LineWidth", this, 1.0D, 0.0D, 3.0D, false));
  
  Setting ShowCoords = Main.setmgr.add(new Setting("ShowCoords", this, true));
  
  private List<EntityPlayer> knownPlayers;
  
  public static List<EntityPlayer> logoutPositions = Lists.newArrayList();
  
  public Visualrange() {
    super("Visualrange", 0, Category.RENDER, "Visualrange");
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    List<EntityPlayer> tempplayer = new ArrayList<>();
    for (Entity entity : mc.world.getLoadedEntityList()) {
      if (entity instanceof EntityPlayer) {
        EntityPlayer entity1 = (EntityPlayer)entity;
        if (entity1.getName().equals(mc.player.getName()))
          continue; 
        if (!this.knownPlayers.contains(entity1)) {
          if (this.Box.getValBoolean())
            logoutPositions.remove(entity1); 
          this.knownPlayers.add(entity1);
          if (this.playSound.getValBoolean())
            mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F)); 
          ChatUtils.message(FriendManager.isFriend(entity1.getName()) ? ChatFormatting.GREEN.toString() : (ChatFormatting.RED.toString() + entity1.getName() + ChatFormatting.RESET.toString() + (this.ShowCoords.getValBoolean() ? (" Joined at, x: " + entity1.getPosition().getX() + " y: " + entity1.getPosition().getY() + " z: " + entity1.getPosition().getZ()) : " Joined!")));
        } 
        tempplayer.add(entity1);
      } 
    } 
    for (EntityPlayer knownPlayer : this.knownPlayers) {
      if (!tempplayer.contains(knownPlayer)) {
        this.knownPlayers.remove(knownPlayer);
        if (this.leaving.getValBoolean()) {
          if (this.Box.getValBoolean())
            logoutPositions.add(knownPlayer); 
          if (this.playSound.getValBoolean())
            mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F)); 
          ChatUtils.message(FriendManager.isFriend(knownPlayer.getName()) ? ChatFormatting.GREEN.toString() : (ChatFormatting.RED.toString() + knownPlayer.getName() + ChatFormatting.RESET.toString() + (this.ShowCoords.getValBoolean() ? (" Left at, x: " + knownPlayer.getPosition().getX() + " y: " + knownPlayer.getPosition().getY() + " z: " + knownPlayer.getPosition().getZ()) : " Left!")));
        } 
      } 
    } 
  }
  
  public void onEnable() {
    this.knownPlayers = new ArrayList<>();
    if (mc.player != null && 
      mc.isSingleplayer())
      toggle(); 
  }
  
  public void onRenderWorldLast(RenderWorldLastEvent event) {
    if (this.Box.getValBoolean())
      for (EntityPlayer logoutPosition : logoutPositions) {
        double renderPosX = logoutPosition.posX - (mc.getRenderManager()).viewerPosX;
        double renderPosY = logoutPosition.posY - (mc.getRenderManager()).viewerPosY;
        double renderPosZ = logoutPosition.posZ - (mc.getRenderManager()).viewerPosZ;
        AxisAlignedBB bb = new AxisAlignedBB(renderPosX, renderPosY, renderPosZ, renderPosX + 1.0D, renderPosY + 2.0D, renderPosZ + 1.0D);
        RenderUtils.SimpleNametag(logoutPosition.getPositionVector(), logoutPosition.getName() + (this.ShowCoords.getValBoolean() ? ("X: " + (int)logoutPosition.posX + " Y: " + (int)logoutPosition.posY + " Z: " + (int)logoutPosition.posZ) : ""));
        RenderUtils.RenderBlock(this.Mode.getValString(), bb, FriendManager.isFriend(logoutPosition.getName()) ? ColorUtils.rainbow().getRGB() : this.color.getcolor(), Double.valueOf(this.LineWidth.getValDouble()));
      }  
  }
}
