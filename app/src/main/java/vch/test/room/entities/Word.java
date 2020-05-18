package vch.test.room.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "word_table")
public class Word {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "word")
    private String word;



    public void setId(@NonNull int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setWord(@NonNull String word) {
        this.word = word;
    }

    public String getWord() {
        return this.word;
    }
}
