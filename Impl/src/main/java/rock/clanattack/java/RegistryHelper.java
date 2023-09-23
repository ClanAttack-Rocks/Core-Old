package rock.clanattack.java;

import rocks.clanattack.entry.Registry;
import rocks.clanattack.entry.RegistryKt;

public class RegistryHelper {

    public static void setRegistry(Registry registry) throws NoSuchFieldException, IllegalAccessException {
        var field = RegistryKt.class.getDeclaredField("registry");
        field.setAccessible(true);
        field.set(null, registry);
    }

}
