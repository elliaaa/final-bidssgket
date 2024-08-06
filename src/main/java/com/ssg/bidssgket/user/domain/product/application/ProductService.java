package com.ssg.bidssgket.user.domain.product.application;

import com.ssg.bidssgket.global.util.ncps3.FileDto;
import com.ssg.bidssgket.global.util.ncps3.FileService;
import com.ssg.bidssgket.user.domain.product.api.dto.request.RegistProductReqDto;
import com.ssg.bidssgket.user.domain.product.domain.Category;
import com.ssg.bidssgket.user.domain.product.domain.Product;
import com.ssg.bidssgket.user.domain.product.domain.ProductImage;
import com.ssg.bidssgket.user.domain.product.domain.Sales_status;
import com.ssg.bidssgket.user.domain.product.domain.repository.ProductImageRepository;
import com.ssg.bidssgket.user.domain.product.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final FileService fileService;

    @Transactional
    public Product registProduct(RegistProductReqDto registProductReqDto, List<MultipartFile> productImages) {
        Product product = Product.builder()
                .productName(registProductReqDto.getProductName())
                .category(Category.valueOf(registProductReqDto.getCategory()))
                .salesStatus(Sales_status.selling)
                .productDesc(registProductReqDto.getProductDesc())
                .imdPurchase(registProductReqDto.getImdPurchase())
                .auctionSelected(registProductReqDto.getAuctionSelected())
                .eventAuction(registProductReqDto.getEventAuction())
                .buyNowPrice(registProductReqDto.getBuyNowPrice())
                .auctionStartPrice(registProductReqDto.getAuctionStartPrice())
                .auctionStartTime(registProductReqDto.getAuctionStartTime())
                .auctionEndTime(registProductReqDto.getAuctionEndTime())
                .build();

        productRepository.save(product);

        // 2. 파일 업로드 및 이미지 정보 저장
        List<FileDto> fileDtos = fileService.uploadFiles(productImages, "product-images");
        for (FileDto fileDto : fileDtos) {
            ProductImage productImage = ProductImage.builder()
                    .productImg(fileDto.getUploadFileUrl())
                    .productThumbnail(false) // 필요에 따라 썸네일 여부 설정
                    .product(product)
                    .build();
            productImageRepository.save(productImage);
        }
        return product;
    }
}
