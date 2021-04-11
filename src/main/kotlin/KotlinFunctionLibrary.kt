import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.Comparator
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.system.measureNanoTime

@Suppress("UNUSED")
object KotlinFunctionLibrary{
    const val WINDOWS_FILENAME_REGEX =
        "^(?!^(PRN|AUX|CLOCK$|NUL|CON|COM\\d|LPT\\d|\\..*)(\\..+)?$)[^\\x00-\\x1f\\\\<>:\"/|?*;]+$"
    const val WINDOWS_FILE_EXTENSION_REGEX = "\\w+"

    /**
     * A variable representing a class's parameters - something which Kotlin does not provide
     * */
    val <T : Any> KClass<T>.parameters: List<String?>?
    get() {
        return primaryConstructor?.parameters?.toListBy { it.name }?.toList()
    }

    /**
     * Lambda to generate a random word of size [wordLength]
     * */
    val randomWord = { wordLength:Int ->
        (1..wordLength)
            .map { (('A'..'Z') + ('a'..'z')).random() }
            .joinToString("")
    }

    /**
     * Lambda to generate a list of size [numWords] containing random words of size [wordLength]
     * */
    val listOfRandomWords = {numWords:Int, wordLength:Int->
        val randomListOfWords = mutableListOf<String>()
        for (i in 1..numWords) randomListOfWords.add(randomWord(wordLength))
        randomListOfWords
    }

    /**
     * A version of println which prints [this] and returns [this], facilitating fluent interfaces and functional programming.
     * Also useful for debugging values without breaking a call change or interrupting the flow of code for logging values.
     * */
    fun <T> T.println() = this.apply { println(this) }
    /**
     * A version of print which prints [this] and returns [this], facilitating fluent interfaces, functional programming, and assisting in debugging.
     * Also useful for debugging values without breaking a call change or interrupting the flow of code for logging values.
     * */
    fun <T> T.print() = this.apply { print(this) }

    /**
     * A version of println which passes [this] into [message] to create the message to pass to println, facilitating fluent interfaces and functional programming.
     * Also useful for debugging values without breaking a call change or interrupting the flow of code for logging values.
     * */
    fun <T, R> T.println(message: (T) -> R) = this.apply { println(message(this)) }
    /**
     * A version of print which passes [this] into [message] to create the message to pass to print, facilitating fluent interfaces, functional programming, and assisting in debugging.
     * Also useful for debugging values without breaking a call change or interrupting the flow of code for logging values.
     * */
    fun <T, R> T.print(message: (T) -> R) = this.apply { print(message(this)) }

    /**
     * A version of println which which prints [message] and returns [this], facilitating fluent interfaces, functional programming, and assisting in debugging.
     * Intended for debugging values without breaking a call change or interrupting the flow of code for logging values.
     * */
    fun <T, R> T.printlnAndReturn(message: R) = this.apply { println(message) }
    /**
     * A version of print which which prints [message] and returns [this], facilitating fluent interfaces, functional programming, and assisting in debugging.
     * Intended for debugging values without breaking a call change or interrupting the flow of code for logging values.
     * */
    fun <T, R> T.printAndReturn(message: R) = this.apply { print(message) }

    /**
     * A version of println which passes [this] into [message] to create the message to pass to println, facilitating fluent interfaces, functional programming, and assisting in debugging.
     * Also useful for debugging values without breaking a call change or interrupting the flow of code for logging values.
     * */
    fun <T,R> T.printlnAndReturn(message: (T) -> R) = this.apply { println(message(this)) }

    /**
     *  A version of [measureNanoTime] which relieves the need to store the execution time of [block] and print it (and do the math to print the result in seconds if desired).
     *  Does not return the result of [block]; see {@link #measureNanoTimeAndPrintAndReturnResult(Boolean, String, () -> T)}
     *  @param message the message to be displayed when printing the time to complete. Will be passed to System.out.printf, so can use "%d"
     *  as the template/placeholder for the time to complete
     * */
    fun measureNanoTimeAndPrintWithoutReturningResult(
        inSeconds: Boolean = false,
        message: String = if (inSeconds) "Time to complete: %d seconds" else "Time to complete: %d nanoseconds",
        block: () -> Unit
    ) {
        val timeToComplete = measureNanoTime(block)
        System.out.printf(message, if(inSeconds) timeToComplete / 1_000_000_000.00 else timeToComplete)
    }

