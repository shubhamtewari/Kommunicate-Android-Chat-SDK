package com.applozic.mobicomkit.uiwidgets.kommunicate;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.text.TextUtils;

import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.attachment.FileClientService;
import com.applozic.mobicomkit.api.conversation.Message;
import com.applozic.mobicomkit.uiwidgets.AlCustomizationSettings;
import com.applozic.mobicomkit.uiwidgets.ApplozicSetting;
import com.applozic.mobicomkit.uiwidgets.R;
import com.applozic.mobicomkit.uiwidgets.async.FileTaskAsync;
import com.applozic.mobicomkit.uiwidgets.kommunicate.callbacks.PrePostUIMethods;
import com.applozic.mobicommons.commons.core.utils.Utils;
import com.applozic.mobicommons.file.FileUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * contains all methods that can be used to send, write and manage attachment [messages]
 *
 * @author shubham
 * 20th September, 2019
 */
public class KmAttachmentsController {
    Context context;
    public static final String TAG = "KmAttController";

    public KmAttachmentsController(Context context) {
        this.context = context;
    }

    /**
     * create a message object and set its attributes for a file (uri given) and return it
     *
     * @param uri the uri of the file
     * @param isImageVideoUpload true if this is just a image/video (one step flow)
     * @param groupID the groupId to send to
     * @param userID the userId to send to
     * @param messageText the message text (only for the non-isImageVideoUpload flow)
     * @throws Exception if uri path is empty and others
     * @return the message (messageToSend)
     */
    public Message putAttachmentInfo(Uri uri, boolean isImageVideoUpload, int groupID, String userID, String messageText) throws Exception{
        MobiComUserPreference userPreference = MobiComUserPreference.getInstance(context);
            String filePath = uri.getPath();
            if (TextUtils.isEmpty(filePath)) {
                Utils.printLog(context, TAG, context.getResources().getString(R.string.info_file_attachment_error));
                throw new Exception(""+R.string.info_file_attachment_error);
            }
            Message messageToSend = new Message();
            if (groupID != 0) {
                messageToSend.setGroupId(groupID);
            } else {
                messageToSend.setTo(userID);
                messageToSend.setContactIds(userID);
            }
            messageToSend.setContentType(Message.ContentType.ATTACHMENT.getValue());
            messageToSend.setRead(Boolean.TRUE);
            messageToSend.setStoreOnDevice(Boolean.TRUE);
            if (messageToSend.getCreatedAtTime() == null) {
                messageToSend.setCreatedAtTime(System.currentTimeMillis() + userPreference.getDeviceTimeOffset());
            }
            messageToSend.setSendToDevice(Boolean.FALSE);
            messageToSend.setType(Message.MessageType.MT_OUTBOX.getValue());
            if(!isImageVideoUpload) {
                if(!TextUtils.isEmpty(messageText))
                messageToSend.setMessage(messageText);
            }
            messageToSend.setDeviceKeyString(userPreference.getDeviceKeyString());
            messageToSend.setSource(Message.Source.MT_MOBILE_APP.getValue());
            if (!TextUtils.isEmpty(filePath)) {
                List<String> filePaths = new ArrayList<String>();
                filePaths.add(filePath);
                messageToSend.setFilePaths(filePaths);
            }
            return messageToSend;
    }

    /**
     * get filter options from customization settings
     *
     * @param alCustomizationSettings the settings
     * @return the filter options
     */
    public FileUtils.GalleryFilterOptions getFilterOptions(AlCustomizationSettings alCustomizationSettings) {
        Map<String, Boolean> filterOptions;
        if (alCustomizationSettings.getFilterGallery() != null) {
            filterOptions = alCustomizationSettings.getFilterGallery();
        } else {
            filterOptions = ApplozicSetting.getInstance(context.getApplicationContext()).getGalleryFilterOptions();
        }

        FileUtils.GalleryFilterOptions choosenOption = FileUtils.GalleryFilterOptions.ALL_FILES;
        if (filterOptions != null) {
            for (FileUtils.GalleryFilterOptions option : FileUtils.GalleryFilterOptions.values()) {
                if (filterOptions.get(option.name())) {
                    choosenOption = option;
                    break;
                }
            }
        }
        return choosenOption;
    }

