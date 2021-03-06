package com.example.joaovitor.divulgadoreventos.Interfaces;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.joaovitor.divulgadoreventos.R;

import java.util.ArrayList;
import java.util.List;

public class telaLogin extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    // Interação com a activity.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private TextView Txt_SemLogin;
    private TextView Txt_Criar;
    private TextInputLayout Til_EmailLogin;
    private TextInputLayout Til_SenhaLogin;
    private ImageView Img_Facebook;
    private ImageView Img_Googleplus;

    /*Acompanha a tarefa de login, para garantir que possa ser cancelada quando preciso.
    Por exemplo, caso o login/email estiver errado ou não estiverem sincronizados um com o outro
    */
    private UserLoginTask mAuthTask = null;

    private void AbrirMain(){
        Intent intent = new Intent(this,telaMain.class);
        startActivity(intent);
        finish();
    }

    private void AbrirCadastro(){
        Intent intent = new Intent(this,telaCadastro.class);
        startActivity(intent);
        finish();
    }

    /*Array com credenciais de usuário para login e testes do aplicativo.
     TODO: Remover este array quando o aplicativo for usar credenciais reais.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world","teste@example.com:teste"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.Txt_EmailLogin);
        mPasswordView = (EditText) findViewById(R.id.Txt_Pw);
        Til_EmailLogin = (TextInputLayout) findViewById(R.id.Til_EmailLogin);
        Til_SenhaLogin = (TextInputLayout) findViewById(R.id.Til_SenhaLogin);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.Btn_Login);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();

            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        Txt_SemLogin = (TextView) findViewById(R.id.Txt_SemLogin);
        Txt_Criar = (TextView) findViewById(R.id.Txt_Criar);
        Img_Facebook = (ImageView) findViewById(R.id.Img_Facebook);
        Img_Googleplus = (ImageView) findViewById(R.id.Img_Googleplus);

        Txt_Criar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AbrirCadastro();
            }
        });

        Img_Facebook.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Img_Googleplus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Txt_SemLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AbrirMain();
            }
        });

    }

    //Faz a checagem da tentativa de login ou criação de senha.
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        Til_EmailLogin.setError(null);
        Til_SenhaLogin.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Verifica se a senha é válida ou inválida
        if (TextUtils.isEmpty(password) && isPasswordValid(password)) {
            Til_SenhaLogin.setError(getString(R.string.Erro_PasswordInvalido));
            focusView = Til_SenhaLogin;
            cancel = true;
        }

        // Verifica se o e-mail é valido.
        if (TextUtils.isEmpty(email)) {
            Til_EmailLogin.setError(getString(R.string.Erro_CampoObrigatorio));
            focusView = Til_EmailLogin;
            cancel = true;
        } else if (!isEmailValid(email)) {
            Til_EmailLogin.setError(getString(R.string.Erro_EmailInvalido));
            focusView = Til_EmailLogin;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            /* Mostra a barra de progresso do login e abre a tela main para o usuário,
            caso a e-mail ou o password estiver errado mensagens aparecerão.*/
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
            AbrirMain();
        }
    }

    // verifica se o e-mail é valido, se ele conter o "@".
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic

        return email.contains("@example.com");
    }

    // verifica se o password é valido, se ele houver mais que 4 digitos.
    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic

        return password.length() > 5;
    }

    // Mostra a barra de progresso e esconde a tela de login.
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

         return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    //Função de auto complete no e-mail.
    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(telaLogin.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: Tenta fazer uma conexão com o aplicativo na rede.
            try {
                // Simula o acesso com internet
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            //Lê o array das credenciais padrões, para fazer o login.
            for (String credential : DUMMY_CREDENTIALS) {

                String[] pieces = credential.split(":");

                if (pieces[0].equals(mEmail)) {
                    // faz a checagem se a conta existe, caso exista, retorna um valor true.
                    return pieces[1].equals(mPassword);
                }

            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.Erro_PasswordIncorreto));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

