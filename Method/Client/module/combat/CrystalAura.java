package Method.Client.module.combat;

import Method.Client.Main;
import Method.Client.managers.FriendManager;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.TimerUtils;
import Method.Client.utils.Utils;
import Method.Client.utils.system.Connection;
import Method.Client.utils.system.Wrapper;
import Method.Client.utils.visual.RenderUtils;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.potion.Potion;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class CrystalAura extends Module {
  public Setting range = Main.setmgr.add(new Setting("Hit Range", this, 6.0D, 0.0D, 8.0D, false));
  
  public Setting Explode = Main.setmgr.add(new Setting("Explode", this, true));
  
  public Setting Placer = Main.setmgr.add(new Setting("Place Crystals", this, true));
  
  public Setting Damage = Main.setmgr.add(new Setting("Max Self Dmg", this, 14.0D, 0.0D, 20.0D, false));
  
  public Setting OtherDamage = Main.setmgr.add(new Setting("Min Enemy Dmg", this, 4.0D, 0.0D, 20.0D, false));
  
  public Setting HitDelayBetween = Main.setmgr.add(new Setting("Hit Delay", this, 0.2D, 0.0D, 1.0D, false));
  
  public Setting PlaceDelayBetween = Main.setmgr.add(new Setting("Place Delay", this, 0.2D, 0.0D, 1.0D, false));
  
  public Setting FastSwitch = Main.setmgr.add(new Setting("Fast Switch", this, true));
  
  public Setting Packetreach = Main.setmgr.add(new Setting("Packet reach", this, false));
  
  public Setting Hand = Main.setmgr.add(new Setting("Hand", this, "Mainhand", new String[] { "Mainhand", "Offhand", "Both", "None" }));
  
  public Setting SpoofAngle = Main.setmgr.add(new Setting("Spoof Angle", this, true));
  
  public Setting OverlayColor = Main.setmgr.add(new Setting("OverlayColor", this, 0.0D, 1.0D, 1.0D, 0.62D));
  
  public Setting Mode = Main.setmgr.add(new Setting("Render", this, "Outline", BlockEspOptions()));
  
  public Setting LineWidth = Main.setmgr.add(new Setting("LineWidth", this, 1.0D, 0.0D, 3.0D, false));
  
  public Setting HoleJiggle = Main.setmgr.add(new Setting("Hole Jiggle", this, true));
  
  public Setting MultiPlace = Main.setmgr.add(new Setting("MultiPlace", this, true));
  
  public Setting SwordHit = Main.setmgr.add(new Setting("SwordHit", this, false));
  
  private final TimerUtils attackTimer = new TimerUtils();
  
  private final TimerUtils placeTimer = new TimerUtils();
  
  private final List<PlaceLocation> placeLocations = new ArrayList<>();
  
  private static ExecutorService executor;
  
  public float[] Rots;
  
  public CrystalAura() {
    super("CrystalAura", 0, Category.COMBAT, "CrystalAura");
  }
  
  public void setup() {
    executor = Executors.newSingleThreadExecutor();
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    ArrayList<EntityEnderCrystal> Crystals = new ArrayList<>();
    for (Entity entity : mc.world.loadedEntityList) {
      if (entity instanceof EntityEnderCrystal)
        Crystals.add((EntityEnderCrystal)entity); 
    } 
    try {
      executor.execute(() -> {
            for (Entity entity : mc.world.loadedEntityList) {
              if (entity instanceof EntityOtherPlayerMP) {
                EntityOtherPlayerMP playerMP = (EntityOtherPlayerMP)entity;
                if (FriendManager.isFriend(playerMP.getName()))
                  continue; 
                if (this.Explode.getValBoolean()) {
                  if (this.MultiPlace.getValBoolean())
                    Collections.shuffle(Crystals); 
                  for (EntityEnderCrystal crystal : Crystals) {
                    if (mc.player.canEntityBeSeen((Entity)crystal) && playerMP.getDistance((Entity)crystal) < 12.0F && crystal.getDistance((Entity)mc.player) <= this.range.getValDouble() && Calculate_Damage(crystal.posX, crystal.posY, crystal.posZ, (Entity)mc.player) < this.Damage.getValDouble() && Calculate_Damage(crystal.posX, crystal.posY, crystal.posZ, (Entity)playerMP) > this.OtherDamage.getValDouble())
                      processAttack(crystal); 
                  } 
                } 
                if (this.placeTimer.isDelay((long)(this.PlaceDelayBetween.getValDouble() * 1000.0D)) && this.Placer.getValBoolean()) {
                  ArrayList<BlockPos> posable = new ArrayList<>();
                  if (mc.player.getDistance((Entity)playerMP) < 13.0F)
                    for (int x = (int)playerMP.posX - 8; x <= (int)playerMP.posX + 8; x++) {
                      for (int z = (int)playerMP.posZ - 8; z <= (int)playerMP.posZ + 8; z++) {
                        for (int y = (int)playerMP.posY - 4; y <= (int)playerMP.posY + 3; y++) {
                          BlockPos blockPos = new BlockPos(x, y, z);
                          if (canPlaceCrystal(blockPos) && Calculate_Damage(blockPos.x + 0.5D, (blockPos.y + 1), blockPos.z + 0.5D, (Entity)mc.player) < this.Damage.getValDouble() && Calculate_Damage(blockPos.x + 0.5D, (blockPos.y + 1), blockPos.z + 0.5D, (Entity)playerMP) > this.OtherDamage.getValDouble())
                            posable.add(blockPos); 
                        } 
                      } 
                    }  
                  placeCrystalOnBlock(posable, playerMP);
                } 
              } 
            } 
          });
    } catch (ConcurrentModificationException concurrentModificationException) {}
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (side == Connection.Side.IN && 
      packet instanceof SPacketSpawnObject) {
      SPacketSpawnObject packetSpawnObject = (SPacketSpawnObject)packet;
      if (packetSpawnObject.getType() == 51)
        for (PlaceLocation placeLocation : this.placeLocations) {
          if (placeLocation.distanceSq((Vec3i)new BlockPos(packetSpawnObject.getX(), packetSpawnObject.getY(), packetSpawnObject.getZ())) < 2.0D) {
            placeLocation.Timeset = System.currentTimeMillis();
            placeLocation.PacketConfirmed = true;
          } 
        }  
    } 
    if (side == Connection.Side.OUT && 
      this.SpoofAngle.getValBoolean() && this.Rots != null && (
      packet instanceof CPacketPlayer.Rotation || packet instanceof CPacketPlayer.PositionRotation)) {
      CPacketPlayer packet2 = (CPacketPlayer)packet;
      packet2.yaw = this.Rots[0];
      packet2.pitch = this.Rots[1];
    } 
    return true;
  }
  
  public void onRenderWorldLast(RenderWorldLastEvent event) {
    for (PlaceLocation placeLocation : this.placeLocations) {
      if (placeLocation.PacketConfirmed) {
        RenderUtils.RenderBlock(this.Mode.getValString(), RenderUtils.Standardbb(new BlockPos(placeLocation.x, placeLocation.y, placeLocation.z)), this.OverlayColor.getcolor(), Double.valueOf(this.LineWidth.getValDouble()));
        RenderUtils.SimpleNametag(new Vec3d(placeLocation.x, placeLocation.y, placeLocation.z), (new DecimalFormat("0.00")).format(placeLocation.damage));
      } 
    } 
    this.placeLocations.removeIf(placeLocation -> (placeLocation.Timeset + 1000.0D < System.currentTimeMillis()));
  }
  
  public boolean canPlaceCrystal(BlockPos blockPos) {
    try {
      if (mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN && mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK)
        return false; 
      if (mc.world.getBlockState(blockPos.up()).getBlock() != Blocks.AIR || mc.world.getBlockState(blockPos.up(2)).getBlock() != Blocks.AIR)
        return false; 
      for (Object entity : mc.world.getEntitiesWithinAABB(Entity.class, (new AxisAlignedBB(blockPos)).grow(0.0D, 3.0D, 0.0D))) {
        if ((!(entity instanceof EntityEnderCrystal) || this.MultiPlace.getValBoolean()) && !(entity instanceof net.minecraft.entity.item.EntityXPOrb) && !(entity instanceof net.minecraft.entity.item.EntityItem))
          return false; 
      } 
    } catch (Exception ignored) {
      return false;
    } 
    return true;
  }
  
  public void placeCrystalOnBlock(ArrayList<BlockPos> finalcheck, EntityOtherPlayerMP playerMP) {
    for (BlockPos pos : finalcheck) {
      EnumFacing facing;
      RayTraceResult result = mc.world.rayTraceBlocks(mc.player.getPositionEyes(1.0F), new Vec3d(pos.getX() + 0.5D, pos.getY() - 0.5D, pos.getZ() + 0.5D));
      if (result == null || result.sideHit == null) {
        facing = EnumFacing.UP;
      } else {
        facing = result.sideHit;
      } 
      if (mc.player.posY + mc.player.eyeHeight < pos.z && facing == EnumFacing.UP)
        continue; 
      assert result != null;
      if (result.getBlockPos().distanceSq((Vec3i)pos) > 2.0D)
        continue; 
      if (mc.player.eyeHeight + mc.player.posY < pos.y && facing.equals(EnumFacing.UP))
        continue; 
      this.placeTimer.setLastMS();
      if (!mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL) && this.Hand.getValString().equalsIgnoreCase("Mainhand") && this.FastSwitch.getValBoolean() && 
        find_in_hotbar(Items.END_CRYSTAL) != -1) {
        mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(find_in_hotbar(Items.END_CRYSTAL)));
        mc.player.inventory.currentItem = find_in_hotbar(Items.END_CRYSTAL);
      } 
      TryJiggle(pos);
      EnumHand hand = null;
      if (mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL) && (this.Hand.getValString().equalsIgnoreCase("Offhand") || this.Hand
        .getValString().equalsIgnoreCase("Either")))
        hand = EnumHand.OFF_HAND; 
      if (mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL) && (this.Hand.getValString().equalsIgnoreCase("Mainhand") || this.Hand
        .getValString().equalsIgnoreCase("Either")))
        hand = EnumHand.MAIN_HAND; 
      if (hand != null) {
        if (this.SpoofAngle.getValBoolean()) {
          this.Rots = Utils.getNeededRotations(new Vec3d(pos.x + 0.5D, pos.y + ((facing == EnumFacing.UP) ? 1.0D : 0.5D), pos.z + 0.5D), 0.0F, 0.0F);
          mc.player.rotationYaw = this.Rots[0];
          mc.player.rotationPitch = this.Rots[1];
        } 
        mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(pos, facing, hand, 0.5F, 0.5F, 0.5F));
        this.placeLocations.add(new PlaceLocation(pos.x, pos.y, pos.z, Calculate_Damage(pos.x + 0.5D, (pos.y + 1), pos.z + 0.5D, (Entity)playerMP)));
        break;
      } 
    } 
  }
  
  private void TryJiggle(BlockPos pos) {
    if (this.HoleJiggle.getValBoolean() && 
      mc.player.getDistance(pos.x, pos.y, pos.z) > 5.0D)
      Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(Math.floor(mc.player.posX) + 0.5D + ((mc.player.posX < pos.x) ? 0.2D : -0.2D), mc.player.posY, Math.floor(mc.player.posZ) + 0.5D + ((mc.player.posZ < pos.z) ? 0.2D : -0.2D), mc.player.onGround)); 
  }
  
  private int find_in_hotbar(Item item) {
    for (int i = 0; i < 9; i++) {
      ItemStack stack = mc.player.inventory.getStackInSlot(i);
      if (stack != ItemStack.EMPTY && stack.getItem().equals(item))
        return i; 
    } 
    return -1;
  }
  
  static class PlaceLocation extends Vec3i {
    double damage;
    
    boolean PacketConfirmed;
    
    double Timeset;
    
    private PlaceLocation(double xIn, double yIn, double zIn, double v) {
      super(xIn, yIn, zIn);
      this.damage = v;
    }
  }
  
  public void processAttack(EntityEnderCrystal entity) {
    if (this.Packetreach.getValBoolean()) {
      double posX = entity.posX - 3.5D * Math.cos(Math.toRadians((Utils.getYaw((Entity)entity) + 90.0F)));
      double posZ = entity.posZ - 3.5D * Math.sin(Math.toRadians((Utils.getYaw((Entity)entity) + 90.0F)));
      Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.PositionRotation(posX, entity.posY, posZ, Utils.getYaw((Entity)entity), Utils.getPitch((Entity)entity), mc.player.onGround));
      Wrapper.INSTANCE.sendPacket((Packet)new CPacketUseEntity((Entity)entity));
      Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.onGround));
    } 
    if (this.attackTimer.isDelay((long)(this.HitDelayBetween.getValDouble() * 1000.0D))) {
      this.attackTimer.setLastMS();
      TryJiggle(entity.getPosition());
      if (this.SpoofAngle.getValBoolean()) {
        this.Rots = Utils.getNeededRotations(entity.getPositionVector().add(0.0D, 0.8D, 0.0D), 0.0F, 0.0F);
        mc.player.rotationYaw = this.Rots[0];
        mc.player.rotationPitch = this.Rots[1];
      } 
      if (this.SwordHit.getValBoolean() && find_in_hotbar(Items.DIAMOND_SWORD) != -1 && mc.player.isPotionActive(MobEffects.WEAKNESS)) {
        mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(find_in_hotbar(Items.DIAMOND_SWORD)));
        mc.player.inventory.currentItem = find_in_hotbar(Items.DIAMOND_SWORD);
      } 
      if (!this.SwordHit.getValBoolean() || mc.player.getHeldItemMainhand().getItem().equals(Items.DIAMOND_SWORD)) {
        Wrapper.INSTANCE.attack((Entity)entity);
        mc.player.swingArm(EnumHand.MAIN_HAND);
      } 
    } 
  }
  
  private static float get_damage_multiplied(float damage) {
    int diff = mc.world.getDifficulty().getId();
    return damage * ((diff == 0) ? 0.0F : ((diff == 2) ? 1.0F : ((diff == 1) ? 0.5F : 1.5F)));
  }
  
  public static float Calculate_Damage(double posX, double posY, double posZ, Entity entity) {
    double distancedsize = entity.getDistance(posX, posY, posZ) / 12.0D;
    double blockDensity = entity.world.getBlockDensity(new Vec3d(posX, posY, posZ), entity.getEntityBoundingBox());
    double v = (1.0D - distancedsize) * blockDensity;
    float damage = (int)((v * v + v) / 2.0D * 7.0D * 12.0D + 1.0D);
    if (entity instanceof EntityLivingBase)
      return get_blast_reduction((EntityLivingBase)entity, get_damage_multiplied(damage), new Explosion((World)mc.world, null, posX, posY, posZ, 6.0F, false, true)); 
    return 1.0F;
  }
  
  public static float get_blast_reduction(EntityLivingBase entity, float damage, Explosion explosion) {
    if (entity instanceof EntityPlayer) {
      EntityPlayer ep = (EntityPlayer)entity;
      damage = CombatRules.getDamageAfterAbsorb(damage, ep.getTotalArmorValue(), (float)ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
      int k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), DamageSource.causeExplosionDamage(explosion));
      damage *= 1.0F - MathHelper.clamp(k, 0.0F, 20.0F) / 25.0F;
      if (entity.isPotionActive(Objects.<Potion>requireNonNull(MobEffects.RESISTANCE)))
        damage -= damage / 4.0F; 
      return Math.max(damage, 0.0F);
    } 
    return CombatRules.getDamageAfterAbsorb(damage, entity.getTotalArmorValue(), (float)entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
  }
}
