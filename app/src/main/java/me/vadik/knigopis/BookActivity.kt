package me.vadik.knigopis

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.View.*
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import me.vadik.knigopis.api.BookCoverSearch
import me.vadik.knigopis.api.BookCoverSearchImpl
import me.vadik.knigopis.api.ImageEndpoint

class BookActivity : AppCompatActivity() {

  private val imageSearch: BookCoverSearch by lazy {
    BookCoverSearchImpl(
        app().imageApi.create(ImageEndpoint::class.java),
        getSharedPreferences("knigopis", MODE_PRIVATE)
    )
  }
  private val toolbar by lazy { findView<Toolbar>(R.id.toolbar) }
  private val titleEditText by lazy { findView<EditText>(R.id.book_title_edit_text) }
  private val coverImageView by lazy { findView<ImageView>(R.id.cover_image_view) }
  private val readCheckbox by lazy { findView<CheckBox>(R.id.book_read_checkbox) }
  private val dateInputViews by lazy {
    arrayOf<View>(
        findView(R.id.book_year_input),
        findView(R.id.book_month_input),
        findView(R.id.book_day_input)
    )
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.book_edit)
    toolbar.inflateMenu(R.menu.book_menu)
    toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
    toolbar.setNavigationOnClickListener {
      finish()
    }
    toolbar.setOnMenuItemClickListener {
      when (it.itemId) {
        R.id.option_save_book -> {
          finish()
          true
        }
        else -> false
      }
    }
    titleEditText.setOnFocusChangeListener { _, focus ->
      val editable = titleEditText.editableText
      if (!focus && !editable.isEmpty()) {
        imageSearch.search(editable.toString())
            .subscribe({ coverUrl ->
              Glide.with(applicationContext)
                  .load(coverUrl)
                  .apply(RequestOptions.centerCropTransform())
                  .into(coverImageView)
            }, {
              logError("cannot load thumbnail", it)
            })
      }
    }
    readCheckbox.setOnCheckedChangeListener { _, checked ->
      dateInputViews.forEach {
        it.visibility = if (checked) VISIBLE else GONE
      }
    }
  }
}