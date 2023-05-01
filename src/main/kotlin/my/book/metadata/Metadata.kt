package my.book.metadata

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import my.book.source.Source

class Metadata
{
	val mapper: YAMLMapper = YAMLMapper().apply{findAndRegisterModules()}

	fun book(source: Source): Book
    {
        return mapper.readValue(source.reader(), Book::class.java)
    }
}
