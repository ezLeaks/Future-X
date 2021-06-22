package Method.Client.module.render;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.visual.RenderUtils;
import net.minecraft.client.renderer.DestroyBlockProgress;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class BreakEsp extends Module {
  Setting ignoreSelf;
  
  Setting onlyObsi;
  
  Setting fade;
  
  Setting Mode;
  
  Setting LineWidth;
  
  Setting OverlayColor;
  
  Setting Distance;
  
  public BreakEsp() {
    super("BreakEsp", 0, Category.RENDER, "BreakEsp");
    this.ignoreSelf = Main.setmgr.add(new Setting("ignoreSelf", this, false));
    this.onlyObsi = Main.setmgr.add(new Setting("onlyObsi", this, false));
    this.fade = Main.setmgr.add(new Setting("fade", this, true));
    this.Mode = Main.setmgr.add(new Setting("Hole Mode", this, "Outline", BlockEspOptions()));
    this.LineWidth = Main.setmgr.add(new Setting("LineWidth", this, 1.0D, 0.0D, 3.0D, false));
    this.OverlayColor = Main.setmgr.add(new Setting("OverlayColor", this, 0.0D, 1.0D, 1.0D, 0.56D));
    this.Distance = Main.setmgr.add(new Setting("Distance", this, 10.0D, 0.0D, 50.0D, false));
  }
  
  public void onRenderWorldLast(RenderWorldLastEvent event) {
    mc.renderGlobal.damagedBlocks.forEach((var1x, destroyBlockProgress) -> {
          if (destroyBlockProgress != null && this.Distance.getValDouble() * 5.0D > mc.player.getDistanceSqToCenter(destroyBlockProgress.getPosition()) && (!this.ignoreSelf.getValBoolean() || mc.world.getEntityByID(var1x.intValue()) != mc.player) && (!this.onlyObsi.getValBoolean() || mc.world.getBlockState(destroyBlockProgress.getPosition()).getBlock() == Blocks.OBSIDIAN)) {
            AxisAlignedBB pos = RenderUtils.Standardbb(destroyBlockProgress.getPosition());
            if (this.fade.getValBoolean())
              pos = pos.shrink((3.0D - destroyBlockProgress.getPartialBlockDamage() / 2.6666666666666665D) / 9.0D); 
            RenderUtils.RenderBlock(this.Mode.getValString(), pos, this.OverlayColor.getcolor(), Double.valueOf(this.LineWidth.getValDouble()));
          } 
        });
    super.onRenderWorldLast(event);
  }
}
