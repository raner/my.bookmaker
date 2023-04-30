package my.book.metadata

interface Book
{
  fun title(): String
  fun subtitle(): String
  fun edition(): Int
  fun language(): String
  fun author(): String
  fun copyright(): String
  fun description(): String
  fun keywords(): List<String>
  fun categories(): List<String>
  fun trim(): String
  fun bleed(): Boolean
  fun cover(): Cover
  fun style(): String
}