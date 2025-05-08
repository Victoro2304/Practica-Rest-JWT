
package Application;

import jakarta.ws.rs.core.Application;
import java.util.Set;

/**
 *
 * @author victo
 */
@jakarta.ws.rs.ApplicationPath("webresources")
public class ClienteApplicationConfig extends Application{
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(filtros.FiltroJWT.class);
        resources.add(resources.ClienteResource.class);
    }
}
