package Method.Client.utils;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.MoverType;
import net.minecraft.util.MovementInput;
import net.minecraft.world.World;

public class Entity301 extends EntityOtherPlayerMP {
  public Entity301(World worldIn, GameProfile gameProfileIn) {
    super(worldIn, gameProfileIn);
  }
  
  public void setMovementInput(MovementInput movementInput) {
    if (movementInput.jump && this.onGround)
      jump(); 
    moveRelative(movementInput.moveStrafe, this.moveVertical, movementInput.moveForward, this.movedDistance);
  }
  
  public void move(MoverType type, double x, double y, double z) {
    this.onGround = true;
    super.move(type, x, y, z);
    this.onGround = true;
  }
  
  public boolean isSneaking() {
    return false;
  }
  
  public void onLivingUpdate() {
    super.onLivingUpdate();
    this.noClip = true;
    this.motionX = 0.0D;
    this.motionY = 0.0D;
    this.motionZ = 0.0D;
    this.noClip = false;
  }
}
