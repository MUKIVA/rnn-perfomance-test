package gen

class OffsetIterateGenDecorator(
    private val iterateGen: IIterateGen<Double>,
    private val sign: Sign,
    private val step: () -> Double,
) : IIterateGen<Double> {

    enum class Sign {
        POSITIVE,
        NEGATIVE
    }

    private var offset: Double = 0.0

    override val iteration: UInt
        get() = iterateGen.iteration

    override fun generateNext(): Double {
        val value = iterateGen.generateNext()
        return (value + offset).apply {
            when (sign) {
                Sign.POSITIVE -> offset += step()
                Sign.NEGATIVE -> offset -= step()
            }
        }
    }

}