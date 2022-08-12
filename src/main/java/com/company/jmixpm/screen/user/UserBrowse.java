package com.company.jmixpm.screen.user;

import com.company.jmixpm.entity.User;
import io.jmix.ui.UiComponents;
import io.jmix.ui.component.*;
import io.jmix.ui.download.DownloadFormat;
import io.jmix.ui.download.Downloader;
import io.jmix.ui.navigation.Route;
import io.jmix.ui.screen.*;
import io.jmix.ui.screen.LookupComponent;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.Collection;
import java.util.Set;
import java.util.zip.CRC32;

@UiController("User.browse")
@UiDescriptor("user-browse.xml")
@LookupComponent("usersTable")
@Route("users")
public class UserBrowse extends StandardLookup<User> {
    public static final String ENCODING = "CP866";

    @Autowired
    private UiComponents uiComponents;

    @Autowired
    private GroupTable<User> usersTable;

    @Autowired
    private Downloader downloader;

    @Install(to = "usersTable.avatar", subject = "columnGenerator")
    private Component usersTableAvatarColumnGenerator(User user) {
        if (user.getAvatar() != null) {
            Image image = uiComponents.create(Image.NAME);
            image.setWidth("50px");
            image.setHeight("50px");
            image.setScaleMode(Image.ScaleMode.SCALE_DOWN);

            image.setSource(StreamResource.class)
                    .setStreamSupplier(() -> new ByteArrayInputStream(user.getAvatar()));
            return image;
        } else {
            return new Table.PlainTextCell("");
        }

    }

    @Subscribe("downloadSelectedTasks")
    public void onDownloadSelectedTasksClick(Button.ClickEvent event) {
        Set<User> selected = usersTable.getSelected();
        if (CollectionUtils.isEmpty(selected)) {
            return;
        }

        byte[] bytes = toZipArchive(selected);

        downloader.download(bytes, "avatars.zip", DownloadFormat.ZIP);
    }

    private byte[] toZipArchive(Collection<User> users) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        ZipArchiveOutputStream zipOutputStream = new ZipArchiveOutputStream(byteArrayOutputStream);
        try {
            zipOutputStream.setMethod(ZipArchiveOutputStream.STORED);
            zipOutputStream.setEncoding(ENCODING);
            for (User user : users) {
                try {
                    byte[] reportBytes = user.getAvatar();
                    // todo Необходимо сохранять разрешение файла где-то в User,
                    //  чтобы здесь его правильно приписывать к названию
                    ArchiveEntry singleReportEntry = newStoredEntry(user.getUsername() + ".png", reportBytes);
                    zipOutputStream.putArchiveEntry(singleReportEntry);
                    zipOutputStream.write(reportBytes);
                    zipOutputStream.closeArchiveEntry();
                } catch (IOException e) {
                    throw new RuntimeException(String.format("Exception occurred while exporting file [%s]", user.getUsername()), e);
                }
            }
        } finally {
            IOUtils.closeQuietly(zipOutputStream);
        }

        return byteArrayOutputStream.toByteArray();
    }

    protected ArchiveEntry newStoredEntry(String name, byte[] data) {
        ZipArchiveEntry zipEntry = new ZipArchiveEntry(name);
        zipEntry.setSize(data.length);
        zipEntry.setCompressedSize(zipEntry.getSize());
        CRC32 crc32 = new CRC32();
        crc32.update(data);
        zipEntry.setCrc(crc32.getValue());
        return zipEntry;
    }
}