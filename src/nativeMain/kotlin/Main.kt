import okio.FileSystem
import okio.Path.Companion.toPath
import okio.buffer
import utils.ILogger

fun main(args: Array<String>) {
    val path = "./out.csv".toPath()
    val sink =  FileSystem.SYSTEM
        .sink(path)
        .buffer()

    val configFactory = CLIConfigurationFactory(
        logger = object : ILogger {
            override val log: (Any) -> Unit = { obj ->
                println(obj.toString())
            }
        }
    )

    Application(
        config = configFactory.create(args.toList())
    ).run(sink)

    sink.close()
}