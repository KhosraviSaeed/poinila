package data;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.util.ConnectionUitls;
import com.shaya.poinila.android.util.ConstantsUtils;
import com.shaya.poinila.android.util.ContextHolder;
import com.shaya.poinila.android.util.Logger;
import com.shaya.poinila.android.utils.PonilaJsonParser;

import java.io.StringReader;
import java.lang.reflect.Type;

import data.model.PoinilaResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by iran on 2015-09-01.
 */
public abstract class PoinilaCallback<T extends PoinilaResponse> implements Callback<Response> {

    public static String getStringFromResponse(Response response){
        return new String(((TypedByteArray) response.getBody()).getBytes());
    }

    public boolean handleError(){
        return false;
    }
   /* public boolean validate(T poinilaResponse, Response response){

    }*/

    @Override
    public final void success(Response response, Response response2){
//        JsonElement jsonElement = new JsonParser().parse(getStringFromResponse(response));
        // PonilaJsonParser set Lenient to True for malformed json
        JsonElement jsonElement = new PonilaJsonParser().parse(getStringFromResponse(response));
        int code = jsonElement.getAsJsonObject().get("code").getAsInt();
        //if (validate(poinilaResponse, response))
        switch (code){
            case 401:

                LocalBroadcastManager.getInstance(ContextHolder.getContext()).sendBroadcast(new Intent(ConstantsUtils.INTENT_FILTER_JWT));
                break;

            case 200:

                JsonElement dataElement = jsonElement.getAsJsonObject().get("data");
                if (dataElement.isJsonArray())
                    dataList = dataElement.getAsJsonArray();
                else {
                    dataList = new JsonArray();
                    dataList.add(dataElement);
                }
                JsonReader reader = new JsonReader(new StringReader(jsonElement.toString()));
                reader.setLenient(true);
                T t = PoinilaNetService.getGson().fromJson(reader, getType());
//                T t = PoinilaNetService.getGson().fromJson(jsonElement, getType());
                poinilaSuccess(t, response);

                break;

            default:
                PoinilaResponse poinilaResponse = PoinilaNetService.getGson().fromJson(jsonElement, getPoinilaResponseType());
                if (!poinilaError(poinilaResponse)){
                    Logger.toast(R.string.error_sth_bad_happened);
                }
        }

    }

    //public abstract Type getType();

    public Type getType(){
        return getPoinilaResponseType();
    }

    private Type getPoinilaResponseType() {
        return new TypeToken<PoinilaResponse>(){}.getType();
    }


    public JsonArray getDataList() {
        return dataList;
    }

    JsonArray dataList;


    @Override
    public final void failure(RetrofitError error) {
        switch (error.getKind()){
            case NETWORK:
                if (ConnectionUitls.isNetworkOnline()) {
                    Logger.toast(R.string.error_unable_to_connect);
                }
                break;
            case CONVERSION: // error on converting response
            case UNEXPECTED:
                throw error;
            case HTTP: // 503, 502, etc
                // do nothing for now
        }
    }

    public abstract void poinilaSuccess(T ponilaResponse, Response response);

    /**
     * hook method for handling poinila responses. (Http status is 200)
     * <br/> Can consume the call so default error handling doesn't occur.
     * @param poinilaResponse standard Poinila response packet. {@link PoinilaResponse#data} type is not known.
     * @return true if you want to disable parent default handling, false otherwise.
     */
    public boolean poinilaError(PoinilaResponse poinilaResponse){
        return false;
    }
}
