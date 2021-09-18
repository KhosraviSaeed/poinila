package data.model;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Set;

/**
 * Created by iran on 1/20/2016.
 */
public class SystemPreferences {

    @SerializedName("rating_market")
    public MarketPackages rateDestinationMarket;

    @SerializedName("sms_provider_number")
    public List<String> smsProviderNumbers;


    public enum MarketPackages {
        @SerializedName("bazaar")
        Bazaar("com.farsitel.bazaar", "bazaar://details?id="),
        @SerializedName("myket")
        Myket("ir.mservices.market", "myket://comment/#Intent;scheme=comment;package="),
        @SerializedName("google_play")
        GooglePlay("com.android.vending", "https://play.google.com/store/apps/details?id=");

        public static final String MARKET_ADDRESS_PREFIX = "market://details?id=";
        public String packageName;
        public String pageAddressPrefix;

        MarketPackages(String packageName, String pageAddressPrefix) {
            this.packageName = packageName;
            this.pageAddressPrefix = pageAddressPrefix;
        }

        public Uri getUri() {
            return Uri.parse(packageName);
        }
    }



}
