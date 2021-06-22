package Method.Client.module.command;

import Method.Client.utils.visual.ChatUtils;
import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.world.World;

public class FakePlayer extends Command {
  public FakePlayer() {
    super("FakePlayer");
  }
  
  public void runCommand(String s, String[] args) {
    try {
      EntityOtherPlayerMP fake = new EntityOtherPlayerMP((World)mc.world, new GameProfile(UUID.randomUUID(), args[0]));
      fake.setPosition(mc.player.posX, mc.player.posY, mc.player.posZ);
      mc.world.loadedEntityList.add(fake);
      ChatUtils.message("Added Fake Player ");
    } catch (Exception e) {
      ChatUtils.error("Usage: " + getSyntax());
    } 
  }
  
  public String getDescription() {
    return "FakePlayer Spawner";
  }
  
  public String getSyntax() {
    return "FakePlayer <Name>";
  }
}
