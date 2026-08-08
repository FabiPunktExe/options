package de.fabiexe.options

class OptionalArgumentOption<T : Any>(val option: RequiredArgumentOption<T>) : Option<T?> {
    override val longName: String
        get() = option.longName
    override val shortName: String
        get() = option.shortName
    override val description: String
        get() = option.description
    override val argumentType: String
        get() = "${option.argumentType} (optional)"

    override fun read(str: String) {
        option.read(str)
    }

    override fun validate() {}
    override fun get(): T? = option.value
}