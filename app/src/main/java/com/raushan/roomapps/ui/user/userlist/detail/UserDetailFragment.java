package com.raushan.roomapps.ui.user.userlist.detail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.raushan.roomapps.Config;
import com.raushan.roomapps.MainActivity;
import com.raushan.roomapps.R;
import com.raushan.roomapps.binding.FragmentDataBindingComponent;
import com.raushan.roomapps.databinding.FragmentUserDetailBinding;
import com.raushan.roomapps.ui.common.DataBoundListAdapter;
import com.raushan.roomapps.ui.common.PSFragment;
import com.raushan.roomapps.ui.item.adapter.ItemVerticalListAdapter;
import com.raushan.roomapps.utils.AutoClearedValue;
import com.raushan.roomapps.utils.Constants;
import com.raushan.roomapps.utils.PSDialogMsg;
import com.raushan.roomapps.utils.Utils;
import com.raushan.roomapps.viewmodel.item.ItemViewModel;
import com.raushan.roomapps.viewmodel.user.UserViewModel;
import com.raushan.roomapps.viewobject.Item;
import com.raushan.roomapps.viewobject.User;
import com.raushan.roomapps.viewobject.common.Status;

import java.util.List;

public class UserDetailFragment extends PSFragment implements DataBoundListAdapter.DiffUtilDispatchedInterface {


    //region Variables

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private ItemViewModel itemViewModel;
    private UserViewModel userViewModel;
    public PSDialogMsg psDialogMsg;
    private static final int REQUEST_CALL = 1;


    @VisibleForTesting
    private AutoClearedValue<FragmentUserDetailBinding> binding;
    private AutoClearedValue<ItemVerticalListAdapter> adapter;

    //endregion

