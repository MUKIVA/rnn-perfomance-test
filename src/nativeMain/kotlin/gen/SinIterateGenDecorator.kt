package gen

import kotlin.math.PI
import kotlin.math.sin

class SinIterateGenDecorator(
    private val gen: IIterateGen<Double>,
    private val amplitude: Double = 1.0,
    private val period: Double = 10.0
) : IIterateGen<Double> {
    override val iteration: UInt
        get() = gen.iteration

    override fun generateNext(): Double {
        val value = gen.generateNext()
        val s = sin((2 * PI / period) * iteration.toDouble()) * amplitude
        return value + s
    }

}