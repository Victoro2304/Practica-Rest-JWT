package resources;

import entidades.Cliente;
import filtros.FiltroJWT;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 *
 * @author victo
 */
@Path("clientes")
@RequestScoped
public class ClienteResource {

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(Cliente credenciales) {
        Cliente clienteRegistrado = FiltroJWT.buscarCliente(credenciales.getUser());
        if (clienteRegistrado != null
                && clienteRegistrado.getContrasena().equals(credenciales.getContrasena())) {
            String token = generarToken(clienteRegistrado.getUser());
            FiltroJWT.tokens.put(clienteRegistrado.getUser(), token);
            return Response.ok()
                    .header("Autorizacion", token)
                    .entity("Login exitoso")
                    .build();
        } else {
            return Response.status(Status.UNAUTHORIZED)
                    .entity("Usuario o contraseña incorrectos")
                    .build();
        }
    }

    @POST
    @Path("/registro")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrar(Cliente nuevoCliente) {
        Cliente existente = FiltroJWT.buscarCliente(nuevoCliente.getUser());
        if (existente != null) {
            return Response.status(Status.CONFLICT)
                    .entity("El usuario ya existe")
                    .build();
        }
        int nuevoId = FiltroJWT.listarClientes().size() + 1;
        nuevoCliente.setId(nuevoId);
        FiltroJWT.agregarCliente(nuevoCliente);

        return Response.status(Status.CREATED)
                .entity("Usuario registrado con éxito")
                .build();
    }

    @GET
    @Path("/protected")
    @Produces(MediaType.APPLICATION_JSON)
    public Response protectedRoute(@HeaderParam("Authorization") String authorizationHeader) {
        Cliente clienteAutenticado = null;

        if (authorizationHeader != null && !authorizationHeader.trim().isEmpty()) {
            String token = authorizationHeader;
            try {
                String username = FiltroJWT.verificarToken(token);
                clienteAutenticado = FiltroJWT.buscarCliente(username);
            } catch (Exception e) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Token inválido o expirado: " + e.getMessage())
                        .build();
            }
        } else {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Acceso no autorizado. Token no proporcionado o formato incorrecto.")
                    .build();
        }

        if (clienteAutenticado == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("No se encontró usuario autenticado")
                    .build();
        }

        return Response.ok()
                .entity("Acceso autorizado para el usuario: " + clienteAutenticado.getUser()
                        + "\nInformación del usuario: " + clienteAutenticado.toString())
                .build();
    }

    @GET
    @Path("/logout")
    public Response logout(@HeaderParam("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && !authorizationHeader.trim().isEmpty()) {
            String token = authorizationHeader;
            try {
                String username = FiltroJWT.verificarToken(token);
                FiltroJWT.invalidarToken(username);
            } catch (Exception e) {
                return Response.status(Status.UNAUTHORIZED).entity("Invalid Token").build();
            }

            return Response.ok()
                    .entity("Sesión cerrada correctamente")
                    .build();
        } else {
            return Response.status(Status.BAD_REQUEST)
                    .entity("No hay sesión activa")
                    .build();
        }
    }

    private String generarToken(String username) {
        return FiltroJWT.crearToken(username);
    }
}
