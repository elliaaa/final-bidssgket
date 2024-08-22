package com.ssg.bidssgket.user.domain.main.view;

import com.ssg.bidssgket.user.domain.eventAuction.application.EventAuctionService;
import com.ssg.bidssgket.user.domain.product.application.ProductService;
import com.ssg.bidssgket.user.domain.product.domain.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class mainViewController {

    private final ProductService productService;
    private final EventAuctionService eventAuctionService;

    @GetMapping("/")
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser");
        model.addAttribute("isAuthenticated", isAuthenticated);
        List<Product> products = productService.getMainpageProduct();
        model.addAttribute("products", products);
        List<Product> eventProducts = eventAuctionService.getEventProductsMain();
        model.addAttribute("eventProducts", eventProducts);

        return "user/main/mainpage";
    }

    @GetMapping("/auction/auctionMain")
    public String auctionMain(Model model) {
        List<Product> products = productService.getAuctionProducts();
        model.addAttribute("products", products);
        return "user/auction/auctionMain";
    }

    @GetMapping("/search")
    public String searchProducts(Model model,@RequestParam(value = "search", required = false) String search,HttpSession httpSession) {
        List<Product> products;
        if (search != null && !search.isEmpty()) {
            products = productService.searchProducts(search);
        } else {
            products = productService.getAllProducts();
        }
        System.out.println("products = " + products.toString());
        model.addAttribute("products", products);
        SessionMember sessionMember = (SessionMember) httpSession.getAttribute("member");
        List<Long> wishedProductIds = new ArrayList<>();

        if (sessionMember != null) {
            MemberDTO member = auctionService.getMemberByEmail(sessionMember.getEmail());
            wishedProductIds = productWishService.findProductNoByMemberNo(member.getMemberNo());
            System.out.println("wishedProductIds = " + wishedProductIds);
        }
        model.addAttribute("wishedProductIds", wishedProductIds);
        return "user/product/list";
    }
}
