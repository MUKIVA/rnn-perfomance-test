package utils

import platform.posix.floor
import kotlin.random.Random

class PerlinNoise(seed: Int) {
    private val permutation: IntArray

    init {
        val random = Random(seed)
        val p = IntArray(256) { it }
        for (i in p.indices) {
            val j = random.nextInt(p.size)
            val temp = p[i]
            p[i] = p[j]
            p[j] = temp
        }
        permutation = IntArray(512) { p[it % 256] }
    }

    private fun fade(t: Double): Double = t * t * t * (t * (t * 6 - 15) + 10)

    private fun lerp(t: Double, a: Double, b: Double): Double = a + t * (b - a)

    private fun grad(hash: Int, x: Double, y: Double, z: Double): Double {
        val h = hash and 15
        val u = if (h < 8) x else y
        val v = if (h < 4) y else if (h == 12 || h == 14) x else z
        return (if ((h and 1) == 0) u else -u) + (if ((h and 2) == 0) v else -v)
    }

    fun noise(x: Double, y: Double, z: Double = 0.0): Double {
        // Добавляем небольшой сдвиг, чтобы избежать "цельных координат"
        val offset = 1e-6
        val xf = x + offset
        val yf = y + offset
        val zf = z + offset

        val X = floor(xf).toInt() and 255
        val Y = floor(yf).toInt() and 255
        val Z = floor(zf).toInt() and 255

        val dx = xf - floor(xf)
        val dy = yf - floor(yf)
        val dz = zf - floor(zf)

        val u = fade(dx)
        val v = fade(dy)
        val w = fade(dz)

        val aaa = permutation[permutation[permutation[X] + Y] + Z]
        val aba = permutation[permutation[permutation[X] + Y + 1] + Z]
        val aab = permutation[permutation[permutation[X] + Y] + Z + 1]
        val abb = permutation[permutation[permutation[X] + Y + 1] + Z + 1]
        val baa = permutation[permutation[permutation[X + 1] + Y] + Z]
        val bba = permutation[permutation[permutation[X + 1] + Y + 1] + Z]
        val bab = permutation[permutation[permutation[X + 1] + Y] + Z + 1]
        val bbb = permutation[permutation[permutation[X + 1] + Y + 1] + Z + 1]

        val x1 = lerp(u, grad(aaa, dx, dy, dz), grad(baa, dx - 1, dy, dz))
        val x2 = lerp(u, grad(aba, dx, dy - 1, dz), grad(bba, dx - 1, dy - 1, dz))
        val y1 = lerp(v, x1, x2)

        val x3 = lerp(u, grad(aab, dx, dy, dz - 1), grad(bab, dx - 1, dy, dz - 1))
        val x4 = lerp(u, grad(abb, dx, dy - 1, dz - 1), grad(bbb, dx - 1, dy - 1, dz - 1))
        val y2 = lerp(v, x3, x4)

        return lerp(w, y1, y2) // Возвращаем значение в диапазоне [-1, 1]
    }
}
