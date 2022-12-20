package com.example.lesnettoyeurs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;

public class MessagesFragment extends Fragment {

    private OnListFragmentInteractionListener mListener;

    private ListeMessages mMessages;
    private MyMessageRecyclerViewAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MessagesFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            if (mMessages == null) mMessages = new ListeMessages();
            if (mAdapter == null) mAdapter = new MyMessageRecyclerViewAdapter(mMessages);
            recyclerView.setAdapter(mAdapter);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Message item);
    }

    void addMessage(int id, Date date, String author, String message)
    {
        mMessages.ajouteMessage(id,date,author,message);
        mAdapter.notifyDataSetChanged();
    }

    void deleteMessage(int id)
    {
        mMessages.deleteMessageFromIndex(id);
        mAdapter.notifyDataSetChanged();
    }
    void deleteMessageFromID(int id)
    {
        mMessages.deleteMessageFromId(id);
        mAdapter.notifyDataSetChanged();
    }

    void deleteMessages()
    {
        mMessages.deleteMessages();
        mAdapter.notifyDataSetChanged();
    }
}
