import okio.FileSystem
import okio.Path.Companion.toPath
import okio.buffer

fun main(args: Array<String>) {
    val path = "./out.csv".toPath()
    val sink =  FileSystem.SYSTEM
        .sink(path)
        .buffer()

    val configFactory = CLIConfigurationFactory()

    Application(
        config = configFactory.create(args.toList())
    ).run(sink)

    sink.close()
}