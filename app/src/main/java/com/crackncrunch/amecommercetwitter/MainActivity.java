package com.crackncrunch.amecommercetwitter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.models.User;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;

public class MainActivity extends AppCompatActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "bMOpef36pEVqzvzk3FrcEszC4";
    private static final String TWITTER_SECRET = "0fom4F5B5CIH6l5gJrmDjJWkLDrURREA0bH5iiuaDhzByf2Ntg";

    Button mCustomButton;
    TwitterAuthClient mTwitterAuthClient;
    ImageView mImageView;
    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);

        mTwitterAuthClient = new TwitterAuthClient();
        mImageView = (ImageView) findViewById(R.id.profile_img);
        mTextView = (TextView) findViewById(R.id.details_txt);

        mCustomButton = (Button) findViewById(R.id.custom_login_btn);
        mCustomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTwitterAuthClient.authorize(MainActivity.this, new
                        Callback<TwitterSession>() {
                            @Override
                            public void success(Result<TwitterSession> result) {
                                TwitterSession session = result.data;
                                final String userName = session.getUserName();

                                Call<User> userCall = Twitter.getApiClient(session).getAccountService()
                                        .verifyCredentials(true, false);
                                userCall.enqueue(new Callback<User>() {
                                    @Override
                                    public void success(Result<User> result) {
                                        User userInfo = result.data;
                                        String email = userInfo.email;
                                        String description = userInfo.description;
                                        String location = userInfo.location;
                                        int friendsCount = userInfo.friendsCount;
                                        int favouritesCount = userInfo.favouritesCount;
                                        int followersCount = userInfo.followersCount;

                                        String profileImageUrl = userInfo.profileImageUrl.replace("_normal", "");
                                        Picasso.with(getApplicationContext())
                                                .load(profileImageUrl)
                                                .into(mImageView);

                                        StringBuilder stringBuilder = new StringBuilder();
                                        stringBuilder
                                                .append("userName: " + userName).append("\n")
                                                .append("email: " + email).append("\n")
                                                .append("description: " + description).append("\n")
                                                .append("location: " + location).append("\n")
                                                .append("friendsCount: " + friendsCount).append("\n")
                                                .append("favouritesCount: " + favouritesCount).append("\n")
                                                .append("followersCount: " + followersCount);

                                        mTextView.setText(stringBuilder.toString());

                                        mCustomButton.setVisibility(View.INVISIBLE);
                                    }

                                    @Override
                                    public void failure(TwitterException exception) {

                                    }
                                });
                            }

                            @Override
                            public void failure(TwitterException exception) {
                                Toast.makeText(MainActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
    }
}
