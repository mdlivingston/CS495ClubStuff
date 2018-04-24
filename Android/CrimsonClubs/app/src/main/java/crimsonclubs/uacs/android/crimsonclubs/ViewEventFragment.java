package crimsonclubs.uacs.android.crimsonclubs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class ViewEventFragment extends BaseFragment {

    public int currId;

    public EventDto currEvent;

    //public ArrayList<>

    public ViewEventFragment() {

    }

    public View onCreateView(LayoutInflater inflator, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflator.inflate(R.layout.fragment_view_event, container, false);

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();


        updateUI();

    }

    public void updateUI() {
        final Gson gson = new GsonBuilder().serializeNulls().create();

        String url = "http://cclubs.us-east-2.elasticbeanstalk.com/api/events/" + Integer.toString(currId);

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
                                "Remote server could not be reached. ",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    if (response.code() == 401) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(),
                                        "Authentication failed ",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(),
                                        "An unspecified networking error has occurred\n" +
                                            "error Code: " + response.code(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
                else {
                    boolean isNull = false;

                    ArrayList<EventDto> temp = new ArrayList<EventDto>();

                    String body = response.body().string();

                    try {
                        temp = new ArrayList<EventDto>(Arrays.asList(gson.fromJson(body, EventDto[].class)));
                    } catch (JsonSyntaxException e) {
                        temp.add(gson.fromJson(body, EventDto.class));
                    }

                    if (temp.get(0) == null)
                        isNull = true;
                    else
                        currEvent = temp.get(0);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("name", currEvent.name);

                            TextView name = (TextView) getActivity().findViewById(R.id.name);
                            name.setText(currEvent.name);
                            TextView desc = (TextView) getActivity().findViewById(R.id.description);
                            desc.setText(currEvent.description);
                            TextView startDate = (TextView) getActivity().findViewById(R.id.startDate);
                            startDate.setText(currEvent.start);
                            TextView finishDate = (TextView) getActivity().findViewById(R.id.finishDate);
                            finishDate.setText(currEvent.finish);


                        }
                    });
                }
            }
        });
    }
}
