package com.company.jmixpm.screen.multiupload;

import io.jmix.ui.Notifications;
import io.jmix.ui.UiComponents;
import io.jmix.ui.component.Button;
import io.jmix.ui.component.FileMultiUploadField;
import io.jmix.ui.component.FileStorageUploadField;
import io.jmix.ui.component.SingleFileUploadField;
import io.jmix.ui.screen.Screen;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;
import io.jmix.ui.upload.TemporaryStorage;
import org.apache.commons.collections4.MapUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@UiController("MultiUploadScreen")
@UiDescriptor("multi-upload-screen.xml")
public class MultiUploadScreen extends Screen {

    @Autowired
    private FileStorageUploadField filesStorageUploadField;

    @Autowired
    private Notifications notifications;

    @Autowired
    private UiComponents uiComponents;
    @Autowired
    private TemporaryStorage temporaryStorage;

    @Subscribe("filesStorageUploadField")
    public void onMultiUploadFileUploadSucceed(SingleFileUploadField.FileUploadSucceedEvent event) throws IOException {
        UUID fileId = filesStorageUploadField.getFileId();

        File file = temporaryStorage.getFile(fileId);

        FileInputStream fileInputStream = new FileInputStream(file);
        Workbook workbook = new XSSFWorkbook(fileInputStream);

        Sheet sheet = workbook.getSheetAt(0);

        StringBuilder sb = new StringBuilder();

        Map<Integer, List<String>> data = new HashMap<>();
        int i = 0;
        for (Row row : sheet) {
            data.put(i, new ArrayList<>());
            sb.append("Row ").append(i).append(" :\n");
            for (Cell cell : row) {
                switch (cell.getCellType()) {
                    case STRING:
                        sb.append("Value str: ").append(cell.getStringCellValue()).append("\n");
                        data.get(i).add(cell.getStringCellValue());
                        break;
                    case NUMERIC:
                        sb.append("Value str: ").append(cell.getNumericCellValue()).append("\n");
                        data.get(i).add(cell.getNumericCellValue() + "");
                        break;
                }
            }
            i++;
        }

        notifications.create(Notifications.NotificationType.TRAY)
                .withCaption(sb.toString())
                .show();
    }
}