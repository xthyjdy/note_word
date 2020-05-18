package vch.test.room.repositories;

import android.app.Application;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;
import java.util.ArrayList;
import java.util.List;
import vch.test.room.api.Api;
import vch.test.room.dao.WordDao;
import vch.test.room.db.WordRoomDatabase;
import vch.test.room.entities.Word;

public class WordRepository {

    private WordDao wordDao;
    private LiveData<List<Word>> allWords;

    public WordRepository(Application application) {
        WordRoomDatabase db = WordRoomDatabase.getDatabase(application);
        wordDao = db.wordDao();
        allWords = wordDao.getAlphabetizedWords();
    }

    public LiveData<List<Word>> getAllWords() {
        return allWords;
    }

    public void insert(Word word) {
        new InsertNoteAsyncTask(wordDao).execute(word);
    }

    public void update(Word word) {
        new UpdateNoteAsyncTask(wordDao).execute(word);
    }

    public void delete(Word word) {
        new DeleteNoteAsyncTask(wordDao).execute(word);
    }

    public void deleteAll() {
        new DeleteAllNoteAsyncTask(wordDao).execute();
    }

    /**
     * Get Remote Words - method which get Words from remote repository and insert it to database
     */
    public void getRemoteWords() {
        Api api = new Api(wordDao);
        api.getWords();
    }

    /**
     * Save Words To Remote Repository - save all unique local words to remote repository
     */
    public void saveWordsToRemoteRepository() {
        Api api = new Api();
        List words = this.dataToList(this.getAllWords());
//        api.saveWords(words);
        api.saveWords(words);
    }

    private static class DeleteAllNoteAsyncTask extends AsyncTask<Void, Void, Void> {
        private WordDao wordDao;

        private DeleteAllNoteAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            wordDao.deleteAll();
            return null;
        }
    }

    private static class InsertNoteAsyncTask extends AsyncTask<Word, Void, Void> {
        private WordDao wordDao;

        private InsertNoteAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            wordDao.insert(words[0]);
            return null;
        }
    }

    private static class UpdateNoteAsyncTask extends AsyncTask<Word, Void, Void> {
        private WordDao wordDao;

        private UpdateNoteAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            wordDao.update(words[0]);
            return null;
        }
    }

    private static class DeleteNoteAsyncTask extends AsyncTask<Word, Void, Void> {
        private WordDao wordDao;

        private DeleteNoteAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            wordDao.delete(words[0]);
            return null;
        }
    }

    /**
     * Data To List - get LiveData LiveData<List<Word>> and convert it to array
     * @param liveData - instance of LiveData<List<Word>>
     * @return array
     */
    private List dataToList(LiveData<List<Word>> liveData) {
        return (ArrayList)liveData.getValue();
    }
}
