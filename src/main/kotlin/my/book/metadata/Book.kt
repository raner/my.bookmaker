package my.book.metadata

interface Book
{
  val title: String
  val subtitle: String
  val edition: Int
  val language: String
  val author: String
  val copyright: String
  val description: String
  val keywords: List<String>
  val categories: List<String>
  val trim: String
  val bleed: Boolean
  val cover: Cover
  val style: String
}