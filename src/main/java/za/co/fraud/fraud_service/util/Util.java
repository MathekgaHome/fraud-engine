package za.co.fraud.fraud_service.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public final class Util {

    public static Pageable getPageable(final int page, final int size, final Sort.Direction direction, final String property) {
        return PageRequest.of(page, size, direction, property);
    }
}
