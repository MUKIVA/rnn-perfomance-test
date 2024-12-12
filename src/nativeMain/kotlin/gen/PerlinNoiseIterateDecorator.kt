package gen

import utils.PerlinNoiseGenerator

class PerlinNoiseIterateDecorator(
    seed: Int,
    private val iterateGen: IIterateGen<Double>,
    private val perlinScale: Double,
    private val iterationFraction: Double
) : IIterateGen<Double> {

    private val perlinNoise = PerlinNoiseGenerator(seed)

    override val iteration: UInt
        get() = iterateGen.iteration

    override fun generateNext(): Double {
        val value = iterateGen.generateNext()
        val perlin = perlinNoise.perlin(iteration.toDouble() / iterationFraction)
        return value + perlin * perlinScale
    }

}