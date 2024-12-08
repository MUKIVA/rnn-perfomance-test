package gen

import kotlin.math.sin

class SeasonOffsetIterateGenDecorator(
    private val gen: IIterateGen<Double> = object : IIterateGen<Double> {
        override val iteration: UInt
            get() = 0U

        override fun generateNext(): Double {
            return 0.0
        }
    },
    private val scale: Double,
    private val step: Double,
) : IIterateGen<Double> {
    override val iteration: UInt
        get() = gen.iteration

    private var mAngle: Double = 0.0

    override fun generateNext(): Double {
        val value = gen.generateNext()
        val sin = (sin(mAngle) * scale).apply {
            mAngle += step
        }
        return value + sin
    }
}