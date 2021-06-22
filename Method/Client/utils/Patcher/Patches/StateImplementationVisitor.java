package Method.Client.utils.Patcher.Patches;

import Method.Client.utils.Patcher.Events.GetAmbientOcclusionLightValueEvent;
import Method.Client.utils.Patcher.Events.ShouldSideBeRenderedEvent;
import Method.Client.utils.Patcher.ModClassVisitor;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public final class StateImplementationVisitor extends ModClassVisitor {
  public StateImplementationVisitor(ClassVisitor cv, boolean obf) {
    super(cv);
    String iBlockAccess = unmap("net/minecraft/world/IBlockAccess");
    String blockPos = unmap("net/minecraft/util/math/BlockPos");
    String enumFacing = unmap("net/minecraft/util/EnumFacing");
    String getAmbientOcclusionLightValue_name = obf ? "j" : "getAmbientOcclusionLightValue";
    String getAmbientOcclusionLightValue_desc = "()F";
    String shouldSideBeRendered_name = obf ? "c" : "shouldSideBeRendered";
    String shouldSideBeRendered_desc = "(L" + iBlockAccess + ";L" + blockPos + ";L" + enumFacing + ";)Z";
    registerMethodVisitor(getAmbientOcclusionLightValue_name, getAmbientOcclusionLightValue_desc, GetAmbientOcclusionLightValueVisitor::new);
    registerMethodVisitor(shouldSideBeRendered_name, shouldSideBeRendered_desc, ShouldSideBeRenderedVisitor::new);
  }
  
  public static class GetAmbientOcclusionLightValueVisitor extends MethodVisitor {
    public GetAmbientOcclusionLightValueVisitor(MethodVisitor mv) {
      super(262144, mv);
    }
    
    public void visitInsn(int opcode) {
      if (opcode == 174) {
        System.out.println("StateImplementationVisitor.GetAmbientOcclusionLightValueVisitor.visitInsn()");
        this.mv.visitVarInsn(25, 0);
        this.mv.visitMethodInsn(184, Type.getInternalName(getClass()), "getAmbientOcclusionLightValue", "(FLnet/minecraft/block/state/IBlockState;)F", false);
      } 
      super.visitInsn(opcode);
    }
    
    public static float getAmbientOcclusionLightValue(float f, IBlockState state) {
      GetAmbientOcclusionLightValueEvent event = new GetAmbientOcclusionLightValueEvent(state, f);
      MinecraftForge.EVENT_BUS.post((Event)event);
      return event.getLightValue();
    }
  }
  
  public static class ShouldSideBeRenderedVisitor extends MethodVisitor {
    public ShouldSideBeRenderedVisitor(MethodVisitor mv) {
      super(262144, mv);
    }
    
    public void visitInsn(int opcode) {
      if (opcode == 172) {
        System.out.println("StateImplementationVisitor.ShouldSideBeRenderedVisitor.visitInsn()");
        this.mv.visitVarInsn(25, 0);
        this.mv.visitMethodInsn(184, Type.getInternalName(getClass()), "shouldSideBeRendered", "(ZLnet/minecraft/block/state/IBlockState;)Z", false);
      } 
      super.visitInsn(opcode);
    }
    
    public static boolean shouldSideBeRendered(boolean b, IBlockState state) {
      ShouldSideBeRenderedEvent event = new ShouldSideBeRenderedEvent(state, b);
      MinecraftForge.EVENT_BUS.post((Event)event);
      return event.isRendered();
    }
  }
}
