package utils

import kotlin.random.Random

class PerlinNoise1D(
    private val seed: Long
) {

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
