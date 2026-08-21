package de.fabiexe.options

import kotlin.reflect.KProperty

sealed interface Option<T> {
    val longName: String
    val shortName: String
    val description: String
    val argumentType: String?

    @Throws(IllegalArgumentException::class)
    fun read(str: String)

    @Throws(IllegalArgumentException::class)
    fun validate()

    @Throws(IllegalStateException::class)
    fun get(): T

    @Throws(IllegalStateException::class)
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = get()
}