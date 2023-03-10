package com.application.fusamate.rest.controller;
import com.application.fusamate.dto.ColorDto;
import com.application.fusamate.dto.UpdateColorDto;
import com.application.fusamate.entity.Color;
import com.application.fusamate.model.ColorSearchCriteriaModel;
import com.application.fusamate.model.ResponseChangeDataModel;
import com.application.fusamate.model.ResponseGetDataModel;
import com.application.fusamate.service.ColorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/color")
@RequiredArgsConstructor
@CrossOrigin("*")
@Validated
public class ColorController {
    private final ColorService colorService;
    @GetMapping("/get-detail/{id}")
    public ResponseEntity<?> getColor(@PathVariable(name="id") Integer id){
        return ResponseEntity.ok().body(colorService.getColor(id));
    }
    @PostMapping("/get-list")
    public ResponseEntity<?> getColors(@RequestBody ColorSearchCriteriaModel colorSearchCriteriaModel) {
        Page<Color> pageElements = colorService.getColors(colorSearchCriteriaModel);
        return ResponseEntity.ok().body(new ResponseGetDataModel(pageElements.getContent(),pageElements.getNumber(), pageElements.getTotalElements(), pageElements.getTotalPages()));
    }
    @PostMapping("/create")
    public ResponseEntity<?> createColor(@RequestBody ColorDto colorDto) throws Exception {
        return ResponseEntity.ok().body(
                new ResponseChangeDataModel(colorService.createColor(colorDto), HttpStatus.OK.value()));

    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateColor(@PathVariable(value = "id") Integer id, @RequestBody UpdateColorDto colorDto) throws Exception {
        return ResponseEntity.ok().body(new ResponseChangeDataModel( colorService.updateColor(colorDto, id),HttpStatus.OK.value()));
    }
    @GetMapping("/get-all")
    public ResponseEntity<?> getAllActiveColor() {
        return ResponseEntity.ok().body(colorService.getAllColor());
    }

}
