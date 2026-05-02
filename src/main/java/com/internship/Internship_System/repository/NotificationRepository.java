package com.internship.Internship_System.repository;

import com.internship.Internship_System.model.Notification;
import com.internship.Internship_System.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserOrderByIdDesc(User user);
}