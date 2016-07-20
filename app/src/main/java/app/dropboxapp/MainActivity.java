package app.dropboxapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Replace APP_KEY from your APP_KEY
    final static private String APP_KEY = "9e60fjdg1wtuypn";
    // Relace APP_SECRET from your APP_SECRET
    final static private String APP_SECRET = "g0fzatsdarrq5w5";

    private static final int FOLDER_SELECT_CODE = 0;
    //
    private DropboxAPI<AndroidAuthSession> mDBApi;


    File[] listFile;
    ArrayList<String> f = new ArrayList<String>();// list of file paths
    String filePath = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button folderChooserBtn = (Button)findViewById(R.id.folderchooser);
        folderChooserBtn.setOnClickListener(btnListener1);
        Button uploadFileBtn = (Button)findViewById(R.id.uploadfile);
        uploadFileBtn.setOnClickListener(btnListener2);

        // callback method
        initialize_session();
//        getFromSdcard();
    }

    private View.OnClickListener btnListener1 = new View.OnClickListener()
    {
        String m_chosen;

        public void onClick(View v)
        {
            SimpleFileDialog FolderChooseDialog =  new SimpleFileDialog(MainActivity.this, "FolderChoose",
                    new SimpleFileDialog.SimpleFileDialogListener()
                    {
                        @Override
                        public void onChosenDir(String chosenDir)
                        {
                            m_chosen = chosenDir;
                            filePath = chosenDir;
                            Toast.makeText(MainActivity.this, "Chosen FileOpenDialog File: " +
                                    m_chosen, Toast.LENGTH_LONG).show();
                        }
                    });

            FolderChooseDialog.chooseFile_or_Dir();
        }

    };

    private View.OnClickListener btnListener2 = new View.OnClickListener()
    {

        public void onClick(View v)
        {
            uploadFiles();
        }

    };
    /**
     *  Initialize the Session of the Key pair to authenticate with dropbox
     *
     */
    protected void initialize_session(){

        // store app key and secret key
        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        //Pass app key pair to the new DropboxAPI object.
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);
        // MyActivity below should be your activity class name
        //start session
        mDBApi.getSession().startOAuth2Authentication(MainActivity.this);
    }

    public void uploadFiles(){
//        File file = new File("/storage/sdcard0/Music/hl.mp3");
        getFromSdcard();
        if (listFile!=null&&listFile.length>0)
        {
            UploadFile upload = new UploadFile(3000,this, mDBApi, "/",listFile);
            upload.execute();
        }

    }




    protected void onResume() {
        super.onResume();

        if (mDBApi.getSession().authenticationSuccessful()) {
            try {
                // Required to complete auth, sets the access token on the session
                mDBApi.getSession().finishAuthentication();

                String accessToken = mDBApi.getSession().getOAuth2AccessToken();
            } catch (IllegalStateException e) {
                Log.i("DbAuthLog", "Error authenticating", e);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FOLDER_SELECT_CODE:
                if(resultCode == RESULT_OK) {
//                    filePath = data.getDataString();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void getFromSdcard()
    {
        File file = new File(filePath);
        if (file.isDirectory())
        {
            listFile = file.listFiles();

            for (int i = 0; i < listFile.length; i++)
            {
                f.add(listFile[i].getAbsolutePath());

            }
        }
    }

    public void showFolderChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.parse("/storage/sdcard0/Music");
        intent.setDataAndType(uri, "resource/folder");
        startActivityForResult(Intent.createChooser(intent, "Open folder"), FOLDER_SELECT_CODE);
    }
}
