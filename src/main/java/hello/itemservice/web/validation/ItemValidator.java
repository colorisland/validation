package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

// 스프링 빈에 등록.
@Component
public class ItemValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        // 파라미터로 넘어온 clazz 가 이 Validator 를 쓸 수 있는지 식별.
        // Item 의 자식클래스가 clazz 로 넘어와도 true 반환.
        return Item.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        // errors 는 BindingResult 의 부모클래스로, 실제로 우리 코드에서는 BindingResult 가 넘어온다.
        // 자식클래스일 수도 있으니까 캐스팅하기.
        Item item = (Item) target;

        // 컨트롤러의 검증로직을 여기로 옮겼다.
        // 검증 로직.(단일)
        if (!StringUtils.hasText(item.getItemName())) {
            // 에러메시지 자동화. 이렇게 전달하면 required.itemName 이런식으로 에러메시지에 접근하는 것이다.
            errors.rejectValue("itemName","required");
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            // bindingResult.addError(new FieldError("item", "price", item.getPrice(), false, new String[]{"range.item.price"}, new Object[]{1000,1000000}, null));
            errors.rejectValue("price", "range",new Object[]{1000,1000000},null);
        }
        if (item.getQuantity() == null || item.getQuantity() > 9999) {
            errors.rejectValue("quantity", "max",new Object[]{9999},null);
        }

        // 복합 검증.
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                // 특정 필드에러가 아니고 글로벌오류라면 ObjectError 를 사용한다.
                // bindingResult.addError(new ObjectError("item",new String[]{"totalPriceMin"},new Object[]{10000,resultPrice}, null));
                errors.reject("totalPriceMin",new Object[]{10000,resultPrice}, null);
            }
        }


    }
}