    /**
     *  A version of [measureNanoTime] which relieves the need to store the result and execution time of [block] and print it (and do the math to print the result in seconds if desired).
     *  Does return the result of [block];see {@link #measureNanoTimeAndPrintWithoutReturningResult(Boolean, String, () -> Unit)}
     *  @param message the message to be displayed when printing the time to complete. Will be passed to System.out.printf, so can use "%d"
     *  as the template/placeholder for the time to complete
     *  @see #measureNanoTimeAndPrintAndReturnResult(Boolean, String, T, (T) -> R)}
     * */
    fun <T> measureNanoTimeAndPrintAndReturnResult(
        inSeconds: Boolean = false,
        message: String = if (inSeconds) "Time to complete: %d seconds" else "Time to complete: %d nanoseconds",
        block: () -> T
    ): T {
        var result: T
        val timeToComplete = measureNanoTime { result = block() }
        System.out.printf(message, if(inSeconds) timeToComplete / 1_000_000_000.00 else timeToComplete)
        return result
    }

    /**
     *  A version of [measureNanoTime] which relieves the need to store the result and execution time of [block] and print it (and do the math to print the result in seconds if desired).
     *  Does return the result of [block]; see {@link #measureNanoTimeAndPrintWithoutReturningResult(Boolean, String, () -> Unit)}
     *  @param message the message to be displayed when printing the time to complete. Will be passed to System.out.printf, so can use "%d"
     *  as the template/placeholder for the time to complete
     *  @param param parameter to pass to [block]
     * */
    fun <T, R> measureNanoTimeAndPrintAndReturnResult(
        inSeconds: Boolean = false,
        message: String = if (inSeconds) "Time to complete: %d seconds" else "Time to complete: %d nanoseconds",
        param: T,
        block: (T) -> R
    ): R {
        var result: R
        val timeToComplete = measureNanoTime { result = block(param) }
        System.out.printf(message, if(inSeconds) timeToComplete / 1_000_000_000.00 else timeToComplete)
        return result
    }

    /**
     * Return result of [block] or, if block threw an error (i.e. did not complete), run [onError] and return null. NOTE: Will also return null if [block] completed and returned null
     * */
    fun <T, R> tryAndReturn(
        onError: (e: Throwable, result: R?) -> T = { e, result -> println("Error thrown; Throwable: $e, Result: $result") as T },
        block: () -> R
    ): R? {
        var result: R? = null
        try {
            result = block()
        } catch (e: Throwable) {
            onError(e, result)
        }
        return result
    }

    /**
     * If [returnErrorBlock] is false, Return result of [block], and if block threw an error (i.e. did not complete), run [onError] and return null.
     * Otherwise, run block, and if it throws an error, run [onError] and return its result.
     * NOTE: Will also return null if [block] completed and returned null
     * @param returnErrorBlock true if caller wants to return the result of the error block in case it is run. Assumes that [onError] will return a nullable version of whatever type [block] returns
     * */
    fun <T, R> tryAndReturn(
        returnErrorBlock: Boolean,
        onError: (e: Throwable, result: R?) -> T = { e, result -> println("Throwable: $e, Result: $result") as T },
        block: () -> R
    ): R? {
        var result: R? = null
        try {
            result = block()
        } catch (e: Throwable) {
            if (returnErrorBlock) result = onError(e, result) as R? else onError(e, result)
        }
        return result
    }

    /**
     * Request input from the user by first printing [firstMessageToDisplay] and then calling readLine()
     * Loops for input until the input matches the provided [regex], printing [messageToDisplayOnError] every time the user enters an invalid input
     * @return aninput from the user which matches [regex]
     * */
    fun getValidatedInput(
        regex: Regex,
        firstMessageToDisplay: String,
        messageToDisplayOnError: String
    ): String? {
        print("$firstMessageToDisplay: ")
        var input = readLine()
        while (input?.matches(regex)
                ?.not() == true
        ) /*doesn't match regex (written in a roundabout way to retain nullability)*/ {
            print("$messageToDisplayOnError: ")
            input = readLine()
        }
        return input
    }

