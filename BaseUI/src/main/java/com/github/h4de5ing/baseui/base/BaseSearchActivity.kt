package com.github.h4de5ing.baseui.base

import android.annotation.SuppressLint
import android.app.SearchManager
import android.database.MatrixCursor
import android.provider.BaseColumns
import android.view.Menu
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.MutableLiveData
import com.github.h4de5ing.baseui.R
import com.github.h4de5ing.baseui.base.adapter.CustomSuggestionsAdapter
import java.util.Locale.getDefault

/**
 * 继承本类需要设置主题
 * android:theme="@style/AppTheme.NoActionBar"
 */
abstract class BaseSearchActivity : BaseReturnActivity() {
    var searchView: SearchView? = null

    //第一行为标题 第二行为显示的内容 第三行为点击后的标记
    val allDataList = mutableListOf<Triple<String, String, Int>>()
    var onClickItem = MutableLiveData<Int>()
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search, menu)
        searchView = menu.findItem(R.id.action_search).actionView as SearchView
        searchView?.apply {
            searchView?.queryHint = getString(R.string.search_view_hint)
            searchView?.setIconifiedByDefault(true)
            searchView?.suggestionsAdapter = CustomSuggestionsAdapter(this@BaseSearchActivity)
            searchView?.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
                override fun onSuggestionSelect(position: Int): Boolean = false

                override fun onSuggestionClick(position: Int): Boolean {
                    val info = with(searchView?.suggestionsAdapter?.cursor) {
                        this?.moveToPosition(position)
                        this?.getInt(3)
                    }
                    onClickItem.value = info
                    closeSearchView()
                    return true
                }
            })
            searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean = false

                override fun onQueryTextChange(query: String): Boolean {
                    val newCursor = MatrixCursor(
                        arrayOf(
                            BaseColumns._ID,
                            SearchManager.SUGGEST_COLUMN_TEXT_1,
                            SearchManager.SUGGEST_COLUMN_TEXT_2,
                            SearchManager.SUGGEST_COLUMN_TEXT_2_URL
                        )
                    )
                    allDataList.filter {
                        it.second.lowercase(getDefault()).contains(
                            query.lowercase(
                                getDefault()
                            )
                        )
                    }
                        .forEachIndexed { index, any ->
                            newCursor.addRow(arrayOf(index, any.first, any.second, any.third))
                        }
                    searchView?.suggestionsAdapter?.swapCursor(newCursor)
                    return false
                }
            })
        }
        return true
    }

    @SuppressLint("RestrictedApi")
    fun closeSearchView() {
//        if (toolbar != null) toolbar.collapseActionView()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        closeSearchView()
    }
}