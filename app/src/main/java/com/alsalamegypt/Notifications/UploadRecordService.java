package com.alsalamegypt.Notifications;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.alsalamegypt.R;
import com.alsalamegypt.RecordHistory;
import com.alsalamegypt.UI.MainActivity;
import com.alsalamegypt.UI.MakeRecordsFragment;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;

public class UploadRecordService extends Service {

    private FirebaseStorage storage;
    private StorageReference storageReference;

    private RecordHistory recordHistory;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        recordHistory = (RecordHistory) (intent.getSerializableExtra("record_history"));


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //startUploadRecordProgNotification()
    }


    private void uploadRecord() {

        //showDefaultProgressDialog();

        NotificationBuilder notificationBuilder = new NotificationBuilder(getApplicationContext())
                .buildProgressNotification(recordHistory.getRecordName());

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        StorageReference ref = storageReference.child("audios/" + recordHistory.getRecordName());
        UploadTask uploadTask = ref.putFile(Uri.fromFile(new File(recordHistory.getRecordFilePath())));

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return ref.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {

                            recordHistory.setRecordName(task.getResult().toString());

                        } else {

                            notificationBuilder.showProgress(-1);
                        }
                    }
                });

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        notificationBuilder.showProgress(-1);
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                .getTotalByteCount());

                        notificationBuilder.showProgress(progress);


                    }
                });
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
