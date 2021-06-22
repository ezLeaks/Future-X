package Method.Client.module.movement;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.TimerUtils;
import Method.Client.utils.system.Connection;
import Method.Client.utils.system.Wrapper;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AntiFall extends Module {
  private int state;
  
  private double fall;
  
  Setting mode;
  
  public TimerUtils timer;
  
  public AntiFall() {
    super("NoFall", 0, Category.MOVEMENT, "Take no fall damage");
    this.mode = Main.setmgr.add(new Setting("Mode", this, "Vanilla", new String[] { "Vanilla", "LAAC", "Hypixel", "SpoofGround", "NoGround", "AAC", "AAC3.3.15", "Spartan", "Quick", "NCP" }));
    this.timer = new TimerUtils();
  }
  
  public void onEnable() {
    super.onEnable();
    this.fall = 0.0D;
  }
  
  public double getDistanceToGround() {
    for (int var3 = 0; var3 < 256; var3++) {
      BlockPos var4 = new BlockPos(mc.player.posX, mc.player.posY - var3, mc.player.posZ);
      if (mc.world.getBlockState(var4).getBlock() != Blocks.AIR && mc.world.getBlockState(var4).getBlock() != Blocks.GRASS && mc.world.getBlockState(var4).getBlock() != Blocks.TALLGRASS && mc.world.getBlockState(var4).getBlock() != Blocks.RED_FLOWER && mc.world.getBlockState(var4).getBlock() != Blocks.YELLOW_FLOWER) {
        double var1 = mc.player.posY - var4.getY();
        return var1 - 1.0D;
      } 
    } 
    return 256.0D;
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (this.mode.getValString().equalsIgnoreCase("NCP")) {
      Block block = mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY - 6.0D, mc.player.posZ)).getBlock();
      Block block2 = mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY - 5.0D, mc.player.posZ)).getBlock();
      Block block3 = mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY - 4.0D, mc.player.posZ)).getBlock();
      if ((block != Blocks.AIR || block2 != Blocks.AIR || block3 != Blocks.AIR) && mc.player.fallDistance > 2.0F) {
        mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.1D, mc.player.posZ, false));
        mc.player.motionY = -10.0D;
        mc.player.fallDistance = MathHelper.SQRT_2;
      } 
    } 
    if (this.mode.getValString().equalsIgnoreCase("Vanilla") && 
      mc.player.fallDistance > 2.0F)
      Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer(true)); 
    if (this.mode.getValString().equalsIgnoreCase("Quick") && mc.player.fallDistance > 3.1D) {
      if (getDistanceToGround() > 40.0D)
        return; 
      Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 0.5D, mc.player.posZ, true));
      Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.5D, mc.player.posZ, true));
      Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, -9.0D, mc.player.posZ, true));
      Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, true));
      Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + getDistanceToGround(), mc.player.posZ, true));
      mc.player.motionY += 0.3D;
    } 
    if (this.mode.getValString().equalsIgnoreCase("LAAC") && 
      mc.player != null && mc.world != null && mc.player.fallDistance > 2.0F && 
      mc.player.ticksExisted % 6 == 0)
      mc.player.setPosition(mc.player.posX, mc.player.posY - mc.player.fallDistance, mc.player.posZ); 
    if (this.mode.getValString().equalsIgnoreCase("AAC")) {
      if (mc.player.fallDistance > 2.0F) {
        Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer(true));
        this.state = 2;
      } else if (this.state == 2 && mc.player.fallDistance < 2.0F) {
        mc.player.motionY = 0.1D;
        this.state = 3;
      } 
      switch (this.state) {
        case 3:
          mc.player.motionY = 0.1D;
          this.state = 4;
          break;
        case 4:
          mc.player.motionY = 0.1D;
          this.state = 5;
          break;
        case 5:
          mc.player.motionY = 0.1D;
          this.state = 1;
          break;
      } 
    } 
    if (this.mode.getValString().equalsIgnoreCase("aac3.3.15") && 
      mc.player.fallDistance > 2.0F) {
      if (!mc.isIntegratedServerRunning())
        Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, Double.NaN, mc.player.posZ, false)); 
      mc.player.fallDistance = -9999.0F;
    } 
    if (this.mode.getValString().equalsIgnoreCase("spartan")) {
      this.timer.reset();
      if (mc.player.fallDistance > 1.5D && this.timer.hasReached(10.0F)) {
        Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 10.0D, mc.player.posZ, true));
        Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 10.0D, mc.player.posZ, true));
        this.timer.reset();
      } 
    } 
    if (this.mode.getValString().equalsIgnoreCase("hypixel")) {
      if (!mc.player.onGround) {
        if (mc.player.motionY < -0.08D)
          this.fall -= mc.player.motionY; 
        if (this.fall > 2.0D) {
          this.fall = 0.0D;
          mc.player.onGround = false;
        } 
      } 
      this.fall = 0.0D;
    } 
    super.onClientTick(event);
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (packet instanceof CPacketPlayer) {
      CPacketPlayer playerPacket = (CPacketPlayer)packet;
      if (this.mode.getValString().equalsIgnoreCase("SpoofGround"))
        playerPacket.onGround = true; 
      if (this.mode.getValString().equalsIgnoreCase("NoGround"))
        playerPacket.onGround = false; 
    } 
    return true;
  }
}
