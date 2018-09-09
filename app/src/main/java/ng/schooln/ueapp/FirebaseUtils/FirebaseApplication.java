package ng.schooln.ueapp.FirebaseUtils;

import android.app.Application;
import android.content.Context;
import com.crashlytics.android.Crashlytics;
import com.firebase.client.Firebase;
import com.google.firebase.database.FirebaseDatabase;
import io.fabric.sdk.android.Fabric;

/**
 * Created by xyjoe on 9/8/18.
 */

public class FirebaseApplication extends Application {
    private static FirebaseApplication mInstance;
    private static Context context;


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        Firebase.setAndroidContext(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        context = getApplicationContext();
        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)           // Enables Crashlytics debugger
                .build();
        Fabric.with(fabric);
        // Installing the Emoji Manager Class for the entire application
        //  EmojiManager.install(new GoogleEmojiProvider());




    }

    public static synchronized FirebaseApplication getInstance() {
        return mInstance;
    }

    public static Context getContext() {
        return context;
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

    }



}
