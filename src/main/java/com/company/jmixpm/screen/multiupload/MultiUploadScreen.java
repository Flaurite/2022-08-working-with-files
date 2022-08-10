package com.company.jmixpm.screen.multiupload;

import io.jmix.ui.Notifications;
import io.jmix.ui.UiComponents;
import io.jmix.ui.component.Button;
import io.jmix.ui.component.FileMultiUploadField;
import io.jmix.ui.screen.Screen;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;
import io.jmix.ui.upload.TemporaryStorage;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@UiController("MultiUploadScreen")
@UiDescriptor("multi-upload-screen.xml")
public class MultiUploadScreen extends Screen {

    @Autowired
    private FileMultiUploadField multiUpload;

    @Autowired
    private Notifications notifications;

    @Autowired
    private UiComponents uiComponents;
    @Autowired
    private TemporaryStorage temporaryStorage;

    private Map<UUID, String> tempFiles;

    @Subscribe("clearTempStorageBtn")
    public void onClearTempStorageBtnClick(Button.ClickEvent event) {
        if (MapUtils.isNotEmpty(tempFiles)) {
            tempFiles.keySet()
                    .forEach(temporaryStorage::deleteFile);
        }
    }

    @Subscribe("multiUpload")
    public void onMultiUploadQueueUploadComplete(FileMultiUploadField.QueueUploadCompleteEvent event) {
        tempFiles = new HashMap<>(multiUpload.getUploadsMap());

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<UUID, String> entry : tempFiles.entrySet()) {
            sb.append("UUID: ").append(entry.getKey())
                    .append(" - Name: ").append(entry.getValue()).append("\n");
        }

        notifications.create()
                .withCaption("Uploaded files: \n" + sb)
                .show();

        multiUpload.clearUploads();
    }
}