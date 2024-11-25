abstract class AbstractFactory<T, I> {

    abstract fun create(input: I): T

}