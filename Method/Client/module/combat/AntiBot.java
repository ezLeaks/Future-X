package Method.Client.module.combat;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Utils;
import Method.Client.utils.system.Connection;
import Method.Client.utils.system.Wrapper;
import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketSpawnPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AntiBot extends Module {
  public static ArrayList<EntityBot> bots = new ArrayList<>();
  
  public Setting level = Main.setmgr.add(new Setting("level", this, 0.0D, 0.0D, 6.0D, false));
  
  public Setting tick = Main.setmgr.add(new Setting("tick", this, 0.0D, 0.0D, 999.0D, true));
  
  public Setting ifInAir = Main.setmgr.add(new Setting("InAir", this, false));
  
  public Setting ifGround = Main.setmgr.add(new Setting("OnGround", this, false));
  
  public Setting ifZeroHealth = Main.setmgr.add(new Setting("ZeroHealth", this, false));
  
  public Setting ifInvisible = Main.setmgr.add(new Setting("Invisible", this, false));
  
  public Setting ifEntityId = Main.setmgr.add(new Setting("EntityId", this, false));
  
  public Setting ifTabName = Main.setmgr.add(new Setting("OutTabName", this, false));
  
  public Setting ifPing = Main.setmgr.add(new Setting("PingCheck", this, false));
  
  public Setting remove = Main.setmgr.add(new Setting("RemoveBots", this, false));
  
  public Setting gwen = Main.setmgr.add(new Setting("Gwen", this, false));
  
  public AntiBot() {
    super("AntiBot", 0, Category.COMBAT, "Does not hit bots");
  }
  
  public void onEnable() {
    bots.clear();
    super.onEnable();
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (this.gwen.getValBoolean())
      for (Object entity : mc.world.loadedEntityList) {
        if (packet instanceof SPacketSpawnPlayer) {
          SPacketSpawnPlayer spawn = (SPacketSpawnPlayer)packet;
          double posX = spawn.getX() / 32.0D;
          double posY = spawn.getY() / 32.0D;
          double posZ = spawn.getZ() / 32.0D;
          double difX = mc.player.posX - posX;
          double difY = mc.player.posY - posY;
          double difZ = mc.player.posZ - posZ;
          double dist = Math.sqrt(difX * difX + difY * difY + difZ * difZ);
          if (dist <= 17.0D && posX != mc.player.posX && posY != mc.player.posY && posZ != mc.player.posZ)
            return false; 
        } 
      }  
    return true;
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (this.tick.getValDouble() > 0.0D)
      bots.clear(); 
    for (Object object : mc.world.loadedEntityList) {
      if (object instanceof EntityLivingBase) {
        EntityLivingBase entity = (EntityLivingBase)object;
        if (!(entity instanceof net.minecraft.client.entity.EntityPlayerSP) && entity instanceof EntityPlayer) {
          EntityPlayer bot = (EntityPlayer)entity;
          if (!isBotBase(bot)) {
            int ailevel = (int)this.level.getValDouble();
            boolean isAi = (ailevel > 0.0D);
            if (isAi && botPercentage(bot) > ailevel) {
              addBot(bot);
              continue;
            } 
            if (!isAi && botCondition(bot))
              addBot(bot); 
            continue;
          } 
          addBot(bot);
          if (this.remove.getValBoolean())
            mc.world.removeEntity((Entity)bot); 
        } 
      } 
    } 
    super.onClientTick(event);
  }
  
  void addBot(EntityPlayer player) {
    if (!isBot(player))
      bots.add(new EntityBot(player)); 
  }
  
  public static boolean isBot(EntityPlayer player) {
    for (EntityBot bot : bots) {
      if (bot.getName().equals(player.getName())) {
        if (player.isInvisible() != bot.isInvisible())
          return player.isInvisible(); 
        return true;
      } 
      if (bot.getId() == player.getEntityId() || bot
        .getUuid().equals(player.getGameProfile().getId()))
        return true; 
    } 
    return false;
  }
  
  boolean botCondition(EntityPlayer bot) {
    if (this.tick.getValDouble() > 0.0D && bot.ticksExisted < this.tick.getValDouble())
      return true; 
    if (this.ifInAir.getValBoolean() && bot
      .isInvisible() && bot.motionY == 0.0D && bot.posY > mc.player.posY + 1.0D && 
      
      Utils.isBlockMaterial((new BlockPos((Entity)bot)).down(), Blocks.AIR))
      return true; 
    if (this.ifGround.getValBoolean() && bot.motionY == 0.0D && !bot.collidedVertically && bot.onGround && bot.posY % 1.0D != 0.0D && bot.posY % 0.5D != 0.0D)
      return true; 
    if (this.ifZeroHealth.getValBoolean() && bot.getHealth() <= 0.0F)
      return true; 
    if (this.ifInvisible.getValBoolean() && bot.isInvisible())
      return true; 
    if (this.ifEntityId.getValBoolean() && bot.getEntityId() >= 1000000000)
      return true; 
    if (this.ifTabName.getValBoolean()) {
      boolean isTabName = false;
      for (NetworkPlayerInfo npi : ((NetHandlerPlayClient)Objects.<NetHandlerPlayClient>requireNonNull(Wrapper.INSTANCE.mc().getConnection())).getPlayerInfoMap()) {
        npi.getGameProfile();
        if (npi.getGameProfile().getName().contains(bot.getName()))
          isTabName = true; 
      } 
      return !isTabName;
    } 
    return false;
  }
  
  int botPercentage(EntityPlayer bot) {
    int percentage = 0;
    if (this.tick.getValDouble() > 0.0D && bot.ticksExisted < this.tick.getValDouble())
      percentage++; 
    if (this.ifInAir.getValBoolean() && bot
      .isInvisible() && bot.posY > mc.player.posY + 1.0D && 
      
      Utils.isBlockMaterial((new BlockPos((Entity)bot)).down(), Blocks.AIR))
      percentage++; 
    if (this.ifGround.getValBoolean() && bot.motionY == 0.0D && !bot.collidedVertically && bot.onGround && bot.posY % 1.0D != 0.0D && bot.posY % 0.5D != 0.0D)
      percentage++; 
    if (this.ifZeroHealth.getValBoolean() && bot.getHealth() <= 0.0F)
      percentage++; 
    if (this.ifInvisible.getValBoolean() && bot.isInvisible())
      percentage++; 
    if (this.ifEntityId.getValBoolean() && bot.getEntityId() >= 1000000000)
      percentage++; 
    if (this.ifTabName.getValBoolean()) {
      boolean isTabName = false;
      for (NetworkPlayerInfo npi : ((NetHandlerPlayClient)Objects.<NetHandlerPlayClient>requireNonNull(Wrapper.INSTANCE.mc().getConnection())).getPlayerInfoMap()) {
        npi.getGameProfile();
        if (npi.getGameProfile().getName().contains(bot.getName()))
          isTabName = true; 
      } 
      if (!isTabName)
        percentage++; 
    } 
    return percentage;
  }
  
  boolean isBotBase(EntityPlayer bot) {
    if (isBot(bot))
      return true; 
    bot.getGameProfile();
    GameProfile botProfile = bot.getGameProfile();
    bot.getUniqueID();
    if (botProfile.getName() == null)
      return true; 
    String botName = botProfile.getName();
    return (botName.contains("Body #") || botName.contains("NPC") || botName
      .equalsIgnoreCase(Utils.getEntityNameColor((EntityLivingBase)bot)));
  }
  
  public static class EntityBot {
    private final String name;
    
    private final int id;
    
    private final UUID uuid;
    
    private final boolean invisible;
    
    public EntityBot(EntityPlayer player) {
      this.name = String.valueOf(player.getGameProfile().getName());
      this.id = player.getEntityId();
      this.uuid = player.getGameProfile().getId();
      this.invisible = player.isInvisible();
    }
    
    public int getId() {
      return this.id;
    }
    
    public String getName() {
      return this.name;
    }
    
    public UUID getUuid() {
      return this.uuid;
    }
    
    public boolean isInvisible() {
      return this.invisible;
    }
  }
}
