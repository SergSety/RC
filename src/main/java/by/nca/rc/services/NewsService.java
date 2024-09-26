package by.nca.rc.services;

import by.nca.rc.dto.NewsDto;
import by.nca.rc.exceptions.AlreadyExistException;
import by.nca.rc.exceptions.NotFoundException;
import by.nca.rc.models.News;
import by.nca.rc.models.Remark;
import by.nca.rc.models.payload.CreateChangeNewsRequest;
import by.nca.rc.models.payload.NewsResponse;
import by.nca.rc.repositories.NewsRepository;
import by.nca.rc.security.CustomerDetails;
import by.nca.rc.util.Converter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@Transactional(readOnly = true)
public class NewsService {

    private final NewsRepository newsRepository;
    private final Converter converter;

    public NewsService(NewsRepository newsRepository, Converter converter) {
        this.newsRepository = newsRepository;
        this.converter = converter;
    }

    @Transactional
    public void createNews(CreateChangeNewsRequest createChangeNewsRequest) {

        if (newsRepository.findNewsByTitle(createChangeNewsRequest.getTitle()).isPresent())
            throw  new AlreadyExistException("This title is already taken");

        News news = new News();

        news.setTitle(createChangeNewsRequest.getTitle());
        news.setText(createChangeNewsRequest.getText());
        news.setCreationDate(new Date());
        news.setLastEditDate(new Date());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomerDetails customerDetails = (CustomerDetails) authentication.getPrincipal();
        news.setInsertedById(customerDetails.getId());
        news.setUpdatedById(customerDetails.getId());

        List<Remark> comments = new ArrayList<>();
        news.setRemarks(comments);

        newsRepository.save(news);
    }

    public NewsResponse findAll(Integer page, Integer size) {

        Pageable paging = PageRequest.of(page, size, Sort.by("title").ascending());

        Page<News> pageNews = newsRepository.findAll(paging);

        List<News> news = pageNews.getContent();

        List<NewsDto> content = new ArrayList<>();
        news.forEach(a -> content.add(converter.convertToNewsDto(a)));

        Map<String, Object> response = new HashMap<>();
        response.put("news", content);
        response.put("currentPage", pageNews.getNumber());
        response.put("totalItems", pageNews.getTotalElements());
        response.put("totalPages", pageNews.getTotalPages());
        response.put("last", pageNews.isLast());

        NewsResponse newsResponse = new NewsResponse();
        newsResponse.setNewsResponse(response);

        return newsResponse;
    }

    @Transactional
    public void changeNews(CreateChangeNewsRequest createChangeNewsRequest, Long id) {

        News newsToBeUpdated = newsRepository.findById(id).orElseThrow(() -> new NotFoundException("News not found"));

        if (newsRepository.findNewsByTitle(createChangeNewsRequest.getTitle()).isPresent() &&
                !newsRepository.findNewsByTitle(createChangeNewsRequest.getTitle()).get().getTitle()
                        .equals(newsToBeUpdated.getTitle()))
            throw  new AlreadyExistException("This title is already taken");

        if (newsRepository.findNewsByText(createChangeNewsRequest.getText()).isPresent() &&
                !newsRepository.findNewsByText(createChangeNewsRequest.getText()).get().getText()
                        .equals(newsToBeUpdated.getText()))
            throw  new AlreadyExistException("Such a text already exists");

        News news = new News();

        news.setId(id);
        news.setTitle(createChangeNewsRequest.getTitle());
        news.setText(createChangeNewsRequest.getText());
        news.setCreationDate(newsToBeUpdated.getCreationDate());
        news.setLastEditDate(new Date());
        news.setInsertedById(newsToBeUpdated.getInsertedById());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomerDetails customerDetails = (CustomerDetails) authentication.getPrincipal();
        news.setUpdatedById(customerDetails.getId());

        news.setRemarks(newsToBeUpdated.getRemarks());

        newsRepository.save(news);
    }

    @Transactional
    public void deleteNews(Long id) {

        newsRepository.deleteById(id);
    }

    // This method has been added to check the cache health
    public void findById(Long id) {

        log.info("Fetching the news {}", id);
        News news = newsRepository.findById(id).orElseThrow(() -> new NotFoundException("News not found"));
        log.info("Successfully fetched news {}", news.getTitle());
    }

    public NewsResponse findNewsBySearchText(String searchText) {

        List<News> news = newsRepository.findNewsBySearchText(searchText);

        List<NewsDto> content = new ArrayList<>();
        news.forEach(a -> content.add(converter.convertToNewsDto(a)));

        Map<String, Object> response = new HashMap<>();
        response.put("Search Results:", content);

        NewsResponse newsResponse = new NewsResponse();
        newsResponse.setNewsResponse(response);

        return newsResponse;
    }
}
