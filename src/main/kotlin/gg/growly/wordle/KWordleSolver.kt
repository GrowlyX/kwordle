package gg.growly.wordle

import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
import gg.growly.wordle.character.KWordleCharType
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.URL

/**
 * Wordle solver- only tested
 * with wordle unlimited.
 *
 * @author GrowlyX
 * @since 2/18/2022
 */
object KWordleSolver
{
    @JvmStatic
    fun main(args: Array<String>)
    {
        val gson = GsonBuilder()
            .setLongSerializationPolicy(LongSerializationPolicy.STRING)
            // wordle's json array is wonky
            .setLenient().create()

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

        val randomWord = wordList
            .shuffled().random()

        println("${Color.YELLOW}Please enter the first word: ${Color.CYAN}${randomWord}")
    }

    private val reader = BufferedReader(
        InputStreamReader(System.`in`)
    )

    private fun String.response(): String
    {
        println("$this ")
        return reader.readLine()
    }

    private fun List<String>.response(): String
    {
        forEach { println(it) }
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
