package Method.Client.module.Onscreen.Display;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.module.Onscreen.PinableFrame;
import java.util.ArrayList;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.input.Mouse;

public final class Player extends Module {
  static Setting xpos;
  
  static Setting ypos;
  
  static Setting Scale;
  
  static Setting Nolook;
  
  public Player() {
    super("Player", 0, Category.ONSCREEN, "Player");
  }
  
  public void setup() {
    this.visible = false;
    Main.setmgr.add(xpos = new Setting("xpos", this, 200.0D, -20.0D, (mc.displayWidth + 40), true));
    Main.setmgr.add(ypos = new Setting("ypos", this, 20.0D, -20.0D, (mc.displayHeight + 40), true));
    Main.setmgr.add(Scale = new Setting("Scale", this, 1.0D, 0.0D, 5.0D, false));
    ArrayList<String> options = new ArrayList<>();
    options.add("Free");
    options.add("Mouse");
    options.add("None");
    Main.setmgr.add(Nolook = new Setting("Mode", this, "Free", options));
  }
  
  public void onEnable() {
    PinableFrame.Toggle("PlayerSET", true);
  }
  
  public void onDisable() {
    PinableFrame.Toggle("PlayerSET", false);
  }
  
  public static class PlayerRUN extends PinableFrame {
    public PlayerRUN() {
      super("PlayerSET", new String[0], (int)Player.ypos.getValDouble(), (int)Player.xpos.getValDouble());
    }
    
    public void setup() {
      this.x = (int)Player.xpos.getValDouble();
      this.y = (int)Player.ypos.getValDouble();
    }
    
    public void Ongui() {
      if (!getDrag().booleanValue()) {
        this.x = (int)Player.xpos.getValDouble();
        this.y = (int)Player.ypos.getValDouble();
      } else {
        Player.xpos.setValDouble(this.x);
        Player.ypos.setValDouble(this.y);
      } 
    }
    
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
      if (this.mc.player == null)
        return; 
      if (this.mc.gameSettings.thirdPersonView != 0)
        return; 
      GlStateManager.pushMatrix();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      if (Player.Nolook.getValString().equalsIgnoreCase("Free")) {
        drawPlayer(this.x, this.y, (EntityLivingBase)this.mc.player);
      } else {
        GuiInventory.drawEntityOnScreen(this.x + 17, this.y + 60, (int)(Player.Scale.getValDouble() * 30.0D), Player.Nolook.getValString().equalsIgnoreCase("None") ? 0.0F : (this.x - Mouse.getX()), Player.Nolook.getValString().equalsIgnoreCase("None") ? 0.0F : (-this.mc.displayHeight + Mouse.getY()), (EntityLivingBase)this.mc.player);
      } 
      GlStateManager.popMatrix();
      super.onRenderGameOverlay(event);
    }
    
    private void drawPlayer(int x, int y, EntityLivingBase ent) {
      GlStateManager.translate(x + 30.0F, y + 50.0F, 50.0F);
      GlStateManager.scale((float)-Player.Scale.getValDouble() * 24.0F, (float)Player.Scale.getValDouble() * 24.0F, (float)Player.Scale.getValDouble() * 24.0F);
      GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.disableBlend();
      GlStateManager.enableDepth();
      GlStateManager.depthMask(true);
      RenderHelper.enableStandardItemLighting();
      GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-((float)Math.atan(0.0D)) * 20.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.translate(0.0F, 0.0F, 0.0F);
      RenderManager renderManager = this.mc.getRenderManager();
      renderManager.setPlayerViewY(180.0F);
      renderManager.setRenderShadow(false);
      renderManager.renderEntity((Entity)ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
      renderManager.setRenderShadow(true);
      RenderHelper.disableStandardItemLighting();
      GlStateManager.enableBlend();
      GlStateManager.disableDepth();
      GlStateManager.depthMask(false);
    }
  }
}
