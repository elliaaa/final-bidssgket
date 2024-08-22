package com.ssg.bidssgket.user.domain.member.api.chat.controller;

import com.ssg.bidssgket.user.domain.member.api.chat.model.ChatMessage;
import com.ssg.bidssgket.user.domain.member.api.chat.model.ChatRoom;
import com.ssg.bidssgket.user.domain.member.api.chat.model.ChatRoomMember;
import com.ssg.bidssgket.user.domain.member.api.chat.repository.ChatRoomMemberRepository;
import com.ssg.bidssgket.user.domain.member.api.chat.repository.ChatRoomRepository;
import com.ssg.bidssgket.user.domain.member.api.chat.service.ChatMessageService;
import com.ssg.bidssgket.user.domain.member.api.chat.service.ChatRoomService;
import com.ssg.bidssgket.user.domain.member.api.googleLogin.SessionMember;
import com.ssg.bidssgket.user.domain.member.domain.Member;
import com.ssg.bidssgket.user.domain.member.domain.repository.MemberRepository;
import com.ssg.bidssgket.user.domain.product.domain.Product;
import com.ssg.bidssgket.user.domain.product.domain.repository.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("chat")
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRoomService chatRoomService;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageService chatMessageService;
    private final ChatRoomRepository chatRoomRepository;

    @GetMapping
    public String getChatPage(Model model, HttpServletRequest request) {
        SessionMember sessionMember = (SessionMember) request.getSession().getAttribute("member");
        Member member = memberRepository.findByEmail(sessionMember.getEmail()).orElseThrow();

        List<ChatRoomMember> chatRoomMembers = member.getChatRoomMembers();
        List<ChatRoom> chatRooms = new ArrayList<>();
        chatRoomMembers.forEach(chatRoomMember -> chatRooms.add(chatRoomMember.getChatRoom()));

        model.addAttribute("chatRoomMembers", chatRoomMembers);
        model.addAttribute("chatRooms", chatRooms);
        model.addAttribute("memberNo", member.getMemberNo());
        return "/user/member/chat";
    }

    @GetMapping("/{chatRoomMemberNo}")
    public String getMessagePage(@PathVariable Long chatRoomMemberNo, Model model, HttpServletRequest request) { // 각 채팅방별 내용 불러오기
        SessionMember sessionMember = (SessionMember) request.getSession().getAttribute("member");
        Member member = memberRepository.findByEmail(sessionMember.getEmail()).orElseThrow();
        List<ChatRoomMember> chatRoomMembers = member.getChatRoomMembers(); //현재 사용자가 속한 모든 채팅방 조회

        ChatRoomMember chatRoomMember = chatRoomMemberRepository.findById(chatRoomMemberNo).orElseThrow();
        ChatRoom chatRoom = chatRoomMember.getChatRoom();

        Long chatRoomNo = chatRoom.getChatRoomNo();

        List<ChatMessage> messages = chatMessageService.findMessagesByChatRoomNo(chatRoomNo);

        Long memberNo = member.getMemberNo();

        String chatRoomName = chatRoom.getName();

        model.addAttribute("chatRoomMembers", chatRoomMembers);
        model.addAttribute("chatRoomName", chatRoomName);
        model.addAttribute("chatRoomNo", chatRoomNo);
        model.addAttribute("messages", messages);
        model.addAttribute("memberNo", memberNo);
        return "/user/member/chat";
    }

    @PostMapping("/start")
    public String startChat(@RequestParam Long productNo, HttpServletRequest request) {
        SessionMember sessionMember = (SessionMember) request.getSession().getAttribute("member");
        Member currentUser = memberRepository.findByEmail(sessionMember.getEmail()).orElseThrow();

        // 상품 조회
        Product product = productRepository.findById(productNo)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productNo));

        // 기존 채팅방 조회
        ChatRoom existingChatRoom = chatRoomService.findByProductNo(productNo);

        if (existingChatRoom != null) {
            // 이미 존재하는 채팅방으로 리디렉션
            return "redirect:/chat/" + existingChatRoom.getChatRoomNo();
        }

        // 채팅방 생성
        ChatRoom newChatRoom = chatRoomService.createRoom(productNo);

        // 판매자와 현재 사용자 채팅방에 추가
        Long sellerId = product.getMember().getMemberNo();
        Long currentUserId = currentUser.getMemberNo();

        chatRoomService.addMember(newChatRoom.getChatRoomNo(), sellerId);
        chatRoomService.addMember(newChatRoom.getChatRoomNo(), currentUserId);

        // 새로 생성된 채팅방으로 리디렉션
        return "redirect:/chat/" + newChatRoom.getChatRoomNo();
    }

}
