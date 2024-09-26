package by.nca.rc.services;

import by.nca.rc.dto.NewsDto;
import by.nca.rc.dto.RemarkDto;
import by.nca.rc.exceptions.AlreadyExistException;
import by.nca.rc.exceptions.NotFoundException;
import by.nca.rc.models.News;
import by.nca.rc.models.Remark;
import by.nca.rc.models.payload.CreateChangeRemarkRequest;
import by.nca.rc.models.payload.RemarkResponse;
import by.nca.rc.repositories.NewsRepository;
import by.nca.rc.repositories.RemarkRepository;
import by.nca.rc.security.CustomerDetails;
import by.nca.rc.util.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class RemarkService {

    private final RemarkRepository remarkRepository;
    private final NewsRepository newsRepository;
    private final Converter converter;

    public RemarkService(RemarkRepository remarkRepository, NewsRepository newsRepository, Converter converter) {
        this.remarkRepository = remarkRepository;
        this.newsRepository = newsRepository;
        this.converter = converter;
    }

    @Transactional
    public void createRemark(Long id, CreateChangeRemarkRequest createChangeRemarkRequest) {

        if (remarkRepository.findRemarkByText(createChangeRemarkRequest.getText()).isPresent())
            throw  new AlreadyExistException("This text is already taken");

        Remark remark = new Remark();

        remark.setText(createChangeRemarkRequest.getText());
        remark.setCreationDate(new Date());
        remark.setLastEditDate(new Date());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomerDetails customerDetails = (CustomerDetails) authentication.getPrincipal();
        remark.setInsertedById(customerDetails.getId());

        News news = newsRepository.findById(id).orElseThrow(() -> new NotFoundException("News not found"));
        remark.setNews(news);
        remarkRepository.save(remark);
    }

    public RemarkResponse findAllRemarkByNewsId(Long id, Integer page, Integer size) {

        News news = newsRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Not found News with id = " + id));

        NewsDto newsDto = converter.convertToNewsDto(news);

        Pageable paging = PageRequest.of(page, size, Sort.by("creationDate").ascending());

        Page<Remark> pageRemark = remarkRepository.findAllByNewsId(id, paging);

        List<Remark> remarks = pageRemark.getContent();

        List<RemarkDto> content = new ArrayList<>();
        remarks.forEach(a -> content.add(converter.convertToRemarkDto(a)));

        Map<String, Object> response = new HashMap<>();
        response.put("remarks", content);
        response.put("currentPage", pageRemark.getNumber());
        response.put("totalItems", pageRemark.getTotalElements());
        response.put("totalPages", pageRemark.getTotalPages());
        response.put("last", pageRemark.isLast());

        RemarkResponse remarkResponse = new RemarkResponse();
        remarkResponse.setRemark(response);
        remarkResponse.setNews(newsDto);

        return remarkResponse;
    }

    @Transactional
    public void changeRemark(CreateChangeRemarkRequest createChangeRemarkRequest, Long id) {

        Remark remarkToBeUpdated = remarkRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Remark not found"));

        if (remarkRepository.findRemarkByText(createChangeRemarkRequest.getText()).isPresent() &&
                !remarkRepository.findRemarkByText(createChangeRemarkRequest.getText()).get().getText()
                        .equals(remarkToBeUpdated.getText()))
            throw  new AlreadyExistException("Such a text already exists");

        Remark remark = new Remark();

        remark.setId(id);
        remark.setText(createChangeRemarkRequest.getText());
        remark.setCreationDate(remarkToBeUpdated.getCreationDate());
        remark.setLastEditDate(new Date());
        remark.setInsertedById(remarkToBeUpdated.getInsertedById());
        remark.setNews(remarkToBeUpdated.getNews());

        remarkRepository.save(remark);
    }

    @Transactional
    public void deleteRemark(Long id) {

        remarkRepository.deleteById(id);
    }
}
