package dev.thecodewarrior.mirror.impl

import dev.thecodewarrior.mirror.impl.member.ParameterMirrorImpl
import dev.thecodewarrior.mirror.member.ExecutableMirror
import dev.thecodewarrior.mirror.member.ParameterMirror
import java.lang.reflect.Parameter
import java.util.concurrent.ConcurrentHashMap

internal class ParameterMirrorCache(private val cache: MirrorCache) {
    private val rawCache = ConcurrentHashMap<Parameter, ParameterMirror>()
    private val specializedCache = ConcurrentHashMap<Pair<ParameterMirror, ExecutableMirror>, ParameterMirror>()

    fun reflect(parameter: Parameter): ParameterMirror {
        return rawCache.getOrPut(parameter) {
            ParameterMirrorImpl(cache, null, null, parameter)
        }
    }

    fun specialize(parameter: ParameterMirror, executable: ExecutableMirror): ParameterMirror {
        val raw = parameter.raw
        return specializedCache.getOrPut(raw to executable) {
            ParameterMirrorImpl(cache, raw as ParameterMirrorImpl, executable, raw.java)
        }
    }
}
