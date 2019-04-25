package com.applozic.mobicomkit.uiwidgets.conversation.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.provider.OpenableColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.applozic.mobicomkit.ApplozicClient;
import com.applozic.mobicomkit.api.MobiComKitConstants;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.account.user.User;
import com.applozic.mobicomkit.api.account.user.UserBlockTask;
import com.applozic.mobicomkit.api.account.user.UserService;
import com.applozic.mobicomkit.api.attachment.AttachmentView;
import com.applozic.mobicomkit.api.attachment.FileClientService;
import com.applozic.mobicomkit.api.attachment.FileMeta;
import com.applozic.mobicomkit.api.conversation.ApplozicMqttIntentService;
import com.applozic.mobicomkit.api.conversation.Message;
import com.applozic.mobicomkit.api.conversation.MessageClientService;
import com.applozic.mobicomkit.api.conversation.MessageIntentService;
import com.applozic.mobicomkit.api.conversation.MobiComConversationService;
import com.applozic.mobicomkit.api.conversation.SyncCallService;
import com.applozic.mobicomkit.api.conversation.database.MessageDatabaseService;
import com.applozic.mobicomkit.api.conversation.selfdestruct.DisappearingMessageTask;
import com.applozic.mobicomkit.api.conversation.service.ConversationService;
import com.applozic.mobicomkit.api.notification.MuteNotificationAsync;
import com.applozic.mobicomkit.api.notification.MuteNotificationRequest;
import com.applozic.mobicomkit.api.notification.NotificationService;
import com.applozic.mobicomkit.api.notification.MuteUserNotificationAsync;
import com.applozic.mobicomkit.api.people.UserIntentService;
import com.applozic.mobicomkit.broadcast.BroadcastService;
import com.applozic.mobicomkit.channel.service.ChannelService;
import com.applozic.mobicomkit.contact.AppContactService;
import com.applozic.mobicomkit.contact.MobiComVCFParser;
import com.applozic.mobicomkit.contact.VCFContactData;
import com.applozic.mobicomkit.feed.ApiResponse;
import com.applozic.mobicomkit.uiwidgets.AlCustomizationSettings;
import com.applozic.mobicomkit.uiwidgets.DashedLineView;
import com.applozic.mobicomkit.uiwidgets.KmFontManager;
import com.applozic.mobicomkit.uiwidgets.R;
import com.applozic.mobicomkit.uiwidgets.alphanumbericcolor.AlphaNumberColorUtil;
import com.applozic.mobicomkit.uiwidgets.async.AlMessageMetadataUpdateTask;
import com.applozic.mobicomkit.uiwidgets.attachmentview.ApplozicAudioManager;
import com.applozic.mobicomkit.uiwidgets.attachmentview.ApplozicAudioRecordManager;
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.DeleteConversationAsyncTask;
import com.applozic.mobicomkit.uiwidgets.conversation.MessageCommunicator;
import com.applozic.mobicomkit.uiwidgets.conversation.MobicomMessageTemplate;
import com.applozic.mobicomkit.uiwidgets.conversation.UIService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ChannelInfoActivity;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.FullScreenImageActivity;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.MobiComKitActivityInterface;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.RecyclerViewPositionHelper;
import com.applozic.mobicomkit.uiwidgets.conversation.adapter.ApplozicContextSpinnerAdapter;
import com.applozic.mobicomkit.uiwidgets.conversation.adapter.DetailedConversationAdapter;
import com.applozic.mobicomkit.uiwidgets.conversation.adapter.MobicomMessageTemplateAdapter;
import com.applozic.mobicomkit.uiwidgets.conversation.richmessaging.ALBookingDetailsModel;
import com.applozic.mobicomkit.uiwidgets.conversation.richmessaging.ALGuestCountModel;
import com.applozic.mobicomkit.uiwidgets.conversation.richmessaging.ALRichMessageListener;
import com.applozic.mobicomkit.uiwidgets.conversation.richmessaging.ALRichMessageModel;
import com.applozic.mobicomkit.uiwidgets.conversation.richmessaging.AlHotelBookingModel;
import com.applozic.mobicomkit.uiwidgets.conversation.richmessaging.AlRichMessage;
import com.applozic.mobicomkit.uiwidgets.conversation.richmessaging.payment.PaymentActivity;
import com.applozic.mobicomkit.uiwidgets.instruction.InstructionUtil;
import com.applozic.mobicomkit.uiwidgets.kommunicate.KmSettings;
import com.applozic.mobicomkit.uiwidgets.kommunicate.KommunicateUI;
import com.applozic.mobicomkit.uiwidgets.kommunicate.animators.OnBasketAnimationEndListener;
import com.applozic.mobicomkit.uiwidgets.kommunicate.callbacks.KmAwayMessageHandler;
import com.applozic.mobicomkit.uiwidgets.kommunicate.models.KmAwayMessageResponse;
import com.applozic.mobicomkit.uiwidgets.kommunicate.services.KmClientService;
import com.applozic.mobicomkit.uiwidgets.kommunicate.services.KmService;
import com.applozic.mobicomkit.uiwidgets.kommunicate.utils.KmUtils;
import com.applozic.mobicomkit.uiwidgets.kommunicate.views.KmRecordButton;
import com.applozic.mobicomkit.uiwidgets.kommunicate.views.KmRecordView;
import com.applozic.mobicomkit.uiwidgets.kommunicate.views.KmToast;
import com.applozic.mobicomkit.uiwidgets.people.fragment.UserProfileFragment;
import com.applozic.mobicomkit.uiwidgets.schedule.ConversationScheduler;
import com.applozic.mobicomkit.uiwidgets.schedule.ScheduledTimeHolder;
import com.applozic.mobicomkit.uiwidgets.uilistener.ContextMenuClickListener;
import com.applozic.mobicomkit.uiwidgets.uilistener.KmOnRecordListener;
import com.applozic.mobicomkit.uiwidgets.uilistener.KmStoragePermission;
import com.applozic.mobicomkit.uiwidgets.uilistener.KmStoragePermissionListener;
import com.applozic.mobicommons.ApplozicService;
import com.applozic.mobicommons.commons.core.utils.DateUtils;
import com.applozic.mobicommons.commons.core.utils.LocationUtils;
import com.applozic.mobicommons.commons.core.utils.Support;
import com.applozic.mobicommons.commons.core.utils.Utils;
import com.applozic.mobicommons.commons.image.ImageCache;
import com.applozic.mobicommons.commons.image.ImageLoader;
import com.applozic.mobicommons.commons.image.ImageUtils;
import com.applozic.mobicommons.emoticon.EmojiconHandler;
import com.applozic.mobicommons.file.FileUtils;
import com.applozic.mobicommons.json.GsonUtils;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.people.channel.ChannelUserMapper;
import com.applozic.mobicommons.people.channel.ChannelUtils;
import com.applozic.mobicommons.people.channel.Conversation;
import com.applozic.mobicommons.people.contact.Contact;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.Timer;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.VISIBLE;
import static java.util.Collections.disjoint;

/**
 * reg
 * Created by devashish on 10/2/15.
 */
abstract public class MobiComConversationFragment extends Fragment implements View.OnClickListener, ContextMenuClickListener, ALRichMessageListener, KmOnRecordListener, OnBasketAnimationEndListener {

    private static final String TAG = "MobiComConversation";
    public FrameLayout emoticonsFrameLayout, contextFrameLayout;
    public GridView multimediaPopupGrid;
    protected List<Conversation> conversations;
    protected String title = "Conversations";
    protected DownloadConversation downloadConversation;
    protected MobiComConversationService conversationService;
    protected TextView infoBroadcast;
    protected Class messageIntentClass;
    protected TextView emptyTextView;
    protected boolean loadMore = true;
    protected Contact contact;
    protected Channel channel;
    protected Integer currentConversationId;
    protected EditText messageEditText;
    protected KmRecordButton recordButton;
    protected ImageButton sendButton;
    protected ImageButton attachButton;
    protected Spinner sendType;
    protected LinearLayout individualMessageSendLayout, mainEditTextLinearLayout;
    protected LinearLayout extendedSendingOptionLayout;
    protected RelativeLayout attachmentLayout;
    protected ProgressBar mediaUploadProgressBar;
    protected View spinnerLayout;
    protected SwipeRefreshLayout swipeLayout;
    protected Button scheduleOption;
    protected ScheduledTimeHolder scheduledTimeHolder = new ScheduledTimeHolder();
    protected Spinner selfDestructMessageSpinner;
    protected ImageView mediaContainer;
    protected TextView attachedFile, userNotAbleToChatTextView;
    protected String filePath;
    protected boolean firstTimeMTexterFriend;
    protected MessageCommunicator messageCommunicator;
    protected List<Message> messageList = new ArrayList<Message>();
    protected DetailedConversationAdapter conversationAdapter = null;
    protected Drawable sentIcon;
    protected Drawable deliveredIcon;
    protected Drawable readIcon;
    protected Drawable pendingIcon;
    protected ImageButton emoticonsBtn;
    protected Support support;
    protected MultimediaOptionFragment multimediaOptionFragment = new MultimediaOptionFragment();
    protected boolean hideExtendedSendingOptionLayout;
    protected SyncCallService syncCallService;
    protected ApplozicContextSpinnerAdapter applozicContextSpinnerAdapter;
    protected Message messageToForward;
    protected String searchString;
    protected AlCustomizationSettings alCustomizationSettings;
    LinearLayout userNotAbleToChatLayout;
    List<ChannelUserMapper> channelUserMapperList;
    AdapterView.OnItemSelectedListener adapterView;
    MessageDatabaseService messageDatabaseService;
    AppContactService appContactService;
    ConversationUIService conversationUIService;
    long millisecond;
    MuteNotificationRequest muteNotificationRequest;
    List<String> restrictedWords;
    RelativeLayout replayRelativeLayout;
    ImageButton attachReplyCancelLayout;
    TextView nameTextView, messageTextView;
    ImageView galleryImageView;
    FileClientService fileClientService;
    ImageLoader imageThumbnailLoader, messageImageLoader;
    ImageView imageViewForAttachmentType;
    RelativeLayout imageViewRLayout;
    Map<String, String> messageMetaData = new HashMap<>();
    FrameLayout audioRecordFrameLayout;
    ApplozicAudioRecordManager applozicAudioRecordManager;
    ImageView slideImageView;
    private EmojiconHandler emojiIconHandler;
    private Bitmap previewThumbnail;
    protected TextView isTyping, bottomlayoutTextView;
    private String defaultText;
    private boolean typingStarted;
    private Integer channelKey;
    private Toolbar toolbar;
    private Menu menu;
    private Spinner contextSpinner;
    private boolean onSelected;
    private ImageCache imageCache;
    private RecyclerView messageTemplateView;
    private ImageButton cameraButton, locationButton, fileAttachmentButton;
    WeakReference<KmRecordButton> recordButtonWeakReference;
    RecyclerView recyclerView;
    RecyclerViewPositionHelper recyclerViewPositionHelper;
    protected LinearLayoutManager linearLayoutManager;
    DetailedConversationAdapter recyclerDetailConversationAdapter;
    MobicomMessageTemplate messageTemplate;
    MobicomMessageTemplateAdapter templateAdapter;
    DashedLineView awayMessageDivider;
    TextView awayMessageTv;
    TextView applozicLabel;
    RelativeLayout customToolbarLayout;
    CircleImageView toolbarImageView;
    TextView toolbarTitleText;
    TextView toolbarSubtitleText;
    TextView toolbarOnlineTv;
    TextView toolbarOfflineTv;
    TextView toolbarAlphabeticImage;
    private String geoApiKey;
    private FrameLayout emailReplyReminderLayout;
    private Contact conversationAssignee;
    public static final int TYPING_STOP_TIME = 30;
    public static final String KM_CONVERSATION_SUBJECT = "KM_CONVERSATION_SUBJECT";
    public Map<String, CountDownTimer> typingTimerMap;
    public int loggedInUserRole;
    public static final String AUDIO_RECORD_OPTION = ":audio";
    KmRecordView recordView;
    FrameLayout recordLayout;
    boolean isRecording = false;
    private KmFontManager fontManager;

