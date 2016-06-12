package io.github.sporklibrary;

import io.github.sporklibrary.logging.LoggerFactoryTemp;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.sporklibrary.binders.FieldBinder;
import io.github.sporklibrary.binders.MethodBinder;
import io.github.sporklibrary.binders.TypeBinder;
import io.github.sporklibrary.caching.BinderCache;
import io.github.sporklibrary.interfaces.ObjectBinder;
import io.github.sporklibrary.logging.Logger;

/**
 * The BinderManager manages all bindings and their cache.
 */
public class BinderManager {
    private static final Logger logger = LoggerFactoryTemp.getLogger(BinderManager.class);
    private final List<FieldBinder<?>> fieldBinders = new ArrayList<>();
    private final List<MethodBinder<?>> methodBinders = new ArrayList<>();
    private final List<TypeBinder<?>> typeBinders = new ArrayList<>();
    private final Map<Class<?>, BinderCache> classBinderCacheMap = new HashMap<>();

    /**
     * Register a FieldBinder
     *
     * @param binder           the binder instance
     * @param <AnnotationType> the annotation type of the binder
     */
    public <AnnotationType extends Annotation> void register(FieldBinder<AnnotationType> binder) {
        if (logger.isDebugEnabled()) {
            logger.debug("register {}", binder.getClass());
        }

        fieldBinders.add(binder);

        // Update cache
        for (BinderCache cache : classBinderCacheMap.values()) {
            cache.register(binder);
        }
    }

    /**
     * Register a MethodBinder
     *
     * @param binder           the binder instance
     * @param <AnnotationType> the annotation type of the binder
     */
    public <AnnotationType extends Annotation> void register(MethodBinder<AnnotationType> binder) {
        if (logger.isDebugEnabled()) {
            logger.debug("register {}", binder.getClass());
        }

        methodBinders.add(binder);

        // Update cache
        for (BinderCache cache : classBinderCacheMap.values()) {
            cache.register(binder);
        }
    }

    /**
     * Register a TypeBinder
     *
     * @param binder           the binder instance
     * @param <AnnotationType> the annotation type of the binder
     */
    public <AnnotationType extends Annotation> void register(TypeBinder<AnnotationType> binder) {
        if (logger.isDebugEnabled()) {
            logger.debug("register {}", binder.getClass());
        }

        typeBinders.add(binder);

        // Update cache
        for (BinderCache cache : classBinderCacheMap.values()) {
            cache.register(binder);
        }
    }

    /**
     * Bind all annotations for an object instance on all levels of inheritance.
     *
     * @param object the instance to bind annotations for
     */
    public void bind(Object object) {
        if (logger.isDebugEnabled()) {
            logger.debug("bind {}", object);
        }

        Class<?> objectClass = object.getClass();

        while (objectClass != null && objectClass != Object.class) {
            BinderCache cache = classBinderCacheMap.get(objectClass);

            if (cache == null) {
                cache = createCache(objectClass);

                classBinderCacheMap.put(objectClass, cache);
            }

            bind(object, cache);

            objectClass = objectClass.getSuperclass();
        }

    }

    /**
     * Bind all annotations for an object instance for one specific class (one level of
     * inheritance).
     *
     * @param object the instance to bind annotations for
     * @param cache  the cache to bind with
     */
    private void bind(Object object, BinderCache cache) {
        for (ObjectBinder binder : cache.getBinders()) {
            binder.bind(object);
        }
    }

    /**
     * Allocated the cache for the specified class
     *
     * @param classObject the class to create a cache for
     * @return the cache
     */
    private BinderCache createCache(Class<?> classObject) {
        if (logger.isDebugEnabled()) {
            logger.debug("createCache {}", classObject.getName());
        }

        BinderCache cache = new BinderCache(classObject);

        for (TypeBinder<?> typeBinder : typeBinders) {
            cache.register(typeBinder);
        }

        for (FieldBinder<?> fieldBinder : fieldBinders) {
            cache.register(fieldBinder);
        }

        for (MethodBinder<?> methodBinder : methodBinders) {
            cache.register(methodBinder);
        }

        return cache;
    }
}