package de.fabiexe.options.toml

import de.fabiexe.options.Options
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TomlOptionsTest {
    @Test
    fun testStringIntFlagFromToml() {
        val options = Options()
        val port by options.intOption("port", "p", "The port to listen on")
        val name by options.stringOption("name", "n", "The name of the server")
        val debug by options.option("debug", "d", "Enable debug mode")

        options.readOptionsFromToml(
            """
            port = 8080
            name = "SpinningFish"
            debug = true
            """.trimIndent()
        )
        assertTrue(options.validate())
        assertTrue(options.validationsErrors.isEmpty())
        assertEquals(8080, port)
        assertEquals("SpinningFish", name)
        assertTrue(debug)
    }

    @Test
    fun testDefaultedFromToml() {
        // With value, expect value
        val options1 = Options()
        val name1 by options1.stringOption("name", "n", "The name of the server")
            .defaulted("DefaultName")
        options1.readOptionsFromToml("""name = "SpinningFish"""")
        options1.validate()
        assertEquals("SpinningFish", name1)

        // Without value, expect default
        val options2 = Options()
        val name2 by options2.stringOption("name", "n", "The name of the server")
            .defaulted("DefaultName")
        options2.readOptionsFromToml("")
        options2.validate()
        assertEquals("DefaultName", name2)
    }

    @Test
    fun testOptionalFromToml() {
        // With value, expect value
        val options1 = Options()
        val name1 by options1.stringOption("name", "n", "The name of the server")
            .optional()
        options1.readOptionsFromToml("""name = "SpinningFish"""")
        options1.validate()
        assertEquals("SpinningFish", name1)

        // Without value, expect null
        val options2 = Options()
        val name2 by options2.stringOption("name", "n", "The name of the server")
            .optional()
        options2.readOptionsFromToml("")
        options2.validate()
        assertTrue(options2.validationsErrors.isEmpty())
        assertNull(name2)
    }

    @Test
    fun testFlagBoolean() {
        // true
        val options1 = Options()
        val debug1 by options1.option("debug", "d", "Enable debug mode")
        options1.readOptionsFromToml("debug = true")
        options1.validate()
        assertTrue(debug1)

        // false
        val options2 = Options()
        val debug2 by options2.option("debug", "d", "Enable debug mode")
        options2.readOptionsFromToml("debug = false")
        options2.validate()
        assertFalse(debug2)
    }

    @Test
    fun testBadIntFromToml() {
        val options = Options()
        val port by options.intOption("port", "p", "The port to listen on")
        options.readOptionsFromToml("""port = "not-a-number"""")
        options.validate()
        assertEquals(1, options.validationsErrors.size)
        assertFailsWith(IllegalStateException::class) { port }
    }

    @Test
    fun testUnknownKeyIgnored() {
        val options = Options()
        val name by options.stringOption("name", "n", "The name of the server")
            .defaulted("DefaultName")
        options.readOptionsFromToml(
            """
            unknown = 42
            name = "SpinningFish"
            """.trimIndent()
        )
        options.validate()
        assertTrue(options.validationsErrors.isEmpty())
        assertEquals("SpinningFish", name)
    }

    @Test
    fun testTable() {
        val options = Options()
        val port by options.intOption("server.port", "p", "The port of the server")
        options.readOptionsFromToml(
            """
            name = "SpinningFish"

            [server]
            port = 8080
            host = "localhost"
            """.trimIndent()
        )
        options.validate()
        assertTrue(options.validationsErrors.isEmpty())
        assertEquals(8080, port)
    }

    @Test
    fun testListToml() {
        val options = Options()
        val ports by options.intOption("ports", "p", "Ports to listen on").list()
        options.readOptionsFromToml("ports = [8080, 9090, 3000]")
        options.validate()
        assertTrue(options.validationsErrors.isEmpty())
        assertEquals(listOf(8080, 9090, 3000), ports)
    }

    @Test
    fun testMalformedTomlRecordsError() {
        val options = Options()
        val name by options.stringOption("name", "n", "The name of the server")
            .defaulted("DefaultName")
        options.readOptionsFromToml("name = \"unterminated")
        options.validate()
        assertEquals(1, options.validationsErrors.size)
        assertEquals("DefaultName", name)
    }
}