package com.teamwizardry.mirror.member

import com.teamwizardry.mirror.MirrorCache
import com.teamwizardry.mirror.type.ClassMirror
import java.lang.reflect.Member

abstract class MemberMirror internal constructor(
    internal val cache: MirrorCache,
    _enclosing: ClassMirror?
) {
    abstract val java: Member

    val declaringClass: ClassMirror by lazy {
        _enclosing ?: cache.types.reflect(java.declaringClass) as ClassMirror
    }

    abstract fun withDeclaringClass(enclosing: ClassMirror?): MemberMirror
}