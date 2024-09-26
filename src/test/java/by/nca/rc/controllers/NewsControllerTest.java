package by.nca.rc.controllers;

import by.nca.rc.exceptions.NotCreatedException;
import by.nca.rc.models.payload.CreateChangeNewsRequest;
import by.nca.rc.services.NewsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NewsControllerTest {

    private final static Long ID = 1L;

    @Mock
    NewsService newsService;
    @Mock
    BindingResult bindingResult;

    @InjectMocks
    NewsController newsController;

    private CreateChangeNewsRequest createChangeNewsRequest;

    @BeforeEach
    public void setup() {

        createChangeNewsRequest = new CreateChangeNewsRequest("Test title", "Test text");
    }

    //<-----------------------------------------------CREATE NEWS  -------------------------------------------------->//

    @Test
    @DisplayName("POST /news: Returns HTTP response with status 201 CREATED")
    void createNews_CreateChangeNewsRequestIsValid_ReturnsValidResponseEntity() {

        // given

        // when
        var responseEntity = this.newsController.createNews(createChangeNewsRequest, bindingResult);

        // then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        verify(newsService).createNews(createChangeNewsRequest);
    }

    @Test
    @DisplayName("POST /news: BindingResult has error")
    public void createNews_BindingResultHasError_InvokeErrorResponse() {

        // Given
        when(bindingResult.hasErrors()).thenReturn(true);

        // When
        org.junit.jupiter.api.Assertions.assertThrows(NotCreatedException.class,
                () -> newsController.createNews(createChangeNewsRequest, bindingResult));

        // Then
        verify(newsService, never()).createNews(any(CreateChangeNewsRequest.class));
    }

    @Test
    @DisplayName("GET /news: Returns HTTP response with status 200 OK")
    void findAll_ReturnsValidResponseEntity() {

        // given

        // when
        var responseEntity = newsController.findAll(1, 3);

        // then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        verify(newsService).findAll(1, 3);
    }

    //<-----------------------------------------------CHANGE NEWS  -------------------------------------------------->//

    @Test
    @DisplayName("PUT /news/{id}: Returns HTTP response with status 200 OK")
    void changeNews_CreateChangeNewsRequestIsValid_ReturnsValidResponseEntity() {

        // given

        // when
        var responseEntity = this.newsController.changeNews(ID, createChangeNewsRequest, bindingResult);

        // then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        verify(newsService).changeNews(createChangeNewsRequest, ID);
    }

    @Test
    @DisplayName("PUT /news/{id}: BindingResult has error")
    public void changeNews_BindingResultHasError_InvokeErrorResponse() {

        // Given
        when(bindingResult.hasErrors()).thenReturn(true);

        // When
        org.junit.jupiter.api.Assertions.assertThrows(NotCreatedException.class,
                () -> newsController.changeNews(ID, createChangeNewsRequest, bindingResult));

        // Then
        verify(newsService, never()).changeNews(createChangeNewsRequest, ID);
    }

    @Test
    @DisplayName("DELETE /news{id}: Returns HTTP status 204 NO_CONTENT")
    void deleteNews_ReturnsHttpStatus_NO_CONTENT() {

        // given

        // when
        var responseEntity = newsController.deleteNews(ID);

        // then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());

        verify(newsService, times(1)).deleteNews(ID);
    }

    @Test
    @DisplayName("GET /news/{id}: Returns HTTP response with status 200 OK")
    void findById_ReturnsValidResponseEntity() {

        // given

        // when
        var responseEntity = newsController.findById(ID);

        // then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        verify(newsService).findById(ID);
    }
}