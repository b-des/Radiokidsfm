package fm.radiokids.interfaces;

import org.json.JSONObject;

import java.util.List;

import fm.radiokids.models.ChatModel;
import fm.radiokids.models.Post;
import fm.radiokids.models.Message;
import fm.radiokids.models.User;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RadioKidsApi {

    @GET("/wp-json/wp/v2/news?orderby=date&fields=id,title,content,excerpt,date,thumbnail")
    Call<List<Post>> getPosts(@Query("offset") int from, @Query("per_page") int count);

    @GET("/wp-json/wp/v2/news/{id}/")
    Call<Post> getPost(@Path("id") int id);

    @GET("/wp-json/wise-api/v2/messages/0")
    Call<List<ChatModel>> getMessages(@Query("timestamp") long timestamp);

    @POST("/wp-json/wise-api/v2/messages/send")
    Call<ChatModel> sendMessage(@Query("user") int user, @Query("message") String message, @Query("photo") String photo);

    @POST("/wp-json/wise-api/v2/user/get")
    Call<User> login(@Query("login") String login, @Query("password") String password);

    @POST("/wp-json/wise-api/v2/user/new")
    Call<User> sign(@Query("login") String login,
                      @Query("name") String name,
                      @Query("email") String email,
                      @Query("password") String password,
                      @Query("photo") String photo);

    @POST("/wp-json/wise-api/v2/user/social")
    Call<User> social(@Query("login") String login,
                      @Query("name") String name,
                      @Query("email") String email,
                      @Query("photo") String photo);

    @POST("/wp-json/wise-api/v2/user/changePassword")
    Call<String> changePassword(@Query("id") int id,
                                @Query("old") String oldPass,
                        @Query("new") String newPass);

}
