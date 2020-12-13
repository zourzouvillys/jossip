package io.rtcore.sip.proxy.plugins.okhttp;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public class OkHttpPlugin {

  public interface GitHubService {
    @Headers({
      "Accept: application/json, application/problem+json",
      "User-Agent: jossip"
    })
    @GET("users/{user}/repos")
    Call<JsonNode> listRepos(@Path("user") String user);
  }

  public static void main(String[] args) throws IOException {
    Retrofit retrofit =
      new Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .addConverterFactory(JacksonConverterFactory.create(new JsonMapper()))
        .validateEagerly(true)
        .build();

    GitHubService service = retrofit.create(GitHubService.class);

    Response<JsonNode> res = service.listRepos("zourzouvillys").execute();

    System.err.println(res);
    System.err.println(res.body());

  }

}
