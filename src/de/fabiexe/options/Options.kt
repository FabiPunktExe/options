package de.fabiexe.options

import kotlin.math.max

class Options {
    val options = mutableListOf<Option<*>>()
    private val optionsWithErrors = mutableSetOf<Option<*>>()
    val validationsErrors = mutableListOf<Exception>()

    internal fun <T : Option<*>> option(option: T): T {
        options += option
        return option
    }

    fun option(longName: String, shortName: String, description: String): SimpleOption {
        return option(SimpleOption(longName, shortName, description))
    }

    fun stringOption(longName: String, shortName: String, description: String): RequiredArgumentOption<String> {
        return option(RequiredArgumentOption(
            this,
            longName,
            shortName,
            description,
            "text",
            { it }
        ))
    }

    fun intOption(longName: String, shortName: String, description: String): RequiredArgumentOption<Int> {
        return option(RequiredArgumentOption(
            this,
            longName,
            shortName,
            description,
            "integer",
            String::toInt
        ))
    }

    fun readOptionsFromEnv(prefix: String) {
        for (option in options) {
            val value = getEnv("${prefix}_${option.longName}".uppercase().replace('-', '_'))
            if (value != null) {
                try {
                    option.read(value)
                } catch (e: Exception) {
                    if (option !in optionsWithErrors) {
                        optionsWithErrors += option
                        validationsErrors += e
                    }
                }
            }
        }
    }

    fun readOptionsFromArgs(args: Array<String>) {
        var i = 0
        var option: Option<*>? = null
        while (i < args.size) {
            val arg = args[i]
            val readOption = options.find { option -> arg == "-${option.shortName}" || arg == "--${option.longName}" }
            if (option == null) {
                if (readOption == null) {
                    validationsErrors += IllegalArgumentException("Unknown option: $arg")
                } else {
                    option = readOption
                }
            } else {
                if (readOption == null) {
                    try {
                        option.read(arg)
                    } catch (e: Exception) {
                        if (option !in optionsWithErrors) {
                            optionsWithErrors += option
                            validationsErrors += e
                        }
                    }
                    option = null
                } else {
                    try {
                        option.read("")
                    } catch (e: Exception) {
                        if (option !in optionsWithErrors) {
                            optionsWithErrors += option
                            validationsErrors += e
                        }
                    }
                    option = readOption
                }
            }
            i++
        }
        if (option != null) {
            try {
                option.read("")
            } catch (e: Exception) {
                if (option !in optionsWithErrors) {
                    optionsWithErrors += option
                    validationsErrors += e
                }
            }
        }
    }

    fun validate(): Boolean {
        for (option in options) {
            try {
                option.validate()
            } catch (e: Exception) {
                if (option !in optionsWithErrors) {
                    optionsWithErrors += option
                    validationsErrors += e
                }
            }
        }
        return validationsErrors.isEmpty()
    }

    fun printHelp() {
        println("\u001B[33mOptions:\u001B[0m")

        val lines = options.map { option ->
            val argumentTypeLength = option.argumentType?.length?.plus(3) ?: 0
            val lineLength = 1 + option.longName.length + argumentTypeLength + 4 + option.shortName.length + argumentTypeLength
            option to lineLength
        }
        val maxLineLength = lines.maxOfOrNull { it.second } ?: 0

        for ((option, lineLength) in lines) {
            val argumentType = when (option.argumentType) {
                null -> ""
                else -> " \u001B[33m<${option.argumentType}>"
            }
            val padding = " ".repeat(max(3, maxLineLength - lineLength + 3))
            println("  \u001B[94m--${option.longName}$argumentType\u001B[0m, \u001B[94m-${option.shortName}$argumentType\u001B[0m$padding${option.description}\u001B[0m")
        }
    }
}