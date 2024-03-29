package com.raushan.roomapps.ui.city.menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.ads.AdRequest;
import com.raushan.roomapps.Config;
import com.raushan.roomapps.R;
import com.raushan.roomapps.binding.FragmentDataBindingComponent;
import com.raushan.roomapps.databinding.FragmentCityMenuBinding;
import com.raushan.roomapps.ui.common.PSFragment;
import com.raushan.roomapps.utils.AutoClearedValue;
import com.raushan.roomapps.utils.Utils;
import com.raushan.roomapps.viewmodel.user.UserViewModel;
import com.raushan.roomapps.viewobject.holder.ItemParameterHolder;

public class CityMenuFragment extends PSFragment {

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);
    private UserViewModel userViewModel;

    @VisibleForTesting
    private AutoClearedValue<FragmentCityMenuBinding> binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        FragmentCityMenuBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_city_menu, container, false, dataBindingComponent);

        binding = new AutoClearedValue<>(this, dataBinding);
        setHasOptionsMenu(true);

        return binding.get().getRoot();
    }

    @Override
    protected void initUIAndActions() {

        if (Config.SHOW_ADMOB && connectivity.isConnected()) {
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            binding.get().adView.loadAd(adRequest);
        } else {
            binding.get().adView.setVisibility(View.GONE);
        }

        checkUserLogin();

        binding.get().categoryCardView.setOnClickListener(v -> navigationController.navigateToCategoryActivity(getActivity()));

        binding.get().latestProductCardView.setOnClickListener(v -> navigationController.navigateToHomeFilteringActivity(getActivity(), new ItemParameterHolder().getRecentItem(), getString(R.string.menu__latest_item), selectedCityLat, selectedCityLng, selectedCityName));

//        binding.get().discountProductCardView.setOnClickListener(v -> navigationController.navigateToHomeFilteringActivity(getActivity(), new ItemParameterHolder().getDiscountItem(), getString(R.string.menu__discount), selectedCityLat, selectedCityLng, selectedCityName));
//
//        binding.get().featuredProductCardView.setOnClickListener(v -> navigationController.navigateToHomeFilteringActivity(getActivity(), new ItemParameterHolder().getFeaturedItem(), getString(R.string.menu__featured_item), selectedCityLat, selectedCityLng, selectedCityName));

        binding.get().trendingProductCardView.setOnClickListener(v -> navigationController.navigateToHomeFilteringActivity(getActivity(), new ItemParameterHolder().getPopularItem(), getString(R.string.menu__trending_item), selectedCityLat, selectedCityLng, selectedCityName));

        binding.get().button2.setOnClickListener(v -> navigationController.navigateToUserLoginActivity(getActivity()));
    }

    @Override
    protected void initViewModels() {
        userViewModel = ViewModelProviders.of(this, viewModelFactory).get(UserViewModel.class);
    }

    @Override
    protected void initAdapters() {

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onResume() {
        super.onResume();

        loadLoginUserId();

        checkUserLogin();
    }

    private void checkUserLogin() {
        if (loginUserId.isEmpty()) {
            binding.get().button2.setVisibility(View.VISIBLE);
            binding.get().phoneTextView.setVisibility(View.GONE);
            binding.get().statusTextView.setVisibility(View.GONE);

        } else {
            binding.get().button2.setVisibility(View.GONE);
            binding.get().phoneTextView.setVisibility(View.VISIBLE);
            binding.get().statusTextView.setVisibility(View.VISIBLE);

            userViewModel.getLoginUser().observe(this, listResource -> {
                // we don't need any null checks here for the adapter since LiveData guarantees that
                // it won't call us if fragment is stopped or not started.
                if (listResource != null && listResource.size() > 0) {
                    Utils.psLog("Got Data");

                    userViewModel.user = listResource.get(0).user;
                    Utils.psLog("Photo : " + listResource.get(0).user.userProfilePhoto);
                    binding.get().userNameTextView.setText(userViewModel.user.userName);

                    binding.get().phoneTextView.setText(userViewModel.user.userPhone);

                    binding.get().statusTextView.setText(userViewModel.user.userEmail);

                    if (!userViewModel.user.userProfilePhoto.isEmpty()) {
                        binding.get().setUser(userViewModel.user);
                    }

                } else {
                    //noinspection Constant Conditions
                    Utils.psLog("Empty Data");

                }
            });

        }
    }

}
