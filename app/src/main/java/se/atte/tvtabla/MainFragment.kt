/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package se.atte.tvtabla

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseFragment
import androidx.leanback.widget.*
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import se.atte.tvtabla.channel.ChannelDateInfo
import se.atte.tvtabla.dto.ChannelDateInfoDto
import se.atte.tvtabla.template.BrowseErrorActivity
import se.atte.tvtabla.template.CardPresenter
import se.atte.tvtabla.template.DetailsActivity
import se.atte.tvtabla.template.Movie
import se.atte.tvtabla.util.Resource
import java.util.*

/**
 * Loads a grid of cards with movies to browse.
 */
class MainFragment : BrowseFragment() {

    private val mHandler = Handler()
    private lateinit var mBackgroundManager: BackgroundManager
    private var mDefaultBackground: Drawable? = null
    private lateinit var mMetrics: DisplayMetrics
    private var mBackgroundTimer: Timer? = null
    private var mBackgroundUri: String? = null

    private lateinit var viewModel: ChannelViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate")
        super.onActivityCreated(savedInstanceState)

        viewModel = (activity as FragmentActivity).getViewModel()
        Log.d("atte2", "viewmodel: " + viewModel)

        prepareBackgroundManager()

        setupUIElements()

        viewModel.loadChannelInfo().observe(activity as FragmentActivity,
            Observer<Resource<List<ChannelDateInfoDto>>> { resource ->
                if (resource?.data != null) {
                    val list = mutableListOf<ChannelDateInfo>()
                    for (dto in resource.data) {
                        list.add(ChannelDateInfo(dto))
                    }
                    loadUiWithChannelDateInfo(list)
                }
            })

