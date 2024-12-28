package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import hello.itemservice.domain.item.SaveCheck;
import hello.itemservice.domain.item.UpdateCheck;
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

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/validation/v3/items")
@RequiredArgsConstructor
public class ValidationItemControllerV3 {

    // (복습)이 결과는 생성자는 1개이고 파라미터가 2개인 형태가 된다.
    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v3/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable("itemId") long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v3/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v3/addForm";
    }

    // WebDataBinder 사용
    @PostMapping("/add")
    public String addItemV2(@Validated(SaveCheck.class) @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        // 복합 검증.
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                // 특정 필드에러가 아니고 글로벌오류라면 ObjectError 를 사용한다.
                // bindingResult.addError(new ObjectError("item",new String[]{"totalPriceMin"},new Object[]{10000,resultPrice}, null));
                bindingResult.reject("totalPriceMin",new Object[]{10000,resultPrice}, null);
            }
        }
        // 검증에 실패하면 다시 입력 폼으로 이동.
        // bindingResult 를 사용하면 에러가 있는지 hasError 로 확인할 수 있고, model 에 안담아줘도 자동으로 뷰 파일에 넘어간다.
        if (bindingResult.hasErrors()) {
            return "validation/v3/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v3/items/{itemId}";
    }

//    @PostMapping("/add")
    public String addItem(@Validated @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        // 복합 검증.
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                // 특정 필드에러가 아니고 글로벌오류라면 ObjectError 를 사용한다.
                // bindingResult.addError(new ObjectError("item",new String[]{"totalPriceMin"},new Object[]{10000,resultPrice}, null));
                bindingResult.reject("totalPriceMin",new Object[]{10000,resultPrice}, null);
            }
        }
        // 검증에 실패하면 다시 입력 폼으로 이동.
        // bindingResult 를 사용하면 에러가 있는지 hasError 로 확인할 수 있고, model 에 안담아줘도 자동으로 뷰 파일에 넘어간다.
        if (bindingResult.hasErrors()) {
            return "validation/v3/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v3/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable("itemId") Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v3/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String editV2(@PathVariable("itemId") Long itemId, @Validated(UpdateCheck.class) @ModelAttribute Item item, BindingResult bindingResult) {
        // 복합 검증.
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                // 특정 필드에러가 아니고 글로벌오류라면 ObjectError 를 사용한다.
                // bindingResult.addError(new ObjectError("item",new String[]{"totalPriceMin"},new Object[]{10000,resultPrice}, null));
                bindingResult.reject("totalPriceMin",new Object[]{10000,resultPrice}, null);
            }
        }
        // 검증에 실패하면 다시 입력 폼으로 이동.
        // bindingResult 를 사용하면 에러가 있는지 hasError 로 확인할 수 있고, model 에 안담아줘도 자동으로 뷰 파일에 넘어간다.
        if (bindingResult.hasErrors()) {
            return "validation/v3/editForm";
        }
        itemRepository.update(itemId, item);
        return "redirect:/validation/v3/items/{itemId}";
    }

//    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable("itemId") Long itemId, @Validated @ModelAttribute Item item, BindingResult bindingResult) {
        // 복합 검증.
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                // 특정 필드에러가 아니고 글로벌오류라면 ObjectError 를 사용한다.
                // bindingResult.addError(new ObjectError("item",new String[]{"totalPriceMin"},new Object[]{10000,resultPrice}, null));
                bindingResult.reject("totalPriceMin",new Object[]{10000,resultPrice}, null);
            }
        }
        // 검증에 실패하면 다시 입력 폼으로 이동.
        // bindingResult 를 사용하면 에러가 있는지 hasError 로 확인할 수 있고, model 에 안담아줘도 자동으로 뷰 파일에 넘어간다.
        if (bindingResult.hasErrors()) {
            return "validation/v3/editForm";
        }
        itemRepository.update(itemId, item);
        return "redirect:/validation/v3/items/{itemId}";
    }

}

