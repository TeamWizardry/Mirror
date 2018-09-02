package com.teamwizardry.mirror.type

import com.teamwizardry.mirror.Mirror
import com.teamwizardry.mirror.testsupport.Interface1
import com.teamwizardry.mirror.testsupport.Interface2
import com.teamwizardry.mirror.testsupport.assertSameList
import com.teamwizardry.mirror.typeParameter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class VariableMirrorTest {

    @Test
    fun getBounds_onUnboundedType_shouldReturnListOfObject() {
        class TypeVariableHolder<T>
        val typeVariable = TypeVariableHolder::class.java.typeParameter(0)!!
        val type = Mirror.reflect(typeVariable) as VariableMirror
        assertEquals(listOf(Mirror.reflect<Any>()), type.bounds)
    }

    @Test
    fun getBounds_onTypeWithSingleBound_shouldReturnListOfBound() {
        class TypeVariableHolder<T: Interface1>
        val typeVariable = TypeVariableHolder::class.java.typeParameter(0)!!
        val type = Mirror.reflect(typeVariable) as VariableMirror
        assertEquals(listOf(Mirror.reflect<Interface1>()), type.bounds)
    }

    @Test
    fun getBounds_onTypeWithMultipleBounds_shouldReturnListOfBoundsInSourceOrder() {
        class TypeVariableHolder<T> where T: Interface1, T: Interface2
        val typeVariable = TypeVariableHolder::class.java.typeParameter(0)!!
        val type = Mirror.reflect(typeVariable) as VariableMirror
        assertSameList(listOf(Mirror.reflect<Interface1>(), Mirror.reflect<Interface2>()), type.bounds)
    }
}