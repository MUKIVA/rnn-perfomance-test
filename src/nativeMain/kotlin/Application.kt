import gen.IIterateGen
import gen.OffsetIterateGenDecorator
import gen.PerlinNoiseIterateDecorator
import gen.SinIterateGen
import okio.BufferedSink

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

        val stepCount = (config.timeDuration / config.frequency).toInt()

        repeat(stepCount) {
            val value = gen.generateNext()
            outBuffer.writeUtf8("$value$SEPARATOR")
        }
    }

    private fun createGen(): IIterateGen<Double> {
        val angle = 0.0
        val step = config.timeDuration / config.frequency

        val sinGen = SinIterateGen(
            initialAngle = angle,
            step = step
        )

        val perlinGen = PerlinNoiseIterateDecorator(
            seed = config.seed,
            iterateGen = sinGen,
            perlinScale = config.perlinScale
        )

        return OffsetIterateGenDecorator(
            iterateGen = perlinGen,
            sign = OffsetIterateGenDecorator.Sign.POSITIVE,
            step = step
        )
    }

    companion object {
        private const val SEPARATOR = ";"
    }
}