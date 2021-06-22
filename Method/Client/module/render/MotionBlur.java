package Method.Client.module.render;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import java.lang.reflect.Field;
import java.util.Map;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class MotionBlur extends Module {
  boolean Setup = true;
  
  double old = 0.0D;
  
  private static Map domainResourceManagers;
  
  public static Setting blurAmount;
  
  public void setup() {
    Main.setmgr.add(blurAmount = new Setting("blurAmount", this, 1.0D, 0.0D, 10.0D, false));
  }
  
  public MotionBlur() {
    super("MotionBlur", 0, Category.RENDER, "MotionBlur");
  }
  
  public void onEnable() {
    this.Setup = true;
    domainResourceManagers = null;
  }
  
  public void onDisable() {
    mc.entityRenderer.stopUseShader();
    domainResourceManagers = null;
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (this.old != blurAmount.getValDouble()) {
      this.old = blurAmount.getValDouble();
      this.Setup = true;
      domainResourceManagers = null;
      return;
    } 
    if (domainResourceManagers == null)
      try {
        Field[] var2 = SimpleReloadableResourceManager.class.getDeclaredFields();
        for (Field field : var2) {
          if (field.getType() == Map.class) {
            field.setAccessible(true);
            domainResourceManagers = (Map)field.get(mc.getResourceManager());
            break;
          } 
        } 
      } catch (Exception var6) {
        throw new RuntimeException(var6);
      }  
    assert domainResourceManagers != null;
    if (!domainResourceManagers.containsKey("motionblur"))
      domainResourceManagers.put("motionblur", new MotionBlurResourceManager()); 
    if (this.Setup) {
      mc.entityRenderer.loadShader(new ResourceLocation("motionblur", "motionblur"));
      mc.entityRenderer.getShaderGroup().createBindFramebuffers(MC.displayWidth, MC.displayHeight);
      this.Setup = false;
    } 
  }
}
