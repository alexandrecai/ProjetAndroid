package com.example.lesnettoyeurs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;

public class MyMessageRecyclerViewAdapter extends RecyclerView.Adapter<MyMessageRecyclerViewAdapter.ViewHolder> {

    private final ListeMessages mValues;
    //private final OnListFragmentInteractionListener mListener;

    public MyMessageRecyclerViewAdapter(ListeMessages items) {
        mValues = items;
        //mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getTitre());
        String textContent = mValues.get(position).getContenu();
        if (textContent.length() > 50) textContent = textContent.substring(0,47)+"...";
        holder.mContentView.setText(textContent);

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void ajouteMessage(int id, Date date, String author, String contenu)
    {
        mValues.ajouteMessage(id,date,author,contenu);
        this.notifyItemInserted(mValues.size()-1);
    }
    public void supprimeMessage(int id)
    {
        int msgPos = mValues.deleteMessageFromId(id);
        if (msgPos >=0)
        {
            this.notifyItemRemoved(msgPos);
            this.notifyItemRangeChanged(msgPos,mValues.size());
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Message mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