    public void setEmojiIconHandler(EmojiconHandler emojiIconHandler) {
        this.emojiIconHandler = emojiIconHandler;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String jsonString = FileUtils.loadSettingsJsonFile(ApplozicService.getContext(getContext()));
        geoApiKey = Utils.getMetaDataValue(ApplozicService.getContext(getContext()), ConversationActivity.GOOGLE_API_KEY_META_DATA);
        if (!TextUtils.isEmpty(jsonString)) {
            alCustomizationSettings = (AlCustomizationSettings) GsonUtils.getObjectFromJson(jsonString, AlCustomizationSettings.class);
        } else {
            alCustomizationSettings = new AlCustomizationSettings();
        }

        fontManager = new KmFontManager(getContext(), alCustomizationSettings);

        restrictedWords = FileUtils.loadRestrictedWordsFile(ApplozicService.getContext(getContext()));
        conversationUIService = new ConversationUIService(getActivity());
        syncCallService = SyncCallService.getInstance(getActivity());
        appContactService = new AppContactService(getActivity());
        messageDatabaseService = new MessageDatabaseService(getActivity());
        fileClientService = new FileClientService(getActivity());
        setHasOptionsMenu(true);
        imageThumbnailLoader = new ImageLoader(ApplozicService.getContext(getContext()), ImageUtils.getLargestScreenDimension((Activity) getContext())) {
            @Override
            protected Bitmap processBitmap(Object data) {
                return fileClientService.loadThumbnailImage(getContext(), (Message) data, getImageLayoutParam(false).width, getImageLayoutParam(false).height);
            }
        };

        imageCache = ImageCache.getInstance((getActivity()).getSupportFragmentManager(), 0.1f);
        imageThumbnailLoader.setImageFadeIn(false);
        imageThumbnailLoader.addImageCache((getActivity()).getSupportFragmentManager(), 0.1f);
        messageImageLoader = new ImageLoader(ApplozicService.getContext(getContext()), ImageUtils.getLargestScreenDimension((Activity) getContext())) {
            @Override
            protected Bitmap processBitmap(Object data) {
                return fileClientService.loadMessageImage(getContext(), (String) data);
            }
        };
        messageImageLoader.setImageFadeIn(false);
        messageImageLoader.addImageCache((getActivity()).getSupportFragmentManager(), 0.1f);
        applozicAudioRecordManager = new ApplozicAudioRecordManager(getActivity());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View list = inflater.inflate(R.layout.mobicom_message_list, container, false);
        recyclerView = (RecyclerView) list.findViewById(R.id.messageList);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerViewPositionHelper = new RecyclerViewPositionHelper(recyclerView, linearLayoutManager);
        ((ConversationActivity) getActivity()).setChildFragmentLayoutBGToTransparent();
        messageList = new ArrayList<Message>();
        multimediaPopupGrid = (GridView) list.findViewById(R.id.mobicom_multimedia_options1);
        loggedInUserRole = MobiComUserPreference.getInstance(getContext()).getUserRoleType();

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        toolbar = (Toolbar) getActivity().findViewById(R.id.my_toolbar);
        toolbar.setClickable(true);

        customToolbarLayout = toolbar.findViewById(R.id.custom_toolbar_root_layout);

        RelativeLayout faqButtonLayout = toolbar.findViewById(R.id.faqButtonLayout);
        if (faqButtonLayout != null) {
            faqButtonLayout.setVisibility(View.GONE);
        }

        if (customToolbarLayout != null) {
            customToolbarLayout.setVisibility(View.GONE);
            toolbarImageView = customToolbarLayout.findViewById(R.id.conversation_contact_photo);
            toolbarSubtitleText = customToolbarLayout.findViewById(R.id.toolbar_subtitle);
            if (fontManager != null && fontManager.getToolbarSubtitleFont() != null) {
                toolbarSubtitleText.setTypeface(fontManager.getToolbarSubtitleFont());
            }
            toolbarTitleText = customToolbarLayout.findViewById(R.id.toolbar_title);
            if (fontManager != null && fontManager.getToolbarTitleFont() != null) {
                toolbarTitleText.setTypeface(fontManager.getToolbarTitleFont());
            }
            toolbarOnlineTv = customToolbarLayout.findViewById(R.id.onlineTextView);
            toolbarOfflineTv = customToolbarLayout.findViewById(R.id.offlineTextView);
            toolbarAlphabeticImage = customToolbarLayout.findViewById(R.id.toolbarAlphabeticImage);

            TextView faqOption = customToolbarLayout.findViewById(R.id.kmFaqOption);

            if (KmSettings.getInstance(getContext()).isFaqOptionEnabled() || alCustomizationSettings.isFaqOptionEnabled(2)) {
                faqOption.setVisibility(View.VISIBLE);
                if (faqOption != null) {
                    faqOption.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ConversationActivity.openFaq(getActivity(), new KmClientService(getContext()).getHelpCenterUrl());
                        }
                    });
                }
            } else {
                faqOption.setVisibility(View.GONE);
            }
        }

        mainEditTextLinearLayout = (LinearLayout) list.findViewById(R.id.main_edit_text_linear_layout);
        individualMessageSendLayout = (LinearLayout) list.findViewById(R.id.individual_message_send_layout);
        sendButton = (ImageButton) individualMessageSendLayout.findViewById(R.id.conversation_send);

        recordLayout = list.findViewById(R.id.kmRecordLayout);

        if (alCustomizationSettings != null
                && alCustomizationSettings.getAttachmentOptions() != null
                && alCustomizationSettings.getAttachmentOptions().get(AUDIO_RECORD_OPTION) != null
                && alCustomizationSettings.getAttachmentOptions().get(AUDIO_RECORD_OPTION)) {
            recordLayout.setVisibility(VISIBLE);
        }

        recordView = list.findViewById(R.id.km_record_view);
        recordView.setOnBasketAnimationEndListener(this);
        recordView.setOnRecordListener(this);
        recordButton = list.findViewById(R.id.audio_record_button);
        recordButton.setRecordView(recordView);
        recordButton.setListenForRecord(true);

        mainEditTextLinearLayout = (LinearLayout) list.findViewById(R.id.main_edit_text_linear_layout);
        messageTemplateView = (RecyclerView) list.findViewById(R.id.mobicomMessageTemplateView);
        applozicLabel = list.findViewById(R.id.applozicLabel);
        cameraButton = list.findViewById(R.id.camera_btn);
        locationButton = list.findViewById(R.id.location_btn);
        fileAttachmentButton = list.findViewById(R.id.file_as_attachment_btn);
        emailReplyReminderLayout = list.findViewById(R.id.emailReplyReminderView);
        processAttachmentIconsClick();
        Configuration config = getResources().getConfiguration();
        recordButtonWeakReference = new WeakReference<>(recordButton);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
                sendButton.setScaleX(-1);
                mainEditTextLinearLayout.setBackgroundResource(R.drawable.applozic_chat_left_icon);
                audioRecordFrameLayout.setBackgroundResource(R.drawable.applozic_chat_left_icon);
                slideImageView.setImageResource(R.drawable.slide_arrow_right);
            }
        }

        boolean isAgentApp = alCustomizationSettings != null && alCustomizationSettings.isAgentApp();

        if (!isAgentApp && MobiComUserPreference.getInstance(getContext()).getPricingPackage() == 1) {
            applozicLabel.setVisibility(VISIBLE);
        }

        extendedSendingOptionLayout = (LinearLayout) list.findViewById(R.id.extended_sending_option_layout);

        attachmentLayout = (RelativeLayout) list.findViewById(R.id.attachment_layout);
        isTyping = (TextView) list.findViewById(R.id.isTyping);

        contextFrameLayout = (FrameLayout) list.findViewById(R.id.contextFrameLayout);

        contextSpinner = (Spinner) list.findViewById(R.id.spinner_show);
        adapterView = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                if (conversations != null && conversations.size() > 0) {
                    Conversation conversation = conversations.get(pos);
                    BroadcastService.currentConversationId = conversation.getId();
                    if (onSelected) {
                        currentConversationId = conversation.getId();
                        if (messageList != null) {
                            messageList.clear();
                        }
                        downloadConversation = new DownloadConversation(recyclerView, true, 1, 0, 0, contact, channel, conversation.getId());
                        downloadConversation.execute();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        };

        mediaUploadProgressBar = (ProgressBar) attachmentLayout.findViewById(R.id.media_upload_progress_bar);
        emoticonsFrameLayout = (FrameLayout) list.findViewById(R.id.emojicons_frame_layout);
        emoticonsBtn = (ImageButton) list.findViewById(R.id.emoji_btn);
        replayRelativeLayout = (RelativeLayout) list.findViewById(R.id.reply_message_layout);
        messageTextView = (TextView) list.findViewById(R.id.messageTextView);
        galleryImageView = (ImageView) list.findViewById(R.id.imageViewForPhoto);
        nameTextView = (TextView) list.findViewById(R.id.replyNameTextView);
        attachReplyCancelLayout = (ImageButton) list.findViewById(R.id.imageCancel);
        imageViewRLayout = (RelativeLayout) list.findViewById(R.id.imageViewRLayout);
        imageViewForAttachmentType = (ImageView) list.findViewById(R.id.imageViewForAttachmentType);
        spinnerLayout = inflater.inflate(R.layout.mobicom_message_list_header_footer, null);
        infoBroadcast = (TextView) spinnerLayout.findViewById(R.id.info_broadcast);
        spinnerLayout.setVisibility(View.GONE);
        emptyTextView = (TextView) list.findViewById(R.id.noConversations);
        emptyTextView.setTextColor(Color.parseColor(alCustomizationSettings.getNoConversationLabelTextColor().trim()));
        emoticonsBtn.setOnClickListener(this);
        sentIcon = getResources().getDrawable(R.drawable.km_sent_icon_c);
        deliveredIcon = getResources().getDrawable(R.drawable.km_delivered_icon_c);
        readIcon = getResources().getDrawable(R.drawable.km_read_icon_c);
        pendingIcon = getResources().getDrawable(R.drawable.km_pending_icon_c);

        awayMessageDivider = list.findViewById(R.id.awayMessageDivider);
        awayMessageTv = list.findViewById(R.id.awayMessageTV);

        boolean isRecordOptionEnabled = alCustomizationSettings != null && alCustomizationSettings.getAttachmentOptions() != null
                && alCustomizationSettings.getAttachmentOptions().get(AUDIO_RECORD_OPTION) != null
                && alCustomizationSettings.getAttachmentOptions().get(AUDIO_RECORD_OPTION);

        recordButton.setVisibility(isRecordOptionEnabled ? View.VISIBLE : View.GONE);
        sendButton.setVisibility(isRecordOptionEnabled ? View.GONE : View.VISIBLE);

        GradientDrawable bgShape = (GradientDrawable) sendButton.getBackground();
        bgShape.setColor(Color.parseColor(alCustomizationSettings.getSendButtonBackgroundColor().trim()));

        attachButton = (ImageButton) individualMessageSendLayout.findViewById(R.id.attach_button);

        sendType = (Spinner) extendedSendingOptionLayout.findViewById(R.id.sendTypeSpinner);
        messageEditText = (EditText) individualMessageSendLayout.findViewById(R.id.conversation_message);

        if (fontManager != null && fontManager.getMessageEditTextFont() != null) {
            messageEditText.setTypeface(fontManager.getMessageEditTextFont());
        }

        messageEditText.setTextColor(Color.parseColor(alCustomizationSettings.getMessageEditTextTextColor()));

        messageEditText.setHintTextColor(Color.parseColor(alCustomizationSettings.getMessageEditTextHintTextColor()));

        userNotAbleToChatLayout = (LinearLayout) list.findViewById(R.id.user_not_able_to_chat_layout);
        userNotAbleToChatTextView = (TextView) userNotAbleToChatLayout.findViewById(R.id.user_not_able_to_chat_textView);
        userNotAbleToChatTextView.setTextColor(Color.parseColor(alCustomizationSettings.getUserNotAbleToChatTextColor()));

        if (channel != null && channel.isDeleted()) {
            userNotAbleToChatTextView.setText(R.string.group_has_been_deleted_text);
        }


        bottomlayoutTextView = (TextView) list.findViewById(R.id.user_not_able_to_chat_textView);
        if (!TextUtils.isEmpty(defaultText)) {
            messageEditText.setText(defaultText);
            defaultText = "";
        }
        scheduleOption = (Button) extendedSendingOptionLayout.findViewById(R.id.scheduleOption);
        mediaContainer = (ImageView) attachmentLayout.findViewById(R.id.media_container);
        attachedFile = (TextView) attachmentLayout.findViewById(R.id.attached_file);
        ImageView closeAttachmentLayout = (ImageView) attachmentLayout.findViewById(R.id.close_attachment_layout);

        swipeLayout = (SwipeRefreshLayout) list.findViewById(R.id.swipe_container);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        ArrayAdapter<CharSequence> sendTypeAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.send_type_options, R.layout.mobiframework_custom_spinner);

        sendTypeAdapter.setDropDownViewResource(R.layout.mobiframework_custom_spinner);
        sendType.setAdapter(sendTypeAdapter);

        scheduleOption.setOnClickListener(new View.OnClickListener() {

                                              @Override
                                              public void onClick(View v) {
                                                  ConversationScheduler conversationScheduler = new ConversationScheduler();
                                                  conversationScheduler.setScheduleOption(scheduleOption);
                                                  conversationScheduler.setScheduledTimeHolder(scheduledTimeHolder);
                                                  conversationScheduler.setCancelable(false);
                                                  conversationScheduler.show(getActivity().getSupportFragmentManager(), "conversationScheduler");
                                              }
                                          }
        );

        messageEditText.addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //TODO: write code to emoticons .....
            }

            public void afterTextChanged(Editable s) {
                try {
                    if (!TextUtils.isEmpty(s.toString()) && s.toString().trim().length() > 0 && !typingStarted) {
                        typingStarted = true;
                        handleSendAndRecordButtonView(true);
                        Intent intent = new Intent(getActivity(), ApplozicMqttIntentService.class);
                        intent.putExtra(ApplozicMqttIntentService.CHANNEL, channel);
                        intent.putExtra(ApplozicMqttIntentService.CONTACT, contact);
                        intent.putExtra(ApplozicMqttIntentService.TYPING, typingStarted);
                        ApplozicMqttIntentService.enqueueWork(getActivity(), intent);
                    } else if (s.toString().trim().length() == 0 && typingStarted) {
                        typingStarted = false;
                        handleSendAndRecordButtonView(false);
                        Intent intent = new Intent(getActivity(), ApplozicMqttIntentService.class);
                        intent.putExtra(ApplozicMqttIntentService.CHANNEL, channel);
                        intent.putExtra(ApplozicMqttIntentService.CONTACT, contact);
                        intent.putExtra(ApplozicMqttIntentService.TYPING, typingStarted);
                        ApplozicMqttIntentService.enqueueWork(getActivity(), intent);
                    }

                } catch (Exception e) {

                }
            }
        });

        messageEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                emoticonsFrameLayout.setVisibility(View.GONE);
            }
        });

        attachReplyCancelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageMetaData = null;
                replayRelativeLayout.setVisibility(View.GONE);
            }
        });

        messageEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (typingStarted) {
                        Intent intent = new Intent(getActivity(), ApplozicMqttIntentService.class);
                        intent.putExtra(ApplozicMqttIntentService.CHANNEL, channel);
                        intent.putExtra(ApplozicMqttIntentService.CONTACT, contact);
                        intent.putExtra(ApplozicMqttIntentService.TYPING, typingStarted);
                        ApplozicMqttIntentService.enqueueWork(getActivity(), intent);

                    }
                    emoticonsFrameLayout.setVisibility(View.GONE);

                    multimediaPopupGrid.setVisibility(View.GONE);
                }
            }

        });

        messageEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    Utils.toggleSoftKeyBoard(getActivity(), true);
                    return true;
                }
                return false;
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View view) {
                                              emoticonsFrameLayout.setVisibility(View.GONE);
                                              sendMessage();
                                              if (contact != null && !contact.isBlocked() || channel != null) {
                                                  handleSendAndRecordButtonView(false);
                                              }
                                          }
                                      }
        );


        closeAttachmentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filePath = null;
                if (previewThumbnail != null) {
                    previewThumbnail.recycle();
                }
                attachmentLayout.setVisibility(View.GONE);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (recyclerDetailConversationAdapter != null) {
                    recyclerDetailConversationAdapter.contactImageLoader.setPauseWork(newState == RecyclerView.SCROLL_STATE_DRAGGING);
                }
            }

            @Override
            public void onScrolled(final RecyclerView recyclerView, int dx, int dy) {
                //super.onScrolled(recyclerView, dx, dy);
                if (loadMore) {
                    int topRowVerticalPosition =
                            (recyclerView == null || recyclerView.getChildCount() == 0) ?
                                    0 : recyclerView.getChildAt(0).getTop();
                    swipeLayout.setEnabled(topRowVerticalPosition >= 0);
                }
            }
        });

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (channel != null) {
                    if (channel.isDeleted()) {
                        return;
                    }
                    if (Channel.GroupType.SUPPORT_GROUP.getValue().equals(channel.getType())
                            && User.RoleType.USER_ROLE.getValue().equals(MobiComUserPreference.getInstance(getContext()).getUserRoleType())) {
                        return;
                    }
                    if (alCustomizationSettings.isGroupInfoScreenVisible() && !Channel.GroupType.GROUPOFTWO.getValue().equals(channel.getType())) {
                        Intent channelInfo = new Intent(getActivity(), ChannelInfoActivity.class);
                        channelInfo.putExtra(ChannelInfoActivity.CHANNEL_KEY, channel.getKey());
                        startActivity(channelInfo);
                    } else if (Channel.GroupType.GROUPOFTWO.getValue().equals(channel.getType()) && alCustomizationSettings.isUserProfileFragment()) {
                        UserProfileFragment userProfileFragment = (UserProfileFragment) UIService.getFragmentByTag(getActivity(), ConversationUIService.USER_PROFILE_FRAMENT);
                        if (userProfileFragment == null) {
                            String userId = ChannelService.getInstance(getActivity()).getGroupOfTwoReceiverUserId(channel.getKey());
                            if (!TextUtils.isEmpty(userId)) {
                                Contact newcContact = appContactService.getContactById(userId);
                                userProfileFragment = new UserProfileFragment();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(ConversationUIService.CONTACT, newcContact);
                                userProfileFragment.setArguments(bundle);
                                ConversationActivity.addFragment(getActivity(), userProfileFragment, ConversationUIService.USER_PROFILE_FRAMENT);
                            }
                        }
                    }
                } else {
                    if (alCustomizationSettings.isUserProfileFragment()) {
                        UserProfileFragment userProfileFragment = (UserProfileFragment) UIService.getFragmentByTag(getActivity(), ConversationUIService.USER_PROFILE_FRAMENT);
                        if (userProfileFragment == null) {
                            userProfileFragment = new UserProfileFragment();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(ConversationUIService.CONTACT, contact);
                            userProfileFragment.setArguments(bundle);
                            ConversationActivity.addFragment(getActivity(), userProfileFragment, ConversationUIService.USER_PROFILE_FRAMENT);
                        }
                    }

                }
            }
        });
        recyclerView.setLongClickable(true);
        messageTemplate = alCustomizationSettings.getMessageTemplate();

        if (messageTemplate != null && messageTemplate.isEnabled()) {
            messageTemplateView.setVisibility(View.VISIBLE);
            templateAdapter = new MobicomMessageTemplateAdapter(messageTemplate);
            MobicomMessageTemplateAdapter.MessageTemplateDataListener listener = new MobicomMessageTemplateAdapter.MessageTemplateDataListener() {
                @Override
                public void onItemSelected(String message) {

                    final Message lastMessage = !messageList.isEmpty() ? messageList.get(messageList.size() - 1) : null;

                    if ((messageTemplate.getTextMessageList() != null && !messageTemplate.getTextMessageList().getMessageList().isEmpty() && messageTemplate.getTextMessageList().isSendMessageOnClick() && "text".equals(getMessageType(lastMessage)))
                            || (messageTemplate.getImageMessageList() != null && !messageTemplate.getImageMessageList().getMessageList().isEmpty() && messageTemplate.getImageMessageList().isSendMessageOnClick() && "image".equals(getMessageType(lastMessage)))
                            || (messageTemplate.getVideoMessageList() != null && !messageTemplate.getVideoMessageList().getMessageList().isEmpty() && messageTemplate.getVideoMessageList().isSendMessageOnClick() && "video".equals(getMessageType(lastMessage)))
                            || (messageTemplate.getLocationMessageList() != null && !messageTemplate.getLocationMessageList().getMessageList().isEmpty() && messageTemplate.getLocationMessageList().isSendMessageOnClick() && "location".equals(getMessageType(lastMessage)))
                            || (messageTemplate.getContactMessageList() != null && !messageTemplate.getContactMessageList().getMessageList().isEmpty() && messageTemplate.getContactMessageList().isSendMessageOnClick() && "contact".equals(getMessageType(lastMessage)))
                            || (messageTemplate.getAudioMessageList() != null && !messageTemplate.getAudioMessageList().getMessageList().isEmpty() && messageTemplate.getAudioMessageList().isSendMessageOnClick() && "audio".equals(getMessageType(lastMessage)))
                            || messageTemplate.getSendMessageOnClick()) {
                        sendMessage(message);
                    }

                    if (messageTemplate.getHideOnSend()) {
                        AlMessageMetadataUpdateTask.MessageMetadataListener listener1 = new AlMessageMetadataUpdateTask.MessageMetadataListener() {
                            @Override
                            public void onSuccess(Context context, String message) {
                                templateAdapter.setMessageList(new HashMap<String, String>());
                                templateAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onFailure(Context context, String error) {
                            }
                        };

                        if (lastMessage != null) {
                            Map<String, String> metadata = lastMessage.getMetadata();
                            metadata.put("isDoneWithClicking", "true");
                            lastMessage.setMetadata(metadata);
                            new AlMessageMetadataUpdateTask(getContext(), lastMessage.getKeyString(), lastMessage.getMetadata(), listener1).execute();
                        }
                    }

                    final Intent intent = new Intent();
                    intent.setAction("com.applozic.mobicomkit.TemplateMessage");
                    intent.putExtra("templateMessage", message);
                    intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                    ApplozicService.getContext(getContext()).sendBroadcast(intent);
                }
            };

            templateAdapter.setOnItemSelected(listener);
            LinearLayoutManager horizontalLayoutManagaer
                    = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            messageTemplateView.setLayoutManager(horizontalLayoutManagaer);
            messageTemplateView.setAdapter(templateAdapter);
        }

        createTemplateMessages();

        if (channel != null && alCustomizationSettings.isEnableAwayMessage()) {
            KommunicateUI.getAwayMessage(getContext(), channel.getKey(), new KmAwayMessageHandler() {
                @Override
                public void onSuccess(Context context, KmAwayMessageResponse.KmMessageResponse response) {
                    showAwayMessage(true, response.getMessage());
                }

                @Override
                public void onFailure(Context context, Exception e, String response) {
                    Utils.printLog(context, TAG, "Response : " + response + " ----- Exception : " + e);
                }
            });
        }

        emoticonsBtn.setVisibility(View.GONE);

        if (alCustomizationSettings.getAttachmentOptions() != null && !alCustomizationSettings.getAttachmentOptions().isEmpty()) {
            Map<String, Boolean> attachmentOptions = alCustomizationSettings.getAttachmentOptions();

            if (attachmentOptions.containsKey(":location")) {
                locationButton.setVisibility(attachmentOptions.get(":location") ? VISIBLE : View.GONE);
            }

            if (attachmentOptions.containsKey(":camera")) {
                cameraButton.setVisibility(attachmentOptions.get(":camera") ? VISIBLE : View.GONE);
            }

            if (attachmentOptions.containsKey(":file")) {
                fileAttachmentButton.setVisibility(attachmentOptions.get(":file") ? VISIBLE : View.GONE);
            }
        }

        return list;
    }

    public void handleSendAndRecordButtonView(boolean isSendButtonVisible) {
        boolean showRecordButton = alCustomizationSettings != null
                && alCustomizationSettings.getAttachmentOptions() != null
                && alCustomizationSettings.getAttachmentOptions().get(AUDIO_RECORD_OPTION) != null
                && alCustomizationSettings.getAttachmentOptions().get(AUDIO_RECORD_OPTION);
        sendButton.setVisibility(showRecordButton ? isSendButtonVisible ? View.VISIBLE : View.GONE : View.VISIBLE);
        recordButton.setVisibility(showRecordButton ? isSendButtonVisible ? View.GONE : View.VISIBLE : View.GONE);
    }

    @SuppressLint("MissingPermission")
    private void vibrate() {
        try {
            if (getActivity() != null) {
                Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                if (vibrator != null) {
                    vibrator.vibrate(200);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void sendMessage() {
        if (channel != null) {
            if (Channel.GroupType.GROUPOFTWO.getValue().equals(channel.getType())) {
                String userId = ChannelService.getInstance(getActivity()).getGroupOfTwoReceiverUserId(channel.getKey());
                if (!TextUtils.isEmpty(userId)) {
                    Contact withUserContact = appContactService.getContactById(userId);
                    if (withUserContact.isBlocked()) {
                        userBlockDialog(false, withUserContact, true);
                    } else {
                        processSendMessage();
                    }
                }
            } else if (Channel.GroupType.OPEN.getValue().equals(channel.getType())) {
                if (Utils.isInternetAvailable(getActivity())) {
                    processSendMessage();
                } else {
                    Toast.makeText(ApplozicService.getContext(getContext()), ApplozicService.getContext(getContext()).getString(R.string.internet_connection_not_available), Toast.LENGTH_SHORT).show();
                }
            } else {
                processSendMessage();
            }
        } else if (contact != null) {
            if (contact.isBlocked()) {
                userBlockDialog(false, contact, false);
            } else {
                processSendMessage();
            }
        }
    }

    protected void processSendMessage() {
        if (!TextUtils.isEmpty(messageEditText.getText().toString().trim()) || !TextUtils.isEmpty(filePath)) {
            String inputMessage = messageEditText.getText().toString();
            String[] inputMsg = inputMessage.toLowerCase().split(" ");
            List<String> userInputList = Arrays.asList(inputMsg);

            boolean disjointResult = (restrictedWords == null) || disjoint(restrictedWords, userInputList);

            if (disjointResult) {

                sendMessage(messageEditText.getText().toString().trim());
                messageEditText.setText("");
                scheduleOption.setText(R.string.ScheduleText);
                if (scheduledTimeHolder.getTimestamp() != null) {
                    showScheduleMessageToast();
                }
                scheduledTimeHolder.resetScheduledTimeHolder();

            } else {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity()).
                        setPositiveButton(R.string.ok_alert, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                alertDialog.setTitle(alCustomizationSettings.getRestrictedWordMessage());
                alertDialog.setCancelable(true);
                alertDialog.create().show();

            }
        }

    }

    public void showScheduleMessageToast() {
        if (getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(ApplozicService.getContext(getContext()), ApplozicService.getContext(getContext()).getString(R.string.info_message_scheduled), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteMessageFromDeviceList(String messageKeyString) {
        try {
            int position;
            boolean updateQuickConversation = false;
            int index;
            for (Message message : messageList) {
                boolean value = message.getKeyString() != null ? message.getKeyString().equals(messageKeyString) : false;
                if (value) {
                    index = messageList.indexOf(message);
                    if (index != -1) {
                        int aboveIndex = index - 1;
                        int belowIndex = index + 1;
                        Message aboveMessage = messageList.get(aboveIndex);
                        if (belowIndex != messageList.size()) {
                            Message belowMessage = messageList.get(belowIndex);
                            if (aboveMessage.isTempDateType() && belowMessage.isTempDateType()) {
                                messageList.remove(aboveMessage);
                            }
                        } else if (belowIndex == messageList.size() && aboveMessage.isTempDateType()) {
                            messageList.remove(aboveMessage);
                        }
                    }
                }
                if (message.getKeyString() != null && message.getKeyString().equals(messageKeyString)) {
                    position = messageList.indexOf(message);

                    if (position == messageList.size() - 1) {
                        updateQuickConversation = true;
                    }
                    if (message.getScheduledAt() != null && message.getScheduledAt() != 0) {
                        messageDatabaseService.deleteScheduledMessage(messageKeyString);
                    }
                    messageList.remove(position);
                    recyclerDetailConversationAdapter.notifyDataSetChanged();
                    if (messageList.isEmpty()) {
                        emptyTextView.setVisibility(VISIBLE);
                        if (getActivity() != null) {
                            ((MobiComKitActivityInterface) getActivity()).removeConversation(message, channel != null ? String.valueOf(channel.getKey()) : contact.getFormattedContactNumber());
                        }
                    }
                    break;
                }
            }
            int messageListSize = messageList.size();
            if (messageListSize > 0 && updateQuickConversation && getActivity() != null) {
                ((MobiComKitActivityInterface) getActivity()).updateLatestMessage(messageList.get(messageListSize - 1), channel != null ? String.valueOf(channel.getKey()) : contact.getFormattedContactNumber());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getCurrentUserId() {
        if (contact == null) {
            return "";
        }
        return contact.getUserId() != null ? contact.getUserId() : contact.getFormattedContactNumber();
    }

    public Contact getContact() {
        return contact;
    }

    protected void setContact(Contact contact) {
        this.contact = contact;
    }

    public String getFormattedContactNumber() {
        return contact != null ? contact.getFormattedContactNumber() : null;
    }

    public boolean hasMultiplePhoneNumbers() {
        return contact != null && contact.hasMultiplePhoneNumbers();
    }

    public MultimediaOptionFragment getMultimediaOptionFragment() {
        return multimediaOptionFragment;
    }

    public Spinner getSendType() {
        return sendType;
    }

    public Spinner getSelfDestructMessageSpinner() {
        return selfDestructMessageSpinner;
    }

    public Button getScheduleOption() {
        return scheduleOption;
    }

    public void setFirstTimeMTexterFriend(boolean firstTimeMTexterFriend) {
        this.firstTimeMTexterFriend = firstTimeMTexterFriend;
    }

//    public EmojiconEditText getMessageEditText() {
//        return messageEditText;
//    }

    public void clearList() {
        if (getActivity() == null) {
            return;
        }
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (recyclerDetailConversationAdapter != null) {
                    messageList.clear();
                    if (messageList.isEmpty()) {
                        emptyTextView.setVisibility(View.VISIBLE);
                    }
                    recyclerDetailConversationAdapter.notifyDataSetChanged();
                }
                if (applozicContextSpinnerAdapter != null) {
                    contextFrameLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    public void updateMessage(final Message message) {
        if (getActivity() == null) {
            return;
        }
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Note: Removing and adding the same message again as the new sms object will contain the keyString.
                messageList.remove(message);
                messageList.add(message);
                recyclerDetailConversationAdapter.notifyDataSetChanged();
            }
        });
    }

    public void updateMessageMetadata(String keyString) {
        int i = -1;
        if (!messageList.isEmpty()) {
            for (Message message : messageList) {
                if (keyString.equals(message.getKeyString())) {
                    i = messageList.indexOf(message);
                }
            }
        }
        if (i != -1) {
            messageList.get(i).setMetadata(messageDatabaseService.getMessage(keyString).getMetadata());
            conversationAdapter.notifyDataSetChanged();
            if (messageList.get(messageList.size() - 1).getMetadata().containsKey("isDoneWithClicking")) {
                templateAdapter.setMessageList(new HashMap<String, String>());
                templateAdapter.notifyDataSetChanged();
            }
        }
    }

    public void addMessage(final Message message) {
        hideAwayMessage(message);

        if (message.getGroupId() != null) {
            if (channel != null && channel.getKey().equals(message.getGroupId())) {
                if (message.getTo() != null) {
                    updateTypingStatus(message.getTo(), false);
                }
            }
        } else if (contact != null && contact.getContactIds().equals(message.getTo())) {
            updateTypingStatus(message.getTo(), false);
        }

        if (getActivity() == null) {
            return;
        }

        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Todo: Handle disappearing messages.
                boolean added = updateMessageList(message, false);
                if (added) {
                    //Todo: update unread count
                    linearLayoutManager.setStackFromEnd(true);
                    recyclerDetailConversationAdapter.notifyDataSetChanged();
                    linearLayoutManager.scrollToPositionWithOffset(messageList.size() - 1, 0);
                    emptyTextView.setVisibility(View.GONE);
                    currentConversationId = message.getConversationId();
                    channelKey = message.getGroupId();
                    if (Message.MessageType.MT_INBOX.getValue().equals(message.getType())) {
                        try {
                            messageDatabaseService.updateReadStatusForKeyString(message.getKeyString());
                            Intent intent = new Intent(getActivity(), UserIntentService.class);
                            intent.putExtra(UserIntentService.SINGLE_MESSAGE_READ, true);
                            intent.putExtra(UserIntentService.CONTACT, contact);
                            intent.putExtra(UserIntentService.CHANNEL, channel);
                            UserIntentService.enqueueWork(getActivity(), intent);
                        } catch (Exception e) {
                            Utils.printLog(getContext(), TAG, "Got exception while read");
                        }
                    }
                }
                createTemplateMessages();
                selfDestructMessage(message);
            }
        });
    }

    protected abstract void processMobiTexterUserCheck();

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        this.menu = menu;

        if (contact != null && contact.isDeleted()) {
            menu.findItem(R.id.dial).setVisible(false);
            menu.findItem(R.id.refresh).setVisible(false);
            menu.removeItem(R.id.conversations);
            menu.findItem(R.id.userBlock).setVisible(false);
            menu.findItem(R.id.userUnBlock).setVisible(false);
            menu.findItem(R.id.dial).setVisible(false);
            return;
        }

        String contactNumber = contact != null ? contact.getContactNumber() : null;
        ApplozicClient setting = ApplozicClient.getInstance(getActivity());

        if ((setting.isHandleDial() && !TextUtils.isEmpty(contactNumber) && contactNumber.length() > 2)
                || (setting.isIPCallEnabled())) {
            if (setting.isIPCallEnabled()) {
                menu.findItem(R.id.dial).setVisible(true);
                menu.findItem(R.id.video_call).setVisible(true);
            }
            if (setting.isHandleDial()) {
                menu.findItem(R.id.dial).setVisible(true);
            }
        } else {
            menu.findItem(R.id.video_call).setVisible(false);
            menu.findItem(R.id.dial).setVisible(false);
        }
        if (channel != null) {
            menu.findItem(R.id.dial).setVisible(false);
            menu.findItem(R.id.video_call).setVisible(false);
            menu.findItem(R.id.share_conversation).setVisible(Channel.GroupType.SUPPORT_GROUP.getValue().equals(channel.getType()) && alCustomizationSettings.isEnableShareConversation());

            if (Channel.GroupType.GROUPOFTWO.getValue().equals(channel.getType())) {
                String userId = ChannelService.getInstance(getActivity()).getGroupOfTwoReceiverUserId(channel.getKey());
                if (!TextUtils.isEmpty(userId) && alCustomizationSettings.isBlockOption()) {
                    Contact withUserContact = appContactService.getContactById(userId);
                    if (withUserContact.isBlocked()) {
                        menu.findItem(R.id.userUnBlock).setVisible(true);
                    } else {
                        menu.findItem(R.id.userBlock).setVisible(true);
                    }
                }
            } else {
                menu.findItem(R.id.userBlock).setVisible(false);
                menu.findItem(R.id.userUnBlock).setVisible(false);
                if (alCustomizationSettings.isMuteOption() && !Channel.GroupType.BROADCAST.getValue().equals(channel.getType())) {
                    menu.findItem(R.id.unmuteGroup).setVisible(!channel.isDeleted() && channel.isNotificationMuted());
                    menu.findItem(R.id.muteGroup).setVisible(!channel.isDeleted() && !channel.isNotificationMuted());
                }
            }
        } else if (alCustomizationSettings.isMuteUserChatOption() && contact != null) {
            menu.findItem(R.id.userBlock).setVisible(false);
            menu.findItem(R.id.userUnBlock).setVisible(false);
            menu.findItem(R.id.unmuteGroup).setVisible(!contact.isDeleted() && contact.isNotificationMuted());
            menu.findItem(R.id.muteGroup).setVisible(!contact.isDeleted() && !contact.isNotificationMuted());

        } else if (contact != null && alCustomizationSettings.isBlockOption()) {
            if (contact.isBlocked()) {
                menu.findItem(R.id.userUnBlock).setVisible(true);
            } else {
                menu.findItem(R.id.userBlock).setVisible(true);
            }
        }

        menu.removeItem(R.id.menu_search);
        menu.removeItem(R.id.start_new);

        if (channel != null && channel.isDeleted()) {
            menu.findItem(R.id.refresh).setVisible(false);
            menu.findItem(R.id.deleteConversation).setVisible(false);
        } else {
            menu.findItem(R.id.refresh).setVisible(alCustomizationSettings.isRefreshOption());
            menu.findItem(R.id.deleteConversation).setVisible(alCustomizationSettings.isDeleteOption());
        }

        menu.removeItem(R.id.conversations);

        menu.findItem(R.id.dial).setVisible(false);
        menu.findItem(R.id.refresh).setVisible(false);
        menu.removeItem(R.id.conversations);
        menu.findItem(R.id.userBlock).setVisible(false);
        menu.findItem(R.id.userUnBlock).setVisible(false);
        menu.findItem(R.id.dial).setVisible(false);
        menu.findItem(R.id.muteGroup).setVisible(false);
        menu.findItem(R.id.unmuteGroup).setVisible(false);
        menu.findItem(R.id.deleteConversation).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.userBlock) {
            if (channel != null) {
                if (Channel.GroupType.GROUPOFTWO.getValue().equals(channel.getType())) {
                    String userId = ChannelService.getInstance(getActivity()).getGroupOfTwoReceiverUserId(channel.getKey());
                    if (!TextUtils.isEmpty(userId)) {
                        userBlockDialog(true, appContactService.getContactById(userId), true);
                    }
                }
            } else if (contact != null) {
                userBlockDialog(true, contact, false);
            }
        }
        if (id == R.id.userUnBlock) {
            if (channel != null) {
                if (Channel.GroupType.GROUPOFTWO.getValue().equals(channel.getType())) {
                    String userId = ChannelService.getInstance(getActivity()).getGroupOfTwoReceiverUserId(channel.getKey());
                    if (!TextUtils.isEmpty(userId)) {
                        userBlockDialog(false, appContactService.getContactById(userId), true);
                    }
                }
            } else if (contact != null) {
                userBlockDialog(false, contact, false);
            }
        }
        if (id == R.id.dial) {
            if (contact != null) {
                if (contact.isBlocked()) {
                    userBlockDialog(false, contact, false);
                } else {
                    ((ConversationActivity) getActivity()).processCall(contact, currentConversationId);
                }
            }
        }
        if (id == R.id.deleteConversation) {
            deleteConversationThread();
            return true;
        }

        if (id == R.id.share_conversation) {
            if (channel != null && Channel.GroupType.SUPPORT_GROUP.getValue().equals(channel.getType())) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, new KmClientService(getContext()).getConversationShareUrl() + channel.getKey());
                startActivity(Intent.createChooser(sharingIntent, ApplozicService.getContext(getContext()).getResources().getString(R.string.share_using)));
            }
        }

        if (id == R.id.video_call) {
            if (contact != null) {
                if (contact.isBlocked()) {
                    userBlockDialog(false, contact, false);
                } else {
                    ((ConversationActivity) getActivity()).processVideoCall(contact, currentConversationId);
                }
            }
        }
        if (id == R.id.muteGroup) {
            if (channel != null) {
                muteGroupChat();
            } else if (contact != null) {
                muteUserChat();
            }
        }
        if (id == R.id.unmuteGroup) {
            if (channel != null) {
                umuteGroupChat();
            } else if (contact != null) {
                unMuteUserChat();
            }
        }
        return false;
    }

    public void loadConversation(final Contact contact, final Channel channel, final Integer conversationId, final String searchString) {
        if (downloadConversation != null) {
            downloadConversation.cancel(true);
        }

        BroadcastService.currentUserId = contact != null ? contact.getContactIds() : String.valueOf(channel.getKey());
        typingStarted = false;
        onSelected = false;
        messageMetaData = null;

        if (userNotAbleToChatLayout != null) {
            if (contact != null && contact.isDeleted()) {
                userNotAbleToChatLayout.setVisibility(VISIBLE);
                individualMessageSendLayout.setVisibility(View.GONE);
            } else {
                userNotAbleToChatLayout.setVisibility(View.GONE);
                individualMessageSendLayout.setVisibility(VISIBLE);
            }
        }

        if (contact != null && this.channel != null) {
            if (getActivity() != null) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("");
            }
            if (menu != null) {
                menu.findItem(R.id.unmuteGroup).setVisible(false);
                menu.findItem(R.id.muteGroup).setVisible(false);
            }
        }
        if (replayRelativeLayout != null) {
            replayRelativeLayout.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(filePath) && attachmentLayout != null) {
            attachmentLayout.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(defaultText) && messageEditText != null) {
            messageEditText.setText(defaultText);
            defaultText = "";
        }

        extendedSendingOptionLayout.setVisibility(VISIBLE);
        setContact(contact);
        setChannel(channel);

        unregisterForContextMenu(recyclerView);
        if (ApplozicClient.getInstance(getActivity()).isNotificationStacking()) {
            NotificationManagerCompat nMgr = NotificationManagerCompat.from(ApplozicService.getContext(getContext()));
            nMgr.cancel(NotificationService.NOTIFICATION_ID);
        } else {
            if (contact != null) {
                if (!TextUtils.isEmpty(contact.getContactIds())) {
                    NotificationManager notificationManager =
                            (NotificationManager) ApplozicService.getContext(getContext()).getSystemService(Context.NOTIFICATION_SERVICE);
                    if (notificationManager != null) {
                        notificationManager.cancel(contact.getContactIds().hashCode());
                    }
                }
            }

            if (channel != null) {
                NotificationManager notificationManager =
                        (NotificationManager) ApplozicService.getContext(getContext()).getSystemService(Context.NOTIFICATION_SERVICE);
                if (notificationManager != null) {
                    notificationManager.cancel(String.valueOf(channel.getKey()).hashCode());
                }
            }
        }

        clearList();
        updateTitle();
        swipeLayout.setEnabled(true);
        loadMore = true;
        if (selfDestructMessageSpinner != null) {
            selfDestructMessageSpinner.setSelection(0);
        }

        if (contact != null) {
            recyclerDetailConversationAdapter = new DetailedConversationAdapter(getActivity(),
                    R.layout.mobicom_message_row_view, messageList, contact, messageIntentClass, emojiIconHandler);
        } else if (channel != null) {
            recyclerDetailConversationAdapter = new DetailedConversationAdapter(getActivity(),
                    R.layout.mobicom_message_row_view, messageList, channel, messageIntentClass, emojiIconHandler);
        }

        recyclerDetailConversationAdapter.setAlCustomizationSettings(alCustomizationSettings);
        recyclerDetailConversationAdapter.setContextMenuClickListener(this);
        recyclerDetailConversationAdapter.setRichMessageCallbackListener(this);
        recyclerDetailConversationAdapter.setFontManager(fontManager);
        if (getActivity() instanceof KmStoragePermissionListener) {
            recyclerDetailConversationAdapter.setStoragePermissionListener((KmStoragePermissionListener) getActivity());
        } else {
            recyclerDetailConversationAdapter.setStoragePermissionListener(new KmStoragePermissionListener() {
                @Override
                public boolean isPermissionGranted() {
                    return false;
                }

                @Override
                public void checkPermission(KmStoragePermission storagePermission) {
                }
            });
        }

        linearLayoutManager.setSmoothScrollbarEnabled(true);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                linearLayoutManager.setStackFromEnd(true);
            }
        });
        recyclerView.setAdapter(recyclerDetailConversationAdapter);
        registerForContextMenu(recyclerView);

        processMobiTexterUserCheck();


        downloadConversation = new DownloadConversation(recyclerView, true, 1, 0, 0, contact, channel, conversationId);
        downloadConversation.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        if (hideExtendedSendingOptionLayout) {
            extendedSendingOptionLayout.setVisibility(View.GONE);
        }
        emoticonsFrameLayout.setVisibility(View.GONE);

        if (contact != null) {
            Intent intent = new Intent(getActivity(), UserIntentService.class);
            intent.putExtra(UserIntentService.USER_ID, contact.getUserId());
            UserIntentService.enqueueWork(getActivity(), intent);
        }

        if (channel != null) {
            if (Channel.GroupType.GROUPOFTWO.getValue().equals(channel.getType())) {
                String userId = ChannelService.getInstance(getActivity()).getGroupOfTwoReceiverUserId(channel.getKey());
                if (!TextUtils.isEmpty(userId)) {
                    Intent intent = new Intent(getActivity(), UserIntentService.class);
                    intent.putExtra(UserIntentService.USER_ID, userId);
                    UserIntentService.enqueueWork(getActivity(), intent);
                }
            } else {
                updateChannelSubTitle();
            }
        }

        InstructionUtil.showInstruction(getActivity(), R.string.instruction_go_back_to_recent_conversation_list, MobiComKitActivityInterface.INSTRUCTION_DELAY, BroadcastService.INTENT_ACTIONS.INSTRUCTION.toString());
    }


    public void updateLastSeenStatus() {
        if (this.getActivity() == null) {
            return;
        }
        if (contact != null) {
            contact = appContactService.getContactById(contact.getContactIds());
        }

        if (contact != null) {
            processUpdateLastSeenStatus(contact);
        } else if (channel != null && Channel.GroupType.GROUPOFTWO.getValue().equals(channel.getType())) {
            String userId = ChannelService.getInstance(getActivity()).getGroupOfTwoReceiverUserId(channel.getKey());
            if (!TextUtils.isEmpty(userId)) {
                Contact withUserContact = appContactService.getContactById(userId);
                processUpdateLastSeenStatus(withUserContact);
            }

        }

    }

    protected void processUpdateLastSeenStatus(final Contact withUserContact) {
        if (withUserContact == null) {
            return;
        }

        if (this.getActivity() == null) {
            return;
        }
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (userNotAbleToChatLayout != null && individualMessageSendLayout != null) {
                    userNotAbleToChatLayout.setVisibility(withUserContact.isDeleted() ? VISIBLE : View.GONE);
                    individualMessageSendLayout.setVisibility(withUserContact.isDeleted() ? View.GONE : VISIBLE);
                    bottomlayoutTextView.setText(R.string.user_has_been_deleted_text);
                }

                if (menu != null) {
                    menu.findItem(R.id.userBlock).setVisible(alCustomizationSettings.isBlockOption() ? !withUserContact.isDeleted() && !withUserContact.isBlocked() : alCustomizationSettings.isBlockOption());
                    menu.findItem(R.id.userUnBlock).setVisible(alCustomizationSettings.isBlockOption() ? !withUserContact.isDeleted() && withUserContact.isBlocked() : alCustomizationSettings.isBlockOption());
                    menu.findItem(R.id.refresh).setVisible(alCustomizationSettings.isRefreshOption() ? !withUserContact.isDeleted() : alCustomizationSettings.isRefreshOption());
                }

                if (withUserContact.isBlocked() || withUserContact.isBlockedBy() || withUserContact.isDeleted()) {
                    if (getActivity() != null) {
                        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("");
                    }
                    return;
                }
                if (withUserContact != null) {
                    if (withUserContact.isConnected()) {
                        typingStarted = false;
                        if (getActivity() != null) {
                            ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(ApplozicService.getContext(getContext()).getString(R.string.user_online));
                        }
                    } else if (withUserContact.getLastSeenAt() != 0) {
                        if (getActivity() != null) {
                            ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(ApplozicService.getContext(getContext()).getString(R.string.subtitle_last_seen_at_time) + " " + DateUtils.getDateAndTimeForLastSeen(ApplozicService.getContext(getContext()), withUserContact.getLastSeenAt(), R.string.JUST_NOW, R.plurals.MINUTES_AGO, R.plurals.HOURS_AGO, R.string.YESTERDAY));
                        }
                    } else {
                        if (getActivity() != null) {
                            ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("");
                        }
                    }
                }
            }

        });
    }

    public void updateChannelSubTitle() {
        channelUserMapperList = ChannelService.getInstance(getActivity()).getListOfUsersFromChannelUserMapper(channel.getKey());
        if (channelUserMapperList != null && channelUserMapperList.size() > 0) {
            if (Channel.GroupType.GROUPOFTWO.getValue().equals(channel.getType())) {
                String userId = ChannelService.getInstance(getActivity()).getGroupOfTwoReceiverUserId(channel.getKey());
                if (!TextUtils.isEmpty(userId)) {
                    Contact withUserContact = appContactService.getContactById(userId);
                    if (withUserContact != null && getActivity() != null) {
                        if (withUserContact.isBlocked()) {
                            ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("");
                        } else {
                            if (withUserContact.isConnected()) {
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(ApplozicService.getContext(getContext()).getString(R.string.user_online));
                            } else if (withUserContact.getLastSeenAt() != 0) {
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(ApplozicService.getContext(getContext()).getString(R.string.subtitle_last_seen_at_time) + " " + DateUtils.getDateAndTimeForLastSeen(ApplozicService.getContext(getContext()), withUserContact.getLastSeenAt(), R.string.JUST_NOW, R.plurals.MINUTES_AGO, R.plurals.HOURS_AGO, R.string.YESTERDAY));
                            } else {
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("");
                            }
                        }
                    }
                }

            } else {
                if (!alCustomizationSettings.isGroupSubtitleHidden()) {
                    StringBuffer stringBuffer = new StringBuffer();
                    Contact contactDisplayName;
                    String youString = null;
                    int i = 0;
                    for (ChannelUserMapper channelUserMapper : channelUserMapperList) {
                        i++;
                        if (i > 20)
                            break;
                        contactDisplayName = appContactService.getContactById(channelUserMapper.getUserKey());
                        if (!TextUtils.isEmpty(channelUserMapper.getUserKey())) {
                            if (MobiComUserPreference.getInstance(getActivity()).getUserId().equals(channelUserMapper.getUserKey())) {
                                youString = ApplozicService.getContext(getContext()).getString(R.string.you_string);
                            } else {
                                stringBuffer.append(contactDisplayName.getDisplayName()).append(", ");
                            }
                        }
                    }
                    if (!TextUtils.isEmpty(stringBuffer)) {
                        if (channelUserMapperList.size() <= 20) {
                            if (!TextUtils.isEmpty(youString)) {
                                stringBuffer.append(youString).append(", ");
                            }
                            int lastIndex = stringBuffer.lastIndexOf(", ");
                            String userIds = stringBuffer.replace(lastIndex, lastIndex + 1, "").toString();
                            ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(userIds);
                        } else {
                            ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(stringBuffer.toString());
                        }
                    } else {
                        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(youString);
                    }
                } else {
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("");
                }
            }

        }
    }

    public boolean isBroadcastedToChannel(Integer channelKey) {
        return getChannel() != null && getChannel().getKey().equals(channelKey);
    }

    public boolean getCurrentChannelKey(Integer channelKey) {
        return channel != null && channel.getKey().equals(channelKey);
    }

    public Channel getChannel() {
        return channel;
    }

    protected void setChannel(Channel channel) {
        this.channel = channel;
        if (channel != null && !ChannelService.getInstance(getContext()).isUserAlreadyPresentInChannel(channel.getKey(), MobiComUserPreference.getInstance(getContext()).getUserId())
                && messageTemplate != null && messageTemplate.isEnabled() && templateAdapter != null) {
            templateAdapter.setMessageList(new HashMap<String, String>());
            templateAdapter.notifyDataSetChanged();
        }
    }

    public boolean isMsgForConversation(Message message) {

        if (BroadcastService.isContextBasedChatEnabled() && message.getConversationId() != null) {
            return isMessageForCurrentConversation(message) && compareConversationId(message);
        }
        return isMessageForCurrentConversation(message);
    }

    public boolean isMessageForCurrentConversation(Message message) {
        return (message.getGroupId() != null && channel != null && message.getGroupId().equals(channel.getKey())) ||
                (!TextUtils.isEmpty(message.getContactIds()) && contact != null && message.getContactIds().equals(contact.getContactIds())) && message.getGroupId() == null;
    }

    public boolean compareConversationId(Message message) {
        return message.getConversationId() != null && currentConversationId != null && message.getConversationId().equals(currentConversationId);
    }

    public void updateUploadFailedStatus(final Message message) {
        if (getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int i = messageList.indexOf(message);
                if (i != -1) {
                    messageList.get(i).setCanceled(true);
                    recyclerDetailConversationAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    public void downloadFailed(final Message message) {
        if (getActivity() == null) {
            return;
        }
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int index = messageList.indexOf(message);
                if (index != -1) {
                    View view = recyclerView.getChildAt(index -
                            linearLayoutManager.findFirstVisibleItemPosition());

                    if (view != null) {
                        final LinearLayout attachmentDownloadLayout = (LinearLayout) view.findViewById(R.id.attachment_download_layout);
                        attachmentDownloadLayout.setVisibility(VISIBLE);
                    }

                }
            }

        });
    }

    abstract public void attachLocation(Location mCurrentLocation);

    public void updateDeliveryStatusForAllMessages(final boolean markRead) {
        if (getActivity() == null) {
            return;
        }

        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Drawable statusIcon = deliveredIcon;
                    if (markRead) {
                        statusIcon = readIcon;
                    }
                    for (int index = 0; index < messageList.size(); index++) {
                        Message message = messageList.get(index);
                        if ((message.getStatus() == Message.Status.DELIVERED_AND_READ.getValue()) || message.isTempDateType() || message.isCustom() || !message.isTypeOutbox() || message.isChannelCustomMessage()) {
                            continue;
                        }
                        if (messageList.get(index) != null) {
                            messageList.get(index).setDelivered(true);
                        }
                        message.setDelivered(true);
                        if (markRead) {
                            if (messageList.get(index) != null) {
                                messageList.get(index).setStatus(Message.Status.DELIVERED_AND_READ.getValue());
                            }
                            message.setStatus(Message.Status.DELIVERED_AND_READ.getValue());
                        }
                        View view = recyclerView.getChildAt(index -
                                linearLayoutManager.findFirstVisibleItemPosition());
                        if (view != null && !message.isCustom() && !message.isChannelCustomMessage()) {
                            TextView statusImage = view.findViewById(R.id.statusImage);
                            statusImage.setCompoundDrawablesWithIntrinsicBounds(null, null, statusIcon, null);
                        }
                    }
                } catch (Exception ex) {
                    Utils.printLog(getContext(), TAG, "Exception while updating delivery status in UI.");
                }
            }
        });
    }

    public void updateDeliveryStatus(final Message message) {
        if (getActivity() != null) {
            this.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int index = messageList.indexOf(message);
                        if (index != -1) {
                            if (messageList.get(index).getStatus() == Message.Status.DELIVERED_AND_READ.getValue()
                                    || messageList.get(index).isTempDateType()
                                    || messageList.get(index).isCustom()
                                    || messageList.get(index).isChannelCustomMessage()) {
                                return;
                            }
                            messageList.get(index).setDelivered(true);
                            messageList.get(index).setStatus(message.getStatus());
                            View view = recyclerView.getChildAt(index -
                                    linearLayoutManager.findFirstVisibleItemPosition());
                            if (view != null && !messageList.get(index).isCustom()) {
                                Drawable statusIcon = deliveredIcon;
                                if (message.getStatus() == Message.Status.DELIVERED_AND_READ.getValue()) {
                                    statusIcon = readIcon;
                                    messageList.get(index).setStatus(Message.Status.DELIVERED_AND_READ.getValue());
                                }
                                TextView statusImage = view.findViewById(R.id.statusImage);
                                statusImage.setCompoundDrawablesWithIntrinsicBounds(null, null, statusIcon, null);
                            }
                        } else if (!message.isVideoNotificationMessage() && !message.isHidden()) {
                            messageList.add(message);
                            linearLayoutManager.scrollToPositionWithOffset(messageList.size() - 1, 0);
                            emptyTextView.setVisibility(View.GONE);
                            recyclerDetailConversationAdapter.notifyDataSetChanged();
                        }
                    } catch (Exception ex) {
                        Utils.printLog(getContext(), TAG, "Exception while updating delivery status in UI.");
                    }
                }
            });
        }
    }

    public void loadFile(Uri uri, File file) {
        if (uri == null || file == null) {
            Toast.makeText(ApplozicService.getContext(getContext()), ApplozicService.getContext(getContext()).getString(R.string.file_not_selected), Toast.LENGTH_LONG).show();
            return;
        }
        handleSendAndRecordButtonView(true);
        filePath = Uri.parse(file.getAbsolutePath()).toString();
        if (TextUtils.isEmpty(filePath)) {
            Utils.printLog(getContext(), TAG, "Error while fetching filePath");
            attachmentLayout.setVisibility(View.GONE);
            Toast.makeText(ApplozicService.getContext(getContext()), ApplozicService.getContext(getContext()).getString(R.string.info_file_attachment_error), Toast.LENGTH_LONG).show();
            return;
        }
        String mimeType = ApplozicService.getContext(getContext()).getContentResolver().getType(uri);
        Cursor returnCursor =
                ApplozicService.getContext(getContext()).getContentResolver().query(uri, null, null, null, null);
        if (returnCursor != null) {
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();
            long fileSize = returnCursor.getLong(sizeIndex);
            long maxFileSize = alCustomizationSettings.getMaxAttachmentSizeAllowed() * 1024 * 1024;
            if (fileSize > maxFileSize) {
                Toast.makeText(ApplozicService.getContext(getContext()), ApplozicService.getContext(getContext()).getString(R.string.info_attachment_max_allowed_file_size), Toast.LENGTH_LONG).show();
                return;
            }
            attachedFile.setText(returnCursor.getString(returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)));
            returnCursor.close();
        }
        attachmentLayout.setVisibility(VISIBLE);
        if (mimeType != null && (mimeType.startsWith("image") || mimeType.startsWith("video"))) {
            attachedFile.setVisibility(View.GONE);
            int reqWidth = mediaContainer.getWidth();
            int reqHeight = mediaContainer.getHeight();
            if (reqWidth == 0 || reqHeight == 0) {
                DisplayMetrics displaymetrics = new DisplayMetrics();
                if (getActivity() != null) {
                    getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                }
                reqHeight = displaymetrics.heightPixels;
                reqWidth = displaymetrics.widthPixels;
            }
            previewThumbnail = FileUtils.getPreview(filePath, reqWidth, reqHeight, alCustomizationSettings.isImageCompression(), mimeType);
            mediaContainer.setImageBitmap(previewThumbnail);
        } else {
            attachedFile.setVisibility(VISIBLE);
            mediaContainer.setImageBitmap(null);
        }
    }

    public synchronized boolean updateMessageList(Message message, boolean update) {
        boolean toAdd = !messageList.contains(message);
        loadMore = true;
        if (update) {
            messageList.remove(message);
            messageList.add(message);
        } else if (toAdd) {
            Message firstDateMessage = new Message();
            firstDateMessage.setTempDateType(Short.valueOf("100"));
            firstDateMessage.setCreatedAtTime(message.getCreatedAtTime());
            if (!messageList.contains(firstDateMessage)) {
                messageList.add(firstDateMessage);
            }

            messageList.add(message);
        }
        return toAdd;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        support = new Support(activity);
        try {
            messageCommunicator = (MessageCommunicator) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement interfaceDataCommunicator");
        }
    }

    protected AlertDialog showInviteDialog(int titleId, int messageId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(ApplozicService.getContext(getContext()).getString(messageId).replace("[name]", getNameForInviteDialog()))
                .setTitle(titleId);
        builder.setPositiveButton(R.string.invite, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent share = new Intent(Intent.ACTION_SEND);
                startActivity(Intent.createChooser(share, "Share Via"));
                sendType.setSelection(0);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                sendType.setSelection(0);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }


    public String getNameForInviteDialog() {
        if (contact != null) {
            return contact.getDisplayName();
        } else if (channel != null) {
            if (Channel.GroupType.GROUPOFTWO.getValue().equals(channel.getType())) {
                String userId = ChannelService.getInstance(getActivity()).getGroupOfTwoReceiverUserId(channel.getKey());
                if (!TextUtils.isEmpty(userId)) {
                    Contact withUserContact = appContactService.getContactById(userId);
                    return withUserContact.getDisplayName();
                }
            } else {
                return ChannelUtils.getChannelTitleName(channel, MobiComUserPreference.getInstance(getActivity()).getUserId());
            }
        }
        return "";
    }

    public void onClickOnMessageReply(Message message) {
        if (message != null) {
            if (recyclerView != null) {
                int height = recyclerView.getHeight();
                int itemHeight = recyclerView.getChildAt(0).getHeight();
                int index = messageList.indexOf(message);
                if (index != -1) {
                    recyclerView.requestFocusFromTouch();
                    linearLayoutManager.setStackFromEnd(true);
                    linearLayoutManager.scrollToPositionWithOffset(index, height / 2 - itemHeight / 2);
                    recyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (recyclerView != null) {
                                try {
                                    if (recyclerView.isFocused()) {
                                        recyclerView.clearFocus();
                                    }
                                } catch (Exception e) {
                                }
                            }
                        }
                    }, 800);
                }
            }

        }
    }

    public void forwardMessage(Message messageToForward, Contact contact, Channel channel) {
        this.contact = contact;
        this.channel = channel;
        if (messageToForward.isAttachmentDownloaded()) {
            filePath = messageToForward.getFilePaths().get(0);
        }
        this.messageToForward = messageToForward;
        loadConversation(contact, channel, currentConversationId, null);

    }

    private void sendForwardMessage(Message messageToForward) {
        MobiComUserPreference userPreferences = MobiComUserPreference.getInstance(getActivity());

        if (channel != null) {
            if (!ChannelService.getInstance(getContext()).processIsUserPresentInChannel(channel.getKey())) {
                return;
            }
            messageToForward.setGroupId(channel.getKey());
            messageToForward.setClientGroupId(null);
            messageToForward.setContactIds(null);
            messageToForward.setTo(null);
        } else {
            if (contact.isBlocked()) {
                return;
            }
            messageToForward.setGroupId(null);
            messageToForward.setClientGroupId(null);
            messageToForward.setTo(contact.getContactIds());
            messageToForward.setContactIds(contact.getContactIds());
        }

        messageToForward.setKeyString(null);
        messageToForward.setMessageId(null);
        messageToForward.setDelivered(false);
        messageToForward.setRead(Boolean.TRUE);
        messageToForward.setStoreOnDevice(Boolean.TRUE);
        messageToForward.setCreatedAtTime(System.currentTimeMillis() + userPreferences.getDeviceTimeOffset());
        if (currentConversationId != null && currentConversationId != 0) {
            messageToForward.setConversationId(currentConversationId);
        }
        Map<String, String> metaDataMapForward = messageToForward.getMetadata();
        if (metaDataMapForward != null && !metaDataMapForward.isEmpty() && metaDataMapForward.get(Message.MetaDataType.AL_REPLY.getValue()) != null) {
            messageToForward.setMetadata(null);
        }
        messageToForward.setSendToDevice(Boolean.FALSE);
        messageToForward.setType(sendType.getSelectedItemId() == 1 ? Message.MessageType.MT_OUTBOX.getValue() : Message.MessageType.OUTBOX.getValue());
        messageToForward.setTimeToLive(getTimeToLive());
        messageToForward.setSentToServer(false);
        messageToForward.setStatus(Message.Status.READ.getValue());

        if (!TextUtils.isEmpty(filePath)) {
            List<String> filePaths = new ArrayList<String>();
            filePaths.add(filePath);
            messageToForward.setFilePaths(filePaths);
        }
        conversationService.sendMessage(messageToForward, messageIntentClass);
        if (selfDestructMessageSpinner != null) {
            selfDestructMessageSpinner.setSelection(0);
        }
        attachmentLayout.setVisibility(View.GONE);
        filePath = null;
    }


    public void sendMessage(String message, Map<String, String> messageMetaData, FileMeta fileMetas, String fileMetaKeyStrings, short messageContentType) {
        MobiComUserPreference userPreferences = MobiComUserPreference.getInstance(getActivity());
        Message messageToSend = new Message();

        if (channel != null) {
            messageToSend.setGroupId(channel.getKey());
            if (!TextUtils.isEmpty(channel.getClientGroupId())) {
                messageToSend.setClientGroupId(channel.getClientGroupId());
            }
        } else {
            messageToSend.setTo(contact.getContactIds());
            messageToSend.setContactIds(contact.getContactIds());
        }
        messageToSend.setRead(Boolean.TRUE);
        messageToSend.setStoreOnDevice(Boolean.TRUE);
        if (messageToSend.getCreatedAtTime() == null) {
            messageToSend.setCreatedAtTime(System.currentTimeMillis() + userPreferences.getDeviceTimeOffset());
        }
        if (currentConversationId != null && currentConversationId != 0) {
            messageToSend.setConversationId(currentConversationId);
        }
        messageToSend.setSendToDevice(Boolean.FALSE);
        messageToSend.setType(sendType.getSelectedItemId() == 1 ? Message.MessageType.MT_OUTBOX.getValue() : Message.MessageType.OUTBOX.getValue());
        messageToSend.setTimeToLive(getTimeToLive());
        messageToSend.setMessage(message);
        messageToSend.setDeviceKeyString(userPreferences.getDeviceKeyString());
        messageToSend.setScheduledAt(scheduledTimeHolder.getTimestamp());
        messageToSend.setSource(Message.Source.MT_MOBILE_APP.getValue());
        if (!TextUtils.isEmpty(filePath)) {
            List<String> filePaths = new ArrayList<String>();
            filePaths.add(filePath);
            messageToSend.setFilePaths(filePaths);
            if (messageContentType == Message.ContentType.AUDIO_MSG.getValue() || messageContentType == Message.ContentType.CONTACT_MSG.getValue() || messageContentType == Message.ContentType.VIDEO_MSG.getValue()) {
                messageToSend.setContentType(messageContentType);
            } else {
                messageToSend.setContentType(Message.ContentType.ATTACHMENT.getValue());
            }
        } else {
            messageToSend.setContentType(messageContentType);
        }

        if (messageContentType == Message.ContentType.LOCATION.getValue()) {
            messageToSend.setContentType(Message.ContentType.LOCATION.getValue());
            messageToSend.setFilePaths(null);
        }
        if (messageMetaData == null) {
            messageMetaData = new HashMap<>();
        }
        messageToSend.setFileMetaKeyStrings(fileMetaKeyStrings);
        messageToSend.setFileMetas(fileMetas);
        if (!TextUtils.isEmpty(ApplozicClient.getInstance(getActivity()).getMessageMetaData())) {
            Type mapType = new TypeToken<Map<String, String>>() {
            }.getType();
            Map<String, String> messageMetaDataMap = null;
            try {
                messageMetaDataMap = new Gson().fromJson(ApplozicClient.getInstance(getActivity()).getMessageMetaData(), mapType);
                if (messageMetaDataMap != null && !messageMetaDataMap.isEmpty()) {
                    messageMetaData.putAll(messageMetaDataMap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (this.messageMetaData != null && !this.messageMetaData.isEmpty()) {
            messageMetaData.putAll(this.messageMetaData);
        }

        messageToSend.setMetadata(messageMetaData);


        conversationService.sendMessage(messageToSend, messageIntentClass);
        if (replayRelativeLayout != null) {
            replayRelativeLayout.setVisibility(View.GONE);
        }
        if (selfDestructMessageSpinner != null) {
            selfDestructMessageSpinner.setSelection(0);
        }
        attachmentLayout.setVisibility(View.GONE);
        if (channel != null && channel.getType() != null && Channel.GroupType.BROADCAST_ONE_BY_ONE.getValue().equals(channel.getType())) {
            sendBroadcastMessage(message, filePath);
        }
        this.messageMetaData = null;
        filePath = null;
    }

    public void sendProductMessage(final String messageToSend, final FileMeta fileMeta, final Contact contact, final short messageContentType) {
        final Message message = new Message();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String topicId;
                MobiComConversationService conversationService = new MobiComConversationService(getActivity());
                MobiComUserPreference userPreferences = MobiComUserPreference.getInstance(getActivity());
                topicId = new MessageClientService(getActivity()).getTopicId(currentConversationId);
                if (getChannel() != null) {
                    message.setGroupId(channelKey);
                } else {
                    message.setContactIds(contact.getUserId());
                    message.setTo(contact.getUserId());
                }
                message.setMessage(messageToSend);
                message.setRead(Boolean.TRUE);
                message.setStoreOnDevice(Boolean.TRUE);
                message.setSendToDevice(Boolean.FALSE);
                message.setContentType(messageContentType);
                message.setType(Message.MessageType.MT_OUTBOX.getValue());
                message.setDeviceKeyString(userPreferences.getDeviceKeyString());
                message.setSource(Message.Source.MT_MOBILE_APP.getValue());
                message.setTopicId(messageToSend);
                message.setCreatedAtTime(System.currentTimeMillis() + userPreferences.getDeviceTimeOffset());
                message.setTopicId(topicId);
                message.setConversationId(currentConversationId);
                message.setFileMetas(fileMeta);
                conversationService.sendMessage(message, MessageIntentService.class);
            }
        }).start();

    }

    public void sendBroadcastMessage(String message, String path) {
        MobiComUserPreference userPreferences = MobiComUserPreference.getInstance(getActivity());
        if (channelUserMapperList != null && channelUserMapperList.size() > 0) {
            for (ChannelUserMapper channelUserMapper : channelUserMapperList) {
                if (!userPreferences.getUserId().equals(channelUserMapper.getUserKey())) {
                    Message messageToSend = new Message();
                    messageToSend.setTo(channelUserMapper.getUserKey());
                    messageToSend.setContactIds(channelUserMapper.getUserKey());
                    messageToSend.setRead(Boolean.TRUE);
                    messageToSend.setStoreOnDevice(Boolean.TRUE);
                    if (messageToSend.getCreatedAtTime() == null) {
                        messageToSend.setCreatedAtTime(System.currentTimeMillis() + userPreferences.getDeviceTimeOffset());
                    }
                    if (currentConversationId != null && currentConversationId != 0) {
                        messageToSend.setConversationId(currentConversationId);
                    }
                    messageToSend.setSendToDevice(Boolean.FALSE);
                    messageToSend.setType(sendType.getSelectedItemId() == 1 ? Message.MessageType.MT_OUTBOX.getValue() : Message.MessageType.OUTBOX.getValue());
                    messageToSend.setTimeToLive(getTimeToLive());
                    messageToSend.setMessage(message);
                    messageToSend.setDeviceKeyString(userPreferences.getDeviceKeyString());
                    messageToSend.setScheduledAt(scheduledTimeHolder.getTimestamp());
                    messageToSend.setSource(Message.Source.MT_MOBILE_APP.getValue());
                    if (!TextUtils.isEmpty(path)) {
                        List<String> filePaths = new ArrayList<String>();
                        filePaths.add(path);
                        messageToSend.setFilePaths(filePaths);
                    }
                    conversationService.sendMessage(messageToSend, MessageIntentService.class);

                    if (selfDestructMessageSpinner != null) {
                        selfDestructMessageSpinner.setSelection(0);
                    }
                    attachmentLayout.setVisibility(View.GONE);
                }
            }
        }
    }

    private Integer getTimeToLive() {
        if (selfDestructMessageSpinner == null || selfDestructMessageSpinner.getSelectedItemPosition() <= 1) {
            return null;
        }
        return Integer.parseInt(selfDestructMessageSpinner.getSelectedItem().toString().replace("mins", "").replace("min", "").trim());
    }

    public void sendMessage(String message) {
        sendMessage(message, null, null, null, Message.ContentType.DEFAULT.getValue());
    }

    public void sendMessage(short messageContentType, String filePath) {
        this.filePath = filePath;
        sendMessage("", messageContentType);
    }

    public void sendMessage(String message, short messageContentType, String filePath) {
        this.filePath = filePath;
        sendMessage(message, null, null, null, messageContentType);
    }

    public void sendMessage(String message, short messageContentType) {
        sendMessage(message, null, null, null, messageContentType);
    }

    public void sendMessage(String message, Map<String, String> messageMetaData, short messageContentType) {
        sendMessage(message, messageMetaData, null, null, messageContentType);
    }

    public void updateMessageKeyString(final Message message) {
        if (getActivity() == null) {
            return;
        }

        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int index = messageList.indexOf(message);
                if (index != -1) {
                    Message messageListItem = messageList.get(index);
                    messageListItem.setKeyString(message.getKeyString());
                    messageListItem.setSentToServer(true);
                    messageListItem.setCreatedAtTime(message.getSentMessageTimeAtServer());
                    messageListItem.setFileMetaKeyStrings(message.getFileMetaKeyStrings());
                    messageListItem.setFileMetas(message.getFileMetas());
                    if (messageList.get(index) != null) {
                        messageList.get(index).setKeyString(message.getKeyString());
                        messageList.get(index).setSentToServer(true);
                        messageList.get(index).setCreatedAtTime(message.getSentMessageTimeAtServer());
                        messageList.get(index).setFileMetaKeyStrings(message.getFileMetaKeyStrings());
                        messageList.get(index).setFileMetas(message.getFileMetas());
                    }
                    View view = recyclerView.getChildAt(index - linearLayoutManager.findFirstVisibleItemPosition());
                    if (view != null) {
                        ProgressBar mediaUploadProgressBarIndividualMessage = (ProgressBar) view.findViewById(R.id.media_upload_progress_bar);
                        RelativeLayout downloadInProgressLayout = (RelativeLayout) view.findViewById(R.id.applozic_doc_download_progress_rl);
                        if (mediaUploadProgressBarIndividualMessage != null) {
                            mediaUploadProgressBarIndividualMessage.setVisibility(View.GONE);
                        }
                        if (downloadInProgressLayout != null) {
                            downloadInProgressLayout.setVisibility(View.GONE);
                        }
                        if (message.getFileMetas() != null && !"image".contains(message.getFileMetas().getContentType()) && !"video".contains(message.getFileMetas().getContentType())) {
                            RelativeLayout applozicDocRelativeLayout = (RelativeLayout) view.findViewById(R.id.applozic_doc_downloaded);
                            ImageView imageViewDoc = (ImageView) applozicDocRelativeLayout.findViewById(R.id.doc_icon);
                            if (message.getFileMetas() != null) {
                                if (message.getFileMetas().getContentType().contains("audio")) {
                                    imageViewDoc.setImageResource(R.drawable.ic_play_circle_outline);
                                } else {
                                    imageViewDoc.setImageResource(R.drawable.ic_documentreceive);
                                }
                                applozicDocRelativeLayout.setVisibility(VISIBLE);
                            } else if (message.getFilePaths() != null) {
                                String filePath = message.getFilePaths().get(0);
                                final String mimeType = FileUtils.getMimeType(filePath);
                                if (mimeType.contains("audio")) {
                                    imageViewDoc.setImageResource(R.drawable.ic_play_circle_outline);
                                } else {
                                    imageViewDoc.setImageResource(R.drawable.ic_documentreceive);
                                }
                                applozicDocRelativeLayout.setVisibility(VISIBLE);
                            }
                        }
                        TextView createdAtTime = (TextView) view.findViewById(R.id.createdAtTime);
                        TextView statusTextView = view.findViewById(R.id.statusImage);

                        if (statusTextView != null && messageListItem.getKeyString() != null && messageListItem.isTypeOutbox() && !messageListItem.isCall() && !messageListItem.getDelivered() && !messageListItem.isCustom() && !messageListItem.isChannelCustomMessage() && messageListItem.getScheduledAt() == null) {
                            statusTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, support.isSupportNumber(getCurrentUserId()) ? deliveredIcon : sentIcon, null);
                        }
                    }
                }
            }
        });
    }

    public void updateDownloadStatus(final Message message) {
        if (getActivity() == null) {
            return;
        }

        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    int index = messageList.indexOf(message);
                    if (index != -1) {
                        Message smListItem = messageList.get(index);
                        smListItem.setKeyString(message.getKeyString());
                        smListItem.setFileMetaKeyStrings(message.getFileMetaKeyStrings());
                        if (messageList.get(index) != null) {
                            messageList.get(index).setKeyString(message.getKeyString());
                            messageList.get(index).setFileMetaKeyStrings(message.getFileMetaKeyStrings());
                        }
                        View view = recyclerView.getChildAt(index - linearLayoutManager.findFirstVisibleItemPosition());
                        if (view != null) {
                            final RelativeLayout attachmentDownloadProgressLayout = (RelativeLayout) view.findViewById(R.id.attachment_download_progress_layout);
                            final AttachmentView attachmentView = (AttachmentView) view.findViewById(R.id.main_attachment_view);
                            final ImageView preview = (ImageView) view.findViewById(R.id.preview);
                            TextView audioDurationTextView = (TextView) view.findViewById(R.id.audio_duration_textView);
                            final ImageView videoIcon = (ImageView) view.findViewById(R.id.video_icon);
                            String audioDuration;
                            if (message.getFileMetas() != null && message.getFileMetas().getContentType().contains("image")) {
                                attachmentView.setVisibility(VISIBLE);
                                preview.setVisibility(View.GONE);
                                attachmentView.setMessage(smListItem);
                                attachmentDownloadProgressLayout.setVisibility(View.GONE);
                            } else if (message.getFileMetas() != null && message.getFileMetas().getContentType().contains("video")) {
                                FileClientService fileClientService = new FileClientService(getContext());
                                attachedFile.setVisibility(View.GONE);
                                preview.setVisibility(VISIBLE);
                                videoIcon.setVisibility(VISIBLE);
                                preview.setImageBitmap(fileClientService.createAndSaveVideoThumbnail(message.getFilePaths().get(0)));
                            } else if (message.getFileMetas() != null) {
                                //Hide Attachment View...
                                RelativeLayout applozicDocRelativeLayout = (RelativeLayout) view.findViewById(R.id.applozic_doc_downloaded);
                                ImageView imageViewDoc = (ImageView) applozicDocRelativeLayout.findViewById(R.id.doc_icon);
                                if (message.getFileMetas() != null && message.getFilePaths() == null) {
                                    if (message.getFileMetas().getContentType().contains("audio")) {
                                        imageViewDoc.setImageResource(R.drawable.ic_play_circle_outline);
                                    } else {
                                        imageViewDoc.setImageResource(R.drawable.ic_documentreceive);
                                    }
                                    applozicDocRelativeLayout.setVisibility(VISIBLE);
                                } else if (message.getFilePaths() != null) {
                                    String filePath = message.getFilePaths().get(0);
                                    final String mimeType = FileUtils.getMimeType(filePath);
                                    if (mimeType.contains("audio")) {
                                        if (message.isAttachmentDownloaded()) {
                                            audioDuration = ApplozicAudioManager.getInstance(getContext()).refreshAudioDuration(filePath);
                                            audioDurationTextView.setVisibility(View.VISIBLE);
                                            audioDurationTextView.setText(audioDuration);
                                        } else {
                                            audioDurationTextView.setVisibility(View.VISIBLE);
                                            audioDurationTextView.setText("00:00");
                                        }
                                        imageViewDoc.setImageResource(R.drawable.ic_play_circle_outline);
                                    } else {
                                        imageViewDoc.setImageResource(R.drawable.ic_documentreceive);
                                    }
                                    applozicDocRelativeLayout.setVisibility(VISIBLE);
                                }
                                view.findViewById(R.id.applozic_doc_download_progress_rl).setVisibility(View.GONE);
                            }
                        }

                    }
                } catch (Exception ex) {
                    Utils.printLog(getContext(), TAG, "Exception while updating download status: " + ex.getMessage());
                }
            }
        });
    }

    public void setDefaultText(String defaultText) {
        this.defaultText = defaultText;
    }

    public void setConversationId(Integer conversationId) {
        this.currentConversationId = conversationId;
    }

    public void updateUserTypingStatus(final String typingUserId, final String isTypingStatus) {
        updateTypingStatus(typingUserId, "1".equals(isTypingStatus));
    }

    public void updateTypingStatus(final String typingUserId, final boolean start) {
        if (contact != null) {
            if (contact.isBlocked() || contact.isBlockedBy()) {
                return;
            }
        }

        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (start) {
                        CountDownTimer timer = getCountDownTimer(typingUserId);
                        if (timer != null) {
                            timer.start();
                        }

                        if (channel != null) {
                            if (!MobiComUserPreference.getInstance(getActivity()).getUserId().equals(typingUserId)) {
                                Contact displayNameContact = appContactService.getContactById(typingUserId);
                                if (displayNameContact.isBlocked() || displayNameContact.isBlockedBy()) {
                                    return;
                                }
                                if (Channel.GroupType.GROUPOFTWO.getValue().equals(channel.getType())) {
                                    ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(ApplozicService.getContext(getContext()).getString(R.string.is_typing));
                                } else if (Channel.GroupType.SUPPORT_GROUP.getValue().equals(channel.getType())) {
                                    if (toolbarSubtitleText != null && getContext() != null) {
                                        toolbarSubtitleText.setText(ApplozicService.getContext(getContext()).getString(R.string.typing));
                                        toolbarSubtitleText.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                                    }
                                } else {
                                    ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(displayNameContact.getDisplayName() + " " + ApplozicService.getContext(getContext()).getString(R.string.is_typing));
                                }
                            }
                        } else {
                            ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(ApplozicService.getContext(getContext()).getString(R.string.is_typing));
                        }
                    } else {
                        if (typingTimerMap != null) {
                            CountDownTimer timer = typingTimerMap.get(typingUserId);
                            if (timer != null) {
                                typingTimerMap.remove(typingUserId);
                                timer.cancel();
                            }
                        }

                        if (channel != null) {
                            if (!MobiComUserPreference.getInstance(getActivity()).getUserId().equals(typingUserId)) {
                                Contact displayNameContact = appContactService.getContactById(typingUserId);
                                if (displayNameContact.isBlocked() || displayNameContact.isBlockedBy()) {
                                    return;
                                }
                                if (Channel.GroupType.SUPPORT_GROUP.getValue().equals(channel.getType())) {
                                    if (toolbarSubtitleText != null) {
                                        toolbarSubtitleText.setVisibility(View.GONE);
                                    }
                                    if (conversationAssignee != null) {
                                        switchContactStatus(conversationAssignee);
                                    } else {
                                        processSupportGroupDetails(channel);
                                    }
                                } else {
                                    updateChannelSubTitle();
                                }
                            }
                        } else {
                            updateLastSeenStatus();
                        }
                    }
                }
            });
        }
    }