    /**
     * A variation of String.indexOf() which takes a regex [Pattern] instead of a [CharSequence]
     *  @return index of pattern in [this, or -1 if not found
     */
    fun String?.indexOf(pattern: Pattern): Int {
        val matcher: Matcher = pattern.matcher(toString())
        return if (matcher.find()) matcher.start() else -1
    }
    /**
     * Takes a [Triple] of <hour,minute,second> and returns either e.g. "05:32:15", or "5 hr 32 min 15 sec"
     * */
    fun Triple<Int, Int, Int>.formatted(withColons: Boolean) =
        if (withColons) "${if(this.first.toString().length==1) "0${this.first}" else "${this.first}"}:${this.second}:${this.third}"
        else timeFormattedConcisely(
            this.first,
            this.second,
            this.third
        )

    /**
     * Takes an hour, minute, and second, and will return a string with only those values which are not equal to 0 (e.g. "5 hr 15 sec", "5 hr 32 min 15 sec")
     * */
    fun timeFormattedConcisely(hour: Int, minute: Int, second: Int): String {
        val string = StringBuilder()
        if (hour != 0) string.append("$hour hr ")
        if (minute != 0) string.append("$minute min ")
        if (second != 0) string.append("$second sec")
        return string.toString().trim()
    }

    fun toSeconds(hour: Int, minute: Int, second: Int) =
        (hour * 3600) + (minute * 60) + second

    /**
     * Converts seconds ([this]) to hours, minutes, seconds format
     * @receiver seconds to convert
     * @return a [Triple] of final hour, minute, second
     * @sample (578).toHrMinSec() = (0, 9, 38)*/
    fun Int.toHrMinSec(): Triple<Int, Int, Int> {
        var hour = 0
        var minute = 0
        var second = this
        minute += (second / 60)
        hour += (minute / 60)
        second %= 60
        minute %= 60
        return Triple(hour, minute, second)
    }

    /**
     * Takes hours, minutes, seconds, and converts it to a [Triple] of the form <hour,minute,second>
     * */
    fun toHrMinSec(hour: Int = 0, minute: Int = 0, second: Int = 0): Triple<Int, Int, Int> {
        var minute1 = minute
        var second1 = second
        var hour1 = hour
        minute1 += (second1 / 60)
        hour1 += (minute1 / 60)
        second1 %= 60
        minute1 %= 60
        return Triple(hour1, minute1, second1)
    }

    /**
     * Sorts a list by mutliple criteria
     * NOTE: mutates the provided list in the process
     * @sample  sort(
    myList,
    listOf(Class::myParameter1.name, Class::myParameter2.name),
    listOf(true, false)
    )
     */
    @JvmName("sortWithListGivenAsParameters")
    inline fun <reified T> sort(
        workingList: MutableList<T>,
        sortCriteria: List<String>,
        ascending: List<Boolean>
    ) {
        /*//unoptimized version:
        val oneOfMyClasses = workingList[0]::class
        val firstSelector = oneOfMyClasses.getPropertyToSortBy(shiurFilterOptions[0])
        val compareBy = getComparator(ascending, firstSelector, shiurFilterOptions, oneOfMyClasses)
        workingList.sortWith(compareBy)*/
        //optimized version:

        val size = requireNotEmptyAndSameSize(ascending, sortCriteria,"List of ascending/descending must not be empty",
            "List of sort criteria must not be empty",
            "Each sort criteria must be matched with a ascending/descending boolean; size of ascending/descending list: %d, Size of sort criteria list: %d ")

        workingList.sortWith(
            PRIVATEgetComparator(
                ascending,
                firstSelector = PRIVATEgetPropertyToSortBy(sortCriteria[0]),
                sortCriteria,
                size.second
            )
        )

    }

