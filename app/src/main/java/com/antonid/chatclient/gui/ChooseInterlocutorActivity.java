package com.antonid.chatclient.gui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.antonid.chatclient.R;
import com.antonid.chatclient.SettingsServiceProvider;
import com.antonid.chatclient.api.service.ApiProvider;
import com.antonid.chatclient.api.utils.LoadingDialogCallback;
import com.antonid.chatclient.gui.chat.ChatActivity;
import com.antonid.chatclient.models.User;

import retrofit2.Call;
import retrofit2.Response;


public class ChooseInterlocutorActivity extends AppCompatActivity {

    private EditText interlocutor;

    public static void start(Context context) {
        Intent chat = new Intent(context, ChooseInterlocutorActivity.class);
        context.startActivity(chat);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_interlocutor_activity);

        interlocutor = (EditText) findViewById(R.id.interlocutor);

        Button ok = (Button) findViewById(R.id.ok);
        ok.setOnClickListener(new OkOnClickListener());
    }

    private class OkOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            String interlocutorString = interlocutor.getText().toString();

            if (interlocutorString.isEmpty()) {
                Toast.makeText(ChooseInterlocutorActivity.this, R.string.interlocutor_empty_error, Toast.LENGTH_SHORT)
                        .show();
                return;
            }

            User loggedUser = SettingsServiceProvider.getSettingsService(ChooseInterlocutorActivity.this).load()
                    .getLoggedUser();
            if (interlocutorString.equals(loggedUser.getUsername())) {
                Toast.makeText(ChooseInterlocutorActivity.this, R.string.self_chat_error, Toast.LENGTH_SHORT).show();
                return;
            }

            ApiProvider.getChatApi().isInterlocutorExists(interlocutorString)
                    .enqueue(new IsInterlocutorExistsCallback(interlocutorString, ChooseInterlocutorActivity.this));
        }
    }

    private class IsInterlocutorExistsCallback extends LoadingDialogCallback<Boolean> {

        private String interlocutor;

        IsInterlocutorExistsCallback(String interlocutor, @NonNull Context context) {
            super(context);

            this.interlocutor = interlocutor;
        }

        @Override
        public void onResponse(Call<Boolean> call, Response<Boolean> response) {
            super.onResponse(call, response);

            boolean exists = response.body();
            if (exists) {
                ChatActivity.start(ChooseInterlocutorActivity.this, interlocutor);
            } else {
                Toast.makeText(ChooseInterlocutorActivity.this, R.string.no_interlocutor,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

}
