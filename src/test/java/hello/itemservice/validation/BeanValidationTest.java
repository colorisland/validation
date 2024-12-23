package hello.itemservice.validation;

import hello.itemservice.domain.item.Item;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class BeanValidationTest {

    @Test
    void beanValidation() {
        // 기본 Validator 공장에서 새로운 Validator를 가져온다.
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        // 유효성에 안맞는 item 객체 만들기.
        Item item = new Item(" ",0,10000);

        // 검증하면 위반사항이 set 형태로 나온다.
        Set<ConstraintViolation<Item>> violations = validator.validate(item);
        for (ConstraintViolation<Item> violation : violations) {
            System.out.println("violation: "+ violation);
            System.out.println("violation = " + violation.getMessage());
        }
    }
}
