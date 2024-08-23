package com.valdisdot.util.vaadin.security;

import java.util.*;

/**
 * In-memory implementation of {@code UserRoleService}.
 * This service allows defining and managing role-based permissions for accessing, creating, updating, and deleting components.
 *
 * <p>Permission configurations can be set up using either {@link RolePermissionConfigurator} or {@link ContainerPermissionConfigurator},
 * which allow defining permissions based on roles or containers (components) respectively.</p>
 */
public class StaticUserRoleService implements UserRoleService {
    private final static Map<String, Collection<PermissionHolder>> permissionHolders = new HashMap<>();

    /**
     * Creates a new {@code RolePermissionConfigurator} instance for configuring role-based permissions.
     *
     * @return a new {@code RolePermissionConfigurator} instance
     */
    public static RolePermissionConfigurator roleConfigurator() {
        return new RolePermissionConfigurator();
    }

    /**
     * Creates a new {@code ContainerPermissionConfigurator} instance for configuring container-based permissions.
     *
     * @return a new {@code ContainerPermissionConfigurator} instance
     */
    public static ContainerPermissionConfigurator containerConfigurator(){
        return new ContainerPermissionConfigurator();
    }

    private static boolean isOperationAllowed(String role, Class<? extends AccessibleContainer> accessibleComponent, AllowedOperation allowedOperation) {
        return checkPermission(role, accessibleComponent, allowedOperation) ||
                checkPermission("ALL", accessibleComponent, allowedOperation);
    }

