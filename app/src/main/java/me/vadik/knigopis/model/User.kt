package me.vadik.knigopis.model

class User(
    val id: String,
    val nickname: String,
    val booksCount: Int,
    val updatedAt: String
) {

  val color: Int
    get() {
      val alpha = Math.min(0xff, booksCount)
      return alpha shl 24
    }
}