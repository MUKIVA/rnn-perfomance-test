package gen

class RootSequenceGen : IIterateGen<Double> {
    override val iteration: UInt
        get() = mIteration

    private var mIteration: UInt = 0U

    override fun generateNext(): Double {
        return 0.0.also {
            ++mIteration
        }
    }
}