package com.raushan.roomapps.viewmodel.psappinfo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.raushan.roomapps.repository.appInfo.AppInfoRepository;
import com.raushan.roomapps.utils.AbsentLiveData;
import com.raushan.roomapps.utils.Utils;
import com.raushan.roomapps.viewmodel.common.PSViewModel;
import com.raushan.roomapps.viewobject.PSAppInfo;
import com.raushan.roomapps.viewobject.common.Resource;

import javax.inject.Inject;

public class PSAppInfoViewModel extends PSViewModel {

    private final LiveData<Resource<PSAppInfo>> deleteHistoryData;
    private MutableLiveData<TmpDataHolder> deleteHistoryObj = new MutableLiveData<>();

    public String appSettingLat;
    public String appSettingLng;

    @Inject
    PSAppInfoViewModel(AppInfoRepository repository) {
        deleteHistoryData = Transformations.switchMap(deleteHistoryObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }
            Utils.psLog("PSAppInfoViewModel");
            return repository.deleteTheSpecificObjects(obj.startDate, obj.endDate);
        });
    }

    public void setDeleteHistoryObj(String startDate, String endDate) {

        TmpDataHolder tmpDataHolder = new TmpDataHolder(startDate, endDate);

        this.deleteHistoryObj.setValue(tmpDataHolder);
    }

    public LiveData<Resource<PSAppInfo>> getDeleteHistoryData() {
        return deleteHistoryData;
    }

    class TmpDataHolder {
        String startDate, endDate;

        private TmpDataHolder(String startDate, String endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }

}
