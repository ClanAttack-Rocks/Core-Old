package rocks.clanattack.impl.java;

import rocks.clanattack.entry.Registry;
import rocks.clanattack.entry.RegistryKt;
import rocks.clanattack.entry.service.ServiceImplementation;

import java.lang.reflect.Field;

public class Reflection {

    public static void setRegistry(Registry registry) throws NoSuchFieldException, IllegalAccessException {
        Field field = RegistryKt.class.getDeclaredField("registry");
        field.setAccessible(true);
        field.set(null, registry);
        field.setAccessible(false);
    }

    public static void setServiceState(ServiceImplementation service, Boolean enabled) throws NoSuchFieldException, IllegalAccessException {
        Field field = ServiceImplementation.class.getDeclaredField("enabled");

        field.setAccessible(true);
        field.set(service, enabled);
        field.setAccessible(false);
    }

}
