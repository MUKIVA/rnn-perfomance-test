package gen

class TrendIterateGenDecorator(
    private val gen: IIterateGen<Double>,
    private val tan: Double = 0.0,
    private val offset: Double = 0.0
) : IIterateGen<Double> {
    override val iteration: UInt
        get() = gen.iteration

    override fun generateNext(): Double {
        val value = gen.generateNext()
        return value + (tan * iteration.toDouble() + offset)
    }
}
