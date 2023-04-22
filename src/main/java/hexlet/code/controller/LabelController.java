package hexlet.code.controller;

import hexlet.code.domain.dto.LabelRequestDTO;
import hexlet.code.domain.dto.LabelResponseDTO;
import hexlet.code.domain.mapper.LabelModelMapper;
import hexlet.code.domain.model.Label;
import hexlet.code.service.LabelService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api")
public class LabelController {

    private final LabelService labelService;
    private final LabelModelMapper labelMapper;

    public LabelController(LabelService labelService,
                           LabelModelMapper labelMapper) {
        this.labelService = labelService;
        this.labelMapper = labelMapper;
    }

    @GetMapping(path = "/labels")
    public List<LabelResponseDTO> findAllLabels() {
        Iterable<Label> existedLabels = labelService.findAllLabels();
        List<LabelResponseDTO> labelDTOList = new ArrayList<>();
        existedLabels.forEach(label -> labelDTOList.add(labelMapper.toLabelResponseDTO(label)));
        labelDTOList.sort(Comparator.comparing(LabelResponseDTO::getId));
        return labelDTOList;
    }

    @GetMapping(path = "/labels/{id}")
    public LabelResponseDTO findLabelById(@PathVariable(name = "id") long id) {
        Label existedLabel = labelService.findLabelById(id);
        return labelMapper.toLabelResponseDTO(existedLabel);
    }

    @PostMapping(path = "/labels")
    public LabelResponseDTO createLabel(@RequestBody @Valid LabelRequestDTO dto) {
        Label createdLabel = labelService.createLabel(dto);
        return labelMapper.toLabelResponseDTO(createdLabel);
    }

    @PutMapping(path = "/labels/{id}")
    public LabelResponseDTO updateLabel(@RequestBody @Valid LabelRequestDTO dto,
                                        @PathVariable(name = "id") long id) {
        Label updatedLabel = labelService.updateLabel(id, dto);
        return labelMapper.toLabelResponseDTO(updatedLabel);
    }

    @DeleteMapping(path = "/labels/{id}")
    public void deleteLabel(@PathVariable(name = "id") long id) {
        labelService.deleteLabel(id);
    }
}
