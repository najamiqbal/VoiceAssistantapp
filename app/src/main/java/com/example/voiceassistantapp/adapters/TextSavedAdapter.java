package com.example.voiceassistantapp.adapters;

import android.content.Context;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voiceassistantapp.R;
import com.example.voiceassistantapp.activities.TextSavedActivity;
import com.example.voiceassistantapp.models.MyTextSaved;
import com.example.voiceassistantapp.utils.MyTextsavedDatabase;
import com.example.voiceassistantapp.utils.OnSwipeTouchListener;
import com.pedromassango.doubleclick.DoubleClick;
import com.pedromassango.doubleclick.DoubleClickListener;

import java.util.List;

public class TextSavedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    private List<MyTextSaved> myTextSaveds;
    private OnLongClickListener onLongClickListener;
    private MyTextsavedDatabase myTextsavedDatabase;

    public TextSavedAdapter(Context context, List<MyTextSaved> myTextSaveds) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.myTextSaveds = myTextSaveds;
        myTextsavedDatabase = new MyTextsavedDatabase(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemview = inflater.inflate(R.layout.item_text_saved, null);
        return new ViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int i) {
        final MyTextSaved myTextSaved = myTextSaveds.get(i);

        ((ViewHolder) viewHolder).mTextViewName.setText(myTextSaved.getNameMyText());
        ((ViewHolder) viewHolder).mTextViewDate.setText(myTextSaved.getDateMyText());
        ((ViewHolder) viewHolder).mTextViewdetail.setText(myTextSaved.getMyText());
        ((ViewHolder) viewHolder).main_card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onLongClickListener.onlongClicked(myTextSaved.getMyText());
                return true;
            }
        });
        ((ViewHolder)viewHolder).main_card.setOnClickListener(new DoubleClick(new DoubleClickListener() {
            @Override
            public void onSingleClick(View view) {

            }

            @Override
            public void onDoubleClick(View view) {
                if (context instanceof TextSavedActivity) {
                    ((TextSavedActivity)context).onDeleteTextSavedClicked(myTextSaved.getNameMyText());
                }
              /*  myTextsavedDatabase.deleteTextsaved(myTextSaved.getNameMyText());
                myTextSaveds.remove(myTextSaveds.get(i));
                notifyDataSetChanged();*/
                Toast.makeText(context, "Delete", Toast.LENGTH_SHORT).show();
            }
        }));

/*        ((ViewHolder)viewHolder).main_card.setOnTouchListener(new OnSwipeTouchListener(context){
            @Override
            public void onSwipeTop() {
                myTextsavedDatabase.deleteTextsaved(myTextSaved.getNameMyText());
                notifyDataSetChanged();
                Toast.makeText(context, "Delete", Toast.LENGTH_SHORT).show();
            }
        });*/

    }

    @Override
    public int getItemCount() {
        return myTextSaveds.size();
    }


    public void setOnLongClickListener(OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextViewName;
        private TextView mTextViewdetail;
        private TextView mTextViewDate;
        CardView main_card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewName = itemView.findViewById(R.id.txt_NameItemTextSaved);
            mTextViewdetail = itemView.findViewById(R.id.txt_detailItemTextSaved);
            mTextViewDate = itemView.findViewById(R.id.txt_DateItemTextSaved);
            main_card=itemView.findViewById(R.id.main_card);

        }
    }

    public interface OnLongClickListener {
        void onlongClicked(String myText);
        void onDeleteTextSavedClicked(String nameTextDelete);
    }
}
