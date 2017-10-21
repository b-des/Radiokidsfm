package fm.radiokids.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import fm.radiokids.App;
import fm.radiokids.R;
import fm.radiokids.classes.SmileyTools;
import fm.radiokids.models.ChatModel;


public class ChatAdapter extends ArrayAdapter<ChatModel> implements View.OnClickListener{
    private ArrayList<ChatModel> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtAuthor;
        TextView txtMessage;
        TextView txtDate;
        ImageView avatar;
    }

    public ChatAdapter(ArrayList<ChatModel> data, Context context) {
        super(context, R.layout.chat_item_1, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        ChatModel dataModel=(ChatModel)object;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ChatModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.chat_item_1, parent, false);
            viewHolder.txtAuthor = (TextView) convertView.findViewById(R.id.author);
            viewHolder.txtMessage = (TextView) convertView.findViewById(R.id.message);
            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.date);
            viewHolder.avatar = (ImageView) convertView.findViewById(R.id.avatar);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }




        viewHolder.txtMessage.setText(SmileyTools.getSmiledText(getContext(),dataModel.getText()));
        viewHolder.txtDate.setText(App.getDate(getContext(),dataModel.geTime()));
        viewHolder.txtAuthor.setText(dataModel.getUser());
        Glide.with(getContext()).load(dataModel.getAvatar_url()).apply(RequestOptions.circleCropTransform()).into(viewHolder.avatar);
        // Return the completed view to render on screen
        return convertView;
    }

}
