package application.aku.volleyclass.METHOD;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import application.aku.volleyclass.MainActivity;
import application.aku.volleyclass.R;

public class Custom_item extends RecyclerView.Adapter<Custom_item.MyViewHolder> {
    private LayoutInflater inflater;
    private Activity context;
    private ArrayList<Object_item> listItem = new ArrayList<>();

    public Custom_item(Activity context, ArrayList listItem){
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.listItem = listItem;
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(view);
    }

    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.tvname.setText(listItem.get(position).getName());

        holder.RLklik.setOnClickListener(v -> {
            if (context instanceof MainActivity) {
                ((MainActivity) context).klik(position, "detail");
            }
        });

        holder.ivedit.setOnClickListener(v -> {
            if (context instanceof MainActivity) {
                ((MainActivity) context).klik(position, "edit");
            }
        });

        holder.ivdelete.setOnClickListener(v -> {
            if (context instanceof MainActivity) {
                ((MainActivity) context).klik(position, "delete");
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        RelativeLayout RLklik;
        TextView tvname;
        ImageView ivedit, ivdelete;

        public MyViewHolder(View itemView) {
            super(itemView);
            RLklik = itemView.findViewById(R.id.RLklik);
            tvname = itemView.findViewById(R.id.tvname);
            ivedit = itemView.findViewById(R.id.ivedit);
            ivdelete = itemView.findViewById(R.id.ivdelete);
        }
    }
}
