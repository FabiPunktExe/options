package de.fabiexe.options

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKStringFromUtf8
import platform.posix.getenv

@OptIn(ExperimentalForeignApi::class)
internal actual fun getEnv(name: String): String? = getenv(name)?.toKStringFromUtf8()