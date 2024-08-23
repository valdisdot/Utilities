package com.valdisdot.util.vaadin.security;

import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Optional;

/**
 * Service class for managing the current user's authentication and authorization.
 * Provides methods to check access permissions and retrieve user details.
 */
public class CurrentUserService {
    private final AuthenticationContext authenticationContext;
    private final UserRoleService userRoleService;

    /**
     * Constructs a CurrentUserService with the given authentication context and user role service.
     *
     * @param authenticationContext the authentication context
     * @param userRoleService       the user role service
     */
    public CurrentUserService(AuthenticationContext authenticationContext, UserRoleService userRoleService) {
        this.authenticationContext = authenticationContext;
        this.userRoleService = userRoleService;
    }

    /**
     * Retrieves the current authenticated user.
     *
     * @return the current authenticated user details
     * @throws AccessDeniedException if the user is not authenticated
     */
    private UserDetails getCurrentUser() {
        Optional<UserDetails> authenticatedUser = authenticationContext.getAuthenticatedUser(UserDetails.class);
        if (authenticatedUser.isEmpty()) throw new AccessDeniedException("Forbidden for anonymous");
        else return authenticatedUser.orElseThrow();
    }

    /**
     * Retrieves the roles of the current authenticated user.
     *
     * @return a collection of the current user's roles
     */
    private Collection<String> getCurrentUserRoles() {
        return getCurrentUser().getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .filter(s -> s.startsWith("ROLE_"))
                .map(s -> s.replaceFirst("ROLE_", ""))
                .toList();
    }

    /**
     * Checks if the current user has access to the specified accessible component.
     *
     * @param AccessibleContainer the accessible component class
     * @return true if access is allowed, false otherwise
     */
    public boolean isAccessAllowed(Class<? extends AccessibleContainer> AccessibleContainer) {
        for (String role : getCurrentUserRoles()) {
            if (userRoleService.isAccessAllowed(role, AccessibleContainer)) return true;
        }
        return false;
    }

    /**
     * Checks if the current user is allowed to create the specified accessible component.
     *
     * @param AccessibleContainer the accessible component class
     * @return true if create operation is allowed, false otherwise
     */
    public boolean isCreateOperationAllowed(Class<? extends AccessibleContainer> AccessibleContainer) {
        for (String role : getCurrentUserRoles()) {
            if (userRoleService.isCreateOperationAllowed(role, AccessibleContainer)) return true;
        }
        return false;
    }

    /**
     * Checks if the current user is allowed to update the specified accessible component.
     *
     * @param AccessibleContainer the accessible component class
     * @return true if update operation is allowed, false otherwise
     */
    public boolean isUpdateOperationAllowed(Class<? extends AccessibleContainer> AccessibleContainer) {
        for (String role : getCurrentUserRoles()) {
            if (userRoleService.isUpdateOperationAllowed(role, AccessibleContainer)) return true;
        }
        return false;
    }

    /**
     * Checks if the current user is allowed to delete the specified accessible component.
     *
     * @param AccessibleContainer the accessible component class
     * @return true if delete operation is allowed, false otherwise
     */
    public boolean isDeleteOperationAllowed(Class<? extends AccessibleContainer> AccessibleContainer) {
        for (String role : getCurrentUserRoles()) {
            if (userRoleService.isDeleteOperationAllowed(role, AccessibleContainer)) return true;
        }
        return false;
    }

    /**
     * Retrieves the username of the current authenticated user.
     *
     * @return the current user's username
     */
    public String getUserName() {
        return getCurrentUser().getUsername();
    }

    /**
     * Logs out the current user.
     */
    public void logout() {
        authenticationContext.logout();
    }
}

