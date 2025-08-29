package will.dev.artisan_des_saveurs.dtoMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import will.dev.artisan_des_saveurs.dto.order.OrderDTO;
import will.dev.artisan_des_saveurs.entity.Order;
import will.dev.artisan_des_saveurs.entity.ProductItem;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderMapper {
    private final ProductItemMapper productItemMapper;

    public OrderDTO toDTO(Order order) {
        if (order == null) return null;

        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setSubtotal(order.getSubtotal());
        dto.setDiscount(order.getDiscount());
        dto.setTotal(order.getTotal());
        dto.setStatus(order.getDelivered());
        dto.setFreeShipping(order.isFreeShipping());
        dto.setUser(UserDtoMapper.toDto(order.getUser()));

        if (order.getItems() != null) {
            dto.setItems(order.getItems().stream()
                    .map(productItemMapper::toDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public Order toEntity(OrderDTO dto) {
        if (dto == null) return null;

        Order order = new Order();
        order.setId(dto.getId());
        order.setSubtotal(dto.getSubtotal());
        order.setDiscount(dto.getDiscount());
        order.setTotal(dto.getTotal());
        order.setFreeShipping(dto.isFreeShipping());
        order.setUser(UserDtoMapper.toEntity(dto.getUser()));

        if (dto.getItems() != null) {
            List<ProductItem> items = dto.getItems().stream()
                    .map(productItemMapper::toEntity)
                    .peek(item -> item.setOrder(order)) // assigner la commande
                    .collect(Collectors.toList());
            order.setItems(items);
        }

        return order;
    }
}

