package hexlet.code.domain.mapper;

import hexlet.code.domain.dto.LabelRequestDTO;
import hexlet.code.domain.dto.LabelResponseDTO;
import hexlet.code.domain.model.Label;
import org.springframework.stereotype.Component;

@Component
public class LabelModelMapper {

    public LabelResponseDTO toLabelResponseDTO(Label label) {
        LabelResponseDTO dto = new LabelResponseDTO();
        dto.setId(label.getId());
        dto.setName(label.getName());
        dto.setCreatedAt(label.getCreatedAt());
        return dto;
    }

    public Label toLabelModel(LabelRequestDTO dto) {
        Label label = new Label();
        updateLabelModel(label, dto);
        return label;
    }

    public void updateLabelModel(Label label, LabelRequestDTO dto) {
        label.setName(dto.getName());
    }
}