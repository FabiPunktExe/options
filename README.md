# Options

A Kotlin library for parsing command-line arguments and environment variables.


## Supported platforms
- JVM
- Native
  - Windows (mingw64)
  - Linux (linuxX64, linuxArm64)
  - macOS (macosArm64)

## Usage

```kotlin
val options = Options()

// Define options using delegates
val port by options.intOption("port", "p", "The port to listen on")
    .defaulted(8080)

val name by options.stringOption("name", "n", "The name of the server")

val debug by options.option("debug", "d", "Enable debug mode")

// Read from args and env
options.readOptionsFromEnv("COMPASS")
options.readOptionsFromArgs(args)

// Validate
if (!options.validate()) {
    options.printHelp()
    return
}

println("Starting $name on port $port (debug=$debug)")
```


## Environment variables

Options are automatically mapped to uppercase environment variables.
Example: `--port` with prefix `COMPASS` maps to `COMPASS_PORT`.


## Help output

`options.printHelp()` prints a colored overview of all registered options:

```text
Options:
  -p <integer>, --port <integer>     The port to listen on
  -n <text>, --name <text>           The name of the server
  -d, --debug                        Enable debug mode
```