    private static boolean checkPermission(String role, Class<? extends AccessibleContainer> accessibleComponent, AllowedOperation allowedOperation) {
        return permissionHolders.containsKey(role) && permissionHolders.get(role)
                .stream()
                .filter(permissionHolder -> permissionHolder.isOperationAllowed(allowedOperation))
                .anyMatch(permissionHolder -> permissionHolder.accessibleComponent == accessibleComponent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAccessAllowed(String role, Class<? extends AccessibleContainer> accessibleComponent) {
        return permissionHolders.containsKey(role) && permissionHolders.get(role)
                .stream()
                .anyMatch(permissionHolder -> permissionHolder.accessibleComponent == accessibleComponent) ||
                permissionHolders.containsKey("ALL") && permissionHolders.get("ALL")
                        .stream()
                        .anyMatch(permissionHolder -> permissionHolder.accessibleComponent == accessibleComponent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCreateOperationAllowed(String role, Class<? extends AccessibleContainer> accessibleComponent) {
        return isOperationAllowed(role, accessibleComponent, AllowedOperation.CREATE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUpdateOperationAllowed(String role, Class<? extends AccessibleContainer> accessibleComponent) {
        return isOperationAllowed(role, accessibleComponent, AllowedOperation.UPDATE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDeleteOperationAllowed(String role, Class<? extends AccessibleContainer> accessibleComponent) {
        return isOperationAllowed(role, accessibleComponent, AllowedOperation.DELETE);
    }

    /**
     * For {@code StaticUserRoleService}, this method is not implemented.
     */
    @Override
    public void update() {
        // Not implemented
    }

    /**
     * Enum representing possible CRUD operations for a permission.
     *
     * <p>Read operation is implicitly defined, assuming that permission is granted by the presence of
     * {@code Class<? extends AccessibleContainer>} in the permission holder map by a role.</p>
     *
     * <p>Otherwise, if no READ permission is granted, CREATE, UPDATE, DELETE are not available.</p>
     */
    enum AllowedOperation {
        CREATE, UPDATE, DELETE
    }

    /**
     * Holds the permissions for a component. For internal use.
     */
    private static class PermissionHolder {
        Set<AllowedOperation> allowedOperations;
        Class<? extends AccessibleContainer> accessibleComponent;

        PermissionHolder(Class<? extends AccessibleContainer> accessibleComponent, Set<AllowedOperation> allowedOperations) {
            this.accessibleComponent = accessibleComponent;
            this.allowedOperations = allowedOperations;
        }

        boolean isOperationAllowed(AllowedOperation allowedOperation) {
            return allowedOperations.contains(allowedOperation);
        }
    }

    /**
     * Configurator for defining permissions based on roles.
     */
    public static class RolePermissionConfigurator {
        Set<String> roles;

        private RolePermissionConfigurator() {
        }

        /**
         * Sets permission for all users, ignoring the role.
         *
         * @return this {@code RolePermissionConfigurator} instance
         */
        public RolePermissionConfigurator forAll() {
            return forRoles("ALL");
        }

        /**
         * Sets multiple roles for the permission.
         *
         * @param roles the roles to set
         * @return this {@code RolePermissionConfigurator} instance
         */
        public RolePermissionConfigurator forRoles(String... roles) {
            if (roles != null) {
                this.roles = new HashSet<>();
                for (String role : roles) {
                    if (role != null && !role.isBlank()) {
                        this.roles.add(role);
                    }
                }
            }
            return this;
        }

        /**
         * Sets multiple roles for the permission.
         *
         * @param roles the roles to set
         * @return this {@code RolePermissionConfigurator} instance
         */
        public RolePermissionConfigurator forRoles(Collection<String> roles) {
            if (roles != null) {
                this.roles = new HashSet<>();
                for (String role : roles) {
                    if (role != null && !role.isBlank()) {
                        this.roles.add(role);
                    }
                }
            }
            return this;
        }

        /**
         * Endpoint to configure the access to the specified components.
         *
         * @param accessibleComponents the components to allow access to
         * @return a new {@code OperationConfigurator} instance
         */
        @SafeVarargs
        public final OperationConfigurator allowAccess(Class<? extends AccessibleContainer>... accessibleComponents) {
            OperationConfigurator operationConfigurator = new OperationConfigurator();
            if (accessibleComponents != null)
                for (Class<? extends AccessibleContainer> accessibleComponent : accessibleComponents)
                    if (accessibleComponent != null)
                        operationConfigurator.put(accessibleComponent);
            return operationConfigurator;
        }

        /**
         * Endpoint to configure the access to the specified components.
         *
         * @param accessibleComponents the components to allow access to
         * @return this {@code OperationConfigurator} instance
         */
        public OperationConfigurator allowAccess(Collection<Class<? extends AccessibleContainer>> accessibleComponents) {
            OperationConfigurator operationConfigurator = new OperationConfigurator();
            if (accessibleComponents != null)
                for (Class<? extends AccessibleContainer> accessibleComponent : accessibleComponents)
                    if (accessibleComponent != null)
                        operationConfigurator.put(accessibleComponent);
            return operationConfigurator;
        }

        /**
         * Returns a wrapper for accessing the user-defined roles, which implements {@code UserRoleService}.
         *
         * @return an instance of {@code UserRoleService} wrapping the configured permissions
         */
        public UserRoleService wrap() {
            return new StaticUserRoleService();
        }

        /**
         * Configurator for operations on the specified components.
         */
        public class OperationConfigurator {
            List<PermissionHolder> holders;

            private OperationConfigurator() {
                holders = new ArrayList<>();
            }

            private void put(Class<? extends AccessibleContainer> accessibleComponent) {
                this.holders.add(new PermissionHolder(accessibleComponent, EnumSet.noneOf(AllowedOperation.class)));
            }

            /**
             * Allows the create operations on the specified components.
             *
             * @return this {@code OperationConfigurator} instance
             */
            public OperationConfigurator allowCreateOperations() {
                for (PermissionHolder holder : holders) {
                    holder.allowedOperations.add(AllowedOperation.CREATE);
                }
                return this;
            }

            /**
             * Allows the update operations on the specified components.
             *
             * @return this {@code OperationConfigurator} instance
             */
            public OperationConfigurator allowUpdateOperations() {
                for (PermissionHolder holder : holders) {
                    holder.allowedOperations.add(AllowedOperation.UPDATE);
                }
                return this;
            }

            /**
             * Allows the delete operations on the specified components.
             *
             * @return this {@code OperationConfigurator} instance
             */
            public OperationConfigurator allowDeleteOperations() {
                for (PermissionHolder holder : holders) {
                    holder.allowedOperations.add(AllowedOperation.DELETE);
                }
                return this;
            }

            /**
             * Accepts and saves the permissions defined by this configurator.
             *
             * @return this {@code RolePermissionConfigurator} instance
             */
            public RolePermissionConfigurator accept() {
                if (holders != null && roles != null) {
                    for (String role : roles) {
                        permissionHolders.compute(role, (key, permissionHolderCollection) -> {
                            if (permissionHolderCollection == null) {
                                permissionHolderCollection = new LinkedList<>();
                            }
                            permissionHolderCollection.addAll(holders);
                            return permissionHolderCollection;
                        });
                    }
                }
                roles = null;
                return RolePermissionConfigurator.this;
            }

            /**
             * Allows all create, update, and delete operations on the specified components,
             * then accepts and saves the permissions defined by this configurator.
             *
             * @return this {@code RolePermissionConfigurator} instance
             */
            public RolePermissionConfigurator allowAllOperations() {
                for (PermissionHolder holder : holders) {
                    holder.allowedOperations.add(AllowedOperation.CREATE);
                    holder.allowedOperations.add(AllowedOperation.UPDATE);
                    holder.allowedOperations.add(AllowedOperation.DELETE);
                }
                return accept();
            }
        }
    }

    /**
     * Configurator for defining permissions based on containers.
     */
    public static class ContainerPermissionConfigurator {
        private Set<Class<? extends AccessibleContainer>> containers;

        /**
         * Configures permissions for the specified containers.
         *
         * @param containers the containers to configure permissions for
         * @return a new {@code OperationConfigurator} instance
         */
        @SafeVarargs
        public final OperationConfigurator forContainers(Class<? extends AccessibleContainer> ... containers){
            this.containers = new HashSet<>();
            if(containers != null)
                for(Class<? extends AccessibleContainer> container : containers)
                    if(container != null)
                        this.containers.add(container);
            return new OperationConfigurator();
        }

        /**
         * Configures permissions for the specified containers.
         *
         * @param containers the containers to configure permissions for
         * @return a new {@code OperationConfigurator} instance
         */
        public OperationConfigurator forContainers(Collection<Class<? extends AccessibleContainer>> containers){
            this.containers = new HashSet<>();
            if(containers != null)
                for(Class<? extends AccessibleContainer> container : containers)
                    if(container != null)
                        this.containers.add(container);
            return new OperationConfigurator();
        }

        /**
         * Returns a wrapper for accessing the user-defined roles, which implements {@code UserRoleService}.
         *
         * @return an instance of {@code UserRoleService} wrapping the configured permissions
         */
        public UserRoleService get() {
            return new StaticUserRoleService();
        }

        /**
         * Configurator for operations on the specified containers.
         */
        public class OperationConfigurator {
            private Map<String, Collection<PermissionHolder>> current;

            public OperationConfigurator() {
                this.current = new HashMap<>();
            }

            /**
             * Allows access for all roles, with optional create, update, and delete permissions.
             *
             * @param alsoAllowCreate whether to allow create operations
             * @param alsoAllowUpdate whether to allow update operations
             * @param alsoAllowDelete whether to allow delete operations
             * @return this {@code OperationConfigurator} instance
             */
            public OperationConfigurator allowAccessForAll(boolean alsoAllowCreate, boolean alsoAllowUpdate, boolean alsoAllowDelete){
                return allowAccess(alsoAllowCreate, alsoAllowUpdate, alsoAllowDelete, "ALL");
            }

            /**
             * Allows access for the specified roles, with optional create, update, and delete permissions.
             *
             * @param alsoAllowCreate whether to allow create operations
             * @param alsoAllowUpdate whether to allow update operations
             * @param alsoAllowDelete whether to allow delete operations
             * @param roles the roles to allow access for
             * @return this {@code OperationConfigurator} instance
             */
            public OperationConfigurator allowAccess(boolean alsoAllowCreate, boolean alsoAllowUpdate, boolean alsoAllowDelete, Collection<String> roles) {
                return allowAccess(alsoAllowCreate, alsoAllowUpdate, alsoAllowDelete, List.copyOf(roles).toArray(new String[0]));
            }

            /**
             * Allows access for the specified roles, with optional create, update, and delete permissions.
             *
             * @param alsoAllowCreate whether to allow create operations
             * @param alsoAllowUpdate whether to allow update operations
             * @param alsoAllowDelete whether to allow delete operations
             * @param roles the roles to allow access for
             * @return this {@code OperationConfigurator} instance
             */
            public OperationConfigurator allowAccess(final boolean alsoAllowCreate, final boolean alsoAllowUpdate, final boolean alsoAllowDelete, String ... roles){
                if(roles != null)
                    for (String role : roles)
                        if (role != null) {
                            current.compute(role, (_role, _list) -> {
                                if(_list == null) _list = new LinkedList<>();
                                containers.stream().map(container -> {
                                    PermissionHolder holder = new PermissionHolder(container, EnumSet.noneOf(AllowedOperation.class));
                                    if(alsoAllowCreate) holder.allowedOperations.add(AllowedOperation.CREATE);
                                    if(alsoAllowUpdate) holder.allowedOperations.add(AllowedOperation.UPDATE);
                                    if(alsoAllowDelete) holder.allowedOperations.add(AllowedOperation.DELETE);
                                    return holder;
                                }).forEach(_list::add);
                                return _list;
                            });
                        }
                return this;
            }

            /**
             * Accepts and saves the permissions defined by this configurator.
             *
             * @return this {@code ContainerPermissionConfigurator} instance
             */
            public ContainerPermissionConfigurator accept(){
                current.entrySet().stream().forEach(entry -> {
                    permissionHolders.compute(entry.getKey(), (_role, _list) -> {
                        if(_list == null) return entry.getValue();
                        _list.addAll(entry.getValue());
                        return _list;
                    });
                });
                return ContainerPermissionConfigurator.this;
            }
        }
    }
}

