package Method.Client.module.render;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.visual.RenderUtils;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class BlockOverlay extends Module {
  Setting OverlayColor = Main.setmgr.add(new Setting("OverlayColor", this, 0.0D, 1.0D, 1.0D, 0.62D));
  
  Setting Mode = Main.setmgr.add(new Setting("Hole Mode", this, "Outline", BlockEspOptions()));
  
  Setting LineWidth = Main.setmgr.add(new Setting("LineWidth", this, 1.0D, 0.0D, 3.0D, false));
  
  public BlockOverlay() {
    super("BlockOverlay", 0, Category.RENDER, "BlockOverlay");
  }
  
  public void onRenderWorldLast(RenderWorldLastEvent event) {
    if (mc.objectMouseOver == null)
      return; 
    if (Block.getIdFromBlock(mc.world.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock()) == 0)
      return; 
    if (mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
      BlockPos blockPos = mc.objectMouseOver.getBlockPos();
      RenderUtils.RenderBlock(this.Mode.getValString(), RenderUtils.Standardbb(blockPos), this.OverlayColor.getcolor(), Double.valueOf(this.LineWidth.getValDouble()));
    } 
    super.onRenderWorldLast(event);
  }
}
