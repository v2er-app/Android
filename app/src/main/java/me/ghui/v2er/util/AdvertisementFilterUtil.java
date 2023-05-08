package me.ghui.v2er.util;

import android.content.Context;
import android.content.res.Resources;

import me.ghui.v2er.R;

public class AdvertisementFilterUtil {

    public static String clearAdvertisementDivJs(Context context){
        StringBuilder js = new StringBuilder("javascript:");
        Resources res = context.getResources();
        String[] adDivs = res.getStringArray(R.array.advertisementBlockDiv);
        for(int i=0;i<adDivs.length;i++) {
            js.append("var adDiv")
                    .append(i)
                    .append("= document.getElementsByClassName('")
                    .append(adDivs[i]).append("');if(adDiv")
                    .append(i).append("[0] != null)var adDiv")
                    .append(i)
                    .append("p = adDiv")
                    .append(i)
                    .append("[0].parentNode;if (adDiv")
                    .append(i)
                    .append("p != null)adDiv")
                    .append(i)
                    .append("p.parentNode.removeChild(adDiv")
                    .append(i)
                    .append("p);");
        }
        for(int i=0;i<adDivs.length;i++){
            js.append("var adDiv")
                    .append(i)
                    .append("= document.getElementById('")
                    .append(adDivs[i]).append("');if(adDiv")
                    .append(i).append(" != null)var adDiv")
                    .append(i)
                    .append("p = adDiv")
                    .append(i)
                    .append(".parentNode;if (adDiv")
                    .append(i)
                    .append("p != null)adDiv")
                    .append(i)
                    .append("p.parentNode.removeChild(adDiv")
                    .append(i)
                    .append("p);");
        }
        return js.toString();
    }
}
