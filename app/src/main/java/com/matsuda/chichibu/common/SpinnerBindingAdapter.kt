package com.matsuda.chichibu.common

import android.view.View
import androidx.databinding.BindingAdapter
import android.widget.ArrayAdapter
import android.widget.AdapterView
import androidx.databinding.InverseBindingListener
import androidx.appcompat.widget.AppCompatSpinner
import androidx.databinding.InverseBindingAdapter
import com.matsuda.chichibu.actions.OwnerActionCreator
import com.matsuda.chichibu.data.ArticleCategory
import com.matsuda.chichibu.data.Prefecture

object SpinnerBindingAdapter {
    @BindingAdapter(
        "selectedCategoryValue", "selectedCategoryValueAttrChanged", requireAll = false
    )
    @JvmStatic
    fun bindSpinnerData(
        pAppCompatSpinner: AppCompatSpinner,
        newSelectedValue: ArticleCategory?,
        newTextAttrChanged: InverseBindingListener
    ) {
        pAppCompatSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                newTextAttrChanged.onChange()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        newSelectedValue?.run {
            val adapter = pAppCompatSpinner.adapter as ArrayAdapter<String>
            val pos = adapter.getPosition(this.name)
            pAppCompatSpinner.setSelection(pos, true)
        }
    }

    @BindingAdapter(
        "selectedAreaValue", "isParent", "selectedAreaValueAttrChanged", requireAll = false
    )
    @JvmStatic
    fun bindAreaSpinnerData(
        pAppCompatSpinner: AppCompatSpinner,
        newSelectedValue: String?,
        isParent: Boolean = false,
        newTextAttrChanged: InverseBindingListener
    ) {
        pAppCompatSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                if (isParent) {
                    val parentArea = pAppCompatSpinner.selectedItem as String
                    OwnerActionCreator.fetchChildrenArea(Prefecture.valueOf(parentArea))
                }
                newTextAttrChanged.onChange()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        newSelectedValue?.run {
            val adapter = pAppCompatSpinner.adapter as ArrayAdapter<String>
            val pos = adapter.getPosition(this)
            pAppCompatSpinner.setSelection(pos, true)
        }
    }

    @InverseBindingAdapter(
        attribute = "selectedCategoryValue",
        event = "selectedCategoryValueAttrChanged"
    )
    @JvmStatic
    fun captureSelectedValue(pAppCompatSpinner: AppCompatSpinner): ArticleCategory {
        return ArticleCategory.valueOf(pAppCompatSpinner.selectedItem as String)
    }

    @InverseBindingAdapter(
        attribute = "selectedAreaValue",
        event = "selectedAreaValueAttrChanged"
    )
    @JvmStatic
    fun captureSelectedAreaValue(pAppCompatSpinner: AppCompatSpinner): String {
        return pAppCompatSpinner.selectedItem as String
    }
}