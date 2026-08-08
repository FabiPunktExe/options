package de.fabiexe.options

internal actual fun getEnv(name: String): String? = System.getenv(name)