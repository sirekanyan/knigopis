package me.vadik.knigopis

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.View.*
import android.widget.CheckBox

class BookActivity : AppCompatActivity() {

  private val toolbar by lazy { findView<Toolbar>(R.id.toolbar) }
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
    readCheckbox.setOnCheckedChangeListener { _, checked ->
      dateInputViews.forEach {
        it.visibility = if (checked) VISIBLE else GONE
      }
    }
  }
}