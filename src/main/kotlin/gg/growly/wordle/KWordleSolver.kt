package gg.growly.wordle

import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
import gg.growly.wordle.character.KWordleChar
import gg.growly.wordle.character.KWordleCharType
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

/**
 * Wordle solver- only tested
 * with wordle unlimited.
 *
 * @author GrowlyX
 * @since 2/18/2022
 */
object KWordleSolver
{
    // tracking characters of the current word, we
    // will use this to find the best possible next "currentWord"
    private val characters =
        mutableMapOf<Int, KWordleChar>()

    @JvmStatic
    fun main(args: Array<String>)
    {
        val gson = GsonBuilder()
            .setLongSerializationPolicy(LongSerializationPolicy.STRING)
            .create()

        // asking for the character amount
        // they're playing with
        val charAmount = "${Color.GREEN}How many characters have you selected?"
            .response().toInt()

        println("${Color.CYAN}Processing available words...")

        // parse words from our word file from WordleUnlimited
        val wordList = track(
            "word parsing ($charAmount chars)"
        ) {
            val serialized = File("words.json")
                .readLines().joinToString()

            return@track gson
                .fromJson<List<String?>>(
                    serialized, stringListType
                )
                .filterNotNull()
                .filter {
                    it.length == charAmount
                }
        }

        // generating a random word
        // for our first entry
        var currentWord = wordList
            .shuffled().random()

        while (true)
        {
            println("${Color.YELLOW}Please enter the word: ${Color.CYAN}$currentWord")

            // handling characters within
            // the range of 1-charAmount
            for (i in 1..charAmount)
            {
                val character = currentWord[i - 1]
                    .lowercaseChar()

                val status = """
                    ${Color.YELLOW}What is the status for $character in $currentWord?
                    ${Color.WHITE_BOLD}Statuses: ${KWordleCharType.values().joinToString()}
                """.trimIndent().response()

                val parsedStatus = KWordleCharType
                    .valueOf(status)

                val wordleChar = KWordleChar(
                    character, parsedStatus
                )

                characters[i - 1] = wordleChar
            }

            val values = characters.values

            val unused = values
                .filter { it.type == KWordleCharType.NO_CHAR }
                .map { it.value }

            val fixedPlacement = characters
                .filter { it.value.type == KWordleCharType.FIXED }

            val wrongPlacement = values
                .filter { it.type == KWordleCharType.WRONG_PLACEMENT }

            currentWord = wordList
                .filter { word ->
                    word.none { unused.contains(it) }
                }
                .filter { word ->
                    wrongPlacement
                        .map { it.value }
                        .all { word.contains(it) }
                }
                .first { word ->
                    fixedPlacement
                        .all { word[it.key] == it.value.value }
                }
        }
    }

    private val reader = BufferedReader(
        InputStreamReader(System.`in`)
    )

    private fun String.response(): String
    {
        println("$this ")
        return reader.readLine()
    }

    private fun <T> track(
        process: String, lambda: () -> T
    ): T
    {
        val start = System.currentTimeMillis()
        val result = lambda.invoke()

        println(
            "${Color.GREEN}Completed $process in ${
                System.currentTimeMillis() - start
            }ms!"
        )

        return result
    }
}
