package fm.radiokids.adapters;
import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideType;
import com.bumptech.glide.request.RequestOptions;

import org.jsoup.Jsoup;

import java.util.List;

import fm.radiokids.R;
import fm.radiokids.classes.RecyclerItemClickListener;
import fm.radiokids.interfaces.OnLoadMoreListener;
import fm.radiokids.models.Post;


public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener onLoadMoreListener;
    private RecyclerItemClickListener.OnItemClickListener onItemClickListener;
    private boolean isLoading;
    private Activity activity;
    private List<Post> news;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private View.OnClickListener mClickListener;


    public NewsAdapter(RecyclerView recyclerView, List<Post> news, Activity activity) {
        this.news = news;
        this.activity = activity;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener;
    }

    public void setClickListener(View.OnClickListener callback) {
        mClickListener = callback;
    }

    @Override
    public int getItemViewType(int position) {
        return news.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_recycler_view_row, parent, false);

            return new UserViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof UserViewHolder) {
            Post contact = news.get(position);
            UserViewHolder userViewHolder = (UserViewHolder) holder;
            //userViewHolder.thumbnail.;

            Glide.with(activity).load(contact.getThumbnail()).apply(
                    new RequestOptions()
                    .placeholder(R.drawable.placeholder)
            ).into(userViewHolder.thumbnail);
           /*  userViewHolder.thumbnail.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            userViewHolder.thumbnail.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            userViewHolder.thumbnail.requestLayout();*/
            //Picasso.with(activity).load(contact.getThumbnail()).centerInside().into(userViewHolder.thumbnail);
            /*userViewHolder.thumbnail.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            userViewHolder.thumbnail.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            userViewHolder.thumbnail.requestLayout();*/
            userViewHolder.date.setText(contact.getDate().replaceAll("T","  "));
            userViewHolder.title.setText(contact.getTitle().getRendered());
            userViewHolder.description.setText(Jsoup.parse(contact.getExcerpt().getRendered()).text());

        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }




    @Override
    public int getItemCount() {
        return news == null ? 0 : news.size();
    }

    public void setLoaded() {
        isLoading = false;
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
        }
    }

    private class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView thumbnail;
        public TextView title;
        public TextView date;
        public TextView description;

        public UserViewHolder(View view) {
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            date = (TextView) view.findViewById(R.id.txt_date);
            title = (TextView) view.findViewById(R.id.txt_title);
            description = (TextView) view.findViewById(R.id.txt_description);
        }

        @Override
        public void onClick(View view) {

        }
    }


}

