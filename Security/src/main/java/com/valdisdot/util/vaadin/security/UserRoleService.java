package com.valdisdot.util.vaadin.security;

/**
 * Interface for managing role-based permissions for accessing, creating, updating, and deleting components.
 *
 * <p>This interface defines methods to check whether certain operations (access, create, update, delete)
 * are allowed based on the user's role and the component they are interacting with.</p>
 */
public interface UserRoleService {

    /**
     * Checks if access to the specified component is allowed for the given role.
     *
     * @param role                the role of the user
     * @param accessibleComponent the class of the component being accessed
     * @return {@code true} if access is allowed, {@code false} otherwise
     */
    boolean isAccessAllowed(String role, Class<? extends AccessibleContainer> accessibleComponent);

    /**
     * Checks if the create operation is allowed for the specified component and role.
     *
     * @param role                the role of the user
     * @param accessibleComponent the class of the component being accessed
     * @return {@code true} if the create operation is allowed, {@code false} otherwise
     */
    boolean isCreateOperationAllowed(String role, Class<? extends AccessibleContainer> accessibleComponent);

    /**
     * Checks if the update operation is allowed for the specified component and role.
     *
     * @param role                the role of the user
     * @param accessibleComponent the class of the component being accessed
     * @return {@code true} if the update operation is allowed, {@code false} otherwise
     */
    boolean isUpdateOperationAllowed(String role, Class<? extends AccessibleContainer> accessibleComponent);

    /**
     * Checks if the delete operation is allowed for the specified component and role.
     *
     * @param role                the role of the user
     * @param accessibleComponent the class of the component being accessed
     * @return {@code true} if the delete operation is allowed, {@code false} otherwise
     */
    boolean isDeleteOperationAllowed(String role, Class<? extends AccessibleContainer> accessibleComponent);

    /**
     * Updates the permission configurations.
     */
    void update();
}