//    public void onEmojiconClicked(Emojicon emojicon) {
//        //TODO: Move OntextChangeListiner to EmojiEditableTExt
//        int currentPos = messageEditText.getSelectionStart();
//        messageEditText.setTextKeepState(messageEditText.getText().
//                insert(currentPos, emojicon.getEmoji()));
//    }


    @Override
    public LayoutInflater getLayoutInflater(Bundle savedInstanceState) {
        return super.getLayoutInflater(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
    }

    //TODO: Please add onclick events here...  anonymous class are
    // TODO :hard to read and suggested if we have very few event view
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.emoji_btn) {
            if (emoticonsFrameLayout.getVisibility() == VISIBLE) {
                emoticonsFrameLayout.setVisibility(View.GONE);
                Utils.toggleSoftKeyBoard(getActivity(), false);
            } else {
                Utils.toggleSoftKeyBoard(getActivity(), true);
                emoticonsFrameLayout.setVisibility(VISIBLE);
                multimediaPopupGrid.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isRecording) {
            onLessThanSecond();
            if (recordButton != null) {
                recordButton.stopScale();
            }
            if (recordView != null) {
                recordView.hideViews(true);
            }
            onAnimationEnd();
        }
        if (alCustomizationSettings != null && alCustomizationSettings.isAgentApp()) {
            ((ConversationActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            ((ConversationActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(false);
        }

        BroadcastService.currentUserId = null;
        BroadcastService.currentConversationId = null;
        if (typingStarted) {
            Intent intent = new Intent(getActivity(), ApplozicMqttIntentService.class);
            intent.putExtra(ApplozicMqttIntentService.CHANNEL, channel);
            intent.putExtra(ApplozicMqttIntentService.CONTACT, contact);
            intent.putExtra(ApplozicMqttIntentService.TYPING, false);
            ApplozicMqttIntentService.enqueueWork(getActivity(), intent);
            typingStarted = false;
        }
        Intent intent = new Intent(getActivity(), ApplozicMqttIntentService.class);
        intent.putExtra(ApplozicMqttIntentService.CHANNEL, channel);
        intent.putExtra(ApplozicMqttIntentService.UN_SUBSCRIBE_TO_TYPING, true);
        ApplozicMqttIntentService.enqueueWork(getActivity(), intent);
        if (recyclerDetailConversationAdapter != null) {
            recyclerDetailConversationAdapter.contactImageLoader.setPauseWork(false);
        }

        if (typingTimerMap != null) {
            typingTimerMap.clear();
        }
    }

    public void updateTitle() {
        StringBuffer stringBufferTitle = new StringBuffer();
        if (contact != null) {
            stringBufferTitle.append(contact.getDisplayName());
        } else if (channel != null) {
            if (Channel.GroupType.GROUPOFTWO.getValue().equals(channel.getType())) {
                String userId = ChannelService.getInstance(getActivity()).getGroupOfTwoReceiverUserId(channel.getKey());
                if (!TextUtils.isEmpty(userId)) {
                    Contact withUserContact = appContactService.getContactById(userId);
                    stringBufferTitle.append(withUserContact.getDisplayName());
                }
            } else {
                stringBufferTitle.append(ChannelUtils.getChannelTitleName(channel, MobiComUserPreference.getInstance(getActivity()).getUserId()));
            }
        }
        if (stringBufferTitle != null && getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(stringBufferTitle.toString());
        }

    }

    public void loadConversation(Channel channel, Integer conversationId) {
        loadConversation(null, channel, conversationId, null);
    }

    public void loadConversation(Contact contact, Integer conversationId) {
        loadConversation(contact, null, conversationId, null);
    }

    //With search
    public void loadConversation(Contact contact, Integer conversationId, String searchString) {
        loadConversation(contact, null, conversationId, searchString);
    }

    public void loadConversation(Channel channel, Integer conversationId, String searchString) {
        loadConversation(null, channel, conversationId, searchString);
    }

    public void deleteConversationThread() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity()).
                setPositiveButton(R.string.delete_conversation, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new DeleteConversationAsyncTask(new MobiComConversationService(getActivity()), contact, channel, currentConversationId, getActivity()).execute();
                    }
                });
        alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        alertDialog.setTitle(ApplozicService.getContext(getContext()).getString(R.string.dialog_delete_conversation_title).replace("[name]", getNameForInviteDialog()));
        alertDialog.setMessage(ApplozicService.getContext(getContext()).getString(R.string.dialog_delete_conversation_confir).replace("[name]", getNameForInviteDialog()));
        alertDialog.setCancelable(true);
        alertDialog.create().show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (alCustomizationSettings != null && alCustomizationSettings.isAgentApp()) {
            ((ConversationActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((ConversationActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        }
        if (MobiComUserPreference.getInstance(getActivity()).isChannelDeleted()) {
            MobiComUserPreference.getInstance(getActivity()).setDeleteChannel(false);
            if (getActivity().getSupportFragmentManager() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
            return;
        }

        ((ConversationActivity) getActivity()).setChildFragmentLayoutBGToTransparent();
        if (contact != null || channel != null) {
            BroadcastService.currentUserId = contact != null ? contact.getContactIds() : String.valueOf(channel.getKey());
            BroadcastService.currentConversationId = currentConversationId;
            if (BroadcastService.currentUserId != null) {
                NotificationManagerCompat nMgr = NotificationManagerCompat.from(getActivity());
                if (ApplozicClient.getInstance(getActivity()).isNotificationStacking()) {
                    nMgr.cancel(NotificationService.NOTIFICATION_ID);
                } else {
                    if (contact != null) {
                        if (!TextUtils.isEmpty(contact.getContactIds())) {
                            nMgr.cancel(contact.getContactIds().hashCode());
                        }
                    }
                    if (channel != null) {
                        nMgr.cancel(String.valueOf(channel.getKey()).hashCode());
                    }
                }
            }

            Intent intent = new Intent(getActivity(), ApplozicMqttIntentService.class);
            intent.putExtra(ApplozicMqttIntentService.CHANNEL, channel);
            intent.putExtra(ApplozicMqttIntentService.SUBSCRIBE_TO_TYPING, true);
            ApplozicMqttIntentService.enqueueWork(getActivity(), intent);

            if (downloadConversation != null) {
                downloadConversation.cancel(true);
            }

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (appContactService != null && contact != null) {
                            updateLastSeenStatus();
                        }
                    } catch (Exception e) {

                    }
                }
            });
            thread.setPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            thread.start();

            if (SyncCallService.refreshView) {
                messageList.clear();
                SyncCallService.refreshView = false;
            }

            if (channel != null && !ChannelService.getInstance(getActivity()).processIsUserPresentInChannel(channel.getKey())) {
                Channel newChannel = ChannelService.getInstance(getActivity()).getChannelByChannelKey(channel.getKey());
                if (newChannel != null && newChannel.getType() != null && Channel.GroupType.OPEN.getValue().equals(newChannel.getType())) {
                    MobiComUserPreference.getInstance(getActivity()).setNewMessageFlag(true);
                }
            }

            if (messageList.isEmpty()) {
                loadConversation(contact, channel, currentConversationId, null);
            } else if (MobiComUserPreference.getInstance(getActivity()).getNewMessageFlag()) {
                loadnewMessageOnResume(contact, channel, currentConversationId);
            }

            MobiComUserPreference.getInstance(getActivity()).setNewMessageFlag(false);
        }
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                downloadConversation = new DownloadConversation(recyclerView, false, 1, 1, 1, contact, channel, currentConversationId);
                downloadConversation.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
        if (channel != null) {
            if (Channel.GroupType.SUPPORT_GROUP.getValue().equals(channel.getType())) {
                processSupportGroupDetails(channel);
                if (customToolbarLayout != null) {
                    customToolbarLayout.setVisibility(VISIBLE);
                }
            } else {
                updateChannelTitle();
                if (customToolbarLayout != null) {
                    customToolbarLayout.setVisibility(View.GONE);
                }
            }

            if (channel.getType() != null && (!Channel.GroupType.OPEN.getValue().equals(channel.getType()) && !Channel.GroupType.SUPPORT_GROUP.getValue().equals(channel.getType()))) {
                boolean present = ChannelService.getInstance(getActivity()).processIsUserPresentInChannel(channel.getKey());
                hideSendMessageLayout(channel.isDeleted() || !present);
            } else {
                hideSendMessageLayout(channel.isDeleted());
            }
            if (ChannelService.isUpdateTitle) {
                updateChannelSubTitle();
                ChannelService.isUpdateTitle = false;
            }
        }

        if (isEmailConversation(channel)) {
            emailReplyReminderLayout.setVisibility(VISIBLE);
        }
    }

    private void hideSendMessageLayout(boolean hide) {
        if (hide) {
            individualMessageSendLayout.setVisibility(View.GONE);
            userNotAbleToChatLayout.setVisibility(VISIBLE);
        } else {
            userNotAbleToChatLayout.setVisibility(View.GONE);

        }

    }

    public void updateSupportGroupTitle(Contact contact, Channel channel) {
        String imageUrl = "";
        String name = "";

        if (contact != null) {
            name = contact.getDisplayName();
            imageUrl = contact.getImageURL();
        } else if (channel != null) {
            name = channel.getName();
            imageUrl = channel.getImageUrl();
        }

        if (!TextUtils.isEmpty(imageUrl)) {
            toolbarAlphabeticImage.setVisibility(View.GONE);
            toolbarImageView.setVisibility(VISIBLE);
            try {
                if (getContext() != null) {
                    RequestOptions options = new RequestOptions()
                            .centerCrop()
                            .placeholder(R.drawable.applozic_ic_contact_picture_holo_light)
                            .error(R.drawable.applozic_ic_contact_picture_holo_light);


                    Glide.with(getContext()).load(imageUrl).apply(options).into(toolbarImageView);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            toolbarAlphabeticImage.setVisibility(VISIBLE);
            toolbarImageView.setVisibility(View.GONE);

            String contactNumber = "";
            char firstLetter = 0;

            if (name == null) {
                return;
            }
            contactNumber = name.toUpperCase();
            firstLetter = name.toUpperCase().charAt(0);

            if (firstLetter != '+') {
                toolbarAlphabeticImage.setText(String.valueOf(firstLetter));
            } else if (contactNumber.length() >= 2) {
                toolbarAlphabeticImage.setText(String.valueOf(contactNumber.charAt(1)));
            }

            Character colorKey = AlphaNumberColorUtil.alphabetBackgroundColorMap.containsKey(firstLetter) ? firstLetter : null;
            GradientDrawable bgShape = (GradientDrawable) toolbarAlphabeticImage.getBackground();
            if (getContext() != null) {
                bgShape.setColor(getContext().getResources().getColor(AlphaNumberColorUtil.alphabetBackgroundColorMap.get(colorKey)));
            }
        }

        if (!TextUtils.isEmpty(name)) {
            toolbarTitleText.setText(name);
        }

        if (toolbarOnlineTv != null) {
            toolbarOnlineTv.setVisibility(View.GONE);
        }
        if (toolbarOfflineTv != null) {
            toolbarOfflineTv.setVisibility(View.VISIBLE);
        }
        if (toolbarSubtitleText != null) {
            toolbarSubtitleText.setVisibility(View.GONE);
        }

        if (contact != null) {
            switchContactStatus(contact);
        }
    }

    public void getUserDetail(Context context, String userId, KmUserDetailsCallback callback) {
        new KMUserDetailTask(context, userId, callback).execute();
    }

    public void processSupportGroupDetails(final Channel channel) {
        Contact contact = KmService.getSupportGroupContact(getContext(), channel, appContactService, loggedInUserRole);
        updateSupportGroupTitle(contact, channel);
        conversationAssignee = contact;

        if (contact != null) {
            getUserDetail(getContext(), contact.getUserId(), new KmUserDetailsCallback() {
                @Override
                public void hasFinished(Contact contact) {
                    conversationAssignee = contact;
                    updateSupportGroupTitle(contact, channel);
                }
            });
        }
    }

    public void switchContactStatus(Contact contact) {
        if (toolbarSubtitleText != null) {
            if (fontManager != null && fontManager.getToolbarSubtitleFont() != null) {
                toolbarSubtitleText.setTypeface(fontManager.getToolbarSubtitleFont());
            } else {
                toolbarSubtitleText.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            }
            if (User.RoleType.BOT.getValue().equals(contact.getRoleType())) {
                toolbarSubtitleText.setText(ApplozicService.getContext(getContext()).getString(R.string.online));
                toolbarSubtitleText.setVisibility(VISIBLE);
                if (toolbarOnlineTv != null && toolbarOfflineTv != null) {
                    toolbarOnlineTv.setVisibility(VISIBLE);
                    toolbarOfflineTv.setVisibility(View.GONE);
                }
                return;
            }
            if (contact.isConnected()) {
                toolbarSubtitleText.setText(R.string.online);
                toolbarSubtitleText.setVisibility(VISIBLE);
            } else {
                if (User.RoleType.USER_ROLE.getValue().equals(contact.getRoleType())) {
                    if (contact.getLastSeenAt() > 0) {
                        if (getActivity() != null) {
                            toolbarSubtitleText.setVisibility(VISIBLE);
                            toolbarSubtitleText.setText(ApplozicService.getContext(getContext()).getString(R.string.subtitle_last_seen_at_time) + " " + DateUtils.getDateAndTimeForLastSeen(ApplozicService.getContext(getContext()), contact.getLastSeenAt(), R.string.JUST_NOW, R.plurals.MINUTES_AGO, R.plurals.HOURS_AGO, R.string.YESTERDAY));
                        }
                    } else {
                        toolbarSubtitleText.setVisibility(View.GONE);
                    }
                } else {
                    toolbarSubtitleText.setVisibility(View.VISIBLE);
                    toolbarSubtitleText.setText(R.string.offline);
                }
            }
        }

        if (toolbarOnlineTv != null && toolbarOfflineTv != null) {
            if (contact.isConnected()) {
                toolbarOnlineTv.setVisibility(VISIBLE);
                toolbarOfflineTv.setVisibility(View.GONE);
            } else {
                toolbarOnlineTv.setVisibility(View.GONE);
                toolbarOfflineTv.setVisibility(VISIBLE);
            }
        }
    }

    public void updateChannelTitleAndSubTitle() {
        if (channel != null) {
            Channel channelInfo = ChannelService.getInstance(getActivity()).getChannelInfo(channel.getKey());

            if (channelInfo.isDeleted()) {
                channel.setDeletedAtTime(channelInfo.getDeletedAtTime());
                individualMessageSendLayout.setVisibility(View.GONE);
                userNotAbleToChatLayout.setVisibility(VISIBLE);
                userNotAbleToChatTextView.setText(ApplozicService.getContext(getContext()).getString(R.string.group_has_been_deleted_text));
                if (channel != null && !ChannelService.getInstance(getContext()).isUserAlreadyPresentInChannel(channel.getKey(), MobiComUserPreference.getInstance(getContext()).getUserId())
                        && messageTemplate != null && messageTemplate.isEnabled() && templateAdapter != null) {
                    templateAdapter.setMessageList(new HashMap<String, String>());
                    templateAdapter.notifyDataSetChanged();
                }
            } else {
                if ((!ChannelService.getInstance(getActivity()).processIsUserPresentInChannel(channel.getKey())
                        && userNotAbleToChatLayout != null
                        && (!Channel.GroupType.OPEN.getValue().equals(channel.getType())) && !Channel.GroupType.SUPPORT_GROUP.getValue().equals(channel.getType()))) {
                    individualMessageSendLayout.setVisibility(View.GONE);
                    userNotAbleToChatLayout.setVisibility(VISIBLE);
                    if (channel != null && !ChannelService.getInstance(getContext()).isUserAlreadyPresentInChannel(channel.getKey(), MobiComUserPreference.getInstance(getContext()).getUserId())
                            && messageTemplate != null && messageTemplate.isEnabled() && templateAdapter != null) {
                        templateAdapter.setMessageList(new HashMap<String, String>());
                        templateAdapter.notifyDataSetChanged();
                    }
                }
            }

            if (channel != null && Channel.GroupType.SUPPORT_GROUP.getValue().equals(channel.getType())) {
                processSupportGroupDetails(channel);
            } else {
                updateChannelTitle();
                updateChannelSubTitle();
            }
        }
    }


    public void updateChannelTitle() {
        if (!Channel.GroupType.GROUPOFTWO.getValue().equals(channel.getType())) {
            Channel newChannel = ChannelService.getInstance(getActivity()).getChannelByChannelKey(channel.getKey());
            if (newChannel != null && !TextUtils.isEmpty(channel.getName()) && !channel.getName().equals(newChannel.getName())) {
                title = ChannelUtils.getChannelTitleName(newChannel, MobiComUserPreference.getInstance(getActivity()).getUserId());
                channel = newChannel;
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
            }
        }
    }

    public void updateTitleForOpenGroup() {
        try {
            if (channel != null) {
                Channel newChannel = ChannelService.getInstance(getActivity()).getChannelByChannelKey(channel.getKey());
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(newChannel.getName());
            }
            updateChannelSubTitle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void selfDestructMessage(Message message) {
        if (Message.MessageType.MT_INBOX.getValue().equals(message.getType()) &&
                message.getTimeToLive() != null && message.getTimeToLive() != 0) {
            new Timer().schedule(new DisappearingMessageTask(getActivity(), conversationService, message), message.getTimeToLive() * 60 * 1000);
        }
    }

    public void loadnewMessageOnResume(Contact contact, Channel channel, Integer conversationId) {
        downloadConversation = new DownloadConversation(recyclerView, true, 1, 0, 0, contact, channel, conversationId);
        downloadConversation.execute();
    }

    public int ScrollToFirstSearchIndex() {

        int position = 0;
        if (searchString != null) {

            for (position = messageList.size() - 1; position >= 0; position--) {
                Message message = messageList.get(position);
                if (!TextUtils.isEmpty(message.getMessage()) && message.getMessage().toLowerCase(Locale.getDefault()).indexOf(
                        searchString.toString().toLowerCase(Locale.getDefault())) != -1) {
                    return position;
                }
            }
        } else {
            position = messageList.size();
        }
        return position;
    }

    public void blockUserProcess(final String userId, final boolean block, final boolean isFromChannel) {

        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "",
                ApplozicService.getContext(getContext()).getString(R.string.please_wait_info), true);

        UserBlockTask.TaskListener listener = new UserBlockTask.TaskListener() {

            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (block && typingStarted) {
                    if (getActivity() != null) {
                        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("");
                    }
                    Intent intent = new Intent(getActivity(), ApplozicMqttIntentService.class);
                    intent.putExtra(ApplozicMqttIntentService.CONTACT, contact);
                    intent.putExtra(ApplozicMqttIntentService.STOP_TYPING, true);
                    ApplozicMqttIntentService.enqueueWork(getActivity(), intent);
                }
                menu.findItem(R.id.userBlock).setVisible(!block);
                menu.findItem(R.id.userUnBlock).setVisible(block);
            }

            @Override
            public void onFailure(ApiResponse apiResponse, Exception exception) {
                String error = ApplozicService.getContext(getContext()).getString(Utils.isInternetAvailable(getActivity()) ? R.string.applozic_server_error : R.string.you_need_network_access_for_block_or_unblock);
                Toast toast = Toast.makeText(getActivity(), error, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }

            @Override
            public void onCompletion() {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (!isFromChannel) {
                    contact = appContactService.getContactById(userId);
                }
            }

        };

        new UserBlockTask(getActivity(), listener, userId, block).execute((Void) null);
    }

    public void userBlockDialog(final boolean block, final Contact withUserContact, final boolean isFromChannel) {
        if (withUserContact == null) {
            return;
        }
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity()).
                setPositiveButton(R.string.ok_alert, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        blockUserProcess(withUserContact.getUserId(), block, isFromChannel);
                    }
                });
        alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        String name = withUserContact.getDisplayName();
        alertDialog.setMessage(getString(block ? R.string.user_block_info : R.string.user_un_block_info).replace("[name]", name));
        alertDialog.setCancelable(true);
        alertDialog.create().show();
    }

    public void muteGroupChat() {

        final CharSequence[] items = {ApplozicService.getContext(getContext()).getString(R.string.eight_Hours), ApplozicService.getContext(getContext()).getString(R.string.one_week), ApplozicService.getContext(getContext()).getString(R.string.one_year)};
        Date date = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
        millisecond = date.getTime();

        final MuteNotificationAsync.TaskListener taskListener = new MuteNotificationAsync.TaskListener() {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (menu != null) {
                    menu.findItem(R.id.muteGroup).setVisible(false);
                    menu.findItem(R.id.unmuteGroup).setVisible(true);
                }
            }

            @Override
            public void onFailure(ApiResponse apiResponse, Exception exception) {

            }

            @Override
            public void onCompletion() {

            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle(ApplozicService.getContext(getContext()).getResources().getString(R.string.mute_group_for))
                .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, final int selectedItem) {
                        if (selectedItem == 0) {
                            millisecond = millisecond + 28800000;
                        } else if (selectedItem == 1) {
                            millisecond = millisecond + 604800000;

                        } else if (selectedItem == 2) {
                            millisecond = millisecond + 31558000000L;
                        }

                        muteNotificationRequest = new MuteNotificationRequest(channel.getKey(), millisecond);
                        MuteNotificationAsync muteNotificationAsync = new MuteNotificationAsync(getContext(), taskListener, muteNotificationRequest);
                        muteNotificationAsync.execute((Void) null);
                        dialog.dismiss();

                    }
                });
        AlertDialog alertdialog = builder.create();
        alertdialog.show();
    }

    public void umuteGroupChat() {
        Date date = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
        millisecond = date.getTime();

        final MuteNotificationAsync.TaskListener taskListener = new MuteNotificationAsync.TaskListener() {
            @Override
            public void onSuccess(ApiResponse apiResponse) {
                if (menu != null) {
                    menu.findItem(R.id.unmuteGroup).setVisible(false);
                    menu.findItem(R.id.muteGroup).setVisible(true);
                }
            }

            @Override
            public void onFailure(ApiResponse apiResponse, Exception exception) {

            }

            @Override
            public void onCompletion() {

            }
        };
        muteNotificationRequest = new MuteNotificationRequest(channel.getKey(), millisecond);
        MuteNotificationAsync muteNotificationAsync = new MuteNotificationAsync(getContext(), taskListener, muteNotificationRequest);
        muteNotificationAsync.execute((Void) null);
    }

    public void muteUserChat() {
        final CharSequence[] items = {ApplozicService.getContext(getContext()).getString(R.string.eight_Hours), ApplozicService.getContext(getContext()).getString(R.string.one_week), ApplozicService.getContext(getContext()).getString(R.string.one_year)};
        Date date = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
        millisecond = date.getTime();

        final MuteUserNotificationAsync.TaskListener listener = new MuteUserNotificationAsync.TaskListener() {

            @Override
            public void onSuccess(String status, Context context) {
                if (menu != null) {
                    menu.findItem(R.id.muteGroup).setVisible(false);
                    menu.findItem(R.id.unmuteGroup).setVisible(true);
                }
            }

            @Override
            public void onFailure(String error, Context context) {

            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle(ApplozicService.getContext(getContext()).getResources().getString(R.string.mute_user_for))
                .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, final int selectedItem) {
                        if (selectedItem == 0) {
                            millisecond = millisecond + 28800000;
                        } else if (selectedItem == 1) {
                            millisecond = millisecond + 604800000;
                        } else if (selectedItem == 2) {
                            millisecond = millisecond + 31558000000L;
                        }

                        new MuteUserNotificationAsync(listener, millisecond, contact.getUserId(), getContext()).execute();
                        dialog.dismiss();

                    }
                });
        AlertDialog alertdialog = builder.create();
        alertdialog.show();
    }

    public void unMuteUserChat() {
        Date date = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
        millisecond = date.getTime();

        final MuteUserNotificationAsync.TaskListener taskListener = new MuteUserNotificationAsync.TaskListener() {

            @Override
            public void onSuccess(String status, Context context) {
                if (menu != null) {
                    menu.findItem(R.id.unmuteGroup).setVisible(false);
                    menu.findItem(R.id.muteGroup).setVisible(true);
                }
            }

            @Override
            public void onFailure(String error, Context context) {

            }
        };
        new MuteUserNotificationAsync(taskListener, millisecond, contact.getUserId(), getContext()).execute();
    }

    public void muteUser(boolean mute) {
        if (menu != null) {
            menu.findItem(R.id.unmuteGroup).setVisible(mute);
            menu.findItem(R.id.muteGroup).setVisible(!mute);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() != null) {
            ((ConversationActivity) getActivity()).setChildFragmentLayoutBG();
        }
        if (ApplozicAudioManager.getInstance(getContext()) != null) {
            ApplozicAudioManager.getInstance(getContext()).audiostop();
        }
    }

    public ViewGroup.LayoutParams getImageLayoutParam(boolean outBoxType) {
        if (getActivity() != null) {
            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
            if (wm != null) {
                wm.getDefaultDisplay().getMetrics(metrics);
                float wt_px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getActivity().getResources().getDisplayMetrics());
                ViewGroup.MarginLayoutParams params;
                if (outBoxType) {
                    params = new RelativeLayout.LayoutParams(metrics.widthPixels + (int) wt_px * 2, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins((int) wt_px, 0, (int) wt_px, 0);
                } else {
                    params = new RelativeLayout.LayoutParams(metrics.widthPixels - (int) wt_px * 2, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 0, 0, 0);
                }
                return params;
            }
        }
        return null;
    }

    public void createTemplateMessages() {
        if (templateAdapter == null) {
            return;
        }

        if (!messageList.isEmpty()) {
            Message lastMessage = messageList.get(messageList.size() - 1);

            if (lastMessage.getMetadata().containsKey("isDoneWithClicking")) {
                return;
            }

            if (lastMessage.getMetadata() != null && lastMessage.getMetadata().containsKey(MobiComKitConstants.TEMPLATE_MESSAGE_LIST)) {
                Map<String, String> messageArray = (Map<String, String>) GsonUtils.getObjectFromJson(lastMessage.getMetadata().get(MobiComKitConstants.TEMPLATE_MESSAGE_LIST), Map.class);
                templateAdapter.setMessageList(messageArray);
                templateAdapter.notifyDataSetChanged();
            } else {
                String type = getMessageType(lastMessage);
                if ("audio".equals(type)) {
                    if (messageTemplate.getAudioMessageList() != null) {
                        if ((lastMessage.isTypeOutbox() && messageTemplate.getAudioMessageList().isShowOnSenderSide()) ||
                                messageTemplate.getAudioMessageList().isShowOnReceiverSide()) {
                            templateAdapter.setMessageList(messageTemplate.getAudioMessageList().getMessageList());
                            templateAdapter.notifyDataSetChanged();
                        }
                    }
                } else if ("video".equals(type)) {
                    if (messageTemplate.getVideoMessageList() != null) {
                        if ((lastMessage.isTypeOutbox() && messageTemplate.getVideoMessageList().isShowOnSenderSide()) ||
                                messageTemplate.getVideoMessageList().isShowOnReceiverSide()) {
                            templateAdapter.setMessageList(messageTemplate.getVideoMessageList().getMessageList());
                            templateAdapter.notifyDataSetChanged();
                        }
                    }
                } else if ("image".equals(type)) {
                    if (messageTemplate.getImageMessageList() != null) {
                        if ((lastMessage.isTypeOutbox() && messageTemplate.getImageMessageList().isShowOnSenderSide()) ||
                                messageTemplate.getImageMessageList().isShowOnReceiverSide()) {
                            templateAdapter.setMessageList(messageTemplate.getImageMessageList().getMessageList());
                            templateAdapter.notifyDataSetChanged();
                        }
                    }
                } else if (lastMessage.getContentType() == Message.ContentType.LOCATION.getValue()) {
                    if (messageTemplate.getLocationMessageList() != null) {
                        if ((lastMessage.isTypeOutbox() && messageTemplate.getLocationMessageList().isShowOnSenderSide()) ||
                                messageTemplate.getLocationMessageList().isShowOnReceiverSide()) {
                            templateAdapter.setMessageList(messageTemplate.getLocationMessageList().getMessageList());
                            templateAdapter.notifyDataSetChanged();
                        }
                    }
                } else if (lastMessage.getContentType() == Message.ContentType.CONTACT_MSG.getValue()) {
                    if (messageTemplate.getContactMessageList() != null) {
                        if ((lastMessage.isTypeOutbox() && messageTemplate.getContactMessageList().isShowOnSenderSide()) ||
                                messageTemplate.getContactMessageList().isShowOnReceiverSide()) {
                            templateAdapter.setMessageList(messageTemplate.getContactMessageList().getMessageList());
                            templateAdapter.notifyDataSetChanged();
                        }
                    }
                } else if ("text".equals(type)) {
                    if (messageTemplate.getTextMessageList() != null) {
                        if ((lastMessage.isTypeOutbox() && messageTemplate.getTextMessageList().isShowOnSenderSide()) ||
                                messageTemplate.getTextMessageList().isShowOnReceiverSide()) {
                            templateAdapter.setMessageList(messageTemplate.getTextMessageList().getMessageList());
                            templateAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }
    }

    public String getMessageType(Message lastMessage) {
        String type = null;

        if (lastMessage == null) {
            return null;
        }

        if (lastMessage.getContentType() == Message.ContentType.LOCATION.getValue()) {
            type = "location";
        } else if (lastMessage.getContentType() == Message.ContentType.AUDIO_MSG.getValue()) {
            type = "audio";
        } else if (lastMessage.getContentType() == Message.ContentType.VIDEO_MSG.getValue()) {
            type = "video";
        } else if (lastMessage.getContentType() == Message.ContentType.ATTACHMENT.getValue()) {
            if (lastMessage.getFilePaths() != null) {
                String filePath = lastMessage.getFilePaths().get(lastMessage.getFilePaths().size() - 1);
                String mimeType = FileUtils.getMimeType(filePath);

                if (mimeType != null) {
                    if (mimeType.startsWith("image")) {
                        type = "image";
                    } else if (mimeType.startsWith("audio")) {
                        type = "audio";
                    } else if (mimeType.startsWith("video")) {
                        type = "video";
                    }
                }
            } else if (lastMessage.getFileMetas() != null) {
                if (lastMessage.getFileMetas().getContentType().contains("image")) {
                    type = "image";
                } else if (lastMessage.getFileMetas().getContentType().contains("audio")) {
                    type = "audio";
                } else if (lastMessage.getFileMetas().getContentType().contains("video")) {
                    type = "video";
                }
            }
        } else if (lastMessage.getContentType() == Message.ContentType.CONTACT_MSG.getValue()) {
            type = "contact";
        } else {
            type = "text";
        }
        return type;
    }

    public class DownloadConversation extends AsyncTask<Void, Integer, Long> {

        //private AbsListView view;
        private RecyclerView recyclerView;
        //private QuickConversationAdapter recyclerAdapter;
        private int firstVisibleItem;
        private int amountVisible;
        private int totalItems;
        private boolean initial;
        private Contact contact;
        private Channel channel;
        private Integer conversationId;
        private List<Conversation> conversationList;
        private List<Message> nextMessageList = new ArrayList<Message>();

        public DownloadConversation(RecyclerView recyclerView, boolean initial, int firstVisibleItem, int amountVisible, int totalItems, Contact contact, Channel channel, Integer conversationId) {
            this.recyclerView = recyclerView;
            this.initial = initial;
            this.firstVisibleItem = firstVisibleItem;
            this.amountVisible = amountVisible;
            this.totalItems = totalItems;
            this.contact = contact;
            this.channel = channel;
            this.conversationId = conversationId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            emptyTextView.setVisibility(View.GONE);

            if (swipeLayout != null) {
                swipeLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeLayout.setRefreshing(true);
                    }
                });
            }

            if (initial) {
                if (recordButtonWeakReference != null) {
                    KmRecordButton recordButton = recordButtonWeakReference.get();
                    if (recordButton != null) {
                        recordButton.setEnabled(false);
                    }
                }
                sendButton.setEnabled(false);
                messageEditText.setEnabled(false);
            }

            if (!initial && messageList.isEmpty()) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity()).
                        setPositiveButton(R.string.ok_alert, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        loadMore = false;
                    }
                });
                //Todo: Move this to mobitexter app
                alertDialog.setTitle(ApplozicService.getContext(getContext()).getString(R.string.sync_older_messages));
                alertDialog.setCancelable(true);
                alertDialog.create().show();
            }
        }

        @Override
        protected Long doInBackground(Void... voids) {
            try {
                if (initial) {
                    Long lastConversationloadTime = 1L;
                    if (!messageList.isEmpty()) {
                        for (int i = messageList.size() - 1; i >= 0; i--) {
                            if (messageList.get(i).isTempDateType()) {
                                continue;
                            }
                            lastConversationloadTime = messageList.get(i).getCreatedAtTime();
                            break;
                        }
                    }


                    nextMessageList = conversationService.getMessages(lastConversationloadTime + 1L, null, contact, channel, conversationId);
                } else if (firstVisibleItem == 1 && loadMore && !messageList.isEmpty()) {
                    loadMore = false;
                    Long endTime = null;
                    for (Message message : messageList) {
                        if (message.isTempDateType()) {
                            continue;
                        }
                        endTime = messageList.get(0).getCreatedAtTime();
                        break;
                    }
                    nextMessageList = conversationService.getMessages(null, endTime, contact, channel, conversationId);
                }
                if (BroadcastService.isContextBasedChatEnabled()) {
                    conversations = ConversationService.getInstance(getActivity()).getConversationList(channel, contact);
                }

                List<Message> createAtMessage = new ArrayList<Message>();
                if (nextMessageList != null && !nextMessageList.isEmpty()) {
                    Message firstDateMessage = new Message();
                    firstDateMessage.setTempDateType(Short.valueOf("100"));
                    firstDateMessage.setCreatedAtTime(nextMessageList.get(0).getCreatedAtTime());

                    if (initial && !messageList.contains(firstDateMessage)) {
                        createAtMessage.add(firstDateMessage);
                    } else if (!initial) {
                        createAtMessage.add(firstDateMessage);
                        messageList.remove(firstDateMessage);
                    }
                    if (!createAtMessage.contains(nextMessageList.get(0))) {
                        createAtMessage.add(nextMessageList.get(0));
                    }

                    for (int i = 1; i <= nextMessageList.size() - 1; i++) {
                        long dayDifference = DateUtils.daysBetween(new Date(nextMessageList.get(i - 1).getCreatedAtTime()), new Date(nextMessageList.get(i).getCreatedAtTime()));

                        if (dayDifference >= 1) {
                            Message message = new Message();
                            message.setTempDateType(Short.valueOf("100"));
                            message.setCreatedAtTime(nextMessageList.get(i).getCreatedAtTime());
                            if (initial && !messageList.contains(message)) {
                                createAtMessage.add(message);
                            } else if (!initial) {
                                createAtMessage.add(message);
                                messageList.remove(message);
                            }
                        }
                        if (!createAtMessage.contains(nextMessageList.get(i))) {
                            createAtMessage.add(nextMessageList.get(i));
                        }
                    }
                }
                nextMessageList = createAtMessage;
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return 0L;
        }

        @Override
        protected void onPostExecute(Long result) {
            super.onPostExecute(result);
            //TODO: FIX ME
            if (swipeLayout != null) {
                swipeLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeLayout.setRefreshing(true);
                    }
                });
            }
            if (nextMessageList.isEmpty()) {
                linearLayoutManager.setStackFromEnd(true);
            }

            if (!messageList.isEmpty() && !nextMessageList.isEmpty() &&
                    messageList.get(0).equals(nextMessageList.get(nextMessageList.size() - 1))) {
                nextMessageList.remove(nextMessageList.size() - 1);
            }

            if (!messageList.isEmpty() && !nextMessageList.isEmpty() &&
                    messageList.get(0).getCreatedAtTime().equals(nextMessageList.get(nextMessageList.size() - 1).getCreatedAtTime())) {
                nextMessageList.remove(nextMessageList.size() - 1);
            }

            for (Message message : nextMessageList) {
                if (initial && !messageList.contains(message)) {
                    messageList.add(message);
                }
                selfDestructMessage(message);
            }

            if (initial) {
                recyclerDetailConversationAdapter.searchString = searchString;
                emptyTextView.setVisibility(messageList.isEmpty() ? VISIBLE : View.GONE);

                if (!messageList.isEmpty()) {
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!TextUtils.isEmpty(searchString)) {
                                int height = recyclerView.getHeight();
                                int itemHeight = recyclerView.getChildAt(0).getHeight();
                                recyclerView.requestFocusFromTouch();
                                recyclerView.scrollTo(ScrollToFirstSearchIndex() + 1, height / 2 - itemHeight / 2);
                            } else {
                                linearLayoutManager.scrollToPositionWithOffset(messageList.size() - 1, 0);
                            }
                        }
                    });
                }
            } else if (!nextMessageList.isEmpty()) {
                linearLayoutManager.setStackFromEnd(true);
                messageList.addAll(0, nextMessageList);
                linearLayoutManager.scrollToPositionWithOffset(nextMessageList.size() - 1, 0);
            }

            conversationService.read(contact, channel);

            if (!messageList.isEmpty()) {
                for (int i = messageList.size() - 1; i >= 0; i--) {
                    Message message = messageList.get(i);
                    if (!message.isRead() && !message.isTempDateType() && !message.isCustom()) {
                        message.setRead(Boolean.TRUE);
                        messageDatabaseService.updateMessageReadFlag(message.getMessageId(), true);
                    }
                }
            }

            if (conversations != null && conversations.size() > 0) {
                conversationList = conversations;
            }
            if (conversationList != null && conversationList.size() > 0 && !onSelected) {
                onSelected = true;
                applozicContextSpinnerAdapter = new ApplozicContextSpinnerAdapter(getActivity(), conversationList);
                if (applozicContextSpinnerAdapter != null && contextSpinner != null) {
                    contextSpinner.setAdapter(applozicContextSpinnerAdapter);
                    contextFrameLayout.setVisibility(VISIBLE);
                    int i = 0;
                    for (Conversation c : conversationList) {
                        i++;
                        if (c.getId().equals(conversationId)) {
                            break;
                        }
                    }
                    contextSpinner.setSelection(i - 1, false);
                    contextSpinner.setOnItemSelectedListener(adapterView);
                }
            }
            if (recyclerDetailConversationAdapter != null) {
                recyclerDetailConversationAdapter.notifyDataSetChanged();
            }
            if (swipeLayout != null) {
                swipeLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeLayout.setRefreshing(false);
                    }
                });
            }

            if (messageToForward != null) {
                sendForwardMessage(messageToForward);
                messageToForward = null;
            }

            if (!messageList.isEmpty()) {
                channelKey = messageList.get(messageList.size() - 1).getGroupId();
            }
            if (initial) {
                if (recordButtonWeakReference != null) {
                    KmRecordButton recordButton = recordButtonWeakReference.get();
                    if (recordButton != null) {
                        recordButton.setEnabled(true);
                    }
                }
                if (sendButton != null) {
                    sendButton.setEnabled(true);
                }
                if (messageEditText != null) {
                    messageEditText.setEnabled(true);
                }
            }
            loadMore = !nextMessageList.isEmpty();
            createTemplateMessages();
        }

    }

    @Override
    public boolean onItemClick(int position, MenuItem item) {
        if (messageList.size() <= position || position == -1) {
            return true;
        }
        Message message = messageList.get(position);
        if (message.isTempDateType() || message.isCustom()) {
            return true;
        }

        switch (item.getItemId()) {
            case 0:
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) ApplozicService.getContext(getContext()).getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText(ApplozicService.getContext(getContext()).getString(R.string.copied_message), message.getMessage());
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                }
                break;
            case 1:
                conversationUIService.startContactActivityForResult(message, null);
                break;
            case 2:
                Message messageToResend = new Message(message);
                messageToResend.setCreatedAtTime(System.currentTimeMillis() + MobiComUserPreference.getInstance(getActivity()).getDeviceTimeOffset());
                conversationService.sendMessage(messageToResend, messageIntentClass);
                break;
            case 3:
                String messageKeyString = message.getKeyString();
                new DeleteConversationAsyncTask(conversationService, message, contact).execute();
                deleteMessageFromDeviceList(messageKeyString);
                break;
            case 4:
                String messageJson = GsonUtils.getJsonFromObject(message, Message.class);
                conversationUIService.startMessageInfoFragment(messageJson);
                break;
            case 5:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                if (message.getFilePaths() != null) {
                    Uri shareUri = null;
                    if (Utils.hasNougat()) {
                        shareUri = FileProvider.getUriForFile(ApplozicService.getContext(getContext()), Utils.getMetaDataValue(getActivity(), MobiComKitConstants.PACKAGE_NAME) + ".provider", new File(message.getFilePaths().get(0)));
                    } else {
                        shareUri = Uri.fromFile(new File(message.getFilePaths().get(0)));
                    }
                    shareIntent.setDataAndType(shareUri, "text/x-vcard");
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, shareUri);
                    if (!TextUtils.isEmpty(message.getMessage())) {
                        shareIntent.putExtra(Intent.EXTRA_TEXT, message.getMessage());
                    }
                    shareIntent.setType(FileUtils.getMimeType(new File(message.getFilePaths().get(0))));
                } else {
                    shareIntent.putExtra(Intent.EXTRA_TEXT, message.getMessage());
                    shareIntent.setType("text/plain");
                }
                startActivity(Intent.createChooser(shareIntent, ApplozicService.getContext(getContext()).getString(R.string.send_message_to)));
                break;

            case 6:
                try {
                    Configuration config = ApplozicService.getContext(getContext()).getResources().getConfiguration();
                    messageMetaData = new HashMap<>();
                    String displayName;
                    if (message.getGroupId() != null) {
                        if (MobiComUserPreference.getInstance(getActivity()).getUserId().equals(message.getContactIds()) || TextUtils.isEmpty(message.getContactIds())) {
                            displayName = ApplozicService.getContext(getContext()).getString(R.string.you_string);
                        } else {
                            displayName = appContactService.getContactById(message.getContactIds()).getDisplayName();
                        }
                    } else {
                        if (message.isTypeOutbox()) {
                            displayName = ApplozicService.getContext(getContext()).getString(R.string.you_string);
                        } else {
                            displayName = appContactService.getContactById(message.getContactIds()).getDisplayName();
                        }
                    }
                    nameTextView.setText(displayName);
                    if (message.hasAttachment()) {
                        FileMeta fileMeta = message.getFileMetas();
                        imageViewForAttachmentType.setVisibility(VISIBLE);
                        if (fileMeta.getContentType().contains("image")) {
                            imageViewForAttachmentType.setImageResource(R.drawable.applozic_ic_image_camera_alt);
                            if (TextUtils.isEmpty(message.getMessage())) {
                                messageTextView.setText(ApplozicService.getContext(getContext()).getString(R.string.photo_string));
                            } else {
                                messageTextView.setText(message.getMessage());
                            }
                            galleryImageView.setVisibility(VISIBLE);
                            imageViewRLayout.setVisibility(VISIBLE);
                            imageThumbnailLoader.loadImage(message, galleryImageView);
                        } else if (fileMeta.getContentType().contains("video")) {
                            imageViewForAttachmentType.setImageResource(R.drawable.applozic_ic_action_video);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                if (config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
                                    imageViewForAttachmentType.setScaleX(-1);
                                }
                            }
                            if (TextUtils.isEmpty(message.getMessage())) {
                                messageTextView.setText(ApplozicService.getContext(getContext()).getString(R.string.video_string));
                            } else {
                                messageTextView.setText(message.getMessage());
                            }
                            if (message.getFilePaths() != null && message.getFilePaths().size() > 0) {
                                if (imageCache.getBitmapFromMemCache(message.getKeyString()) != null) {
                                    galleryImageView.setImageBitmap(imageCache.getBitmapFromMemCache(message.getKeyString()));
                                } else {
                                    imageCache.addBitmapToCache(message.getKeyString(), fileClientService.createAndSaveVideoThumbnail(message.getFilePaths().get(0)));
                                    galleryImageView.setImageBitmap(fileClientService.createAndSaveVideoThumbnail(message.getFilePaths().get(0)));
                                }
                            }
                            galleryImageView.setVisibility(VISIBLE);
                            imageViewRLayout.setVisibility(VISIBLE);
                        } else if (fileMeta.getContentType().contains("audio")) {
                            imageViewForAttachmentType.setImageResource(R.drawable.applozic_ic_music_note);
                            if (TextUtils.isEmpty(message.getMessage())) {
                                messageTextView.setText(ApplozicService.getContext(getContext()).getString(R.string.audio_string));
                            } else {
                                messageTextView.setText(message.getMessage());
                            }
                            galleryImageView.setVisibility(View.GONE);
                            imageViewRLayout.setVisibility(View.GONE);
                        } else if (message.isContactMessage()) {
                            MobiComVCFParser parser = new MobiComVCFParser();
                            imageViewForAttachmentType.setImageResource(R.drawable.applozic_ic_person_white);
                            try {
                                VCFContactData data = parser.parseCVFContactData(message.getFilePaths().get(0));
                                if (data != null) {
                                    messageTextView.setText(ApplozicService.getContext(getContext()).getString(R.string.contact_string));
                                    messageTextView.append(" " + data.getName());
                                }
                            } catch (Exception e) {
                                imageViewForAttachmentType.setImageResource(R.drawable.applozic_ic_person_white);
                                messageTextView.setText(ApplozicService.getContext(getContext()).getString(R.string.contact_string));
                            }
                            galleryImageView.setVisibility(View.GONE);
                            imageViewRLayout.setVisibility(View.GONE);
                        } else {
                            imageViewForAttachmentType.setImageResource(R.drawable.applozic_ic_action_attachment);
                            if (TextUtils.isEmpty(message.getMessage())) {
                                messageTextView.setText(ApplozicService.getContext(getContext()).getString(R.string.attachment_string));
                            } else {
                                messageTextView.setText(message.getMessage());
                            }
                            galleryImageView.setVisibility(View.GONE);
                            imageViewRLayout.setVisibility(View.GONE);
                        }
                        imageViewForAttachmentType.setColorFilter(ContextCompat.getColor(ApplozicService.getContext(getContext()), R.color.apploizc_lite_gray_color));
                    } else if (message.getContentType() == Message.ContentType.LOCATION.getValue()) {
                        imageViewForAttachmentType.setVisibility(VISIBLE);
                        galleryImageView.setVisibility(VISIBLE);
                        imageViewRLayout.setVisibility(VISIBLE);
                        messageTextView.setText(ApplozicService.getContext(getContext()).getString(R.string.al_location_string));
                        imageViewForAttachmentType.setImageResource(R.drawable.applozic_ic_location_on_white_24dp);
                        imageViewForAttachmentType.setColorFilter(ContextCompat.getColor(ApplozicService.getContext(getContext()), R.color.apploizc_lite_gray_color));
                        messageImageLoader.setLoadingImage(R.drawable.applozic_map_offline_thumbnail);
                        messageImageLoader.loadImage(LocationUtils.loadStaticMap(message.getMessage(), geoApiKey), galleryImageView);
                    } else {
                        imageViewForAttachmentType.setVisibility(View.GONE);
                        imageViewRLayout.setVisibility(View.GONE);
                        galleryImageView.setVisibility(View.GONE);
                        messageTextView.setText(message.getMessage());
                    }
                    messageMetaData.put(Message.MetaDataType.AL_REPLY.getValue(), message.getKeyString());
                    if (messageMetaData != null && !messageMetaData.isEmpty()) {
                        String replyMessageKey = messageMetaData.get(Message.MetaDataType.AL_REPLY.getValue());
                        if (!TextUtils.isEmpty(replyMessageKey)) {
                            messageDatabaseService.updateMessageReplyType(replyMessageKey, Message.ReplyMessage.REPLY_MESSAGE.getValue());
                        }
                    }
                    attachReplyCancelLayout.setVisibility(VISIBLE);
                    replayRelativeLayout.setVisibility(VISIBLE);
                } catch (Exception e) {

                }
                break;
        }
        return true;
    }

    public void processAttachmentIconsClick() {

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emoticonsFrameLayout.setVisibility(View.GONE);
                if (getActivity() != null) {
                    if (((KmStoragePermissionListener) getActivity()).isPermissionGranted()) {
                        ((ConversationActivity) getActivity()).isTakePhoto(true);
                        ((ConversationActivity) getActivity()).processCameraAction();
                    } else {
                        ((KmStoragePermissionListener) getActivity()).checkPermission(new KmStoragePermission() {
                            @Override
                            public void onAction(boolean didGrant) {
                                if (didGrant) {
                                    ((ConversationActivity) getActivity()).isTakePhoto(true);
                                    ((ConversationActivity) getActivity()).processCameraAction();
                                }
                            }
                        });
                    }
                }
            }
        });

        fileAttachmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emoticonsFrameLayout.setVisibility(View.GONE);
                if (getActivity() != null) {
                    if (((KmStoragePermissionListener) getActivity()).isPermissionGranted()) {
                        ((ConversationActivity) getActivity()).isAttachment(true);
                        ((ConversationActivity) getActivity()).processAttachment();
                    } else {
                        ((KmStoragePermissionListener) getActivity()).checkPermission(new KmStoragePermission() {
                            @Override
                            public void onAction(boolean didGrant) {
                                if (didGrant) {
                                    ((ConversationActivity) getActivity()).isAttachment(true);
                                    ((ConversationActivity) getActivity()).processAttachment();
                                }
                            }
                        });
                    }
                }
            }
        });

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emoticonsFrameLayout.setVisibility(View.GONE);
                if (getActivity() != null) {
                    ((ConversationActivity) getActivity()).processLocation();
                }
            }
        });
    }

    public void showAwayMessage(boolean show, String message) {
        if (message != null) {
            awayMessageTv.setText(message);
            if (alCustomizationSettings.getAwayMessageTextColor() != null) {
                awayMessageTv.setTextColor(Color.parseColor(alCustomizationSettings.getAwayMessageTextColor()));
            }
        }
        if (awayMessageTv != null) {
            awayMessageTv.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (awayMessageDivider != null) {
            awayMessageDivider.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    public boolean isAwayMessageVisible() {
        return (awayMessageTv != null && awayMessageDivider != null && awayMessageTv.getVisibility() == View.VISIBLE && awayMessageDivider.getVisibility() == View.VISIBLE);
    }

    public void hideAwayMessage(Message message) {
        if (message.isTypeOutbox()) {
            return;
        }

        if (isAwayMessageVisible() && message.getMetadata() != null) {
            if (message.getMetadata().isEmpty()) {
                showAwayMessage(false, null);
                return;
            }
            boolean isValidMetadata = message.getMetadata().containsKey("category") && !"HIDDEN".equals(message.getMetadata().get("category"))
                    && message.getMetadata().containsKey("hide") && !"true".equals(message.getMetadata().get("hide"));

            boolean isSentByBot = message.getMetadata().containsKey("skipBot") && "true".equals(message.getMetadata().get("skipBot"));

            if (isValidMetadata || isSentByBot) {
                showAwayMessage(false, null);
                return;
            }
        }

        if (isAwayMessageVisible() && message.getMetadata() == null) {
            showAwayMessage(false, null);
        }
    }

    @Override
    public void onAction(Context context, String action, Message message, Object object, Map<String, Object> replyMetadata) {
        switch (action) {
            case AlRichMessage.SEND_GUEST_LIST:
                List<ALGuestCountModel> guestCountModels = (List<ALGuestCountModel>) object;
                sendGuestListMessage(guestCountModels, getStringMap(replyMetadata));
                break;

            case AlRichMessage.SEND_HOTEL_RATING:
                sendMessage((String) object, getStringMap(replyMetadata));
                break;

            case AlRichMessage.SEND_HOTEL_DETAILS:
                sendHotelDetailMessage((AlHotelBookingModel) object, getStringMap(replyMetadata));
                break;

            case AlRichMessage.SEND_ROOM_DETAILS_MESSAGE:
                sendRoomDetailsMessage((AlHotelBookingModel) object, getStringMap(replyMetadata));
                break;

            case AlRichMessage.SEND_BOOKING_DETAILS:
                sendBookingDetailsMessage((ALBookingDetailsModel) object, getStringMap(replyMetadata));
                break;

            case AlRichMessage.MAKE_PAYMENT:
            case AlRichMessage.SUBMIT_BUTTON:
                handleSubmitButton(object);
                break;

            case AlRichMessage.QUICK_REPLY_OLD:
            case AlRichMessage.QUICK_REPLY:
                handleQuickReplies(object, replyMetadata);
                break;

            case AlRichMessage.TEMPLATE_ID + 9:
                loadImageOnFullScreen(context, action, (ALRichMessageModel.ALPayloadModel) object);
                break;

            case AlRichMessage.WEB_LINK:
                handleWebLinks(object);
                break;
        }
    }

    public void handleWebLinks(Object object) {
        ALRichMessageModel.AlAction alAction = null;

        if (object instanceof ALRichMessageModel.AlButtonModel) {
            alAction = ((ALRichMessageModel.AlButtonModel) object).getAction();
        } else if (object instanceof ALRichMessageModel.AlElementModel) {
            alAction = ((ALRichMessageModel.AlElementModel) object).getAction();
        } else if (object instanceof ALRichMessageModel.AlAction) {
            alAction = (ALRichMessageModel.AlAction) object;
        }

        if (alAction != null) {
            if (!TextUtils.isEmpty(alAction.getUrl())) {
                openWebLink(alAction.getUrl());
            } else if (alAction.getPayload() != null && !TextUtils.isEmpty(alAction.getPayload().getUrl())) {
                openWebLink(alAction.getPayload().getUrl());
            }
        }

        if (object instanceof ALRichMessageModel.ALPayloadModel) {
            ALRichMessageModel.ALPayloadModel payloadModel = (ALRichMessageModel.ALPayloadModel) object;
            if (!TextUtils.isEmpty(payloadModel.getUrl())) {
                openWebLink(payloadModel.getUrl());
            }
        }
    }

    public void handleQuickReplies(Object object, Map<String, Object> replyMetadata) {
        String message = null;
        if (object instanceof ALRichMessageModel.AlButtonModel) {
            message = ((ALRichMessageModel.AlButtonModel) object).getName();
        } else if (object instanceof ALRichMessageModel.AlAction) {
            message = ((ALRichMessageModel.AlAction) object).getText();
        } else if (object instanceof ALRichMessageModel.AlElementModel) {
            ALRichMessageModel.AlElementModel elementModel = (ALRichMessageModel.AlElementModel) object;
            message = elementModel.getTitle();
            if (replyMetadata == null) {
                replyMetadata = new HashMap<>();
            }
            if (elementModel.getArticleId() != null) {
                replyMetadata.put(AlRichMessage.KM_FAQ_ID, elementModel.getArticleId());
            }
            if (!TextUtils.isEmpty(elementModel.getSource())) {
                replyMetadata.put(AlRichMessage.KM_SOURCE, elementModel.getSource());
            }
        }

        sendMessage(message, getStringMap(replyMetadata));
    }

    public void handleSubmitButton(Object object) {
        if (object instanceof ALRichMessageModel.AlButtonModel) {
            ALRichMessageModel.AlButtonModel buttonModel = (ALRichMessageModel.AlButtonModel) object;
            if (buttonModel.getAction() != null && buttonModel.getAction().getPayload() != null) {
                openWebLink(GsonUtils.getJsonFromObject(buttonModel.getAction().getPayload().getFormData(), ALRichMessageModel.AlFormDataModel.class)
                        , buttonModel.getAction().getPayload().getFormAction());
            }
        } else if (object instanceof ALRichMessageModel) {
            ALRichMessageModel model = (ALRichMessageModel) object;
            openWebLink(model.getFormData(), model.getFormAction());
        }
    }

    public Map<String, String> getStringMap(Map<String, Object> objectMap) {
        if (objectMap == null) {
            return null;
        }
        Map<String, String> newMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : objectMap.entrySet()) {
            newMap.put(entry.getKey(), entry.getValue() instanceof String ? (String) entry.getValue() : entry.getValue().toString());
        }
        return newMap;
    }

    public void sendMessage(String message, Map<String, String> replyMetadata) {
        sendMessage(message, replyMetadata, null, null, Message.ContentType.DEFAULT.getValue());
    }

    public void openWebLink(String url) {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), PaymentActivity.class);
            intent.putExtra(AlRichMessage.WEB_LINK, true);
            intent.putExtra(AlRichMessage.LINK_URL, url);
            getActivity().startActivity(intent);
        }
    }

    public void openWebLink(String formData, String formAction) {
        Intent intent = new Intent(getActivity(), PaymentActivity.class);
        if (!TextUtils.isEmpty(formData)) {
            intent.putExtra(AlRichMessage.KM_FORM_DATA, formData);
        }
        if (!TextUtils.isEmpty(formAction)) {
            intent.putExtra(AlRichMessage.KM_FORM_ACTION, formAction);
        }
        if (getActivity() != null) {
            getActivity().startActivity(intent);
        }
    }

    public void sendGuestListMessage(List<ALGuestCountModel> guestList, Map<String, String> replyMetadata) {

        Map<String, String> metadata = new HashMap<>();
        metadata.put("guestTypeId", "ADULTS");
        metadata.put("isRoomGuestJSON", "true");
        metadata.put("roomGuestJson", GsonUtils.getJsonFromObject(guestList, List.class));

        StringBuilder message = new StringBuilder("");
        int count = 0;

        for (ALGuestCountModel guestModel : guestList) {
            message.append("Room ");
            message.append(count + 1);
            message.append(" Guest ");
            message.append(guestModel.getNoOfAdults());
            message.append(" Children ");
            message.append(guestModel.getNoOfChild());
            message.append(", ");
        }

        if (replyMetadata != null) {
            metadata.putAll(replyMetadata);
        }

        sendMessage(message.toString(), metadata, Message.ContentType.DEFAULT.getValue());
    }

    public void sendHotelDetailMessage(AlHotelBookingModel hotel, Map<String, String> replyMetadata) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("hotelSelected", "true");
        metadata.put("resultIndex", String.valueOf(hotel.getResultIndex()));
        metadata.put("sessionId", hotel.getSessionId());
        metadata.put("skipBot", "true");

        String message = "Get room detail of " + hotel.getHotelName();

        if (replyMetadata != null) {
            metadata.putAll(replyMetadata);
        }

        sendMessage(message, metadata, Message.ContentType.DEFAULT.getValue());
    }

    public void sendRoomDetailsMessage(AlHotelBookingModel hotel, Map<String, String> replyMetadata) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("HotelResultIndex", String.valueOf(hotel.getHotelResultIndex()));
        metadata.put("NoOfRooms", String.valueOf(hotel.getNoOfRooms()));
        metadata.put("RoomIndex", String.valueOf(hotel.getRoomIndex()));
        metadata.put("blockHotelRoom", "true");
        metadata.put("sessionId", hotel.getSessionId());
        metadata.put("skipBot", "true");

        if (replyMetadata != null) {
            metadata.putAll(replyMetadata);
        }

        String message = "Book Hotel " + hotel.getHotelName() + ", Room " + hotel.getRoomTypeName();

        sendMessage(message, metadata, Message.ContentType.DEFAULT.getValue());
    }

    public void sendBookingDetailsMessage(ALBookingDetailsModel model, Map<String, String> replyMetadata) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("guestDetail", "true");
        metadata.put("personInfo", GsonUtils.getJsonFromObject(model.getPersonInfo(), ALBookingDetailsModel.ALBookingDetails.class));
        metadata.put("sessionId", model.getSessionId());
        metadata.put("skipBot", "true");

        if (replyMetadata != null) {
            metadata.putAll(replyMetadata);
        }

        sendMessage("Your details have been submitted", metadata, Message.ContentType.DEFAULT.getValue());
    }

    public void loadImageOnFullScreen(Context context, String action, ALRichMessageModel.ALPayloadModel payloadModel) {
        Intent intent = new Intent(context, FullScreenImageActivity.class);
        intent.putExtra(action, GsonUtils.getJsonFromObject(payloadModel, ALRichMessageModel.ALPayloadModel.class));
        ((MobiComKitActivityInterface) context).startActivityForResult(intent, MobiComKitActivityInterface.REQUEST_CODE_FULL_SCREEN_ACTION);
    }

    public static class KMUserDetailTask extends AsyncTask<Void, Void, Boolean> {

        WeakReference<Context> context;
        String userId;
        KmUserDetailsCallback callback;
        AppContactService appContactService;

        public KMUserDetailTask(Context context, String userId, KmUserDetailsCallback callback) {
            this.context = new WeakReference<>(context);
            this.userId = userId;
            this.callback = callback;
            appContactService = new AppContactService(this.context.get());
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Set<String> userIdSet = new HashSet<>();
                userIdSet.add(userId);
                UserService.getInstance(context.get()).processUserDetailsByUserIds(userIdSet);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean && callback != null) {
                callback.hasFinished(appContactService.getContactById(userId));
            }
        }
    }

    public interface KmUserDetailsCallback {
        void hasFinished(Contact contact);

    }

    public static boolean isEmailConversation(Channel channel) {
        return channel != null && channel.getMetadata() != null && channel.getMetadata().containsKey(KM_CONVERSATION_SUBJECT);
    }

    public CountDownTimer getCountDownTimer(final String userId) {

        if (typingTimerMap == null) {
            typingTimerMap = new HashMap<>();
        }

        if (typingTimerMap.containsKey(userId)) {
            return typingTimerMap.get(userId);
        }

        CountDownTimer timer = new CountDownTimer(TYPING_STOP_TIME * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                if (typingTimerMap != null) {
                    typingTimerMap.remove(userId);
                }
                updateTypingStatus(userId, false);
            }
        };

        typingTimerMap.put(userId, timer);
        return timer;
    }

    @Override
    public void onRecordStart() {
        vibrate();
        toggleRecordViews(false);
        if (applozicAudioRecordManager != null) {
            applozicAudioRecordManager.recordAudio();
        }
    }

    @Override
    public void onRecordCancel() {
        isRecording = false;
        if (recordButton != null && getContext() != null) {
            KmUtils.setBackground(getContext(), recordButton, R.drawable.km_audio_button_background);
        }
        if (applozicAudioRecordManager != null) {
            applozicAudioRecordManager.cancelAudio();
        }
    }

    @Override
    public void onRecordFinish(long recordTime) {
        toggleRecordViews(true);
        if (applozicAudioRecordManager != null) {
            applozicAudioRecordManager.sendAudio();
        }
    }

    @Override
    public void onLessThanSecond() {
        toggleRecordViews(true);
        if (getContext() != null) {
            KmToast.makeText(getContext(), getContext().getString(R.string.km_audio_record_toast_message), Toast.LENGTH_SHORT).show();
        }
        if (applozicAudioRecordManager != null) {
            applozicAudioRecordManager.cancelAudio();
        }
    }

    public void toggleRecordViews(boolean stopRecording) {
        isRecording = !stopRecording;
        if (recordButton != null && getContext() != null) {
            KmUtils.setBackground(getContext(), recordButton, stopRecording ? R.drawable.km_audio_button_background : R.drawable.km_audio_button_pressed_background);
        }

        if (messageEditText != null) {
            messageEditText.setVisibility(stopRecording ? View.VISIBLE : View.GONE);
            if (stopRecording) {
                messageEditText.requestFocus();
            }
        }
    }

    @Override
    public void onAnimationEnd() {
        if (messageEditText != null) {
            messageEditText.setVisibility(View.VISIBLE);
            messageEditText.requestFocus();
        }
    }
}
