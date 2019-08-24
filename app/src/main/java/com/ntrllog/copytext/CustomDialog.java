package com.ntrllog.copytext;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class CustomDialog extends Dialog implements android.view.View.OnClickListener {

    private OnMyDialogResult mDialogResult;

    public CustomDialog(Activity a) {
        super(a);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);

        Button enter = findViewById(R.id.dialog_enter);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText type = findViewById(R.id.type_dialog);
                EditText content = findViewById(R.id.content_dialog);
                String t = type.getText().toString();
                String c = content.getText().toString();
                if(mDialogResult != null){
                    mDialogResult.finish(t, c);
                }
                dismiss();
            }
        });

    }

    @Override
    public void onClick(View v) {
    }

    public void setDialogResult(OnMyDialogResult dialogResult) {
        mDialogResult = dialogResult;
    }

    public interface OnMyDialogResult {
        void finish(String type, String content);
    }
}
