package com.teamwizardry.mirror.type

import com.teamwizardry.mirror.Mirror
import com.teamwizardry.mirror.testsupport.GenericObject1
import com.teamwizardry.mirror.testsupport.GenericObject2
import com.teamwizardry.mirror.testsupport.Interface1
import com.teamwizardry.mirror.testsupport.Interface1Sub1
import com.teamwizardry.mirror.testsupport.Interface1Sub2
import com.teamwizardry.mirror.testsupport.LowerBounded
import com.teamwizardry.mirror.testsupport.MirrorTestBase
import com.teamwizardry.mirror.testsupport.Object1
import com.teamwizardry.mirror.testsupport.UpperBounded
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.lang.reflect.ParameterizedType

internal class IsAssignableTest: MirrorTestBase() {
    @Test
    @DisplayName("A class should be assignable from itself")
    fun classMirrorAssignableFromSelf() {
        val type = Mirror.reflect<Object1>()
        assertTrue(type.isAssignableFrom(type))
    }

    @Test
    @DisplayName("An array mirror should be assignable from itself")
    fun arrayMirrorAssignableFromSelf() {
        val type = Mirror.reflect<Array<Any>>()
        assertTrue(type.isAssignableFrom(type))
    }

    @Test
    @DisplayName("A primitive array mirror should be assignable from itself")
    fun primitiveArrayMirrorAssignableFromSelf() {
        val type = Mirror.reflect<IntArray>()
        assertTrue(type.isAssignableFrom(type))
    }

    @Test
    @DisplayName("A primitive int array mirror should not be assignable from an Integer[] array")
    fun primitiveIntArrayMirrorNotAssignableFromIntegerArray() {
        assertFalse(Mirror.reflect<IntArray>().isAssignableFrom(Mirror.reflect<Array<Int>>()))
    }

    @Test
    @DisplayName("The Object mirror should be assignable from itself")
    fun objectAssignableFromSelf() {
        assertTrue(
            Mirror.reflect<Any>().isAssignableFrom(
                Mirror.reflect<Any>()
            )
        )
    }

    @Test
    @DisplayName("The Object mirror should be assignable from another class")
    fun objectAssignableFromClass() {
        assertTrue(
            Mirror.reflect<Any>().isAssignableFrom(
                Mirror.reflect<Object1>()
            )
        )
    }

    @Test
    @DisplayName("The Object mirror should be assignable from a generic class")
    fun objectAssignableFromGenericClass() {
        assertTrue(
            Mirror.reflect<Any>().isAssignableFrom(
                Mirror.reflect<GenericObject1<Object1>>()
            )
        )
    }

    @Test
    @DisplayName("The Object mirror should be assignable from an array")
    fun objectAssignableFromArray() {
        assertTrue(
            Mirror.reflect<Any>().isAssignableFrom(
                Mirror.reflect<GenericObject1<Object1>>()
            )
        )
    }

    @Test
    @DisplayName("The Object mirror should not be assignable from a primitive")
    fun objectNotAssignableFromPrimitive() {
        assertFalse(
            Mirror.reflect<Any>().isAssignableFrom(
                Mirror.reflect(Int::class.javaPrimitiveType!!)
            )
        )
    }

    interface ClassASuperInterface
    interface ClassAInterface
    interface ClassASubInterface
    open class ClassASuper: ClassASuperInterface
    open class ClassA: ClassASuper(), ClassAInterface
    open class ClassASub: ClassA(), ClassASubInterface

    @Test
    @DisplayName("A class mirror should be assignable from itself")
    fun classAssignableFromSelf() {
        assertTrue(
            Mirror.reflect<ClassA>().isAssignableFrom(
                Mirror.reflect<ClassA>()
            )
        )
    }

    @Test
    @DisplayName("A class mirror should be assignable from a subclass")
    fun classAssignableFromSubclass() {
        assertTrue(
            Mirror.reflect<ClassA>().isAssignableFrom(
                Mirror.reflect<ClassASub>()
            )
        )
    }

    @Test
    @DisplayName("A class mirror should not be assignable from a superclass")
    fun classNotAssignableFromSuperclass() {
        assertFalse(
            Mirror.reflect<ClassA>().isAssignableFrom(
                Mirror.reflect<ClassASuper>()
            )
        )
    }

