package com.orion.skinnzrecyclerview.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.orion.skinnzrecyclerview.R
import com.orion.skinnzrecyclerview.enum.RecyclerViewState
import com.orion.skinnzrecyclerview.extensions.hide
import com.orion.skinnzrecyclerview.extensions.show
import com.orion.skinnzrecyclerview.utils.Utils

class MoodRecyclerView : RecyclerView {

    private var mAttrs: AttributeSet? = null

    private var mEmptyView: View? = null
    private var mProgressView: View? = null
    private var mErrorView: View? = null

    var state: RecyclerViewState = RecyclerViewState.LOADING
        set(value) {
            field = value
            setViewForState(value)
        }


    //User Properties

    var shouldCloseKeyboardOnScroll = false

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        mAttrs = attrs
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mAttrs = attrs
    }

    init {
        context.theme.obtainStyledAttributes(mAttrs, R.styleable.MoodRecyclerView, 0, 0).apply {

            try {
                val emptyViewId = getResourceId(R.styleable.MoodRecyclerView_emptyView, 0)
                val progressViewId = getResourceId(R.styleable.MoodRecyclerView_progressView, 0)
                val errorViewId = getResourceId(R.styleable.MoodRecyclerView_errorView, 0)

                when {
                    emptyViewId != 0 -> findViewById<View>(emptyViewId)?.let { mEmptyView = it }
                }
                when {
                    progressViewId != 0 -> findViewById<View>(progressViewId)?.let { mProgressView = it }
                }
                when {
                    errorViewId != 0 -> findViewById<View>(errorViewId)?.let { mErrorView = it }
                }
            } finally {
                recycle()
            }
        }
    }

    private val dataObserver = object : RecyclerView.AdapterDataObserver() {

        override fun onChanged() {
            adapter?.let { recyclerAdapter: Adapter<*> ->

                state = when (recyclerAdapter.itemCount) {
                    0 -> RecyclerViewState.EMPTY
                    else -> RecyclerViewState.LOADED
                }
                setViewForState(state)
            }
        }
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)
        adapter?.registerAdapterDataObserver(this.dataObserver)
    }

    private fun setViewForState(recyclerViewState: RecyclerViewState) {
        when (recyclerViewState) {
            RecyclerViewState.LOADING -> {
                mEmptyView?.hide()
                mProgressView?.show()
                mErrorView?.hide()
            }
            RecyclerViewState.LOADED -> {
                mEmptyView?.hide()
                mProgressView?.hide()
                mErrorView?.hide()
            }
            RecyclerViewState.EMPTY -> {
                mEmptyView?.show()
                mProgressView?.hide()
                mErrorView?.hide()
            }
            RecyclerViewState.ERROR -> {
                mEmptyView?.hide()
                mProgressView?.hide()
                mErrorView?.show()
            }
        }
    }

    override fun onScrolled(dx: Int, dy: Int) {
        super.onScrolled(dx, dy)
        when{
            shouldCloseKeyboardOnScroll&& dy != 0 -> Utils.hideKeyboard(context, this)
        }
    }
}