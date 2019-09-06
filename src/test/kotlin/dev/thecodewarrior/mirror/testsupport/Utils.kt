package dev.thecodewarrior.mirror.testsupport

import org.opentest4j.AssertionFailedError

/**
 * _Asserts_ that [expected] and [actual] have the same length and that the elements of each refer to the same objects.
 */
fun assertSameList(expected: List<Any?>, actual: List<Any?>) {
    if(expected.size != actual.size || expected.zip(actual).any { it.first !== it.second })
        throw AssertionFailedError("",
            expected.map { it.toStringWithIdentity() },
            actual.map { it.toStringWithIdentity() }
        )
}

/**
 * _Asserts_ that [expected] and [actual] have the same size and that each element in [expected] has exactly one
 * matching element in [actual] that refers to the same object (multiple copies in one will require the same number of
 * copies in the other)
 */
fun assertSameSet(expected: List<Any?>, actual: List<Any?>) {
    val remainingExpected = expected.toMutableList()
    val remainingActual = actual.toMutableList()

    val orderedExpected = mutableListOf<Any?>()
    val orderedActual = mutableListOf<Any?>()

    remainingExpected.removeIf { expect ->
        val i = remainingActual.indexOfFirst { it === expect }
        if(i != -1) {
            orderedActual.add(remainingActual.removeAt(i))
            orderedExpected.add(expect)
            true
        } else {
            false
        }
    }

    orderedExpected.addAll(remainingExpected)
    orderedActual.addAll(remainingActual)

    if(expected.size != actual.size || orderedExpected.zip(orderedActual).any { it.first !== it.second })
        throw AssertionFailedError("",
            orderedExpected.map { it.toStringWithIdentity() },
            orderedActual.map { it.toStringWithIdentity() }
        )
}

/**
 * _Asserts_ that [expected] and [actual] have the same size and that each element in [expected] has exactly one
 * equal element in [actual] (multiple copies in one will require the same number of
 * copies in the other)
 */
fun assertSetEquals(expected: List<Any?>, actual: List<Any?>) {
    val remainingExpected = expected.toMutableList()
    val remainingActual = actual.toMutableList()

    val orderedExpected = mutableListOf<Any?>()
    val orderedActual = mutableListOf<Any?>()

    remainingExpected.removeIf { expect ->
        val i = remainingActual.indexOfFirst { it == expect }
        if(i != -1) {
            orderedActual.add(remainingActual.removeAt(i))
            orderedExpected.add(expect)
            true
        } else {
            false
        }
    }

    orderedExpected.addAll(remainingExpected)
    orderedActual.addAll(remainingActual)

    if(expected.size != actual.size || orderedExpected.zip(orderedActual).any { it.first != it.second })
        throw AssertionFailedError("",
            orderedExpected,
            orderedActual
        )
}


fun assertInstanceOf(expected: Class<*>, actual: Any?) {
    if(!expected.isInstance(actual))
        throw AssertionFailedError("",
            expected.canonicalName,
            actual.toStringWithIdentity()
        )
}

inline fun <reified T> assertInstanceOf(actual: Any?) {
    assertInstanceOf(T::class.java, actual)
}

private fun Any?.toStringWithIdentity(): String {
    if(this == null) return "null"

    val hashCode = System.identityHashCode(this).toString(16)
    val value = this.toString()
    if (value.endsWith(hashCode))
        return value
    else
        return "$value@$hashCode"
}

fun nothing(): Nothing {
    throw NotImplementedError("Nothing")
}