    @Test
    @DisplayName("A class mirror should not be assignable from an unrelated class")
    fun classNotAssignableFromUnrelatedClass() {
        assertFalse(
            Mirror.reflect<ClassA>().isAssignableFrom(
                Mirror.reflect<Object1>()
            )
        )
    }

    @Test
    @DisplayName("An interface mirror should be assignable from an implementing class")
    fun interfaceAssignableFromImplementingClass() {
        assertTrue(
            Mirror.reflect<ClassAInterface>().isAssignableFrom(
                Mirror.reflect<ClassA>()
            )
        )
    }

    @Test
    @DisplayName("An interface should be assignable from the subclass of an implementing class")
    fun interfaceAssignableFromSubClassOfImplementingClass() {
        assertTrue(
            Mirror.reflect<ClassASuperInterface>().isAssignableFrom(
                Mirror.reflect<ClassA>()
            )
        )
    }

    @Test
    @DisplayName("A generic class should be assignable from itself")
    fun genericClassAssignableFromSelf() {
        assertTrue(
            Mirror.reflect<GenericObject1<ClassA>>().isAssignableFrom(
                Mirror.reflect<GenericObject1<ClassA>>()
            )
        )
    }

    @Test
    @DisplayName("A generic class should be assignable from itself with subclassed type parameters")
    fun genericClassAssignableFromSubclassedParameters() {
        assertTrue(
            Mirror.reflect<GenericObject1<ClassA>>().isAssignableFrom(
                Mirror.reflect<GenericObject1<ClassASub>>()
            )
        )
    }

    @Test
    @DisplayName("A generic class should not be assignable from itself with superclassed type parameters")
    fun genericClassNotAssignableFromSuperclassedParameters() {
        assertFalse(
            Mirror.reflect<GenericObject1<ClassA>>().isAssignableFrom(
                Mirror.reflect<GenericObject1<ClassASuper>>()
            )
        )
    }

    @Test
    @DisplayName("A generic class should be assignable from a subclass that specifies type parameters explicitly")
    fun genericClassAssignableFromSubclassWithExplicitParameters() {
        class GenericObjectSub: GenericObject1<ClassA>()
        assertTrue(
            Mirror.reflect<GenericObject1<ClassA>>().isAssignableFrom(
                Mirror.reflect<GenericObjectSub>()
            )
        )
    }

    @Test
    @DisplayName("A generic class should be assignable from a subclass that specifies type parameters dynamically")
    fun genericClassAssignableFromSubclassWithDynamicParameters() {
        class GenericObjectSub<T>: GenericObject1<GenericObject2<T>>()
        assertTrue(
            Mirror.reflect<GenericObject1<GenericObject2<Object1>>>().isAssignableFrom(
                Mirror.reflect<GenericObjectSub<Object1>>()
            )
        )
    }

    @Test
    @DisplayName("A generic class should be assignable from a subclass that specifies incorrect type parameters dynamically")
    fun genericClassNotAssignableFromSubclassWithIncorrectDynamicParameters() {
        class GenericObjectSub<T>: GenericObject1<GenericObject2<T>>()
        assertFalse(
            Mirror.reflect<GenericObject1<GenericObject2<Object1>>>().isAssignableFrom(
                Mirror.reflect<GenericObjectSub<ClassA>>()
            )
        )
    }

    @Test
    @DisplayName("A generic class should be assignable from a raw version of itself")
    fun genericClassNotAssignableFromRawSelf() {
        assertFalse(
            Mirror.reflect<GenericObject1<Object1>>().isAssignableFrom(
                Mirror.reflect<GenericObject1<*>>().raw
            )
        )
    }

    @Test
    @DisplayName("A raw generic class should be assignable from a raw version of itself")
    fun rawGenericClassAssignableFromRawSelf() {
        assertTrue(
            Mirror.reflect<GenericObject1<Object1>>().raw.isAssignableFrom(
                Mirror.reflect<GenericObject1<*>>().raw
            )
        )
    }

