package Method.Client.module.render;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.visual.ChatUtils;
import Method.Client.utils.visual.ColorUtils;
import Method.Client.utils.visual.RenderUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.item.Item;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

public class Trajectories extends Module {
  public final List<Bpos> Pos;
  
  Setting FindEpearl;
  
  Setting ChatPrint;
  
  Setting RenderTime;
  
  Setting Mode;
  
  Setting LineWidth;
  
  Setting Color;
  
  Setting skeleton;
  
  public Trajectories() {
    super("Trajectories", 0, Category.RENDER, "Trajectories");
    this.Pos = new ArrayList<>();
    this.FindEpearl = Main.setmgr.add(new Setting("Follow Pearl", this, true));
    this.ChatPrint = Main.setmgr.add(new Setting("ChatPrint", this, false, this.FindEpearl, 3));
    this.RenderTime = Main.setmgr.add(new Setting("RenderTime", this, 5.0D, 0.0D, 35.0D, false, this.FindEpearl, 4));
    this.Mode = Main.setmgr.add(new Setting("Mode", this, "Xspot", BlockEspOptions()));
    this.LineWidth = Main.setmgr.add(new Setting("LineWidth", this, 1.0D, 0.0D, 3.0D, false));
    this.Color = Main.setmgr.add(new Setting("Color", this, 0.22D, 1.0D, 0.6D, 0.65D));
    this.skeleton = Main.setmgr.add(new Setting("Skeleton", this, false));
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (this.FindEpearl.getValBoolean())
      for (Entity entity : mc.world.loadedEntityList) {
        if (entity instanceof EntityEnderPearl) {
          EntityEnderPearl e = (EntityEnderPearl)entity;
          boolean notfound = true;
          for (Bpos po : this.Pos) {
            if (po.getUuid().equals(e.getUniqueID())) {
              notfound = false;
              break;
            } 
          } 
          if (notfound) {
            this.Pos.add(new Bpos(new ArrayList<>(Collections.singletonList(e.getPositionVector())), e.getUniqueID(), System.currentTimeMillis()));
            if (this.ChatPrint.getValBoolean())
              ChatUtils.message(e.perlThrower.toString() + " Threw a pearl!"); 
            continue;
          } 
          for (Bpos po : this.Pos) {
            if (po.uuid.equals(e.getUniqueID()))
              po.vec3ds.add(e.getPositionVector()); 
          } 
        } 
      }  
  }
  
  public boolean itemcheck(Item item) {
    return (item instanceof net.minecraft.item.ItemBow || item instanceof net.minecraft.item.ItemSnowball || item instanceof net.minecraft.item.ItemEgg || item instanceof net.minecraft.item.ItemEnderPearl || item instanceof net.minecraft.item.ItemSplashPotion || item instanceof net.minecraft.item.ItemLingeringPotion || item instanceof net.minecraft.item.ItemFishingRod);
  }
  