    /**
     *
     * much faster than passing in sort criteria strings
     *  * @sample  sort(
    myList,
    listOf(Class::myParameter1 as KProperty1<Class, Comparable<Any>>,
    Class::myParameter2 as KProperty1<Class, Comparable<Any>>),
    listOf(true, false)
    )
     * */
    inline fun <reified T> sort(
        workingList: MutableList<T>,
        sortCriteria: List<KProperty1<T, Comparable<Any>?>>,
        ascending: List<Boolean>
    ) {
        val size = requireNotEmptyAndSameSize(ascending, sortCriteria,"List of ascending/descending must not be empty",
            "List of sort criteria must not be empty",
            "Each sort criteria must be matched with a ascending/descending boolean; size of ascending/descending list: %d, Size of sort criteria list: %d ")
        workingList.sortWith(
            PRIVATEgetComparator(
                ascending,
                firstSelector = sortCriteria[0],
                sortCriteria,
                size.second
            )
        )
    }
    /**
     * Wrapper to {@link sort(MutableList<T>,List<KProperty1<T, Comparable<Any>?>>,List<Boolean>)} using [Map] for a more convenient API
     * */
    inline fun <reified T> sort(
        workingList: MutableList<T>,
        sortCriteriaMappedToAscending: Map<KProperty1<T, Comparable<Any>?>,Boolean>
    ) = sort(workingList, sortCriteriaMappedToAscending.keys.toList(), sortCriteriaMappedToAscending.values.toList())

    /**
     * Wrapper to {@link sort(MutableList<T>,List<String>,List<Boolean>)} using [Map] for a more convenient API
     * */
    @JvmName("sortWithClassParameterStrings")
    inline fun <reified T> sort(
        workingList: MutableList<T>,
        sortCriteriaMappedToAscending: Map<String,Boolean>
    ) = sort(workingList, sortCriteriaMappedToAscending.keys.toMutableList(), sortCriteriaMappedToAscending.values.toList())

    /**
     * Wrapper to {@link sort(MutableList<T>,List<KProperty1<T, Comparable<Any>?>>,List<Boolean>)} using [Map] for a more convenient API
     * */
    @JvmName("sortWithListAsReceiverAndMapOfKProperty1")
    inline fun <reified T> MutableList<T>.sort(
        map: Map<KProperty1<T, Comparable<Any>?>, Boolean>
    ) = sort(this, map.keys.toList(), map.values.toList())

    /**
     * Wrapper to {@link sort(MutableList<T>,List<String>,List<Boolean>)} using [Map] for a more convenient API
     * */
    @JvmName("sortWithListAsReceiverAndMapOfString")
    inline fun <reified T> MutableList<T>.sort(
        map: Map<String, Boolean>
    ) = sort(this, map.keys.toMutableList(), map.values.toList())
    /**
     * Takes two lists and throws an [IllegalArgumentException] if either of the lists are empty or if they are different sizes.
     * @param aEmptyMessage message to be printed if [listA] is empty; optionally use "%d" as a template/placeholder for the size of [listA]
     * @param bEmptyMessage message to be printed if [listB] is empty; optionally use "%d" as a template/placeholder for the size of [listB]
     * @param notSameSizeMessage message to br printed if lists are not the same size; optionally use the first "%d" as a template/placeholder for the size of [listB]
     * @return a [Pair] of [listA].size, [listB].size
     * */
    fun <A, B> requireNotEmptyAndSameSize(
        listA: Collection<A>, //ascending
        listB: Collection<B>, //sortCriteria
        aEmptyMessage: String = "List A is empty.",
        bEmptyMessage: String = "List B is empty.",
        notSameSizeMessage: String = "Lists are not same size; List A is size %d, List B is size %d."
    ): Pair<Int,Int> {
        val sizeA = listA.size
        val sizeB = listB.size
        require(sizeA > 0) { System.out.printf(aEmptyMessage,sizeA) }
        require(sizeB > 0) { System.out.printf(bEmptyMessage,sizeB) }
        require(sizeB == sizeA) { System.out.printf(notSameSizeMessage,  sizeA, sizeB) }
        return Pair(sizeA,sizeB)
    }

