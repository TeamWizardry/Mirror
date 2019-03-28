package com.teamwizardry.mirror.testsupport;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.*;
import java.util.*;

public class AnnotatedTypeHolder {
    @NotNull
    private Map<String, Map<Integer, AnnotatedType>> types = new HashMap<>();
    @NotNull
    private Map<String, Object> elements = new HashMap<>();

    protected AnnotatedTypeHolder() {
        populateTypes();
    }

    public Method getMethod(String name) { return (Method) elements.get(name); }
    public Field getField(String name) { return (Field) elements.get(name); }
    public Constructor getConstructor(String name) { return (Constructor) elements.get(name); }
    public Parameter getParameter(String name) { return (Parameter) elements.get(name); }
    public Class getClass(String name) { return (Class) elements.get(name); }

    @NotNull
    public AnnotatedType get(String name) {
        return get(name, 0);
    }

    @NotNull
    public AnnotatedType get(String name, int index) {
        Map<Integer, AnnotatedType> indexMap = types.get(name);
        if(indexMap == null) {
            throw new IllegalArgumentException("No such key found. key=`" + name + "`");
        }

        AnnotatedType type = indexMap.get(index);
        if(type == null)
            throw new IllegalArgumentException("No such index found. key=`" + name + "` index=" + index);

        return type;
    }

    private void populateTypes() {
        Set<Class<?>> classes = new HashSet<>();
        Deque<Class<?>> searchQueue = new LinkedList<>();
        searchQueue.add(this.getClass());

        while(!searchQueue.isEmpty()) {
            Class<?> clazz = searchQueue.poll();
            classes.add(clazz);
            if(clazz.getSuperclass() != null) {
                searchQueue.add(clazz.getSuperclass());
            }
            Collections.addAll(searchQueue, clazz.getInterfaces());
            Collections.addAll(searchQueue, clazz.getDeclaredClasses());
        }

        Set<Field> fields = new HashSet<>();
        Set<Method> methods = new HashSet<>();
        Set<Parameter> parameters = new HashSet<>();
        Set<Constructor> constructors = new HashSet<>();

        for(Class<?> clazz : classes) {
            Collections.addAll(fields, clazz.getDeclaredFields());
            Collections.addAll(methods, clazz.getDeclaredMethods());
            for(Method method : clazz.getDeclaredMethods()) {
                Collections.addAll(parameters, method.getParameters());
            }
            Collections.addAll(constructors, clazz.getDeclaredConstructors());

            ElementHolder elementHolder = clazz.getDeclaredAnnotation(ElementHolder.class);
            if(elementHolder != null) {
                elements.put(elementHolder.value(), clazz);
            }
        }

        for(Method method : methods) {
            TypeHolder holder = method.getDeclaredAnnotation(TypeHolder.class);
            if(holder != null) {
                if(holder.type() == HolderType.DIRECT) {
                    addType(holder.value(), holder.index(), method.getAnnotatedReturnType());
                } else if(holder.type() == HolderType.PARAMS) {
                    int i = 0;
                    for(Parameter parameter : method.getParameters()) {
                        addType(holder.value(), holder.index() + i, parameter.getAnnotatedType());
                        i++;
                    }
                }
            }
            ElementHolder elementHolder = method.getDeclaredAnnotation(ElementHolder.class);
            if(elementHolder != null) {
                elements.put(elementHolder.value(), method);
            }
        }

        for(Field field : fields) {
            TypeHolder holder = field.getDeclaredAnnotation(TypeHolder.class);
            if(holder != null) {
                addType(holder.value(), holder.index(), field.getAnnotatedType());
            }
            ElementHolder elementHolder = field.getDeclaredAnnotation(ElementHolder.class);
            if(elementHolder != null) {
                elements.put(elementHolder.value(), field);
            }
        }

        for(Parameter parameter : parameters) {
            TypeHolder holder = parameter.getDeclaredAnnotation(TypeHolder.class);
            if(holder != null) {
                addType(holder.value(), holder.index(), parameter.getAnnotatedType());
            }
            ElementHolder elementHolder = parameter.getDeclaredAnnotation(ElementHolder.class);
            if(elementHolder != null) {
                elements.put(elementHolder.value(), parameter);
            }
        }

        for(Constructor constructor : constructors) {
            TypeHolder holder = constructor.getDeclaredAnnotation(TypeHolder.class);
            ElementHolder elementHolder = constructor.getDeclaredAnnotation(ElementHolder.class);
            if(elementHolder != null) {
                elements.put(elementHolder.value(), constructor);
            }
        }
    }

    private void addType(String name, int index, AnnotatedType type) {
        Map<Integer, AnnotatedType> indexMap;
        if(types.containsKey(name)) {
            indexMap = types.get(name);
        } else {
            indexMap = new HashMap<>();
            types.put(name, indexMap);
        }

        if(type.getType() instanceof ParameterizedType && ((ParameterizedType) type.getType()).getRawType() == Unwrap.class) {
            indexMap.put(index, ((AnnotatedParameterizedType) type).getAnnotatedActualTypeArguments()[0]);
        } else {
            indexMap.put(index, type);
        }
    }

    public enum HolderType {
        PARAMS,
        DIRECT
    }

    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface TypeHolder {
        String value();
        int index() default 0;
        HolderType type() default HolderType.PARAMS;
    }

    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR,
            ElementType.PARAMETER, ElementType.ANNOTATION_TYPE, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ElementHolder {
        String value();
    }

    protected final class Unwrap<T> {
    }
}
