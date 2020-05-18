package vch.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import vch.test.room.adapters.WordListAdapter;
import vch.test.room.entities.Word;
import vch.test.room.viewModels.WordViewModel;

public class MainActivity extends AppCompatActivity {
    public static final int NEW_WORD_ACTIVITY_REQUEST_CODE = 1;
    public static final int EDIT_WORD_ACTIVITY_REQUEST_CODE = 2;
    public static final int NOTE_DELETE_STATE = 1;
    public static final int NOTE_LIVE_STATE = 0;
    public static final int EMPTY_ID = -1;
    public static final String EXTRA_ID = "vch.test.EXTRA_ID";
    public static final String EXTRA_WORD = "vch.test.EXTRA_WORD";
    public static final String LOG_TAG = "mylog";
    public static boolean DELETE_ITEM_STATE = false;
    public static boolean MARK_ALL_NOTES = false;
    public static boolean DELETE_CHECKED_NOTES = false;
    //default template for recycleview item
    public static int layoutManagerTemplate = R.layout.recyclerview_item;
    public LayoutInflater inflater;
    public LinearLayout mainLayout;
    public LinearLayout innerLayout;
    public TextView innerTextView;
    private ImageView ivRejectChosen;
    private ImageView ivMainTopMenu;
    private ImageView ivChoseAllList;
    private TextView tvCountOfChosenItems;
    private WordViewModel wordViewModel;
    private WordListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private RelativeLayout topBarRelativeLayout;
    private View child;
    private final int CARD_INTERFACE = 0;
    private final int LIST_INTERFACE = 1;
    private LinearLayout linLHomePageMainTopBar, linLHomePageDeleteTopBar;
    private FloatingActionButton addWordButton;
    private Button deleteButton;
    private View wordHomePageBottomBarContainer;
    private Map<Integer, Integer> deleteMap;
    private final String linearName = "LinearLayoutManager";
    private final String gridName = "GridLayoutManager";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_home_page);

        this.init();
    }

    public void init() {
        recyclerView = findViewById(R.id.recyclerview);
        adapter = new WordListAdapter(this);
        recyclerView.setAdapter(adapter);
        setWordLayoutManager(null);

        wordViewModel = new ViewModelProvider(this).get(WordViewModel.class);
        wordViewModel.getAllWords().observe(this, new Observer<List<Word>>() {
            @Override
            public void onChanged(List<Word> wordList) {
                adapter.setWords(wordList);
            }
        });

        adapter.setOnItemClickListener(new WordListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Word word) {
                Intent intent = new Intent(MainActivity.this, SaveWordActivity.class);
                intent.putExtra(MainActivity.EXTRA_ID, word.getId());
                intent.putExtra(MainActivity.EXTRA_WORD, word.getWord());
                startActivityForResult(intent, EDIT_WORD_ACTIVITY_REQUEST_CODE);
            }
        });

        adapter.setOnItemLongClickListener(new WordListAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(Word word) {
                DELETE_ITEM_STATE = true;
                changeTopBarMode();
            }
        });

        addWordButton = findViewById(R.id.add_button);
        addWordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SaveWordActivity.class);
                startActivityForResult(intent, NEW_WORD_ACTIVITY_REQUEST_CODE);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                wordViewModel.delete(adapter.getWordAt(position));
            }
        }).attachToRecyclerView(recyclerView);

        ivMainTopMenu = (ImageView) findViewById(R.id.iv_main_top_menu);
        ivMainTopMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopapMenu(view);
            }
        });

        ivRejectChosen = (ImageView) findViewById(R.id.iv_reject_chosen);
        ivRejectChosen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DELETE_ITEM_STATE = false;
                changeTopBarMode();
            }
        });

        ivChoseAllList = (ImageView) findViewById(R.id.iv_chose_all_list);
        ivChoseAllList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DELETE_ITEM_STATE = true;
                MARK_ALL_NOTES = true == MARK_ALL_NOTES ? false : true;
                adapter.notifyRecycleView();
            }
        });

        deleteButton = (Button) findViewById(R.id.word_home_page_delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMap = adapter.getDeleteMap();
                Word word;

                if (deleteMap.size() > 0) {
                    for (Map.Entry<Integer, Integer> entry : deleteMap.entrySet()) {
                        if (NOTE_DELETE_STATE == entry.getValue()) {
                            word = new Word();
                            word.setId(entry.getKey());
                            wordViewModel.delete(word);
                        }
                    }
                    //close delete mode (hide delete view)
                    DELETE_ITEM_STATE = false;
                    changeTopBarMode();
                    adapter.notifyRecycleView();
                } else {
                    Toast.makeText(MainActivity.this, "chose Notes", Toast.LENGTH_LONG).show();
                }
            }
        });

        tvCountOfChosenItems = (TextView) findViewById(R.id.tv_count_of_chosen_items);
        tvCountOfChosenItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMap = adapter.getDeleteMap();
            }
        });

        linLHomePageMainTopBar = (LinearLayout) findViewById(R.id.lin_l_home_page_main_top_bar);
        linLHomePageDeleteTopBar = (LinearLayout) findViewById(R.id.lin_l_home_page_delete_top_bar);
        wordHomePageBottomBarContainer = findViewById(R.id.word_home_page_bottom_bar_container);
    }

    private void showPopapMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
        popupMenu.inflate(R.menu.home_page_top_bar_popup_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.top_bar_popap_server_side_get_diff_notes_from_server:
                        wordViewModel.getRemoteWords();
                        return true;
                    case R.id.top_bar_popap_server_side_save_diff_notes_to_server:
                        wordViewModel.saveWordsToRemoteRepository();
                        return true;
                    case R.id.top_bar_popap_card_interface:
                        layoutManager = new GridLayoutManager(MainActivity.this, 2);
                        setWordLayoutManager(layoutManager);
                        return true;
                    case R.id.top_bar_popap_list_interface:
                        layoutManager = new LinearLayoutManager(MainActivity.this);
                        setWordLayoutManager(layoutManager);
                        return true;
                    case R.id.top_bar_popap_delete_all_notes:

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                        alertDialogBuilder
                                .setCancelable(false)
                                .setPositiveButton("Delete?",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                wordViewModel.deleteAll();
                                            }
                                        })
                                .setNegativeButton("Cancel",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {}
        });

        popupMenu.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_WORD_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String wordText = data.getStringExtra(EXTRA_WORD);
                Word word = new Word();

                word.setWord(wordText);
                wordViewModel.insert(word);
                Toast.makeText(
                        getApplicationContext(),
                        "new object was create 11",
                        Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == EDIT_WORD_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                int id = data.getIntExtra(MainActivity.EXTRA_ID, EMPTY_ID);

                if (EMPTY_ID != id) {
                    String wordText = data.getStringExtra(EXTRA_WORD);
                    Word word = new Word();

                    word.setId(id);
                    word.setWord(wordText);
                    wordViewModel.update(word);
                    Toast.makeText(
                            getApplicationContext(),
                            "new object was created 22",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            "id = -1; object not EDITED",
                            Toast.LENGTH_LONG).show();
                }
            }
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    R.string.empty_not_saved,
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Set Word Layout Manager - reset layout manager AND template layout (.xml) for recycleview item (liner or grid)
     * @param layoutManager
     */
    private void setWordLayoutManager(RecyclerView.LayoutManager layoutManager) {
        if (null == layoutManager) {
            layoutManager = new LinearLayoutManager(this);
        }
        String layoutManagerName = layoutManager.getClass().getSimpleName();

        if (layoutManagerName.equals(linearName)) {
            MainActivity.layoutManagerTemplate = R.layout.recyclerview_item;
        } else if (layoutManagerName.equals(gridName)){
            MainActivity.layoutManagerTemplate = R.layout.recyclerview_card_item;
        }

        //reset recycleview adapter for rewrite view with specified case (list or grid view)
        recyclerView.setAdapter(null);
        recyclerView.setLayoutManager(null);
        recyclerView.getRecycledViewPool().clear();
        recyclerView.swapAdapter(adapter, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter.notifyDataSetChanged();
    }

    private void changeTopBarMode() {
        if (DELETE_ITEM_STATE) {
            linLHomePageMainTopBar.setVisibility(View.GONE);
            linLHomePageDeleteTopBar.setVisibility(View.VISIBLE);
            addWordButton.setVisibility(View.GONE);
            wordHomePageBottomBarContainer.setVisibility(View.VISIBLE);
        } else {
            linLHomePageMainTopBar.setVisibility(View.VISIBLE);
            linLHomePageDeleteTopBar.setVisibility(View.GONE);
            addWordButton.setVisibility(View.VISIBLE);
            wordHomePageBottomBarContainer.setVisibility(View.GONE);
        }
        invalidateOptionsMenu();
        adapter.notifyRecycleView();
    }

    /**
     * Delay Run - method which checks "some functional" without using button (without wake phone)
     */
    public void delayRun() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        wordViewModel.saveWordsToRemoteRepository();
//                        Gson gson = new Gson();
//
//                        ArrayList arrayList = (ArrayList) wordViewModel.getAllWords().getValue();
//                        String json = gson.toJson(arrayList);
//
//                        Log.e(LOG_TAG, json);
                    }
                }, 1000);
            }
        }).start();
    }

    public static void log(String string) {
        Log.e(LOG_TAG, String.valueOf(string));
    }
}
