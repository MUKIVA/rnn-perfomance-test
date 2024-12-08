# rnn-performance-test

# Зависимости
- Java
- Python 3.12

# Как использовать

1) `git clone https://github.com/MUKIVA/rnn-performance-test.git`
2) Для сборки генератора последовательностей ./gradlew nativeBinaries
3) Исполняемый файл будет лежать в папке `$PROJECT_ROOT/build/bin/native/releaseExecutable`
4) Можно использовать несколько флагов:
   - `-d` Задает продолжительность, то есть сколько шагов будет сгенерировано
   - `-f` Задает с какой долей от числа PI будет изменяться значение. Чем больше задан параметр, тем меньше будет изменяться число
   - `-s` Задает сид для генерации шума Перлина
   - `-p` Задает силу воздействия шума на значения
5) Запустите программу с нужными параметрами. Результат должен поместиться рядом с исполняемым файлом в `out.csv`
6) Переместите полученную последовательность по пути `python/rnn_performance_test`
7) Откройте `main.ipynb` последовательно запустите каждый блок кода

# Если нужно добавить свой алгоритм генерации

- Можно использовать интерфейс `IIterateGen<T : Number>` и применить свой алгоритм в классе `Application` в методе `createGen`

```kotlin
    private fun createGen(): IIterateGen<Double> {
        val angle = 0.0
        val mainStep = PI / config.frequency

        val sinGen = SinIterateGen(
            initialAngle = angle,
            step = mainStep
        )

        val perlinGen = PerlinNoiseIterateDecorator(
            seed = config.seed,
            iterateGen = sinGen,
            perlinScale = config.perlinScale
        )

        return SeasonOffsetIterateGenDecorator(
            gen = perlinGen,
            scale = 0.5,
            step = PI / (config.timeDuration / SEASON_COUNT),
        )
    }
```
