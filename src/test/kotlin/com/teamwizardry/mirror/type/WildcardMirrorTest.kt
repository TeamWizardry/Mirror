package com.teamwizardry.mirror.type

import com.teamwizardry.mirror.Mirror
import com.teamwizardry.mirror.testsupport.Interface1
import com.teamwizardry.mirror.testsupport.LowerBounded
import com.teamwizardry.mirror.testsupport.UpperBounded
import com.teamwizardry.mirror.testsupport.assertSameList
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.lang.reflect.ParameterizedType

internal class WildcardMirrorTest {

    @Test
    fun getLowerBounds_onLowerBoundedWildcard_shouldReturnLowerBound() {
        class FieldHolder(
            @JvmField
            var field: LowerBounded<Interface1>
        )

        val wildcard = (FieldHolder::class.java.getField("field").genericType as ParameterizedType).actualTypeArguments[0]
        val type = Mirror.reflect(wildcard) as WildcardMirror
        assertSameList(listOf(Mirror.reflect<Interface1>()), type.lowerBounds)
    }

    @Test
    fun getUpperBounds_onUpperBoundedWildcard_shouldReturnUpperBound() {
        class FieldHolder(
            @JvmField
            var field: UpperBounded<Interface1>
        )

        val wildcard = (FieldHolder::class.java.getField("field").genericType as ParameterizedType).actualTypeArguments[0]
        val type = Mirror.reflect(wildcard) as WildcardMirror
        assertSameList(listOf(Mirror.reflect<Interface1>()), type.upperBounds)
    }

    @Test
    fun getRaw_onWildcard_shouldReturnItself() {
        class FieldHolder(
            @JvmField
            var field: UpperBounded<Interface1>
        )

        val wildcard = (FieldHolder::class.java.getField("field").genericType as ParameterizedType).actualTypeArguments[0]
        val type = Mirror.reflect(wildcard) as WildcardMirror
        assertEquals(type, type.raw)
    }
}