  public void onRenderWorldLast(RenderWorldLastEvent event) {
    for (Entity entity : mc.world.loadedEntityList) {
      if (entity instanceof EntityLivingBase) {
        EntityLivingBase livingBase = (EntityLivingBase)entity;
        if (livingBase instanceof net.minecraft.entity.monster.EntitySkeleton && !this.skeleton.getValBoolean())
          return; 
        if (itemcheck(livingBase.getHeldItemMainhand().getItem()) || itemcheck(livingBase.getHeldItemOffhand().getItem())) {
          livingBase.getActiveItemStack();
          boolean usingBow = livingBase.getActiveItemStack().getItem() instanceof net.minecraft.item.ItemBow;
          double arrowPosX = livingBase.lastTickPosX + (livingBase.posX - livingBase.lastTickPosX) * event.getPartialTicks() - (MathHelper.cos((float)Math.toRadians(livingBase.rotationYaw)) * 0.16F);
          double arrowPosY = livingBase.lastTickPosY + (livingBase.posY - livingBase.lastTickPosY) * event.getPartialTicks() + livingBase.getEyeHeight() - 0.1D;
          double arrowPosZ = livingBase.lastTickPosZ + (livingBase.posZ - livingBase.lastTickPosZ) * event.getPartialTicks() - (MathHelper.sin((float)Math.toRadians(livingBase.rotationYaw)) * 0.16F);
          float arrowMotionFactor = usingBow ? 1.0F : 0.4F;
          float yaw = (float)Math.toRadians(livingBase.rotationYaw);
          float pitch = (float)Math.toRadians(livingBase.rotationPitch);
          float arrowMotionX = -MathHelper.sin(yaw) * MathHelper.cos(pitch) * arrowMotionFactor;
          float arrowMotionY = -MathHelper.sin(pitch) * arrowMotionFactor;
          float arrowMotionZ = MathHelper.cos(yaw) * MathHelper.cos(pitch) * arrowMotionFactor;
          double arrowMotion = Math.sqrt((arrowMotionX * arrowMotionX + arrowMotionY * arrowMotionY + arrowMotionZ * arrowMotionZ));
          double bowPower = 1.5D;
          if (usingBow) {
            bowPower = ((72000 - livingBase.getItemInUseCount()) / 20.0F);
            bowPower = (bowPower * bowPower + bowPower * 2.0D) / 3.0D;
            bowPower = (bowPower > 1.0D || bowPower <= 0.10000000149011612D) ? 3.0D : (bowPower * 3.0D);
          } 
          arrowMotionX = (float)(arrowMotionX / arrowMotion * bowPower);
          arrowMotionY = (float)(arrowMotionY / arrowMotion * bowPower);
          arrowMotionZ = (float)(arrowMotionZ / arrowMotion * bowPower);
          double gravity = usingBow ? 0.05D : ((livingBase.getHeldItemMainhand().getItem() instanceof net.minecraft.item.ItemPotion || livingBase.getHeldItemOffhand().getItem() instanceof net.minecraft.item.ItemPotion) ? 0.4D : ((livingBase.getHeldItemMainhand().getItem() instanceof net.minecraft.item.ItemFishingRod || livingBase.getHeldItemOffhand().getItem() instanceof net.minecraft.item.ItemFishingRod) ? 0.15D : 0.03D));
          Vec3d playerVector = new Vec3d(livingBase.posX, livingBase.posY + livingBase.getEyeHeight(), livingBase.posZ);
          RenderUtils.OpenGl();
          GL11.glEnable(32925);
          GlStateManager.glLineWidth((float)this.LineWidth.getValDouble());
          ColorUtils.glColor(this.Color.getcolor());
          GlStateManager.glBegin(3);
          RenderManager renderManager = mc.getRenderManager();
          int i = 0;
          GL11.glVertex3d(arrowPosX - renderManager.viewerPosX, arrowPosY - renderManager.viewerPosY, arrowPosZ - renderManager.viewerPosZ);
          arrowPosX += arrowMotionX * 0.1D;
          arrowPosY += arrowMotionY * 0.1D;
          arrowPosZ += arrowMotionZ * 0.1D;
          arrowMotionX = (float)(arrowMotionX * 0.999D);
          arrowMotionY = (float)(arrowMotionY * 0.999D - gravity * 0.1D);
          arrowMotionZ = (float)(arrowMotionZ * 0.999D);
          for (; i < 1000 && mc.world.rayTraceBlocks(playerVector, new Vec3d(arrowPosX, arrowPosY, arrowPosZ)) == null; i++);
          GlStateManager.glEnd();
          double renderX = arrowPosX - renderManager.viewerPosX;
          double renderY = arrowPosY - renderManager.viewerPosY;
          double renderZ = arrowPosZ - renderManager.viewerPosZ;
          AxisAlignedBB bb = new AxisAlignedBB(renderX - 0.5D, renderY, renderZ - 0.5D, renderX + 0.5D, renderY + 0.5D, renderZ + 0.5D);
          RenderUtils.RenderBlock(this.Mode.getValString(), bb, this.Color.getcolor(), Double.valueOf(this.LineWidth.getValDouble()));
          RenderUtils.ReleaseGl();
          GL11.glDisable(32925);
        } 
      } 
    } 
    if (this.FindEpearl.getValBoolean()) {
      RenderUtils.OpenGl();
      GlStateManager.glLineWidth((float)this.LineWidth.getValDouble() * 3.0F);
      List<Bpos> toremove = new ArrayList<>();
      for (Bpos po : this.Pos) {
        if (po.getaLong() + this.RenderTime.getValDouble() * 1000.0D < System.currentTimeMillis())
          toremove.add(po); 
      } 
      this.Pos.removeAll(toremove);
      if (!this.Pos.isEmpty())
        for (Bpos po : this.Pos) {
          GlStateManager.glBegin(1);
          ColorUtils.glColor(this.Color.getcolor());
          double[] rPos = rPos();
          Vec3d priorpoint = po.getVec3ds().get(0);
          for (Vec3d vec3d : po.getVec3ds()) {
            GL11.glVertex3d(vec3d.x - rPos[0], vec3d.y - rPos[1], vec3d.z - rPos[2]);
            GL11.glVertex3d(priorpoint.x - rPos[0], priorpoint.y - rPos[1], priorpoint.z - rPos[2]);
            priorpoint = vec3d;
          } 
          GlStateManager.glEnd();
        }  
      RenderUtils.ReleaseGl();
    } 
    super.onRenderWorldLast(event);
  }
  
  private double[] rPos() {
    try {
      double renderPosX = (mc.getRenderManager()).viewerPosX;
      double renderPosY = (mc.getRenderManager()).viewerPosY;
      double renderPosZ = (mc.getRenderManager()).viewerPosZ;
      return new double[] { renderPosX, renderPosY, renderPosZ };
    } catch (Exception e) {
      return new double[] { 0.0D, 0.0D, 0.0D };
    } 
  }
  
  static class Bpos {
    private final List<Vec3d> vec3ds;
    
    private final UUID uuid;
    
    private final long aLong;
    
    public List<Vec3d> getVec3ds() {
      return this.vec3ds;
    }
    
    public Bpos(List<Vec3d> vec3ds, UUID uuid, long l) {
      this.vec3ds = vec3ds;
      this.uuid = uuid;
      this.aLong = l;
    }
    
    public UUID getUuid() {
      return this.uuid;
    }
    
    public long getaLong() {
      return this.aLong;
    }
  }
}
