package com.example.galler_v2;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class Home extends Fragment {
    RecyclerView recyclerView;
    ArrayList<ImageUrl> img = new ArrayList<>();
    View view;
    LinearLayoutManager linearLayoutManager;
    String a1="flickr.photos.getRecent",a4="6f102c62f41998d151e5a1b48713cf13",a5="json",a7="url_s";
    int a2=20,a3=1,a6=1,k=0;
    ImageView imageView;
    DataAdapter dataAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        imageView = (ImageView) view.findViewById(R.id.imageView);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        new Asy().execute();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)) {
                    a3++;
                    setcontent();


                }
            }
        });

        return view;
    }
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(view.getContext().CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
    private void setcontent() {

        if(!isOnline())
        {
            Snackbar snackbar = Snackbar
                    .make(getView(), "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                    .setAction("retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setcontent();
                        }
                    });

            snackbar.show();
            return;
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        Api api = retrofit.create(Api.class);
        Call<String> call = api.getUrls(a1,a2,a3,a4,a5,a6,a7);

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
                                String[] url= new String[50];


                                JSONObject p = obj.getJSONObject("photos");
                                JSONArray photo = p.getJSONArray("photo");
                                int j = 0;

                                for (int i = 0; i < photo.length(); i++) {
                                    JSONObject in = photo.getJSONObject(i);
                                    url[j++] = in.getString("url_s");
                                }


                                img = new ArrayList<>();
                                for(int i=0;i<=url.length/2-6;i++) {
                                    ImageUrl im = new ImageUrl();
                                    im.setImageUrl(url[i++]);
                                    im.setImageUrl2(url[i]);
                                    img.add(im);
                                }
                                if(k==0) {
                                    dataAdapter = new DataAdapter(view.getContext(), img);
                                    recyclerView.setAdapter(dataAdapter);
                                    k++;
                                }
                                else
                                    dataAdapter.addimages(img);


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
        });
    }
class Asy extends AsyncTask{
    @Override
    protected void onPreExecute() {
        super.onPreExecute();


    }

    @Override
    protected Object doInBackground(Object[] objects) {
        setcontent();
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        SystemClock.sleep(2000);
        (view.findViewById(R.id.ppb)).setVisibility(View.GONE);
    }
}
}
