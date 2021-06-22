package Method.Client.module.render;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import Method.Client.utils.visual.RenderUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec2f;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class NewChunks extends Module {
  Setting OverlayColor = Main.setmgr.add(new Setting("OverlayColor", this, 0.0D, 1.0D, 1.0D, 1.0D));
  
  Setting Mode = Main.setmgr.add(new Setting("Hole Mode", this, "Outline", BlockEspOptions()));
  
  Setting LineWidth = Main.setmgr.add(new Setting("LineWidth", this, 1.0D, 0.0D, 3.0D, false));
  
  Setting MaxDistance = Main.setmgr.add(new Setting("MaxDistance", this, 1000.0D, 0.0D, 50000.0D, false));
  
  private final List<Vec2f> chunkDataList;
  
  public NewChunks() {
    super("NewChunks", 0, Category.RENDER, "NewChunks");
    this.chunkDataList = new ArrayList<>();
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (packet instanceof SPacketChunkData) {
      SPacketChunkData packet2 = (SPacketChunkData)packet;
      if (!packet2.isFullChunk()) {
        Vec2f chunk = new Vec2f((packet2.getChunkX() * 16), (packet2.getChunkZ() * 16));
        if (!this.chunkDataList.contains(chunk))
          this.chunkDataList.add(chunk); 
      } 
    } 
    return true;
  }
  
  public void onRenderWorldLast(RenderWorldLastEvent event) {
    List<Vec2f> found = new ArrayList<>();
    for (Vec2f chunkData : this.chunkDataList) {
      if (chunkData != null) {
        if (mc.player.getDistance(chunkData.x, mc.player.posY, chunkData.y) > this.MaxDistance.getValDouble())
          found.add(chunkData); 
        double renderPosX = chunkData.x - (mc.getRenderManager()).viewerPosX;
        double renderPosY = -(mc.getRenderManager()).viewerPosY;
        double renderPosZ = chunkData.y - (mc.getRenderManager()).viewerPosZ;
        AxisAlignedBB bb = new AxisAlignedBB(renderPosX, renderPosY, renderPosZ, renderPosX + 16.0D, renderPosY + 1.0D, renderPosZ + 16.0D);
        RenderUtils.RenderBlock(this.Mode.getValString(), bb, this.OverlayColor.getcolor(), Double.valueOf(this.LineWidth.getValDouble()));
      } 
    } 
    this.chunkDataList.removeAll(found);
    super.onRenderWorldLast(event);
  }
}
