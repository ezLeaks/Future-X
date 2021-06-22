package Method.Client.utils.proxy.Overrides;

import Method.Client.module.render.ArmorRender;
import Method.Client.utils.proxy.renderers.ModRenderArmorStand;
import Method.Client.utils.proxy.renderers.ModRenderBoat;
import Method.Client.utils.proxy.renderers.ModRenderGiantZombie;
import Method.Client.utils.proxy.renderers.ModRenderHusk;
import Method.Client.utils.proxy.renderers.ModRenderItem;
import Method.Client.utils.proxy.renderers.ModRenderPigZombie;
import Method.Client.utils.proxy.renderers.ModRenderPlayer;
import Method.Client.utils.proxy.renderers.ModRenderSkeleton;
import Method.Client.utils.proxy.renderers.ModRenderStray;
import Method.Client.utils.proxy.renderers.ModRenderWitherSkeleton;
import Method.Client.utils.proxy.renderers.ModRenderZombie;
import Method.Client.utils.proxy.renderers.ModRenderZombieVillager;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.entity.RenderItemFrame;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.RenderPotion;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityHusk;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityStray;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;

public class ColorMix {
  public static ModRenderItem modRenderItem;
  
  public static void replaceRenderers() {
    Minecraft mc = Minecraft.getMinecraft();
    try {
      modRenderItem = new ModRenderItem(mc.renderItem, mc.modelManager);
      mc.renderItem = (RenderItem)modRenderItem;
      mc.itemRenderer.itemRenderer = (RenderItem)modRenderItem;
      mc.renderManager.playerRenderer = (RenderPlayer)new ModRenderPlayer(mc.renderManager);
      mc.renderManager.skinMap.put("default", new ModRenderPlayer(mc.renderManager));
      mc.renderManager.skinMap.put("slim", new ModRenderPlayer(mc.renderManager, true));
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } 
    mc.renderManager.entityRenderMap.put(EntityBoat.class, new ModRenderBoat(mc.renderManager));
    mc.renderManager.entityRenderMap.put(EntityItemFrame.class, new RenderItemFrame(mc.renderManager, (RenderItem)modRenderItem));
    mc.renderManager.entityRenderMap.put(EntitySnowball.class, new RenderSnowball(mc.renderManager, Items.SNOWBALL, (RenderItem)modRenderItem));
    mc.renderManager.entityRenderMap.put(EntityEnderPearl.class, new RenderSnowball(mc.renderManager, Items.ENDER_PEARL, (RenderItem)modRenderItem));
    mc.renderManager.entityRenderMap.put(EntityEnderEye.class, new RenderSnowball(mc.renderManager, Items.ENDER_EYE, (RenderItem)modRenderItem));
    mc.renderManager.entityRenderMap.put(EntityEgg.class, new RenderSnowball(mc.renderManager, Items.EGG, (RenderItem)modRenderItem));
    mc.renderManager.entityRenderMap.put(EntityPotion.class, new RenderPotion(mc.renderManager, (RenderItem)modRenderItem));
    mc.renderManager.entityRenderMap.put(EntityExpBottle.class, new RenderSnowball(mc.renderManager, Items.EXPERIENCE_BOTTLE, (RenderItem)modRenderItem));
    mc.renderManager.entityRenderMap.put(EntityFireworkRocket.class, new RenderSnowball(mc.renderManager, Items.FIREWORKS, (RenderItem)modRenderItem));
    mc.renderManager.entityRenderMap.put(EntityItem.class, new RenderEntityItem(mc.renderManager, (RenderItem)modRenderItem));
    mc.renderManager.entityRenderMap.put(EntitySkeleton.class, new ModRenderSkeleton(mc.renderManager));
    mc.renderManager.entityRenderMap.put(EntityWitherSkeleton.class, new ModRenderWitherSkeleton(mc.renderManager));
    mc.renderManager.entityRenderMap.put(EntityStray.class, new ModRenderStray(mc.renderManager));
    mc.renderManager.entityRenderMap.put(EntityZombie.class, new ModRenderZombie(mc.renderManager));
    mc.renderManager.entityRenderMap.put(EntityHusk.class, new ModRenderHusk(mc.renderManager));
    mc.renderManager.entityRenderMap.put(EntityZombieVillager.class, new ModRenderZombieVillager(mc.renderManager));
    mc.renderManager.entityRenderMap.put(EntityGiantZombie.class, new ModRenderGiantZombie(mc.renderManager, 6.0F));
    mc.renderManager.entityRenderMap.put(EntityPigZombie.class, new ModRenderPigZombie(mc.renderManager));
    mc.renderManager.entityRenderMap.put(EntityArmorStand.class, new ModRenderArmorStand(mc.renderManager));
    ((IReloadableResourceManager)mc.getResourceManager()).registerReloadListener((IResourceManagerReloadListener)modRenderItem);
  }
  
