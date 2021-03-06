package dev.thecodewarrior.mirror.type

import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.mirror.testsupport.MTest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

@Suppress("LocalVariableName")
internal class IsAssignableTest: MTest() {

    @Test
    fun arrayMirror_shouldBeAssignable_fromItself() {
        val type = Mirror.reflect<Array<Any>>()
        assertTrue(type.isAssignableFrom(type))
    }

    @Test
    fun primitiveArrayMirror_shouldBeAssignable_fromItself() {
        val type = Mirror.reflect<IntArray>()
        assertTrue(type.isAssignableFrom(type))
    }

    @Test
    fun primitiveArrayMirror_shouldBeAssignable_fromBoxedArrayMirror() {
        assertFalse(Mirror.reflect<IntArray>().isAssignableFrom(Mirror.reflect<Array<Int>>()))
    }

    @Test
    fun object_shouldBeAssignable_fromItself() {
        assertTrue(
            Mirror.reflect<Any>().isAssignableFrom(
                Mirror.reflect<Any>()
            )
        )
    }

    @Test
    fun object_shouldBeAssignable_fromClass() {
        val X by sources.add("X", "class X {}")
        sources.compile()

        assertTrue(
            Mirror.reflect<Any>().isAssignableFrom(
                Mirror.reflect(X)
            )
        )
    }

    @Test
    fun object_shouldBeAssignable_fromGeneric() {
        val X by sources.add("X", "class X {}")
        val Generic by sources.add("Generic", "class Generic<T> {}")
        val types = sources.types {
            +"Generic<X>"
        }
        sources.compile()

        assertTrue(
            Mirror.reflect<Any>().isAssignableFrom(
                Mirror.reflect(types["Generic<X>"])
            )
        )
    }

    @Test
    fun object_shouldBeAssignable_fromArray() {
        val X by sources.add("X", "class X {}")
        val types = sources.types {
            +"X[]"
        }
        sources.compile()

        assertTrue(
            Mirror.reflect<Any>().isAssignableFrom(
                Mirror.reflect(types["X[]"])
            )
        )
    }

    @Test
    fun object_shouldNotBeAssignable_fromPrimitive() {
        assertFalse(
            Mirror.reflect<Any>().isAssignableFrom(
                Mirror.types.int
            )
        )
    }

    @Test
    @DisplayName("A class mirror should be assignable from itself")
    fun classAssignableFromSelf() {
        val X by sources.add("X", "class X {}")
        sources.compile()
        assertTrue(
            Mirror.reflect(X).isAssignableFrom(
                Mirror.reflect(X)
            )
        )
    }

    @Test
    @DisplayName("A class mirror should be assignable from a subclass")
    fun classAssignableFromSubclass() {
        val X by sources.add("X", "class X {}")
        val Y by sources.add("Y", "class Y extends X {}")
        sources.compile()
        assertTrue(
            Mirror.reflect(X).isAssignableFrom(
                Mirror.reflect(Y)
            )
        )
    }

    @Test
    @DisplayName("A class mirror should not be assignable from a superclass")
    fun classNotAssignableFromSuperclass() {
        val X by sources.add("X", "class X {}")
        val Y by sources.add("Y", "class Y extends X {}")
        sources.compile()
        assertFalse(
            Mirror.reflect(Y).isAssignableFrom(
                Mirror.reflect(X)
            )
        )
    }

    @Test
    @DisplayName("A class mirror should not be assignable from an unrelated class")
    fun classNotAssignableFromUnrelatedClass() {
        val X by sources.add("X", "class X {}")
        val Y by sources.add("Y", "class Y {}")
        sources.compile()
        assertFalse(
            Mirror.reflect(X).isAssignableFrom(
                Mirror.reflect(Y)
            )
        )
    }

    @Test
    @DisplayName("An interface mirror should be assignable from an implementing class")
    fun interfaceAssignableFromImplementingClass() {
        val I by sources.add("I", "interface I {}")
        val X by sources.add("X", "class X implements I {}")
        sources.compile()
        assertTrue(
            Mirror.reflect(I).isAssignableFrom(
                Mirror.reflect(X)
            )
        )
    }

