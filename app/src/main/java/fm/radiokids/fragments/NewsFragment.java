package fm.radiokids.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import fm.radiokids.App;
import fm.radiokids.ArticleActivity;
import fm.radiokids.R;
import fm.radiokids.adapters.NewsAdapter;
import fm.radiokids.classes.RecyclerItemClickListener;
import fm.radiokids.interfaces.OnLoadMoreListener;
import fm.radiokids.models.Post;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NewsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private List<Post> contacts;
    private NewsAdapter newsAdapter;

    private OnFragmentInteractionListener mListener;

    public NewsFragment() {
        // Required empty public constructor
    }


    public static NewsFragment newInstance(String param1, String param2) {
        NewsFragment fragment = new NewsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        /*final SimpleDialog loader =  new SimpleDialog.Builder(getContext())
                .setContent(getString(R.string.text_loading), 7)
                .showProgress(true)
                .setBtnCancelText(getString(R.string.text_close))
                .setBtnCancelTextColor("#2861b0")
                .show();
*/
        contacts = new ArrayList<>();

        App.getApi().getPosts(0, 8).enqueue(
                new Callback<List<Post>>() {
                    @Override
                    public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                        contacts.addAll(response.body());
                        newsAdapter.notifyDataSetChanged();
                        newsAdapter.setLoaded();
                       // loader.hide();
                    }

                    @Override
                    public void onFailure(Call<List<Post>> call, Throwable t) {
                    }
                }
        );

        //find view by id and attaching adapter for the RecyclerView
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        newsAdapter = new NewsAdapter(recyclerView, contacts, getActivity());
        recyclerView.setAdapter(newsAdapter);
        recyclerView.scrollToPosition(1);
        recyclerView.hasPendingAdapterUpdates();
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO Handle item click
                        if(position != -1){
                            Intent i = new Intent(getContext(), ArticleActivity.class);
                            i.putExtra("id", contacts.get(position).getId());
                            startActivity(i);
                        }
                    }
                })
        );
        recyclerView.scrollToPosition(1);
        recyclerView.hasPendingAdapterUpdates();


        //set load more listener for the RecyclerView adapter
        newsAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                contacts.add(null);
                newsAdapter.notifyItemInserted(contacts.size() - 1);

                contacts.remove(contacts.size() - 1);
                newsAdapter.notifyItemRemoved(contacts.size());

                App.getApi().getPosts(contacts.size(), 5).enqueue(
                        new Callback<List<Post>>() {
                            @Override
                            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                                contacts.addAll(response.body());
                                newsAdapter.notifyDataSetChanged();
                                newsAdapter.setLoaded();
                            }
                            @Override
                            public void onFailure(Call<List<Post>> call, Throwable t) {
                            }
                        }
                );
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
