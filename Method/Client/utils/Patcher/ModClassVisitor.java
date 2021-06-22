package Method.Client.utils.Patcher;

import java.util.ArrayList;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public abstract class ModClassVisitor extends ClassVisitor {
  private final ArrayList<MethodVisitorRegistryEntry> methodVisitorRegistry = new ArrayList<>();
  
  public ModClassVisitor(ClassVisitor cv) {
    super(262144, cv);
  }
  
  public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
    MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
    for (MethodVisitorRegistryEntry entry : this.methodVisitorRegistry) {
      if (name.equals(entry.name) && desc.equals(entry.desc))
        return entry.factory.createMethodVisitor(mv); 
    } 
    return mv;
  }
  
  protected String unmap(String typeName) {
    return FMLDeobfuscatingRemapper.INSTANCE.unmap(typeName);
  }
  
  protected void registerMethodVisitor(String name, String desc, MethodVisitorFactory factory) {
    this.methodVisitorRegistry.add(new MethodVisitorRegistryEntry(name, desc, factory));
  }
  
  private static final class MethodVisitorRegistryEntry {
    private final String name;
    
    private final String desc;
    
    private final ModClassVisitor.MethodVisitorFactory factory;
    
    public MethodVisitorRegistryEntry(String name, String desc, ModClassVisitor.MethodVisitorFactory factory) {
      this.name = name;
      this.desc = desc;
      this.factory = factory;
    }
  }
  
  public static interface MethodVisitorFactory {
    MethodVisitor createMethodVisitor(MethodVisitor param1MethodVisitor);
  }
}
