package Method.Client.module.combat;

import Method.Client.Main;
import Method.Client.managers.FriendManager;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import Method.Client.utils.visual.ChatUtils;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.HashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class TotemPop extends Module {
  public static HashMap<String, Integer> popList;
  
  Setting Friend = Main.setmgr.add(this.Friend = new Setting("Friend", this, true));
  
  public TotemPop() {
    super("TotemPop", 0, Category.COMBAT, "TotemPop");
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (packet instanceof SPacketEntityStatus) {
      SPacketEntityStatus packet2 = (SPacketEntityStatus)packet;
      if (packet2.getOpCode() == 35) {
        Entity entity = packet2.getEntity((World)mc.world);
        pop(entity);
      } 
    } 
    return true;
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    for (EntityPlayer player : mc.world.playerEntities) {
      if (player.getHealth() <= 0.0F && popList.containsKey(player.getName())) {
        ChatUtils.message(ChatFormatting.RED + player.getName() + " died after popping " + ChatFormatting.GREEN + popList.get(player.getName()) + ChatFormatting.RED + " totems!");
        popList.remove(player.getName(), popList.get(player.getName()));
      } 
    } 
    super.onClientTick(event);
  }
  
  public void pop(Entity entity) {
    if (mc.player == null || mc.world == null)
      return; 
    if (popList == null)
      popList = new HashMap<>(); 
    if (this.Friend.getValBoolean() || !FriendManager.isFriend(entity.getName()))
      if (popList.get(entity.getName()) == null) {
        popList.put(entity.getName(), Integer.valueOf(1));
        ChatUtils.message(ChatFormatting.RED + entity.getName() + " popped " + ChatFormatting.YELLOW + '\001' + ChatFormatting.RED + " totem!");
      } else {
        Check(entity);
      }  
  }
  
  private void Check(Entity entity) {
    if (popList.get(entity.getName()) != null) {
      int popCounter = ((Integer)popList.get(entity.getName())).intValue();
      popList.put(entity.getName(), Integer.valueOf(++popCounter));
      ChatUtils.message(ChatFormatting.RED + entity.getName() + ChatFormatting.RED + " popped " + ChatFormatting.YELLOW + ++popCounter + ChatFormatting.RED + " totems!");
    } 
  }
  
  public static int getpops(Entity entity) {
    if (popList.get(entity.getName()) != null)
      return ((Integer)popList.get(entity.getName())).intValue(); 
    return 0;
  }
}