    //region Override Methods

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        FragmentUserDetailBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_detail, container, false, dataBindingComponent);


        binding = new AutoClearedValue<>(this, dataBinding);


        return binding.get().getRoot();
    }


    @Override
    protected void initUIAndActions() {


        if (getActivity() instanceof MainActivity) {
            ((MainActivity) this.getActivity()).binding.toolbar.setBackgroundColor(getResources().getColor(R.color.global__primary));
            ((MainActivity) getActivity()).updateMenuIconWhite();

        }

        psDialogMsg = new PSDialogMsg(getActivity(), false);

        binding.get().userOwnItemList.setNestedScrollingEnabled(false);
        binding.get().seeAllTextView.setOnClickListener(view -> navigationController.navigateToItemListActivity(getActivity(), userViewModel.otherUserId));

        binding.get().followBtn.setOnClickListener(v -> userViewModel.setUserFollowPostObj(loginUserId, userViewModel.otherUserId));

        binding.get().phoneTextView.setOnClickListener(v -> {
            String number = binding.get().phoneTextView.getText().toString();
                if(!(number.trim().isEmpty() || number.trim().equals("-"))){
                    Utils.callPhone(this,number);
                }
        });

        binding.get().ratingBarInformation.setOnClickListener(v -> navigationController.navigateToRatingList(UserDetailFragment.this.getActivity(),binding.get().getUser().userId));
        binding.get().ratingBarInformation.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                navigationController.navigateToRatingList(UserDetailFragment.this.getActivity(),binding.get().getUser().userId);
            }
            return true;
        });
    }

    @Override
    protected void initViewModels() {
        itemViewModel = ViewModelProviders.of(this, viewModelFactory).get(ItemViewModel.class);
        userViewModel = ViewModelProviders.of(this, viewModelFactory).get(UserViewModel.class);
    }

    @Override
    protected void initAdapters() {

        ItemVerticalListAdapter nvAdapter = new ItemVerticalListAdapter(dataBindingComponent,
                item -> navigationController.navigateToItemDetailActivity(UserDetailFragment.this.getActivity(), item.id, item.title), this);
        this.adapter = new AutoClearedValue<>(this, nvAdapter);
        binding.get().userOwnItemList.setAdapter(nvAdapter);
    }

    @Override
    protected void initData() {

        try {
            if (getActivity() != null) {
                if (getActivity().getIntent() != null) {
                    if (getActivity().getIntent().getExtras() != null) {
                        userViewModel.otherUserId = getActivity().getIntent().getExtras().getString(Constants.OTHER_USER_ID);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (userViewModel.otherUserId != null) {
            if (userViewModel.otherUserId.equals(loginUserId) || loginUserId.equals("")) {

                binding.get().followBtn.setVisibility(View.GONE);
            } else {

                binding.get().followBtn.setVisibility(View.VISIBLE);
            }
        }

        //User
        userViewModel.getOtherUser(loginUserId, userViewModel.otherUserId).observe(this, listResource -> {

            if (listResource != null) {

                Utils.psLog("Got Data" + listResource.message + listResource.toString());

                switch (listResource.status) {
                    case LOADING:
                        // Loading State
                        // Data are from Local DB

                        if (listResource.data != null) {
                            //fadeIn Animation
                            fadeIn(binding.get().getRoot());

                            binding.get().setUser(listResource.data);
                            Utils.psLog("Photo : " + listResource.data.userProfilePhoto);

                            replaceUserData(listResource.data);

                        }

                        break;
                    case SUCCESS:
                        // Success State
                        // Data are from Server

                        if (listResource.data != null) {

                            //fadeIn Animation
                            //fadeIn(binding.get().getRoot());

                            binding.get().setUser(listResource.data);
                            Utils.psLog("Photo : " + listResource.data.userProfilePhoto);

                            replaceUserData(listResource.data);

                            if (listResource.data.isFollowed != null) {

                                userViewModel.isFollowed = listResource.data.isFollowed;

                            }

                        }

                        break;
                    case ERROR:
                        // Error State

                        psDialogMsg.showErrorDialog(listResource.message, getString(R.string.app__ok));
                        psDialogMsg.show();

                        userViewModel.isLoading = false;

                        break;
                    default:
                        // Default
                        userViewModel.isLoading = false;

                        break;
                }

            } else {

                // Init Object or Empty Data
                Utils.psLog("Empty Data");

            }

            // we don't need any null checks here for the SubCategoryAdapter since LiveData guarantees that
            // it won't call us if fragment is stopped or not started.
            if (listResource != null && listResource.data != null) {
                Utils.psLog("Got Data");


            } else {
                //noinspection Constant Conditions
                Utils.psLog("Empty Data");

            }
        });

        //Item
        itemViewModel.holder.userId = userViewModel.otherUserId;

        itemViewModel.setItemListByKeyObj(loginUserId, String.valueOf(Config.LOGIN_USER_ITEM_COUNT), Constants.ZERO, itemViewModel.holder);

        itemViewModel.getItemListByKeyData().observe(this, listResource -> {

            if (listResource != null) {
                switch (listResource.status) {
                    case SUCCESS:

                        if (listResource.data != null) {
                            if (listResource.data.size() > 0) {
                                itemReplaceData(listResource.data);
                            }
                        }

                        break;

                    case LOADING:

                        if (listResource.data != null) {
                            if (listResource.data.size() > 0) {
                                itemReplaceData(listResource.data);
                            }
                        }

                        break;

                    case ERROR:
                        break;
                }
            }
        });

        //For user follow post
        userViewModel.getUserFollowPostData().observe(this, result -> {
            if (result != null) {
                if (result.status == Status.SUCCESS) {
                    if (this.getActivity() != null) {
                        Utils.psLog(result.status.toString());
                        userViewModel.setLoadingState(false);
                        userViewModel.getOtherUser(loginUserId, userViewModel.otherUserId);
                    }

                } else if (result.status == Status.ERROR) {
                    if (this.getActivity() != null) {
                        Utils.psLog(result.status.toString());
                        userViewModel.setLoadingState(false);
                    }
                }
            }
        });
    }

    @Override
    public void onDispatched() {

    }


    private void replaceUserData(User user) {

        binding.get().joinedDateTextView.setText(user.addedDate);
        binding.get().nameTextView.setText(user.userName);
        binding.get().overAllRatingTextView.setText(user.overallRating);
        binding.get().ratingBarInformation.setRating(user.ratingDetails.totalRatingValue);
        String ratingCount = "( " + user.ratingCount + " )";
        binding.get().ratingCountTextView.setText(ratingCount);
        binding.get().phoneTextView.setText(user.userPhone.isEmpty() ? " - " : user.userPhone);

        if (user.verifyTypes.equals("1")) {
            binding.get().facebookImage.setVisibility(View.GONE);
            binding.get().emailImage.setVisibility(View.VISIBLE);
        }
        if (user.verifyTypes.equals("2")) {
            binding.get().facebookImage.setVisibility(View.VISIBLE);
            binding.get().emailImage.setVisibility(View.GONE);
        }

    }

    private void itemReplaceData(List<Item> itemList) {
        adapter.get().replace(itemList);
        binding.get().executePendingBindings();
    }

}
