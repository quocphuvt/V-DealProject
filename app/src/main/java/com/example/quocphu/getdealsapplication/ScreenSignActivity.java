package com.example.quocphu.getdealsapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quocphu.getdealsapplication.model.User;
import com.example.quocphu.getdealsapplication.service.CheckDatePostService;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.internal.ImageRequest;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AdditionalUserInfo;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ThrowOnExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.shobhitpuri.custombuttons.GoogleSignInButton;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ScreenSignActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInButton btn_google;
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private ImageView iv;
    private List<User> list_user = new ArrayList<>();
    FirebaseAuth.AuthStateListener mAuthListner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_sign);
        Intent i = new Intent(getApplicationContext(), CheckDatePostService.class);
        startService(i);
        iv = findViewById(R.id.iv_logo);
        btn_google = findViewById(R.id.btn_google);
        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionCodeSettings actionCodeSettings =
                        ActionCodeSettings.newBuilder()
                                // URL you want to redirect back to. The domain (www.example.com) for this
                                // URL must be whitelisted in the Firebase Console.
                                .setUrl("https://www.example.com/finishSignUp?cartId=1234")
                                // This must be true
                                .setHandleCodeInApp(true)
                                .setIOSBundleId("com.example.ios")
                                .setAndroidPackageName(
                                        "com.example.android",
                                        true, /* installIfNotAvailable */
                                        "12"    /* minimumVersion */)
                                .build();
                signIn();

            }
        });
        mAuth = FirebaseAuth.getInstance();
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
        mAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    final String userUid = currentUser.getUid();
                    Query queryClient = FirebaseDatabase.getInstance().getReference("user");
                    queryClient.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            list_user.clear();
                            for (DataSnapshot itemUser : dataSnapshot.getChildren()) {
                                User user = itemUser.getValue(User.class);
                                list_user.add(user);
                                for (int i = 0; i < list_user.size(); i++) {
                                    if (userUid.equals(list_user.get(i).getId_user()) && list_user.get(i).getTypeAccount().equals("client")) {
                                        startActivity(new Intent(ScreenSignActivity.this, MainActivity.class));
                                        finish();
                                        break;
                                    } else if (userUid.equals(list_user.get(i).getId_user()) && list_user.get(i).getTypeAccount().equals("null")) {
                                        Toast.makeText(ScreenSignActivity.this, "User store not accept by Admin", Toast.LENGTH_SHORT).show();
                                        break;
                                    } else if (userUid.equals(list_user.get(i).getId_user()) && list_user.get(i).getTypeAccount().equals("store")) {
                                        startActivity(new Intent(ScreenSignActivity.this, StoreMainActivity.class));
                                        finish();
                                        break;

                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

            }
        };

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.btn_facebook);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() { //Hàm trả về kết quả
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("LoginResult", "facebook:onSuccess:" + loginResult.getAccessToken().getToken());
                handleFacebookAccessToken(loginResult.getAccessToken());
                // call this when login success
                String userID = loginResult.getAccessToken().getUserId();
//                String imgUrl = "https://graph.facebook.com/" + userID + "/picture?type=large";
//                Picasso.with(getApplicationContext()).load(imgUrl).into(iv);  Lấy ảnh từ profile thông qua thư viện Picasso
                xulysaukhilogin(loginResult);



            }

            @Override
            public void onCancel() {
                Log.d("Cancel", "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("ErrorFacebook", "facebook:onError", error);
                // ...
            }
        });
    }
    //Hàm kiểm tra xem có user nào đang đăng nhập lúc bắt đầu chạy ứng dụng không
    @Override
    public void onStart() {
        super.onStart();
        // Kiểm tra nếu user google đã đăng nhập thì làm gì.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mAuth.addAuthStateListener(mAuthListner);

    }
    //Hàm gọi cửa sổ đăng nhập Google Account
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.clearDefaultAccountAndReconnect();
        }
        startActivityForResult(signInIntent, 999);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 999) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Đăng nhập google thành công, xác thực với Firbase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                GoogleSignInResult result =Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Đăng nhập google thất bại, cập nhật giao diện
                Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show();
                // ...
            }
        }
    }
    //Hàm xử lí xác thực google với Firbase
    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                            if(isNew){
                                startActivity(new Intent(ScreenSignActivity.this,ProfileUserActivity.class));
                            }else {
                                checkUserClient();
                                for (int i = 0; i < list_user.size(); i++) {
                                    if (user.getUid().equals(list_user.get(i).getId_user()) && list_user.get(i).getTypeAccount().equals("client")) {
                                        startActivity(new Intent(ScreenSignActivity.this,MainActivity.class));
                                        break;
                                    } else if (user.getUid().equals(list_user.get(i).getId_user()) && list_user.get(i).getTypeAccount().equals("null")) {
                                        Toast.makeText(ScreenSignActivity.this, "User store not accept by Admin", Toast.LENGTH_SHORT).show();
                                        break;
                                    } else if (user.getUid().equals(list_user.get(i).getId_user()) && list_user.get(i).getTypeAccount().equals("store")) {
                                        startActivity(new Intent(ScreenSignActivity.this,StoreMainActivity.class));
                                        break;

                                    }
                                }
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(ScreenSignActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }
    //Xử lí hàm xác thực facebook với Firebase
    private void handleFacebookAccessToken(final AccessToken token) {
        Log.d("FacebookToken", "handleFacebookAccessToken:" + token.getToken());
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                            if (token.getUserId().equals("1612894812190330")){
                                startActivity(new Intent(ScreenSignActivity.this,AdminMainActivity.class));
                            }
                            else if (isNew){
                                startActivity(new Intent(ScreenSignActivity.this,ProfileUserActivity.class));
                            }
                            else { checkUserClient();
                                for (int i = 0; i < list_user.size(); i++) {
                                    if (user.getUid().equals(list_user.get(i).getId_user()) && list_user.get(i).getTypeAccount().equals("client")) {
                                        startActivity(new Intent(ScreenSignActivity.this,MainActivity.class));
                                        break;
                                    } else if (user.getUid().equals(list_user.get(i).getId_user()) && list_user.get(i).getTypeAccount().equals("null")) {
                                        Toast.makeText(ScreenSignActivity.this, "User store not accept by Admin", Toast.LENGTH_SHORT).show();
                                        break;
                                    } else if (user.getUid().equals(list_user.get(i).getId_user()) && list_user.get(i).getTypeAccount().equals("store")) {
                                        startActivity(new Intent(ScreenSignActivity.this,StoreMainActivity.class));
                                        break;

                                    }
                                }}



                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("FacebookLoginFails", "signInWithCredential:failure", task.getException());
                            Toast.makeText(ScreenSignActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }
    //    FirebaseAuth.getInstance().signOut(); Đăng xuất account
    public void xulysaukhilogin(LoginResult loginResult)
    {
        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),new GraphRequest.GraphJSONObjectCallback()
        {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response)
            {
                Log.v("LoginActivity", response.toString());
                // Application code
                try {
                    String name = object.getString("name");
//                    Toast.makeText(ScreenSignActivity.this, "Welcome back! Admin "+name, Toast.LENGTH_SHORT).show();
                    Log.d("iduser",object.getString("id"));
                } catch (Exception e)
                {
                    e.printStackTrace();
                    Toast.makeText(ScreenSignActivity.this, "loi"+ e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email ");
        request.setParameters(parameters);request.executeAsync();

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("Failed",connectionResult + "");
    }
    private void handleSignInResult(GoogleSignInResult result) {
        if(result.isSuccess()){
            // lưu lại người đã đăng nhập vào show lên
            GoogleSignInAccount act=result.getSignInAccount();
            // Lấy thư viện PICASSO về để load hình
//            Picasso.with(this).load(act.getPhotoUrl()).into(iv);
        }
        else {

        }
    }
    private void checkUserClient(){
        Query queryClient = FirebaseDatabase.getInstance().getReference("user");
        queryClient.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list_user.clear();
                for(DataSnapshot itemUser : dataSnapshot.getChildren()){
                    User user = itemUser.getValue(User.class);
                    list_user.add(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onDestroy() {
//        stopService(new Intent(getApplicationContext(),CheckDatePostService.class));
        super.onDestroy();
    }
}