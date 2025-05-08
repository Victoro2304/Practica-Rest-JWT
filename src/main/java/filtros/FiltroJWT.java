package filtros;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import entidades.Cliente;
import java.io.IOException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.Provider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Provider
public class FiltroJWT implements ContainerRequestFilter {

    private final String AUTH_HEADER = "Authorization";
    private static final String SECRET_KEY = "toribio";

    private static final Map<String, Cliente> clientes = new HashMap<>();
    public static final Map<String, String> tokens = new HashMap<>();

    static {
        clientes.put("admin", new Cliente(1, "admin", "admin123"));
        clientes.put("usuario1", new Cliente(2, "usuario1", "pass123"));
    }

    public static void agregarCliente(Cliente cliente) {
        clientes.put(cliente.getUser(), cliente);
    }

    public static Cliente buscarCliente(String nombre) {
        return clientes.get(nombre);
    }

    public static List<Cliente> listarClientes() {
        return new ArrayList<>(clientes.values());
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String metodo = requestContext.getMethod();
        String path = requestContext.getUriInfo().getPath();

        // Permitir acceso sin autenticación a login y registro
        if ((path.contains("login") || path.contains("registro")) && metodo.equals("POST")) {
            return;
        } else {

            String token = requestContext.getHeaderString(AUTH_HEADER);

            try {
                String username = verificarToken(token);
                if (!tokens.containsKey(username)) {
                    throw new WebApplicationException(Response.status(Status.UNAUTHORIZED)
                            .entity("Acceso no autorizado. Token no valido.")
                            .build());
                }
                Cliente cliente = clientes.get(username);
                if (cliente != null) {
                    return;
                }
            } catch (JWTVerificationException ex) {
                throw new WebApplicationException("Token inválido o expirado", ex);
            }
            throw new WebApplicationException(Response.status(Status.UNAUTHORIZED)
                    .entity("Acceso no autorizado. Token inválido o expirado.")
                    .build());
        }
    }

    public static String crearToken(String user) {
        String token = null;
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            token = JWT.create()
                    .withIssuer("auth0")
                    .withClaim("user", user)
                    .sign(algorithm);

        } catch (JWTCreationException ex) {
            throw new WebApplicationException("Error al crear el token", ex);
        }
        return token;
    }

    public static String verificarToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("auth0")
                    .build();

            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaim("user").asString();
        } catch (JWTVerificationException ex) {
            throw new WebApplicationException("Token inválido o expirado", ex);
        }
    }

    public static void invalidarToken(String username) {
        tokens.remove(username);
    }
}
