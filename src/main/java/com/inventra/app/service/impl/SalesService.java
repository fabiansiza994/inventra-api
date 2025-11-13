package com.inventra.app.service.impl;

import com.inventra.app.config.exceptions.CustomServiceException;
import com.inventra.app.entity.Sale;
import com.inventra.app.entity.SaleDetail;
import com.inventra.app.entity.dto.ClientDto;
import com.inventra.app.entity.dto.ProductDto;
import com.inventra.app.entity.dto.SalesDto;
import com.inventra.app.repository.SalesRepository;
import com.inventra.app.repository.SaleDetailRepository;
import com.inventra.app.repository.ProductRepository;
import com.inventra.app.service.IClientService;
import com.inventra.app.service.IProductService;
import com.inventra.app.service.ISalesService;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class SalesService implements ISalesService {

    private static final Logger logger = LogManager.getLogger(SalesService.class);
    private final SalesRepository salesRepository;
    private final IClientService clientService;
    private final IProductService productService;
    private final SaleDetailRepository saleDetailRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public SalesService(SalesRepository salesRepository, IClientService clientService, IProductService productService,
                        SaleDetailRepository saleDetailRepository, ProductRepository productRepository, ModelMapper modelMapper) {
        this.salesRepository = salesRepository;
        this.clientService = clientService;
        this.productService = productService;
        this.saleDetailRepository = saleDetailRepository;
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public SalesDto findById(Long id) {
        var sale = salesRepository.findById(id)
                .orElseThrow(() -> new CustomServiceException("123", "E010", "Venta con id " + id + " no encontrada"));
        return modelMapper.map(sale, SalesDto.class);
    }

    @Override
    public SalesDto getSaleDetail(Long id) {
        var sale = salesRepository.findById(id)
                .orElseThrow(() -> new CustomServiceException("123", "E010", "Venta con id " + id + " no encontrada"));
        SalesDto dto = modelMapper.map(sale, SalesDto.class);
        List<SaleDetail> details = saleDetailRepository.findBySaleId_Id(sale.getId());
        var prodDtos = details.stream().map(d -> {
            ProductDto p = modelMapper.map(d.getProductId(), ProductDto.class);
            p.setQuantity(d.getQuantity());
            return p;
        }).toList();
        dto.setProductList(prodDtos);
        return dto;
    }

    @Override
    public Page<SalesDto> listSales(String date, Pageable pageable) {
        logger.info("** Listing sales paginated. date filter: {} **", date);
        Page<Sale> page;
        if (date == null || date.trim().isEmpty()) {
            page = salesRepository.findAll(pageable);
        } else {
            page = salesRepository.findByDateContaining(date, pageable);
        }

        List<SalesDto> dtos = page.getContent().stream().map(sale -> {
            SalesDto dto = modelMapper.map(sale, SalesDto.class);
            dto.setDate(Instant.parse(sale.getDate()));
            // obtener detalles de venta y mapear products con cantidad
            List<SaleDetail> details = saleDetailRepository.findBySaleId_Id(sale.getId());
            List<ProductDto> prodDtos = details.stream().map(detail -> {
                ProductDto p = modelMapper.map(detail.getProductId(), ProductDto.class);
                p.setQuantity(detail.getQuantity());
                return p;
            }).toList();
            dto.setProductList(prodDtos);
            return dto;
        }).toList();

        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    @Transactional
    @Override
    public SalesDto createSale(SalesDto salesDTO) {
        logger.info("** Creating new sale **");
        var client = validateClient(salesDTO.getClient().getId());

        List<ProductDto> products = salesDTO.getProductList();
        if (products == null || products.isEmpty()) {
            logger.error("** Sale must contain at least one product **");
            throw new CustomServiceException("123", "E004", "Sale must contain at least one product");
        }

        for (ProductDto requested : products) {
            if (requested == null || requested.getId() == null) {
                logger.error("** Product invalid in request **");
                throw new CustomServiceException("123", "E005", "Product invalid in request");
            }

            ProductDto product = productService.findById(requested.getId());
            if (product == null || product.getId() == null) {
                logger.error("** Product with id " + requested.getId() + " not found **");
                throw new CustomServiceException("123", "E005", "Product with id " + requested.getId() + " not found");
            }

            if (!"AVAILABLE".equalsIgnoreCase(product.getStatus())) {
                logger.error("** Product with id " + product.getId() + " is not available for sale **");
                throw new CustomServiceException("123", "E002", "Product with id " + product.getId() + " is not available for sale");
            }

            Integer requestedQty = requested.getQuantity();
            Integer availableQty = product.getQuantity();

            if (requestedQty == null || requestedQty <= 0) {
                logger.error("** Quantity solicited is invalid " + product.getId());
                throw new CustomServiceException("123", "E006", "Quantity solicited is invalid " + product.getId());
            }

            if (availableQty == null) {
                logger.error("** Error in product stock " + product.getId());
                throw new CustomServiceException("123", "E007", "Error in product stock " + product.getId());
            }

            if (requestedQty > availableQty) {
                logger.error("** There is not enough quantity of the product " + product.getId() + ". Available: " + availableQty);
                throw new CustomServiceException("123", "E003", "There is not enough quantity of the product " + product.getId() + ". Available: " + availableQty);
            }

            int newQty = availableQty - requestedQty;
            product.setQuantity(newQty);
            productService.update(product);
        }

        logger.info("** Sale created **");
        salesDTO.setClient(client);
        salesDTO.setDate(new java.util.Date().toInstant());
        salesDTO.setStatus("COMPLETE");
        return save(salesDTO);
    }

    @Transactional
    @Override
    public void updateSaleStatus(Long id, String status) {
        logger.info("** Updating sale status for id {} to {} **", id, status);
        var sale = salesRepository.findById(id)
                .orElseThrow(() -> new CustomServiceException("123", "E010", "Venta con id " + id + " no encontrada"));

        sale.setStatus(status);
        salesRepository.save(sale);

        if ("REJECTED".equalsIgnoreCase(status)) {
            // recuperar detalles y devolver cantidad al stock
            List<SaleDetail> details = saleDetailRepository.findBySaleId_Id(sale.getId());
            for (SaleDetail d : details) {
                if (d.getProductId() == null) continue;
                Integer qty = d.getQuantity() == null ? 0 : d.getQuantity();
                var prodOpt = productRepository.findById(d.getProductId().getId());
                if (prodOpt.isPresent()) {
                    var prod = prodOpt.get();
                    Integer current = prod.getQuantity() == null ? 0 : prod.getQuantity();
                    prod.setQuantity(current + qty);
                    productRepository.save(prod);
                }
            }
        }
    }

    @Override
    public SalesDto save(SalesDto salesDTO) {
        logger.info("** Saving sale **");
        // Mapear SalesDto a entidad Sale (sin detalles)
        var sale = modelMapper.map(salesDTO, Sale.class);

        // Si la venta incluye productos, asignar el primer producto a la entidad Sale
        // para poblar la columna product_id (mantener compatibilidad con la estructura actual).
        if (salesDTO.getProductList() != null && !salesDTO.getProductList().isEmpty()) {
            var firstPdto = salesDTO.getProductList().get(0);
            var firstProductEntity = productRepository.findById(firstPdto.getId())
                    .orElseThrow(() -> new CustomServiceException("123", "E005", "Product with id " + firstPdto.getId() + " not found"));
            sale.setProduct(firstProductEntity);
        }

        // Guardar la venta primero para obtener el id
        var savedSale = salesRepository.save(sale);

        // Recorrer productos del DTO y persistir SaleDetail
        List<SaleDetail> detailsToSave = salesDTO.getProductList() == null ? List.of() : salesDTO.getProductList().stream().map(pdto -> {
            var productEntity = productRepository.findById(pdto.getId())
                    .orElseThrow(() -> new CustomServiceException("123", "E005", "Product with id " + pdto.getId() + " not found"));
            SaleDetail detail = new SaleDetail();
            detail.setSaleId(savedSale);
            detail.setProductId(productEntity);
            Integer qty = pdto.getQuantity() == null ? 0 : pdto.getQuantity();
            detail.setQuantity(qty);
            Double unitPrice = productEntity.getPrice() == null ? 0.0 : productEntity.getPrice();
            detail.setUnitPrice(unitPrice);
            detail.setTotal(unitPrice * qty);
            return detail;
        }).toList();

        if (!detailsToSave.isEmpty()) {
            saleDetailRepository.saveAll(detailsToSave);
        }

        // Construir SalesDto a devolver con productos y cantidades
        SalesDto resultDto = modelMapper.map(savedSale, SalesDto.class);
        List<ProductDto> prodDtos = detailsToSave.stream().map(d -> {
            ProductDto pd = modelMapper.map(d.getProductId(), ProductDto.class);
            pd.setQuantity(d.getQuantity());
            return pd;
        }).toList();
        resultDto.setProductList(prodDtos);
        return resultDto;
    }

    private ClientDto validateClient(Long clientId){
        if (clientId == null) {
            logger.error("** Client id is required **");
            throw new CustomServiceException("123", "E001", "Client id is required");
        }
        var client = clientService.findById(clientId);
        if (client == null || client.getId() == null){
            logger.error("** Client not found **");
            throw new CustomServiceException("123", "E001", "Client not found");
        }
        return client;
    }
}
