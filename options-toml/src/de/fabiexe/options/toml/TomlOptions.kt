@file:JvmName("TomlOptions")
package de.fabiexe.options.toml

import com.akuleshov7.ktoml.TomlInputConfig
import com.akuleshov7.ktoml.parsers.TomlParser
import com.akuleshov7.ktoml.tree.nodes.*
import com.akuleshov7.ktoml.tree.nodes.pairs.values.TomlValue
import de.fabiexe.options.InternalOptionsApi
import de.fabiexe.options.Options
import kotlin.jvm.JvmName

fun Options.readOptionsFromToml(toml: String) {
    val tomlFile = try {
        TomlParser(TomlInputConfig()).parseString(toml)
    } catch (e: Exception) {
        validationsErrors += e
        return
    }

    tomlFile.children.forEach(::applyTomlValue)
}

@OptIn(InternalOptionsApi::class)
private fun Options.applyTomlValue(tomlNode: TomlNode, path: String? = null) {
    val optionName = if (path != null) "$path.${tomlNode.name}" else tomlNode.name
    when (tomlNode) {
        is TomlKeyValuePrimitive -> {
            options.find { it.longName == optionName }?.let { option ->
                readOption(option, tomlNode.value.content.toString())
            }
        }
        is TomlKeyValueArray -> {
            options.find { it.longName == optionName }?.let { option ->
                for (value in tomlNode.value.content as List<*>) {
                    readOption(option, (value as TomlValue).content.toString())
                }
            }
        }
        is TomlTable, is TomlInlineTable -> {
            for (child in tomlNode.children) {
                applyTomlValue(child, optionName)
            }
        }
        else -> error("Unsupported TOML node type: ${tomlNode::class}")
    }
}