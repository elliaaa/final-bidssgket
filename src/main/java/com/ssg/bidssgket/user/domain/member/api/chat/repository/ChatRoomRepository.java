package com.ssg.bidssgket.user.domain.member.api.chat.repository;

import com.ssg.bidssgket.user.domain.member.api.chat.model.ChatRoom;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("SELECT c FROM ChatRoom c WHERE c.product.productNo = :productNo")
    Optional<ChatRoom> findByProductNo(@Param("productNo") Long productNo);
}