    @Test
    @DisplayName("A raw generic class should be assignable from a specialized version of itself")
    fun rawGenericClassAssignableFromSpecializedSelf() {
        assertTrue(
            Mirror.reflect<GenericObject1<*>>().raw.isAssignableFrom(
                Mirror.reflect<GenericObject1<Object1>>()
            )
        )
    }

    @Test
    @DisplayName("The void mirror should be assignable from itself")
    fun voidAssignableFromSelf() {
        assertTrue(
            Mirror.Types.void.isAssignableFrom(
                Mirror.Types.void
            )
        )
    }

    @Test
    @DisplayName("The void mirror should not be assignable from other types")
    fun voidNotAssignableFromOthers() {
        assertFalse(
            Mirror.Types.void.isAssignableFrom(
                Mirror.Types.any
            )
        )
        assertFalse(
            Mirror.Types.void.isAssignableFrom(
                Mirror.reflect<IntArray>()
            )
        )
        assertFalse(
            Mirror.Types.void.isAssignableFrom(
                Mirror.reflect<GenericObject1<String>>()
            )
        )
        assertFalse(
            Mirror.Types.void.isAssignableFrom(
                Mirror.reflect(lowerWildcard)
            )
        )
    }

    class BoundedFieldHolder(
        @JvmField
        var lowerBounded: LowerBounded<Interface1Sub1>,
        @JvmField
        var upperBounded: UpperBounded<Interface1Sub1>
    )

    val lowerWildcard = (BoundedFieldHolder::class.java.getField("lowerBounded").genericType as ParameterizedType).actualTypeArguments[0]
    val upperWildcard = (BoundedFieldHolder::class.java.getField("upperBounded").genericType as ParameterizedType).actualTypeArguments[0]

    @Test
    @DisplayName("The Object mirror should be assignable from wildcard mirrors")
    fun objectAssignableFromWildcard() {
        assertTrue(
            Mirror.reflect<Any>().isAssignableFrom(
                Mirror.reflect(lowerWildcard)
            )
        )
        assertTrue(
            Mirror.reflect<Any>().isAssignableFrom(
                Mirror.reflect(upperWildcard)
            )
        )
    }

    @Test
    @DisplayName("Lower-bounded wildcard mirrors should be assignable from mirrors of their supertype")
    fun lowerWildcardAssignableFromSupertype() {
        assertTrue(
            Mirror.reflect(lowerWildcard).isAssignableFrom(
                Mirror.reflect<Interface1>()
            )
        )
    }

    @Test
    @DisplayName("Lower-bounded wildcard mirrors should be assignable from mirrors of their bound")
    fun lowerWildcardAssignableFromBound() {
        assertTrue(
            Mirror.reflect(lowerWildcard).isAssignableFrom(
                Mirror.reflect<Interface1Sub1>()
            )
        )
    }

    @Test
    @DisplayName("Lower-bounded wildcard mirrors should not be assignable from mirrors of their subtypes")
    fun lowerWildcardNotAssignableFromSubtype() {
        assertFalse(
            Mirror.reflect(lowerWildcard).isAssignableFrom(
                Mirror.reflect<Interface1Sub2>()
            )
        )
    }

    @Test
    @DisplayName("Upper-bounded wildcard mirrors should not be assignable from mirrors of their supertype")
    fun upperWildcardNotAssignableFromSupertype() {
        assertFalse(
            Mirror.reflect(upperWildcard).isAssignableFrom(
                Mirror.reflect<Interface1>()
            )
        )
    }

    @Test
    @DisplayName("Upper-bounded wildcard mirrors should be assignable from mirrors of their bound")
    fun upperWildcardAssignableFromBound() {
        assertTrue(
            Mirror.reflect(upperWildcard).isAssignableFrom(
                Mirror.reflect<Interface1Sub1>()
            )
        )
    }

    @Test
    @DisplayName("Upper-bounded wildcard mirrors should be assignable from mirrors of their subtypes")
    fun upperWildcardAssignableFromSubtype() {
        assertTrue(
            Mirror.reflect(upperWildcard).isAssignableFrom(
                Mirror.reflect<Interface1Sub2>()
            )
        )
    }

}