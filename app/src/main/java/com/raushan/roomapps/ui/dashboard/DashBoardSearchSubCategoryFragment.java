package com.raushan.roomapps.ui.dashboard;

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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.ads.AdRequest;
import com.raushan.roomapps.Config;
import com.raushan.roomapps.R;
import com.raushan.roomapps.binding.FragmentDataBindingComponent;
import com.raushan.roomapps.databinding.FragmentSearchCategoryBinding;
import com.raushan.roomapps.ui.common.PSFragment;
import com.raushan.roomapps.ui.subcategory.adapter.SearchSubCategoryAdapter;
import com.raushan.roomapps.utils.AutoClearedValue;
import com.raushan.roomapps.utils.Constants;
import com.raushan.roomapps.viewmodel.itemsubcategory.ItemSubCategoryViewModel;
import com.raushan.roomapps.viewobject.ItemSubCategory;
import com.raushan.roomapps.viewobject.common.Resource;
import com.raushan.roomapps.viewobject.common.Status;

import java.util.List;

public class DashBoardSearchSubCategoryFragment extends PSFragment {

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private ItemSubCategoryViewModel itemSubCategoryViewModel;
    private String catId;
    private String subCatId;

    @VisibleForTesting
    private AutoClearedValue<FragmentSearchCategoryBinding> binding;
    private AutoClearedValue<SearchSubCategoryAdapter> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FragmentSearchCategoryBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_category, container, false, dataBindingComponent);

        binding = new AutoClearedValue<>(this, dataBinding);

        setHasOptionsMenu(true);

        if (getActivity() != null) {
            Intent intent = getActivity().getIntent();
            this.catId = intent.getStringExtra(Constants.CATEGORY_ID);
            this.subCatId = intent.getStringExtra(Constants.SUBCATEGORY_ID);
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
            this.subCatId = "";

            initAdapters();

            initData();

            navigationController.navigateBackToSearchFragmentFromSubCategory(this.getActivity(), this.subCatId, "");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initViewModels() {

        itemSubCategoryViewModel = ViewModelProviders.of(this, viewModelFactory).get(ItemSubCategoryViewModel.class);
    }

    @Override
    protected void initAdapters() {

        SearchSubCategoryAdapter nvadapter = new SearchSubCategoryAdapter(dataBindingComponent,
                subCategory -> {

                    navigationController.navigateBackToSearchFragmentFromSubCategory(this.getActivity(), subCategory.id, subCategory.name);

                    if (getActivity() != null) {
                        this.getActivity().finish();
                    }

                }, this.subCatId);


        this.adapter = new AutoClearedValue<>(this, nvadapter);
        binding.get().searchCategoryRecyclerView.setAdapter(nvadapter);
    }

    @Override
    protected void initData() {
        loadCategory();
    }

    private void loadCategory() {

        // Load Category List
//        itemSubCategoryViewModel.setNextPageSubCategoryListByCatIdObj(loginUserId, String.valueOf(Config.LIST_CATEGORY_COUNT), String.valueOf(subCategoryViewModel.offset), this.catId);
        itemSubCategoryViewModel.setSubCategoryListByCatIdObj(this.catId, "", String.valueOf(itemSubCategoryViewModel.offset));

        LiveData<Resource<List<ItemSubCategory>>> news = itemSubCategoryViewModel.getSubCategoryListByCatIdData();

        if (news != null) {

            news.observe(this, listResource -> {
                if (listResource != null) {

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

                            itemSubCategoryViewModel.setLoadingState(false);

                            break;

                        case ERROR:
                            // Error State

                            itemSubCategoryViewModel.setLoadingState(false);

                            break;
                        default:
                            // Default

                            break;
                    }

                } else {

                    // Init Object or Empty Data

                    if (itemSubCategoryViewModel.offset > 1) {
                        // No more data for this list
                        // So, Block all future loading
                        itemSubCategoryViewModel.forceEndLoading = true;
                    }

                }

            });
        }

        itemSubCategoryViewModel.getNextPageLoadingStateData().observe(this, state -> {
            if (state != null) {
                if (state.status == Status.ERROR) {

                    itemSubCategoryViewModel.setLoadingState(false);
                    itemSubCategoryViewModel.forceEndLoading = true;
                }
            }
        });
    }

    private void replaceData(List<ItemSubCategory> categoryList) {

        adapter.get().replace(categoryList);
        binding.get().executePendingBindings();

    }
}
