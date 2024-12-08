package gen

import kotlin.math.sin

class SinIterateGen(
    initialAngle: Double = 0.0,
    private val scale: Double = 1.0,
    private val step: Double = 1.0,
    private val offset: Double = 1.0,
) : IIterateGen<Double> {

    private var currentAngle = initialAngle

    override val iteration: UInt
        get() = mIteration

    private var mIteration: UInt = 0U

    override fun generateNext(): Double {
        val scaledOffset = offset * scale
        val scaledSin = sin(currentAngle) * scale
        return (scaledSin + scaledOffset).apply {
            currentAngle += step
            ++mIteration
        }
    }

}