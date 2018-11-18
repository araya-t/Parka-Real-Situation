package com.iplds.minimintji.iplds.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.iplds.minimintji.iplds.R;
import com.iplds.minimintji.iplds.activity.CarPositionHistoryActivity;
import com.iplds.minimintji.iplds.activity.HomeActivityNew;
import com.iplds.minimintji.iplds.dao.CarPositions.CarPositionCollection;
import com.iplds.minimintji.iplds.dao.CarPositions.CarPositions;
import com.iplds.minimintji.iplds.dao.User;
import com.iplds.minimintji.iplds.manager.HttpManager;
import com.iplds.minimintji.iplds.manager.SessionManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by nuuneoi on 11/16/2014.
 */
public class HomeFragment extends Fragment {

    private ProgressBar progressBar;
    private ImageView ivCar;
    private TextView tvStartTimeHere, tvNoParking, tvName, tvLastname, tvPosition, tvZone, tvFloor, tvBuilding, tvStartTime;;
    private  Button btnHistory;
    private SessionManager sessionManager;
    private String userToken, fcmToken = null;
    private LinearLayout layoutUserInfo, layoutCurrentMessage;
    private SwipeRefreshLayout swipLayout;

    public HomeFragment() {
        super();
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.home_fragment, container, false);

        //get fcmToken from HomeActivityNew
        HomeActivityNew homeActivityNew = (HomeActivityNew) getActivity();
        fcmToken = homeActivityNew.getFcmToken();
        Log.i("fcmToken", "HomeFragment || \n fcmToken: " + fcmToken);
        //--------------------

        initInstances(rootView);
        return rootView;
    }

    private void initInstances(View rootView) {
        // Init 'View' instance(s) with rootView.findViewById here
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        ivCar = (ImageView) rootView.findViewById(R.id.ivCar);
        tvNoParking = (TextView) rootView.findViewById(R.id.tvNoParking);
        btnHistory = (Button) rootView.findViewById(R.id.btnHistory);
        layoutUserInfo = (LinearLayout) rootView.findViewById(R.id.layoutUserInfo);
        layoutCurrentMessage = (LinearLayout) rootView.findViewById(R.id.layoutCurrentMessage);

        tvName = (TextView) rootView.findViewById(R.id.layoutUserInfo).findViewById(R.id.tvName);
        tvLastname = (TextView) rootView.findViewById(R.id.layoutUserInfo).findViewById(R.id.tvLastname);

        swipLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        tvPosition = (TextView) rootView.findViewById(R.id.layoutCurrentMessage).findViewById(R.id.tvPosition);
        tvZone = (TextView) rootView.findViewById(R.id.layoutCurrentMessage).findViewById(R.id.tvZone);
        tvFloor = (TextView) rootView.findViewById(R.id.layoutCurrentMessage).findViewById(R.id.tvFloor);
        tvBuilding = (TextView) rootView.findViewById(R.id.layoutCurrentMessage).findViewById(R.id.tvBuilding);
        tvStartTime = (TextView) rootView.findViewById(R.id.layoutCurrentMessage).findViewById(R.id.tvStartTime);
        tvStartTimeHere = (TextView) rootView.findViewById(R.id.layoutCurrentMessage).findViewById(R.id.tvStartTimeHere);

//      Hide progressbar and layoutCurrentMessage
        progressBar.setVisibility(View.GONE);
        layoutCurrentMessage.setVisibility(View.GONE);
//        initFcmToken();

        sessionManager = new SessionManager(getContext());
        userToken = sessionManager.getToken();
        Log.d("userToken","------------ user token: "+userToken);
        getUserInfo(userToken);
        refreshCurrentStatus();

        swipLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ivCar.setVisibility(View.GONE);
                tvNoParking.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                layoutCurrentMessage.setVisibility(View.GONE);
                refreshCurrentStatus();
                swipLayout.setRefreshing(false);
            }
        });

        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CarPositionHistoryActivity.class);
                intent.putExtra("fcmToken",fcmToken);
                startActivity(intent);
            }
        });

    }

