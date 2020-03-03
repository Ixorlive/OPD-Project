package com.example.careerguidancetest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DatabaseErrorHandler;
import android.os.Bundle;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;
import com.rengwuxian.materialedittext.MaterialEditText;

public class MainActivity extends AppCompatActivity {


    ConstraintLayout root;
    DBHelper dbHelper;
    public Button mRegistrationBtn;
    public Button mSignUpBtn;
    public static final String EXTRA_MESSAGE = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        root = findViewById(R.id.registrationActivity);
        mRegistrationBtn = findViewById(R.id.btnregistration);
        mSignUpBtn = findViewById(R.id.btnsignup);

        dbHelper = new DBHelper(getApplicationContext());
    }

    public void signUp(View view){
        final AlertDialog.Builder signDialog = new AlertDialog.Builder(this);
        signDialog.setTitle("Вход");
        signDialog.setMessage("Введите свои данные для авторизации");
        //аналогично Registration
        LayoutInflater inflater = LayoutInflater.from(this);
        View signUpView = inflater.inflate(R.layout.signup, null);
        signDialog.setView(signUpView);

        final MaterialEditText email = signUpView.findViewById(R.id.emailField);
        final MaterialEditText password = signUpView.findViewById(R.id.passwordField);

        signDialog.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        signDialog.setPositiveButton("Войти", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                Cursor c = db.query("mytable", null, null,
                        null, null, null, null);

                String emailStr = "", passwordStr = "";
                if (email.getText() != null ) {emailStr = email.getText().toString(); };
                if (password.getText() != null ) {passwordStr = password.getText().toString(); };

                if(c.moveToFirst()){
                    int emailIndex = c.getColumnIndex("email");
                    int passwordIndex = c.getColumnIndex("password");
                    int nameIndex = c.getColumnIndex("name");
                    boolean check = false;
                    do {
                        if (c.getString(emailIndex).equals(emailStr) &&
                                c.getString(passwordIndex).equals(passwordStr)){
                            Intent intent = new Intent(root.getContext(), MenuActivity.class);
                            intent.putExtra(EXTRA_MESSAGE, c.getString(nameIndex));
                            root.getContext().startActivity(intent);
                            finish();
                            check = true;
                            break;
                        }
                    }while (c.moveToNext());
                    if (check) Snackbar.make(root, "Неверные данные", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        signDialog.show();
    }

    public void registration(View view){
        //Диалоговое всплывающее окно
        final AlertDialog.Builder regDialog = new AlertDialog.Builder(this);
        regDialog.setTitle(getString(R.string.Registration));
        regDialog.setMessage("Введите данные для регистрации");
        //Создаёт из layout View элемент
        LayoutInflater inflater = LayoutInflater.from(this);
        View registrationView = inflater.inflate(R.layout.registration, null);
        regDialog.setView(registrationView);

        final MaterialEditText email = registrationView.findViewById(R.id.emailField);
        final MaterialEditText password = registrationView.findViewById(R.id.passwordField);
        final MaterialEditText name = registrationView.findViewById(R.id.nameField);

        regDialog.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        regDialog.setPositiveButton("Зарегестрироваться", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nameStr = "", emailStr ="", passwordStr = "";
                if (name.getText() != null ) { nameStr = name.getText().toString();};
                if (email.getText() != null ) {emailStr = email.getText().toString(); };
                if (password.getText() != null ) {passwordStr = password.getText().toString(); };

                if (TextUtils.isEmpty(emailStr)){
                    Snackbar.make(root, "Введите вашу почту",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(nameStr)){
                    Snackbar.make(root, "Введите ваше имя",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (passwordStr.length() < 5){
                    Snackbar.make(root, "Введите пароль не менее 5 символов",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }
                //Если пользователь заполнил все поля
                //Регистрация
                ContentValues cv = new ContentValues();
                cv.put("email", emailStr);
                cv.put("password", passwordStr);
                cv.put("name", nameStr);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                //Вставляем в базу данных данные
                db.insert("mytable", null, cv);
                Snackbar.make(root, "Успешно, можете авторизоваться", Snackbar.LENGTH_SHORT).show();
            }
        });
        regDialog.show();
    }

    public void LoginAnonymously(View view){
        Intent intent = new Intent(root.getContext(), MenuActivity.class);
        intent.putExtra(EXTRA_MESSAGE, "Аноним");
        root.getContext().startActivity(intent);
        finish();
    }

    class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context) {
            // конструктор суперкласса
            super(context, "myDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table mytable (" +
                    "email text primary key, "+
                    "password text," +
                    "name text" + ")");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

    }
}
