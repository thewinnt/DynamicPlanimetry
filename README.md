# Dynamic Planimetry

Программа для создания чертежей по планиметрии.

## Функционал
Можно создавать:
- точки
- прямые, отрезки и лучи
- окружности
- многоугольники

У каждой фигуры можно изменять свойства. Прямые, отрезки и лучи (далее - линии) можно преобразовывать между собой.
К линиям можно достраивать параллельные прямые и прямые под произвольным уголм (по умолчанию - 90 градусов).
Единицы измерения можно изменить в настройках.

## Системные требования
- ОС:
  - Windows 7 или новее
  - Android 8 или новее
- Java 21 (для ПК)
- ~30 МБ свободного места (~100 МБ для сборки)

## Сборка
В папке репозитория выполнить команду `./gradlew build` (или `gradlew build` в командной строке Windows). Скачаются
все необходимые библиотеки. Готовый jar-файл для ПК будет в `lwjgl3/build/libs/`, APK-файл будет в
`android/build/outputs/apk/release`. Необходима Java 21.

Чтобы просто запустить программу, можно написать `./gradlew run` (или `gradlew run` в командной строке Windows).

## Используемые библиотеки
- [libGDX](https://libgdx.com)
- [nbt](https://github.com/Querz/NBT)
