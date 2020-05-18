package vch.test.room.viewModels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

import vch.test.MainActivity;
import vch.test.room.entities.Word;
import vch.test.room.repositories.WordRepository;

public class WordViewModel extends AndroidViewModel {

    private WordRepository repository;
    private LiveData<List<Word>> allWords;

    public WordViewModel(@NonNull Application application) {
        super(application);
        repository = new WordRepository(application);
        allWords = repository.getAllWords();
    }

    /**
     * Get Remote Words - method which get Words from remote repository and insert it to database
     */
    public void getRemoteWords() {
        repository.getRemoteWords();
    }

    /**
     * Save Words To Remote Repository - transfer all words objects to repository
     */
    public void saveWordsToRemoteRepository() {
        repository.saveWordsToRemoteRepository();
    }

    public LiveData<List<Word>> getAllWords() {
        return allWords;
    }

    public void insert(Word word) {
        repository.insert(word);
    }

    public void update(Word word) {
        repository.update(word);
    }
    public void delete(Word word) {
        repository.delete(word);
    }
    public void deleteAll() {
        repository.deleteAll();
    }
}