        setupEventListeners()
    }

    override fun onResume() {
        super.onResume()
        sortProgramsByCurrentTime()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: " + mBackgroundTimer?.toString())
        mBackgroundTimer?.cancel()
        super.onDestroy()
    }

    private fun prepareBackgroundManager() {

        mBackgroundManager = BackgroundManager.getInstance(activity)
        mBackgroundManager.attach(activity.window)
        mDefaultBackground = ContextCompat.getDrawable(context, R.drawable.default_background)
        mMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(mMetrics)
    }

    private fun setupUIElements() {
        title = getString(R.string.browse_title)
        // over title
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true

        // set fastLane (or headers) background color
        brandColor = ContextCompat.getColor(context, R.color.fastlane_background)
        // set search icon color
        searchAffordanceColor = ContextCompat.getColor(context, R.color.search_opaque)
    }

    fun loadUiWithChannelDateInfo(channelInfoList: List<ChannelDateInfo>) {
        val listRowPresenter = ListRowPresenter()
        val rowsAdapter = ArrayObjectAdapter(listRowPresenter)
        val cardPresenter = CardPresenter()

        for (channelDateInfo in channelInfoList) {
            val listRowAdapter = ArrayObjectAdapter(cardPresenter)
            for (programme in channelDateInfo.programmes) {
                listRowAdapter.add(programme)
            }
            val header = HeaderItem(channelDateInfo.getDisplayName())
            rowsAdapter.add(ListRow(header, listRowAdapter))
        }

        adapter = rowsAdapter

        mHandler.postDelayed({sortProgramsByCurrentTime()}, 1000)
    }

    fun sortProgramsByCurrentTime() {
        if (rowsFragment == null) {
            return
        }

        // TODO: store current selected item and set that last

        val channelIndexMap = mutableMapOf<Int, Int>()
        // Calculate current program index for each channel
        for (i in adapter.size() -1 downTo 0) {
            val listRow = adapter.get(i) as ListRow
            val index = getIndexForProgramDuringTime(System.currentTimeMillis() / 1000, listRow.adapter)
            Log.d("atte2", "found current program on index: " + index)
            channelIndexMap[i] = index
        }

        // Select the programs
        for ((k,v ) in channelIndexMap) {
            rowsFragment.setSelectedPosition(k, false, ListRowPresenter.SelectItemViewHolderTask(v))
        }
    }

    fun getIndexForProgramDuringTime(timeInSeconds: Long, adapter: ObjectAdapter) : Int {
        for (i in 0 until adapter.size()) {
            val programme = adapter.get(i) as ChannelDateInfo.Programme
            if (timeInSeconds < programme.stop && timeInSeconds > programme.start) {
                return i
            }
        }

        return 0
    }

    private fun setupEventListeners() {
        setOnSearchClickedListener {
            Toast.makeText(context, "Implement your own in-app search", Toast.LENGTH_LONG)
                .show()
        }

        onItemViewClickedListener = ItemViewClickedListener()
        onItemViewSelectedListener = ItemViewSelectedListener()
    }

    private inner class ItemViewClickedListener : OnItemViewClickedListener {
        override fun onItemClicked(
            itemViewHolder: Presenter.ViewHolder,
            item: Any,
            rowViewHolder: RowPresenter.ViewHolder,
            row: Row
        ) {

            if (item is Movie) {
                Log.d(TAG, "Item: " + item.toString())
                val intent = Intent(context, DetailsActivity::class.java)
                intent.putExtra(DetailsActivity.MOVIE, item)

                val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity,
                    (itemViewHolder.view as ImageCardView).mainImageView,
                    DetailsActivity.SHARED_ELEMENT_NAME
                )
                    .toBundle()
                activity.startActivity(intent, bundle)
            } else if (item is String) {
                if (item.contains(getString(R.string.error_fragment))) {
                    val intent = Intent(context, BrowseErrorActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private inner class ItemViewSelectedListener : OnItemViewSelectedListener {
        override fun onItemSelected(
            itemViewHolder: Presenter.ViewHolder?, item: Any?,
            rowViewHolder: RowPresenter.ViewHolder, row: Row
        ) {
            if (item is Movie) {
                mBackgroundUri = item.backgroundImageUrl
                startBackgroundTimer()
            }
        }
    }

    private fun updateBackground(uri: String?) {
        val width = mMetrics.widthPixels
        val height = mMetrics.heightPixels
        Glide.with(context)
            .load(uri)
            .centerCrop()
            .error(mDefaultBackground)
            .into<SimpleTarget<GlideDrawable>>(
                object : SimpleTarget<GlideDrawable>(width, height) {
                    override fun onResourceReady(
                        resource: GlideDrawable,
                        glideAnimation: GlideAnimation<in GlideDrawable>
                    ) {
                        mBackgroundManager.drawable = resource
                    }
                })
        mBackgroundTimer?.cancel()
    }

    private fun startBackgroundTimer() {
        mBackgroundTimer?.cancel()
        mBackgroundTimer = Timer()
        mBackgroundTimer?.schedule(UpdateBackgroundTask(), BACKGROUND_UPDATE_DELAY.toLong())
    }

    private inner class UpdateBackgroundTask : TimerTask() {

        override fun run() {
            mHandler.post { updateBackground(mBackgroundUri) }
        }
    }

    private inner class GridItemPresenter : Presenter() {
        override fun onCreateViewHolder(parent: ViewGroup): Presenter.ViewHolder {
            val view = TextView(parent.context)
            view.layoutParams = ViewGroup.LayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT)
            view.isFocusable = true
            view.isFocusableInTouchMode = true
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.default_background))
            view.setTextColor(Color.WHITE)
            view.gravity = Gravity.CENTER
            return Presenter.ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any) {
            (viewHolder.view as TextView).text = item as String
        }

        override fun onUnbindViewHolder(viewHolder: Presenter.ViewHolder) {}
    }

    companion object {
        private val TAG = "MainFragment"

        private val BACKGROUND_UPDATE_DELAY = 300
        private val GRID_ITEM_WIDTH = 200
        private val GRID_ITEM_HEIGHT = 200
        private val NUM_ROWS = 6
        private val NUM_COLS = 15
    }
}
