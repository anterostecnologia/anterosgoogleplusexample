package br.com.anteros.googleplus.example;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.Scope;

import br.com.anteros.social.google.AnterosGoogle;
import br.com.anteros.social.google.entities.GoogleProfile;
import br.com.anteros.social.google.listeners.OnLoginGoogleListener;
import br.com.anteros.social.google.listeners.OnLogoutGoogleListener;
import br.com.anteros.social.google.listeners.OnProfileGoogleListener;

/**
 * Activity to demonstrate basic retrieval of the Google user's ID, email address, and basic
 * profile.
 */
public class MainActivity extends ActionBarActivity implements
        View.OnClickListener, OnLoginGoogleListener, OnLogoutGoogleListener {

    private TextView status;
    private AnterosGoogle anterosGoogle;
    private ImageView userPhoto;
    private TextView detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Views
        status = (TextView) findViewById(R.id.status);
        userPhoto = (ImageView) findViewById(R.id.user_photo);
        detail = (TextView) findViewById(R.id.detail);

        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.disconnect_button).setOnClickListener(this);

        anterosGoogle = AnterosGoogle.getInstance(this, this,this);

        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setScopes(new Scope[]{new Scope(Scopes.PLUS_LOGIN)});
    }

    @Override
    public void onStart() {
        super.onStart();
        anterosGoogle.silentLogin();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        anterosGoogle.onActivityResult(requestCode, resultCode, data);
    }


    private void updateUI(boolean signedIn)  {
        if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
            status.setText("Connected");

            anterosGoogle.getProfile(new OnProfileGoogleListener(){

                @Override
                public void onThinking() {
                }

                @Override
                public void onFail(Throwable throwable) {
                    desconectado();
                }

                @Override
                public void onComplete(GoogleProfile response) {
                    detail.setText(response.toString());
                    userPhoto.setImageBitmap(response.getImageBitmap());
                }
            });
        } else {
            desconectado();
        }
    }

    private void desconectado() {
        status.setText("Desconnected");

        findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
        findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        detail.setText("");
        userPhoto.setImageBitmap(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                anterosGoogle.login();
                break;
            case R.id.sign_out_button:
                anterosGoogle.logout();
                break;
            case R.id.disconnect_button:
                anterosGoogle.revoke();
                break;
        }
    }

    @Override
    public void onLogin() {
        updateUI(true);
    }

    @Override
    public void onCancel() {
        updateUI(false);
    }

    @Override
    public void onFail(Throwable throwable) {
        updateUI(false);
    }

    @Override
    public void onLogout() {
        updateUI(false);
    }

}