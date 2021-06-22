package Method.Client.module.render;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.TimerUtils;
import Method.Client.utils.system.Wrapper;
import Method.Client.utils.visual.Executer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class WallHack extends Module {
  Setting players;
  
  Setting mobs;
  
  Setting Barrier;
  
  TimerUtils timer;
  
  public WallHack() {
    super("WallHack", 0, Category.RENDER, "WallHack");
    this.players = Main.setmgr.add(new Setting("players", this, false));
    this.mobs = Main.setmgr.add(new Setting("mobs", this, false));
    this.Barrier = Main.setmgr.add(new Setting("Barrier", this, false));
    this.timer = new TimerUtils();
  }
  
  public void onEnable() {
    Executer.init();
  }
  
  public void onRenderWorldLast(RenderWorldLastEvent event) {
    if (this.timer.isDelay(4500L)) {
      if (this.Barrier.getValBoolean())
        Executer.execute(() -> {
              Vec3i playerPos = new Vec3i(mc.player.posX, mc.player.posY, mc.player.posZ);
              for (int x = playerPos.getX() - 10; x < playerPos.getX() + 10; x++) {
                for (int z = playerPos.getZ() - 10; z < playerPos.getZ() + 10; z++) {
                  for (int y = playerPos.getY() + 6; y > playerPos.getY() - 6; y--) {
                    if (mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() == Blocks.BARRIER)
                      mc.world.spawnParticle(EnumParticleTypes.BARRIER, x + 0.5D, y + 0.5D, z + 0.5D, 0.0D, 0.0D, 0.0D, new int[0]); 
                  } 
                } 
              } 
            }); 
      this.timer.setLastMS();
    } 
    GlStateManager.clear(256);
    RenderHelper.enableStandardItemLighting();
    for (Object object : mc.world.loadedEntityList) {
      Entity entity = (Entity)object;
      render(entity, event.getPartialTicks());
    } 
    super.onRenderWorldLast(event);
  }
  
  void render(Entity entity, float ticks) {
    Entity ent = getEntity(entity);
    if (ent == null || ent == mc.player)
      return; 
    if (ent == mc.getRenderViewEntity() && (Wrapper.INSTANCE.mcSettings()).thirdPersonView == 0)
      return; 
    mc.entityRenderer.disableLightmap();
    mc.getRenderManager().renderEntityStatic(ent, ticks, false);
    mc.entityRenderer.enableLightmap();
  }
  
  Entity getEntity(Entity e) {
    Entity entity = null;
    if (this.players.getValBoolean() && e instanceof net.minecraft.entity.player.EntityPlayer) {
      entity = e;
    } else if (this.mobs.getValBoolean() && e instanceof net.minecraft.entity.EntityLiving) {
      entity = e;
    } else if (e instanceof net.minecraft.entity.item.EntityItem) {
      entity = e;
    } else if (e instanceof net.minecraft.entity.projectile.EntityArrow) {
      entity = e;
    } 
    return entity;
  }
}