    @Test
    @DisplayName("An interface should be assignable from the subclass of an implementing class")
    fun interfaceAssignableFromSubClassOfImplementingClass() {
        val I by sources.add("I", "interface I {}")
        val X by sources.add("X", "class X implements I {}")
        val Y by sources.add("Y", "class Y extends X {}")
        sources.compile()
        assertTrue(
            Mirror.reflect(I).isAssignableFrom(
                Mirror.reflect(Y)
            )
        )
    }

    @Test
    @DisplayName("A generic class should be assignable from itself")
    fun genericClassAssignableFromSelf() {
        val X by sources.add("X", "class X {}")
        val Generic by sources.add("Generic", "class Generic<T> {}")
        val types = sources.types {
            +"Generic<X>"
        }
        sources.compile()
        assertTrue(
            Mirror.reflect(types["Generic<X>"]).isAssignableFrom(
                Mirror.reflect(types["Generic<X>"])
            )
        )
    }

    @Test
    @DisplayName("A generic class should be assignable from itself with subclassed type parameters")
    fun genericClassAssignableFromSubclassedParameters() {
        val X by sources.add("X", "class X {}")
        val Y by sources.add("Y", "class Y extends X {}")
        val Generic by sources.add("Generic", "class Generic<T> {}")
        val types = sources.types {
            +"Generic<X>"
            +"Generic<Y>"
        }
        sources.compile()
        assertTrue(
            Mirror.reflect(types["Generic<X>"]).isAssignableFrom(
                Mirror.reflect(types["Generic<Y>"])
            )
        )
    }

    @Test
    @DisplayName("A generic class should not be assignable from itself with superclassed type parameters")
    fun genericClassNotAssignableFromSuperclassedParameters() {
        val X by sources.add("X", "class X {}")
        val Y by sources.add("Y", "class Y extends X {}")
        val Generic by sources.add("Generic", "class Generic<T> {}")
        val types = sources.types {
            +"Generic<X>"
            +"Generic<Y>"
        }
        sources.compile()
        assertFalse(
            Mirror.reflect(types["Generic<Y>"]).isAssignableFrom(
                Mirror.reflect(types["Generic<X>"])
            )
        )
    }

    @Test
    @DisplayName("A generic class should be assignable from a subclass that specifies type parameters explicitly")
    fun genericClassAssignableFromSubclassWithExplicitParameters() {
        val X by sources.add("X", "class X {}")
        val Generic by sources.add("Generic", "class Generic<T> {}")
        val GenericX by sources.add("GenericX", "class GenericX extends Generic<X> {}")
        val types = sources.types {
            +"Generic<X>"
        }
        sources.compile()
        assertTrue(
            Mirror.reflect(types["Generic<X>"]).isAssignableFrom(
                Mirror.reflect(GenericX)
            )
        )
    }

    @Test
    @DisplayName("A generic class should be assignable from a subclass that specifies type parameters dynamically")
    fun genericClassAssignableFromSubclassWithDynamicParameters() {
        val X by sources.add("X", "class X {}")
        val Generic by sources.add("Generic", "class Generic<T> {}")
        val GenericSub by sources.add("GenericSub", "class GenericSub<T> extends Generic<Generic<T>> {}")
        val types = sources.types {
            +"Generic<Generic<X>>"
            +"GenericSub<X>"
        }
        sources.compile()
        assertTrue(
            Mirror.reflect(types["Generic<Generic<X>>"]).isAssignableFrom(
                Mirror.reflect(types["GenericSub<X>"])
            )
        )
    }

    @Test
    @DisplayName("A generic class should be assignable from a subclass that specifies incorrect type parameters dynamically")
    fun genericClassNotAssignableFromSubclassWithIncorrectDynamicParameters() {
        val X by sources.add("X", "class X {}")
        val Y by sources.add("Y", "class Y {}")
        val Generic by sources.add("Generic", "class Generic<T> {}")
        val GenericSub by sources.add("GenericSub", "class GenericSub<T> extends Generic<Generic<T>> {}")
        val types = sources.types {
            +"Generic<Generic<X>>"
            +"GenericSub<Y>"
        }
        sources.compile()
        assertFalse(
            Mirror.reflect(types["Generic<Generic<X>>"]).isAssignableFrom(
                Mirror.reflect(types["GenericSub<Y>"])
            )
        )
    }

