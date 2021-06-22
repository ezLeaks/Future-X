package Method.Client.module.movement;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Patcher.Events.PlayerMoveEvent;
import Method.Client.utils.TimerUtils;
import Method.Client.utils.Utils;
import Method.Client.utils.system.Connection;
import Method.Client.utils.system.Wrapper;
import Method.Client.utils.visual.ChatUtils;
import java.util.Objects;
import net.minecraft.entity.Entity;
import net.minecraft.init.MobEffects;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class LongJump extends Module {
  private int airTicks;
  
  private int groundTicks;
  
  Setting mode = Main.setmgr.add(new Setting("Jump mode", this, "Vanilla", new String[] { 
          "Vanilla", "AAC", "Damage", "Long", "Legit", "Quack", "AAC4", "Mineplex", "Hypixel", "NeruxVace", 
          "NeruxVace2" }));
  
  Setting boostval = Main.setmgr.add(new Setting("boostval", this, 1.0D, 0.0D, 3.0D, false, this.mode, "Long", 3));
  
  Setting Lagback = Main.setmgr.add(new Setting("Lagback", this, true));
  
  private double moveSpeed;
  
  private double lastDist;
  
  private int level;
  
  private final TimerUtils timer = new TimerUtils();
  
  private final TimerUtils aac = new TimerUtils();
  
  int delay2 = 0;
  
  double y = 0.0D;
  
  double speed = 0.0D;
  
  boolean teleported = false;
  
  private float air;
  
  private int stage;
  
  private boolean jump;
  
  public LongJump() {
    super("LongJump", 0, Category.MOVEMENT, "Jump Far");
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (packet instanceof SPacketPlayerPosLook) {
      SPacketPlayerPosLook pac = (SPacketPlayerPosLook)packet;
      if (this.Lagback.getValBoolean()) {
        ChatUtils.warning("Lagback checks!");
        mc.player.onGround = false;
        mc.player.motionX *= 0.0D;
        mc.player.motionZ *= 0.0D;
        mc.player.jumpMovementFactor = 0.0F;
        toggle();
      } else if (this.timer.hasReached(300.0F)) {
        pac.yaw = mc.player.rotationYaw;
        pac.pitch = mc.player.rotationPitch;
      } 
      this.timer.reset();
    } 
    return true;
  }
  
  public void onDisable() {
    if (mc.player != null)
      this.moveSpeed = getBaseMoveSpeed(); 
    this.lastDist = 0.0D;
    assert mc.player != null;
    mc.player.speedInAir = 0.02F;
    if (this.mode.getValString().equalsIgnoreCase("NeruxVace2"))
      setMotion(0.2D); 
    if (this.mode.getValString().equalsIgnoreCase("NeruxVace")) {
      this.teleported = false;
      setMotion(0.22D);
    } 
    this.speed = 0.0D;
    this.delay2 = 0;
    mc.timer.tickLength = 50.0F;
  }
  
  public void onEnable() {
    this.teleported = false;
    this.groundTicks = -5;
    if (mc.player == null || mc.world == null)
      return; 
    this.level = 0;
    this.lastDist = 0.0D;
    if (this.mode.getValString().equalsIgnoreCase("Hypixel")) {
      setMotion(0.15D);
      this.speed = 0.4D;
      this.y = 0.02D;
    } 
  }
  
  public static void toFwd(double speed) {
    float f = mc.player.rotationYaw * 0.017453292F;
    mc.player.motionX -= MathHelper.sin(f) * speed;
    mc.player.motionZ += MathHelper.cos(f) * speed;
  }
  
  public void onPlayerTick(TickEvent.PlayerTickEvent event) {
    double xDist = mc.player.posX - mc.player.prevPosX;
    double zDist = mc.player.posZ - mc.player.prevPosZ;
    this.lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
    if (this.mode.getValString().equalsIgnoreCase("AAC")) {
      mc.gameSettings.keyBindForward.pressed = false;
      if (mc.player.onGround)
        this.jump = true; 
      if (mc.player.onGround && this.aac.isDelay(500L)) {
        mc.player.motionY = 0.42D;
        toFwd(2.3D);
        this.timer.setLastMS();
      } else if (!mc.player.onGround && this.jump) {
        mc.player.motionX = mc.player.motionZ = 0.0D;
        this.jump = false;
      } 
    } 
    if (this.mode.getValString().equalsIgnoreCase("Quack")) {
      boolean moving = (mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown());
      if (!moving)
        return; 
      double forward = mc.player.movementInput.moveForward;
      float yaw = mc.player.rotationYaw;
      if (forward != 0.0D) {
        if (forward > 0.0D) {
          forward = 1.0D;
        } else if (forward < 0.0D) {
          forward = -1.0D;
        } 
      } else {
        forward = 0.0D;
      } 
      float[] motion = { 
          0.4206065F, 0.4179245F, 0.41525924F, 0.41261F, 0.409978F, 0.407361F, 0.404761F, 0.402178F, 0.399611F, 0.39706F, 
          0.394525F, 0.392F, 0.3894F, 0.38644F, 0.383655F, 0.381105F, 0.37867F, 0.37625F, 0.37384F, 0.37145F, 
          0.369F, 0.3666F, 0.3642F, 0.3618F, 0.35945F, 0.357F, 0.354F, 0.351F, 0.348F, 0.345F, 
          0.342F, 0.339F, 0.336F, 0.333F, 0.33F, 0.327F, 0.324F, 0.321F, 0.318F, 0.315F, 
          0.312F, 0.309F, 0.307F, 0.305F, 0.303F, 0.3F, 0.297F, 0.295F, 0.293F, 0.291F, 
          0.289F, 0.287F, 0.285F, 0.283F, 0.281F, 0.279F, 0.277F, 0.275F, 0.273F, 0.271F, 
          0.269F, 0.267F, 0.265F, 0.263F, 0.261F, 0.259F, 0.257F, 0.255F, 0.253F, 0.251F, 
          0.249F, 0.247F, 0.245F, 0.243F, 0.241F, 0.239F, 0.237F };
      float[] glide = { 0.3425F, 0.5445F, 0.65425F, 0.685F, 0.675F, 0.2F, 0.895F, 0.719F, 0.76F };
      double cos = Math.cos(Math.toRadians((yaw + 90.0F)));
      double sin = Math.sin(Math.toRadians((yaw + 90.0F)));
      if (!mc.player.collidedVertically && !mc.player.onGround) {
        this.airTicks++;
        this.groundTicks = -5;
        if (this.airTicks - 6 >= 0 && this.airTicks - 6 < glide.length)
          mc.player.motionY *= glide[this.airTicks - 6]; 
        if (mc.player.motionY < -0.2D && mc.player.motionY > -0.24D) {
          mc.player.motionY *= 0.7D;
        } else if (mc.player.motionY < -0.25D && mc.player.motionY > -0.32D) {
          mc.player.motionY *= 0.8D;
        } else if (mc.player.motionY < -0.35D && mc.player.motionY > -0.8D) {
          mc.player.motionY *= 0.98D;
        } 
        if (this.airTicks - 1 >= 0 && this.airTicks - 1 < motion.length) {
          mc.player.motionX = forward * motion[this.airTicks - 1] * 3.0D * cos;
          mc.player.motionZ = forward * motion[this.airTicks - 1] * 3.0D * sin;
        } else {
          mc.player.motionX = 0.0D;
          mc.player.motionZ = 0.0D;
        } 
      } else {
        this.airTicks = 0;
        this.groundTicks++;
        if (this.groundTicks <= 2) {
          mc.player.motionX = forward * 0.009999999776482582D * cos;
          mc.player.motionZ = forward * 0.009999999776482582D * sin;
        } else {
          mc.player.motionX = forward * 0.30000001192092896D * cos;
          mc.player.motionZ = forward * 0.30000001192092896D * sin;
          mc.player.motionY = 0.42399999499320984D;
        } 
      } 
    } 
    if (this.mode.getValString().equalsIgnoreCase("Damage")) {
      if (mc.player.onGround) {
        Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.300001D, mc.player.posZ, false));
        Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
        Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, true));
      } 
      if (mc.player.hurtTime > 0) {
        Movemulti(5.0D);
      } else {
        Movemulti(0.0D);
      } 
    } 
    if (this.mode.getValString().equalsIgnoreCase("Vanilla") && (
      mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindBack
      .isKeyDown()) && mc.gameSettings.keyBindJump.isKeyDown()) {
      float dir = mc.player.rotationYaw + ((mc.player.moveForward < 0.0F) ? '´' : false) + ((mc.player.moveStrafing > 0.0F) ? (-90.0F * ((mc.player.moveForward < 0.0F) ? -0.5F : ((mc.player.moveForward > 0.0F) ? 0.4F : 1.0F))) : 0.0F);
      float xDir = (float)Math.cos((dir + 90.0F) * Math.PI / 180.0D);
      float zDir = (float)Math.sin((dir + 90.0F) * Math.PI / 180.0D);
      if (mc.player.collidedVertically && (mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight
        .isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown()) && mc.gameSettings.keyBindJump.isKeyDown()) {
        mc.player.motionX = (xDir * 0.29F);
        mc.player.motionZ = (zDir * 0.29F);
      } 
      if (mc.player.motionY == 0.33319999363422365D && (mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight
        .isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown())) {
        mc.player.motionX = xDir * 1.261D;
        mc.player.motionZ = zDir * 1.261D;
      } 
    } 
  }
  
  public void onPlayerMove(PlayerMoveEvent event) {
    if (mc.player == null)
      return; 
    if (mc.player.isInWater())
      return; 
    if (this.mode.getValString().equalsIgnoreCase("NeruxVace2")) {
      mc.timer.tickLength = 30.0F;
      if (mc.player.onGround) {
        mc.player.jump();
        mc.player.motionY = 1.0D;
        setMotion(9.5D);
      } 
    } 
    if (this.mode.getValString().equalsIgnoreCase("NeruxVace")) {
      mc.player.speedInAir = 0.025F;
      mc.player.motionX *= 1.08D;
      mc.player.motionZ *= 1.08D;
      if (mc.player.onGround)
        mc.player.jump(); 
      mc.player.motionY += 0.072D;
    } 
    if (this.mode.getValString().equalsIgnoreCase("Hypixel")) {
      boolean var1 = false;
      if (this.y > 0.0D)
        this.y *= 0.9D; 
      float var7 = mc.player.fallDistance;
      mc.player.setPosition(mc.player.posX * 1.0D, mc.player.posY + 0.035423123132D, mc.player.posZ * 1.0D);
      float var2 = 0.7F + getSpeedEffect() * 0.45F;
      if ((mc.player.moveForward != 0.0F || mc.player.moveStrafing != 0.0F) && mc.player.onGround) {
        setMotion(0.15D);
        mc.player.jump();
        this.stage = 1;
      } 
      if (mc.player.onGround) {
        this.air = 0.0F;
      } else {
        if (mc.player.collidedVertically)
          this.stage = 0; 
        double var3 = 0.95D + getSpeedEffect() * 0.2D - (this.air / 25.0F);
        if (var3 < defaultSpeed() - 0.05D)
          var3 = defaultSpeed() - 0.05D; 
        setMotion(var3 * 0.75D);
        if (this.stage > 0)
          this.stage |= 0x1; 
        this.air += var2;
      } 
    } 
    if (this.mode.getValString().equalsIgnoreCase("AAC4")) {
      if (mc.player.onGround) {
        mc.player.jump();
        mc.player.motionY += 0.2D;
        this.speed = 0.5972999999999999D;
      } else {
        mc.player.motionY += 0.03D;
        this.speed *= 0.99D;
      } 
      setMotion(this.speed);
      if (!mc.player.onGround)
        this.delay2 |= 0x1; 
    } 
    if (this.mode.getValString().equalsIgnoreCase("Mineplex")) {
      if (mc.player.onGround) {
        mc.player.jump();
        mc.player.motionY += 0.1D;
        this.speed = 0.65D;
      } else {
        mc.player.motionY += 0.03D;
        this.speed *= 0.992D;
      } 
      if (!mc.gameSettings.keyBindLeft.pressed && !mc.gameSettings.keyBindRight.pressed) {
        setMotion(this.speed);
      } else {
        setMotion(this.speed * 0.7D);
      } 
      if (mc.player.onGround)
        setMotion(0.0D); 
    } 
    if (this.mode.getValString().equalsIgnoreCase("legit"))
      if (Utils.isMoving((Entity)mc.player)) {
        if (mc.player.onGround)
          mc.player.motionY = 0.41D; 
      } else {
        mc.player.motionX = 0.0D;
        mc.player.motionZ = 0.0D;
      }  
    if (this.mode.getValString().equalsIgnoreCase("long")) {
      double forward = mc.player.movementInput.moveForward;
      double strafe = mc.player.movementInput.moveStrafe;
      float yaw = mc.player.rotationYaw;
      if (forward == 0.0D && strafe == 0.0D) {
        mc.player.motionX = 0.0D;
        mc.player.motionZ = 0.0D;
      } 
      if (forward != 0.0D && strafe != 0.0D) {
        forward *= Math.sin(0.7853981633974483D);
        strafe *= Math.cos(0.7853981633974483D);
      } 
      if (this.level != 1 || (mc.player.moveForward == 0.0F && mc.player.moveStrafing == 0.0F)) {
        if (this.level == 2) {
          this.level++;
          double motionY = 0.40123128D;
          if ((mc.player.moveForward != 0.0F || mc.player.moveStrafing != 0.0F) && mc.player.onGround) {
            if (mc.player.isPotionActive(MobEffects.JUMP_BOOST))
              motionY += ((((PotionEffect)Objects.<PotionEffect>requireNonNull(mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST))).getAmplifier() + 1) * 0.1F); 
            mc.player.motionY = motionY;
            this.moveSpeed *= 2.149D;
          } 
        } else if (this.level == 3) {
          this.level++;
          double difference = 0.763D * (this.lastDist - getBaseMoveSpeed());
          this.moveSpeed = this.lastDist - difference;
        } else {
          if (mc.world.getCollisionBoxes((Entity)mc.player, mc.player.getEntityBoundingBox().offset(0.0D, mc.player.motionY, 0.0D)).size() > 0 || mc.player.collidedVertically)
            this.level = 1; 
          this.moveSpeed = this.lastDist - this.lastDist / 159.0D;
        } 
      } else {
        this.level = 2;
        double boost = mc.player.isPotionActive(MobEffects.SPEED) ? this.boostval.getValDouble() : (this.boostval.getValDouble() + 1.1D);
        this.moveSpeed = boost * getBaseMoveSpeed() - 0.01D;
      } 
      this.moveSpeed = Math.max(this.moveSpeed, getBaseMoveSpeed());
      double mx = -Math.sin(Math.toRadians(yaw));
      double mz = Math.cos(Math.toRadians(yaw));
      mc.player.motionX = forward * this.moveSpeed * mx + strafe * this.moveSpeed * mz;
      mc.player.motionZ = forward * this.moveSpeed * mz - strafe * this.moveSpeed * mx;
    } 
  }
  
  public static void Movemulti(double moveSpeed) {
    float forward = mc.player.moveForward;
    float strafe = mc.player.moveStrafing;
    float yaw = mc.player.rotationYaw;
    if (forward == 0.0D && strafe == 0.0D) {
      mc.player.motionX = 0.0D;
      mc.player.motionZ = 0.0D;
    } 
    int d = 45;
    if (forward != 0.0D) {
      if (strafe > 0.0D) {
        yaw += ((forward > 0.0D) ? -d : d);
      } else if (strafe < 0.0D) {
        yaw += ((forward > 0.0D) ? d : -d);
      } 
      strafe = 0.0F;
      if (forward > 0.0D) {
        forward = 1.0F;
      } else if (forward < 0.0D) {
        forward = -1.0F;
      } 
    } 
    double cos = Math.cos(Math.toRadians((yaw + 90.0F)));
    double sin = Math.sin(Math.toRadians((yaw + 90.0F)));
    double xDist = forward * moveSpeed * cos + strafe * moveSpeed * sin;
    double zDist = forward * moveSpeed * sin - strafe * moveSpeed * cos;
    mc.player.motionX = xDist;
    mc.player.motionZ = zDist;
  }
  
  public void setMotion(double var1) {
    double var3 = mc.player.movementInput.moveForward;
    double var5 = mc.player.movementInput.moveStrafe;
    float var7 = mc.player.rotationYaw;
    if (this.mode.getValString().equalsIgnoreCase("aac4")) {
      var5 = 0.0D;
      var7 = 0.0F;
    } 
    if (var3 == 0.0D && var5 == 0.0D) {
      mc.player.motionX = 0.0D;
      mc.player.motionZ = 0.0D;
    } else {
      if (var3 != 0.0D) {
        if (var5 > 0.0D) {
          var7 += ((var3 > 0.0D) ? -45 : 45);
        } else if (var5 < 0.0D) {
          var7 += ((var3 > 0.0D) ? 45 : -45);
        } 
        var5 = 0.0D;
        if (var3 > 0.0D) {
          var3 = 1.0D;
        } else if (var3 < 0.0D) {
          var3 = -1.0D;
        } 
      } 
      mc.player.motionX = var3 * var1 * Math.cos(Math.toRadians((var7 + 90.0F))) + var5 * var1 * Math.sin(Math.toRadians((var7 + 90.0F)));
      mc.player.motionZ = var3 * var1 * Math.sin(Math.toRadians((var7 + 90.0F))) - var5 * var1 * Math.cos(Math.toRadians((var7 + 90.0F)));
    } 
  }
  
  public int getSpeedEffect() {
    return mc.player.isPotionActive(MobEffects.SPEED) ? (((PotionEffect)Objects.<PotionEffect>requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED))).getAmplifier() | 0x1) : 0;
  }
  
  public double defaultSpeed() {
    double var1 = 0.2873D;
    if (mc.player.isPotionActive(MobEffects.SPEED)) {
      int var3 = ((PotionEffect)Objects.<PotionEffect>requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED))).getAmplifier();
      var1 *= 1.0D + 0.2D * (var3 | 0x1);
    } 
    return var1;
  }
  
  private double getBaseMoveSpeed() {
    double n = 0.2873D;
    if (mc.player.isPotionActive(MobEffects.SPEED))
      n *= 1.0D + 0.2D * (((PotionEffect)Objects.<PotionEffect>requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED))).getAmplifier() + 1); 
    return n;
  }
}
