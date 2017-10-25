package fm.radiokids;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brouding.simpledialog.SimpleDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fm.radiokids.models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.ulogin.sdk.UloginAuthActivity;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private static final int REQUEST_ULOGIN = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 2;
    Intent socialLoginIntent;

    @InjectView(R.id.con)
    LinearLayout con;
    @InjectView(R.id.input_email)
    EditText _emailText;
    @InjectView(R.id.input_password)
    EditText _passwordText;
    @InjectView(R.id.btn_login)
    Button _loginButton;
    @InjectView(R.id.link_signup)
    TextView _signupLink;
    @InjectView(R.id.link_social_login)
    TextView _link_social_login;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

        _link_social_login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the ulogin activity
                socialLoginIntent = new Intent(getApplicationContext(), UloginAuthActivity.class);
                String[] providers = {"vkontakte", "instagram", "odnoklassniki"};
                String[] mandatory_fields = new String[]{"first_name", "last_name"};
                String[] optional_fields = new String[]{"nickname", "photo", "email"};


                socialLoginIntent.putExtra(
                        UloginAuthActivity.PROVIDERS,
                        new ArrayList(Arrays.asList(providers))
                );
                socialLoginIntent.putExtra(
                        UloginAuthActivity.FIELDS,
                        new ArrayList(Arrays.asList(mandatory_fields))
                );
                socialLoginIntent.putExtra(
                        UloginAuthActivity.OPTIONAL,
                        new ArrayList(Arrays.asList(optional_fields))
                );
                if (ContextCompat.checkSelfPermission(LoginActivity.this,
                        Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this,
                            Manifest.permission.READ_CONTACTS)) {


                    } else {

                        ActivityCompat.requestPermissions(LoginActivity.this,
                                new String[]{Manifest.permission.READ_PHONE_STATE},
                                MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);

                    }
                } else {
                    startActivityForResult(socialLoginIntent, REQUEST_ULOGIN);
                }
            }
        });
    }
/*  <?php
    global $page, $paged;
    wp_title('|', true, 'right');
    bloginfo('name');
    $site_description = get_bloginfo('description', 'display');
        if ($site_description && (is_home() || is_front_page()))
    echo " | $site_description";
        if ($paged >= 2 || $page >= 2)
    echo ' | ' . sprintf('Page %s', max($paged, $page));
        ?>*/
    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.text_process_authorize));
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        App.getApi().login(email, password).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                progressDialog.dismiss();
                User user = response.body();
                if (!user.getNickname().equals("")) {
                    onLoginSuccess(user);
                } else {
                    progressDialog.dismiss();
                    _loginButton.setEnabled(true);
                    Toast.makeText(LoginActivity.this, getString(R.string.text_wrong_pass), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(LoginActivity.this, getString(R.string.text_error_network), Toast.LENGTH_LONG).show();
                _loginButton.setEnabled(true);
                progressDialog.dismiss();
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                //int user = data.getIntExtra("user",0);
                User user = (User) data.getSerializableExtra("user");
                Intent resultIntent = new Intent();
                resultIntent.putExtra("user", user);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }

        }
        else if (requestCode == REQUEST_ULOGIN ) {
            HashMap userdata = (HashMap) data.getSerializableExtra(UloginAuthActivity.USERDATA);

            switch (resultCode) {
                case RESULT_OK:
                    //если авторизация прошла успешно, то приветствуем пользователя
                    JSONObject jo = new JSONObject(userdata);
                    String login = "";
                    String name = "";
                    String email = "";
                    String photo = "";
                    try {
                        login = jo.getString("nickname");
                        name = jo.getString("last_name") + " " + jo.getString("first_name");
                        email = jo.getString("email");
                        photo = jo.getString("photo");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    App.getApi().social(login, name, email, photo).enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            User user = response.body();
                            if (!user.getNickname().equals("")) {
                                onLoginSuccess(user);
                            } else {
                                Toast.makeText(LoginActivity.this, getString(R.string.text_error_authorize), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            //Toast.makeText(LoginActivity.this, getString(R.string.text_error_network), Toast.LENGTH_LONG).show();
                            Toast.makeText(LoginActivity.this, "LOL", Toast.LENGTH_LONG).show();
                        }
                    });
                    break;
                case RESULT_CANCELED:
                    //если авторизация завершилась с ошибкой, то выводим причину сообщение
                    if (userdata.get("error").equals("canceled")) {
                        Toast.makeText(this, getString(R.string.text_canceled), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, getString(R.string.text_error) + ": " + userdata.get("error"), Toast.LENGTH_SHORT).show();
                    }
            }
        }
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED, null);
        finish();
    }

    public void onLoginSuccess(User user) {
        _loginButton.setEnabled(true);
        Intent resultIntent = new Intent();
        resultIntent.putExtra("user", user);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    public void onLoginFailed() {
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || password.length() < 4) {
            _emailText.setError(getString(R.string.text_enter_login));
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4) {
            _passwordText.setError(getString(R.string.text_enter_correct_password));
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startActivityForResult(socialLoginIntent, REQUEST_ULOGIN);

                } else {
                    new SimpleDialog.Builder(LoginActivity.this)
                            .setTitle(getString(R.string.text_alarm))
                            .setContent(getString(R.string.text_permission_denied), 3)
                            .setBtnConfirmText(getString(R.string.text_close))
                            .show();
                }
            }

        }
    }
}
