package com.ssg.bidssgket.user.domain.member.view.DTO;

import com.ssg.bidssgket.user.domain.member.domain.Wish;
import com.ssg.bidssgket.user.domain.product.view.dto.response.ProductResDto;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WishDto {

    private Long wishNo;
    private Long memberNo;
    private ProductDto productDto;

    @Builder
    public WishDto(Long wishNo, Long memberNo, Long productNo, ProductDto productDto) {
        this.wishNo = wishNo;
        this.memberNo = memberNo;
        this.productDto = productDto;
    }

    public static WishDto toWishDto(Wish wish) {
        WishDto wishDto = WishDto.builder()
                .wishNo(wish.getWishNo())
                .memberNo(wish.getMember().getMemberNo())
                .build();
        ProductDto productDto = ProductDto.toProductDto(wish.getProduct());
        wishDto.setProductDto(productDto);
        return wishDto;
    }

    private void setProductDto(ProductDto productDto) {
        this.productDto = productDto;
    }

}
