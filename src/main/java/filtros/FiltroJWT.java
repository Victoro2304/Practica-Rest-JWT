package filtros;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import entidades.Cliente;
import java.io.IOException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.Provider;

@Provider
public class FiltroJWT implements ContainerRequestFilter {

    private final String AUTH_HEADER = "Autorizacion";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String metodo = requestContext.getMethod();
        String path = requestContext.getUriInfo().getPath();
        if (path.contains("login") && metodo.equals("POST")) {
            Cliente user = (Cliente)requestContext.getProperty("user");
            crearToken(user.getNombre());
        } else {
            String token = requestContext.getHeaderString(AUTH_HEADER);
            if (token != null) {
                //verificar token
            } else {
                throw new WebApplicationException(Status.UNAUTHORIZED);
            }
        }
    }

    private String crearToken(String user) {
        String token = null;
        try {
            Algorithm algorithm = Algorithm.HMAC256("toribio");
            token = JWT.create()
                    .withIssuer("auth0")
                    .withClaim("user", user)
                    .sign(algorithm);
        } catch (JWTCreationException e) {
        }
        return token;
    }

    private void verificarToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256("toribio");
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("auth0")
                    .build();
        } catch (JWTCreationException e) {
            
        }
    }
}
