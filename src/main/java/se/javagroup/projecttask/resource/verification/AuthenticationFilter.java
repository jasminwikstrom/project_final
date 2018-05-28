package se.javagroup.projecttask.resource.verification;

import org.springframework.stereotype.Component;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

import static java.util.Collections.singletonMap;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static javax.ws.rs.core.Response.status;


@Component
@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        String authorizationHeader
                = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (!"Jasse".equals(authorizationHeader)) {
            requestContext.abortWith(status(UNAUTHORIZED)
                    .entity(singletonMap("error", "Missing/Invalid api key"))
                    .build());
        }

    }
}