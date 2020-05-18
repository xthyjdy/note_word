package vch.test.room.api;

import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import vch.test.MainActivity;
import vch.test.room.dao.WordDao;
import vch.test.room.entities.Word;

public class Api {
    public static final String BASE_URL = "http://vasiliy.ho.ua/";
    private Retrofit retrofit;
    private RetrofitService service;
    private WordDao wordDao;

    /**
     * Api - constructor for get data from remote repository and save it to local db
     * @param wordDao instance of WordDao class
     */
    public Api(WordDao wordDao){
        this.init();
        this.wordDao = wordDao;
    }

    /**
     * Api - constructor for get data from remote repository and save it to local db
     */
    public Api(){
        this.init();
    }

    private void init() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(RetrofitService.class);
    }

    /**
     * Get Remote Words - method which get Words from remote repository and insert it to database
     */
    public void getWords() {
        Call<ResponseBody> remoteWordsJson = service.getJsonWords();

        remoteWordsJson.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String jsonResponseToString = response.body().string();
                    String wordText;
                    JSONArray jsonArray = new JSONArray(jsonResponseToString);
                    JSONObject jsonObject = null;

                    if (jsonArray.length() > 0) {
                        for (int i = 0, len = jsonArray.length(); i < len; i++) {
                            jsonObject = new JSONObject(jsonArray.get(i).toString());
                            if (!jsonObject.getString("word").isEmpty()) {
                                Word word = new Word();
                                word.setWord(jsonObject.getString("word"));
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        wordDao.insert(word);
                                    }
                                }).start();
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    /**
     * Save Words To Remote Repository - save all unique local words to remote repository
     * @param words - list of words objects
     */
    public void saveWords(List words) {
        String wordsToJson = new Gson().toJson(words);
        Log.e(MainActivity.LOG_TAG, wordsToJson);
        service.saveWords(wordsToJson).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Log.e(MainActivity.LOG_TAG, response.body().string());
                } catch (Exception e) {
                    Log.e(MainActivity.LOG_TAG, "_error_onResponse_");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(MainActivity.LOG_TAG, String.valueOf(t.getMessage()));
                t.printStackTrace();
            }
        });
    }

    interface RetrofitService {
        @GET("api")
        Call<ResponseBody> getJsonWords();

        //@Headers("Content-Type: application/json")
        @FormUrlEncoded
        @POST("api/index.php")
        Call<ResponseBody> saveWords(@Field("data") String wordsToJson);
    }
}
