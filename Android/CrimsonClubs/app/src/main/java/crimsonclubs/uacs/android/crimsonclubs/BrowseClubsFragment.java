package crimsonclubs.uacs.android.crimsonclubs;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

import static android.util.Log.e;


/**
 * A simple {@link Fragment} subclass.
 */
public class BrowseClubsFragment extends BaseFragment  {

    public ArrayList<ClubDto> objs = new ArrayList<>();
    public ClubAdapter adapter;


    public BrowseClubsFragment() {
        // Required empty public constructor
    }



    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_browse_clubs, container, false);
    }

    @Override
    public void onResume(){
        super.onResume();

        updateList();

    }

    public void updateList(){


        final Gson gson = new GsonBuilder().serializeNulls().create();

            String url = "http://cclubs.us-east-2.elasticbeanstalk.com/api/clubs";


            Request request = new Request.Builder()
                    .url(url)
                    .header("Authorization", "Bearer " + MainActivity.bearerToken)
                    .build();


            MainActivity.client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(new Runnable() {
                                                           @Override
                                                           public void run() {
                                                               Toast.makeText(getActivity(),
                                                                       "Remote server could not be reached. "
                                                                       ,Toast.LENGTH_LONG).show();
                                                           }
                                                       }

                    );
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {

                    if (!response.isSuccessful()) {
                        if (response.code() == 401) {
                            getActivity().runOnUiThread(new Runnable() {
                                                                   @Override
                                                                   public void run() {
                                                                       Toast.makeText(getActivity(),
                                                                               "Authentication failed.",
                                                                               Toast.LENGTH_LONG).show();

                                                                   }
                                                               }

                            );
                        }
                        else{
                            getActivity().runOnUiThread(new Runnable() {
                                                                   @Override
                                                                   public void run() {
                                                                       Toast.makeText(getActivity(),
                                                                               "An unspecified networking error has occurred\n" +
                                                                                       "Error Code: " + response.code(),
                                                                               Toast.LENGTH_LONG).show();

                                                                   }
                                                               }

                            );
                        }
                    }
                    else {
                        boolean isNull = false;

                        ArrayList<ClubDto> temp = new ArrayList<ClubDto>();

                        String body = response.body().string();


                        try {
                            temp = new ArrayList<ClubDto>(Arrays.asList(gson.fromJson(body, ClubDto[].class)));
                        } catch (JsonSyntaxException e) {
                            temp.add(gson.fromJson(body, ClubDto.class));
                        }

                        if (temp.get(0) == null) { //read failed
                            isNull = true;
                        }

                        objs.clear();
                        objs.addAll(temp);


                        // Run view-related code back on the main thread
                        getActivity().runOnUiThread(new Runnable() {
                                                               @Override
                                                               public void run() {

                                                                   adapter = new ClubAdapter(objs);
                                                                   final ListView lv = (ListView) getActivity().findViewById(R.id.infoList);

                                                                   //lv.setOnItemClickListener( infoItemClickListener());

                                                                   lv.setAdapter(adapter);

                                                                   adapter.notifyDataSetChanged();
                                                               }
                                                           }
                        );
                    }
                }

            });
        }
        }

