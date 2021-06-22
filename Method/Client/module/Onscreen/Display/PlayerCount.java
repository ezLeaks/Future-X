package Method.Client.module.Onscreen.Display;

import Method.Client.Main;
import Method.Client.managers.FriendManager;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.module.Onscreen.PinableFrame;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public final class PlayerCount extends Module {
  static Setting TextColor;
  
  static Setting BgColor;
  
  static Setting Friends;
  
  static Setting background;
  
  static Setting Shadow;
  
  static Setting xpos;
  
  static Setting ypos;
  
  static Setting Frame;
  
  static Setting FontSize;
  
  public PlayerCount() {
    super("PlayerCount", 0, Category.ONSCREEN, "PlayerCount");
  }
  
  public void setup() {
    this.visible = false;
    Main.setmgr.add(Friends = new Setting("Friends", this, false));
    Main.setmgr.add(TextColor = new Setting("TextColor", this, 0.0D, 1.0D, 1.0D, 1.0D));
    Main.setmgr.add(BgColor = new Setting("BGColor", this, 0.01D, 0.0D, 0.3D, 0.22D));
    Main.setmgr.add(Shadow = new Setting("Shadow", this, true));
    Main.setmgr.add(background = new Setting("background", this, false));
    Main.setmgr.add(Frame = new Setting("Font", this, "Times", fontoptions()));
    Main.setmgr.add(FontSize = new Setting("FontSize", this, 22.0D, 10.0D, 40.0D, true));
    Main.setmgr.add(xpos = new Setting("xpos", this, 200.0D, -20.0D, (mc.displayWidth + 40), true));
    Main.setmgr.add(ypos = new Setting("ypos", this, 140.0D, -20.0D, (mc.displayHeight + 40), true));
  }
  
  public void onEnable() {
    PinableFrame.Toggle("PlayerCountSET", true);
  }
  
  public void onDisable() {
    PinableFrame.Toggle("PlayerCountSET", false);
  }
  
  public static class PlayerCountRUN extends PinableFrame {
    int lasting;
    
    public PlayerCountRUN() {
      super("PlayerCountSET", new String[0], (int)PlayerCount.ypos.getValDouble(), (int)PlayerCount.xpos.getValDouble());
      this.lasting = 0;
    }
    
    public void setup() {
      GetSetup(this, PlayerCount.xpos, PlayerCount.ypos, PlayerCount.Frame, PlayerCount.FontSize);
    }
    
    public void Ongui() {
      GetInit(this, PlayerCount.xpos, PlayerCount.ypos, PlayerCount.Frame, PlayerCount.FontSize);
    }
    
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
      String playerCount = "ONLINE: " + this.mc.player.connection.getPlayerInfoMap().size();
      if (PlayerCount.Friends.getValBoolean()) {
        if (this.mc.player.ticksExisted % 20 == 0) {
          int onlinefriend = 0;
          for (EntityPlayer s : this.mc.world.playerEntities) {
            if (FriendManager.friendsList.contains(s.getName()))
              onlinefriend++; 
          } 
          this.lasting = onlinefriend;
        } 
        playerCount = playerCount + " Friends: " + this.lasting;
      } 
      fontSelect(PlayerCount.Frame, playerCount, getX(), (getY() + 10), PlayerCount.TextColor.getcolor(), PlayerCount.Shadow.getValBoolean());
      if (PlayerCount.background.getValBoolean())
        Gui.drawRect(this.x, this.y + 10, this.x + widthcal(PlayerCount.Frame, playerCount), this.y + 20, PlayerCount.BgColor.getcolor()); 
      super.onRenderGameOverlay(event);
    }
  }
}
