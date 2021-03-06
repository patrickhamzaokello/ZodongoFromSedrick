package com.pkasemer.zodongofoods.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pkasemer.zodongofoods.Adapters.HomeSectionedRecyclerViewAdapter;
import com.pkasemer.zodongofoods.Apis.MovieApi;
import com.pkasemer.zodongofoods.Apis.MovieService;
import com.pkasemer.zodongofoods.BottomDialogs.ShowRoundDialogFragment;
import com.pkasemer.zodongofoods.Dialogs.OrderNotFound;
import com.pkasemer.zodongofoods.Dialogs.UpdateAppDialog;
import com.pkasemer.zodongofoods.Models.Category;
import com.pkasemer.zodongofoods.Models.HomeFeed;
import com.pkasemer.zodongofoods.OnBoarding;
import com.pkasemer.zodongofoods.R;
import com.pkasemer.zodongofoods.RootActivity;
import com.pkasemer.zodongofoods.SignUpOptions;
import com.pkasemer.zodongofoods.SplashActivity;
import com.pkasemer.zodongofoods.Utils.PaginationAdapterCallback;
import com.pkasemer.zodongofoods.Utils.PaginationScrollListener;

import java.util.List;
import java.util.concurrent.TimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Home extends Fragment implements PaginationAdapterCallback {



    private static final String TAG = "MainActivity";

    HomeSectionedRecyclerViewAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    SharedPreferences appVersion_sharedPreferences;
    RecyclerView rv;
    ProgressBar progressBar;
    LinearLayout errorLayout;
    Button btnRetry;
    TextView txtError;
    SwipeRefreshLayout swipeRefreshLayout;

    private static final int PAGE_START = 1;

    private boolean isLoading = false;
    private boolean isLastPage = false;
    // limiting to 5 for this tutorial, since total pages in actual API is very large. Feel free to modify.
    private static int TOTAL_PAGES = 5;
    //setting initial app version
    private static int NEW_APP_VERSION;
    private static int PHONE_APP_VERSION = 3;

    private int currentPage = PAGE_START;

    List<Category> categories;

    private MovieService movieService;


    public Home() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rv = view.findViewById(R.id.main_recycler);
        progressBar = view.findViewById(R.id.main_progress);
        errorLayout = view.findViewById(R.id.error_layout);
        btnRetry = view.findViewById(R.id.error_btn_retry);
        txtError = view.findViewById(R.id.error_txt_cause);
        swipeRefreshLayout = view.findViewById(R.id.main_swiperefresh);

        adapter = new HomeSectionedRecyclerViewAdapter(getContext(), this);

        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(linearLayoutManager);
        rv.setItemAnimator(new DefaultItemAnimator());

        rv.setAdapter(adapter);

        rv.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                loadNextPage();
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        //init service and load data
        movieService = MovieApi.getClient(getContext()).create(MovieService.class);

        loadFirstPage();

        btnRetry.setOnClickListener(v -> loadFirstPage());

        swipeRefreshLayout.setOnRefreshListener(this::doRefresh);


        // check application version
        checkAppVersion();


        return view;
    }
    private void doRefresh() {
        progressBar.setVisibility(View.VISIBLE);
        if (callHomeFeed().isExecuted())
            callHomeFeed().cancel();

        // TODO: Check if data is stale.
        //  Execute network request if cache is expired; otherwise do not update data.
        adapter.getMovies().clear();
        adapter.notifyDataSetChanged();
        loadFirstPage();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void loadFirstPage() {
        Log.d(TAG, "loadFirstPage: ");

        // To ensure list is visible when retry button in error view is clicked
        hideErrorView();
        currentPage = PAGE_START;

        callHomeFeed().enqueue(new Callback<HomeFeed>() {
            @Override
            public void onResponse(Call<HomeFeed> call, Response<HomeFeed> response) {
                hideErrorView();

                Log.i(TAG, "onResponse: " + (response.raw().cacheResponse() != null ? "Cache" : "Network"));

                // Got data. Send it to adapter
                categories = fetchResults(response);
                progressBar.setVisibility(View.GONE);
                if(categories.isEmpty()){
                    showCategoryErrorView();
                    return;
                } else {
                    adapter.addAll(categories);
                }

                if (currentPage < TOTAL_PAGES) adapter.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<HomeFeed> call, Throwable t) {
                t.printStackTrace();
                showErrorView(t);
            }
        });
    }



    private List<Category> fetchResults(Response<HomeFeed> response) {
        HomeFeed homeFeed = response.body();
        TOTAL_PAGES = homeFeed.getTotalPages();
        NEW_APP_VERSION = homeFeed.getAppVersion();
        Log.i("NEW_APP_VERSION", String.valueOf(NEW_APP_VERSION));
        return homeFeed.getCategories();
    }

    private void loadNextPage() {
        Log.d(TAG, "loadNextPage: " + currentPage);

        callHomeFeed().enqueue(new Callback<HomeFeed>() {
            @Override
            public void onResponse(Call<HomeFeed> call, Response<HomeFeed> response) {
                Log.i(TAG, "onResponse: " + currentPage
                        + (response.raw().cacheResponse() != null ? "Cache" : "Network"));

                adapter.removeLoadingFooter();
                isLoading = false;

                categories = fetchResults(response);
                adapter.addAll(categories);

                if (currentPage != TOTAL_PAGES) adapter.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<HomeFeed> call, Throwable t) {
                t.printStackTrace();
                adapter.showRetry(true, fetchErrorMessage(t));
            }
        });
    }


    /**
     * Performs a Retrofit call to the top rated movies API.
     * Same API call for Pagination.
     * As {@link #currentPage} will be incremented automatically
     * by @{@link PaginationScrollListener} to load next page.
     */
    private Call<HomeFeed> callHomeFeed() {
        return movieService.getHomeFeed(
                currentPage
        );
    }

    @Override
    public void retryPageLoad() {
        loadNextPage();
    }

    @Override
    public void requestfailed() {

    }


    public void checkAppVersion(){
        appVersion_sharedPreferences = getActivity().getSharedPreferences("appVersionCheck", Context.MODE_PRIVATE);
        int appversion = appVersion_sharedPreferences.getInt("version", PHONE_APP_VERSION);

        if(appversion < NEW_APP_VERSION){
            SharedPreferences.Editor editor = appVersion_sharedPreferences.edit();
            editor.putInt("version", PHONE_APP_VERSION);
            editor.commit();

//            Toast.makeText(getContext(), "appV:" + appversion + ",Update your app", Toast.LENGTH_SHORT).show();
//            showupdatedialog
            UpdateAppDialog updateAppDialog = new UpdateAppDialog();
            updateAppDialog.setCancelable(false);
            updateAppDialog.show(getActivity().getSupportFragmentManager(), "Update App");
        } else {
//            Toast.makeText(getContext(), "appV:" + appversion + "app_old:"+PHONE_APP_VERSION +"app_new:"+NEW_APP_VERSION, Toast.LENGTH_SHORT).show();

        }
    }


    /**
     * @param throwable required for {@link #fetchErrorMessage(Throwable)}
     * @return
     */
    private void showErrorView(Throwable throwable) {

        if (errorLayout.getVisibility() == View.GONE) {
            errorLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

            txtError.setText(fetchErrorMessage(throwable));
        }
    }

    private void showCategoryErrorView() {

        progressBar.setVisibility(View.GONE);

        AlertDialog.Builder android = new AlertDialog.Builder(getContext());
        android.setTitle("Coming Soon");
        android.setIcon(R.drawable.africanwoman);
        android.setMessage("This Menu Category will be updated with great tastes soon, Stay Alert for Updates.")
                .setCancelable(false)

                .setPositiveButton("Home", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //go to activity
                        Intent intent = new Intent(getActivity(), RootActivity.class);
                        startActivity(intent);
                    }
                });
        android.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //go to activity
                Intent intent = new Intent(getActivity(), RootActivity.class);
                startActivity(intent);
            }
        });
        android.create().show();

    }



    /**
     * @param throwable to identify the type of error
     * @return appropriate error message
     */
    private String fetchErrorMessage(Throwable throwable) {
        String errorMsg = getResources().getString(R.string.error_msg_unknown);

        if (!isNetworkConnected()) {
            errorMsg = getResources().getString(R.string.error_msg_no_internet);
        } else if (throwable instanceof TimeoutException) {
            errorMsg = getResources().getString(R.string.error_msg_timeout);
        }

        return errorMsg;
    }

    // Helpers -------------------------------------------------------------------------------------


    private void hideErrorView() {
        if (errorLayout.getVisibility() == View.VISIBLE) {
            errorLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Remember to add android.permission.ACCESS_NETWORK_STATE permission.
     *
     * @return
     */
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(getContext().CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

}