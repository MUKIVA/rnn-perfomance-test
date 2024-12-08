package gen

import utils.PerlinNoise1D

class PerlinNoiseIterateDecorator(
    seed: Long,
    private val iterateGen: IIterateGen<Double>,
    private val perlinScale: Double
) : IIterateGen<Double> {

    private val perlinNoise1D = PerlinNoise1D(seed)

    override val iteration: UInt
        get() = iterateGen.iteration

    override fun generateNext(): Double {
        val value = iterateGen.generateNext()
        val perlin = perlinNoise1D.noise(value)
        println("VALUE: $value; PERLIN: ${perlin * perlinScale};")
        return value + perlin * perlinScale
    }

}