//    ------------------------------------ private method ------------------------------------
    private void refreshCurrentStatus() {
        Log.d("TAG","Pass this line");

        getUserInfo(userToken);
        Call<CarPositionCollection> call = HttpManager.getInstance()
                .getServiceParka()
                .getCurrentPosition(userToken);

        call.enqueue(new Callback<CarPositionCollection>() {
            @Override
            public void onResponse(Call<CarPositionCollection> call, Response<CarPositionCollection> response) {
                layoutCurrentMessage.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                ivCar.setVisibility(View.GONE);
                tvNoParking.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    CarPositionCollection dao = response.body();
                    getUserInfo(userToken);
                    if (dao.getCarPositions() != null) {
                        CarPositions car = (CarPositions) dao.getCarPositions();

                        if (car.isDriveOut() == false) {
                            String message = car.getBuildingName() + " " + car.getFloorName() + " "
                                    + car.getZoneName() + " " + car.getPositionName() + " " +
                                    car.getTimeCreated();

                            tvPosition.setText(car.getPositionName());
                            tvZone.setText(car.getZoneName());
                            tvFloor.setText(car.getFloorName());
                            tvBuilding.setText(car.getBuildingName());

                            String dateString = car.getTimeCreated().substring(0,19).replace('T',' ');

                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            tvStartTimeHere.setText(dateString);

//                            Toast.makeText(getContext(),"message: "+message,Toast.LENGTH_LONG).show();
                        }
                    } else {
                        tvNoParking.setText("No parking.");
                        layoutCurrentMessage.setVisibility(View.GONE);
                        ivCar.setVisibility(View.VISIBLE);
                        tvNoParking.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<CarPositionCollection> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + String.valueOf(t.getMessage()), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sendDataToServer() {
        long millis = System.currentTimeMillis() / 1000L;

        Call<CarPositionCollection> call = HttpManager.getInstance()
                .getServiceParka()
                .sendXYPosition(userToken,
                        5.8,
                        11,
                        5018,
                        fcmToken,
                        millis);

        call.enqueue(new Callback<CarPositionCollection>() {
            @Override
            public void onResponse(Call<CarPositionCollection> call, Response<CarPositionCollection> response) {
                progressBar.setVisibility(View.GONE);
                layoutCurrentMessage.setVisibility(View.VISIBLE);
                layoutUserInfo.setVisibility(View.VISIBLE);
                if (response.isSuccessful()) {
                    CarPositionCollection dao = response.body();
                    if (dao.getCarPositions() != null) {
                        CarPositions car = (CarPositions) dao.getCarPositions();

                        if (car.isDriveOut() == false) {
                            String message = car.getBuildingName() + " " + car.getFloorName() + " "
                                    + car.getZoneName() + " " + car.getPositionName() + " " +
                                    car.getTimeCreated();

                            tvPosition.setText(car.getPositionName());
                            tvZone.setText(car.getZoneName());
                            tvFloor.setText(car.getFloorName());
                            tvBuilding.setText(car.getBuildingName());
                            String dateString = car.getTimeCreated().substring(0,19).replace('T',' ');
                            tvStartTimeHere.setText(dateString);

                            Toast.makeText(getContext(), dao.getMessage(), Toast.LENGTH_LONG).show();
                            Log.d("message from server", "-------- message from send data is: " + message);
                        }
                    } else {
                        tvNoParking.setText(dao.getMessage());
                        layoutCurrentMessage.setVisibility(View.GONE);
                        ivCar.setVisibility(View.VISIBLE);
                        tvNoParking.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<CarPositionCollection> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + String.valueOf(t.getMessage()), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getUserInfo(String userToken) {
        Call<User> call = HttpManager.getInstance()
                .getServiceParka()
                .getUserInfo(userToken);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User userInfo = response.body();
                Log.d("UserInfo", "------------ UserInfo" + userInfo);
                if (response.isSuccessful() && userInfo != null) {
                    // ----- waiting for fragment -----
                    //tvName.setText(userInfo.getName());
                    //tvSurname.setText(userInfo.getSurname());

                    tvName.setText(userInfo.getName());
                    tvLastname.setText(userInfo.getSurname());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + String.valueOf(t.getMessage()), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /*
     * Save Instance State Here
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance State here
    }

    /*
     * Restore Instance State Here
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            // Restore Instance State here
        }
    }
}
