package Method.Client.module.misc;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.visual.ChatUtils;
import java.util.Comparator;
import java.util.Objects;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AutoNametag extends Module {
  Setting Radius = Main.setmgr.add(new Setting("range", this, 4.0D, 0.0D, 10.0D, true));
  
  Setting ReplaceOldNames = Main.setmgr.add(new Setting("ReplaceOldNames", this, true));
  
  Setting AutoSwitch = Main.setmgr.add(new Setting("AutoSwitch", this, true));
  
  Setting WithersOnly = Main.setmgr.add(new Setting("WithersOnly ", this, true));
  
  public AutoNametag() {
    super("AutoNametag", 0, Category.MISC, "AutoNametag");
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (mc.currentScreen != null)
      return; 
    if (!(mc.player.getHeldItemMainhand().getItem() instanceof net.minecraft.item.ItemNameTag)) {
      int i1 = -1;
      if (this.AutoSwitch.getValBoolean())
        for (int i = 0; i < 9; i++) {
          ItemStack item = mc.player.inventory.getStackInSlot(i);
          if (!item.isEmpty())
            if (item.getItem() instanceof net.minecraft.item.ItemNameTag && 
              item.hasDisplayName()) {
              i1 = i;
              mc.player.inventory.currentItem = i1;
              mc.playerController.updateController();
              break;
            }  
        }  
      if (i1 == -1)
        return; 
    } 
    ItemStack name = mc.player.getHeldItemMainhand();
    if (!name.hasDisplayName())
      return; 
    EntityLivingBase l_Entity = mc.world.loadedEntityList.stream().filter(p_Entity -> IsValidEntity(p_Entity, name.getDisplayName())).map(p_Entity -> (EntityLivingBase)p_Entity).min(Comparator.comparing(p_Entity -> Float.valueOf(mc.player.getDistance((Entity)p_Entity)))).orElse(null);
    if (l_Entity != null) {
      double[] lPos = calculateLookAt(l_Entity.posX, l_Entity.posY, l_Entity.posZ, (EntityPlayer)mc.player);
      ChatUtils.message(String.format("Gave %s the nametag of %s", new Object[] { l_Entity.getName(), name.getDisplayName() }));
      mc.player.rotationYawHead = (float)lPos[0];
      ((NetHandlerPlayClient)Objects.<NetHandlerPlayClient>requireNonNull(mc.getConnection())).sendPacket((Packet)new CPacketUseEntity((Entity)l_Entity, EnumHand.MAIN_HAND));
    } 
  }
  
  public static double[] calculateLookAt(double px, double py, double pz, EntityPlayer me) {
    double dirx = me.posX - px;
    double diry = me.posY - py;
    double dirz = me.posZ - pz;
    double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);
    dirx /= len;
    diry /= len;
    dirz /= len;
    double pitch = Math.asin(diry);
    double yaw = Math.atan2(dirz, dirx);
    pitch = pitch * 180.0D / Math.PI;
    yaw = yaw * 180.0D / Math.PI;
    yaw += 90.0D;
    return new double[] { yaw, pitch };
  }
  
  private boolean IsValidEntity(Entity entity, String pName) {
    if (!(entity instanceof EntityLivingBase))
      return false; 
    if (entity.getDistance((Entity)mc.player) > this.Radius.getValDouble())
      return false; 
    if (entity instanceof EntityPlayer)
      return false; 
    if (!entity.getCustomNameTag().isEmpty() && !this.ReplaceOldNames.getValBoolean())
      return false; 
    if (this.ReplaceOldNames.getValBoolean() && 
      !entity.getCustomNameTag().isEmpty() && entity.getName().equals(pName))
      return false; 
    return (!this.WithersOnly.getValBoolean() || entity instanceof net.minecraft.entity.boss.EntityWither);
  }
}
