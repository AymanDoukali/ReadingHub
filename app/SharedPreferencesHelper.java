import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {
    private SharedPreferences sp;

    public SharedPreferencesHelper(Context context){
        this.sp = context.getSharedPreferences("userInfo",Context.MODE_PRIVATE);
    }
    public void SaveUserInfoString(String key,String value){
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putString(key, value);
        spEditor.apply();

    }

    public void GetUserInfo(String key){
        String data = sp.getString(key,"");
    }
}
