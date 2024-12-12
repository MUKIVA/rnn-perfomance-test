package utils

import kotlin.math.pow
import kotlin.random.Random

class PerlinNoise1D(seed: Int) {

    private val permutationTable = IntArray(512)

    init {
        val random = Random(seed)
        val p = IntArray(256) { random.nextInt(0, 256) }
        for (i in 0 until 256) {
            permutationTable[i] = p[i]
            permutationTable[i + 256] = p[i]
        }
    }

    // Функция градиента (вычисление скалярного произведения градиента и вектора)
    private fun grad(hash: Int, x: Double): Double {
        val h = hash and 15
        val u = if (h < 8) x else -x
        return u * (if (h and 8 != 0) -1 else 1)
    }

    // Функция линейной интерполяции
    private fun lerp(a: Double, b: Double, t: Double): Double {
        return a + t * (b - a)
    }

    // Функция сглаживания
    private fun fade(t: Double): Double {
        return t * t * t * (t * (t * 6 - 15) + 10)
    }

    // Основная функция для вычисления шума в 1D
    fun noise(x: Double): Double {
        // Преобразуем координаты в целочисленные индексы
        val X = x.toInt() and 255

        // Вычисляем расстояние внутри ячейки
        val fx = x - x.toInt()

        // Вычисляем сглаживание
        val u = fade(fx)

        // Индексы для соседних узлов
        val aa = permutationTable[X] // Левый узел
        val ab = permutationTable[X + 1] // Правый узел

        // Рассчитываем градиенты для обоих узлов
        val gradAA = grad(aa, fx)
        val gradAB = grad(ab, fx - 1)

        // Интерполируем значения
        return lerp(gradAA, gradAB, u)
    }
}

class PerlinNoiseGenerator(seed: Int, private val boundary: Int = 10) {
    private var random = Random(seed)
    private val noise = DoubleArray(boundary) {
        random.nextDouble()
    }

    fun perlin(x: Double, persistence: Double = 0.5, numberOfOctaves: Int = 8): Double {
        var total = 0.0
        var amplitudeSum = 0.0 //used for normalizing results to 0.0 - 1.0
        for (i in 0 until numberOfOctaves) {
            val amplitude = persistence.pow(i) // height of the crests
            val frequency = 2.0.pow(i) // frequency (number of crests per unit distance) doubles per octave
            val octave = amplitude * noise(x * frequency)
            total += octave
            amplitudeSum += amplitude
        }
        return total / amplitudeSum
    }

    private fun noise(t: Double): Double {
        val x = t.toInt()
        val x0 = x % boundary
        val x1 = if (x0 == boundary - 1) 0 else x0 + 1
        val between = t - x

        val y0 = noise[x0]
        val y1 = noise[x1]
        return lerp(y0, y1, between)
    }

    private fun lerp(a: Double, b: Double, alpha: Double): Double {
        return a + alpha * (b - a)
    }
}
