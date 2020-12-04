package kr.heewon.cafe

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import java.util.*


class UsersAdapter(context: Activity?, list: ArrayList<ReservationData>?) :
    RecyclerView.Adapter<UsersAdapter.CustomViewHolder>() {
    private var mList: ArrayList<ReservationData>? = null
    private var context: Activity? = null

    inner class CustomViewHolder(view: View) : ViewHolder(view) {
        var pnum : TextView
        var room : TextView
        var time : TextView
        var drink : TextView


        init {
            pnum = view.findViewById<View>(R.id.textView_list_pnum) as TextView
            room = view.findViewById<View>(R.id.textView_list_room) as TextView
            time = view.findViewById<View>(R.id.textView_list_time) as TextView
            drink = view.findViewById<View>(R.id.textView_list_drink) as TextView
        }
    }

    override fun onCreateViewHolder( // Adapter에서 사용할 ViewHolder설정
        viewGroup: ViewGroup,
        viewType: Int
    ): CustomViewHolder {
        val view =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.item_list, null)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(
        viewholder: CustomViewHolder,
        position: Int
    ) {
        viewholder.itemView.setOnClickListener{
            itemClickListener.onClick(it, position)
        }
        viewholder.pnum.text = mList!![position].res_pnum
        viewholder.room.text = mList!![position].res_room
        viewholder.time.text = mList!![position].res_time
        viewholder.drink.text = mList!![position].res_drink
    }

    override fun getItemCount(): Int { // 생성자로부터 받은 데이터의 갯수 측정
        return mList?.size ?: 0
    }

    init {
        this.context = context
        mList = list
    }

    //ClickListener
    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }
    private lateinit var itemClickListener : OnItemClickListener

    fun setItemClickListener(itemClickListener: OnItemClickListener) {
        this.itemClickListener = itemClickListener
    }
}