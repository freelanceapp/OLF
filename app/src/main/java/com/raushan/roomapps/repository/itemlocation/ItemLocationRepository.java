package com.raushan.roomapps.repository.itemlocation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.raushan.roomapps.AppExecutors;
import com.raushan.roomapps.Config;
import com.raushan.roomapps.api.ApiResponse;
import com.raushan.roomapps.api.PSApiService;
import com.raushan.roomapps.db.ItemLocationDao;
import com.raushan.roomapps.db.PSCoreDb;
import com.raushan.roomapps.repository.common.NetworkBoundResource;
import com.raushan.roomapps.repository.common.PSRepository;
import com.raushan.roomapps.utils.Utils;
import com.raushan.roomapps.viewobject.ItemLocation;
import com.raushan.roomapps.viewobject.common.Resource;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ItemLocationRepository extends PSRepository {
    private ItemLocationDao itemLocationDao;

    @Inject
    ItemLocationRepository(PSApiService psApiService, AppExecutors appExecutors, PSCoreDb db, ItemLocationDao itemLocationDao) {

        super(psApiService, appExecutors, db);
        this.itemLocationDao = itemLocationDao;
    }

    public LiveData<Resource<List<ItemLocation>>> getAllItemLocationList(String limit, String offset) {

        return new NetworkBoundResource<List<ItemLocation>, List<ItemLocation>>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull List<ItemLocation> itemTypeList) {
                Utils.psLog("SaveCallResult of getAllCategoriesWithUserId");


                try {
                    db.beginTransaction();

                    itemLocationDao.deleteAllItemLocation();

                    itemLocationDao.insertAll(itemTypeList);

                    db.setTransactionSuccessful();

                } catch (Exception e) {
                    Utils.psErrorLog("Error in doing transaction of getAllCategoriesWithUserId", e);
                } finally {
                    db.endTransaction();
                }
            }


            @Override
            protected boolean shouldFetch(@Nullable List<ItemLocation> data) {

                return connectivity.isConnected();
            }

            @NonNull
            @Override
            protected LiveData<List<ItemLocation>> loadFromDb() {

                Utils.psLog("Load From Db (All Categories)");

                return itemLocationDao.getAllItemLocation();

            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<ItemLocation>>> createCall() {
                Utils.psLog("Call Get All Categories webservice.");

                return psApiService.getItemLocationList(Config.API_KEY,limit, offset);
            }

            @Override
            protected void onFetchFailed(String message) {
                Utils.psLog("Fetch Failed of About Us");
            }

        }.asLiveData();
    }


}