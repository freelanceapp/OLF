package com.raushan.roomapps.ui.item.itemcurrency;


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

import com.google.android.gms.ads.AdRequest;
import com.raushan.roomapps.Config;
import com.raushan.roomapps.R;
import com.raushan.roomapps.binding.FragmentDataBindingComponent;
import com.raushan.roomapps.databinding.FragmentItemCurrencyTypeBinding;
import com.raushan.roomapps.ui.common.PSFragment;
import com.raushan.roomapps.utils.AutoClearedValue;
import com.raushan.roomapps.utils.Constants;
import com.raushan.roomapps.utils.Utils;
import com.raushan.roomapps.viewmodel.itemcurrency.ItemCurrencyViewModel;
import com.raushan.roomapps.viewobject.ItemCurrency;
import com.raushan.roomapps.viewobject.common.Resource;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ItemCurrencyTypeFragment extends PSFragment {

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private ItemCurrencyViewModel itemCurrencyViewModel;
    public String currencyTypeId;

    @VisibleForTesting
    private AutoClearedValue<FragmentItemCurrencyTypeBinding> binding;
    private AutoClearedValue<ItemCurrencyAdapter> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FragmentItemCurrencyTypeBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_item_currency_type, container, false, dataBindingComponent);

        binding = new AutoClearedValue<>(this, dataBinding);

        setHasOptionsMenu(true);

        if (getActivity() != null) {
            Intent intent = getActivity().getIntent();
            this.currencyTypeId = intent.getStringExtra(Constants.ITEM_CURRENCY_TYPE_ID);
        }

        if (Config.SHOW_ADMOB && connectivity.isConnected()) {
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            binding.get().adView.loadAd(adRequest);
        } else {
            binding.get().adView.setVisibility(View.GONE);
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
            this.currencyTypeId = "";

            initAdapters();

            initData();

            navigationController.navigateBackToItemCurrencyTypeFragment(this.getActivity(), this.currencyTypeId, "");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initViewModels() {

        itemCurrencyViewModel = ViewModelProviders.of(this, viewModelFactory).get(ItemCurrencyViewModel.class);
    }

    @Override
    protected void initAdapters() {

        ItemCurrencyAdapter nvadapter = new ItemCurrencyAdapter(dataBindingComponent,
                new ItemCurrencyAdapter.NewsClickCallback() {
                    @Override
                    public void onClick(ItemCurrency itemCurrency, String id) {

                        navigationController.navigateBackToItemCurrencyTypeFragment(ItemCurrencyTypeFragment.this.getActivity(), itemCurrency.id, itemCurrency.currencySymbol);

                        if (ItemCurrencyTypeFragment.this.getActivity() != null) {
                            ItemCurrencyTypeFragment.this.getActivity().finish();
                        }
                    }
                }, this.currencyTypeId);
        this.adapter = new AutoClearedValue<>(this, nvadapter);
        binding.get().searchCategoryRecyclerView.setAdapter(nvadapter);

    }

    @Override
    protected void initData() {
        loadCategory();
    }

    private void loadCategory() {

        // Load Category List
        itemCurrencyViewModel.categoryParameterHolder.cityId = selectedCityId;

        itemCurrencyViewModel.setItemCurrencyListObj("", String.valueOf(itemCurrencyViewModel.offset));

        LiveData<Resource<List<ItemCurrency>>> news = itemCurrencyViewModel.getItemCurrencyListData();

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

                            itemCurrencyViewModel.setLoadingState(false);

                            break;

                        case ERROR:
                            // Error State

                            itemCurrencyViewModel.setLoadingState(false);

                            break;
                        default:
                            // Default

                            break;
                    }

                } else {

                    // Init Object or Empty Data
                    Utils.psLog("Empty Data");

                    if (itemCurrencyViewModel.offset > 1) {
                        // No more data for this list
                        // So, Block all future loading
                        itemCurrencyViewModel.forceEndLoading = true;
                    }

                }

            });
        }

//        itemCurrencyViewModel.getNextPageLoadingStateData().observe(this, state -> {
//            if (state != null) {
//                if (state.status == Status.ERROR) {
//                    Utils.psLog("Next Page State : " + state.data);
//
//                    itemCurrencyViewModel.setLoadingState(false);
//                    itemCurrencyViewModel.forceEndLoading = true;
//                }
//            }
//        });

    }

    private void replaceData(List<ItemCurrency> categoryList) {

        adapter.get().replace(categoryList);
        binding.get().executePendingBindings();

    }
}