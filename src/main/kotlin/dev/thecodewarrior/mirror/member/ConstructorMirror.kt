package dev.thecodewarrior.mirror.member

import dev.thecodewarrior.mirror.MirrorCache
import dev.thecodewarrior.mirror.type.ClassMirror
import dev.thecodewarrior.mirror.type.TypeMirror
import dev.thecodewarrior.mirror.utils.MethodHandleHelper
import java.lang.reflect.Constructor

//TODO tests
class ConstructorMirror internal constructor(
    cache: MirrorCache,
    override val java: Constructor<*>,
    raw: ConstructorMirror?,
    specialization: ExecutableSpecialization?
): ExecutableMirror(cache, specialization) {

    override val raw: ConstructorMirror = raw ?: this
    override val name: String = java.name
    val description: String get() = "${declaringClass.java.simpleName}(${raw.parameterTypes.joinToString(", ")})"
    val access: Modifier.Access = Modifier.Access.fromModifiers(java.modifiers)

    override fun withTypeParameters(vararg parameters: TypeMirror): ConstructorMirror {
        return super.withTypeParameters(*parameters) as ConstructorMirror
    }

    override fun withDeclaringClass(type: ClassMirror?): ConstructorMirror {
        return super.withDeclaringClass(type) as ConstructorMirror
    }

    private val wrapper by lazy {
        java.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        MethodHandleHelper.wrapperForConstructor(java as Constructor<Any>)
    }

    //TODO test
    @Suppress("UNCHECKED_CAST")
    fun <T : Any?> call(vararg args: Any?): T {
        if(args.size != parameters.size)
            throw IllegalArgumentException("Incorrect argument count (${args.size}) for constructor `$description`")
        return raw.wrapper(args as Array<Any?>) as T
    }

    @JvmSynthetic
    operator fun <T> invoke(vararg args: Any?): T = call(*args)

    override fun toString(): String {
        var str = name
        if(typeParameters.isNotEmpty()) {
            str += "<${typeParameters.joinToString(", ")}>"
        }
        str += "(${parameters.joinToString(", ")})"
        return str
    }
}