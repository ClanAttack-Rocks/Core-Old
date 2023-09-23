package rock.clanattack.java;

import rocks.clanattack.entry.service.ServiceImplementation;

public class ServiceHelper {

    public static void setEnabled(ServiceImplementation implementation, Boolean enabled) throws NoSuchFieldException, IllegalAccessException {
        var field = ServiceImplementation.class.getDeclaredField("enabled");
        field.setAccessible(true);
        field.set(implementation, enabled);
    }

}
