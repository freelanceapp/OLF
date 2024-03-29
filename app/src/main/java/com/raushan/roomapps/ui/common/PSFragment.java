package com.raushan.roomapps.ui.common;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.raushan.roomapps.Config;
import com.raushan.roomapps.R;
import com.raushan.roomapps.di.Injectable;
import com.raushan.roomapps.utils.Connectivity;
import com.raushan.roomapps.utils.Constants;
import com.raushan.roomapps.utils.Utils;

import javax.inject.Inject;

/**
 * Parent class for all fragment in this project.
 * Created by Panacea-Soft on 12/2/17.
 * Contact Email : teamps.is.cool@gmail.com
 */

public abstract class PSFragment extends Fragment implements Injectable {

    //region Variables

    @Inject
    protected ViewModelProvider.Factory viewModelFactory;

    @Inject
    protected NavigationController navigationController;

    @Inject
    protected Connectivity connectivity;

    @Inject
    protected SharedPreferences pref;

    protected String loginUserId, facebookId;

    protected String loginUserEmail;

    protected String loginUserPwd;

    protected String loginUserName;

    protected String selectedCityId;

    protected String selectedCityName;

    protected String selectedCityLat;

    protected String selectedCityLng;

    protected String versionNo;

    protected String consent_status;

    protected Boolean force_update = false;

    protected String force_update_msg, force_update_title;

    private boolean isFadeIn = false;

    protected String selectedLat, selectedLng;

    protected String cameraType;

    protected String selected_location_id, selected_location_name;

    protected String userOldEmail, userOldPassword, userOldName, userOldId;

    protected String profileType;

    //endregion


    //region Override Methods
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadLoginUserId();

        initViewModels();

        initUIAndActions();

        initAdapters();

        initData();
    }

    //endregion


    //region Methods

    protected void loadLoginUserId() {
        try {

            if (getActivity() != null && getActivity().getBaseContext() != null) {

                loginUserId = pref.getString(Constants.USER_ID, Constants.EMPTY_STRING);
                facebookId = pref.getString(Constants.FACEBOOK_ID, Constants.EMPTY_STRING);
                loginUserEmail = pref.getString(Constants.USER_EMAIL, Constants.EMPTY_STRING);
                loginUserPwd = pref.getString(Constants.USER_PASSWORD, Constants.EMPTY_STRING);
                if (!facebookId.isEmpty()) {
                    loginUserPwd = facebookId;
                    loginUserEmail = facebookId;
                }
                loginUserName = pref.getString(Constants.USER_NAME, Constants.EMPTY_STRING);
                selectedCityId = pref.getString(Constants.CITY_ID, Constants.EMPTY_STRING);
                selectedCityName = pref.getString(Constants.CITY_NAME, Constants.EMPTY_STRING);
                selectedCityLat = pref.getString(Constants.CITY_LAT, Constants.EMPTY_STRING);
                selectedCityLng = pref.getString(Constants.CITY_LNG, Constants.EMPTY_STRING);
                versionNo = pref.getString(Constants.APPINFO_PREF_VERSION_NO, Constants.EMPTY_STRING);
                force_update = pref.getBoolean(Constants.APPINFO_PREF_FORCE_UPDATE, false);
                force_update_msg = pref.getString(Constants.APPINFO_FORCE_UPDATE_MSG, Constants.EMPTY_STRING);
                selectedLat = pref.getString(Constants.LAT, Constants.EMPTY_STRING);
                selectedLng = pref.getString(Constants.LNG, Constants.EMPTY_STRING);
                cameraType = pref.getString(Constants.CAMERA_TYPE, Constants.DEFAULT_CAMERA);
                force_update_title = pref.getString(Constants.APPINFO_FORCE_UPDATE_TITLE, Constants.EMPTY_STRING);
                selected_location_id = pref.getString(Constants.SELECTED_LOCATION_ID, Constants.EMPTY_STRING);
                selected_location_name = pref.getString(Constants.SELECTED_LOCATION_NAME, Constants.EMPTY_STRING);
                userOldEmail = pref.getString(Constants.USER_OLD_EMAIL, Constants.EMPTY_STRING);
                userOldPassword = pref.getString(Constants.USER_OLD_PASSWORD, Constants.EMPTY_STRING);
                userOldName = pref.getString(Constants.USER_OLD_NAME, Constants.EMPTY_STRING);
                userOldId = pref.getString(Constants.USER_OLD_ID, Constants.EMPTY_STRING);
                consent_status = pref.getString(Config.CONSENTSTATUS_CURRENT_STATUS, Config.CONSENTSTATUS_CURRENT_STATUS);

            }

        } catch (NullPointerException ne) {
            Utils.psErrorLog("Null Pointer Exception.", ne);
        } catch (Exception e) {
            Utils.psErrorLog("Error in getting notification flag data.", e);
        }
    }

    protected abstract void initUIAndActions();

    protected abstract void initViewModels();

    protected abstract void initAdapters();

    protected abstract void initData();

    protected void fadeIn(View view) {

        if (!isFadeIn) {
            view.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));
            isFadeIn = true; // Fade in will do only one time.
        }
    }
    //endregion

}
