package my.bookmaker.metadata

interface Manuscript {
    val chapters: Array<Chapter>
    val appendix: Array<Appendix>
}