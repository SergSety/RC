package by.nca.rc.controllers;

import by.nca.rc.models.payload.CreateChangeNewsRequest;
import by.nca.rc.models.payload.NewsResponse;
import by.nca.rc.services.NewsService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static by.nca.rc.util.ErrorsResponse.errorsResponse;

@Slf4j
@RestController
@RequestMapping("/news")
@CrossOrigin(origins="*")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {

        this.newsService = newsService;
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN') or hasRole('JOURNALIST')")
    public ResponseEntity<HttpStatus> createNews(@RequestBody @Valid CreateChangeNewsRequest createChangeNewsRequest,
                                                 BindingResult bindingResult) {

        if (bindingResult.hasErrors()) errorsResponse(bindingResult);

        newsService.createNews(createChangeNewsRequest);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

// We can add sorting by others parameters + direction (The user must install)
    @GetMapping()
    public ResponseEntity<NewsResponse> findAll(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "3") Integer size) {

        return new ResponseEntity<>(newsService.findAll(page, size), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @methodAccess.hasCustomerIdForNews(#id)")
    public ResponseEntity<HttpStatus> changeNews(@PathVariable("id") Long id,
                                        @RequestBody @Valid CreateChangeNewsRequest createChangeNewsRequest,
                                        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) errorsResponse(bindingResult);

        newsService.changeNews(createChangeNewsRequest, id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping({"/{id}"})
    @PreAuthorize("hasRole('ADMIN') or @methodAccess.hasCustomerIdForNews(#id)")
    public ResponseEntity<HttpStatus> deleteNews(@PathVariable("id") Long id) {
        newsService.deleteNews(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // This endpoint has been added to check the cache health
    @GetMapping({"/{id}"})
    public ResponseEntity<?> findById(@PathVariable("id") Long id) {

        log.info("Fetching news by id: {}", id);

        newsService.findById(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<NewsResponse> findNewsBySearchText(@RequestParam String searchText) {

        return new ResponseEntity<>(newsService.findNewsBySearchText(searchText), HttpStatus.OK);
    }
}
