package me.vadik.knigopis.model

import me.vadik.knigopis.orDefault

interface Book {
  val id: String
  val title: String
  val author: String
  val titleOrDefault get() = title.orDefault("(без названия)")
  val authorOrDefault get() = author.orDefault("(автор не указан)")
}