package fm.radiokids;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.brouding.simpledialog.SimpleDialog;

import fm.radiokids.models.Post;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        Intent i = getIntent();
        int id = i.getIntExtra("id", 0);

        final SimpleDialog loader = new SimpleDialog.Builder(this)
                .setContent(getString(R.string.text_loading), 7)
                .showProgress(true)
                .setBtnCancelText(getString(R.string.text_close))
                .setBtnCancelTextColor("#2861b0")
                .show();

        final WebView webview =  this.findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        App.getApi().getPost(id).enqueue(
                new Callback<Post>() {
                    @Override
                    public void onResponse(Call<Post> call, Response<Post> response) {
                        String title = response.body().getTitle().getRendered();
                        String content = response.body().getContent().getRendered();
                        getSupportActionBar().setTitle(title);
                        content += "<style>body{padding:15px !important;white-space: normal;} img,iframe{width:100%;height:auto}</style>";
                        content += "" +
                                "<link href=\"http://radiokids.fm/wp-content/themes/sanfirst/dist/css/bootstrap.min.css\" rel=\"stylesheet\" />\n" +
                                "    <link href=\"http://radiokids.fm/wp-content/themes/sanfirst/style.css\" rel=\"stylesheet\" />   \n";


                        webview.loadData(content, "text/html; charset=utf-8", "UTF-8");
                        loader.hide();
                    }

                    @Override
                    public void onFailure(Call<Post> call, Throwable t) {
                        Toast.makeText(ArticleActivity.this, "ERROR", Toast.LENGTH_LONG).show();

                    }
                }
        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