    /**
     * Creates the [Comparator] which is used to filter a list by multiple criteria
     * Can be thought of as a chain resembling something like
     * val comparator = compareBy(LaundryItem::speaker).thenBy { it.title }.thenByDescending { it.length }.thenByDescending { it.series }.thenBy { it.language }
     * which will then be fed into list.sortedWith(comparator), except the calls to thenBy() and thenByDescending() will also be passed [KProperty1]s
     * @param firstSelector used to start the chain of comparators with ascending or descending order;
     * should be the first of the list of conditions to be sorted by. The iteration through the sort criteria
     * will continue with the sort criteria after [firstSelector]
     * Would make explicitly private but Kotlin throws an error

     * */
    @PublishedApi internal inline fun <reified T> PRIVATEgetComparator(
        ascending: List<Boolean>,
        firstSelector: KProperty1<T, Comparable<Any>?>,
        sortCriteria: List<String>,
        size: Int
    ): Comparator<T> {
        var compareBy =
            if (ascending[0]) compareBy(firstSelector) else compareByDescending(firstSelector)
        for (index in 1 until size) {
//        unoptimized:
//        val isAscending = ascending[index]
//        val propertyToSortBy = getPropertyToSortBy<T>(sortCriteria[index])
//        compareBy = if (isAscending) compareBy.thenBy(propertyToSortBy) else compareBy.thenByDescending(propertyToSortBy)
//          optimized:
            compareBy =
                if (ascending[index]) compareBy.thenBy(PRIVATEgetPropertyToSortBy(sortCriteria[index])) else compareBy.thenByDescending(
                    PRIVATEgetPropertyToSortBy(sortCriteria[index])
                )

        }
        return compareBy
    }

    /**
     * Would make explicitly private but doing so would violate Kotlin access restrictions
     * */
    @JvmName("comparatorWithExplicitKPropertylist")
    @PublishedApi internal fun <T> PRIVATEgetComparator(
        ascending: List<Boolean>,
        firstSelector: KProperty1<T, Comparable<Any>?>,
        sortCriteria: List<KProperty1<T, Comparable<Any>?>>,
        size: Int
    ): Comparator<T> {
        var compareBy =
            if (ascending[0]) compareBy(firstSelector) else compareByDescending(firstSelector)
        for (index in 1 until size) {
            compareBy =
                if (ascending[index]) compareBy.thenBy(sortCriteria[index]) else compareBy.thenByDescending(
                    sortCriteria[index]
                )

        }
        return compareBy
    }

    /**
     * Would make explicitly private but doing so would violate Kotlin access restrictions
     * @return null if no parameter was found
     * */
    @PublishedApi internal inline fun <reified T> PRIVATEgetPropertyToSortBy(
        sortCriterion: String
    ): KProperty1<T, Comparable<Any>?> =
        (T::class as KClass<*>).memberProperties.find { it.name == sortCriterion } as KProperty1<T, Comparable<Any>?>?
            ?: throw IllegalArgumentException("Parameter \"$sortCriterion\" not found")

    /**
     * Returns a list containing the results of applying the given [transform] function
     * to each element in the original collection.
     * */
    fun <T, R> Iterable<T>.toListBy(transform: (T) -> R): MutableList<R> {
        val mutableList: MutableList<R> = mutableListOf()
        forEach { mutableList.add(transform(it)) }
        return mutableList
    }

    /**
     * @param nullable to distinguish between nullable and not
     * */
    fun <T, R> Iterable<T?>.toListBy(nullable: Boolean, transform: (T) -> R): MutableList<R> {
        val mutableList: MutableList<R> = mutableListOf()
        forEach { it?.let { it1 -> mutableList.add(transform(it1)) } }
        return mutableList
    }

