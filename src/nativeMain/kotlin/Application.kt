import descriptors.*
import gen.*
import okio.BufferedSink
import utils.toRadians
import kotlin.math.tan

open class Application(
    private val config: IConfiguration,
) {

    interface IConfiguration {
        val timeDuration: Double
        val seed: Long
        val description: List<IDecorationDescriptor>
    }

    fun run(outBuffer: BufferedSink) {
        val gen = createGen(config.description)
        val stepCount = config.timeDuration.toInt()

        repeat(stepCount) {
            val value = gen.generateNext()
            outBuffer.writeUtf8("$value$SEPARATOR")
        }
    }

    private fun createGen(description: List<IDecorationDescriptor>): IIterateGen<Double> {
        var currentGen: IIterateGen<Double> = RootSequenceGen()
        description.onEach { descriptor ->
            currentGen = createGenDecoration(currentGen, descriptor)
        }
        return currentGen
    }

    private fun createGenDecoration(
        gen: IIterateGen<Double>,
        descriptor: IDecorationDescriptor
    ) : IIterateGen<Double> = when (descriptor) {
        is PerlinDescriptor -> PerlinNoiseIterateDecorator(
            iterateGen = gen,
            seed = config.seed.toInt(),
            perlinScale = descriptor.scale,
            iterationFraction = descriptor.iterationFraction
        )
        is SeasonDescriptor -> SeasonOffsetIterateGenDecorator(
            gen = gen,
            scale = descriptor.scale,
            period = descriptor.period
        )
        is SinDescriptor -> SinIterateGenDecorator(
            gen = gen,
            amplitude = descriptor.amplitude,
            period = descriptor.period
        )
        is TrendDescriptor -> TrendIterateGenDecorator(
            gen = gen,
            tan = tan(descriptor.angle.toRadians()),
            offset = descriptor.offset
        )
    }

    companion object {
        private const val SEPARATOR = "\n"
    }
}