package fm.radiokids.classes;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;

import com.brouding.simpledialog.SimpleDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fm.radiokids.R;
import fm.radiokids.adapters.GridViewAdapter;
import fm.radiokids.models.SmileyItem;

public class SmileyTools {
    public static HashMap<String, Integer> emoticons = new HashMap<String, Integer>();

    static {
        emoticons.put(":[", R.drawable.angry);
        emoticons.put("<bulb>", R.drawable.bulb);
        emoticons.put("<cafe>", R.drawable.cafe);
        emoticons.put("<clap>", R.drawable.clap);
        emoticons.put("<clouds>", R.drawable.clouds);
        emoticons.put(";(", R.drawable.cry);
        emoticons.put(":]", R.drawable.devil);
        emoticons.put("<gift>", R.drawable.gift);
        emoticons.put("<handshake>", R.drawable.handshake);
        emoticons.put("<heart>", R.drawable.heart);
        emoticons.put(":*", R.drawable.kissy);
        emoticons.put("xD", R.drawable.laugh_big);
        emoticons.put("<no>", R.drawable.no);
        emoticons.put("<ok>", R.drawable.ok);
        emoticons.put("<feel_peace>", R.drawable.feel_peace);
        emoticons.put("<oh_please>", R.drawable.oh_please);
        emoticons.put("<rain>", R.drawable.rain);
        emoticons.put("<scared>", R.drawable.scared);
        emoticons.put(":P", R.drawable.silly);
        emoticons.put("<snail>", R.drawable.snail);
        emoticons.put("<sun>", R.drawable.sun);
        emoticons.put("<baloons>", R.drawable.baloons);
        emoticons.put("<bye>", R.drawable.bye);
        emoticons.put("<cake>", R.drawable.cake);
        emoticons.put("<cleaver>", R.drawable.cleaver);
        emoticons.put("<cool>", R.drawable.cool);
        emoticons.put("<cry_big>", R.drawable.cry_big);
        emoticons.put("<drink>", R.drawable.drink);
        emoticons.put("<hat>", R.drawable.hat);
        emoticons.put("<heart_big>", R.drawable.heart_big);
        emoticons.put(":D", R.drawable.laugh);
        emoticons.put("<moon>", R.drawable.moon);
        emoticons.put("<offended>", R.drawable.offended);
        emoticons.put("<omg>", R.drawable.omg);
        emoticons.put("<a_phone>", R.drawable.a_phone);
        emoticons.put("<question>", R.drawable.question);
        emoticons.put(":(", R.drawable.sad);
        emoticons.put("<shy>", R.drawable.shy);
        emoticons.put(":)", R.drawable.smile);
        emoticons.put("<stars>", R.drawable.stars);
        emoticons.put("<wine>", R.drawable.wine);
    }


    static GridView smiley_grid;
    static private GridViewAdapter gridAdapter;
    static ArrayAdapter<String> adapter;

    public static void showSmileyChoosePopup(final Activity activity, final Context context, final LayoutInflater ltInflater, final View layout) {


        View view = ltInflater.inflate(R.layout.smile_layout_dialog, null, false);
        smiley_grid = view.findViewById(R.id.smiley_grid);
        gridAdapter = new GridViewAdapter(activity, R.layout.grid_item_layout, getData(activity));
        smiley_grid.setNumColumns(5);
        smiley_grid.setAdapter(gridAdapter);

        smiley_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                SmileyItem item = (SmileyItem) parent.getItemAtPosition(position);
                EditText message = layout.findViewById(R.id.message_input);
                String newMessage = message.getText()+item.getCode();
                message.setText(getSmiledText(context,newMessage));
                //Toast.makeText(activity,String.valueOf(item.getCode()),Toast.LENGTH_LONG).show();
                //Toast.makeText(activity,String.valueOf(v.getTag(position)),Toast.LENGTH_LONG).show();
            }
        });


        new SimpleDialog.Builder(context)
                //.setTitle("This is Title :)")
                // If the customView is long enough, SimpleDialog will put your layout in the ScrollView automatically
                .setCustomView(view)
                .setBtnConfirmText(context.getString(R.string.text_close))
                .setCancelableOnTouchOutside(true)
                // .setBtnConfirmTextSizeDp(16)
                // .setBtnConfirmTextColor("#1fd1ab")
                //.setBtnCancelText("Cancel", false)
                //.setBtnCancelTextColor("#555555")
                .show();
    }

    // Prepare some dummy data for gridview
    private static ArrayList<SmileyItem> getData(Activity activity) {
        final ArrayList<SmileyItem> smileyItems = new ArrayList<>();

        for (Object o : emoticons.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            System.out.println(pair.getKey() + " = " + pair.getValue());
            //it.remove(); // avoids a ConcurrentModificationException
            Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), Integer.valueOf(pair.getValue().toString()));
            String code = pair.getKey().toString();
            smileyItems.add(new SmileyItem(bitmap,code/*,Integer.valueOf(pair.getValue().toString())*/));
        }

        return smileyItems;
    }


    public static Spannable getSmiledText(Context context, String s) {
        int index;
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(s);

        for (index = 0; index < builder.length(); index++) {
            for (Map.Entry<String, Integer> entry : emoticons.entrySet()) {
                int length = entry.getKey().length();
                if (index + length > builder.length())
                    continue;
                if (builder.subSequence(index, index + length).toString()
                        .equals(entry.getKey())) {
                    builder.setSpan(new ImageSpan(context, entry.getValue()),
                            index, index + length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    index += length - 1;
                    break;
                }
            }
        }
        return builder;
    }
}
