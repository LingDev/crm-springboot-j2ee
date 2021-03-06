package com.linkdoan.backend.repository;

import com.linkdoan.backend.model.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationsRepository extends JpaRepository<Notifications, Long> {

    //get Count of not read noti

    @Query(value =
            "SELECT COUNT(unn)  " +
                    "FROM User sendUserr INNER JOIN UserNotifications unn ON sendUserr.username = unn.senderId " +
                    "INNER JOIN Notifications nn ON unn.notificationId = nn.id " +
                    "INNER JOIN User rUserr ON unn.receiverId = rUserr.username " +
                    "WHERE unn.receiverId = :username AND unn.status = 1  ")
    int getCountOfNotReadNoti(@Param("username") String username);

    //get All Notifications of user has user Id
    @Query(value =
            "SELECT un.senderId, sendUser.username,un.id, un.notificationId, n.data ,un.status, n.title, un.createdDate " +
                    "FROM User sendUser INNER JOIN UserNotifications un ON sendUser.username = un.senderId " +
                    "INNER JOIN Notifications n ON un.notificationId = n.id " +
                    "INNER JOIN User rUser ON un.receiverId = rUser.username " +
                    "WHERE un.receiverId = :username ORDER BY un.createdDate DESC "
    )
    List<Object[]> getAllNotificationsOfUser(@Param("username") String username);

    //set read for notification

    @Modifying
    @Query(value =
            "UPDATE UserNotifications un SET un.status = 2 WHERE un.id = :id "
    )
    int setReadNotification(@Param("id") Long id);
}
