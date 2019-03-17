package com.teamwizardry.mirror.member

import com.teamwizardry.mirror.MirrorCache
import com.teamwizardry.mirror.type.ClassMirror
import com.teamwizardry.mirror.type.TypeMirror
import java.lang.reflect.Method

class MethodMirror internal constructor(
    cache: MirrorCache,
    override val java: Method,
    raw: MethodMirror?,
    specialization: ExecutableSpecialization?
): ExecutableMirror(cache, raw, specialization) {

    override val raw: MethodMirror = raw ?: this
    override val name: String = java.name

    override fun specialize(vararg parameters: TypeMirror): MethodMirror {
        return super.specialize(*parameters) as MethodMirror
    }

    override fun enclose(type: ClassMirror): MethodMirror {
        return super.enclose(type) as MethodMirror
    }

    override fun toString(): String {
        var str = "$returnType $name"
        if(typeParameters.isNotEmpty()) {
            str += "<${typeParameters.joinToString(", ")}>"
        }
        str += "(${parameters.joinToString(", ")})"
        return str
    }
}