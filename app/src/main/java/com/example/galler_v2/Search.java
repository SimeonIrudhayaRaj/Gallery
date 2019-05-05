package com.example.galler_v2;


import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class Search extends Fragment {

    EditText ed;
    RecyclerView recyclerView;
    ArrayList<ImageUrl> img = new ArrayList<>();
    View view;
    LinearLayoutManager linearLayoutManager;
    String a1="flickr.photos.search",a2="6f102c62f41998d151e5a1b48713cf13",a3="json",a5="url_s",a6;
    int a4=20;



    ImageView imageView;
    DataAdapter dataAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_search, container, false);

        imageView = (ImageView) view.findViewById(R.id.imageView);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView2);
        linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        Button button = view.findViewById(R.id.search_button);
        ed =view.findViewById(R.id.search_bar);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a6= ed.getText().toString();
                Async async = new Async();
                async.execute();

            }
        });


        return view;


    }
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(view.getContext().CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }


    class Async extends AsyncTask{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            (view.findViewById(R.id.pb)).setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);

        }

        @Override
        protected Object doInBackground(Object[] objects) {






            if(!isOnline())
            {
                Snackbar snackbar = Snackbar
                        .make(getView(), "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                        .setAction("retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                    new Async().execute();
                            }
                        });

                snackbar.show();

            }
    else {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Api.BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            Api api = retrofit.create(Api.class);
            Call<String> call = api.getSUrls(a1,a2,a3,a4,a5,a6);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    Log.i("Responsestring", response.body().toString());
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            try {
                                JSONObject obj = new JSONObject( response.body().toString());
                                if (obj.getString("stat").equals("ok"))
                                {
                                    String[] url= new String[999];


                                    JSONObject p = obj.getJSONObject("photos");
                                    JSONArray photo = p.getJSONArray("photo");
                                    int j = 0;

                                    for (int i = 0; i < photo.length(); i++) {
                                        JSONObject in = photo.getJSONObject(i);
                                        url[j++] = in.getString("url_s");
                                    }


                                    img = new ArrayList<>();
                                    for(int i=0;i<=j;i++) {
                                        ImageUrl im = new ImageUrl();
                                        im.setImageUrl(url[i++]);
                                        im.setImageUrl2(url[i]);
                                        j--;
                                        img.add(im);
                                    }
                                    dataAdapter = new DataAdapter(view.getContext(), img);
                                    recyclerView.setAdapter(dataAdapter);



                                    recyclerView.setVisibility(View.VISIBLE);


                                }
                            }

                            catch (Exception e)
                            {
                                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();

                            }
                        } else {
                            Log.i("onEmptyResponse", "Returned empty response");
                        }
                    }
                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });}
            return null;

        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            SystemClock.sleep(1000);
            (view.findViewById(R.id.pb)).setVisibility(View.GONE);


        }
    }
}