  public static int getColorForEnchantment(Map<Enchantment, Integer> enchMap) {
    if (ArmorRender.CustomColor.getValBoolean())
      return ArmorRender.Color.getcolor(); 
    if (!ArmorRender.enableColoredGlint.getValBoolean())
      return -8372020; 
    int alpha = 1711276032;
    if (enchMap.containsKey(Enchantments.BANE_OF_ARTHROPODS))
      return alpha | ArmorRender.BANE_OF_ARTHROPODS; 
    if (enchMap.containsKey(Enchantments.FIRE_ASPECT))
      return alpha | ArmorRender.FIRE_ASPECT; 
    if (enchMap.containsKey(Enchantments.KNOCKBACK))
      return alpha | ArmorRender.KNOCKBACK; 
    if (enchMap.containsKey(Enchantments.LOOTING))
      return alpha | ArmorRender.LOOTING; 
    if (enchMap.containsKey(Enchantments.SHARPNESS))
      return alpha | ArmorRender.SHARPNESS; 
    if (enchMap.containsKey(Enchantments.SMITE))
      return alpha | ArmorRender.SMITE; 
    if (enchMap.containsKey(Enchantments.SWEEPING))
      return alpha | ArmorRender.SWEEPING; 
    if (enchMap.containsKey(Enchantments.UNBREAKING))
      return alpha | ArmorRender.UNBREAKING; 
    if (enchMap.containsKey(Enchantments.FLAME))
      return alpha | ArmorRender.FLAME; 
    if (enchMap.containsKey(Enchantments.INFINITY))
      return alpha | ArmorRender.INFINITY; 
    if (enchMap.containsKey(Enchantments.POWER))
      return alpha | ArmorRender.POWER; 
    if (enchMap.containsKey(Enchantments.PUNCH))
      return alpha | ArmorRender.PUNCH; 
    if (enchMap.containsKey(Enchantments.EFFICIENCY))
      return alpha | ArmorRender.EFFICIENCY; 
    if (enchMap.containsKey(Enchantments.FORTUNE))
      return alpha | ArmorRender.FORTUNE; 
    if (enchMap.containsKey(Enchantments.SILK_TOUCH))
      return alpha | ArmorRender.SILK_TOUCH; 
    if (enchMap.containsKey(Enchantments.LUCK_OF_THE_SEA))
      return alpha | ArmorRender.LUCK_OF_THE_SEA; 
    if (enchMap.containsKey(Enchantments.LURE))
      return alpha | ArmorRender.LURE; 
    if (enchMap.containsKey(Enchantments.AQUA_AFFINITY))
      return alpha | ArmorRender.AQUA_AFFINITY; 
    if (enchMap.containsKey(Enchantments.BLAST_PROTECTION))
      return alpha | ArmorRender.BLAST_PROTECTION; 
    if (enchMap.containsKey(Enchantments.DEPTH_STRIDER))
      return alpha | ArmorRender.DEPTH_STRIDER; 
    if (enchMap.containsKey(Enchantments.FEATHER_FALLING))
      return alpha | ArmorRender.FEATHER_FALLING; 
    if (enchMap.containsKey(Enchantments.FIRE_PROTECTION))
      return alpha | ArmorRender.FIRE_PROTECTION; 
    if (enchMap.containsKey(Enchantments.FROST_WALKER))
      return alpha | ArmorRender.FROST_WALKER; 
    if (enchMap.containsKey(Enchantments.MENDING))
      return alpha | ArmorRender.MENDING; 
    if (enchMap.containsKey(Enchantments.PROJECTILE_PROTECTION))
      return alpha | ArmorRender.PROJECTILE_PROTECTION; 
    if (enchMap.containsKey(Enchantments.PROTECTION))
      return alpha | ArmorRender.PROTECTION; 
    if (enchMap.containsKey(Enchantments.RESPIRATION))
      return alpha | ArmorRender.RESPIRATION; 
    if (enchMap.containsKey(Enchantments.THORNS))
      return alpha | ArmorRender.THORNS; 
    if (enchMap.containsKey(Enchantments.VANISHING_CURSE))
      return alpha | ArmorRender.VANISHING_CURSE; 
    if (enchMap.containsKey(Enchantments.BINDING_CURSE))
      return alpha | ArmorRender.BINDING_CURSE; 
    return -8372020;
  }
  
  public static float alphaFromColor() {
    return 0.32F;
  }
  
  public static float redFromColor(int parColor) {
    return (parColor >> 16 & 0xFF) / 255.0F;
  }
  
  public static float greenFromColor(int parColor) {
    return (parColor >> 8 & 0xFF) / 255.0F;
  }
  
  public static float blueFromColor(int parColor) {
    return (parColor & 0xFF) / 255.0F;
  }
}
