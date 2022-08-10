package com.company.jmixpm.screen.user;

import com.company.jmixpm.entity.User;
import io.jmix.ui.UiComponents;
import io.jmix.ui.component.Component;
import io.jmix.ui.component.FileResource;
import io.jmix.ui.component.Image;
import io.jmix.ui.component.StreamResource;
import io.jmix.ui.navigation.Route;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;

@UiController("User.browse")
@UiDescriptor("user-browse.xml")
@LookupComponent("usersTable")
@Route("users")
public class UserBrowse extends StandardLookup<User> {

    @Autowired
    private UiComponents uiComponents;

    @Install(to = "usersTable.avatar", subject = "columnGenerator")
    private Component usersTableAvatarColumnGenerator(User user) {
        Image image = uiComponents.create(Image.NAME);
        image.setWidth("50px");
        image.setHeight("50px");
        image.setScaleMode(Image.ScaleMode.SCALE_DOWN);
        image.setSource(StreamResource.class)
                .setStreamSupplier(() -> new ByteArrayInputStream(user.getAvatar()));
        return image;
    }


}