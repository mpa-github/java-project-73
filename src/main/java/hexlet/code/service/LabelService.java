package hexlet.code.service;

import hexlet.code.domain.dto.LabelRequestDTO;
import hexlet.code.domain.mapper.LabelModelMapper;
import hexlet.code.domain.model.Label;
import hexlet.code.exception.NotFoundException;
import hexlet.code.repository.LabelRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LabelService {

    private final LabelRepository labelRepository;
    private final LabelModelMapper labelMapper;

    public LabelService(LabelRepository labelRepository,
                        LabelModelMapper labelMapper) {
        this.labelRepository = labelRepository;
        this.labelMapper = labelMapper;
    }

    public List<Label> findAllLabels() {
        return labelRepository.findAllByOrderByIdAsc();
    }

    public List<Label> findAllLabelsById(List<Long> labelIds) {
        return labelRepository.findAllById(labelIds);
    }

    // TODO For Hibernate request optimization
    public List<Label> getAllLabelReferencesById(List<Long> labelIds) {
        return labelIds.stream()
            .map(labelRepository::getReferenceById)
            .collect(Collectors.toList());
    }

    public Label findLabelById(long id) {
        return labelRepository.findLabelById(id)
            .orElseThrow(() -> new NotFoundException("Label with id='%d' not found!".formatted(id)));
    }

    public Label createLabel(LabelRequestDTO dto) {
        Label newLabel = labelMapper.toLabelModel(dto);
        return labelRepository.save(newLabel);
    }

    public Label updateLabel(long id, LabelRequestDTO dto) {
        Label labelToUpdate = findLabelById(id);
        labelMapper.updateLabelModel(labelToUpdate, dto);
        return labelRepository.save(labelToUpdate);
    }

    public void deleteLabel(long id) {
        Label existedLabel = findLabelById(id);
        labelRepository.delete(existedLabel);
    }
}