    @Test
    @DisplayName("A generic class should be assignable from a raw version of itself")
    fun genericClassNotAssignableFromRawSelf() {
        val X by sources.add("X", "class X {}")
        val Generic by sources.add("Generic", "class Generic<T> {}")
        val types = sources.types {
            +"Generic<X>"
        }
        sources.compile()
        assertFalse(
            Mirror.reflect(types["Generic<X>"]).isAssignableFrom(
                Mirror.reflect(Generic)
            )
        )
    }

    @Test
    @DisplayName("A raw generic class should be assignable from a raw version of itself")
    fun rawGenericClassAssignableFromRawSelf() {
        val Generic by sources.add("Generic", "class Generic<T> {}")
        sources.compile()
        assertTrue(
            Mirror.reflect(Generic).raw.isAssignableFrom(
                Mirror.reflect(Generic)
            )
        )
    }

    @Test
    @DisplayName("A raw generic class should be assignable from a specialized version of itself")
    fun rawGenericClassAssignableFromSpecializedSelf() {
        val X by sources.add("X", "class X {}")
        val Generic by sources.add("Generic", "class Generic<T> {}")
        val types = sources.types {
            +"Generic<X>"
        }
        sources.compile()
        assertTrue(
            Mirror.reflect(Generic).isAssignableFrom(
                Mirror.reflect(types["Generic<X>"])
            )
        )
    }

    @Test
    @DisplayName("A class should be assignable from a wildcard with an equal upper bound")
    fun classAssignableFromEqualUpperBoundedWildcard() {
        val X by sources.add("X", "class X {}")
        val Y by sources.add("Y", "class Y extends X {}")
        val types = sources.types {
            +"? extends Y"
        }
        sources.compile()
        assertFalse(
            Mirror.reflect(Y).isAssignableFrom(
                Mirror.reflect(types["? extends Y"])
            )
        )
    }

    @Test
    @DisplayName("A class should be assignable from a wildcard with a more subclass as a upper bound")
    fun classAssignableFromSubclassUpperBoundedWildcard() {
        val X by sources.add("X", "class X {}")
        val Y by sources.add("Y", "class Y extends X {}")
        val types = sources.types {
            +"? extends Y"
        }
        sources.compile()
        assertFalse(
            Mirror.reflect(X).isAssignableFrom(
                Mirror.reflect(types["? extends Y"])
            )
        )
    }

    @Test
    fun `classes should be assignable from type variables with them in their bound`() {
        val I by sources.add("I", "interface I {}")
        val types = sources.types {
            block("T extends I") {}
            +"I"
        }
        sources.compile()
        assertTrue(
            Mirror.reflect(types["I"]).isAssignableFrom(
                Mirror.reflect(types["T extends I"])
            )
        )
    }

    @Test
    fun `class 'isAssignableFrom' with a self-referential type variable should not infinitely recurse`() {
        val I by sources.add("I", "interface I<K> {}")
        val types = sources.types {
            block("T extends I<T>") {}
            +"I<I>"
        }
        sources.compile()
        assertDoesNotThrow {
            Mirror.reflect(types["I<I>"]).isAssignableFrom(
                Mirror.reflect(types["T extends I<T>"])
            )
        }
    }

    @Test
    @DisplayName("The void mirror should be assignable from itself")
    fun voidAssignableFromSelf() {
        assertTrue(
            Mirror.types.void.isAssignableFrom(
                Mirror.types.void
            )
        )
    }

