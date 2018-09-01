package com.teamwizardry.mirror.type

import com.teamwizardry.mirror.MirrorCache
import com.teamwizardry.mirror.abstractionlayer.field.AbstractField
import com.teamwizardry.mirror.abstractionlayer.type.AbstractClass
import com.teamwizardry.mirror.member.FieldMirror
import com.teamwizardry.mirror.utils.lazyOrSet
import com.teamwizardry.mirror.utils.unmodifiable
import java.util.concurrent.ConcurrentHashMap

/**
 * A type mirror representing a Java class. Classes are the only type mirror that supports manual specialization as
 * they are the only types that have generic type parameters.
 *
 * @see TypeMirror
 */
class ClassMirror internal constructor(override val cache: MirrorCache, override val abstractType: AbstractClass): ConcreteTypeMirror() {
    override val rawType = abstractType.type

//region Supertypes
    /**
     * The supertype of this class. This property is `null` if this reflect represents [Object], an interface,
     * a primitive, or `void`. The returned type will be specialized based on this type's specialization and any
     * explicit parameters set in the source code.
     */
    val superclass: ClassMirror? by lazy {
        abstractType.genericSuperclass?.let {
            this.map(cache.types.reflect(it)) as ClassMirror
        }
    }

    /**
     * The list of interfaces directly implemented by this type, in the order they appear in the source code.
     * The returned type will be specialized based on this type's specialization and any explicit parameters set in the
     * source code.
     */
    val interfaces: List<ClassMirror> by lazy {
        abstractType.genericInterfaces.map {
            this.map(cache.types.reflect(it)) as ClassMirror
        }.unmodifiable()
    }

    /**
     * The list of type parameters defined by this mirror. These will be replaced when specializing, so you should use
     * [raw] to get the actual type parameters of the class as opposed to their specializations.
     */
    var typeParameters: List<TypeMirror> by lazyOrSet {
        abstractType.typeParameters.map { cache.types.reflect(it) }.unmodifiable()
    }
        internal set

    /**
     * The raw, unspecialized version of this mirror.
     */
    var raw: ClassMirror = this
        internal set

    /**
     * Specializes this class replacing its type parameters the given types. This will ripple the changes down to
     * supertypes/interfaces, method and field signatures, etc.
     *
     * @throws IllegalArgumentException if the passed type list is not the same length as [typeParameters]
     * @return The specialized version of this type
     */
    fun specialize(vararg parameters: TypeMirror): ClassMirror {
        if(parameters.size != typeParameters.size)
            throw IllegalArgumentException("Passed parameter count ${parameters.size} is different from class type " +
                    "parameter count ${typeParameters.size}")
        return cache.types.getClassMirror(raw.abstractType, parameters.toList())
    }
//endregion

//region fields
    /**
     * The fields declared directly inside of this class, any fields inherited from superclasses will not appear in
     * this list.
     *
     * This list is created when it is first accessed. This field is thread safe
     */
    val declaredFields: List<FieldMirror> by lazy {
        abstractType.declaredFields.map {
            this.map(it)
        }.unmodifiable()
    }

    private val fieldNameCache = ConcurrentHashMap<String, FieldMirror?>()

    fun field(name: String): FieldMirror? {
        return fieldNameCache.getOrPut(name) {
            var field: FieldMirror? = null
            field = field ?: declaredFields.find { it.name == name }
            field = field ?: superclass?.field(name)
            return@getOrPut field
        }
    }
//endregion

    private fun map(type: TypeMirror): TypeMirror {
        genericMapping[type]?.let {
            return it
        }

        when (type) {
            is ArrayMirror -> {
                val component = this.map(type.component)
                if(component != type.component) {
                    return cache.types.getArrayMirror(component as ConcreteTypeMirror)
                }
            }
            is ClassMirror -> {
                val parameters = type.typeParameters.map { this.map(it) }
                if(parameters != type.typeParameters) {
                    return cache.types.getClassMirror(type.raw.abstractType, parameters)
                }
            }
        }

        return type
    }

    private val genericMapping: Map<TypeMirror, TypeMirror> by lazy {
        raw.typeParameters.zip(typeParameters).associate { it }
    }

    private fun map(field: AbstractField): FieldMirror {
        val raw = cache.fields.reflect(field)
        val newType = this.map(raw.type)

        if(newType == raw.type) return raw

        return cache.fields.getFieldMirror(raw.abstractField, newType)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ClassMirror) return false

        if (cache != other.cache) return false
        if (abstractType != other.abstractType) return false
        if (typeParameters != other.typeParameters) return false

        return true
    }

    override fun hashCode(): Int {
        var result = cache.hashCode()
        result = 31 * result + abstractType.hashCode()
        result = 31 * result + typeParameters.hashCode()
        return result
    }

    /**
     * Returns a string representing the full declaration of this mirror, as opposed to [toString] which returns only
     * the type name and generic parameters
     */
    fun toFullString(): String {
        var str = ""
        str += abstractType.type.simpleName
        if(typeParameters.isNotEmpty()) {
            str += "<${typeParameters.joinToString(", ")}>"
        }
        superclass?.let { superclass ->
            str += " extends $superclass"
        }
        if(interfaces.isNotEmpty()) {
            str += " implements ${interfaces.joinToString(", ")}"
        }

        return str
    }

    override fun toString(): String {
        var str = ""
        str += abstractType.type.simpleName
        if(typeParameters.isNotEmpty()) {
            str += "<${typeParameters.joinToString(", ")}>"
        }
        return str
    }
}

