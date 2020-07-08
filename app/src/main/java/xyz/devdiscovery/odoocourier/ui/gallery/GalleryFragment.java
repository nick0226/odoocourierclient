package xyz.devdiscovery.odoocourier.ui.gallery;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;

import xyz.devdiscovery.odoocourier.R;

import static android.app.Activity.RESULT_OK;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;



    private final int RQS_LOAD_IMAGE = 0;
    private final int RQS_SEND_EMAIL = 1;
    //private EditText mEmailAddressEditText;
    //private EditText mEmailSubjectEditText;
    private EditText mEmailTextEditText;
    private ArrayList<Uri> mUriList = new ArrayList<>();
    private ArrayAdapter<Uri> mFileListAdapter;



    View.OnClickListener addFileOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, RQS_LOAD_IMAGE);
        }
    };

    View.OnClickListener sendOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
         //   String emailAddress = mEmailAddressEditText.getText().toString();
         //   String emailSubject = mEmailSubjectEditText.getText().toString();
            String emailText = mEmailTextEditText.getText().toString();
            if (mEmailTextEditText.getText().toString().isEmpty())
            {
                emptyField();
                return;
            }
         //   String[] emailAddressList = {emailAddress};

            Intent intent = new Intent();
         //   intent.putExtra(Intent.EXTRA_EMAIL, emailAddressList);
         //   intent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"general@pro100systems.com.ua"});
            intent.putExtra(Intent.EXTRA_TEXT, emailText);

            if (mUriList.isEmpty()) {
                // посылаем письмо без прикрепленной картинки
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("plain/text");
            } else if (mUriList.size() == 1) {
                // посылаем письмо с одной картинкой
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, mUriList.get(0));
                intent.setType("image/*");
            } else {
                // посылаем письмо с несколькими картинками
                intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, mUriList);
                intent.setType("image/*");
            }
           // startActivity(Intent.createChooser(intent, "Выберите программу:"));

            intent.setType("message/rfc822");

            intent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmailExternal");
            Toast toast = Toast.makeText(getActivity(), "Запускаю клиент Gmail", Toast.LENGTH_LONG);
            toast.show();
            try {
                startActivity(intent);

            } catch (ActivityNotFoundException ex) {
                // GMail not found
            }

            mEmailTextEditText.setText("");

        }
    };


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);



     //   final TextView textView = root.findViewById(R.id.text_gallery);
     //   galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
     //       @Override
     //       public void onChanged(@Nullable String s) {
     //           textView.setText(s);
     //       }
     //   });



     //   mEmailAddressEditText = (EditText) root.findViewById(R.id.email_address);
     //   mEmailSubjectEditText = (EditText) root.findViewById(R.id.email_subject);
        mEmailTextEditText = (EditText) root.findViewById(R.id.editNote);
        Button addFileButton = (Button) root.findViewById(R.id.openGallary);
        Button sendButton = (Button) root.findViewById(R.id.sendMassanges);
        addFileButton.setOnClickListener(addFileOnClickListener);
        sendButton.setOnClickListener(sendOnClickListener);

        mFileListAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                mUriList);
     //   ListView filesListView = (ListView) root.findViewById(R.id.filelist);
     //   filesListView.setAdapter(mFileListAdapter);










        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RQS_LOAD_IMAGE:
                    Uri imageUri = data.getData();
                    mUriList.add(imageUri);
                    mFileListAdapter.notifyDataSetChanged();
                    break;
                case RQS_SEND_EMAIL:
                    break;
            }
        }
    }

    public void emptyField () {

        Toast.makeText(getActivity(), "Поле \"Примечание\" не может быть пустым, заполните его.", Toast.LENGTH_LONG).show();

    }


}
