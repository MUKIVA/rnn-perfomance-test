import gen.*
import okio.BufferedSink
import kotlin.math.PI

class Application(
    private val config: IConfiguration,
) {

    interface IConfiguration {
        val frequency: Double
        val timeDuration: Double
        val perlinScale: Double
        val seed: Long
    }

    fun run(outBuffer: BufferedSink) {
        val gen = createGen()

        val stepCount = config.timeDuration.toInt()

        repeat(stepCount) {
            val value = gen.generateNext()
            outBuffer.writeUtf8("$value$SEPARATOR")
        }
    }

    private fun createGen(): IIterateGen<Double> {
        val angle = 0.0
        val mainStep = PI / config.frequency

        val sinGen = SinIterateGen(
            initialAngle = angle,
            step = mainStep
        )

        val perlinGen = PerlinNoiseIterateDecorator(
            seed = config.seed,
            iterateGen = sinGen,
            perlinScale = config.perlinScale
        )

        return SeasonOffsetIterateGenDecorator(
            gen = perlinGen,
            scale = 0.5,
            step = PI / (config.timeDuration / SEASON_COUNT),
        )
    }

    companion object {
        private const val SEPARATOR = "\n"

        private const val SEASON_COUNT = 4
    }
}