package Method.Client.utils.visual;

import Method.Client.module.misc.ModSettings;
import java.awt.Color;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Sphere;

public class RenderUtils {
  protected static Minecraft mc = Minecraft.getMinecraft();
  
  private static final ICamera camera = (ICamera)new Frustum();
  
  public static void OpenGl() {
    GlStateManager.pushMatrix();
    GlStateManager.enableBlend();
    GlStateManager.disableDepth();
    GlStateManager.disableTexture2D();
    GlStateManager.disableLighting();
    GlStateManager.disableCull();
    GlStateManager.depthMask(false);
    GL11.glHint(3154, 4354);
    GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
    GL11.glEnable(2848);
    GL11.glEnable(34383);
  }
  
  public static void ReleaseGl() {
    GlStateManager.enableTexture2D();
    GlStateManager.enableDepth();
    GlStateManager.enableCull();
    GlStateManager.enableLighting();
    GlStateManager.disableBlend();
    GlStateManager.popMatrix();
    GlStateManager.depthMask(true);
    GL11.glHint(3154, 4352);
    GL11.glDisable(2848);
    GL11.glDisable(34383);
    GlStateManager.shadeModel(7424);
  }
  
  public static void RenderBlock(String s, AxisAlignedBB bb, int c, Double width) {
    Sphere sph;
    Vec3d eyes;
    BufferBuilder BBuild2;
    camera.setPosition(((Entity)Objects.requireNonNull((T)mc.getRenderViewEntity())).posX, (mc.getRenderViewEntity()).posY, (mc.getRenderViewEntity()).posZ);
    if (!s.equalsIgnoreCase("Tracer") && !ModSettings.Rendernonsee.getValBoolean() && !camera.isBoundingBoxInFrustum(new AxisAlignedBB(bb.minX + (mc.getRenderManager()).viewerPosX, -10.0D, bb.minZ + (mc.getRenderManager()).viewerPosZ, bb.maxX + (mc.getRenderManager()).viewerPosX, 2500.0D, bb.maxZ + (mc.getRenderManager()).viewerPosZ)))
      return; 
    OpenGl();
    GlStateManager.glLineWidth((float)(1.5D * (width.doubleValue() + 1.0E-4D)));
    float a = ColorUtils.colorcalc(c, 24);
    float r = ColorUtils.colorcalc(c, 16);
    float g = ColorUtils.colorcalc(c, 8);
    float b = ColorUtils.colorcalc(c, 0);
    switch (s) {
      case "Shape":
        sph = new Sphere();
        sph.setDrawStyle(100013);
        GlStateManager.color(r, g, b, a);
        GlStateManager.translate((bb.maxX + bb.minX) / 2.0D, (bb.maxY + bb.minY) / 2.0D, (bb.maxZ + bb.minZ) / 2.0D);
        sph.draw(1.0F, (int)ModSettings.Spherelines.getValDouble(), (int)ModSettings.SphereDist.getValDouble());
        break;
      case "Flat":
        RenderGlobal.renderFilledBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.minY, bb.maxZ, r, g, b, a);
        break;
      case "FlatOutline":
        RenderGlobal.drawBoundingBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.minY, bb.maxZ, r, g, b, a);
        break;
      case "Full":
        RenderGlobal.drawBoundingBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, r, g, b, a);
        RenderGlobal.renderFilledBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, r, g, b, a);
        break;
      case "Outline":
        RenderGlobal.drawBoundingBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, r, g, b, a);
        break;
      case "Beacon":
        RenderGlobal.drawBoundingBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, r, g, b, a);
        RenderGlobal.drawBoundingBox(bb.minX, bb.minY, bb.minZ, bb.maxX, 0.0D - bb.maxY + 255.0D, bb.maxZ, r, g, b, a / 2.0F);
        RenderGlobal.renderFilledBox(bb.minX, bb.minY, bb.minZ, bb.maxX, 0.0D - bb.maxY + 255.0D, bb.maxZ, r, g, b, a / 4.0F);
        break;
      case "Tracer":
        GlStateManager.glBegin(1);
        GlStateManager.color(r, g, b, a);
        eyes = ActiveRenderInfo.getCameraPosition();
        GL11.glVertex3d(eyes.x, eyes.y, eyes.z);
        GL11.glVertex3d((bb.getCenter()).x, (bb.getCenter()).y, (bb.getCenter()).z);
        GlStateManager.glEnd();
        break;
      case "Xspot":
        BBuild2 = Tessellator.getInstance().getBuffer();
        BBuild2.begin(3, DefaultVertexFormats.POSITION_COLOR);
        BBuild2.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
        BBuild2.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, a).endVertex();
        BBuild2.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, 0.0F).endVertex();
        BBuild2.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, a).endVertex();
        Tessellator.getInstance().draw();
        break;
    } 
    ReleaseGl();
  }
  
  public static AxisAlignedBB Standardbb(BlockPos pos) {
    double renderPosX = pos.getX() - (mc.getRenderManager()).viewerPosX;
    double renderPosY = pos.getY() - (mc.getRenderManager()).viewerPosY;
    double renderPosZ = pos.getZ() - (mc.getRenderManager()).viewerPosZ;
    return new AxisAlignedBB(renderPosX, renderPosY, renderPosZ, renderPosX + 1.0D, renderPosY + 1.0D, renderPosZ + 1.0D);
  }
  
  public static AxisAlignedBB Boundingbb(Entity entity, double x, double y, double z, double x1, double y1, double z1) {
    double renderPosX = (entity.getEntityBoundingBox()).minX - (mc.getRenderManager()).viewerPosX;
    double renderPosY = (entity.getEntityBoundingBox()).minY - (mc.getRenderManager()).viewerPosY;
    double renderPosZ = (entity.getEntityBoundingBox()).minZ - (mc.getRenderManager()).viewerPosZ;
    double renderPosX2 = (entity.getEntityBoundingBox()).maxX - (mc.getRenderManager()).viewerPosX;
    double renderPosY2 = (entity.getEntityBoundingBox()).maxY - (mc.getRenderManager()).viewerPosY;
    double renderPosZ2 = (entity.getEntityBoundingBox()).maxZ - (mc.getRenderManager()).viewerPosZ;
    return new AxisAlignedBB(renderPosX + x, renderPosY + y, renderPosZ + z, renderPosX2 + x1, renderPosY2 + y1, renderPosZ2 + z1);
  }
  
  public static AxisAlignedBB Modifiedbb(BlockPos pos, int x, int y, int z) {
    double renderPosX = pos.getX() - (mc.getRenderManager()).viewerPosX;
    double renderPosY = pos.getY() - (mc.getRenderManager()).viewerPosY;
    double renderPosZ = pos.getZ() - (mc.getRenderManager()).viewerPosZ;
    return new AxisAlignedBB(renderPosX + x, renderPosY + y, renderPosZ + z, renderPosX + 1.0D + x, renderPosY + 1.0D + y, renderPosZ + 1.0D + z);
  }
  
  public static void RenderLine(List<Vec3d> list, int Color, double width, boolean valBoolean) {
    OpenGl();
    GL11.glEnable(32925);
    GL11.glLineWidth((float)width);
    ColorUtils.glColor(Color);
    GL11.glBegin(3);
    RenderManager renderManager = mc.getRenderManager();
    for (Vec3d blockPos : list) {
      if (valBoolean) {
        BlockPos snap = new BlockPos(blockPos);
        GL11.glVertex3d(snap.x - renderManager.viewerPosX, snap.y - renderManager.viewerPosY, snap.z - renderManager.viewerPosZ);
        continue;
      } 
      GL11.glVertex3d(blockPos.x - renderManager.viewerPosX, blockPos.y - renderManager.viewerPosY, blockPos.z - renderManager.viewerPosZ);
    } 
    GL11.glEnd();
    ReleaseGl();
    GL11.glDisable(32925);
  }
  
  public static Vec3d getInterpolatedRenderPos(Vec3d pos) {
    return (new Vec3d(pos.x, pos.y, pos.z)).subtract((mc.getRenderManager()).renderPosX, (mc.getRenderManager()).renderPosY, (mc.getRenderManager()).renderPosZ);
  }
  
  public static void SimpleNametag(Vec3d pos, String str) {
    OpenGl();
    boolean isThirdPersonFrontal = ((mc.getRenderManager()).options.thirdPersonView == 2);
    Vec3d interp = getInterpolatedRenderPos(pos);
    GlStateManager.translate(interp.x + 0.5D, interp.y + 0.75D, interp.z + 0.5D);
    GlStateManager.rotate(-(mc.getRenderManager()).playerViewY, 0.0F, 1.0F, 0.0F);
    GlStateManager.rotate((isThirdPersonFrontal ? -1 : true) * (mc.getRenderManager()).playerViewX, 1.0F, 0.0F, 0.0F);
    float m = (float)Math.pow(1.04D, mc.player.getDistance(pos.x, pos.y + 0.5D, pos.z));
    GlStateManager.scale(m, m, m);
    GlStateManager.scale(-0.025F, -0.025F, 0.025F);
    int i = mc.fontRenderer.getStringWidth(str) / 2;
    GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
    GlStateManager.enableTexture2D();
    mc.fontRenderer.drawStringWithShadow(str, -i, 9.0F, Color.WHITE.getRGB());
    GlStateManager.disableTexture2D();
    GlStateManager.glNormal3f(0.0F, 0.0F, 0.0F);
    ReleaseGl();
  }
  
  public static void drawRectOutline(double left, double top, double right, double bottom, double width, int color) {
    double l = left - width;
    double t = top - width;
    double r = right + width;
    double b = bottom + width;
    drawRectDouble(l, t, r, top, color);
    drawRectDouble(right, top, r, b, color);
    drawRectDouble(l, bottom, right, b, color);
    drawRectDouble(l, top, left, bottom, color);
  }
  
  public static void drawRectDouble(double left, double top, double right, double bottom, int color) {
    if (left < right) {
      double i = left;
      left = right;
      right = i;
    } 
    if (top < bottom) {
      double j = top;
      top = bottom;
      bottom = j;
    } 
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferbuilder = tessellator.getBuffer();
    GlStateManager.enableBlend();
    GlStateManager.disableTexture2D();
    GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
    ColorUtils.glColor(color);
    bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
    bufferbuilder.pos(left, bottom, 0.0D).endVertex();
    bufferbuilder.pos(right, bottom, 0.0D).endVertex();
    bufferbuilder.pos(right, top, 0.0D).endVertex();
    bufferbuilder.pos(left, top, 0.0D).endVertex();
    tessellator.draw();
    GlStateManager.enableTexture2D();
    GlStateManager.disableBlend();
  }
}
