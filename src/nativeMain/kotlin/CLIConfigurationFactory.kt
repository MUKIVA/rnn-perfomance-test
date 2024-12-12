@file:Suppress("UNCHECKED_CAST")

import descriptors.*
import kotlin.experimental.ExperimentalNativeApi
import kotlin.random.Random

class CLIConfigurationFactory : AbstractFactory<Application.IConfiguration, List<String>>() {

    interface IArgHandler<TResult> {
        fun handle(value: String): TResult
    }

    private data class Configuration(
        override val timeDuration: Double,
        override val seed: Long,
        override val description: List<IDecorationDescriptor>
    ) : Application.IConfiguration

    private val argsMap: MutableMap<ArgName, Any> = mutableMapOf()

    override fun create(input: List<String>): Application.IConfiguration {
        input.onEachWithSplit { (key, value) -> dispatchArg(key, value) }

        return Configuration(
            timeDuration = argsMap[ArgName.TIME_DURATION] as? Double ?: 100000.0,
            seed = argsMap[ArgName.SEED] as? Long ?: Random.nextLong(),
            description = argsMap[ArgName.DESCRIPTION] as? List<IDecorationDescriptor> ?: emptyList()
        )
    }

    private fun saveArg(name: ArgName, value: Any) {
        argsMap.set(
            key = name,
            value = value
        )
    }

    private fun dispatchArg(key: String, value: String) {
        when (val argName = ArgName.fromName(key)) {
            ArgName.TIME_DURATION -> TimeDurationHandler.handle(value).also {
                saveArg(argName, it)
            }
            ArgName.SEED -> SeedHandler.handle(value).also {
                saveArg(argName, it)
            }
            ArgName.DESCRIPTION -> DescriptionHandler(key).handle(value).also {
                if (argsMap[ArgName.DESCRIPTION] == null) {
                    argsMap[ArgName.DESCRIPTION] = mutableListOf<IDecorationDescriptor>()
                }
                (argsMap[ArgName.DESCRIPTION] as? MutableList<IDecorationDescriptor>)
                    ?.add(it)
            }
        }
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
        val fullName: String
    ) {
        DESCRIPTION(
            fullName = """
                SIN
                PERLIN
                SEASON
                TREND
            """.trimIndent()

        ),
        TIME_DURATION(
            fullName = "duration",
        ),
        SEED(
            fullName = "seed",
        );

        companion object {
            fun fromName(name: String): ArgName = ArgName.entries.find {
                it.fullName.contains(name)
            } ?: throw IllegalArgumentException("Illegal argument name: \"$name\"")
        }
    }

    companion object {
        private const val SEPARATOR = "="
    }
}

open class DoubleHandler : CLIConfigurationFactory.IArgHandler<Double> {
    override fun handle(value: String): Double {
        return value.toDouble()
    }
}

class DescriptionHandler(
    private val descriptorType: String
) : CLIConfigurationFactory.IArgHandler<IDecorationDescriptor> {

    private val sinDescriptorFactory: (String) -> IDecorationDescriptor = { args ->
        val splitArgs = args.split(SEPARATOR)
        SinDescriptor(
            period = splitArgs[0].toDouble(),
            amplitude = splitArgs[1].toDouble()
        )
    }

    private val perlinDescriptorFactory: (String) -> IDecorationDescriptor = { args ->
        val splitArgs = args.split(SEPARATOR)
        PerlinDescriptor(
            scale = splitArgs[0].toDouble(),
            iterationFraction = splitArgs[1].toDouble()
        )
    }

    private val seasonDescriptorFactory: (String) -> IDecorationDescriptor = { args ->
        val splitArgs = args.split(SEPARATOR)
        SeasonDescriptor(
            scale = splitArgs[0].toDouble(),
            period = splitArgs[1].toDouble()
        )
    }

    private val trendDescriptorFactory: (String) -> IDecorationDescriptor = { args ->
        val splitArgs = args.split(SEPARATOR)
        TrendDescriptor(
            angle = splitArgs[0].toDouble(),
            offset = splitArgs[1].toDouble()
        )
    }

    override fun handle(value: String): IDecorationDescriptor {
        return when (descriptorType) {
            "SIN"       -> sinDescriptorFactory(value)
            "PERLIN"    -> perlinDescriptorFactory(value)
            "SEASON"    -> seasonDescriptorFactory(value)
            "TREND"     -> trendDescriptorFactory(value)
            else -> throw IllegalArgumentException("Illegal descriptor name: \"$value\"")
        }
    }


    companion object {
        private const val SEPARATOR = ","
    }
}

object TimeDurationHandler : DoubleHandler()

object SeedHandler : DoubleHandler()