package will.dev.artisan_des_saveurs.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import will.dev.artisan_des_saveurs.dto.MessageRetourDto;
import will.dev.artisan_des_saveurs.dto.order.OrderDTO;
import will.dev.artisan_des_saveurs.service.OrderService;

@RestController
@RequestMapping("orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/home")
    public String hello() {
        System.out.println("✅ /api/orders/test atteint !");
        return "Toutes les commande du client ✅";
    }

    @PostMapping("place-order")
    public ResponseEntity<MessageRetourDto> createUser(@RequestBody OrderDTO orderDTO) {
        return this.orderService.sendOrder(orderDTO);
    }
}
