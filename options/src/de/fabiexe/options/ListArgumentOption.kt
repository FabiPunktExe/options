package de.fabiexe.options

class ListArgumentOption<T : Any>(val option: RequiredArgumentOption<T>) : Option<List<T>> {
    override val longName: String
        get() = option.longName
    override val shortName: String
        get() = option.shortName
    override val description: String
        get() = option.description
    override val argumentType: String
        get() = "${option.argumentType} (multiple)"

    internal var value: List<T> = listOf()

    override fun read(str: String) {
        value += option.parser(str)
    }

    override fun validate() {}
    override fun get(): List<T> = value
}