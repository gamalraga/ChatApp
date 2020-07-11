package gamal.myappnew.chatapp.Notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAH5dGhTI:APA91bHeP4mfbZcRarqaGdVLWAsSc5-3zo7gRFJ8PcPPDt-SGOP8dJokeDMUsRErQnrxiSEgaymbQVTET1jIWV_tlyPcdOLdatwboJSNzTx7fPCSX-oQcx5HgrA52m3JfFHUEMzkB2g4"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);


}
