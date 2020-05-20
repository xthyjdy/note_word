package vch.test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SaveWordActivity extends AppCompatActivity {
    private EditText editWordView;
    private ImageView ivBackToHome,
            ivSent,
            ivChangeAppearance,
            ivMenu,
            ivAbortEdit,
            ivStepBack,
            ivStepForward,
            ivSave;
    private LinearLayout linLSavePageMainTopBar, linLSavePageEditTopBar;
    private String buffer;
    private boolean editAction;
    private boolean editMode = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_save_page);

        this.init();
    }

    private void init() {
        linLSavePageMainTopBar = (LinearLayout) findViewById(R.id.lin_l_save_page_main_top_bar);
        linLSavePageEditTopBar = (LinearLayout) findViewById(R.id.lin_l_save_page_edit_top_bar);
        editAction = (boolean) getIntent().hasExtra(MainActivity.EXTRA_ID);
        editWordView = findViewById(R.id.et_save_page_edit);
        editWordView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editMode) {
                    editMode = true;
                    changeMode();
                }
            }
        });
        ivBackToHome = (ImageView) findViewById(R.id.iv_back_to_home);
        ivBackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ivSent = (ImageView) findViewById(R.id.iv_sent);
        ivSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sent();
            }
        });
        ivChangeAppearance = (ImageView) findViewById(R.id.iv_change_appearance);
        ivChangeAppearance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeAppearance();
            }
        });
        ivMenu = (ImageView) findViewById(R.id.iv_save_page_menu);
        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopapMenu(v);
            }
        });
        ivAbortEdit = (ImageView) findViewById(R.id.iv_abort_edit);
        ivAbortEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editMode) {
                    editMode = false;
                    changeMode();
                    editWordView.setText(buffer);
                    editWordView.clearFocus();
                    editWordView.setFocusableInTouchMode(false);
                    editWordView.setFocusable(false);
                }
            }
        });
        ivStepBack = (ImageView) findViewById(R.id.iv_step_back);
        ivStepBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUnderConstructionMessage();
            }
        });
        ivStepForward = (ImageView) findViewById(R.id.iv_step_forward);
        ivStepForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUnderConstructionMessage();
            }
        });
        ivSave = (ImageView) findViewById(R.id.iv_save);
        ivSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        if (editAction) {
            setTitle("Edit Word");
            buffer = getIntent().getStringExtra(MainActivity.EXTRA_WORD);
            editWordView.setText(getIntent().getStringExtra(MainActivity.EXTRA_WORD));
        } else {
            setTitle("Create New Word");
            buffer = "";
        }
    }

    private void save() {
        Intent data = new Intent();
        String content = editWordView.getText().toString();
        if (content.isEmpty()) {
            Toast.makeText(getApplicationContext(), "fill the word", Toast.LENGTH_LONG).show();
        } else {
            if (editAction) {
                int id = getIntent().getIntExtra(MainActivity.EXTRA_ID, MainActivity.EMPTY_ID);
                if (MainActivity.EMPTY_ID != id) {
                    data.putExtra(MainActivity.EXTRA_ID, id);
                    data.putExtra(MainActivity.EXTRA_WORD, content);
                    setResult(RESULT_OK, data);
                } else {
                    setResult(RESULT_CANCELED, data);
                }
            } else {
                if (TextUtils.isEmpty(content)) {
                    setResult(RESULT_CANCELED, data);
                } else {
                    data.putExtra(MainActivity.EXTRA_WORD, content);
                    Log.e(MainActivity.LOG_TAG, "_created_");
                    setResult(RESULT_OK, data);
                }
            }
            finish();
        }
    }

    private void sent() {
        showUnderConstructionMessage();
        Log.e(MainActivity.LOG_TAG, "_sent_");
    }

    private void changeAppearance() {
        showUnderConstructionMessage();
        Log.e(MainActivity.LOG_TAG, "_changeAppearance_");
    }

    private void showPopapMenu(View view) {
        MenuBuilder menuBuilder = new MenuBuilder(this);
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.save_page_popup_top_bar_menu, menuBuilder);
        MenuPopupHelper optionsMenu = new MenuPopupHelper(this, menuBuilder, view);
        optionsMenu.setForceShowIcon(true);
        // Set Item Click Listener
        menuBuilder.setCallback(new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.top_bar_popap_reminder:
                        showUnderConstructionMessage();
                        Log.e(MainActivity.LOG_TAG, "_top_bar_popap_reminder_");
                        return true;
                    case R.id.top_bar_popap_hide:
                        showUnderConstructionMessage();
                        Log.e(MainActivity.LOG_TAG, "_top_bar_popap_hide_");
                        return true;
                    case R.id.top_bar_popap_fix_on_desktop:
                        showUnderConstructionMessage();
                        Log.e(MainActivity.LOG_TAG, "_top_bar_popap_fix_on_desktop_");
                        return true;
                    case R.id.top_bar_popap_move:
                        showUnderConstructionMessage();
                        Log.e(MainActivity.LOG_TAG, "_top_bar_popap_move_");
                        return true;
                    case R.id.top_bar_popap_remove:
                        showUnderConstructionMessage();
                        Log.e(MainActivity.LOG_TAG, "_top_bar_popap_remove_");
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onMenuModeChange(MenuBuilder menu) {}
        });

        // Display the menu
        optionsMenu.show();
    }

    private void changeMode() {
        if (editMode) {
            linLSavePageMainTopBar.setVisibility(View.GONE);
            linLSavePageEditTopBar.setVisibility(View.VISIBLE);
        } else {
            linLSavePageMainTopBar.setVisibility(View.VISIBLE);
            linLSavePageEditTopBar.setVisibility(View.GONE);
        }
    }

    private void showUnderConstructionMessage() {
        Toast.makeText(this, MainActivity.UNDER_CONSTRUCTION_MESSAGE, Toast.LENGTH_SHORT).show();
    }
}
