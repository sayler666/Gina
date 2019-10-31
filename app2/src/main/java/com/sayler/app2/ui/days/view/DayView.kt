package com.sayler.app2.ui.days.view

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.TextProp
import com.sayler.app2.R
import com.sayler.app2.view.inflate
import kotlinx.android.synthetic.main.c_day_view.view.*

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class DayView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        inflate(R.layout.c_day_view)
    }

    @TextProp
    fun setTitle(title: CharSequence?) {
        dv_title.text = title
    }

    @CallbackProp
    fun setClickListener(listener: OnClickListener?) {
        root.setOnClickListener(listener)
    }
}
