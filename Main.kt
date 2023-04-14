package signature

import java.io.File

const val SYMBOL_BORDER_TOP_BOTTOM = "8"
const val SYMBOL_BORDER_LEFT = "88  "
const val SYMBOL_BORDER_RIGHT = "  88"
const val SPACE_NAME = "          "
const val SPACE_STATUS = "     "

class LetterInfo(private val letter: Char, private val length: Int = 0) {
    fun getChar(): Char = letter
    fun getLength(): Int = length
    override fun toString(): String = "$letter with $length length"
}

fun main() {
    println("Enter name and surname:")
    val name = readln()
    println("Enter person's status:")
    val status = readln()

    val statusSpaces = status.toCharArray().filter { it == ' ' }.size * SPACE_STATUS.length
    val nameSpaces = name.toCharArray().filter { it == ' ' }.size * SPACE_NAME.length

    // --- READ FONTS ---
    val mapRoman = fileToMap("src/roman.txt")
    val mapMedium = fileToMap("src/medium.txt")

    // --- NAME INFO ---
    val lengthName = countStringLength(name, mapRoman)

    // --- STATUS INFO ---
    val lengthStatus = countStringLength(status, mapMedium)
    val borderTopBottom =
        SYMBOL_BORDER_TOP_BOTTOM.repeat(
            maxOf(
                maxOf(
                    lengthName + nameSpaces, lengthStatus + statusSpaces
                ) + SYMBOL_BORDER_LEFT.length + SYMBOL_BORDER_RIGHT.length
            )
        )

    val listName = MutableList(10) { "" }
    val listStatus = MutableList(3) { "" }
    createRow(name, listName, mapRoman, SPACE_NAME)
    createRow(status, listStatus, mapMedium, SPACE_STATUS)

    // Check if name or status is the longest
    if (lengthName + nameSpaces > lengthStatus + statusSpaces) {

        val difference = (lengthName + nameSpaces) - (lengthStatus + statusSpaces)

        for (i in listStatus.indices) {
            listStatus[i] = listStatus[i].replace(SYMBOL_BORDER_LEFT, SYMBOL_BORDER_LEFT + " ".repeat(difference / 2))
            listStatus[i] = listStatus[i].dropLast(4)

            if (difference % 2 == 0) {
                listStatus[i] = listStatus[i] + " ".repeat(difference / 2) + SYMBOL_BORDER_RIGHT
            } else {
                listStatus[i] = listStatus[i] + " ".repeat(difference / 2 + 1) + SYMBOL_BORDER_RIGHT
            }
        }
    } else {
        val difference = (lengthStatus + statusSpaces) - (lengthName + nameSpaces)

        for (i in listName.indices) {
            listName[i] = listName[i].replaceFirst(SYMBOL_BORDER_LEFT, SYMBOL_BORDER_LEFT + " ".repeat(difference / 2))
            listName[i] = listName[i].dropLast(4)

            if (difference % 2 == 0) {
                listName[i] = listName[i] + " ".repeat(difference / 2) + SYMBOL_BORDER_RIGHT
            } else {
                listName[i] = listName[i] + " ".repeat(difference / 2 + 1) + SYMBOL_BORDER_RIGHT
            }
        }
    }

    // PRINT RESULT
    printTag(borderTopBottom, listName, listStatus)
}

fun createRow(
    string: String,
    list: MutableList<String>,
    map: MutableMap<LetterInfo, MutableList<String>>,
    spacing: String
) {
    repeat(list.size) { repetition ->
        var currentString = SYMBOL_BORDER_LEFT
        for (element in string) {
            if (element == ' ') {
                currentString += spacing
                continue
            }
            currentString += map.filter { it.key.getChar() == element }.values.first()[repetition]
        }
        currentString += SYMBOL_BORDER_RIGHT
        list[repetition] = currentString
    }
}

fun printTag(borderTopBottom: String, listName: MutableList<String>, listStatus: MutableList<String>) {
    println(borderTopBottom)
    listName.forEach { println(it) }
    listStatus.forEach { println(it) }
    println(borderTopBottom)
}

fun countStringLength(string: String, map: MutableMap<LetterInfo, MutableList<String>>): Int {
    var stringLength = 0
    for (i in string) {
        if (i == ' ') continue
        val x = map.filter { it.key.getChar() == i }
        stringLength += x.keys.first().getLength()
    }
    return stringLength
}

fun fileToMap(fileName: String): MutableMap<LetterInfo, MutableList<String>> {
    val map = mutableMapOf<LetterInfo, MutableList<String>>()
    val lines = File(fileName).readLines()

    val letterLines = lines.first().split(" ")[0].toInt() + 1

    for (i in 2 until lines.size step letterLines) {
        map[LetterInfo(lines[i - 1].first(), lines[i - 1].substringAfter(" ").toInt())] =
            lines.slice(i..i + letterLines - 2).toMutableList()
    }
    return map
}
