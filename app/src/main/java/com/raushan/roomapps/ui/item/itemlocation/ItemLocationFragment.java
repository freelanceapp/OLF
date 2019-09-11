package com.raushan.roomapps.ui.item.itemlocation;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

import com.raushan.roomapps.R;
import com.raushan.roomapps.binding.FragmentDataBindingComponent;
import com.raushan.roomapps.databinding.FragmentItemLocationBinding;
import com.raushan.roomapps.ui.common.PSFragment;
import com.raushan.roomapps.utils.AutoClearedValue;
import com.raushan.roomapps.utils.Constants;
import com.raushan.roomapps.utils.Utils;
import com.raushan.roomapps.viewmodel.itemlocation.ItemLocationViewModel;
import com.raushan.roomapps.viewobject.ItemLocation;
import com.raushan.roomapps.viewobject.common.Resource;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ItemLocationFragment extends PSFragment {

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private ItemLocationViewModel itemLocationViewModel;
    public String locationId;

    @VisibleForTesting
    private AutoClearedValue<FragmentItemLocationBinding> binding;
    private AutoClearedValue<ItemLocationAdapter> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FragmentItemLocationBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_item_location, container, false, dataBindingComponent);

        binding = new AutoClearedValue<>(this, dataBinding);

        if (getActivity() != null) {
            Intent intent = getActivity().getIntent();
            this.locationId = intent.getStringExtra(Constants.ITEM_LOCATION_TYPE_ID);

            if (getActivity().getIntent().getStringExtra(Constants.LOCATION_FLAG).equals(Constants.LOCATION_NOT_CLEAR_ICON) || getActivity().getIntent().getStringExtra(Constants.LOCATION_FLAG).equals(Constants.LOCATION_SELECT_AGAIN)) {
                setHasOptionsMenu(false);
                binding.get().changeLaterTextView.setVisibility(View.VISIBLE);
            } else {

                setHasOptionsMenu(true);
                binding.get().changeLaterTextView.setVisibility(View.GONE);

            }
        }

        return binding.get().getRoot();
    }

    @Override
    protected void initUIAndActions() {

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.clear_button, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.clear) {
            this.locationId = "";

            initAdapters();

            initData();

            navigationController.navigateBackToItemLocationFragment(this.getActivity(), this.locationId, "", "", "");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initViewModels() {

        itemLocationViewModel = ViewModelProviders.of(this, viewModelFactory).get(ItemLocationViewModel.class);
    }

    @Override
    protected void initAdapters() {

        ItemLocationAdapter nvadapter = new ItemLocationAdapter(dataBindingComponent,
                (itemLocation, id) -> {

                    if (getActivity() != null) {

                        if (getActivity().getIntent().getStringExtra(Constants.LOCATION_FLAG).equals(Constants.LOCATION_NOT_CLEAR_ICON)) {
                            navigationController.navigateToMainActivity(ItemLocationFragment.this.getActivity(), itemLocation.id, itemLocation.name, itemLocation.lat, itemLocation.lng);

                        } else if (getActivity().getIntent().getStringExtra(Constants.LOCATION_FLAG).equals(Constants.LOCATION_SELECT_AGAIN)) {
                            navigationController.navigateBackToMainActivity(ItemLocationFragment.this.getActivity(), itemLocation.id, itemLocation.name, itemLocation.lat, itemLocation.lng);

                        } else {
                            navigationController.navigateBackToItemLocationFragment(ItemLocationFragment.this.getActivity(), itemLocation.id, itemLocation.name, itemLocation.lat, itemLocation.lng);
                        }

                        ItemLocationFragment.this.getActivity().finish();

                    }
                }, this.locationId);
        this.adapter = new AutoClearedValue<>(this, nvadapter);
        binding.get().searchCategoryRecyclerView.setAdapter(nvadapter);

    }

    @Override
    protected void initData() {
        loadCategory();
    }

    private void loadCategory() {

        // Load Category List
        itemLocationViewModel.categoryParameterHolder.cityId = selectedCityId;

        if (connectivity.isConnected()) {
            itemLocationViewModel.setItemLocationListObj("", String.valueOf(itemLocationViewModel.offset));
        }

        LiveData<Resource<List<ItemLocation>>> news = itemLocationViewModel.getItemLocationListData();

        if (news != null) {

            news.observe(this, listResource -> {
                if (listResource != null) {

                    Utils.psLog("Got Data" + listResource.message + listResource.toString());

                    switch (listResource.status) {
                        case LOADING:
                            // Loading State
                            // Data are from Local DB

                            if (listResource.data != null) {
                                //fadeIn Animation
                                fadeIn(binding.get().getRoot());

                                // Update the data
                                replaceData(listResource.data);

                            }

                            break;

                        case SUCCESS:
                            // Success State
                            // Data are from Server

                            if (listResource.data != null) {
                                // Update the data
                                replaceData(listResource.data);
                            }

                            itemLocationViewModel.setLoadingState(false);

                            break;

                        case ERROR:
                            // Error State

                            itemLocationViewModel.setLoadingState(false);

                            break;
                        default:
                            // Default

                            break;
                    }

                } else {

                    // Init Object or Empty Data
                    Utils.psLog("Empty Data");

                    if (itemLocationViewModel.offset > 1) {
                        // No more data for this list
                        // So, Block all future loading
                        itemLocationViewModel.forceEndLoading = true;
                    }

                }

            });
        }

//        itemLocationViewModel.getNextPageLoadingStateData().observe(this, state -> {
//            if (state != null) {
//                if (state.status == Status.ERROR) {
//                    Utils.psLog("Next Page State : " + state.data);
//
//                    itemLocationViewModel.setLoadingState(false);
//                    itemLocationViewModel.forceEndLoading = true;
//                }
//            }
//        });

    }

    private void replaceData(List<ItemLocation> categoryList) {

        adapter.get().replace(categoryList);
        binding.get().executePendingBindings();

    }
}