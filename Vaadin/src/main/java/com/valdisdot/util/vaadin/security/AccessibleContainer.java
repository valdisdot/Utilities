package com.valdisdot.util.vaadin.security;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.valdisdot.util.vaadin.component.Container;
import com.valdisdot.util.vaadin.helper.PropertiesRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;

import java.util.Objects;

/**
 * Abstract class for components that are protected by role-based rules and are registered with container keys.
 * This class extends {@link Container} to leverage container key registration and implements {@link BeforeEnterObserver}
 * to manage access control and content initialization.
 * <p>
 * Components extending this class should register with a {@link PropertiesRegister} for key management and
 * implement role-based access control using {@link CurrentUserService}.
 * </p>
 */
public abstract class AccessibleContainer extends Container implements BeforeEnterObserver {
    private CurrentUserService currentUserService;

    /**
     * Constructs an {@code AccessibleContainer} with the given {@link CurrentUserService}.
     * This constructor is used for dependency injection and initializes the container key register from the superclass.
     *
     * @param currentUserService the {@link CurrentUserService} to use for user permission management
     */
    @Autowired
    public AccessibleContainer(CurrentUserService currentUserService) {
        super();
        setCurrentUserService(currentUserService);
    }

    /**
     * Constructs an {@code AccessibleContainer} with the given {@link CurrentUserService} and {@link PropertiesRegister}.
     * This constructor initializes both the container key register and the user permission service.
     *
     * @param currentUserService the {@link CurrentUserService} to use for user permission management
     * @param propertiesRegister the {@link PropertiesRegister} for container key management
     */
    @Autowired
    public AccessibleContainer(CurrentUserService currentUserService, PropertiesRegister propertiesRegister) {
        super(propertiesRegister);
        setCurrentUserService(currentUserService);
    }

    /**
     * Default constructor for {@code AccessibleContainer}. Used for subclass instantiation without dependency injection.
     */
    protected AccessibleContainer() {
        super();
    }

    /**
     * Retrieves the {@link CurrentUserService} used by this container.
     *
     * @return the {@link CurrentUserService} used by this container
     */
    public CurrentUserService getCurrentUserService() {
        return currentUserService;
    }

    /**
     * Sets the {@link CurrentUserService} for this container. This method is used for Spring dependency injection
     * and is not accessible to inheritors.
     *
     * @param currentUserService the {@link CurrentUserService} to set
     */
    @Autowired
    void setCurrentUserService(CurrentUserService currentUserService) {
        this.currentUserService = Objects.requireNonNull(currentUserService, "CurrentUserService is null");
    }

    /**
     * Configures whether the component should be shown or not. This method checks if
     * the user has permission to access the component and initializes the content if allowed.
     *
     * @param event the {@link BeforeEnterEvent} that triggers this method
     * @throws AccessDeniedException if the user does not have permission to access the component
     * @apiNote If you override this method, ensure that {@code initContent()} is also called; otherwise,
     * the UI for this element will not be initialized with the logic implemented in {@code initContent()}.
     * @see AccessibleContainer#initContent()
     */
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!currentUserService.isAccessAllowed(this.getClass()))
            throw new AccessDeniedException("Forbidden for the user");
    }
}
