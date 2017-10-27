package fm.radiokids;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fm.radiokids.models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    @InjectView(R.id.input_login) EditText _loginText;
    @InjectView(R.id.input_name) EditText _nameText;
    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_signup) Button _signupButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.text_process_create_account));
        progressDialog.show();

        String login = _loginText.getText().toString();
        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own signup logic here.

        App.getApi().sign(login,name,email,password,"").enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                _signupButton.setEnabled(true);
                User user = response.body();
                progressDialog.dismiss();

                switch (user.getNickname()) {
                    case "exist":
                        Toast.makeText(SignupActivity.this, getString(R.string.text_user_exist), Toast.LENGTH_LONG).show();
                        break;
                    case "wrong login":
                        Toast.makeText(SignupActivity.this, getString(R.string.text_only_latin_character), Toast.LENGTH_LONG).show();
                        break;
                    case "error":
                        Toast.makeText(SignupActivity.this, getString(R.string.text_create_account_error), Toast.LENGTH_LONG).show();
                        break;
                    default:
                        onSignupSuccess(user);
                        break;
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                _signupButton.setEnabled(true);
                progressDialog.dismiss();
                Toast.makeText(SignupActivity.this, getString(R.string.text_error_network), Toast.LENGTH_LONG).show();
            }
        });

    }


    public void onSignupSuccess(User user) {
        _signupButton.setEnabled(true);
        Intent resultIntent = new Intent();
        //resultIntent.putExtra("user", Integer.valueOf(id));
        resultIntent.putExtra("user",user);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    public void onSignupFailed() {
        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String login = _loginText.getText().toString();
        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (login.isEmpty() || login.length() < 3) {
            _loginText.setError(getString(R.string.text_fill_in_field));
            valid = false;
        } else {
            _loginText.setError(null);
        }

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError(getString(R.string.text_fill_in_field));
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError(getString(R.string.text_enter_correct_email));
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError(getString(R.string.text_fill_in_field));
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}