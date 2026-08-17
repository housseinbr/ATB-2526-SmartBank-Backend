package tn.SmartBank.ATB_2526_SmartBank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.SmartBank.ATB_2526_SmartBank.entity.Mobilite;
import tn.SmartBank.ATB_2526_SmartBank.repository.MobiliteRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MobiliteService {

    private final MobiliteRepository mobiliteRepository;

    @Transactional(readOnly = true)
    public List<Mobilite> getAll() {
        return mobiliteRepository.findAllByOrderByDateDesc();
    }

    @Transactional(readOnly = true)
    public Mobilite getById(Long id) {
        return mobiliteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mobilite introuvable avec l'id : " + id));
    }

    public Mobilite save(Mobilite mobilite) {
        return mobiliteRepository.save(mobilite);
    }

    public void delete(Long id) {
        mobiliteRepository.delete(getById(id));
    }
}
