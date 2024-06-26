package ticket.adapters.rest;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.representations.AccessToken;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Service information.
 */
public class ServiceInfo {

    private final String name = "ticket-system";
    private final String version = "1.0.0";
    private final String status = "OK";
    private final KeycloakPrincipal<?> userPrincipal;

    public ServiceInfo(HttpServletRequest request) {
        this.userPrincipal = (KeycloakPrincipal<?>) request.getUserPrincipal();
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getStatus() {
        return status;
    }

    public String getUserInfo() {
        if (userPrincipal != null) {
            AccessToken token = userPrincipal.getKeycloakSecurityContext().getToken();
            return token.getPreferredUsername() + " " + token.getEmail();
        } else {
            return "<unauthenticated>";
        }
    }
}