    /**
     * check if the mime type present is defined in filter options
     *
     * @param mimeType the mime type to check
     * @param alCustomizationSettings the settings to get filter options from
     * @return true/false accordingly
     */
    private boolean checkMimeType(String mimeType, AlCustomizationSettings alCustomizationSettings) {
        FileUtils.GalleryFilterOptions option = getFilterOptions(alCustomizationSettings);
        switch (option) {
            case ALL_FILES:
                return true;
            case IMAGE_VIDEO:
                return mimeType.contains("image/") || mimeType.contains("video/");
            case IMAGE_ONLY:
                return mimeType.contains("image/");
            case VIDEO_ONLY:
                return mimeType.contains("video/");
            case AUDIO_ONLY:
                return mimeType.contains("audio/");
        }
        return false;
    }

    /**
     * do a few checks and write the uri to a file(in the applozic folder)
     *
     * @param selectedFileUri the uri to process
     * @param alCustomizationSettings the customization settings
     * @param prePostUIMethods the interface for the pre and post async task methods
     * @return -1: attachment size exceeds max allowed size, -2: mimeType is empty, -3: mime type not supported
     * -4: format empty, -10: exception, 1: function end
     */
    public int processFile(Uri selectedFileUri, AlCustomizationSettings alCustomizationSettings, PrePostUIMethods prePostUIMethods) {
        if (selectedFileUri != null) {
            String fileName;
            try {
                long maxFileSize = alCustomizationSettings.getMaxAttachmentSizeAllowed() * 1024 * 1024;
                Cursor returnCursor =
                        context.getContentResolver().query(selectedFileUri, null, null, null, null);
                if (returnCursor != null) {
                    int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                    returnCursor.moveToFirst();
                    Long fileSize = returnCursor.getLong(sizeIndex);
                    returnCursor.close();
                    if (fileSize > maxFileSize) {
                        Utils.printLog(context, TAG, context.getResources().getString(R.string.info_attachment_max_allowed_file_size));
                        //Toast.makeText(this, R.string.info_attachment_max_allowed_file_size, Toast.LENGTH_LONG).show();
                        return -1;
                    }
                }
                String mimeType = FileUtils.getMimeTypeByContentUriOrOther(context, selectedFileUri);
                if (TextUtils.isEmpty(mimeType)) {
                    return -2;
                }
                if (!checkMimeType(mimeType, alCustomizationSettings)) {
                    //Toast.makeText(this, R.string.info_file_attachment_mime_type_not_supported, Toast.LENGTH_LONG).show();
                    return -3;
                }
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                //because images are selected multiple and quickly (milliseconds better in such a situation)
                timeStamp = timeStamp + "_" + System.currentTimeMillis();
                fileName = FileUtils.getFileName(context, selectedFileUri);

                String fileFormat = FileUtils.getFileFormat(fileName);
                String fileNameToWrite;
                if (TextUtils.isEmpty(fileFormat)) {
                    String format = FileUtils.getFileFormat(FileUtils.getFile(context, selectedFileUri).getAbsolutePath());
                    if (TextUtils.isEmpty(format)) {
                        return -4;
                    }
                    fileNameToWrite = timeStamp + "." + format;
                } else {
                    fileNameToWrite = timeStamp + "." + fileFormat;
                }
                File mediaFile = FileClientService.getFilePath(fileNameToWrite, context.getApplicationContext(), mimeType);
                new FileTaskAsync(mediaFile, selectedFileUri, context, prePostUIMethods).execute((Void) null);
            } catch (Exception e) {
                e.printStackTrace();
                return -10;
            }
        }
        return 1;
    }
}
