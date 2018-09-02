package com.jarvis.mohammad.algofacelogin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class UserDetails extends AppCompatActivity {
    TextView userAddress, txtname, txtid, txtbirthday, txtemail, txtfriends;
    ImageView avatar;
    String Id, Name, Email, Friends, Birthday;
    double longitude,latitude;
    Geocoder geocoder;
    List<android.location.Address> addresses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);


        txtname = findViewById(R.id.name);
        txtbirthday = findViewById(R.id.birthday);
        txtfriends = findViewById(R.id.friends);
        txtemail = findViewById(R.id.email);
        avatar = findViewById(R.id.avatar);
        txtid = findViewById(R.id.Id);
        userAddress = findViewById(R.id.address);

        // getting local location of the device by geoCoder.
        geocoder = new Geocoder(this,Locale.getDefault());
         Bundle bundle= getIntent().getExtras();
         latitude = (double) bundle.get("latitude");
         longitude = (double) bundle.get("longitude");

        try {

            //passing latitude and longitude to getFromLocation to get the address of user

            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String address = addresses.get(0).getAddressLine(0);
            String area = addresses.get(0).getLocality();
            String city = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String pincode = addresses.get(0).getPostalCode();


            String fulladdress = address + " " + area + " " + city + " " + country + " " + pincode;
            userAddress.setText(fulladdress);
        } catch (IOException e) {
            e.printStackTrace();
        }


        // getting facebook data by graphRequest provided by facebook in json format.
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                getData(object);
                Log.d("response", response.toString());

            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,middle_name,last_name,email,birthday,friends");
        request.setParameters(parameters);
        request.executeAsync();
        Button logout = findViewById(R.id.button2);

        //getting back to the login page
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logOut();
                Intent login = new Intent(UserDetails.this, MainActivity.class);
                startActivity(login);
                finish();
            }
        });
     }

    private void getData(JSONObject object) {
        try {
            URL profile_picture = new URL("https://graph.facebook.com/" + object.getString("id") + "/picture?width=250&height=250");

           //using picasso to load the profile picture to the image view.
            Picasso.get().load(profile_picture.toString()).into(avatar);
            Email = object.get("email").toString();
            Name = object.get("first_name").toString() + " " + object.get("middle_name").toString() + " " + object.get("last_name").toString();
            Birthday = object.get("birthday").toString();
            Friends = object.getJSONObject("friends").getJSONObject("summary").get("total_count").toString();
            Id = object.get("id").toString();

            txtid.setText("Id: " + Id);
            txtname.setText("Name: " + Name);
            txtemail.setText("Email: " + Email);
            txtbirthday.setText("Bday: " + Birthday);
            txtfriends.setText("Friends: " + Friends);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Toast.makeText(this, "Exception ocurr", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Exception ocurr", Toast.LENGTH_SHORT).show();
        }
    }



}