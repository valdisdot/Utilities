package com.valdisdot.util.vaadin.component;

import com.vaadin.flow.component.Component;

import java.util.Collection;

public class InlineElement extends com.vaadin.flow.component.html.Span {
    public InlineElement(String className){
        super();
        if(className != null && !className.isBlank()) addClassName(className);
    }

    public InlineElement(String className, Collection<Component> components) {
        this(className);
        if(components != null) for(Component c : components) add(c);
    }

    public InlineElement(String className, Component ... components){
        this(className);
        if(components != null) for(Component c : components) add(c);
    }
}
