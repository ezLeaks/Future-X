package Method.Client.module.render;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.visual.RenderUtils;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class ChunkBorder extends Module {
  Setting OverlayColor = Main.setmgr.add(new Setting("OverlayColor", this, 0.0D, 1.0D, 1.0D, 0.52D));
  
  Setting Mode = Main.setmgr.add(new Setting("Chunk Mode", this, "Outline", BlockEspOptions()));
  
  Setting LineWidth = Main.setmgr.add(new Setting("LineWidth", this, 1.0D, 0.0D, 3.0D, false));
  
  Setting Height = Main.setmgr.add(new Setting("Height", this, 0.0D, 0.0D, 255.0D, true));
  
  Setting FollowPlayer = Main.setmgr.add(new Setting("FollowPlayer", this, true));
  
  public ChunkBorder() {
    super("ChunkBorder", 0, Category.RENDER, "ChunkBorder");
  }
  
  public void onRenderWorldLast(RenderWorldLastEvent event) {
    AxisAlignedBB bb1;
    Chunk chunk = mc.world.getChunk(mc.player.getPosition());
    double renderPosX = (chunk.x * 16) - (mc.getRenderManager()).viewerPosX;
    double renderPosY = -(mc.getRenderManager()).viewerPosY;
    double renderPosZ = (chunk.z * 16) - (mc.getRenderManager()).viewerPosZ;
    if (this.FollowPlayer.getValBoolean()) {
      bb1 = new AxisAlignedBB(renderPosX, renderPosY + mc.player.posY, renderPosZ, renderPosX + 16.0D, renderPosY + 1.0D + mc.player.posY, renderPosZ + 16.0D);
    } else {
      bb1 = new AxisAlignedBB(renderPosX, renderPosY + this.Height.getValDouble(), renderPosZ, renderPosX + 16.0D, renderPosY + 1.0D + this.Height.getValDouble(), renderPosZ + 16.0D);
    } 
    RenderUtils.RenderBlock(this.Mode.getValString(), bb1, this.OverlayColor.getcolor(), Double.valueOf(this.LineWidth.getValDouble()));
  }
}
