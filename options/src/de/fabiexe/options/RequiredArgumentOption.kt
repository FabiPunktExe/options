package de.fabiexe.options

class RequiredArgumentOption<T : Any>(
    val options: Options,
    override val longName: String,
    override val shortName: String,
    override val description: String,
    override val argumentType: String,
    val parser: (String) -> T,
) : Option<T> {
    internal var value: T? = null

    override fun read(str: String) {
        value = parser(str)
    }

    override fun validate() {
        if (value == null) {
            error("Option -$shortName, --$longName requires an argument of type $argumentType")
        }
    }

    override fun get(): T {
        return value ?: error("Option $longName is required but was not provided")
    }

    fun optional(): OptionalArgumentOption<T> {
        options.options -= this
        return options.option(OptionalArgumentOption(this))
    }

    fun defaulted(default: T): DefaultedArgumentOption<T> {
        options.options -= this
        return options.option(DefaultedArgumentOption(this, default))
    }

    fun list(): ListArgumentOption<T> {
        options.options -= this
        return options.option(ListArgumentOption(this))
    }
}