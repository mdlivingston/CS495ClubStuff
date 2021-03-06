package crimsonclubs.uacs.android.crimsonclubs;

import android.app.FragmentTransaction;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import mehdi.sakout.fancybuttons.FancyButton;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class EditEventFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public EditEventFragment() {
        // Required empty public constructor
    }

   /*
    public static CreateEventFragment newInstance(String param1, String param2) {
        CreateEventFragment fragment = new CreateEventFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    */

    /*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit_event, container, false);


        return view;
    }

    public void sendEvent(final EditEventDto newEvent){
        String id = this.getArguments().getString("id");
        String token;
        token = main.getIntent().getStringExtra("bearerToken");
        Log.e("token=",token);

        final Gson gson = new GsonBuilder().serializeNulls().create();
        //String url = "http://cclubs.us-east-2.elasticbeanstalk.com/api/events?token=" + token;
        String url = "http://cclubs.us-east-2.elasticbeanstalk.com/api/events";

        Log.e("url",url);

        RequestBody requestBody = new FormBody.Builder()
                .add("name", newEvent.name)
                .add("description", newEvent.description)
                .add("start", newEvent.start)
                .add("finish", newEvent.finish)
                .add("isGroupEvent", "true")
                .add("eventId", id)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + MainActivity.bearerToken)
                .put(requestBody)
                .build();

        System.out.println(request.toString());

        MainActivity.client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();

                main.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(main, "Failure", Toast.LENGTH_LONG).show();
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try  {
                    ResponseBody responseBody = response.body();
                    System.out.println("Response: " + response.toString());
                    System.out.println("ResponseBody: " + responseBody.string());
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    //Headers responseHeaders = response.headers();
                    /*
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }
                    */

                    //System.out.println(responseBody.string());

                    Context context = main;

                    main.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(main, "Success", Toast.LENGTH_LONG).show();
                        }
                    });

                    //Toast.makeText(context, "Success", Toast.LENGTH_LONG).show();

                    //((MainActivity)main).goToLastFragment();

                    //change to view detailed event for event just created when finished
                    BrowseEventsFragment nextFrag = new BrowseEventsFragment();

                    nextFrag.clubId = newEvent.clubId;
                    main.goToFragment(nextFrag);


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        /*
        try {
            Response response = MainActivity.client.newCall(request).enqueue(new Callback());

            Log.e("response", response.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        /*
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this,
                                    "Remote server could not be reached. ",
                                    Toast.LENGTH_LONG).show();
                        }
                });
            }
        });
        */
    }
    /*
    public void showSuccessDialog() {
        DialogFragment newFragment = new SuccessEventDialogFragment();
        newFragment.show();
    }
    */
    @Override
    public void onResume(){
        super.onResume();

        FancyButton btnInput = (FancyButton) main.findViewById(R.id.btn_select);

        // Inflate the layout for this fragment
        btnInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText inputName = (EditText) getView().findViewById(R.id.inputName);
                final EditText inputDesc = (EditText) getView().findViewById(R.id.inputDesc);
                final EditText inputStart = (EditText) getView().findViewById(R.id.inputStartDate);
                final EditText inputFinish = (EditText) getView().findViewById(R.id.inputFinishDate);

                String eventName = inputName.getText().toString();
                String eventDesc = inputDesc.getText().toString();
                String eventStart = inputStart.getText().toString();
                String eventFinish = inputFinish.getText().toString();

                ArrayList<Integer> clubs = new ArrayList<>();
                clubs.add(1);
                clubs.add(2);

                EditEventDto newEvent = new EditEventDto();
                newEvent.name = eventName;
                newEvent.description = eventDesc;
                newEvent.start = eventStart;
                newEvent.finish = eventFinish;
                newEvent.clubId = 1;
                newEvent.isGroupEvent = true;
                newEvent.clubIds = clubs;

                View view2 = main.getCurrentFocus();
                if (view2 != null) {
                    InputMethodManager imm = (InputMethodManager) main.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view2.getWindowToken(), 0);
                }

                //Pass to Database
                sendEvent(newEvent);
            }
        });



        FancyButton del = (FancyButton) main.findViewById(R.id.btn_delete);

        final int eventId = getArguments().getInt("id");

        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteEvent(eventId);
            }
        });
        //updateList();
    }

    public void deleteEvent(int eventId){
        final Gson gson = new GsonBuilder().serializeNulls().create();

        String url = "http://cclubs.us-east-2.elasticbeanstalk.com/api/events/" + eventId ;

        RequestBody body = RequestBody.create(null, new byte[]{}); // empty POST request

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + MainActivity.bearerToken)
                .delete()
                .build();


        MainActivity.client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                main.runOnUiThread(new Runnable() {
                                       @Override
                                       public void run() {
                                           Toast.makeText(main,
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
                        main.runOnUiThread(new Runnable() {
                                               @Override
                                               public void run() {
                                                   Toast.makeText(main,
                                                           "Authentication failed.",
                                                           Toast.LENGTH_LONG).show();

                                               }
                                           }

                        );
                    }
                    else{
                        main.runOnUiThread(new Runnable() {
                                               @Override
                                               public void run() {
                                                   Toast.makeText(main,
                                                           "An unspecified networking error has occurred\n" +
                                                                   "Error Code: " + response.code(),
                                                           Toast.LENGTH_LONG).show();

                                               }
                                           }

                        );
                    }
                }
                else {

                    // Run view-related code back on the main thread
                    main.runOnUiThread(new Runnable() {
                                           @Override
                                           public void run() {

                                               Toast.makeText(main, "Successfully deleted event", Toast.LENGTH_SHORT).show();

                                               BrowseEventsFragment f = new BrowseEventsFragment();

                                               main.goToFragment(f);

                                           }
                                       }
                    );
                }
            }

        });
    }
}
