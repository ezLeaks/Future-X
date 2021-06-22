package Method.Client.module.Onscreen.Display;

import Method.Client.Main;
import Method.Client.managers.FriendManager;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.module.Onscreen.PinableFrame;
import java.awt.Color;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public final class Enemypos extends Module {
  static Setting TextColor;
  
  static Setting BgColor;
  
  static Setting Friends;
  
  static Setting xpos;
  
  static Setting ypos;
  
  static Setting Frame;
  
  static Setting ColorDistance;
  
  static Setting Background;
  
  static Setting LefttoRight;
  
  static Setting Shadow;
  
  static Setting FontSize;
  
  public Enemypos() {
    super("Enemypos", 0, Category.ONSCREEN, "Enemypos");
  }
  
  public void setup() {
    this.visible = false;
    Main.setmgr.add(TextColor = new Setting("TextColor", this, 0.0D, 1.0D, 1.0D, 1.0D));
    Main.setmgr.add(BgColor = new Setting("BGColor", this, 0.01D, 0.0D, 0.3D, 0.22D));
    Main.setmgr.add(Shadow = new Setting("Shadow", this, true));
    Main.setmgr.add(LefttoRight = new Setting("LefttoRight", this, true));
    Main.setmgr.add(Friends = new Setting("Friends", this, true));
    Main.setmgr.add(ColorDistance = new Setting("ColorDistance", this, true));
    Main.setmgr.add(Background = new Setting("Background", this, false));
    Main.setmgr.add(xpos = new Setting("xpos", this, 200.0D, -20.0D, (mc.displayWidth / 2 + 40), true));
    Main.setmgr.add(ypos = new Setting("ypos", this, 60.0D, -20.0D, (mc.displayHeight / 2 + 40), true));
    Main.setmgr.add(Frame = new Setting("Font", this, "Times", fontoptions()));
    Main.setmgr.add(FontSize = new Setting("FontSize", this, 22.0D, 10.0D, 40.0D, true));
  }
  
  public void onEnable() {
    PinableFrame.Toggle("EnemyposSET", true);
  }
  
  public void onDisable() {
    PinableFrame.Toggle("EnemyposSET", false);
  }
  
  public static class EnemyposRUN extends PinableFrame {
    public EnemyposRUN() {
      super("EnemyposSET", new String[0], (int)Enemypos.ypos.getValDouble(), (int)Enemypos.xpos.getValDouble());
    }
    
    public void setup() {
      GetSetup(this, Enemypos.xpos, Enemypos.ypos, Enemypos.Frame, Enemypos.FontSize);
    }
    
    public void Ongui() {
      GetInit(this, Enemypos.xpos, Enemypos.ypos, Enemypos.Frame, Enemypos.FontSize);
    }
    
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
      int yCount = this.y + this.barHeight + 3;
      for (EntityPlayer player : this.mc.world.playerEntities) {
        if ((FriendManager.isFriend(player.getName()) && Enemypos.Friends.getValBoolean()) || player.getName().equals(this.mc.player.getName()))
          continue; 
        int Lr = Enemypos.LefttoRight.getValBoolean() ? (widthcal(Enemypos.Frame, player.getName() + Pos(player)) - 70) : -3;
        if (Enemypos.Background.getValBoolean())
          Gui.drawRect(this.x + 4, yCount, widthcal(Enemypos.Frame, player.getName() + Pos(player)) + this.x + 3, yCount + heightcal(Enemypos.Frame, player.getName() + player.getPosition()) - 1, Enemypos.Background
              .getcolor()); 
        fontSelect(Enemypos.Frame, player.getName() + Pos(player), (this.x - Lr), yCount, Enemypos.ColorDistance.getValBoolean() ? distancecolor(player) : Enemypos.TextColor.getcolor(), Enemypos.Shadow.getValBoolean());
        yCount += 8;
      } 
      super.onRenderGameOverlay(event);
    }
    
    private int distancecolor(EntityPlayer player) {
      int g = 0, r = 0;
      if (this.mc.player.getDistance((Entity)player) > 50.0F && this.mc.player.getDistance((Entity)player) < 100.0F)
        g = (int)((this.mc.player.getDistance((Entity)player) - 50.0F) * 5.1D); 
      if (this.mc.player.getDistance((Entity)player) < 50.0F)
        r = (int)(this.mc.player.getDistance((Entity)player) * 5.1D); 
      this.mc.player.getDistance((Entity)player);
      return (new Color(r, g, 0)).getRGB();
    }
    
    public String Pos(EntityPlayer player) {
      return " X:" + player.getPosition().getX() + ", Y:" + player.getPosition().getY() + ", Z:" + player.getPosition().getZ();
    }
  }
}