    @Test
    @DisplayName("The void mirror should not be assignable from other types")
    fun voidNotAssignableFromOthers() {
        val X by sources.add("X", "class X {}")
        val Y by sources.add("Y", "class Y extends X {}")
        val Generic by sources.add("Generic", "class Generic<T> {}")
        val types = sources.types {
            +"int[]"
            +"Generic<X>"
            +"? super Y"
            +"? extends X"
        }
        sources.compile()
        assertFalse(
            Mirror.types.void.isAssignableFrom(
                Mirror.types.any
            )
        )
        assertFalse(
            Mirror.types.void.isAssignableFrom(
                Mirror.reflect(types["int[]"])
            )
        )
        assertFalse(
            Mirror.types.void.isAssignableFrom(
                Mirror.reflect(types["Generic<X>"])
            )
        )
        assertFalse(
            Mirror.types.void.isAssignableFrom(
                Mirror.reflect(types["? super Y"])
            )
        )
        assertFalse(
            Mirror.types.void.isAssignableFrom(
                Mirror.reflect(types["? extends X"])
            )
        )
    }

    @Test
    @DisplayName("The Object mirror should be assignable from wildcard mirrors")
    fun objectAssignableFromWildcard() {
        val X by sources.add("X", "class X {}")
        val Y by sources.add("Y", "class Y extends X {}")
        val types = sources.types {
            +"? super Y"
            +"? extends X"
        }
        sources.compile()
        assertTrue(
            Mirror.reflect<Any>().isAssignableFrom(
                Mirror.reflect(types["? super Y"])
            )
        )
        assertTrue(
            Mirror.reflect<Any>().isAssignableFrom(
                Mirror.reflect(types["? extends X"])
            )
        )
    }

    @Test
    @DisplayName("Lower-bounded wildcard mirrors should be assignable from mirrors of their supertype")
    fun lowerWildcardAssignableFromSupertype() {
        val X by sources.add("X", "class X {}")
        val Y by sources.add("Y", "class Y extends X {}")
        val types = sources.types {
            +"? super Y"
        }
        sources.compile()
        assertTrue(
            Mirror.reflect(types["? super Y"]).isAssignableFrom(
                Mirror.reflect(X)
            )
        )
    }

    @Test
    @DisplayName("Lower-bounded wildcard mirrors should be assignable from mirrors of their bound")
    fun lowerWildcardAssignableFromBound() {
        val X by sources.add("X", "class X {}")
        val Y by sources.add("Y", "class Y extends X {}")
        val types = sources.types {
            +"? super Y"
        }
        sources.compile()
        assertTrue(
            Mirror.reflect(types["? super Y"]).isAssignableFrom(
                Mirror.reflect(Y)
            )
        )
    }

    @Test
    @DisplayName("Lower-bounded wildcard mirrors should not be assignable from mirrors of their subtypes")
    fun lowerWildcardNotAssignableFromSubtype() {
        val X by sources.add("X", "class X {}")
        val Y by sources.add("Y", "class Y extends X {}")
        val Generic by sources.add("Generic", "class Generic<T> {}")
        val types = sources.types {
            +"? super X"
        }
        sources.compile()
        assertFalse(
            Mirror.reflect(types["? super X"]).isAssignableFrom(
                Mirror.reflect(Y)
            )
        )
    }

    @Test
    @DisplayName("Upper-bounded wildcard mirrors should not be assignable from mirrors of their supertype")
    fun upperWildcardNotAssignableFromSupertype() {
        val X by sources.add("X", "class X {}")
        val Y by sources.add("Y", "class Y extends X {}")
        val types = sources.types {
            +"? extends Y"
        }
        sources.compile()
        assertFalse(
            Mirror.reflect(types["? extends Y"]).isAssignableFrom(
                Mirror.reflect(X)
            )
        )
    }

    @Test
    @DisplayName("Upper-bounded wildcard mirrors should be assignable from mirrors of their bound")
    fun upperWildcardAssignableFromBound() {
        val X by sources.add("X", "class X {}")
        val types = sources.types {
            +"? extends X"
        }
        sources.compile()
        assertTrue(
            Mirror.reflect(types["? extends X"]).isAssignableFrom(
                Mirror.reflect(X)
            )
        )
    }

