package com.raushan.roomapps.ui.customcamera.setting;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.raushan.roomapps.R;
import com.raushan.roomapps.binding.FragmentDataBindingComponent;
import com.raushan.roomapps.databinding.FragmentCameraSettingBinding;
import com.raushan.roomapps.ui.common.PSFragment;
import com.raushan.roomapps.utils.AutoClearedValue;
import com.raushan.roomapps.utils.Constants;
import com.raushan.roomapps.utils.PSDialogMsg;
import com.raushan.roomapps.viewmodel.image.ImageViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class CameraSettingFragment extends PSFragment {


    //region Variables

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    public PSDialogMsg psDialogMsg;

    @VisibleForTesting
    private AutoClearedValue<FragmentCameraSettingBinding> binding;


    private ImageViewModel imageViewModel;

    //endregion


    //region Override Methods

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        FragmentCameraSettingBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_camera_setting, container, false, dataBindingComponent);

        binding = new AutoClearedValue<>(this, dataBinding);

        return binding.get().getRoot();
    }

    @Override
    protected void initUIAndActions() {

        psDialogMsg = new PSDialogMsg(getActivity(), false);


        binding.get().defaultCameraSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                binding.get().defaultCameraSwitch.setChecked(true);
                binding.get().customCameraSwitch.setChecked(false);
                pref.edit().putString(Constants.CAMERA_TYPE,Constants.DEFAULT_CAMERA).apply();
            }else {
                binding.get().customCameraSwitch.setChecked(true);
                binding.get().defaultCameraSwitch.setChecked(false);
                pref.edit().putString(Constants.CAMERA_TYPE,Constants.CUSTOM_CAMERA).apply();
            }
        });

        binding.get().customCameraSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                binding.get().defaultCameraSwitch.setChecked(false);
                binding.get().customCameraSwitch.setChecked(true);
                pref.edit().putString(Constants.CAMERA_TYPE,Constants.CUSTOM_CAMERA).apply();
            }else {
                binding.get().customCameraSwitch.setChecked(false);
                binding.get().defaultCameraSwitch.setChecked(true);
                pref.edit().putString(Constants.CAMERA_TYPE,Constants.DEFAULT_CAMERA).apply();
            }
        });

    }

    @Override
    protected void initViewModels() {
        imageViewModel = ViewModelProviders.of(this, viewModelFactory).get(ImageViewModel.class);
    }

    @Override
    protected void initAdapters() {

    }

    @Override
    protected void initData() {

        imageViewModel.CAMERA_TYPE = cameraType;

        if(imageViewModel.CAMERA_TYPE.equals(Constants.CUSTOM_CAMERA)){
            binding.get().customCameraSwitch.setChecked(true);
            binding.get().defaultCameraSwitch.setChecked(false);
        }else {
            binding.get().defaultCameraSwitch.setChecked(true);
            binding.get().customCameraSwitch.setChecked(false);
        }


    }


    @Override
    public void onResume() {
        super.onResume();

    }

    //endregion


}
