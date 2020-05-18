package vch.test.room.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vch.test.MainActivity;
import vch.test.R;
import vch.test.room.entities.Word;

public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.WordViewHolder> {
    private LayoutInflater inflater;
    private List<Word> wordList;
    private Word currentWord;
    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;
    private Map<Integer, Integer> deleteMap;

    public WordListAdapter (Context context) {
        inflater = LayoutInflater.from(context);
        deleteMap = new HashMap<Integer, Integer>();
    }

    public class WordViewHolder extends RecyclerView.ViewHolder {
        private TextView wordTextView;
        private CheckBox deleteCheckBox;

        public WordViewHolder(@NonNull View itemView) {
            super(itemView);
            wordTextView = itemView.findViewById(R.id.text_view);
            deleteCheckBox = itemView.findViewById(R.id.delete_check_box);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getLayoutPosition();
                    if (null != clickListener && position != RecyclerView.NO_POSITION) {
                        clickListener.onItemClick(wordList.get(position));
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getLayoutPosition();
                    if (null != longClickListener && position != RecyclerView.NO_POSITION) {
                        longClickListener.onItemLongClick(wordList.get(position));
                    }
                    return true;
                }
            });
            deleteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    currentWord = wordList.get(getAdapterPosition());
                    int id = currentWord.getId();
                    if (isChecked) {
                        deleteMap.put(id, MainActivity.NOTE_DELETE_STATE);
                    } else {
                        deleteMap.put(id, MainActivity.NOTE_LIVE_STATE);
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(MainActivity.layoutManagerTemplate, parent, false);

        WordListAdapter.WordViewHolder wordViewHolder =
                new WordListAdapter.WordViewHolder(view);

        return wordViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        if (null != holder) {
            currentWord = wordList.get(position);
            holder.wordTextView.setText(currentWord.getWord());
            if (true == MainActivity.DELETE_ITEM_STATE) {
                holder.deleteCheckBox.setVisibility(View.VISIBLE);
                holder.deleteCheckBox.setChecked(MainActivity.MARK_ALL_NOTES);
            } else {
                holder.deleteCheckBox.setVisibility(View.GONE);
            }
        } else {
            holder.wordTextView.setText("No Text");
        }
    }

    public Word getWordAt(@NonNull int position) {
        return wordList.get(position);
    }

    public void notifyRecycleView() {
        notifyDataSetChanged();
    }

    public void setWords(List<Word> wordList) {
        this.wordList = wordList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (null != wordList) {
            return wordList.size();
        } else {
            return 0;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Word word);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(Word word);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    /**
     * Get Delete Map - return states of every CheckBox(es) (R.id.delete_check_box) and clear deleteMap (buffer for checkboxes state)
      * @return HashMap<Integer, Integer>()
     */
    public Map<Integer, Integer> getDeleteMap() {
        Map<Integer, Integer> buffer = new HashMap<Integer, Integer>();

        buffer = deleteMap;
        deleteMap = new HashMap<Integer, Integer>();
        return buffer;
    }
}
