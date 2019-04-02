package io.bux.assignment.rest;

import io.bux.assignment.exception.IncorrectPriceRangeException;
import io.bux.assignment.service.TradeService;
import io.bux.assignment.persistence.entity.TradePosition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@Slf4j
public class TradePositionController {

    private TradeService tradeService;

    @Autowired
    public TradePositionController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @PostMapping("/trade")
    @ResponseStatus(HttpStatus.CREATED)
    public TradePosition create(@RequestBody TradePosition position) throws Exception {

        if (!(position.getLowerLimit() < position.getBuyPrice()) || !(position.getBuyPrice() < position.getUpperLimit())) {
            throw new IncorrectPriceRangeException("Limits and price must be lowerLimit < buyPrice < upperLimit");
        }

        return tradeService.createPosition(position);
    }
}
