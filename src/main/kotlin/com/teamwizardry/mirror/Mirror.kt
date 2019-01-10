package com.teamwizardry.mirror

import com.teamwizardry.mirror.abstractionlayer.method.AbstractMethod
import com.teamwizardry.mirror.member.FieldMirror
import com.teamwizardry.mirror.member.MethodMirror
import com.teamwizardry.mirror.type.ClassMirror
import com.teamwizardry.mirror.type.TypeMirror
import io.leangen.geantyref.TypeFactory
import java.lang.reflect.AnnotatedType
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Type

/**
 * Provides access to the Mirrors for various reflective types.
 */
object Mirror {
    internal var cache = MirrorCache()

    /**
     * Create a mirror of the passed type.
     */
    @JvmStatic
    fun reflect(type: Type): TypeMirror {
        return cache.types.reflect(type)
    }

    @JvmStatic
    fun reflect(type: AnnotatedType): TypeMirror {
        return cache.types.reflect(type)
    }

    inline fun <reified T> reflect(): TypeMirror {
        return reflect(annotatedTypeToken<T>())
    }

    /**
     * Convenience method to reduce unneeded casting when the passed type is known to be a class rather than an array
     * or void.
     *
     * @throws IllegalArgumentException if the input class is an array type
     */
    @JvmStatic
    fun reflectClass(clazz: Class<*>): ClassMirror {
        if(clazz.isArray) throw IllegalArgumentException("reflectClass cannot reflect an array type")
        return reflect(clazz) as ClassMirror
    }

    /**
     * Convenience method to reduce unneeded casting when the passed type is known to be a class rather than an array
     * or void.
     *
     * @throws IllegalArgumentException if the input class is an array type
     */
    inline fun <reified T> reflectClass(): ClassMirror {
        if(T::class.java.isArray) throw IllegalArgumentException("reflectClass cannot reflect an array type")
        return reflect<T>() as ClassMirror
    }

    @JvmStatic
    fun reflect(field: Field): FieldMirror {
        return cache.fields.reflect(field)
    }

    @JvmStatic
    fun reflect(method: Method): MethodMirror {
        val abstract = AbstractMethod(method)
        return cache.methods.reflect(abstract)
    }

    @JvmStatic
    @JvmOverloads
    fun <T: Annotation> newAnnotation(clazz: Class<T>, arguments: Map<String, Any> = emptyMap()): T {
        return TypeFactory.annotation(clazz, arguments)
    }

    inline fun <reified T: Annotation> newAnnotation(arguments: Map<String, Any> = emptyMap()): T {
        return TypeFactory.annotation(T::class.java, arguments)
    }
}