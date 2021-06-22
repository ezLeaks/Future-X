package Method.Client.module.combat;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

class Hole extends Vec3i {
  private final boolean tall;
  
  private HoleTypes HoleType;
  
  public enum HoleTypes {
    None, Obsidian, Bedrock, Void, Burrow;
  }
  
  public Hole(int x, int y, int z, BlockPos pos, HoleTypes p_Type, boolean tall) {
    super(x, y, z);
    this.tall = tall;
    SetHoleType(p_Type);
  }
  
  public boolean isTall() {
    return this.tall;
  }
  
  public HoleTypes GetHoleType() {
    return this.HoleType;
  }
  
  public void SetHoleType(HoleTypes holeType) {
    this.HoleType = holeType;
  }
}
