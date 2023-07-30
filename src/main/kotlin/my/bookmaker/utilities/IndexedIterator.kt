package my.bookmaker.utilities

import java.util.function.Function
import java.util.function.BiFunction

class IndexedIterator<COLLECTION, ELEMENT>: Iterator<ELEMENT>
{
    private var index: Int = 0
    private val size: Int
    private val collection: COLLECTION
    private val accessor: BiFunction<COLLECTION, Int, ELEMENT>

    constructor(collection: COLLECTION, counter: Function<COLLECTION, Int>, accessor: BiFunction<COLLECTION, Int, ELEMENT>)
    {
        this.collection = collection
        this.accessor = accessor
        size = counter.apply(collection)
    }

    override fun hasNext(): Boolean = index < size

    override fun next(): ELEMENT = accessor.apply(collection, index++)
}
