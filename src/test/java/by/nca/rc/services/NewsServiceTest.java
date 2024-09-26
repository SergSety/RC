package by.nca.rc.services;

import by.nca.rc.exceptions.NotFoundException;
import by.nca.rc.models.News;
import by.nca.rc.repositories.NewsRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NewsServiceTest {

    private final static Long ID = 1L;

    @Mock
    private NewsRepository newsRepository;

    @InjectMocks
    private NewsService newsService;

    private News news;

    @BeforeEach
    public void setup() {

        news = new News(1L, "Test title", "Test text", new Date(), new Date(),
                1L, 1L, new ArrayList<>());
    }

    @Test
    void deleteNews_shouldCallRepository() {

        // given

        // when
        newsService.deleteNews(ID);

        // then
        verify(newsRepository, times(1)).deleteById(ID);
    }

    @Test
    @DisplayName("Should call Repository")
    void findById_shouldFindNewsById() {

        // given
        when(newsRepository.findById(ID)).thenReturn(Optional.of(news));

        // when
        newsService.findById(ID);

        // then
        verify(newsRepository).findById(ID);
    }

    @Test
    @DisplayName("Should throw NotFoundException")
    void findById_shouldThrowNotFoundException() {

        // given
        when(newsRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> newsService.findById(anyLong()));

        // then
        assertEquals("News not found", exception.getMessage());

        verify(newsRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(newsRepository);
    }
}
