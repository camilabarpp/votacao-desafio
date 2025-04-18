package com.votacao.desafio.service;

import com.votacao.desafio.dto.PautaRequest;
import com.votacao.desafio.entity.Pauta;
import com.votacao.desafio.entity.VotingSession;
import com.votacao.desafio.repository.PautaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PautaService {

    private final PautaRepository pautaRepository;

//    public PautaResponse getPautaWithVotes(Long pautaId) {
//        List<ViewPautaWithVotingSession> records = viewRepo.findAllByPautaId(pautaId);
//
//        if (records.isEmpty()) {
//            return null;
//        }
//
//        // Dados da pauta (iguais em todos os registros)
//        ViewPautaWithVotingSession firstRecord = records.get(0);
//        PautaResponse response = new PautaResponse();
//        response.setId(firstRecord.getPautaId());
//        response.setTitle(firstRecord.getTitle());
//        response.setDescription(firstRecord.getDescription());
//        response.setCreatedAt(firstRecord.getCreatedAt());
//
//        // Se não houver sessão de votação
//        if (firstRecord.getSessaoId() == null) {
//            return response;
//        }
//
//        // Dados da sessão de votação
//        VotingSessionResponse sessionDTO = new VotingSessionResponse();
//        sessionDTO.setVotingSessionStartedAt(firstRecord.getVotingSessionStartedAt());
//        sessionDTO.setVotingSessionEndedAt(firstRecord.getVotingSessionEndedAt());
//        sessionDTO.setVotingSessionOpen(firstRecord.getVotingSessionOpen());
//
//        // Processa votos únicos usando um Map para evitar duplicações
//        Map<Long, VoteResponse> votesMap = new HashMap<>();
//
//        for (ViewPautaWithVotingSession record : records) {
//            if (record.getVotoId() != null) {
//                VoteResponse voteDTO = new VoteResponse();
//                voteDTO.setId(record.getVotoId());
//                voteDTO.setAssociateId(record.getAssociateId());
//                voteDTO.setAssociateName(record.getAssociateName());
//                voteDTO.setAssociateCpf(record.getAssociateCpf());
//                voteDTO.setVotedOption(record.getVotedOption());
//                voteDTO.setVotedAt(record.getVotedAt());
//
//                // Usa o ID do voto como chave para garantir unicidade
//                votesMap.put(record.getVotoId(), voteDTO);
//            }
//        }
//
//        // Converte o Map em uma lista
//        List<VoteResponse> votesList = new ArrayList<>(votesMap.values());
//        sessionDTO.setVotes(votesList);
//
//        // Contagem de votos
//        sessionDTO.setVotesCount((long) votesList.size());

    /// /        sessionDTO.setVotesCountYes((int) votesList.stream()
    /// /                .filter(v -> "YES".equals(v.getVotedOption())).count());
    /// /        sessionDTO.setVotesCountNo((int) votesList.stream()
    /// /                .filter(v -> "NO".equals(v.getVotedOption())).count());
//
//        response.setVoatingSessions(sessionDTO);
//
//        return response;
//    }
    @Transactional
    public Pauta createPauta(PautaRequest pautaRequest) {
        return null;
    }

    @Transactional(readOnly = true)
    public Page<Pauta> listAllPautas(Integer page, Integer size) {
        log.info("Listing all Pautas");
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return pautaRepository.findAll(pageable);
    }

    public Pauta getPautaById(Long pautaId) {
        return pautaRepository.findById(pautaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pauta not found"));
    }

    public void verifyPautaExistence(Long pautaId) {
        if (!pautaRepository.existsById(pautaId)) {
            log.error("Pauta not found with ID: {}", pautaId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pauta not found with ID: " + pautaId);
        }
    }

    public Pauta savePauta(Pauta pauta) {
        return pautaRepository.save(pauta);
    }

//    public Page<VotingSession> listAllPautasWithVotingSessionsClosed(Integer , Integer ) {
//    }
}