    /**
     * Mutates the reciever list to exactly match [other]. Does not take into account whether contents are identical but have moved,
     * nor whether a region of the lists are matching, but one is missing the contents of another, and just overrides all of the rest
     * of it instead of inserting the missing elements. (e.g. [1,2,4,5].convertTo([1,2,3,4,5]) will do 3 operations instead of 1 (two changes and one addition))
    TODO make version of this function which uses [DiffUtil] to make it more efficient
     * */
/*Test:
*
* /*    val originalList = listOf("aa","bba","cc","bbc","bbca")
val workingList = mutableListOf("aa", "bba", "bbca")
val workingList1 = mutableListOf("aa","bba","cc","bbc","bbcab")
val originalList1 = mutableListOf("aa", "bba", "bbca")
val workingList2 = mutableListOf("aa","cc","bba")
val originalList2 = mutableListOf("aa", "bba", "bbca")
println("workingList=$workingList")
println("originalList=$originalList")
workingList.simpleConvertToMy(originalList)
println("workingList=$workingList")
println()
println("workingList1=$workingList1")
println("originalList1=$originalList1")
workingList1.simpleConvertToMy(originalList1)
println("workingList1=$workingList1")
println()
println("workingList2=$workingList2")
println("originalList2=$originalList2")
workingList2.simpleConvertToMy(originalList2)
println("workingList2=$workingList2")*/
*
* */
    fun MutableList<String>.convertTo(
        other: List<String>
    ) {
        //TODO would using clear() and addAll() be more efficient?
        val size1 = this.size
        val size2 = other.size
        when {
            size2 > size1 -> {
                this.toList().forEachIndexed { index: Int, s: String ->
                    this[index] =
                        other[index] //TODO would this be more efficient by checking whether they are already equal?
                }
                for (index in size1 until size2) this.add(other[index])
            }
            size2 == size1 -> {
                this.toList().forEachIndexed { index: Int, s: String ->
                    this[index] =
                        other[index] //TODO would this be more efficient by checking whether they are already equal?
                }
            }
            size2 < size1 -> {
                for (counter in size2 until size1) this.removeAt(size2) //constantly remove the "size2"th element until they are the same size
                other.forEachIndexed { index, s ->
                    this[index] = s
                }
            }
        }
    }

    /**
     *
     * */
    fun <E> List<E>.doubled(): MutableList<E> = this.toMutableList().also { it.addAll(this) }

    /*Test:
    *  val mutableListOf = mutableListOf(true, true, true)
    println(mutableListOf)
    mutableListOf.myReplaceAll{false}
    println(mutableListOf)*/

    /**
     * Version of {@link MutableList#replaceAll(UnaryOperator)} which compiles to earlier versions of Kotlin and Android by avoiding the use of UnaryOperator
     * */
    fun <E> MutableList<E>.replaceAll(operator: (E?) -> E) {
        val li: MutableListIterator<E> = this.listIterator()
        while (li.hasNext()) {
            li.set(operator(li.next()))
        }
    }

    /**
     * <p>Finds the n-th index within a String, handling {@code null}.
     * This method uses {@link String#indexOf(String)} if possible.</p>
     * <p>Note that matches may overlap<p>
     *
     * <p>A {@code null} CharSequence will return {@code -1}.</p>
     *
     * @param searchStr  the CharSequence to find, may be null
     * @param ordinal  the n-th {@code searchStr} to find, overlapping matches are allowed.
     * @param startingFromTheEnd true if lastOrdinalIndexOf() otherwise false if ordinalIndexOf()
     * @return the n-th index of the search CharSequence,
     *  {@code -1} if no match or {@code null} string input for [searchStr]
     */
    fun String.ordinalIndexOf(searchStr: String?, ordinal: Int, startingFromTheEnd: Boolean): Int {
        if (searchStr == null || ordinal <= 0) {
            return -1
        }
        if (searchStr.isEmpty()) {
            return if (startingFromTheEnd) this.length else 0
        }
        var found = 0
        // set the initial index beyond the end of the string
        // this is to allow for the initial index decrement/increment
        var index = if (startingFromTheEnd) this.length else -1
        do {
            index = if (startingFromTheEnd) {
                this.lastIndexOf(searchStr, index - 1) // step backwards thru string
            } else {
                this.indexOf(searchStr, index + 1) // step forwards through string
            }
            if (index < 0) {
                return index
            }
            found++
        } while (found < ordinal)
        return index
    }
    /**
     * Checks whether a string is an instance of an enum
     * */
    fun <E : Enum<E>?> isInEnum(value: String, enumClass: Class<E>): Boolean {
        for (e in enumClass.enumConstants) {
            if (e!!.name.equals(value, ignoreCase = true)) {
                return true
            }
        }
        return false
    }
}
