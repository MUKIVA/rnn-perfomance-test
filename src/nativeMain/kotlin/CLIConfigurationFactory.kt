import kotlinx.coroutines.*
import utils.ILogger
import kotlin.experimental.ExperimentalNativeApi
import kotlin.concurrent.AtomicReference
import kotlin.random.Random

class CLIConfigurationFactory(
    private val logger: ILogger
) : AbstractFactory<Application.IConfiguration, List<String>>() {

    interface IArgHandler<TResult> {
        fun handle(value: String, onFinish: (TResult) -> Unit)
        fun isRunning(): Boolean
    }

    private data class Configuration(
        override val frequency: Double,
        override val timeDuration: Double,
        override val perlinScale: Double,
        override val seed: Long
    ) : Application.IConfiguration

    private val argsMap: AtomicReference<MutableMap<ArgName, Any>> = AtomicReference(mutableMapOf())
    private val runningHandlers: MutableList<IArgHandler<*>> = mutableListOf()

    override fun create(input: List<String>): Application.IConfiguration {
        input.onEachWithSplit { (key, value) -> dispatchArg(key, value) }

        logger.log("[PREPARE] Handle Arguments...")
        while (runningHandlers.any { it.isRunning() }) {
            // Nothing to do
        }

        return Configuration(
            frequency = argsMap.value[ArgName.FREQUENCY] as? Double ?: 5.0,
            timeDuration = argsMap.value[ArgName.TIME_DURATION] as? Double ?: 1000.0,
            perlinScale = argsMap.value[ArgName.PERLIN_SCALE] as? Double ?: 1.0,
            seed = argsMap.value[ArgName.SEED] as? Long ?: Random.nextLong()
        )
    }

    private fun saveArg(name: ArgName, value: Any) {
        argsMap.value.set(
            key = name,
            value = value
        )
    }

    private fun dispatchArg(key: String, value: String) = when (val argName = ArgName.fromName(key)) {
        ArgName.FREQUENCY -> FrequencyHandler.handle(value) {
            saveArg(argName, it)
        }.also { runningHandlers.add(FrequencyHandler) }
        ArgName.TIME_DURATION -> TimeDurationHandler.handle(value) {
            saveArg(argName, it)
        }.also { runningHandlers.add(TimeDurationHandler) }
        ArgName.PERLIN_SCALE -> PerlinScaleHandler.handle(value) {
            saveArg(argName, it)
        }.also { runningHandlers.add(PerlinScaleHandler) }
        ArgName.SEED -> SeedHandler.handle(value) {
            saveArg(argName, value)
        }.also { runningHandlers.add(SeedHandler) }
    }


    private inline fun Iterable<String>.onEachWithSplit(action: (Pair<String, String>) -> Unit) {
        for (element in this) action(handleKeyValueArg(SEPARATOR, element))
    }

    private fun handleKeyValueArg(sep: String, arg: String): Pair<String, String> {
        val keyValue = arg.split(sep)
        assertListContainsTwoElements(keyValue)
        val key = keyValue[0]
        val value = keyValue[1]
        return key to value
    }

    @OptIn(ExperimentalNativeApi::class)
    private fun assertListContainsTwoElements(list: List<String>) {
        assert(list.size == 2)
    }

    private enum class ArgName(
        val fullName: String,
        val shortName: String,
    ) {
        FREQUENCY(
            fullName = "frequency",
            shortName = "f",
        ),
        TIME_DURATION(
            fullName = "duration",
            shortName = "d",
        ),
        PERLIN_SCALE(
            fullName = "perlin",
            shortName = "p",
        ),
        SEED(
            fullName = "seed",
            shortName = "s",
        );

        companion object {
            fun fromName(name: String): ArgName = ArgName.entries.find {
                name == it.fullName || name == it.shortName
            } ?: throw IllegalArgumentException("Illegal argument name: \"$name\"")
        }
    }

    companion object {
        private const val SEPARATOR = "="
    }
}

abstract class AsyncHandler<TResult> : CLIConfigurationFactory.IArgHandler<TResult> {
    protected val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
}

object FrequencyHandler : AsyncHandler<Double>() {

    private var job: Job? = null

    override fun handle(value: String, onFinish: (Double) -> Unit) {
        job = scope.launch {
            val result = value.toDouble()
            onFinish(result)
        }
    }

    override fun isRunning(): Boolean {
        return job?.isActive ?: false
    }
}

object TimeDurationHandler : AsyncHandler<Double>() {

    private var job: Job? = null

    override fun handle(value: String, onFinish: (Double) -> Unit) {
        job = scope.launch {
            val result = value.toDouble()
            onFinish(result)
        }
    }

    override fun isRunning(): Boolean {
        return job?.isActive ?: false
    }
}

object PerlinScaleHandler : AsyncHandler<Double>() {

    private var job: Job? = null

    override fun handle(value: String, onFinish: (Double) -> Unit) {
        job = scope.launch {
            val result = value.toDouble()
            onFinish(result)
        }
    }

    override fun isRunning(): Boolean {
        return job?.isActive ?: false
    }
}

object SeedHandler : AsyncHandler<Int>() {

    private var job: Job? = null

    override fun handle(value: String, onFinish: (Int) -> Unit) {
        job = scope.launch {
            val result = value.toInt()
            onFinish(result)
        }
    }

    override fun isRunning(): Boolean {
        return job?.isActive ?: false
    }
}