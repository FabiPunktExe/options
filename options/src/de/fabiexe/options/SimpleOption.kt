package de.fabiexe.options

class SimpleOption(
    override val longName: String,
    override val shortName: String,
    override val description: String
): Option<Boolean> {
    override val argumentType: String?
        get() = null

    internal var present = false

    override fun read(str: String) {
        present = when (str) {
            "", "true" -> true
            "false" -> false
            else -> throw IllegalArgumentException("Invalid boolean value: $str")
        }
    }

    override fun validate() {}
    override fun get(): Boolean = present
}