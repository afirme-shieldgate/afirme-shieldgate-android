package com.afirme.payment.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Size;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents a JSON model used in the afirme Api.
 */
public abstract class PaymentJsonModel {

    @NonNull
    public abstract Map<String, Object> toMap();

    @NonNull
    public abstract JSONObject toJson();

    @Override
    public String toString() {
        return this.toJson().toString();
    }

    static void putPaymentJsonModelMapIfNotNull(
            @NonNull Map<String, Object> upperLevelMap,
            @NonNull @Size(min = 1) String key,
            @Nullable PaymentJsonModel jsonModel) {
        if (jsonModel == null) {
            return;
        }
        upperLevelMap.put(key, jsonModel.toMap());
    }


    static void putPaymentJsonModelIfNotNull(
            @NonNull JSONObject jsonObject,
            @NonNull @Size(min = 1) String key,
            @Nullable PaymentJsonModel jsonModel) {
        if (jsonModel == null) {
            return;
        }

        try {
            jsonObject.put(key, jsonModel.toJson());
        } catch (JSONException ignored) {
        }
    }

    static void putPaymentJsonModelListIfNotNull(
            @NonNull Map<String, Object> upperLevelMap,
            @NonNull @Size(min = 1) String key,
            @Nullable List<? extends PaymentJsonModel> jsonModelList) {
        if (jsonModelList == null) {
            return;
        }

        List<Map<String, Object>> mapList = new ArrayList<>();
        for (int i = 0; i < jsonModelList.size(); i++) {
            mapList.add(jsonModelList.get(i).toMap());
        }
        upperLevelMap.put(key, mapList);
    }


    static void putPaymentJsonModelListIfNotNull(
            @NonNull JSONObject jsonObject,
            @NonNull @Size(min = 1) String key,
            @Nullable List<? extends PaymentJsonModel> jsonModelList) {
        if (jsonModelList == null) {
            return;
        }

        try {
            JSONArray array = new JSONArray();
            for (PaymentJsonModel model : jsonModelList) {
                array.put(model.toJson());
            }
            jsonObject.put(key, array);
        } catch (JSONException ignored) {
        }
    }
}

