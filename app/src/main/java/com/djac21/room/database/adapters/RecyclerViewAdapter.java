package com.djac21.room.database.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import com.djac21.room.database.R;
import com.djac21.room.database.models.Model;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {

    private List<Model> data;
    private List<Model> filteredData;
    private ClickListener clickListener;
    private LongClickListener longClickListener;

    public RecyclerViewAdapter(List<Model> data) {
        this.data = data;
        this.filteredData = data;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
        Model model = filteredData.get(position);
        holder.titleTextView.setText(model.getTitle());
        holder.textTextView.setText(model.getText());
        holder.dateTextView.setText(model.getDate().toLocaleString().substring(0, 12));
        holder.itemView.setTag(model);
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setLongClickListener(LongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    @Override
    public int getItemCount() {
        return filteredData.size();
    }

    public void addItems(List<Model> modelList) {
        this.data = modelList;
        this.filteredData = modelList;
        notifyDataSetChanged();
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredData = data;
                } else {
                    List<Model> filteredList = new ArrayList<>();
                    for (Model model : data)
                        if (model.getTitle().toLowerCase().contains(charString.toLowerCase()))
                            filteredList.add(model);

                    filteredData = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredData;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredData = (ArrayList<Model>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView titleTextView, textTextView, dateTextView;

        RecyclerViewHolder(View view) {
            super(view);
            titleTextView = view.findViewById(R.id.titleTextView);
            textTextView = view.findViewById(R.id.textTextView);
            dateTextView = view.findViewById(R.id.dateTextView);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null)
                clickListener.itemClicked(view, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            if (longClickListener != null)
                longClickListener.itemLongClicked(view, getAdapterPosition());

            return true;
        }
    }

    public interface ClickListener {
        void itemClicked(View view, int position);
    }

    public interface LongClickListener {
        void itemLongClicked(View view, int position);
    }
}