package hello.itemservice.validation;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.MessageCodesResolver;

public class MessageCodesResolverTest {
    MessageCodesResolver codesResolver = new DefaultMessageCodesResolver();

    @Test
    void messageCodesResolverObject() {
        // 파라미터 : 오류코드이름, 객체이름
        String[] messageCodes = codesResolver.resolveMessageCodes("required", "item");
        for (String messageCode : messageCodes) {
            System.out.println("messageCode: " + messageCode);
        }

        // 검증. containsExactly 는 배열 내 원소의 순서까지 맞아야 한다.
        Assertions.assertThat(messageCodes).containsExactly("required", "required.item");
    }

    @Test
    void messageCodesResloverField() {
        // 필드수준까지 더 세밀하게 거르기.
        String[] messageCodes = codesResolver.resolveMessageCodes("required", "item", "itemName", String.class);

        for (String messageCode : messageCodes) {
            System.out.println("messageCode: " + messageCode);
        }
        // 4가지를 만들어준다.
        //messageCode: required.item.itemName
        //messageCode: required.itemName
        //messageCode: required.java.lang.String
        //messageCode: required

        // messageCodes 가 bindingResult.rejectValue 내부에서 사용된다.

        // 검증.
        Assertions.assertThat(messageCodes).containsExactly(
                "required.item.itemName"
                , "required.itemName"
                , "required.java.lang.String"
                , "required");
    }
}
