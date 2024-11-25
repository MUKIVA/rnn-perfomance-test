package gen

interface IIterateGen<T : Number> {

    val iteration: UInt

    fun generateNext(): T
}