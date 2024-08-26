package com.valdisdot.util.vaadin.component;

import com.vaadin.flow.component.Component;

import java.util.Collection;

public class BlockElement extends com.vaadin.flow.component.html.Div {
    public BlockElement(String className){
        super();
        if(className != null && !className.isBlank()) addClassName(className);
    }

    public BlockElement(String className, Collection<Component> components) {
        this(className);
        if(components != null) for(Component c : components) add(c);
    }

    public BlockElement(String className, Component ... components){
        this(className);
        if(components != null) for(Component c : components) add(c);
    }
}
