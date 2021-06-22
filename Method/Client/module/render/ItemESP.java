package Method.Client.module.render;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.visual.RenderUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class ItemESP extends Module {
  Setting OverlayColor = Main.setmgr.add(new Setting("OverlayColor", this, 0.0D, 1.0D, 1.0D, 1.0D));
  
  Setting Mode = Main.setmgr.add(new Setting("Drop Mode", this, "Outline", BlockEspOptions()));
  
  Setting Glow = Main.setmgr.add(new Setting("Glow", this, false));
  
  Setting LifeSpan = Main.setmgr.add(new Setting("Thrower", this, true));
  
  Setting Nametag = Main.setmgr.add(new Setting("Nametag", this, true));
  
  Setting LineWidth = Main.setmgr.add(new Setting("LineWidth", this, 1.0D, 0.0D, 3.0D, false));
  
  Setting Scale = Main.setmgr.add(new Setting("Scale", this, 0.1D, 0.0D, 1.0D, false));
  
  public ItemESP() {
    super("ItemESP", 0, Category.RENDER, "Droped item glow");
  }
  
  public void onDisable() {
    for (Object object : mc.world.loadedEntityList) {
      if (object instanceof EntityItem) {
        Entity item = (Entity)object;
        item.setGlowing(false);
      } 
    } 
  }
  
  public void onRenderWorldLast(RenderWorldLastEvent event) {
    for (Object object : mc.world.loadedEntityList) {
      if (object instanceof EntityItem) {
        EntityItem item = (EntityItem)object;
        double S = this.Scale.getValDouble();
        RenderUtils.RenderBlock(this.Mode.getValString(), RenderUtils.Boundingbb((Entity)item, -S, 0.2D - S, -S, S, 0.2D + S, S), this.OverlayColor.getcolor(), Double.valueOf(this.LineWidth.getValDouble()));
        if (this.Nametag.getValBoolean())
          RenderUtils.SimpleNametag(item.getPositionVector(), item.getItem().getDisplayName() + " x" + item.getItem().getCount() + (this.LifeSpan.getValBoolean() ? (" " + ((item.lifespan - item.age) / 20) + " S") : "")); 
        item.setGlowing(this.Glow.getValBoolean());
      } 
    } 
    super.onRenderWorldLast(event);
  }
}
