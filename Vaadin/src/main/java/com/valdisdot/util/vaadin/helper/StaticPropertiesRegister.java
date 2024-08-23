package com.valdisdot.util.vaadin.helper;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.RouteConfiguration;

import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory implementation of {@code PropertiesRegister}.
 * <p>
 * Example of generation: for a simple class {@code com.example.MyClass},
 * the default key will be {@code com.example.my-class}. By calling {@code getPropertyKeyFor(this.getClass(), "user", "title")},
 * you will get a key {@code com.example.my-class$user.title}, which can be used as needed.
 * </p>
 * @apiNote If the component is a Vaadin Route, the route will be used as a source for key generator. For example, for {@code my/path/page} will be {@code my.path.page}
 */
public class StaticPropertiesRegister implements PropertiesRegister {
    public static final String EMPTY_PROPERTY = "empty";
    public static final String ROOT_PROPERTY = "root";
    private static final Map<Class<?>, String> propertiesRegister = new HashMap<>();

    private static String regComponent(Class<? extends Component> component) {
        if (component == null) return EMPTY_PROPERTY;
        if (propertiesRegister.containsKey(component)) return propertiesRegister.get(component);

        String componentKey;
        Optional<String> optional = RouteConfiguration.forApplicationScope().getUrlBase(component);
        if (optional.isPresent()) {
            componentKey = optional.map(s -> s.replaceAll("/", "."))
                    .filter(s -> !s.isBlank())
                    .orElse(ROOT_PROPERTY);
        } else {
            componentKey = toHyphenatedString(component);
        }
        propertiesRegister.put(component, componentKey);
        return componentKey;
    }

    /**{@inheritDoc}**/
    @Override
    public String registerComponent(Class<?> component) {
        if (component == null) return EMPTY_PROPERTY;
        if (Component.class.isAssignableFrom(component)) {
            return regComponent((Class<? extends Component>) component);
        } else {
            String t = toHyphenatedString(component);
            propertiesRegister.put(component, t);
            return t;
        }
    }

    /**{@inheritDoc}**/
    @Override
    public Optional<String> getPropertyKeyFor(Class<?> component) {
        return Optional.ofNullable(propertiesRegister.get(component));
    }

    /**{@inheritDoc}**/
    @Override
    public Optional<String> getPropertyKeyFor(Class<?> component, String... withSuffixes) {
        if (propertiesRegister.containsKey(component)) {
            return Optional.of(concatenateWithDot(new StringBuilder(propertiesRegister.get(component)), withSuffixes));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Returns the copy of properties.
     * */
    public Collection<String> getValues(){
        return List.copyOf(propertiesRegister.values());
    }

    /**
     * Converts a class name to a hyphenated string representation.
     * <p>
     * For example, {@code com.mypackage.MySuperClass$InnerClass} is converted to {@code com.mypackage.my-super-class.inner_class}.
     * </p>
     *
     * @param clazz   the class whose name is to be converted
     * @param exclude optional list of strings to exclude from the conversion
     * @return the hyphenated string representation of the class name
     */
    private static String toHyphenatedString(Class<?> clazz, String ... exclude) {
        String str = clazz.getName();
        StringBuilder result = new StringBuilder();
        char ch = str.charAt(0);
        result.append(Character.toLowerCase(ch));
        for (int i = 1; i < str.length(); i++) {
            ch = str.charAt(i);
            if (Character.isUpperCase(ch)) {
                result.append('-').append(Character.toLowerCase(ch));
            } else {
                result.append(ch);
            }
        }
        String regex = "";
        if (exclude != null && exclude.length > 0) {
            regex = Arrays.stream(exclude).map(String::toLowerCase).collect(Collectors.joining("|", "-?(", ")-?"));
        }
        return result.toString().replaceAll(regex, "").replaceAll("\\$-", ".").replaceAll("(-{2,}|\\.-)", "-");
    }

    /**
     * Concatenates the base string with the given suffixes, separating each with a dot.
     *
     * @param base    the base string
     * @param suffixes the suffixes to be appended to the base string
     * @return the concatenated string
     */
    private static String concatenateWithDot(StringBuilder base, String ... suffixes) {
        if (base == null) {
            return "";
        } else if (suffixes == null) {
            return base.toString();
        } else {
            String divider = "$";
            for (String suffix : suffixes) {
                if (suffix != null) {
                    base.append(divider).append(suffix);
                    divider = ".";
                }
            }
            return base.toString();
        }
    }
}