    @Test
    @DisplayName("Upper-bounded wildcard mirrors should be assignable from mirrors of their subtypes")
    fun upperWildcardAssignableFromSubtype() {
        val X by sources.add("X", "class X {}")
        val Y by sources.add("Y", "class Y extends X {}")
        val Generic by sources.add("Generic", "class Generic<T> {}")
        val types = sources.types {
            +"? extends X"
        }
        sources.compile()
        assertTrue(
            Mirror.reflect(types["? extends X"]).isAssignableFrom(
                Mirror.reflect(Y)
            )
        )
    }
    @Test
    fun `type variables should not be assignable from classes that implement their bounds`() {
        val I by sources.add("I", "interface I {}")
        val X by sources.add("X", "class X implements I {}")
        val types = sources.types {
            block("T extends I") {
            }
        }
        sources.compile()
        assertFalse(
            Mirror.reflect(types["T extends I"]).isAssignableFrom(
                Mirror.reflect(X)
            )
        )
    }

    @Test
    @DisplayName("Variable mirrors should be assignable from themselves")
    fun variableAssignableFromSelf() {
        val types = sources.types {
            block("T") {
            }
        }
        sources.compile()
        assertTrue(
            Mirror.reflect(types["T"]).isAssignableFrom(
                Mirror.reflect(types["T"])
            )
        )
    }

    @Test
    fun `type variables should not be assignable from unrelated variables`() {
        val I by sources.add("I", "interface I {}")
        val I2 by sources.add("I2", "interface I2 extends I {}")
        val types = sources.types {
            block("T", "T2") {
            }
        }
        sources.compile()
        assertFalse(
            Mirror.reflect(types["T"]).isAssignableFrom(
                Mirror.reflect(types["T2"])
            )
        )
    }

    @Test
    fun `type variables should not be assignable from unrelated variables with compatible bounds`() {
        val I by sources.add("I", "interface I {}")
        val types = sources.types {
            block("T extends I", "T2 extends I") {
            }
        }
        sources.compile()
        assertFalse(
            Mirror.reflect(types["T extends I"]).isAssignableFrom(
                Mirror.reflect(types["T2 extends I"])
            )
        )
    }

    @Test
    fun `type variables should be assignable from variables bounded by them`() {
        val types = sources.types {
            block("T") {
                block("T2 extends T") {}
            }
        }
        sources.compile()
        assertTrue(
            Mirror.reflect(types["T"]).isAssignableFrom(
                Mirror.reflect(types["T2 extends T"])
            )
        )
    }

    @Test
    fun `type variables should not be assignable from variables in their bounds`() {
        val types = sources.types {
            block("T") {
                block("T2 extends T") {}
            }
        }
        sources.compile()
        assertFalse(
            Mirror.reflect(types["T2 extends T"]).isAssignableFrom(
                Mirror.reflect(types["T"])
            )
        )
    }

    @Test
    fun `type variables should not be assignable from wildcards with compatible bounds`() {
        val I by sources.add("I", "interface I {}")
        val types = sources.types {
            block("T extends I") {
            }
            +"? extends I"
        }
        sources.compile()
        assertFalse(
            Mirror.reflect(types["T extends I"]).isAssignableFrom(
                Mirror.reflect(types["? extends I"])
            )
        )
    }

    @Test
    fun `type variables should be assignable from wildcards with them in their upper bounds`() {
        val I by sources.add("I", "interface I {}")
        val types = sources.types {
            block("T extends I") {
                +"? extends T"
            }
        }
        sources.compile()
        assertTrue(
            Mirror.reflect(types["T extends I"]).isAssignableFrom(
                Mirror.reflect(types["? extends T"])
            )
        )
    }

    @Test
    fun `type variables should not be assignable from wildcards with them in their lower bounds`() {
        val I by sources.add("I", "interface I {}")
        val types = sources.types {
            block("T extends I") {
                +"? super T"
            }
        }
        sources.compile()
        assertFalse(
            Mirror.reflect(types["T extends I"]).isAssignableFrom(
                Mirror.reflect(types["? super T"])
            )
        )
    }
}