package Method.Client.utils.system;

import com.google.common.reflect.TypeToken;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.MinecraftDummyContainer;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.ASMEventHandler;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class Nan0EventRegister {
  public static void register(EventBus bus, Object target) {
    ConcurrentHashMap<Object, ArrayList<IEventListener>> listeners = (ConcurrentHashMap<Object, ArrayList<IEventListener>>)ReflectionHelper.getPrivateValue(EventBus.class, bus, new String[] { "listeners" });
    Map<Object, ModContainer> listenerOwners = (Map<Object, ModContainer>)ReflectionHelper.getPrivateValue(EventBus.class, bus, new String[] { "listenerOwners" });
    if (listeners.containsKey(target))
      return; 
    MinecraftDummyContainer minecraftDummyContainer = Loader.instance().getMinecraftModContainer();
    listenerOwners.put(target, minecraftDummyContainer);
    ReflectionHelper.setPrivateValue(EventBus.class, bus, listenerOwners, new String[] { "listenerOwners" });
    Set<? extends Class<?>> supers = TypeToken.of(target.getClass()).getTypes().rawTypes();
    for (Method method : target.getClass().getMethods()) {
      for (Class<?> cls : supers) {
        try {
          Method real = cls.getDeclaredMethod(method.getName(), method.getParameterTypes());
          if (real.isAnnotationPresent((Class)SubscribeEvent.class)) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            Class<?> eventType = parameterTypes[0];
            try {
              int busID = ((Integer)ReflectionHelper.getPrivateValue(EventBus.class, bus, new String[] { "busID" })).intValue();
              Constructor<?> ctr = eventType.getConstructor(new Class[0]);
              ctr.setAccessible(true);
              Event event = (Event)ctr.newInstance(new Object[0]);
              ASMEventHandler listener = new ASMEventHandler(target, method, (ModContainer)minecraftDummyContainer);
              event.getListenerList().register(busID, listener.getPriority(), (IEventListener)listener);
              ArrayList<IEventListener> others = listeners.get(target);
              if (others == null) {
                others = new ArrayList<>();
                listeners.put(target, others);
                ReflectionHelper.setPrivateValue(EventBus.class, bus, listeners, new String[] { "listeners" });
              } 
              others.add(listener);
            } catch (Exception exception) {}
            break;
          } 
        } catch (NoSuchMethodException noSuchMethodException) {}
      } 
    } 
  }
}
