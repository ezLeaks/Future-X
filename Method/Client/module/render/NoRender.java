package Method.Client.module.render;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import net.minecraft.block.material.Material;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class NoRender extends Module {
  Setting stopParticles = Main.setmgr.add(new Setting("stopParticles", this, false));
  
  Setting stopExplosions = Main.setmgr.add(new Setting("stop Explosions", this, false));
  
  Setting helmet = Main.setmgr.add(new Setting("helmet", this, false));
  
  Setting portal = Main.setmgr.add(new Setting("portal", this, false));
  
  Setting crosshair = Main.setmgr.add(new Setting("crosshair", this, false));
  
  Setting bosshealth = Main.setmgr.add(new Setting("bosshealth", this, false));
  
  Setting bossinfo = Main.setmgr.add(new Setting("bossinfo", this, false));
  
  Setting armor = Main.setmgr.add(new Setting("armor", this, false));
  
  Setting health = Main.setmgr.add(new Setting("health", this, false));
  
  Setting food = Main.setmgr.add(new Setting("food", this, false));
  
  Setting air = Main.setmgr.add(new Setting("air", this, false));
  
  Setting hotbar = Main.setmgr.add(new Setting("hotbar", this, false));
  
  Setting experience = Main.setmgr.add(new Setting("experience", this, false));
  
  Setting horsehealth = Main.setmgr.add(new Setting("horsehealth", this, false));
  
  Setting horsejump = Main.setmgr.add(new Setting("horsejump", this, false));
  
  Setting chat = Main.setmgr.add(new Setting("chat", this, false));
  
  Setting playerlist = Main.setmgr.add(new Setting("playerlist", this, false));
  
  Setting potionicon = Main.setmgr.add(new Setting("potionicon", this, false));
  
  Setting subtitles = Main.setmgr.add(new Setting("subtitles", this, false));
  
  Setting fpsgraph = Main.setmgr.add(new Setting("fpsgraph", this, false));
  
  Setting vignette = Main.setmgr.add(new Setting("vignette", this, false));
  
  Setting Blockoverlay = Main.setmgr.add(new Setting("Liquidoverlay", this, false));
  
  Setting Liquidoverlay = Main.setmgr.add(new Setting("Blockoverlay", this, false));
  
  public NoRender() {
    super("NoRender", 0, Category.RENDER, "NoRender");
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (packet instanceof net.minecraft.network.play.server.SPacketExplosion && this.stopExplosions.getValBoolean())
      return false; 
    return (!this.stopParticles.getValBoolean() || !(packet instanceof net.minecraft.network.play.server.SPacketParticles));
  }
  
  public void fogDensity(EntityViewRenderEvent.FogDensity event) {
    if (this.Liquidoverlay.getValBoolean() && (
      event.getState().getMaterial().equals(Material.WATER) || event.getState().getMaterial().equals(Material.LAVA))) {
      event.setDensity(0.0F);
      event.setCanceled(true);
    } 
  }
  
  public void RenderBlockOverlayEvent(RenderBlockOverlayEvent event) {
    if (this.Blockoverlay.getValBoolean())
      event.setCanceled(true); 
  }
  
  public void RendergameOverlay(RenderGameOverlayEvent event) {
    switch (event.getType()) {
      case HELMET:
        event.setCanceled(this.helmet.getValBoolean());
        break;
      case PORTAL:
        event.setCanceled(this.portal.getValBoolean());
        break;
      case CROSSHAIRS:
        event.setCanceled(this.crosshair.getValBoolean());
        break;
      case BOSSHEALTH:
        event.setCanceled(this.bosshealth.getValBoolean());
        break;
      case BOSSINFO:
        event.setCanceled(this.bossinfo.getValBoolean());
        break;
      case ARMOR:
        event.setCanceled(this.armor.getValBoolean());
        break;
      case HEALTH:
        event.setCanceled(this.health.getValBoolean());
        break;
      case FOOD:
        event.setCanceled(this.food.getValBoolean());
        break;
      case AIR:
        event.setCanceled(this.air.getValBoolean());
        break;
      case HOTBAR:
        event.setCanceled(this.hotbar.getValBoolean());
        break;
      case EXPERIENCE:
        event.setCanceled(this.experience.getValBoolean());
        break;
      case HEALTHMOUNT:
        if (mc.player.isRiding())
          event.setCanceled(this.horsehealth.getValBoolean()); 
        break;
      case JUMPBAR:
        if (mc.player.isRiding())
          event.setCanceled(this.horsejump.getValBoolean()); 
        break;
      case CHAT:
        event.setCanceled(this.chat.getValBoolean());
        break;
      case PLAYER_LIST:
        event.setCanceled(this.playerlist.getValBoolean());
        break;
      case POTION_ICONS:
        event.setCanceled(this.potionicon.getValBoolean());
        break;
      case SUBTITLES:
        event.setCanceled(this.subtitles.getValBoolean());
        break;
      case FPS_GRAPH:
        event.setCanceled(this.fpsgraph.getValBoolean());
        break;
      case VIGNETTE:
        event.setCanceled(this.vignette.getValBoolean());
        break;
    } 
  }
}
