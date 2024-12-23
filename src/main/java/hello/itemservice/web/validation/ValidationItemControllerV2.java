package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/validation/v2/items")
@RequiredArgsConstructor
public class ValidationItemControllerV2 {

    // (복습)이 결과는 생성자는 1개이고 파라미터가 2개인 형태가 된다.
    private final ItemRepository itemRepository;
    private final ItemValidator itemValidator;

    // 컨트롤러 요청 받을 때마다 webDataBinder 가 새로 만들어진다.
    @InitBinder
    public void init(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(itemValidator);
    }

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v2/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v2/addForm";
    }

    // WebDataBinder 사용
    @PostMapping("/add")
    public String addItemV6(@Validated @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        // Validated 애노테이션을 사용해서 item 에 자동으로 검증기능을 붙일 수 있다.

        // 검증에 실패하면 다시 입력 폼으로 이동.
        // bindingResult 를 사용하면 에러가 있는지 hasError 로 확인할 수 있고, model 에 안담아줘도 자동으로 뷰 파일에 넘어간다.
        if (bindingResult.hasErrors()) {
            log.error("item {}",item.getPrice());
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    // Validator 적용.
//    @PostMapping("/add")
    public String addItemV5(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        // 내가 만든 validator 호출.
        itemValidator.validate(item,bindingResult);

        // 검증에 실패하면 다시 입력 폼으로 이동.
        // bindingResult 를 사용하면 에러가 있는지 hasError 로 확인할 수 있고, model 에 안담아줘도 자동으로 뷰 파일에 넘어간다.
        if (bindingResult.hasErrors()) {
            log.error("item {}",item.getPrice());
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

//    @PostMapping("/add")
    public String addItemV4(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        // 검증에 실패하면 다시 입력 폼으로 이동.
        // bindingResult 를 사용하면 에러가 있는지 hasError 로 확인할 수 있고, model 에 안담아줘도 자동으로 뷰 파일에 넘어간다.
        if (bindingResult.hasErrors()) {
            log.error("item {}",item.getPrice());
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

//    @PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        // 검증 로직.(단일)
        if (!StringUtils.hasText(item.getItemName())) {
            // 아래처럼 하면 검증 실패한 필드가 뷰에서 채워져서 보인다. item.getItemName() 전달한다면 실패 후 뷰에서 해당필드 인풋에 초기화 안돼..
            // code 파라미터가 배열 형태인 이유는 첫번째 코드없으면 그다음거 쓰려고 하는 것이다.
            bindingResult.addError(new FieldError("item", "itemName", item.getItemName(), false, new String[]{"required.item.itemName", "required.default"}, null, null));
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.addError(new FieldError("item", "price", item.getPrice(), false, new String[]{"range.item.price"}, new Object[]{1000,1000000}, null));
        }
        if (item.getQuantity() == null || item.getQuantity() > 9999) {
            bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(), false, new String[]{"max.item.quantity"}, new Object[]{9999}, null));
        }

        // 복합 검증.
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                // 특정 필드에러가 아니고 글로벌오류라면 ObjectError 를 사용한다.
                bindingResult.addError(new ObjectError("item",new String[]{"totalPriceMin"},new Object[]{10000,resultPrice}, null));
            }
        }

        // 검증에 실패하면 다시 입력 폼으로 이동.
        // bindingResult 를 사용하면 에러가 있는지 hasError 로 확인할 수 있고, model 에 안담아줘도 자동으로 뷰 파일에 넘어간다.
        if (bindingResult.hasErrors()) {
            log.error("item{}",item.getPrice());
            return "validation/v2/addForm";
        }
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

//    @PostMapping("/add")
    public String addItemV2(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        // 검증 로직.(단일)
        if (!StringUtils.hasText(item.getItemName())) {
            // 아래처럼 하면 검증 실패한 필드가 뷰에서 채워져서 보인다. item.getItemName() 전달한다면 실패 후 뷰에서 해당필드 인풋에 초기화 안돼..
            // bindingResult.addError(new FieldError("item", "itemName", item.getItemName(), false, null, null, "상품이름은 필수입니다."));

            bindingResult.addError(new FieldError("item", "itemName", "상품이름은 필수입니다."));
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.addError(new FieldError("item", "price", "가격은 1000원에서 100만원까지입니다."));
        }
        if (item.getQuantity() == null || item.getQuantity() > 9999) {
            bindingResult.addError(new FieldError("item", "quantity", "수량은 9999개까지 허용합니다."));
        }

        // 복합 검증.
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                // 특정 필드에러가 아니고 글로벌오류라면 ObjectError 를 사용한다.
                bindingResult.addError(new ObjectError("item", "가격 * 수량의 합은 10000원 이상이어야 합니다. 현재 값 = "+resultPrice));
            }
        }

        // 검증에 실패하면 다시 입력 폼으로 이동.
        // bindingResult 를 사용하면 에러가 있는지 hasError 로 확인할 수 있고, model 에 안담아줘도 자동으로 뷰 파일에 넘어간다.
        if (bindingResult.hasErrors()) {
            log.error("item{}",item.getPrice());
            return "validation/v2/addForm";
        }
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v2/items/{itemId}";
    }

}

