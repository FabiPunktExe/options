package de.fabiexe.options

import kotlin.test.*

class OptionsTest {
    @Test
    fun testOption() {
        val options1 = Options()
        val force1 by options1.option("force", "f", "Whether to force the operation")
        options1.readOptionsFromArgs(arrayOf("--force"))
        options1.validate()
        assertTrue(options1.validationsErrors.isEmpty())
        assertTrue(force1)

        val options2 = Options()
        val force2 by options2.option("force", "f", "Whether to force the operation")
        options2.readOptionsFromArgs(arrayOf("-f"))
        options2.validate()
        assertTrue(options2.validationsErrors.isEmpty())
        assertTrue(force2)

        val options3 = Options()
        val force3 by options3.option("force", "f", "Whether to force the operation")
        options3.readOptionsFromArgs(arrayOf())
        options3.validate()
        assertTrue(options3.validationsErrors.isEmpty())
        assertFalse(force3)
    }

    @Test
    fun testStringOption() {
        val options = Options()
        val name by options.stringOption("name", "n", "The name of the server")
        options.readOptionsFromArgs(arrayOf("--name", "SpinningFish"))
        options.validate()
        assertEquals("SpinningFish", name)
    }

    @Test
    fun testIntOption() {
        val options1 = Options()
        val port1 by options1.intOption("port", "p", "The port to listen on")
        options1.readOptionsFromArgs(arrayOf("--port", "8080"))
        options1.validate()
        assertTrue(options1.validationsErrors.isEmpty())
        assertEquals(8080, port1)

        val options2 = Options()
        val port2 by options2.intOption("port", "p", "The port to listen on")
        options2.readOptionsFromArgs(arrayOf("--port", "not-a-number"))
        options2.validate()
        assertEquals(1, options2.validationsErrors.size)
        assertFailsWith(IllegalStateException::class) { port2 }
    }

    @Test
    fun testRequiredOption() {
        val options1 = Options()
        val name by options1.stringOption("name", "n", "The name of the server")
        options1.readOptionsFromArgs(arrayOf("--name", "SpinningFish"))
        options1.validate()
        assertTrue(options1.validationsErrors.isEmpty())
        assertEquals("SpinningFish", name)

        val options2 = Options()
        val name2 by options2.stringOption("name", "n", "The name of the server")
        options2.readOptionsFromArgs(arrayOf())
        options2.validate()
        assertEquals(1, options2.validationsErrors.size)
        assertFailsWith(IllegalStateException::class) { name2 }
    }

    @Test
    fun testOptionalOption() {
        val options1 = Options()
        val name1 by options1.stringOption("name", "n", "The name of the server")
            .optional()
        options1.readOptionsFromArgs(arrayOf("--name", "SpinningFish"))
        options1.validate()
        assertTrue(options1.validationsErrors.isEmpty())
        assertEquals("SpinningFish", name1)

        val options2 = Options()
        val name2 by options2.stringOption("name", "n", "The name of the server")
            .optional()
        options2.readOptionsFromArgs(arrayOf())
        options2.validate()
        assertTrue(options2.validationsErrors.isEmpty())
        assertEquals(null, name2)
    }

    @Test
    fun testDefaultedOption() {
        val options1 = Options()
        val name1 by options1.stringOption("name", "n", "The name of the server")
            .defaulted("DefaultName")
        options1.readOptionsFromArgs(arrayOf("--name", "SpinningFish"))
        options1.validate()
        assertTrue(options1.validationsErrors.isEmpty())
        assertEquals("SpinningFish", name1)

        val options2 = Options()
        val name2 by options2.stringOption("name", "n", "The name of the server")
            .defaulted("DefaultName")
        options2.readOptionsFromArgs(arrayOf())
        options2.validate()
        assertTrue(options2.validationsErrors.isEmpty())
        assertEquals("DefaultName", name2)
    }

    @Test
    fun testUnknownOption() {
        val options = Options()
        options.readOptionsFromArgs(arrayOf("--unknown"))
        options.validate()
        assertEquals(1, options.validationsErrors.size)
    }
}